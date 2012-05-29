package edu.aa12;

/**
 * Implementation of the 1-tree lower-bound algorithm.
 */
public class OneTree2 {

  public static void main(String[] args) throws Exception {
    Graph g = new Instance3();

    long start = System.nanoTime();
    BnBNode n = new OneTreeBNB2(g).solve();
    long end = System.nanoTime();
    System.out.printf("Took %.2fms\n",(end-start)/1000000.0);
    Visualization.visualizeSolution(g, n);
  }
}