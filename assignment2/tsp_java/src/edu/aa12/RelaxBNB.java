package edu.aa12;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;

public class RelaxBNB extends BranchAndBound_TSP {
    LpSolve solver;

    public RelaxBNB(Graph g) throws Exception {
        super(g);

        int n = g.getVertices();
        solver = LpSolve.makeLp(0, n*n+n);
        solver.setVerbose(2);

        double[] ds = new double[n*n+n];
        for (Edge e : g.edges) {
            ds[e.u*n + e.v] = g.getLength(e);
            solver.setColName(e.u*n + e.v, e.toString());
        }
        solver.setObjFn(ds);

        for (int i = 0; i < n; i++)
            solver.setColName(n*n + i, "u_" + i);

        for (int i = 0; i < n; i++) {
            double[] assign = new double[n*n+n];
            for (Edge e : g.edges) {
                if (e.u == i) assign[e.u*n + e.v] = 1;
                if (e.v == i) assign[e.u*n + e.v] = 1;
            }

            solver.addConstraint(assign, LpSolve.EQ, 2);
        }

        for (Edge e : g.edges) {
            double[] sube1 = new double[n*n+n];
            sube1[n*n + e.u] = 1;
            sube1[n*n + e.v] = -1;
            sube1[e.u*n + e.v] = n;
            solver.addConstraint(sube1, LpSolve.LE, n-1);
        }

        for (Edge e : g.edges) {
            solver.setBounds(e.u*n + e.v, 0, 1);
        }

        solver.writeLp("relax.lp");
    }

    public double lowerBound(BnBNode node) throws Exception {
        if (node.edgesDefined == this.graph.getVertices()) {
            return objectiveValue(node);
        }

        LpSolve lp = this.solver.copyLp();
        int n = this.graph.getVertices();

        for (BnBNode current = node; current.edge != null; current = current.parent) {
            double x = current.edgeIncluded ? 1 : 0;
            Edge e = current.edge;
            if (e != null) {
                lp.addConstraintex(1, new double[] { 1 }
                                  , new int[] { e.u*n + e.v }
                                  , LpSolve.EQ, x);
            }
        }
        int ret = lp.solve();
        double result = lp.getObjective();

        if (ret == 0)
            return result;
        if (ret == 2)
            return Double.POSITIVE_INFINITY;
        throw new Exception(""+ret);
    }
}