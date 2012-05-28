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
            ds[e.v*n + e.u] = g.getLength(e);
        }
        solver.setObjFn(ds);

        for (int i = 0; i < n; i++) {
            double[] out = new double[n*n+n];
            for (Edge e : g.edges) {
                if (e.u == i) out[e.u*n + e.v] = 1;
                if (e.v == i) out[e.v*n + e.u] = 1;
            }

            solver.addConstraint(out, LpSolve.EQ, 1);
        }

        for (int j = 0; j < n; j++) {
            double[] in = new double[n*n+n];
            for (Edge e : g.edges) {
                if (e.v == j) in[e.u*n + e.v] = 1;
                if (e.u == j) in[e.v*n + e.u] = 1;
            }

            solver.addConstraint(in, LpSolve.EQ, 1);
        }

        for (Edge e : g.edges) {
            double[] sube1 = new double[n*n+n];
            sube1[n*n + e.u] = 1;
            sube1[n*n + e.v] = -1;
            sube1[e.u*n + e.v] = n;
            solver.addConstraint(sube1, LpSolve.LE, n-1);

            double[] sube2 = new double[n*n+n];
            sube2[n*n + e.v] = 1;
            sube2[n*n + e.u] = -1;
            sube2[e.v*n + e.u] = n;
            solver.addConstraint(sube2, LpSolve.LE, n-1);
        }

        for (Edge e : g.edges) {
            solver.setBounds(1 + e.u*n + e.v, 0, 1);
            solver.setBounds(1 + e.v*n + e.u, 0, 1);
        }

        solver.writeLp("relax.lp");
    }

    public double lowerBound(BnBNode node) throws Exception {
        if (node.edgesDefined == this.graph.getVertices()) {
            return objectiveValue(node);
        }


        LpSolve lp = this.solver.copyLp();
        int n = this.graph.getVertices();

        solver.setAddRowmode(true);
        for (BnBNode current = node; current.edge != null; current = current.parent) {
            double x = current.edgeIncluded ? 1 : 0;
            Edge e = current.edge;
            if (e != null) {
                lp.addConstraintex(2, new double[] { 1, 1 }
                                  , new int[] { 1 + e.u*n + e.v, 1 + e.v*n + e.u }
                                  , LpSolve.EQ, x);
            }
        }
        solver.setAddRowmode(false);
        int ret = lp.solve();
        double result = lp.getObjective();
        lp.deleteLp();

        if (ret == 0)
            return result;
        if (ret == 2)
            return Double.POSITIVE_INFINITY;
        if (ret == 5)
            return 0;
        throw new Exception(""+ret);
    }
}