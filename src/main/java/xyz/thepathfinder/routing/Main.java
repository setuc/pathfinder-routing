package xyz.thepathfinder.routing;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;

public class Main {

    public static final String SOLVER_CONFIG = "xyz/thepathfinder/routing/solverconfig.xml";

    public static void main(String args[]) {
        SolverFactory solverFactory = SolverFactory.createFromXmlResource(SOLVER_CONFIG);
        Solver solver = solverFactory.buildSolver();
    }
}
