package xyz.thepathfinder.routing.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.AnchorShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;

import java.util.HashMap;
import java.util.Map;

import xyz.thepathfinder.routing.score.NaiveDifficultyComparator;

@PlanningEntity(difficultyComparatorClass = NaiveDifficultyComparator.class)
public abstract class CommodityAction implements RouteAction {
    Transport transport;
    CommodityAction nextCommodityAction;
    RouteAction previousRouteAction;
    Map<RouteAction, Long> distances = new HashMap<>();
    Map<String, Integer> capacities;
    int id;

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

    @Override
    public long distanceTo(RouteAction routeAction) {
        return distances.getOrDefault(routeAction, Long.MAX_VALUE);
    }

    @Override
    public void setDistance(RouteAction routeAction, long distance) {
        distances.put(routeAction, distance);
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public int getCapacity(String key) {
        return capacities.getOrDefault(key, 0);
    }
}
