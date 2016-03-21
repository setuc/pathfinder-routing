package xyz.thepathfinder.routing.domain;

import java.util.ArrayList;
import java.util.List;

public class Transport implements RouteAction {

    Location startLocation;
    CommodityAction nextCommodityAction;

    @Override
    public Location getLocation() {
        return startLocation;
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
}
