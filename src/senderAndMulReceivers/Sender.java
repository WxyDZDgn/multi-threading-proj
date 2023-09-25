package senderAndMulReceivers;

import java.util.concurrent.Semaphore;

class Sender implements Runnable {
	private final Semaphore empty;
	private final Semaphore mutex;
	private final Semaphore[] full;
	private final Semap i;

	Sender(Semaphore empty, Semaphore mutex, Semaphore[] full, Semap i) {
		this.empty = empty;
		this.mutex = mutex;
		this.full = full;
		this.i = i;
	}

	@Override
	public void run() {
		for(int i = 0; i < 10; ++i) {
			try {
				Thread.sleep(time());
				empty.acquire();
				mutex.acquire();
			} catch(InterruptedException e) {
				throw new RuntimeException(e);
			}
			System.out.println("Sender sent.");
			this.i.val = 0;
			mutex.release();
			for(Semaphore each : full) each.release();
		}
	}

	private long time() {
		return (long) (Math.random() * 1000) + 500;
	}
}
