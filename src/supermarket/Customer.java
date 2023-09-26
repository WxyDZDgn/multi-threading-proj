package supermarket;

import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class Customer implements Runnable {
	private final int id;
	private final Market market;
	private final HashMap<Integer, Integer> cart;
	private final int maxRandomAmount;
	private final double willing2Purchase;
	private final Semaphore waiting4Check;
	private Counter currentCounter;
	private double total;
	private final Semaphore left;
	public Customer(int id, Market market, int maxRandomAmount, double willing2Purchase, Semaphore left) {
		this.id = id;
		this.market = market;
		this.maxRandomAmount = maxRandomAmount;
		this.willing2Purchase = willing2Purchase;
		cart = new HashMap<>();
		waiting4Check = new Semaphore(0);
		currentCounter = null;
		total = 0;
		this.left = left;
	}
	@Override
	public void run() {
		int countTypes = market.getCountTypes();
		System.out.printf("Customer %d - ARRIVED!\n", id);
		for(int i = 0; i < countTypes; ++ i) {
			boolean purchase = Math.random() <= willing2Purchase;
			if(!purchase) continue;
			int amount = (int)((1 - Math.random()) * maxRandomAmount);
			cart.putIfAbsent(i, market.purchase(i, amount));
			System.out.printf("Customer %d - purchased: %d - amount: %d\n", id, i, amount);
			randomSleep();
		}
		// 添加查看收银台哪个位置人最少并等待队列
		int minIndex = 0;
		for(int i = 1; i < market.getNumberOfCounters(); ++ i) {
			if(market.getCounterQueueLength(i) < market.getCounterQueueLength(minIndex))
				minIndex = i;
		}
		this.currentCounter = market.getCounter(minIndex);
		System.out.printf("Customer %d - via %d counter\n", id, minIndex);
		currentCounter.wakeup(this);
		System.out.printf("Customer %d - waiting 4 counter\n", id);
		try {
			this.waiting4Check.acquire();
		} catch(InterruptedException e) {
			throw new RuntimeException(e);
		}
		System.out.printf("Customer %d - counter is ready.\n", id);
		cart.forEach((key, value) -> {
			if(value == 0) return;
			randomSleep();
			currentCounter.check(key, value);
			try {
				waiting4Check.acquire();
			} catch(InterruptedException e) {
				throw new RuntimeException(e);
			}
		});
		currentCounter.check(-1, -1);

		System.out.printf("Customer %d - total cost: %.2f\n", id, total);
		randomSleep();
		System.out.printf("Customer %d - LEFT!\n", id);
		left.release();
	}
	public void wakeup(Counter currentCounter) {
		// 该结账了 或 结账下一个类别
		if(currentCounter != null) this.currentCounter = currentCounter;
		this.waiting4Check.release();
	}
	public void addTotal(double cur) {
		System.out.printf("Customer %d - cost: %.2f\n", id, cur);
		total += cur;
	}
	public int getId() {
		return id;
	}
	private void randomSleep() {
		try {
			Thread.sleep((long)(Math.random() * 1000));
		} catch(InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
