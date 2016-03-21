package xyz.thepathfinder.routing.domain;

public class CommodityDropoff extends CommodityAction {
    RouteAction pickup;

    public RouteAction getPickup() {
        return this.pickup;
    }

    public void setPickup(RouteAction pickup) {
        this.pickup = pickup;
    }
}
