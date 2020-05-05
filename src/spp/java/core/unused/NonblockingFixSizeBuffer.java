package spp.java.core.unused;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class NonblockingFixSizeBuffer {

	/**
	 * The buffer where data is stored.
	 */
	private byte[] buf;

	private AtomicInteger prepareCount = new AtomicInteger(0);

	private AtomicInteger endCount = new AtomicInteger(0);

	private OutputStream target;

	private long targetPos = 0;
	
	private int minTimeWait = 10;
//	private ConcurrentLinkedQueue<BufferDataOutput> queue = new ConcurrentLinkedQueue<BufferDataOutput>();

	private static class ByteBuff extends OutputStream {
		private byte[] bytes;
		private int off;
		private int bufSize;
		private int count = 0;

		ByteBuff(byte[] bytes) {
			this.bytes = bytes;
		}

		void init(int off, int len) {
			this.off = off;
			this.bufSize = len;
			count = 0;
		}
		private void testFull(int size) {
			if (count + size > bufSize)
				throw new Error("Error Record size, require " + (count + size) + " but " + bufSize);
		}

		public void write(int b) {
			testFull(1);
			bytes[off + count] = (byte) b;
			count++;
		}

		public void write(byte b[], int offset, int length) {
			testFull(length);
			System.arraycopy(b, offset, bytes, off + count, length);
			count += length;
		}
	}
	
	private static class BufferDataOutput extends DataOutputStream implements IPartBufferFile{
		private long filePos;
		private int buffPos;
		public BufferDataOutput(OutputStream out) {
			super(out);
		}

		void init(long filePos, int buffPos) {
			this.filePos = filePos;
			this.buffPos = buffPos;
		}
		@Override
		public long getCurrentFilePostion() {
			return this.filePos;
		}

		@Override
		public int getCurrentBufferPosition() {
			return this.buffPos;
		}
		
		public ByteBuff getStream() {
			return (ByteBuff)out;
		}

		@Override
		public int getLength() {
			return getStream().bufSize;
		}
	}

	public NonblockingFixSizeBuffer(int size, OutputStream target, long targetPos, int threads, int minTimeWait) {
		if (size < 0) {
			throw new IllegalArgumentException("Negative initial size: " + size);
		}
		this.target = target;
		this.targetPos = targetPos;
		this.minTimeWait = minTimeWait;
		buf = new byte[(size / 64 + 1) * 64];
//		for(int i = 0;i<threads;i++)
//			queue.add(new BufferDataOutput(new ByteBuff(buf)));
	}

	public void flush() throws IOException {
		startRecord(buf.length);
	}
	public IPartBufferFile startRecord(int len) throws IOException {
		if (prepareCount.get() >= buf.length) {
			return null;
		}
		int cur = prepareCount.getAndAdd(len);
		if (cur + len >buf.length) {
			if (cur < buf.length) {
				flush(cur);
			}
			return null;
		}
		BufferDataOutput ret = null;
//		while((ret = queue.poll())== null);
		ret = new BufferDataOutput(new ByteBuff(buf));
		
		ByteBuff bb = ret.getStream();
		bb.init(cur, len);
		ret.init( this.targetPos, cur);
		return ret;
//		return new BufferDataOutput(new ByteBuff(buf, cur, len), this.targetPos, cur);
	}

	public void endRecord(IPartBufferFile part) {
		endCount.addAndGet(part.getLength());//if cur == curToflush then onFlushing can be ignore, else can not enter notify
//		queue.add((BufferDataOutput) part);
	}

	private void flush(int cur) throws IOException {
		while(!this.endCount.compareAndSet(cur, 0)) {
//			try {
//				Thread.currentThread().sleep(this.minTimeWait);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
		this.targetPos += cur;
		target.write(buf, 0, cur);
		prepareCount.set(0);
	}
}
