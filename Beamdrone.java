// Profeanu Ioana, 323CA
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import static java.util.Collections.nCopies;
import java.util.Deque;
import java.util.Scanner;

/**
 * Class for solving the Beamdrone problem
 */
public class Beamdrone {
	static class Task {
		public static final String INPUT_FILE = "beamdrone.in";
		public static final String OUTPUT_FILE = "beamdrone.out";
		// input data
		int n, m, xi, yi, xf, yf;
		// arraylist of arraylists containing the racetracks
		// 0 means wall, 1 means empty space
		ArrayList<ArrayList<Integer>> racetrack;
		// arraylist of arraylists containing the costs for
		// when the node is reached with and without changing
		// direction
		ArrayList<ArrayList<Costs>> distancesCost;

		static int inf = Integer.MAX_VALUE;

		/**
		 * Class which contains the data relative to a cell
		 * that will be visited at a certain point
		 */
		static class CellData {
			// coordinates
			int x, y;
			// the current cost
			int currentCost;
			// the direction - false for vertically,
			// true for horizontally
			boolean direction;

			// create new object
			public CellData(int x, int y, int currentCost, boolean direction) {
				this.x = x;
				this.y = y;
				this.currentCost = currentCost;
				this.direction = direction;
			}
		}

		// Class which contains the cost of a cell
		// from when it is visited via a vertical or a
		// horizontal direction
		static class Costs {
			int verticalCost;
			int horizontalCost;

			public Costs() {
				this.verticalCost = inf;
				this.horizontalCost = inf;
			}
		}

		public void solve() throws IOException {
			readInput();
			writeOutput();
		}

		/**
		 * Read the input from the file
		 */
		private void readInput() throws IOException {
			Scanner sc = new Scanner(new File(INPUT_FILE));
			// read the board dimensions
			n = sc.nextInt();
			m = sc.nextInt();
			// read start and finish coordinates
			xi = sc.nextInt();
			yi = sc.nextInt();
			xf = sc.nextInt();
			yf = sc.nextInt();
			sc.nextLine();
			// initialise the racetrack; the value is 1 if
			// it is an empty cell, or 0 if it is a wall
			initialiseRacetrack();
			// read racetrack
			for (int i = 0; i < n; i++) {
				String line = sc.nextLine();
				for (int j = 0; j < m; j++) {
					char character = line.charAt(j);
					// if it is a wall, change the value to 0
					if (character == 'W') {
						racetrack.get(i).set(j, 0);
					}
				}
			}
			sc.close();
		}

		/**
		 * Initialise the racetrack arraylist of arraylists by adding
		 * the value 1 everywhere (assuming we have no walls)
		 */
		private void initialiseRacetrack() {
			racetrack = new ArrayList<>(n);
			for (int i = 0; i < n; i++) {
				ArrayList<Integer> newDummyArraylist = new ArrayList<>(nCopies(m, 1));
				racetrack.add(newDummyArraylist);
			}
		}

		/**
		 * Initialise the arraylist of arraylist with the cost of
		 * each cell by creating new Costs objects
		 */
		private void initialiseDistancesCost() {
			distancesCost = new ArrayList<>();
			for (int i = 0; i < n; i++) {
				ArrayList<Costs> newCostsLine = new ArrayList<>();
				for (int j = 0; j < m; j++) {
					Costs newCost = new Costs();
					newCostsLine.add(newCost);
				}
				distancesCost.add(newCostsLine);
			}
		}

		/**
		 * Write the output in the file
		 */
		private void writeOutput() {
			try {
				initialiseDistancesCost();
				PrintWriter pw = new PrintWriter(OUTPUT_FILE);
				// print the result
				pw.printf("%d", getResult());
				pw.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		/**
		 * Calculate the minimum cost for reaching the finish
		 */
		private int getResult() {
			// create deque
			Deque<CellData> deque = new ArrayDeque<>();
			// start from the initial cell and create two objects, one that goes
			// vertically and another that goes horizontally
			CellData newCellVertical = new CellData(xi, yi, 0, false);
			CellData newCellHorizontal = new CellData(xi, yi, 0, true);
			// add the vertical one to the front
			deque.addFirst(newCellVertical);
			// add the horizontal one to the back
			deque.addLast(newCellHorizontal);
			// change the horizontal and vertical cost of the cell
			distancesCost.get(xi).get(yi).horizontalCost = 0;
			distancesCost.get(xi).get(yi).verticalCost = 0;

			// while the dequeue is not empty
			while (deque.size() != 0) {
				// get and remove the cell at the beginning of the deque
				CellData currentCell = deque.getFirst();
				deque.removeFirst();
				// if the current direction is vertically
				if (!currentCell.direction) {
					// keep direction
					// check if we can move upwards
					if (currentCell.x - 1 >= 0) {
						int newX = currentCell.x - 1;
						int y = currentCell.y;
						// if the new cell is empty
						if (racetrack.get(newX).get(y) == 1) {
							// check if the new
							// cost in cheaper than the current one
							if (currentCell.currentCost
								< distancesCost.get(newX).get(y).verticalCost) {
								// enqueue at the beginning and change the cost of the next cell
								deque.addFirst(new CellData(newX, y,
									currentCell.currentCost, false));
								distancesCost.get(newX).get(y).verticalCost =
									currentCell.currentCost;
							}
						}
					}
					// check if we can move downwards
					if (currentCell.x + 1 < n) {
						int newX = currentCell.x + 1;
						int y = currentCell.y;
						// if the new cell is empty
						if (racetrack.get(newX).get(y) == 1) {
							// check if the new
							// cost in cheaper than the current one
							if (currentCell.currentCost
								< distancesCost.get(newX).get(y).verticalCost) {
								// enqueue at the beginning and change the cost of the next cell
								deque.addFirst(new CellData(newX, y,
									currentCell.currentCost, false));
								distancesCost.get(newX).get(y).verticalCost =
									currentCell.currentCost;
							}
						}
					}
					// change direction
					// check if we can move to the left
					if (currentCell.y - 1 >= 0) {
						int x = currentCell.x;
						int newY = currentCell.y - 1;
						// if the new cell is empty
						if (racetrack.get(x).get(newY) == 1) {
							// check if the new cost is cheaper
							// than the current one (keep in mind we changed
							// directions, so we add 1 to the current cost for turning)
							if (currentCell.currentCost + 1
								< distancesCost.get(x).get(newY).horizontalCost) {
								// enqueue at the end and change the cost of the next cell
								deque.addLast(new CellData(x, newY,
									currentCell.currentCost + 1, true));
								distancesCost.get(x).get(newY).horizontalCost =
									currentCell.currentCost + 1;
							}
						}
					}
					// check if we can move to the right
					if (currentCell.y + 1 < m) {
						int x = currentCell.x;
						int newY = currentCell.y + 1;
						// if the new cell is empty
						if (racetrack.get(x).get(newY) == 1) {
							// check if the new cost is cheaper
							// than the current one (keep in mind we changed
							// directions, so we add 1 to the current cost for turning)
							if (currentCell.currentCost + 1
								< distancesCost.get(x).get(newY).horizontalCost) {
								// enqueue at the end and change the cost of the next cell
								deque.addLast(new CellData(x, newY,
									currentCell.currentCost + 1, true));
								distancesCost.get(x).get(newY).horizontalCost =
									currentCell.currentCost + 1;
							}
						}
					}
				}

				// if the current direction is horizontally
				if (currentCell.direction) {
					// keep direction
					// check if we can move to the left
					if (currentCell.y - 1 >= 0) {
						int x = currentCell.x;
						int newY = currentCell.y - 1;
						// if the new cell is empty
						if (racetrack.get(x).get(newY) == 1) {
							// check if the new cost is cheaper
							// than the current one
							if (currentCell.currentCost
								< distancesCost.get(x).get(newY).horizontalCost) {
								// enqueue at the beginning and change the cost of the next cell
								deque.addFirst(new CellData(x, newY,
									currentCell.currentCost, true));
								distancesCost.get(x).get(newY).horizontalCost =
									currentCell.currentCost;
							}
						}
					}
					// check if we can move to the right
					if (currentCell.y + 1 < m) {
						int x = currentCell.x;
						int newY = currentCell.y + 1;
						// if the new cell is empty
						if (racetrack.get(x).get(newY) == 1) {
							// check if the new cost is cheaper
							// than the current one
							if (currentCell.currentCost
								< distancesCost.get(x).get(newY).horizontalCost) {
								// enqueue at the beginning and change the cost of the next cell
								deque.addFirst(new CellData(x, newY,
									currentCell.currentCost, true));
								distancesCost.get(x).get(newY).horizontalCost =
									currentCell.currentCost;
							}
						}
					}

					// change direction
					// check if we can move upwards
					if (currentCell.x - 1 >= 0) {
						int newX = currentCell.x - 1;
						int y = currentCell.y;
						// if the new cell is empty
						if (racetrack.get(newX).get(y) == 1) {
							// check if the new cost is cheaper
							// than the current one (keep in mind we changed
							// directions, so we add 1 to the current cost for turning)
							if (currentCell.currentCost + 1
								< distancesCost.get(newX).get(y).verticalCost) {
								// enqueue at the end and change the cost of the next cell
								deque.addLast(new CellData(newX, y,
									currentCell.currentCost + 1, false));
								distancesCost.get(newX).get(y).verticalCost =
									currentCell.currentCost + 1;
							}
						}
					}
					// check if we can move downwards
					if (currentCell.x + 1 < n) {
						int newX = currentCell.x + 1;
						int y = currentCell.y;
						// if the new cell is empty
						if (racetrack.get(newX).get(y) == 1) {
							// check if the new cost is cheaper
							// than the current one (keep in mind we changed
							// directions, so we add 1 to the current cost for turning)
							if (currentCell.currentCost + 1
								< distancesCost.get(newX).get(y).verticalCost) {
								// enqueue at the end and change the cost of the next cell
								deque.addLast(new CellData(newX, y, currentCell
									.currentCost + 1, false));
								distancesCost.get(newX).get(y).verticalCost = currentCell
									.currentCost + 1;
							}
						}
					}
				}
			}

			// the result will be the minimum value between
			// the cost obtained horizontally and vertically
			return Math.min(distancesCost.get(xf).get(yf).horizontalCost,
				distancesCost.get(xf).get(yf).verticalCost);
		}
	}

	public static void main(String[] args) throws IOException {
		new Task().solve();
	}
}
