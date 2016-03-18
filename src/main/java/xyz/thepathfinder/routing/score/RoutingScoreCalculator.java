package xyz.thepathfinder.routing.score;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;

import xyz.thepathfinder.routing.domain.RoutingSolution;

public class RoutingScoreCalculator implements EasyScoreCalculator<RoutingSolution> {

    @Override public Score calculateScore(RoutingSolution solution) {
        long hardScore = 0L;
        long softScore = 0L;
        return HardSoftLongScore.valueOf(hardScore, softScore);
    }
}
