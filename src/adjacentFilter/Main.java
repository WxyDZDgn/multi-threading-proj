package adjacentFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Main {
	public static void main(String [] args) {
		/*
		 有 NON 个节点, 编号为 0 ~ NON - 1.
		 编号 i 的节点筛选区间 [ i * RLIN, (i + 1) * RLIN ) 范围内的数字.
		 若当前节点被传入的数字不为 -1 且不在范围内, 只能将该数字传给 "相邻编号" 的节点.
		 若数字为 -1 则结束程序并输出筛选结果.
		 */
		final int NUMBER_OF_NODES = 23; // 总节点数
		final int RANGE_LEN_IN_NODES = 17; // 每个节点筛选范围的长度
		final int TOTAL_TESTING_NUMBER = 114; // 用于测试的数据个数

		final Node [] nodes = new Node[NUMBER_OF_NODES];
		final Thread [] threads = new Thread[NUMBER_OF_NODES];
		final List<Integer> testingValues = new ArrayList<>();
		final Semaphore accomplish = new Semaphore(0);
		for(int i = 0; i < NUMBER_OF_NODES; ++ i)
			nodes[i] = new Node(i, i * RANGE_LEN_IN_NODES, (i + 1) * RANGE_LEN_IN_NODES, accomplish);
		for(int i = 0; i < NUMBER_OF_NODES; ++ i) {
			Node pre = i > 0 ? nodes[i - 1] : null;
			Node nxt = i < NUMBER_OF_NODES - 1 ? nodes[i + 1] : null;
			nodes[i].setAdjacentNode(pre, nxt);
			threads[i] = new Thread(nodes[i], String.format("node_%d", i));
			threads[i].start();
		}
		for(int i = 0; i < TOTAL_TESTING_NUMBER; ++ i) {
			final int randomIndex = (int)(Math.random() * NUMBER_OF_NODES);
			final int randomValue = (int)(Math.random() * (NUMBER_OF_NODES * RANGE_LEN_IN_NODES));
			testingValues.add(randomValue);
			nodes[randomIndex].put(randomValue);
		}

		try {
			accomplish.acquire(TOTAL_TESTING_NUMBER);
		} catch(InterruptedException e) {
			throw new RuntimeException(e);
		}
		System.out.printf("INPUT: %d - %s\n", testingValues.size(), testingValues);
		for(int i = 0; i < NUMBER_OF_NODES; ++ i) nodes[i].put(-1);

	}
}
