package spp.java.core.db.file.test;

import java.io.DataInput;
import java.io.IOException;
import java.nio.ByteBuffer;

import spp.java.core.db.file.SingleThreadDataBlockWriter;
import spp.java.core.db.file.IDataReader;
import spp.java.core.db.file.IDataWriter;
import spp.java.core.db.file.NormalDataBlockReader;

public class DataBlockWriterTester extends BaseWriterTester{
	void dbWrite(int count) throws IOException {
		DemoStringGenerator sg = new DemoStringGenerator("The quick fox jump over lazy dog");
		SingleThreadDataBlockWriter dbWriter = new SingleThreadDataBlockWriter("d:\\temp\\db.txt", "d:\\temp\\db.idx", 1024,
				10, 1024*1024, 50);
		long time = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
			byte[] bytes = sg.next().getBytes();
			final int index = i;
			IDataWriter data = new IDataWriter() {

				@Override
				public int size() {
					return bytes.length;
				}

				@Override
				public void write(ByteBuffer buff) {
					buff.put(bytes);
				}

				@Override
				public int getID() {
					return index;
				}

				@Override
				public int getMetadataID() {
					return 0xff;
				}
				
			};
			dbWriter.writeData(data);
		}
		long cost = System.currentTimeMillis() - time;

		System.out.println("=====nioWrite cost: " + cost);
		
	}
	public static void main(String[] args) {
		DataBlockWriterTester test = new DataBlockWriterTester();
		try {
			int count = 1000000;
//			test.syncWrite(count);
			test.dbWrite(count);
			IDataReader<String> dataReader = new IDataReader<String>() {

				@Override
				public String read(DataInput in, int metaID, int len) throws IOException {
					byte[] bytes = new byte[len];
					in.readFully(bytes);
					return new String(bytes);
				}
				
			};
			NormalDataBlockReader<String> reader = new NormalDataBlockReader<String>("d:\\temp\\db.idx", "d:\\temp\\db.txt", dataReader);
			System.out.println(reader.readData(1024));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
