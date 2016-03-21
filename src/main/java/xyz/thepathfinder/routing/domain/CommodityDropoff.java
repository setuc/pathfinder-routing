package xyz.thepathfinder.routing.domain;

public class CommodityDropoff extends CommodityAction {
    RouteAction pickup;

    public CommodityDropoff() { }

    public CommodityDropoff(int id) {
        this.id = id;
    }

    public RouteAction getPickup() {
        return this.pickup;
    }

    public void setPickup(RouteAction pickup) {
        this.pickup = pickup;
    }
}
