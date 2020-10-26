package spp.java.core.db.file.test;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class BaseWriterTester {
	private static String fileName = "D:\\temp\\base.txt";
	public final void syncWrite(int count) throws IOException {
		DemoStringGenerator sg = new DemoStringGenerator("The quick fox jump over lazy dog");
		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(fileName);
			BufferedOutputStream bout = new BufferedOutputStream(fout, 4 * 1024 * 1024);
			long time = System.currentTimeMillis();
			for (int i = 0; i < count; i++) {
				String s = sg.next();
				bout.write(s.getBytes());
			}
			bout.flush();
			long cost = System.currentTimeMillis() - time;
			System.out.println("=====syncWrite cost: " + cost);
		} finally {
			if (fout != null)
				fout.close();
		}

	}
}
