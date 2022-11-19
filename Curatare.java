// Profeanu Ioana, 323CA
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import static java.util.Collections.nCopies;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

/**
 * Class for solving the Curatare problem
 */
public class Curatare {
	static class Task {
		public static final String INPUT_FILE = "curatare.in";
		public static final String OUTPUT_FILE = "curatare.out";
		// input data
		int n, m;
		// arraylist of arraylists containing the accesibility of the
		// board; a cell is true if it can be accessed, false otherwise
		ArrayList<ArrayList<Boolean>> accesibility;
		// store the index number and the map indices of the robots
		ArrayList<CellData> robots = new ArrayList<>();
		// store the index number and the map indices of the dirty cells
		ArrayList<CellData> dirts = new ArrayList<>();
		// arraylist of arraylist which contains the distance of
		// each dirt to each of the cells in the map
		ArrayList<ArrayList<CellDistanceFromDirt>> disanceFromDirts;
		// arraylist which contains the permuted robots
		ArrayList<CellData> permuteDirt;
		// for the ith dirt, assign a certain robot to clean it; the value
		// stored is the index of the assigned robot
		ArrayList<Integer> assignRobots;
		// the maximum time until cleaning all the dirt
		int minimumCleaningTime;

		/**
		 * Class which contains the data relative to a cell
		 * that will be visited at a certain point
		 */
		static class CellData {
			// order number and coordinates
			int orderNumber;
			int x, y;

			// create new object
			public CellData(int x, int y, int orderNumber) {
				this.x = x;
				this.y = y;
				this.orderNumber = orderNumber;
			}
		}

		/**
		 * Class which contains the number of seconds it
		 * takes to reach a certain accessible cell from each
		 * of the given dirt cells
		 */
		static class CellDistanceFromDirt {
			int distanceFromDirt0;
			int distanceFromDirt1;
			int distanceFromDirt2;
			int distanceFromDirt3;

			public CellDistanceFromDirt() {
				distanceFromDirt0 = -1;
				distanceFromDirt1 = -1;
				distanceFromDirt2 = -1;
				distanceFromDirt3 = -1;
			}

			public int returnDistanceFromCurrentDirt(CellData dirt) {
				if (dirt.orderNumber == 0) {
					return distanceFromDirt0;
				}
				if (dirt.orderNumber == 1) {
					return distanceFromDirt1;
				}
				if (dirt.orderNumber == 2) {
					return distanceFromDirt2;
				}
				if (dirt.orderNumber == 3) {
					return distanceFromDirt3;
				}
				return -1;
			}

			public void changeDistanceFromCurrentDirt(CellData dirt, int newValue) {
				if (dirt.orderNumber == 0) {
					distanceFromDirt0 = newValue;
				}
				if (dirt.orderNumber == 1) {
					distanceFromDirt1 = newValue;
				}
				if (dirt.orderNumber == 2) {
					distanceFromDirt2 = newValue;
				}
				if (dirt.orderNumber == 3) {
					distanceFromDirt3 = newValue;
				}
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
			// read the map dimensions
			n = sc.nextInt();
			m = sc.nextInt();
			sc.nextLine();
			// initialise the accessibility board
			initialiseBoard();
			// read board
			for (int i = 0; i < n; i++) {
				String line = sc.nextLine();
				for (int j = 0; j < m; j++) {
					char character = line.charAt(j);
					// if it is a blocked, change the accessibility to false
					if (character == 'X') {
						accesibility.get(i).set(j, false);
					}
					// if robot, add it to list
					if (character == 'R') {
						robots.add(new CellData(i, j, robots.size()));
					}
					// if dirt, add it to list
					if (character == 'S') {
						dirts.add(new CellData(i, j, dirts.size()));
					}
				}
			}
			// initialise all cells distances from each one of the four possible dirts
			initialiseDistanceFromDirts();
			sc.close();
		}

		/**
		 * Initialise the board arraylist of arraylists by adding
		 * the value true everywhere (assuming all cells are accessible)
		 */
		private void initialiseBoard() {
			accesibility = new ArrayList<>(n);
			for (int i = 0; i < n; i++) {
				ArrayList<Boolean> newDummyArraylist = new ArrayList<>(nCopies(m, true));
				accesibility.add(newDummyArraylist);
			}
		}

		/**
		 * Initialise the arraylist of arraylist with the distance from each
		 * one of the four dirts
		 */
		private void initialiseDistanceFromDirts() {
			disanceFromDirts = new ArrayList<>();
			for (int i = 0; i < n; i++) {
				ArrayList<CellDistanceFromDirt> newCellLine = new ArrayList<>();
				for (int j = 0; j < m; j++) {
					CellDistanceFromDirt newCell = new CellDistanceFromDirt();
					newCellLine.add(newCell);
				}
				disanceFromDirts.add(newCellLine);
			}
		}

		/**
		 * For each cell, get its distance from all of the four dirts
		 */
		private void getDistanceFromDirts() {
			// for each dirt
			for (CellData dirt : dirts) {
				// create queue and add the dirt's current cell
				Queue<CellData> queue = new LinkedList<>();
				disanceFromDirts.get(dirt.x).get(dirt.y)
					.changeDistanceFromCurrentDirt(dirt, 0);
				queue.add(dirt);
				// while the queue is not empty
				while (queue.size() != 0) {
					CellData currentCell = queue.peek();
					queue.remove();
					// check if we can move upward
					if (currentCell.x - 1 >= 0) {
						int newX = currentCell.x - 1;
						int y = currentCell.y;
						// if the new cell is empty and has not yet been visited
						if (accesibility.get(newX).get(y)
							&& disanceFromDirts.get(newX).get(y).returnDistanceFromCurrentDirt(dirt)
							== -1) {
							int newDistance = disanceFromDirts.get(currentCell.x)
								.get(currentCell.y).returnDistanceFromCurrentDirt(dirt) + 1;
							disanceFromDirts.get(newX).get(y)
								.changeDistanceFromCurrentDirt(dirt, newDistance);
							// add the current cell to the queue
							CellData newCell = new CellData(newX, y, -1);
							queue.add(newCell);
						}
					}
					// check if we can move downwards
					if (currentCell.x + 1 < n) {
						int newX = currentCell.x + 1;
						int y = currentCell.y;
						// if the new cell is empty and has not yet been visited
						if (accesibility.get(newX).get(y)
							&& disanceFromDirts.get(newX).get(y).returnDistanceFromCurrentDirt(dirt)
							== -1) {
							int newDistance = disanceFromDirts.get(currentCell.x)
								.get(currentCell.y).returnDistanceFromCurrentDirt(dirt) + 1;
							disanceFromDirts.get(newX).get(y)
								.changeDistanceFromCurrentDirt(dirt, newDistance);
							CellData newCell = new CellData(newX, y, -1);
							queue.add(newCell);
						}
					}

					// check if we can move to the left
					if (currentCell.y - 1 >= 0) {
						int x = currentCell.x;
						int newY = currentCell.y - 1;
						// if the new cell is empty and has not yet been visited
						if (accesibility.get(x).get(newY)
							&& disanceFromDirts.get(x).get(newY).returnDistanceFromCurrentDirt(dirt)
							== -1) {
							int newDistance = disanceFromDirts.get(currentCell.x)
								.get(currentCell.y).returnDistanceFromCurrentDirt(dirt) + 1;
							disanceFromDirts.get(x).get(newY).changeDistanceFromCurrentDirt(dirt,
								newDistance);
							CellData newCell = new CellData(x, newY, -1);
							queue.add(newCell);
						}
					}

					// check if we can move to the right
					if (currentCell.y + 1 < m) {
						int x = currentCell.x;
						int newY = currentCell.y + 1;
						// if the new cell is empty and has not yet been visited
						if (accesibility.get(x).get(newY)
							&& disanceFromDirts.get(x).get(newY).returnDistanceFromCurrentDirt(dirt)
							== -1) {
							int newDistance = disanceFromDirts.get(currentCell.x)
								.get(currentCell.y).returnDistanceFromCurrentDirt(dirt) + 1;
							disanceFromDirts.get(x).get(newY).changeDistanceFromCurrentDirt(dirt,
								newDistance);
							CellData newCell = new CellData(x, newY, -1);
							queue.add(newCell);
						}
					}
				}
			}
		}

		/**
		 * Write the output in the file
		 */
		private void writeOutput() {
			try {
				minimumCleaningTime = n * m + 1;
				getDistanceFromDirts();
				permuteDirt = new ArrayList<>(dirts);
				permuteDirt(permuteDirt.size());
				PrintWriter pw = new PrintWriter(OUTPUT_FILE);
				// print the result
				pw.printf("%d", minimumCleaningTime);
				pw.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		/**
		 * Using Heap's algorithm, generate all permutations of the
		 * the maximum four dirts given
		 * @param size the current size
		 */
		private void permuteDirt(int size) {
			// when reaching one element
			if (size == 1) {
				assignRobots = new ArrayList<>(dirts.size());
				generateRobotsAssigments(0);
			} else {
				for (int i = 0; i < size - 1; i++) {
					permuteDirt(size - 1);
					CellData temp;
					// interchange elements depending on the size's parity
					if (size % 2 == 0) {
						temp = permuteDirt.get(i);
						permuteDirt.set(i, permuteDirt.get(size - 1));
					} else {
						temp = permuteDirt.get(0);
						permuteDirt.set(0, permuteDirt.get(size - 1));
					}
					permuteDirt.set(size - 1, temp);
				}
				permuteDirt(size - 1);
			}
		}

		/**
		 * Using backtracking, generate all possible combinations of robots
		 * that can clean the previously generated permutations of dirts
		 * @param currentSize the current size
		 */
		private void generateRobotsAssigments(int currentSize) {
			// when we reach a number of robots equal to the number of dirts
			// (so for each dirt we assigned a certain robot - robots can repeat
			// and clean none, one or more dirts), we make the robots clean the dirt
			if (currentSize == dirts.size()) {
				robotsClean();
				return;
			}
			int currentIndex = 0;
			while (currentIndex < robots.size()) {
				assignRobots.add(currentSize, currentIndex);
				generateRobotsAssigments(currentSize + 1);
				currentIndex++;
			}
		}

		/**
		 * Calculate the time it takes the previously assigned robots to clean
		 * the assigned dirts; keep a local maximum time and a global minimum time;
		 * we want the minimum maximum time in which all dirts are cleaned
		 */
		private void robotsClean() {
			// initialise array of cleaning time
			ArrayList<Integer> cleaningTimePerRobot = new ArrayList<>(nCopies(4, 0));
			// local maximum
			int maxCleaningTime = -1;
			// create a new arraylist of robots, so that we can change their coordinates
			// without interfering in other cleaning processes
			ArrayList<CellData> copyRobots = new ArrayList<>();
			for (CellData robot : robots) {
				copyRobots.add(new CellData(robot.x, robot.y, robot.orderNumber));
			}
			// for each permuted dirts
			for (int i = 0; i < permuteDirt.size(); i++) {
				// get the current robot and the current dirt
				CellData currentRobot = copyRobots.get(assignRobots.get(i));
				CellData currentDirt = permuteDirt.get(i);
				// get the current cleaning time for the current robot
				int currentCleaningTime = cleaningTimePerRobot.get(currentRobot.orderNumber);
				// add the distance from the dirt to the robot to the current cleaning time
				currentCleaningTime += disanceFromDirts.get(currentRobot.x)
					.get(currentRobot.y).returnDistanceFromCurrentDirt(currentDirt);
				cleaningTimePerRobot.set(currentRobot.orderNumber, currentCleaningTime);
				// change the local maximum cleaning time
				maxCleaningTime = Math.max(currentCleaningTime, maxCleaningTime);
				// change the robot's coordinates to be in the dirt's location
				currentRobot.x = currentDirt.x;
				currentRobot.y = currentDirt.y;
			}
			// change global minimum cleaning time
			minimumCleaningTime = Math.min(maxCleaningTime, minimumCleaningTime);
		}

	}


	public static void main(String[] args) throws IOException {
		new Task().solve();
	}
}
