package spp.java.core.db.file;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;

import spp.java.core.IRandomAccessable;

/**
 * Only single thread.
 * @author Administrator
 *
 */
public class IndexFileTwoPhaseWriter {
	private static int INDEX_SIZE = 12;
//	private static int BLOCK_SIZE = 4*1024*1024;
	private IIndex[] prepared = new IIndex[1024];
	private int size = 0;
	private IRandomAccessable raf;
	private int minIndexID = Integer.MAX_VALUE;
	private static class BAOutput extends ByteArrayOutputStream{
		private static final int MAX_ARRAY_LENGTH = Integer.MAX_VALUE - 8;
		private int size = -1;
		public byte[] getByteArray() {
			return this.buf;
		}
	    public static int newLength(int oldLength, int minGrowth, int prefGrowth) {
	        // assert oldLength >= 0
	        // assert minGrowth > 0

	        int newLength = Math.max(minGrowth, prefGrowth) + oldLength;
	        if (newLength - MAX_ARRAY_LENGTH <= 0) {
	            return newLength;
	        }
	        return hugeLength(oldLength, minGrowth);
	    }

	    private static int hugeLength(int oldLength, int minGrowth) {
	        int minLength = oldLength + minGrowth;
	        if (minLength < 0) { // overflow
	            throw new OutOfMemoryError("Required array length too large");
	        }
	        if (minLength <= MAX_ARRAY_LENGTH) {
	            return MAX_ARRAY_LENGTH;
	        }
	        return Integer.MAX_VALUE;
	    }
	    void ensureCapacity(int minCapacity) {
	        // overflow-conscious code
	        int oldCapacity = buf.length;
	        int minGrowth = minCapacity - oldCapacity;
	        if (minGrowth > 0) {
	            buf = Arrays.copyOf(buf, newLength(oldCapacity,
	                    minGrowth, oldCapacity /* preferred growth */));
	        }
	    }		
		public void seek(int pos) {
			if(pos >= this.buf.length) {
				this.ensureCapacity(pos - this.buf.length);
				size = pos;
			}
			if(size < count) {
				size = count;
			}			
			count = pos;
		}
		
		public void reset() {
			this.count = 0;
			this.size = 0;
		}
		
		public int getLength() {
			return count > size ? count : size;
		}
	}
	
	private BAOutput buff = new BAOutput();
	private DataOutputStream dout = new DataOutputStream(buff);
	public IndexFileTwoPhaseWriter(IRandomAccessable raf) {
		this.raf = raf;
	}

	public int prepareAdd(IIndex index) {
		int ret = size;
		if(size == prepared.length) {
			IIndex[] tmp = new IIndex[prepared.length + 1024];
			System.arraycopy(prepared, 0, tmp, 0, prepared.length);
			prepared = tmp;
		}
		prepared[size]= index;
		minIndexID = minIndexID < index.getIndexID() ? minIndexID : index.getIndexID();
		return ret;
	}
	
	public void dumpToFile(long pos,ExecutorService es) {
		IIndex[] data = new IIndex[size];
		System.arraycopy(prepared, 0, data, 0, size);
		size = 0;
		es.submit(new Runnable() {

			@Override
			public void run() {
				buff.reset();
				for(int i = 0;i<data.length;i++) {
					IIndex idx = data[i];
					long thePos = pos + idx.getPosition();
					int diff = idx.getIndexID() - minIndexID;
					try {
						buff.seek(diff * INDEX_SIZE);
						dout.writeShort(idx.getMetadataID());
						dout.writeLong(thePos);
						dout.writeShort(idx.getLength());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			}
			
		});
	}
}
