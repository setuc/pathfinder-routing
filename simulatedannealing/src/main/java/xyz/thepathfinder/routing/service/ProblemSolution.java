package xyz.thepathfinder.routing.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import xyz.thepathfinder.routing.domain.CommodityAction;
import xyz.thepathfinder.routing.domain.Transport;

public class ProblemSolution {
    private List<List<Integer>> routes;

    public ProblemSolution() { }

    public static ProblemSolution create(Map<Transport, List<CommodityAction>> routes) {
        List<List<Integer>> integerRoutes = new ArrayList<>();
        for (Map.Entry<Transport, List<CommodityAction>> routeEntry : routes.entrySet()) {
            List<Integer> singleRoute = new ArrayList<>();
            singleRoute.add(Integer.parseInt(routeEntry.getKey().getName()));
            for (CommodityAction action : routeEntry.getValue()) {
                singleRoute.add(Integer.parseInt(action.getName()));
            }
            integerRoutes.add(singleRoute);
        }
        return new ProblemSolution(integerRoutes);
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
