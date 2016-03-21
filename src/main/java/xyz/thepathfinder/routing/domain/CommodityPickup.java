package xyz.thepathfinder.routing.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;

import java.util.Map;

@PlanningEntity
public class CommodityPickup extends CommodityAction {
    CommodityDropoff dropoff;

    public CommodityPickup() { }

    public CommodityPickup(int id, Map<String, Integer> capacities) {
        this.id = id;
        this.capacities = capacities;
    }

    public CommodityDropoff getDropoff() {
        return dropoff;
    }

    public void setDropoff(CommodityDropoff dropoff) {
        this.dropoff = dropoff;
    }
}
