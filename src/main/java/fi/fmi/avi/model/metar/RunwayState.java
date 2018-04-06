package fi.fmi.avi.model.metar;

import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.RunwayDirection;

@FreeBuilder
@JsonDeserialize(builder = RunwayState.Builder.class)
public interface RunwayState extends AviationCodeListUser {

    boolean isAppliedToAllRunways();

    boolean isCleared();

    boolean isEstimatedSurfaceFrictionUnreliable();

    boolean isSnowClosure();

    boolean isRepetition();

    boolean isDepthNotMeasurable();

    boolean isRunwayNotOperational();

    Optional<RunwayDirection> getRunwayDirection();

    Optional<RunwayDeposit> getDeposit();

    Optional<RunwayContamination> getContamination();

    Optional<NumericMeasure> getDepthOfDeposit();

    Optional<RelationalOperator> getDepthOperator();

    Optional<Double> getEstimatedSurfaceFriction();

    Optional<BreakingAction> getBreakingAction();

    Builder toBuilder();

    class Builder extends RunwayState_Builder {
    }

}
