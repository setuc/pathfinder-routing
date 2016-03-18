package xyz.thepathfinder.routing.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;

public class Transport implements RouteAction {

    Location startLocation;
    CommodityAction nextCommodityAction;

    @Override public Location getLocation() {
        return startLocation;
    }

    @Override public Transport getTransport() {
        return this;
    }

    @Override public CommodityAction getNextCommodityAction() {
        return nextCommodityAction;
    }

    @Override public void setNextCommodityAction(CommodityAction commodityAction) {
        nextCommodityAction = commodityAction;
    }
}
