package xyz.thepathfinder.routing.domain;

public class CommodityPickup implements CommodityStart, CommodityAction {
    final String name;

    public CommodityPickup(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
