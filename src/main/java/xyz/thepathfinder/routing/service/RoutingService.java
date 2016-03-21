package xyz.thepathfinder.routing.service;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import xyz.thepathfinder.routing.domain.RoutingSolution;

@Path("/")
public class RoutingService {
    Logger logger = LoggerFactory.getLogger(RoutingService.class);

    public static final String SOLVER_CONFIG = "xyz/thepathfinder/routing/solverconfig.xml";

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ProblemSolution solveProblem(ProblemDescription problemDescription) {
        logger.info("Received request to route: " + problemDescription);
        SolverFactory solverFactory = SolverFactory.createFromXmlResource(SOLVER_CONFIG);
        Solver solver = solverFactory.buildSolver();
        RoutingSolution routingSolution = problemDescription.createEmptyRoutingSolution();
        solver.solve(routingSolution);
        return ProblemSolution.create((RoutingSolution) solver.getBestSolution());
    }
}
