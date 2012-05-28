package edu.aa12;

import java.util.Random;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

public class OneTreeBNB extends BranchAndBound_TSP {
  
  public OneTreeBNB(Graph g){
    super(g);
  }

  public double lowerBound(BnBNode node){
  
    if(node.edgesDefined==this.graph.getVertices()) {
      return objectiveValue(node);
    }

    Graph workingG = this.graph.copy();
  
    // Select a random vertex v1 and remove incident edges
    int v1 = new Random().nextInt(workingG.getVertices()-1);
    workingG.edges.removeAll(workingG.incidentEdges[v1]);

    // Find the sum of the length of the edges in the minimum spanning tree of the 
    // new graph.
    double sum = 0;    
    for (Edge e : this.kruskal.minimumSpanningTree(workingG, node)) {
      sum += workingG.getLength(e);
    }

    // Sort the list of lengths
    List<Double> lengths = new ArrayList<Double>();
    for (Edge e : workingG.incidentEdges[v1]) {
      lengths.add(workingG.getLength(e));
    }

    Collections.sort(lengths);
    sum += lengths.get(0);
    sum += lengths.get(1);

    return sum;
  }
}