package edu.aa12;

/**
 * Implementation of the 1-tree lower-bound algorithm.
 */
public class OneTree {

  public static void main(String[] args) {
    Graph g = new Instance2();

    long start = System.nanoTime();
    BnBNode n = new OneTreeBNB(g).solve();
    long end = System.nanoTime();
    System.out.printf("Took %.2fms\n",(end-start)/1000000.0);
    // Visualization.visualizeSolution(g, n);
  }
}

