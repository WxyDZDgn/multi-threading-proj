package supermarket;

public class Market {
	private final int countTypes;
	private final int [] counts;
	private final double [] prices;
	private final Counter [] counters;
	private final int numberOfCounters;
	public Market(int countTypes, int maxRandomCount, double maxRandomPrice, Counter [] counters) {
		this.countTypes = countTypes;
		counts = new int[countTypes];
		prices = new double[countTypes];
		for(int i = 0; i < countTypes; ++ i) {
			counts[i] = (int)((1 - Math.random()) * maxRandomCount);
			prices[i] = (1 - Math.random()) * maxRandomPrice;
		}
		this.counters = counters;
		numberOfCounters = counters.length;
	}
	public double getPrice(int idx) {
		return prices[idx];
	}
	public synchronized int purchase(int idx, int amount) {
		int delta = Math.min(amount, counts[idx]);
		counts[idx] -= delta;
		return delta;
	}
	public int getCountTypes() {
		return countTypes;
	}
	public int getNumberOfCounters() {
		return numberOfCounters;
	}
	public synchronized int getCounterQueueLength(int idx) {
		return counters[idx].currentQueueLength();
	}
	public synchronized Counter getCounter(int idx) {
		return counters[idx];
	}
}
