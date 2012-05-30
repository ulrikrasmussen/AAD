package edu.aa12;

/**
 * Implementation of the 1-tree lower-bound algorithm.
 */
public class OneTree2 {

  public static void main(String[] args) throws Exception {
    solveGraph(new Instance1());
    solveGraph(new Instance2());
    solveGraph(new Instance3());
  }

    public static void solveGraph(Graph g) throws Exception {
      long start = System.nanoTime();
      BnBNode n = new OneTreeBNB2(g).solve();
      long end = System.nanoTime();
      System.out.printf("Took %.2fms\n",(end-start)/1000000.0);
      Visualization.visualizeSolution(g, n);
    }
}