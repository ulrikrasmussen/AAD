package edu.aa12;

/**
 * Implementation of the 1-tree lower-bound algorithm.
 */
public class OneTree {

  public static void main(String[] args) {
    
    Graph g = new Instance1();

    BnBNode n = new OneTreeBNB(g).solve();

    System.out.println("Yo dawg");
    System.out.println(n);

  }
}

