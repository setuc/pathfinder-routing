package xyz.thepathfinder.routing.service;

import java.util.List;

import xyz.thepathfinder.routing.domain.RoutingSolution;
import xyz.thepathfinder.routing.domain.Transport;

import static java.util.stream.Collectors.toList;

public class ProblemSolution {
    private List<List<Integer>> routes;

    public ProblemSolution() { }

    public static ProblemSolution create(RoutingSolution solution) {
        return new ProblemSolution(solution.getTransportList().stream()
                .map(Transport::getPathfinderRoute).collect(toList()));
    }

    public ProblemSolution(List<List<Integer>> routes) {
        this.routes = routes;
    }

    public void setRoutes(List<List<Integer>> routes) {
        this.routes = routes;
    }

    public List<List<Integer>> getRoutes() {
        return routes;
    }
}
