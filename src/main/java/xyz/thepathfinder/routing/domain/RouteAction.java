package xyz.thepathfinder.routing.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;

@PlanningEntity
public interface RouteAction {

    Location getLocation();
    Transport getTransport();

    @InverseRelationShadowVariable(sourceVariableName =  "previousRouteAction")
    CommodityAction getNextCommodityAction();
    void setNextCommodityAction(CommodityAction commodityAction);
}
