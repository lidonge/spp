package spp.java.core.util;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;


public class ReusebleCache<T> {
	private ConcurrentLinkedQueue<T> reused = null;
	private AtomicInteger acount = new AtomicInteger(0);
	public synchronized void initReusedBuffer(int reusedCount, IReusebleFactory<T> factory) {
		if(reused == null) {
			reused = new ConcurrentLinkedQueue<T>();
			for(int i = 0;i<reusedCount;i++) {
				reused.add(factory.create());
				acount.getAndIncrement();
			}
		}
	}
	
	public T getBuffer() {
		T ret = null;
		while((ret = reused.poll()) == null) {
//			System.out.println("=====================*****" + acount.get());
		};
		acount.decrementAndGet();
		return ret;
	}

	public void reuse(T buff) {
		reused.add(buff);
		acount.getAndIncrement();
	}
}
