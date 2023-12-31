package supermarket;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Counter implements Runnable {

	private final int id;
	private final Market market;
	private final Queue<Customer> queue;
	private int itemId;
	private int itemAmount;
	private final Semaphore waiting4Check;
	private final Semaphore arrival;
	private final Semaphore busy;
	private double total;

	public Counter(int id, Market market, Semaphore arrival) {
		this.id = id;
		this.market = market;
		this.arrival = arrival;
		queue = new LinkedList<>();
		waiting4Check = new Semaphore(0);
		total = 0;
		busy = new Semaphore(0);
	}
	@Override
	public void run() {
		arrival.release();
		System.out.printf("Counter %d - ARRIVED!\n", id);
		while(true) {
			busy.release();
			System.out.printf("Counter %d - available\n", id);
			try {
				this.waiting4Check.acquire();
			} catch(InterruptedException e) {
				throw new RuntimeException(e);
			}
			if(queue.isEmpty()) break;
			Customer current = queue.poll();
			randomSleep();
			System.out.printf("Counter %d - checking for customer %d\n", id, current.getId());
			current.wakeup(this);
			try {
				this.waiting4Check.acquire();
			} catch(InterruptedException e) {
				throw new RuntimeException(e);
			}
			randomSleep();
			System.out.printf("Counter %d - customer is ready\n", id);
			double currentBargain = 0;
			while(itemId >= 0 && itemAmount >= 0) {
				double itemPrice = market.getPrice(itemId);
				double item = itemPrice * itemAmount;
				current.addTotal(item);
				currentBargain += item;
				System.out.printf("Counter %d - item: %d - price: %.2f - amount: %d\n", id, itemId, itemPrice, itemAmount);
				System.out.printf("Counter %d - handled: %.2f\n", id, item);
				current.wakeup(this);
				try {
					this.waiting4Check.acquire();
				} catch(InterruptedException e) {
					throw new RuntimeException(e);
				}
				randomSleep();
			}
			total += currentBargain;
			System.out.printf("Counter %d - current bargain: %.2f\n", id, currentBargain);
			randomSleep();
		}
		System.out.printf("Counter %d - total bargain: %.2f\n", id, total);
		System.out.printf("Counter %d - LEFT!\n", id);
	}
	public void check(int id, int amount) {
		itemId = id;
		itemAmount = amount;
		this.waiting4Check.release();
	}
	public int currentQueueLength() {
		return queue.size();
	}
	public void wakeup(Customer another) {
		queue.add(another);
//		System.out.println(busy.getQueueLength());
		try {
			busy.acquire();
		} catch(InterruptedException e) {
			throw new RuntimeException(e);
		}
		this.waiting4Check.release();
	}
	public void wakeup() {
		this.waiting4Check.release();
	}
	private void randomSleep() {
		try {
			Thread.sleep((long)(Math.random() * 1000));
		} catch(InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
