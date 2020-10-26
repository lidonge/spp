package spp.java.core.unused;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BFTest {
	private String fileName = "D:\\testbf.txt";
	private Index[] indexs;

	public static String[] contents = new String[] { "package spp.java.core.db.file.test;\n", "\n",
			"import java.io.FileNotFoundException;\n", "import java.io.FileOutputStream;\n", };

	private static class Index {
		int cont;
		int len;
		long pos;

		Index(int cont, int len, long pos) {
			this.cont = cont;
			this.len = len;
			this.pos = pos;
		}
	}

	void syncWrite(int count) throws IOException {

		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(fileName);
			BufferedOutputStream bout = new BufferedOutputStream(fout, 4 * 1024 * 1024);
			long time = System.currentTimeMillis();
			for (int i = 0; i < count; i++) {
				int cur = i % contents.length;
				bout.write(contents[cur].getBytes());
			}
			bout.flush();
			long cost = System.currentTimeMillis() - time;
			System.out.println("=====syncWrite cost: " + cost);
		} finally {
			if (fout != null)
				fout.close();
		}

	}

	private void addToBf(FixedSingleThreadBuffer buf, TwoPhaseBlockWritingBuffer bf, int prevStart, int threads, int count) {
		long pos = bf.addBuffer(buf);
		int prev = 0;
		for(int k = prevStart;k<count;k+=threads) {
			indexs[k].pos = pos + prev;
			prev = indexs[k].len;
		}
	}
	private void startRecord( TwoPhaseBlockWritingBuffer bf, int j, int threads, int count,
			ExecutorService es) {
		es.submit(new Runnable() {
//		new Runnable() {
			@Override
			public void run() {
				long time = System.currentTimeMillis();
				try {
					FixedSingleThreadBuffer buf = FixedSingleThreadBuffer.getBuffer();
					int prevStart = j;
//					System.out.println(count+"******prevStart:"+prevStart);
					for (int i = j; i < count; i += threads) {
						int cur = i % contents.length;
						int len = contents[cur].length();
						if(!buf.hasSpace(len)) {
							FixedSingleThreadBuffer tmp = buf;
							buf = FixedSingleThreadBuffer.getBuffer();
							
							addToBf( tmp, bf, prevStart, threads, i);

							prevStart = i;
						}
						indexs[i] = new Index(cur,len,0);
						buf.write(contents[cur].getBytes());
					}
					addToBf( buf, bf, prevStart, threads, count);
				} catch (IOException e) {
					e.printStackTrace();
				}

				long cost = System.currentTimeMillis() - time;
				System.out.println("=====" + j + " thread Write cost: " + cost);

			}
		});
//		}.run();
	}

	void write(int count, int bufSize,int bufCount, int threads) throws IOException {
		FileOutputStream fout = new FileOutputStream(fileName);
		ExecutorService es = Executors.newFixedThreadPool(threads + 1);
		TwoPhaseBlockWritingBuffer bf = new TwoPhaseBlockWritingBuffer( fout, 0, es);
		FixedSingleThreadBuffer.initReusedBuffer(bufSize, bufCount);
		indexs = new Index[count];
		for (int j = 0; j < threads; j++) {
			this.startRecord( bf, j, threads, count, es);
		}

	}

	void cheak() throws IOException {
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(fileName, "r");
			System.out.println("Begin check:");
			for (int i = 0; i < indexs.length; i++) {
				Index idx = indexs[i];
				byte[] bytes = new byte[idx.len];
				raf.seek(idx.pos);
				raf.read(bytes);
				String s = new String(bytes);
				System.out.println(idx.pos + "******"+s  +"====" + contents[idx.cont]);
				if (!s.equals(contents[idx.cont])) {
					System.out.println(i + " Error in " + idx.cont + " at " + idx.pos+", should be \"" + contents[idx.cont] + "\"");
				}
			}
			System.out.println("Check done!");
		} finally {
			if (raf != null)
				raf.close();
		}
	}

	public static void main(String[] args) {
		BFTest test = new BFTest();
		try {
			int count = 1000000;
			test.syncWrite(count);
			test.write(count, 1 * 1024*1024,16, 16);
			Thread.currentThread().sleep(1000);
			test.cheak();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
