// Profeanu Ioana, 323CA
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import static java.util.Collections.nCopies;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

/**
 * Class for solving the Curse problem
 */
public class Curse {
	static class Task {
		public static final String INPUT_FILE = "curse.in";
		public static final String OUTPUT_FILE = "curse.out";
		// number of tracks, cars and trainings
		int n, m, a;
		// adjacency list where each index of the list represents
		// the neighbor nodes of the ith car out of the total m cars
		ArrayList<ArrayList<Integer>> adjacencyList = new ArrayList<>();
		// list of internal degree of the nodes
		ArrayList<Integer> internalDegrees;
		// the result of the topological sort using Khan algorithm
		ArrayList<Integer> topoSortedCars = new ArrayList<>();

		public void solve() throws FileNotFoundException {
			readInput();
			writeOutput();
		}

		/**
		 * Read the input from the file
		 */
		private void readInput() throws FileNotFoundException {
			Scanner sc = new Scanner(new File(INPUT_FILE));
			// read the number of tracks
			n = sc.nextInt();
			// read the number of cars
			m = sc.nextInt();
			// read the number of trainings
			a = sc.nextInt();
			// initialise list of degrees and of neighbor nodes
			internalDegrees = new ArrayList<>(nCopies(m + 1, 0));
			initialiseAdjacencyList();
			// read the input matrix
			int[][] inputMatrix = new int[a + 1][n + 1];
			for (int i = 1; i <= a; i++) {
				for (int j = 1; j <= n; j++) {
					int x = sc.nextInt();
					inputMatrix[i][j] = x;
				}
			}
			sc.close();
			createGraph(inputMatrix);
		}

		/**
		 * Write the output in the file
		 */
		private void writeOutput() {
			try {
				PrintWriter pw = new PrintWriter(OUTPUT_FILE);
				khanTopoSort();
				// print each car from the topologically sorted arraylist
				for (Integer car : topoSortedCars) {
					pw.printf("%d ", car);
				}
				pw.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		/**
		 * Initialise the adjacency list
		 */
		private void initialiseAdjacencyList() {
			ArrayList<Integer> dummyArraylist = new ArrayList<>();
			adjacencyList.add(dummyArraylist);
			for (int i = 1; i <= m; i++) {
				ArrayList<Integer> newDummyArraylist = new ArrayList<>();
				adjacencyList.add(newDummyArraylist);
			}
		}

		/**
		 * Create the graph by adding the neighbors of each node
		 * in the adjacency list
		 * @param inputMatrix the matrix with the trainings data
		 */
		private void createGraph(int[][] inputMatrix) {
			// for each training session
			for (int i = 1; i < a; i++) {
				// search for the first track where the
				// car from the current training is different from
				// the car from the next training
				boolean foundDifferentCars = false;
				int j;
				for (j = 1; j <= n; j++) {
					// if we found two different cars on the same track but
					// on two different consecutive trainings
					if (inputMatrix[i][j] != inputMatrix[i + 1][j]) {
						foundDifferentCars = true;
						break;
					}
				}
				// if found
				if (foundDifferentCars) {
					adjacencyList.get(inputMatrix[i][j]).add(inputMatrix[i + 1][j]);
					// modify the neighbor's (the car on the lower priority training)
					// internal degree
					int currentDegree = internalDegrees.get(inputMatrix[i + 1][j]);
					internalDegrees.set(inputMatrix[i + 1][j], currentDegree + 1);
				}
			}
		}

		/**
		 * Make the topological sort
		 */
		private void khanTopoSort() {
			// initialise queue
			Queue<Integer> queue = new LinkedList<>();
			// add all the nodes with the internal degree 0 in the queue
			for (int i = 1; i <= m; i++) {
				int carDegree = internalDegrees.get(i);
				if (carDegree == 0) {
					queue.add(i);
				}
			}

			// while the queue is not empty
			while (queue.size() != 0) {
				// get the peek and add it to the result list
				int currentCar = queue.peek();
				queue.remove();
				topoSortedCars.add(currentCar);
				// get each neighbor of the node
				for (Integer neighborCar : adjacencyList.get(currentCar)) {
					int currentDegree = internalDegrees.get(neighborCar);
					// decrease its internal degree
					internalDegrees.set(neighborCar, currentDegree - 1);
					// if the degree is 0, add it to the queue
					if (internalDegrees.get(neighborCar) == 0) {
						queue.add(neighborCar);
					}
				}
			}
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		new Task().solve();
	}
}
