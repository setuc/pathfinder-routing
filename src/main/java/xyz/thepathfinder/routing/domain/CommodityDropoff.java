package xyz.thepathfinder.routing.domain;

import java.util.Map;

public class CommodityDropoff extends CommodityAction {
    RouteAction pickup;

    public CommodityDropoff() { }

    public CommodityDropoff(int id, Map<String, Integer> capacities) {
        this.id = id;
        this.capacities = capacities;
    }

    public RouteAction getPickup() {
        return this.pickup;
    }

    public void setPickup(RouteAction pickup) {
        this.pickup = pickup;
    }
}
