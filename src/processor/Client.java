package processor;

import java.util.Scanner;
import java.util.TreeMap;

public class Client implements Runnable {
	protected TreeMap<String, Integer> personal;
	protected TreeMap<String, Integer> shared;
	protected TreeMap<String, Double> itemInfo;
	protected Scanner fileToScan;
	protected int clientId;

	public Client(TreeMap<String, Integer> personal, TreeMap<String, Integer> shared, Scanner fileToScan,
			TreeMap<String, Double> itemInfo) {
		this.personal = personal;
		this.shared = shared;
		this.fileToScan = fileToScan;
		this.itemInfo = itemInfo;
	}

	@Override
	public void run() {
		fileToScan.next();
		this.clientId = fileToScan.nextInt();

		System.out.println("Reading order for client with id: " + clientId);
		while (fileToScan.hasNextLine()) {
			String item = fileToScan.next();
			Integer cnt = personal.get(item);
			personal.put(item, cnt != null ? ++cnt : 1);

			synchronized (shared) {
				Integer allCnt = shared.get(item);
				shared.put(item, allCnt != null ? ++allCnt : 1);
			}
			fileToScan.nextLine();
		}

	}

}
