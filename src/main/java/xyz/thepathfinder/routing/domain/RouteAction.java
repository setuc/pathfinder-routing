package xyz.thepathfinder.routing.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;

@PlanningEntity
public interface RouteAction {

    Transport getTransport();

    @InverseRelationShadowVariable(sourceVariableName =  "previousRouteAction")
    CommodityAction getNextCommodityAction();
    void setNextCommodityAction(CommodityAction commodityAction);

    long distanceTo(RouteAction routeAction);

    void setDistance(RouteAction routeAction, long distance);

    int id();
}
