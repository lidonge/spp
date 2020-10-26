package spp.java.core.db.file;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicBoolean;

import spp.java.core.util.IReusebleFactory;
import spp.java.core.util.ITimeoutable;
import spp.java.core.util.ReusebleCache;
import spp.java.core.util.WriteBufferTimeManager;

public class SingleThreadDataBlockWriter {
	public static final int DEFAULT_BLOCK_COUNT = 1024;
	public static final int BLOCK_INDEX_VERSION = 1;
	public static final int BLOCK_FILE_VERSION = 1;
	public static final int DEFAULT_TIMEOUT = 50;
	
	private static final int LOCK_TIMEOUT_COUNT = 10;
	private static class BAOutput extends ByteArrayOutputStream{
		public byte[] getByteArray() {
			return this.buf;
		}
	}
	
	private int blockCount = DEFAULT_BLOCK_COUNT;
	private AsynchronousFileChannel blockFile = null;
	private RandomAccessFile indexFile = null;
	private BAOutput indexBuff = new BAOutput();
	private DataOutputStream indexBuffStream = new DataOutputStream(indexBuff);
	
	private long curPos = 0;
	private ReusebleCache<ByteBuffer> cache = null;
	private ByteBuffer blkBuff = null;
	private int timeout = DEFAULT_TIMEOUT;
	private long startTime = -1;
	private AtomicBoolean onFlush = new AtomicBoolean(false);
	private IBlockFileHeader metadata;

	public SingleThreadDataBlockWriter(IBlockFileHeader metadata,
			int buffCount, int buffSize, int timeout) throws IOException {
		this(metadata.getBlockFileName(),metadata.getIndexFileName(),
				metadata.getBlockCount(), buffCount,buffSize,timeout);
		this.metadata = metadata;
	}
	private SingleThreadDataBlockWriter(String blockFile, String indexFile, int blockCount,
			int buffCount, int buffSize, int timeout) throws IOException {
		initFile(blockFile, indexFile, blockCount);
		this.curPos = this.blockFile.size();
		if(timeout != -1)
			this.timeout = timeout ;
		
		this.cache = new ReusebleCache<ByteBuffer>();
		this.cache.initReusedBuffer(buffCount,  new IReusebleFactory<ByteBuffer>() {

				@Override
				public ByteBuffer create() {
					return ByteBuffer.allocate(buffSize);
				}
			});
		blkBuff = cache.getBuffer();
		initTimer();
	}
	
	private void initTimer() {
		WriteBufferTimeManager.getInstance().addBuffer(new ITimeoutable() {
			@Override
			public long getStart() {
				return startTime;
			}

			@Override
			public int getTimeout() {
				return timeout;
			}

			@Override
			public void timeout() {
				try {
					flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void initFile(String blk, String idx, int bc) throws IOException {
		this.createOrLoadFile(blk, idx, bc);

		this.blockFile = AsynchronousFileChannel.open(Paths.get(blk), StandardOpenOption.WRITE);
		this.indexFile = new RandomAccessFile(idx, "rw");
//		lockFile(idx);
		this.indexFile.seek(this.indexFile.length());
	}
	
	private void lockFile(String idx) throws IOException {
		FileLock fl = null;  //or fc.lock();
		FileChannel fc = this.indexFile.getChannel();
		int lockCount = 0;
		while((fl = fc.tryLock(0L, Long.MAX_VALUE, true)) == null) {
			try {
				Thread.currentThread().sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(lockCount > LOCK_TIMEOUT_COUNT)
				throw new IOException("Can't lock index file : " + idx);
			lockCount++;
		}
	}
	
	private void createOrLoadFile(String blk, String idx, int bc) throws IOException {
		File fIdx = new File(idx);
		File fBlk = new File(blk);
		boolean newFile = false;
		if(!fIdx.exists()) {
			newFile = fIdx.createNewFile();
			newFile &= fBlk.createNewFile();
			if(!newFile) {
				throw new IOException("Can not create new block files :" + blk + " and " + idx);
			}
		}
		RandomAccessFile rIdx = null;
		RandomAccessFile rBlk = null;
		try {
			rIdx = new RandomAccessFile(fIdx, "rw");
			rBlk = new RandomAccessFile(fBlk, "rw");
			if(newFile) {
				rIdx.writeShort(BLOCK_INDEX_VERSION);
				if( bc != -1)
					this.blockCount = bc;
				rIdx.writeShort(this.blockCount);
				rBlk.writeShort(BLOCK_FILE_VERSION);
			}else {
				int ver_idx = rIdx.readUnsignedShort();
				this.blockCount = rIdx.readUnsignedShort();
				int ver_blk = rBlk.readUnsignedShort();
			}
		}finally {
			if (rIdx != null) {
				try {
					rIdx.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(rBlk != null) {
				try {
					rBlk.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void writeData(IDataWriter data) throws IOException {
		int size = data.size();
		if(data.size() > blkBuff.remaining()) {
			flush();
		}
		if(startTime == -1)
			startTime = System.currentTimeMillis();
		
		synchronized(onFlush) {
			data.write(blkBuff);
			this.processIndex(data, size);
		}
	}
	
	public void flush() throws IOException {
		if(!onFlush.compareAndSet(false, true)) {
			return;
		}
		synchronized(onFlush) {
			startTime = -1;
			if(blkBuff.position() != 0) {
				blkBuff.flip();
				blockFile.write(blkBuff, blockFile.size(), blkBuff, new CompletionHandler<Integer, ByteBuffer>() {
				    @Override
				    public void completed(Integer result, ByteBuffer attachment) {
				        attachment.clear();
				        cache.reuse(attachment);
				    }
		
				    @Override
				    public void failed(Throwable exc, ByteBuffer attachment) {
				        System.out.println("Failed to write data block file :" + metadata.getBlockFileName());
				    }
				});
				blkBuff = cache.getBuffer();
				this.indexFile.write(indexBuff.getByteArray(),0,indexBuff.size());
				this.indexBuff.reset();	
			}
		}
		onFlush.set(false);
	}
	
	private void processIndex(IDataWriter data, int size) throws IOException {
		indexBuffStream.writeLong(curPos);
		curPos += size;
	}
	
	public void close() throws IOException {
		if(this.blockFile != null)
			this.blockFile.close();
		if(this.indexFile != null)
			this.indexFile.close();
	}
}
