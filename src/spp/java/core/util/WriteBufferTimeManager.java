package spp.java.core.util;

import java.util.ArrayList;

public class WriteBufferTimeManager implements Runnable{
	private static WriteBufferTimeManager instance;
	public static WriteBufferTimeManager getInstance() {
		if(instance  == null) {
			synchronized(WriteBufferTimeManager.class) {
				if(instance == null)
					instance = new WriteBufferTimeManager(10);
			}
		}
		return instance;
	}
	
	private ArrayList<ITimeoutable> buffs = new ArrayList<>();
	private Thread thread = null;
	private int minPeriod = 1;

	private WriteBufferTimeManager(int minPeriod) {
		this.minPeriod = minPeriod;
		thread =new Thread(this);
		thread.start();
	}
	public void addBuffer(ITimeoutable buff) {
		buffs.add(buff);
	}
	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(minPeriod);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			int size = buffs.size();
			for(int i = 0;i<size;i++) {
				ITimeoutable t = buffs.get(i);
				if(t.getStart() + t.getTimeout() >= System.currentTimeMillis()) {
					t.timeout();
				}
			}
		}
	}
}
