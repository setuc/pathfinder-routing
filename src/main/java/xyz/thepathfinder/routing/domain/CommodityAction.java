package xyz.thepathfinder.routing.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.AnchorShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;

@PlanningEntity
public abstract class CommodityAction implements RouteAction {
    Location location;
    Transport transport;
    CommodityAction nextCommodityAction;
    RouteAction previousRouteAction;

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    @AnchorShadowVariable(sourceVariableName = "previousRouteAction")
    public Transport getTransport() {
        return transport;
    }

    public void setTransport(Transport transport) {
        this.transport = transport;
    }

    @Override
    public CommodityAction getNextCommodityAction() {
        return nextCommodityAction;
    }

    @Override
    public void setNextCommodityAction(CommodityAction commodityAction) {
        nextCommodityAction = commodityAction;
    }

    @PlanningVariable(valueRangeProviderRefs = {"transportRange", "commodityActionRange"},
        graphType = PlanningVariableGraphType.CHAINED)
    public RouteAction getPreviousRouteAction() {
        return previousRouteAction;
    }

    public void setPreviousRouteAction(RouteAction routeAction) {
        previousRouteAction = routeAction;
    }
}
