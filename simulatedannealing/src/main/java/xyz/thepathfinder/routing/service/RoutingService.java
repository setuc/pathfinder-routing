package xyz.thepathfinder.routing.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import xyz.thepathfinder.routing.domain.VRPSearchState;
import xyz.thepathfinder.simulatedannealing.InfeasibleProblemException;
import xyz.thepathfinder.simulatedannealing.LinearDecayScheduler;
import xyz.thepathfinder.simulatedannealing.Problem;
import xyz.thepathfinder.simulatedannealing.Scheduler;
import xyz.thepathfinder.simulatedannealing.Solver;

@Path("/")
public class RoutingService {
    private static final double INITIAL_TEMPERATURE = 50000;
    private static final int NUMBER_OF_STEPS = 1000000;

    Logger logger = LoggerFactory.getLogger(RoutingService.class);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ProblemSolution solveProblem(ProblemDescription problemDescription)
        throws InfeasibleProblemException {
        logger.info("Received request to route");
        Scheduler scheduler = new LinearDecayScheduler(INITIAL_TEMPERATURE, NUMBER_OF_STEPS);
        Problem<VRPSearchState> problem = problemDescription.createProblem();
        Solver<VRPSearchState> solver = new Solver(problem, scheduler);
        VRPSearchState solution = solver.solve();
        return ProblemSolution.create(solution.getRoutes());
    }
}
