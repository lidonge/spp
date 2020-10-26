package spp.java.core.db.file.test;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import spp.java.core.util.IReusebleFactory;
import spp.java.core.util.ReusebleCache;

public class NIOTest extends BaseWriterTester{
	private String fileName = "D:\\temp\\nio.txt";
	void nioWrite(int count) throws IOException {
		DemoStringGenerator sg = new DemoStringGenerator("The quick fox jump over lazy dog");
		
		Path path = Paths.get(fileName);
		path.toFile().delete();
		path.toFile().createNewFile();
		AsynchronousFileChannel fc = null;
		try {
			fc = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE);
			ReusebleCache<ByteBuffer> cache = new ReusebleCache<ByteBuffer>();
			cache.initReusedBuffer(10, new IReusebleFactory<ByteBuffer>() {

				@Override
				public ByteBuffer create() {
					return ByteBuffer.allocate(1024*1024);
				}
			});
			ByteBuffer bb = cache.getBuffer();

			long time = System.currentTimeMillis();
			for (int i = 0; i < count; i++) {
				byte[] bytes = sg.next().getBytes();
				if(bytes.length > bb.remaining()) {
					bb = flush(fc,bb, cache);
				}
				bb.put(bytes);
			}
			flush(fc,bb,cache);
			long cost = System.currentTimeMillis() - time;
			System.out.println("=====nioWrite cost: " + cost);
		} finally {
			if (fc != null)
				fc.close();
		}
	}
	
	private ByteBuffer flush(AsynchronousFileChannel fc,ByteBuffer bb, ReusebleCache<ByteBuffer> cache) throws IOException {
		bb.flip();
		fc.write(bb, fc.size(), bb, new CompletionHandler<Integer, ByteBuffer>() {
		    @Override
		    public void completed(Integer result, ByteBuffer attachment) {
//		        System.out.println("result: " + result);
		        attachment.clear();
		        cache.reuse(attachment);
		    }

		    @Override
		    public void failed(Throwable exc, ByteBuffer attachment) {
		        System.out.println("failed");
		    }
		});
		return  cache.getBuffer();		
	}
	
	public static void main(String[] args) {
		NIOTest test = new NIOTest();
		try {
			int count = 1000000;
			test.syncWrite(count);
			test.nioWrite(count);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
