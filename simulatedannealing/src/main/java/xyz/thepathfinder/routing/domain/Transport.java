package xyz.thepathfinder.routing.domain;

public class Transport implements CommodityStart {
    final String name;

    public Transport(String name) {
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
