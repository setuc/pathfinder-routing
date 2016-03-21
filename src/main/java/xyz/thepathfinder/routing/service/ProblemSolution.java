package xyz.thepathfinder.routing.service;

import xyz.thepathfinder.routing.domain.RouteAction;
import xyz.thepathfinder.routing.domain.RoutingSolution;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class ProblemSolution {
    private List<List<Integer>> routes;

    public ProblemSolution() { }

    public static ProblemSolution create(RoutingSolution solution) {
        return new ProblemSolution(solution.getTransportList().stream()
                .map(t -> t.getRoute().stream().map(RouteAction::id).collect(toList())).collect(toList()));
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
