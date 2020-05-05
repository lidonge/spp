package spp.java.core.unused;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NBFTest {
	private String fileName = "D:\\testnbf.txt";
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

	private void startRecord( NonblockingFixSizeBuffer nbf, int j, int threads, int count,
			ExecutorService es) {
		es.submit(new Runnable() {

			@Override
			public void run() {
				long time = System.currentTimeMillis();
				try {
					for (int i = j; i < count; i += threads) {
						int cur = i % contents.length;
						int len = contents[cur].length();
						// System.out.println("Start " +i+": len "+len );
						IPartBufferFile buf = null;
						while ((buf = nbf.startRecord(len)) == null) {
							// try {
							// Thread.currentThread().sleep(10);
							// } catch (InterruptedException e) {
							// e.printStackTrace();
							// }
						}
						// System.out.println(i+":"+len + "," + buf.getCurrentFilePostion() + "," +
						// buf.getCurrentBufferPosition());
						// indexs[i] = new Index(cur,len,buf.getCurrentFilePostion() +
						// buf.getCurrentBufferPosition());
						buf.write(contents[cur].getBytes());
						nbf.endRecord(buf);
						// startRecord(buf, nbf, cur, len, es);
					}
					nbf.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}

				long cost = System.currentTimeMillis() - time;
				System.out.println("=====" + j + " thread Write cost: " + cost);

			}

		});
	}

	void write(int count, int bufSize, int threads) throws IOException {
		FileOutputStream fout = new FileOutputStream(fileName);
		ExecutorService es = Executors.newFixedThreadPool(threads + 1);
		NonblockingFixSizeBuffer nbf = new NonblockingFixSizeBuffer(bufSize, fout, 0, 4, 10);
		indexs = new Index[count];
		fout = new FileOutputStream(fileName);
		for (int j = 0; j < threads; j++) {
			this.startRecord( nbf, j, threads, count, es);
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
				if (!s.equals(contents[idx.cont])) {
					System.out.println(i + " Error in " + idx.cont + ", should be \"" + contents[idx.cont] + "\"");
				}
			}
			System.out.println("Check done!");
		} finally {
			if (raf != null)
				raf.close();
		}
	}

	public static void main(String[] args) {
		NBFTest test = new NBFTest();
		try {
			int count = 1000000;
			test.syncWrite(count);
			test.write(count, 4 * 1024 * 1024, 4);
			test.cheak();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
