package xyz.thepathfinder.routing.service;

import java.util.List;

public class ProblemSolution {
    private List<List<Integer>> routes;

    public ProblemSolution() { }

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
