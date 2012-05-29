package edu.aa12;

import java.util.Arrays;
import lpsolve.LpSolve;

public class RadiusBNB extends BranchAndBound_TSP {
    LpSolve solver;

    public RadiusBNB(Graph g) throws Exception {
        super(g);

        int n = g.getVertices();
        solver = LpSolve.makeLp(0, n+1);
        solver.setVerbose(2);
        solver.setMaxim();
        solver.setAddRowmode(true);
        
        double[] radii = new double[n+1];
        Arrays.fill(radii, 2);
        solver.setObjFn(radii);

        // need to check the constraint indexing
        /* also, look at moving all this to the lower bound method 
         * rather than the constructor
        */
        double[] row = new double[n+1];
        for (Edge e : g.edges) {
            solver.setColName(e.u+1, "r_" + e.u);
            solver.setColName(e.v+1, "r_" + e.v);
            Arrays.fill(row,0);
            row[e.u+1] = 1;
            row[e.v+1] = 1;
            System.out.println(e);
            solver.addConstraint(row, LpSolve.LE, g.getLength(e));
        }
        
        for (int i = 0; i<n; i++) {
            Arrays.fill(row,0);
            row[i+1] = 1;
            solver.addConstraint(row, LpSolve.GE, 0);
        }

        solver.writeLp("radius.lp");
    }

    public double lowerBound(BnBNode node) throws Exception {
        if (node.edgesDefined == this.graph.getVertices()) {
            return objectiveValue(node);
        }

        LpSolve lp = this.solver.copyLp();
        
        int ret = lp.solve();
        double result = lp.getObjective();

        if (ret == 0)
            return result;

        // If the relaxed LP is infeasible, then so is the original
        //   ILP. We can prune away this partition of the solution space.
        if (ret == 2)
            return Double.POSITIVE_INFINITY;

        // The lp_solve library can detect numerical errors or other
        //   problems. When this happens, we fail.
        throw new Exception("Unknown return code: " + ret);
    }
}