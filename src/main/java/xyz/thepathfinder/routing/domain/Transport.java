package xyz.thepathfinder.routing.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transport implements RouteAction {

    CommodityAction nextCommodityAction;
    final int id;
    Map<RouteAction, Long> distances = new HashMap<>();

    public Transport(int id) {
        this.id = id;
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
}
