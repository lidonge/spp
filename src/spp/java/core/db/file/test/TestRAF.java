package spp.java.core.db.file.test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.atomic.AtomicInteger;

public class TestRAF {
	public static final int BLOCK = 4*1024*1024;
	RandomAccessFile raf;
	long time = 0;
	AtomicInteger records = new AtomicInteger(0);
	
	TestRAF(String file) throws IOException{
		raf = new RandomAccessFile(file, "rw");
		raf.setLength(BLOCK);
	}
	
	public void start() {
		this.time = System.currentTimeMillis();
	}
	public synchronized void write() throws IOException {
		raf.seek((System.currentTimeMillis() *100)% BLOCK);
		raf.write(0x3f);
	}
	private static class Task implements Runnable{
		TestRAF traf;
		int count;
		Task(TestRAF traf, int count){
			this.traf = traf;
			this.count = count;
		}
		@Override
		public void run() {
			try {
				for(int i = 0;i<count;i++) {
					this.traf.write();
					this.traf.records.incrementAndGet();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			long ltime;
			ltime = System.currentTimeMillis();
			System.out.println(this.traf.records+"==========" + (ltime - traf.time));	
		}
		
	}
	public static void main(String[] args) {
		try {
			TestRAF tr = new TestRAF("d:\\testraf.txt");
			Thread t1 = new Thread(new Task(tr,100000));
			Thread t2 = new Thread(new Task(tr,100000));
			tr.start();
			t1.start();
			t2.start();

		} catch (IOException e) {
			e.printStackTrace();
		}finally {
		
		}

	}

}
