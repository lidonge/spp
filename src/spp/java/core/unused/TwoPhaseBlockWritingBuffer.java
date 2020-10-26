package spp.java.core.unused;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

public class TwoPhaseBlockWritingBuffer implements Runnable{
	private Queue<FixedSingleThreadBuffer> queue;
	private OutputStream target;

	private long targetPos;
	private int curPos = 0;

	public TwoPhaseBlockWritingBuffer( OutputStream target, long targetPos,
			ExecutorService executorService) {
		
		this.target = target;
		this.targetPos = targetPos;
		this.queue = new LinkedList<FixedSingleThreadBuffer>();
		executorService.submit(this);
	}
	
	public long addBuffer(FixedSingleThreadBuffer data) {
		long ret = 0;
		synchronized(queue) {
			queue.add(data);
			ret = targetPos + curPos;
			curPos += data.getLength();
			queue.notify();
		}
		return ret;
	}

	@Override
	public void run() {
		while(true) {
			FixedSingleThreadBuffer data = null;
			synchronized(queue) {
				data = queue.poll();
				if(data == null) {
					try {
						queue.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					data = queue.poll();
				}
			}
			do {
				try {
					target.write(data.getData(),0,data.getLength());
					
				} catch (IOException e) {
					e.printStackTrace();
				}finally {
					FixedSingleThreadBuffer.reuse(data);
				}
				synchronized(queue) {
					data = queue.poll();
				}
			}while(data != null);
		}
	}
}
