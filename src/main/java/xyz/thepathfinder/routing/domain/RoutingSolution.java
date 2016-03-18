package xyz.thepathfinder.routing.domain;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import xyz.thepathfinder.routing.service.ProblemSolution;

@PlanningSolution
public class RoutingSolution implements Solution<HardSoftLongScore> {

    HardSoftLongScore score;
    List<Transport> transportList;
    List<CommodityAction> commodityActionList;

    @Override
    public HardSoftLongScore getScore() {
        return score;
    }

    @Override
    public void setScore(HardSoftLongScore score) {
        this.score = score;
    }

    @Override
    public Collection<? extends RouteAction> getProblemFacts() {
        List<RouteAction> routeActions = new ArrayList<>();
        routeActions.addAll(transportList);
        routeActions.addAll(commodityActionList);
        return routeActions;
    }

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "transportRange")
    public List<Transport> getTransportList() {
        return transportList;
    }

    public void setTransportList(List<Transport> transports) {
        transportList = transports;
    }

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "commodityActionRange")
    public List<CommodityAction> getCommodityActionList() {
        return commodityActionList;
    }

    public ProblemSolution getProblemSolution() {
        return new ProblemSolution(Arrays.asList(Arrays.asList(1, 2, 3)));
    }
}
