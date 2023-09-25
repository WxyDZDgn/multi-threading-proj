package adjacentFilter;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;

public class Node implements Runnable {
	private final int id;
	private final Semaphore current;
	private final Deque<Integer> todo;
	private Node previous;
	private Node next;
	private final int leftRange, rightRange;
	private final List<Integer> res;
	private final Semaphore accomplish;
	public Node(int id, int leftRange, int rightRange, Semaphore accomplish) {
		this.id = id;
		this.current = new Semaphore(0);
		this.todo = new LinkedBlockingDeque<>();
		this.res = new ArrayList<>();
		this.leftRange = leftRange;
		this.rightRange = rightRange;
		this.accomplish = accomplish;
	}
	public void put(int val) {
		System.out.printf("%d - %d is received...\n", this.id, val);
		this.todo.addFirst(val);
		this.current.release();
	}

	public void setAdjacentNode(Node previous, Node next) {
		this.previous = previous;
		this.next = next;
	}

	@Override
	public void run() {
		while(true) {
			try {
				this.current.acquire();
			} catch(InterruptedException e) {
				throw new RuntimeException(e);
			}
			Integer val = this.todo.pollLast();
			if(val == null || val < 0) break;
			else if(leftRange <= val && val < rightRange) {
				System.out.printf("%d - %d is filtered!\n", this.id, val);
				res.add(val);
				this.accomplish.release();
			} else if(previous != null && val < leftRange) {
				System.out.printf("%d - %d is not in [%d, %d), moved to pre.\n", this.id, val, this.leftRange, this.rightRange);
				previous.put(val);
			} else if(next != null && rightRange <= val) {
				System.out.printf("%d - %d is not in [%d, %d), moved to nxt.\n", this.id, val, this.leftRange, this.rightRange);
				next.put(val);
			} else {
				System.out.printf("sth is wrong on node %d with value %d!\n", id, val);
			}
		}
		this.res.sort(null);
		System.out.printf("%d - LEN: %d - LS: %s\n", id, res.size(), res);
	}
}
