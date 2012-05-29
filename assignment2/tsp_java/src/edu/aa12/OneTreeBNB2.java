package edu.aa12;

import java.util.Random;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Comparator;

import edu.aa12.DisjointSet.DSNode;

public class OneTreeBNB2 extends BranchAndBound_TSP {
    public OneTreeBNB2(Graph g){
        super(g);
    }

    final DisjointSet ds = new DisjointSet();

    public double lowerBound(BnBNode node) throws Exception {
        if (node.edgesDefined == this.graph.getVertices()) {
            return objectiveValue(node);
        }

        // Initialize disjoint sets of nodes
        DSNode[] nodes = new DSNode[this.graph.getVertices()];
        for (Edge e: graph.edges) {
            nodes[e.u] = ds.makeSet(e.u);
            nodes[e.v] = ds.makeSet(e.v);
        }

        // Find connected components and filter out edges that are not
        //   included in this branch
        List<Edge> incidentEdges = new LinkedList<Edge>(graph.edges);
        for (BnBNode cur = node; cur.parent != null; cur = cur.parent) {
            if (cur.edgeIncluded)
                ds.union(nodes[cur.edge.u], nodes[cur.edge.v]);
            else
                incidentEdges.remove(cur.edge);
        }

        // Select a random vertex v1
        DSNode v1 = ds.find(nodes[new Random().nextInt(graph.getVertices())]);

        // Filter out all edges that are not incident to the connected
        //   component the vertex is in
        for (Edge e : graph.edges) {
            boolean u_in_c = ds.find(nodes[e.u]) == v1;
            boolean v_in_c = ds.find(nodes[e.v]) == v1;
            if (!u_in_c && !v_in_c   // edge is outside component
                || u_in_c && v_in_c) // edge is inside component
                incidentEdges.remove(e);
        }

        // Make a copy of the graph, and remove all edges incident to
        //   the selected component
        Graph workingG = this.graph.copy();
        workingG.edges.removeAll(incidentEdges);

        // Find the sum of the length of the edges in the minimum
        //   spanning tree of the new graph.
        double sum = 0;
        for (Edge e : this.kruskal.minimumSpanningTree(workingG,node)) {
            sum += workingG.getLength(e);
        }

        // Sort incident edges in ascending order
        incidentEdges = new ArrayList<Edge>(incidentEdges);
        Collections.sort(incidentEdges, new Comparator<Edge>() {
                public int compare(Edge o1, Edge o2) {
                    return Double.compare(graph.getDistance(o1.u, o1.v)
                                          ,graph.getDistance(o2.u, o2.v));
                }});

        // When |V|-1 edges have been defined, there are no edges
        //   incident to the selected component. The MST is also empty.
        //   In all other cases, the branch algorithm will ensure that
        //   there are at least two edges incident to the component.
        if (node.edgesDefined != graph.getVertices() - 1) {
            sum += graph.getLength(incidentEdges.get(0));
            sum += graph.getLength(incidentEdges.get(1));
        } else {
            // assert that the above comment is true
            assert (incidentEdges.size() == 0)
                : "incident edge count is non-zero : " + incidentEdges.size();
        }

        return objectiveValue(node) + sum;
    }
}