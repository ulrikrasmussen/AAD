package edu.aa12;

import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.Comparator;
import lpsolve.LpSolve;

import edu.aa12.DisjointSet.DSNode;

public class RadiusBNB extends BranchAndBound_TSP {
    LpSolve solver;
    DisjointSet ds = new DisjointSet();

    public RadiusBNB(Graph g) throws Exception {
        super(g);
    }

    // Override edge sort order.  This seems to work well at least for
    //   Instance1, since the smallest edges will be chosen first,
    //   creating many "islands" of components with nodes that are
    //   close to each other.
    protected Comparator<Edge> getSortComparator() {
        return new Comparator<Edge>(){
            public int compare(Edge arg0, Edge arg1) {
                return Double.compare(graph.getLength(arg0),graph.getLength(arg1));
            }};
    }

    private LpSolve buildLP(Set<DSNode> components, DSNode[] nodes
                            , Graph graph, BnBNode node) throws Exception {
        int n = graph.getVertices();
        int varCount = 1 + 2*n;

        solver = LpSolve.makeLp(0, varCount);
        solver.setVerbose(2);
        solver.setMaxim();
        solver.setAddRowmode(true);

        double[] radii = new double[varCount];
        Arrays.fill(radii, 1, 1+n, 2);

        for (int i = 0; i < n; i++) {
            solver.setColName(i+1, "r_" + i);
        }

        // If we only have one edge left to complete a cycle, there
        //   will only be one component in the graph. Therefore, we
        //   cannot add moats, as the problem would then become
        //   unbounded.
        if (node.edgesDefined != graph.getVertices() - 1)
            for (DSNode comp : components) {
                solver.setColName( 1+n+(Integer)comp.object
                                 , "w_" + comp.object);
                radii[1+n+(Integer)comp.object] = 2;
            }

        solver.setObjFn(radii);

        double[] row = new double[varCount];
        for (Edge e : graph.edges) {
            Arrays.fill(row,0);
            // radii sum
            row[e.u+1] = 1;
            row[e.v+1] = 1;

            // If the two nodes reside in different components, add
            //   constraints on the size of their moats.
            DSNode comp_u = ds.find(nodes[e.u]);
            DSNode comp_v = ds.find(nodes[e.v]);
            if (comp_u != comp_v) {
                row[1+n+(Integer)comp_u.object] = 1;
                row[1+n+(Integer)comp_v.object] = 1;
            }
            solver.addConstraint(row, LpSolve.LE, graph.getLength(e));
        }

        solver.setAddRowmode(false);

        return solver;
    }

    public double lowerBound(BnBNode node) throws Exception {
        if (node.edgesDefined == this.graph.getVertices()) {
            return objectiveValue(node);
        }

        // Find connected components arising from the set of chosen edges
        DSNode[] nodes = new DSNode[graph.getVertices()];
        for (int i = 0; i < graph.getVertices(); i++)
            nodes[i] = ds.makeSet(i);

        Graph workingG = this.graph.copy();
        for (BnBNode cur = node; cur.parent != null; cur = cur.parent) {
            if (cur.edgeIncluded) {
                ds.union(nodes[cur.edge.u], nodes[cur.edge.v]);
            } else {
                workingG.edges.remove(cur.edge);
            }
        }

        Set<DSNode> components = new HashSet<DSNode>();
        for (int i = 0; i < graph.getVertices(); i++) {
            components.add(ds.find(nodes[i]));
        }

        LpSolve lp = buildLP(components, nodes, workingG, node);
        try {
            int ret = lp.solve();

            if (ret == 0) {
                double obj = lp.getObjective();
                return obj;
            }

            // Model is unbounded. This happens when the branching
            //   results in a disjoint graph with two or more "islands"
            if (ret == 3)
                return Double.POSITIVE_INFINITY;

            // The lp_solve library can detect numerical errors or other
            //   problems. When this happens, we fail.
            throw new Exception("Unknown return code: " + ret);
        } finally {
            lp.deleteLp();
        }
    }
}