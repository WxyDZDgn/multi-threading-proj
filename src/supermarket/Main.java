package supermarket;

import java.util.concurrent.Semaphore;

public class Main {
	public static void main(String [] args) throws InterruptedException {
		final int NUMBER_OF_CUSTOMERS = 10; // 顾客数量
		final int NUMBER_OF_COUNTERS = 3; // 收银员数量
		final int MAX_RANDOM_AMOUNT = 3; // 顾客同种商品最大购买数
		final double WILLING_TO_PURCHASE = 0.5; // 顾客单类购买意愿概率
		final int COUNT_TYPES = 10; // 商品种类数
		final int MAX_RANDOM_COUNT = 50; // 商品最大库存数
		final double MAX_RANDOM_PRICE = 11.45; // 商品最大单价

		final Semaphore left = new Semaphore(0);
		final Semaphore arrival = new Semaphore(0);
		final Customer [] customers = new Customer[NUMBER_OF_CUSTOMERS];
		final Counter [] counters = new Counter[NUMBER_OF_COUNTERS];
		final Market market = new Market(COUNT_TYPES, MAX_RANDOM_COUNT, MAX_RANDOM_PRICE, counters);
		for(int i = 0; i < NUMBER_OF_CUSTOMERS; ++ i)
			customers[i] = new Customer(i, market, MAX_RANDOM_AMOUNT, WILLING_TO_PURCHASE, left);
		for(int i = 0; i < NUMBER_OF_COUNTERS; ++ i)
			counters[i] = new Counter(i, market, arrival);
		Thread [] threads4Counters = new Thread[NUMBER_OF_COUNTERS];
		Thread [] threads4Customers = new Thread[NUMBER_OF_CUSTOMERS];
		for(int i = 0; i < NUMBER_OF_COUNTERS; ++ i) {
			threads4Counters[i] = new Thread(counters[i], String.format("Counter %d", i));
			threads4Counters[i].start();
		}
		arrival.acquire(NUMBER_OF_COUNTERS);
		System.out.println("-----Market is now OPEN!-----");
		for(int i = 0; i < NUMBER_OF_CUSTOMERS; ++ i) {
			threads4Customers[i] = new Thread(customers[i], String.format("Customer %d", i));
			threads4Customers[i].start();
		}
		left.acquire(NUMBER_OF_CUSTOMERS);
		System.out.println("-----Market is now CLOSED!-----");
		for(int i = 0; i < NUMBER_OF_COUNTERS; ++ i) {
			counters[i].wakeup();
		}

	}
}
