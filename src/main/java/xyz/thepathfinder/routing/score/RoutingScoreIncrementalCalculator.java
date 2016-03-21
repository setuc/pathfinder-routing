package xyz.thepathfinder.routing.score;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.director.incremental.AbstractIncrementalScoreCalculator;

import xyz.thepathfinder.routing.domain.RoutingSolution;

public class RoutingScoreIncrementalCalculator extends AbstractIncrementalScoreCalculator<RoutingSolution> {
    @Override public void resetWorkingSolution(RoutingSolution workingSolution) {

    }

    @Override public void beforeEntityAdded(Object entity) {

    }

    @Override public void afterEntityAdded(Object entity) {

    }

    @Override public void beforeVariableChanged(Object entity, String variableName) {

    }

    @Override public void afterVariableChanged(Object entity, String variableName) {

    }

    @Override public void beforeEntityRemoved(Object entity) {

    }

    @Override public void afterEntityRemoved(Object entity) {

    }

    @Override public Score calculateScore() {
        return null;
    }
}
