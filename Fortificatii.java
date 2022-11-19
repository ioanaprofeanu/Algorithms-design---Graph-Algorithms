// Profeanu Ioana, 323CA
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import static java.util.Collections.nCopies;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * Class for solving the Fortificatii problem
 */
public class Fortificatii {
	static class Task {
		public static final String INPUT_FILE = "fortificatii.in";
		public static final String OUTPUT_FILE = "fortificatii.out";
		int n, m, b;
		long k;

		/**
		 * Class which contains data relative to a certain city's neighbor
		 */
		static class Neighbor {
			int cityNumber;
			long timeCost;

			public Neighbor(int cityNumber, long timeCost) {
				this.cityNumber = cityNumber;
				this.timeCost = timeCost;
			}
		}

		/**
		 * The node data used for Dijkstra within the queue
		 */
		static class DijkstraNode implements Comparable<DijkstraNode> {
			int node;
			long timeFromCapital;

			public DijkstraNode(int node, long timeFromCapital) {
				this.node = node;
				this.timeFromCapital = timeFromCapital;
			}

			// the comparator is used for the priority queue
			@Override
			public int compareTo(DijkstraNode o) {
				long diff = timeFromCapital - o.timeFromCapital;
				if (diff > 0) {
					return 1;
				}
				if (diff < 0) {
					return -1;
				}
				return 0;
			}
		}

		// adjacency list where each index of the list represents
		// the neighbor nodes of the ith city
		ArrayList<ArrayList<Neighbor>> adjacencyList = new ArrayList<>();
		// similar to a frequency list, if isBarbar.get(i) is 1, it means
		// the ith city is occupied by barbars; otherwise, the value is 0
		ArrayList<Integer> isBarbar;
		// array with the values in which the barbars reach the capital
		ArrayList<Long> timeUntilCapital = new ArrayList<>();
		// array of times used for Dijkstra's algorithm
		ArrayList<Long> times;
		static long inf = Long.MAX_VALUE;

		public void solve() throws FileNotFoundException {
			readInput();
			writeOutput();
		}

		/**
		 * Read the input from the file
		 */
		private void readInput() throws FileNotFoundException {
			Scanner sc = new Scanner(new File(INPUT_FILE));
			// read the number of cities
			n = sc.nextInt();
			// read the number of routes
			m = sc.nextInt();
			// read the max number of fortifications
			k = sc.nextLong();
			// initialise list of barbars
			isBarbar = new ArrayList<>(nCopies(n + 1, 0));
			// read barbar cities and change the arraylist of barbars
			b = sc.nextInt();
			for (int i = 0; i < b; i++) {
				int barbar = sc.nextInt();
				isBarbar.set(barbar, 1);
			}
			// initialise adjacency list
			initialiseAdjacencyList();
			// read edges
			for (int i = 0; i < m; i++) {
				int city1 = sc.nextInt();
				int city2 = sc.nextInt();
				long time = sc.nextLong();
				Neighbor neighbor1 = new Neighbor(city1, time);
				Neighbor neighbor2 = new Neighbor(city2, time);
				adjacencyList.get(city1).add(neighbor2);
				adjacencyList.get(city2).add(neighbor1);
			}
			sc.close();
		}

		/**
		 * Initialise the adjacency list
		 */
		private void initialiseAdjacencyList() {
			ArrayList<Neighbor> dummyArraylist = new ArrayList<>();
			adjacencyList.add(dummyArraylist);
			for (int i = 1; i <= n; i++) {
				ArrayList<Neighbor> newDummyArraylist = new ArrayList<>();
				adjacencyList.add(newDummyArraylist);
			}
		}

		/**
		 * Write the output in the file
		 */
		private void writeOutput() {
			try {
				PrintWriter pw = new PrintWriter(OUTPUT_FILE);
				dijkstra();
				getTimesCapitalBarbars();
				// print the result
				pw.println(getResult());
				pw.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		/**
		 * Use Dijkstra's algorithm to find the time from the capital to
		 * each city which is not occupied by barbars; we stop going further once
		 * finding a barbar city
		 */
		private void dijkstra() {
			// initialise times with inf
			times = new ArrayList<>(nCopies(n + 1, inf));
			// we start from the 1st city
			times.set(1, 0L);
			// create priority queue and add first element in queue
			PriorityQueue<DijkstraNode> queue = new PriorityQueue<>();
			queue.add(new DijkstraNode(1, 0L));
			// while the queue is not empty
			while (queue.size() != 0) {
				// get the first node from the queue
				DijkstraNode currentNode = queue.remove();
				// get each neighbor of the node
				for (Neighbor neighbor : adjacencyList.get(currentNode.node)) {
					// we exclude the cities which are barbars; we only calculate
					// the time from an unoccupied city to another
					// if the current time from source + the time cost to
					// visit the neighbor is lower than the current time to it
					if (isBarbar.get(neighbor.cityNumber) == 0 && currentNode.timeFromCapital
						+ neighbor.timeCost
							< times.get(neighbor.cityNumber)) {
						times.set(neighbor.cityNumber, currentNode.timeFromCapital
							+ neighbor.timeCost);
						queue.add(new DijkstraNode(neighbor.cityNumber,
							times.get(neighbor.cityNumber)));
					}
				}
			}
		}

		/**
		 * Calculate the time from the barbar cities to the capital by
		 * adding the time its neighbor gets to the capital with the time
		 * the barbar gets to its neighbor
		 */
		private void getTimesCapitalBarbars() {
			// take each unoccupied city that has a barbar as neighbor and
			// calculate the times from the capital to it and add it to a list
			for (int i = 1; i <= n; i++) {
				if (times.get(i) != inf  && isBarbar.get(i) == 0) {
					for (Neighbor neighbor : adjacencyList.get(i)) {
						if (isBarbar.get(neighbor.cityNumber) == 1) {
							long time = times.get(i) + neighbor.timeCost;
							timeUntilCapital.add(time);
						}
					}
				}
			}
			// sort the list in ascending order
			Collections.sort(timeUntilCapital);
		}

		/**
		 * Calculate the result
		 */
		private long getResult() {
			// create a list in which we keep the fortified roads
			ArrayList<Long> fortified = new ArrayList<>();
			// the minimum number of fortifications found on a road
			long currentMin = timeUntilCapital.get(0) + k;
			// add the minimum time from the capital in the arraylist
			fortified.add(timeUntilCapital.get(0));
			// if there is only one barbar city, then the result is the time
			// plus k
			if (timeUntilCapital.size() == 1) {
				return currentMin;
			}

			while (k > 0 && timeUntilCapital.size() > fortified.size()) {
				// add the next minimum time to capital from the array
				fortified.add(timeUntilCapital.get(fortified.size()));
				long lastMinValue = fortified.get(fortified.size() - 2);
				long currentMaxValue = fortified.get(fortified.size() - 1);
				long fortifications = 0;

				// if we can add fortifications to the fortified.size() - 1 roads
				// so that the time matches the time of the last roaad in the fortified
				// arraylist
				if (k >= ((currentMaxValue - lastMinValue) * (fortified.size() - 1))) {
					// get the number of fortifications for each road
					fortifications = currentMaxValue - lastMinValue;
					// change the value of k
					k -= fortifications * (fortified.size() - 1);
				} else {
					// if we don't have enough fortifications left, we will make
					// as many equal fortifications as possible for the
					// fortified.size() - 1 roads
					fortifications = k / (fortified.size() - 1);
					k = 0;
				}
				// for each minimum roads in the list, add the fortifications by increasing
				// the time to the capital
				for (int i = 0; i < fortified.size() - 1; i++) {
					long currentValue = fortified.get(i);
					currentValue += fortifications;
					fortified.set(i, currentValue);
				}
				// change current minim
				currentMin = fortified.get(0);
			}
			// if we still have fortifications left
			if (k > 0 && timeUntilCapital.size() == fortified.size()) {
				currentMin += k / timeUntilCapital.size();
			}
			return currentMin;
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		new Task().solve();
	}
}