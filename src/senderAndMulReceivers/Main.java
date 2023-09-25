package senderAndMulReceivers;

import java.util.concurrent.Semaphore;

public class Main {
	public static void main(String [] args) {
		final int NUMBER_OF_RECEIVERS = 5;

		Semaphore mutex = new Semaphore(1);
		Semaphore empty = new Semaphore(1);
		Semaphore [] full = new Semaphore[NUMBER_OF_RECEIVERS];
		for(int i = 0; i < NUMBER_OF_RECEIVERS; ++ i) {
			full[i] = new Semaphore(0);
		}
		Semap i = new Semap();
		Thread senders = new Thread(new Sender(empty, mutex, full, i));
		Thread [] receivers = new Thread[NUMBER_OF_RECEIVERS];
		for(int j = 0; j < NUMBER_OF_RECEIVERS; ++ j) {
			receivers[j] = new Thread(new Receiver(empty, mutex, full[j], i, NUMBER_OF_RECEIVERS, j));
		}
		for(int j = 0; j < NUMBER_OF_RECEIVERS; ++ j) {
			receivers[j].start();
		}
		senders.start();
	}
}
