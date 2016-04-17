package xyz.thepathfinder.routing.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transport implements RouteAction {

    CommodityAction nextCommodityAction;
    int id;
    Map<RouteAction, Long> distances = new HashMap<>();
    Map<String, Integer> capacities;

    public Transport() {

    }

    public Transport(int id, Map<String, Integer> capacities) {
        this.id = id;
        this.capacities = capacities == null ? new HashMap<>() : capacities;
    }

    @Override
    public Transport getTransport() {
        return this;
    }

    @Override
    public CommodityAction getNextCommodityAction() {
        return nextCommodityAction;
    }

    @Override
    public void setNextCommodityAction(CommodityAction commodityAction) {
        nextCommodityAction = commodityAction;
    }

    public List<Integer> getPathfinderRoute() {
        CommodityAction action = nextCommodityAction;
        List<Integer> route = new ArrayList<>();
        route.add(id);
        while (action != null) {
            route.add(action.id());
            action = action.getNextCommodityAction();
        }
        return route;
    }

    public List<CommodityAction> getRoute() {
        CommodityAction action = nextCommodityAction;
        List<CommodityAction> route = new ArrayList<>();
        while (action != null) {
            route.add(action);
            action = action.getNextCommodityAction();
        }
        return route;
    }

    @Override
    public long distanceTo(RouteAction routeAction) {
        return distances.get(routeAction);
    }

    @Override
    public void setDistance(RouteAction routeAction, long distance) {
        distances.put(routeAction, distance);
    }

    @Override
    public int id() {
        return id;
    }

    @Override public int getCapacity(String key) {
        return capacities.getOrDefault(key, 0);
    }

    public Map<String, Integer> getCapacities() {
        return capacities;
    }
}
