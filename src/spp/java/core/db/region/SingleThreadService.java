package spp.java.core.db.region;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

import spp.java.core.ISingleThreadService;
import spp.java.core.ITask;

public class SingleThreadService implements ISingleThreadService, Runnable{
	private ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();
	private BlockingQueue q;
	@Override
	public void execTask(ITask task) {
		
	}
	@Override
	public void run() {
		
	}

}
