package xyz.thepathfinder.routing.score;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;

import java.util.List;
import java.util.stream.Collectors;

import xyz.thepathfinder.routing.domain.CommodityAction;
import xyz.thepathfinder.routing.domain.CommodityDropoff;
import xyz.thepathfinder.routing.domain.RoutingSolution;

public class RoutingScoreCalculator implements EasyScoreCalculator<RoutingSolution> {

    @Override
    public Score calculateScore(RoutingSolution solution) {
        long hardScore = 0;
        long softScore = 0;
        hardScore -= pickupOutOfOrderViolations(solution);
        softScore -= getTotalDistance(solution);
        System.out.println("Score: " + hardScore + ", " + softScore);
        return HardSoftLongScore.valueOf(hardScore, softScore);
    }

    static long pickupOutOfOrderViolations(RoutingSolution solution) {
        return solution.getTransportList().stream().collect(Collectors.summingLong(t -> {
            List<CommodityAction> route = t.getRoute();
            long violations = 0;
            for (int i = 0; i < route.size(); i++) {
                if (route.get(i) instanceof CommodityDropoff) {
                    CommodityDropoff dropoff = (CommodityDropoff) route.get(i);
                    if (!dropoff.getPickup().equals(t)) {
                        int position = route.indexOf(dropoff.getPickup());
                        if (position < 0 || position >= i) {
                            violations++;
                        }
                    }
                }
            }
            return violations;
        }));
    }

    static long getTotalDistance(RoutingSolution solution) {
        return solution.getTransportList().stream().collect(
            Collectors.summingLong(t -> getPathDistance(t.getNextCommodityAction())));
    }

    static long getPathDistance(CommodityAction commodityAction) {
        if (commodityAction == null) {
            return 0;
        } else {
            return commodityAction.distanceTo(commodityAction.getPreviousRouteAction()) +
                getPathDistance(commodityAction.getNextCommodityAction());
        }
    }
}
