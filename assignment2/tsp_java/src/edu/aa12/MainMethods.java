package edu.aa12;

public class MainMethods {

	public static void main(String[] args) throws Exception {
		solveGraph(new Instance1());
		solveGraph(new Instance2());
		solveGraph(new Instance3());
	}
	
	public static void solveGraph(Graph g) throws Exception {
		BranchAndBound_TSP solver = new BranchAndBound_TSP(g);
		long start = System.nanoTime();
		BnBNode n = solver.solve();
		long end = System.nanoTime();
		System.out.printf("Took %.2fms\n",(end-start)/1000000.0);
		// Visualization.visualizeSolution(g, n);//Requires ProGAL (www.diku.dk/~rfonseca/ProGAL)
	}
}
