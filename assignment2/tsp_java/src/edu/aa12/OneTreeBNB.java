package edu.aa12;

import java.util.Random;
import java.util.List;

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

    double e1 = Double.MAX_VALUE;
    double e2 = Double.MAX_VALUE;
    for (Edge e : workingG.incidentEdges[v1]) {
      double length = workingG.getLength(e);
      if (length < e1) {
        e1 = length;
      } else if (length < e2) {
        e2 = length;
      }
    }

    sum += e1;
    sum += e2;

    return sum;
  }
}