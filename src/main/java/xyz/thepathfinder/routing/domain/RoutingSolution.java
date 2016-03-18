package xyz.thepathfinder.routing.domain;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    public Collection<? extends Object> getProblemFacts() {
        return new ArrayList<>();
    }

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "transportRange")
    public List<Transport> getTransportList() {
        return transportList;
    }

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "commodityActionRange")
    public List<CommodityAction> getCommodityActionList() {
        return commodityActionList;
    }
}
