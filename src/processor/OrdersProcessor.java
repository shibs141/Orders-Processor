package processor;

import java.io.BufferedWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class OrdersProcessor {

	private static void getGrandSummary(TreeMap<String, Integer> allOrders, TreeMap<String, Double> itemInfo,
			BufferedWriter bufferedWriter) {
		double grandTotal = 0;
		for (Map.Entry<String, Integer> entry : allOrders.entrySet()) {
			String summary = "";
			String itemName = entry.getKey();
			int quantity = entry.getValue();
			double cost = itemInfo.get(itemName), total = cost * quantity;
			grandTotal += total;
			summary += "Summary - Item's name: " + itemName + ", Cost per item: "
					+ NumberFormat.getCurrencyInstance().format(cost) + ", Number sold: " + quantity
					+ ", Item's Total: " + NumberFormat.getCurrencyInstance().format(total);

			try {
				bufferedWriter.write(summary + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			bufferedWriter.write("Summary Grand Total: " + NumberFormat.getCurrencyInstance().format(grandTotal));
			bufferedWriter.newLine();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void getPersonalSummary(TreeMap<String, Double> itemInfo, Set<Entry<String, Integer>> client,
			BufferedWriter bufferedWriter) {
		double orderTotal = 0;
		for (Map.Entry<String, Integer> entry : client) {
			String itemName = entry.getKey();
			String orderSummary = "";
			double cost = itemInfo.get(itemName);
			int quantity = entry.getValue();
			double total = cost * quantity;
			orderTotal += total;
			orderSummary += "Item's name: " + itemName + ", Cost per item: "
					+ NumberFormat.getCurrencyInstance().format(cost) + ", Quantity: " + quantity + ", Cost: "
					+ NumberFormat.getCurrencyInstance().format(total);

			try {
				bufferedWriter.write(orderSummary + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			bufferedWriter.write("Order Total: " + NumberFormat.getCurrencyInstance().format(orderTotal));
			bufferedWriter.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		// Asking questions for info
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter item's data file name: "); // file with item costs and names
		String filename = scanner.next();

		System.out.println("Enter 'y' for multiple threads, any other character otherwise: "); // multiple or single //
																								// thread
		String threadOrNot = scanner.next();
		boolean thr = threadOrNot.compareTo("y") == 0 ? true : false;

		System.out.println("Enter number of orders to process: "); // how many orders to process
		int orders = scanner.nextInt();

		System.out.println("Enter order's base filename: "); // files with client info
		String base = scanner.next();

		System.out.println("Enter result's filename: "); // file with results
		String result = scanner.next();

		scanner.close();

		long startTime = System.currentTimeMillis(); // start timer

		BufferedWriter bufferedWriter = null;
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(result));
		} catch (IOException e) {
			e.printStackTrace();
		}

		TreeMap<String, Double> itemInfo = new TreeMap<>();
		Scanner itemScanner = new Scanner(new File(filename)); // Scanning file for item costs and names
		while (itemScanner.hasNextLine()) {
			String item = itemScanner.next();
			double cost = itemScanner.nextDouble();
			itemInfo.put(item, cost);
		}

		itemScanner.close();
		TreeMap<String, Integer> allOrders = new TreeMap<>();

		if (thr) { // multi thread
			Thread[] allThreads = new Thread[orders];
			Client[] clients = new Client[orders];

			for (int i = 0; i < orders; i++) {
				Client client = new Client(new TreeMap<String, Integer>(), allOrders,
						new Scanner(new File(base + (i + 1) + ".txt")), itemInfo);
				allThreads[i] = new Thread(client);
				clients[i] = client;
			}

			for (Thread thread : allThreads) {
				thread.start();
			}

			for (Thread thread : allThreads) {
				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			for (Client clientRef : clients) {
				try {
					bufferedWriter.write("----- Order details for client with Id: " + clientRef.clientId + " -----");
					bufferedWriter.newLine();
				} catch (IOException e) {
					e.printStackTrace();
				}

				getPersonalSummary(itemInfo, clientRef.personal.entrySet(), bufferedWriter);
			}
		} else {
			for (int i = 1; i <= orders; i++) { // single thread
				Scanner clientScanner = new Scanner(new File(base + i + ".txt"));

				clientScanner.next();
				int clientId = clientScanner.nextInt();

				TreeMap<String, Integer> clientOrders = new TreeMap<>();
				System.out.println("Reading order for client with id: " + clientId);
				while (clientScanner.hasNextLine()) {
					String item = clientScanner.next();
					Integer cnt = clientOrders.get(item);
					Integer allCnt = allOrders.get(item);
					clientOrders.put(item, cnt != null ? ++cnt : 1);
					allOrders.put(item, allCnt != null ? ++allCnt : 1);
					clientScanner.nextLine();

				}
				try {
					bufferedWriter.write("----- Order details for client with Id: " + clientId + " -----\n");
				} catch (IOException e) {
					e.printStackTrace();
				}

				getPersonalSummary(itemInfo, clientOrders.entrySet(), bufferedWriter);
			}
		}
		try {
			bufferedWriter.write("***** Summary of all orders *****\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		getGrandSummary(allOrders, itemInfo, bufferedWriter);

		try {
			bufferedWriter.flush();
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		long endTime = System.currentTimeMillis(); // end timer
		double timeTaken = endTime - startTime;
		System.out.println("Processing time (msec): " + timeTaken);
		System.out.println("Results can be found in the file: " + result);
	}
}