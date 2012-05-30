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

        // Find sizes of components
        int[] compSizes = new int[this.graph.getVertices()];
        for (int i = 0; i < graph.getVertices(); i++) {
            compSizes[(int)ds.find(nodes[i]).object]++;
        }

        // Find largest component
        int maxCompIndex = 0;
        for (int i = 0; i < graph.getVertices(); i++) {
            if (compSizes[i] > compSizes[maxCompIndex])
                maxCompIndex = i;
        }

        // Select vertex from largest component
        DSNode v1 = ds.find(nodes[maxCompIndex]);

        // Filter out all edges that are not incident to the connected
        //   component the vertex is in
        for (Edge e : graph.edges) {
            boolean u_in_c = ds.find(nodes[e.u]) == v1;
            boolean v_in_c = ds.find(nodes[e.v]) == v1;
            if (!u_in_c && !v_in_c   // edge is outside component
                || u_in_c && v_in_c) // edge is inside component
                incidentEdges.remove(e);
        }

        // If the selected component has less than two incident edges,
        //   and we have not yet selected |V|-1 edges, then there is
        //   no possible way to construct a valid TSP solution (since
        //   we need at least two edges to form a cycle, one for going
        //   into the component, and one for leaving it again)
        if (incidentEdges.size() < 2
              && node.edgesDefined != graph.getVertices() - 1) {
            // mark solution space as infeasible
            return Double.POSITIVE_INFINITY;
        }

        // Make a copy of the graph, and remove all edges incident to
        //   the selected component
        Graph workingG = this.graph.copy();
        workingG.edges.removeAll(incidentEdges);

        // Find the sum of the length of the edges in the minimum
        //   spanning tree of the new graph.
        double sum = 0;
        List<Edge> mstEdges = this.kruskal.minimumSpanningTree(workingG,node);
        for (Edge e : mstEdges) {
            sum += workingG.getLength(e);
            ds.union(nodes[e.u], nodes[e.v]);
        }

        // Sort incident edges in ascending order
        incidentEdges = new ArrayList<Edge>(incidentEdges);
        Collections.sort(incidentEdges, new Comparator<Edge>() {
                public int compare(Edge o1, Edge o2) {
                    return Double.compare(graph.getDistance(o1.u, o1.v)
                                          ,graph.getDistance(o2.u, o2.v));
                }});

        if (incidentEdges.size() > 0) {
            Edge e = incidentEdges.get(0);
            sum += graph.getLength(e);
            ds.union(nodes[e.u], nodes[e.v]);
        }
        if (incidentEdges.size() > 1) {
            Edge e = incidentEdges.get(1);
            sum += graph.getLength(e);
            ds.union(nodes[e.u], nodes[e.v]);
        }

        // Check that adding two incident edges of the selected
        //   component to the MST forms a connected component of all
        //   nodes. If not, then surely there is no way to ever form a
        //   valid TSP solution.
        DSNode rep = ds.find(nodes[0]); // representative node for connected component
        int counter = 0;
        for (int i = 1; i < graph.getVertices(); i++) {
            if (ds.find(nodes[i]) != rep) {
                return Double.POSITIVE_INFINITY;
            }
        }

        return objectiveValue(node) + sum;
    }
}