package xyz.thepathfinder.routing.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class RoutingService {
    Logger logger = LoggerFactory.getLogger(RoutingService.class);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ProblemSolution solveProblem(ProblemDescription problemDescription) {
        logger.info("Received request to route: " + problemDescription);
        return new ProblemSolution(Arrays.asList(Arrays.asList(1, 2, 3)));
    }
}
