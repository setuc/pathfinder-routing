package xyz.thepathfinder.routing.domain;

public class CommodityDropoff extends CommodityAction {
    CommodityPickup pickup;

    public CommodityPickup getPickup() {
        return this.pickup;
    }

    public void setPickup(CommodityPickup pickup) {
        this.pickup = pickup;
    }
}
