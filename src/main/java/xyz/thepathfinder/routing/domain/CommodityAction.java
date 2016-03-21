package xyz.thepathfinder.routing.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.AnchorShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;

import java.util.Map;

import xyz.thepathfinder.routing.score.NaiveDifficultyComparator;

@PlanningEntity(difficultyComparatorClass = NaiveDifficultyComparator.class)
public abstract class CommodityAction implements RouteAction {
    Location location;
    Transport transport;
    CommodityAction nextCommodityAction;
    RouteAction previousRouteAction;
    Map<RouteAction, Long> distances;

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

    public long distanceTo(RouteAction routeAction) {
        return distances.get(routeAction);
    }

    public void setDistances(Map<RouteAction, Long> distances) {
        this.distances = distances;
    }
}
