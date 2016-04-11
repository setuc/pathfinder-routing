package xyz.thepathfinder.routing.domain;

public class CommodityDropoff implements CommodityAction {
    final CommodityStart start;
    final String name;

    public CommodityDropoff(String name, CommodityStart start) {
        this.start = start;
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
