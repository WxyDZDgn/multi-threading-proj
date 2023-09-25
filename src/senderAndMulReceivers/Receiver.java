package senderAndMulReceivers;

import java.util.concurrent.Semaphore;

class Receiver implements Runnable {
	private final Semaphore empty;
	private final Semaphore mutex;
	private final Semaphore fullI;
	private final Semap i;
	private final int n;
	private final int idx;

	Receiver(Semaphore empty, Semaphore mutex, Semaphore fullI, Semap i, int n, int idx) {
		this.empty = empty;
		this.mutex = mutex;
		this.fullI = fullI;
		this.i = i;
		this.n = n;
		this.idx = idx;
	}

	@Override
	public void run() {
		for(int i = 0; i < 10; ++i) {
			try {
				Thread.sleep(time());
				fullI.acquire();
				mutex.acquire();
			} catch(InterruptedException e) {
				throw new RuntimeException(e);
			}
			System.out.printf("%d: received.\n", idx);
			this.i.val += 1;
			if(this.i.val == this.n) empty.release();
			mutex.release();
		}
	}

	private long time() {
		return (long) (Math.random() * 2000) + 1000;
	}
}
