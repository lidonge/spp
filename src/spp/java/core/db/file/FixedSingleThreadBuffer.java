package spp.java.core.db.file;

import java.io.OutputStream;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class FixedSingleThreadBuffer extends OutputStream{
	private byte[] data;
	private int cur = 0;
	private static ConcurrentLinkedQueue<FixedSingleThreadBuffer> reused = null;
	private static AtomicInteger acount = new AtomicInteger(0);
	public synchronized static void initReusedBuffer(int bufSize, int reusedCount) {
		if(reused == null) {
			reused = new ConcurrentLinkedQueue<FixedSingleThreadBuffer>();
			for(int i = 0;i<reusedCount;i++) {
				reused.add(new FixedSingleThreadBuffer(bufSize));
				acount.getAndIncrement();
			}
		}
	}
	
	public static FixedSingleThreadBuffer getBuffer() {
		FixedSingleThreadBuffer ret = null;
		while((ret = reused.poll()) == null) {
//			System.out.println("=====================*****" + acount.get());
		};
		acount.decrementAndGet();
		return ret;
	}

	public static void reuse(FixedSingleThreadBuffer buff) {
		buff.reset();
		reused.add(buff);
		acount.getAndIncrement();
	}

	private FixedSingleThreadBuffer(int bufSize) {
		this.data = new byte[bufSize];
	}

	void init(int off, int len) {
		cur = 0;
	}
	private void testFull(int newLen) {
		if (!hasSpace(newLen))
			throw new Error("Error Record size, require " + (cur + newLen) + " but " + this.data.length);
	}

	public void write(int b) {
		testFull(1);
		data[cur++] = (byte) b;
	}

	public void write(byte b[], int offset, int length) {
		testFull(length);
		System.arraycopy(b, offset, data, cur, length);
		cur += length;
	}
	
	public boolean hasSpace(int newLen) {
		return cur + newLen <= this.data.length;
	}

	public byte[] getData() {
		return data;
	}
	
	public int getLength() {
		return cur;
	}
	
	public void reset() {
		this.cur = 0;
	}
}
