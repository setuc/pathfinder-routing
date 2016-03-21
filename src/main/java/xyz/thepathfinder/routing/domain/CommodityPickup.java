package xyz.thepathfinder.routing.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;

@PlanningEntity
public class CommodityPickup extends CommodityAction {
    CommodityDropoff dropoff;

    public CommodityPickup() { }

    public CommodityPickup(int id) {
        this.id = id;
    }

    public CommodityDropoff getDropoff() {
        return dropoff;
    }

    public void setDropoff(CommodityDropoff dropoff) {
        this.dropoff = dropoff;
    }
}
