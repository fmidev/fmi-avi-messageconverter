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

    boolean allRunways();

    boolean cleared();

    boolean estimatedSurfaceFrictionUnreliable();

    boolean snowClosure();

    boolean repetition();

    boolean depthNotMeasurable();

    boolean runwayNotOperational();

    Optional<RunwayDirection> runwayDirection();

    Optional<RunwayDeposit> deposit();

    Optional<RunwayContamination> contamination();

    Optional<NumericMeasure> depthOfDeposit();

    Optional<RelationalOperator> depthOperator();

    Optional<Double> estimatedSurfaceFriction();

    Optional<BreakingAction> breakingAction();

    Builder toBuilder();

    class Builder extends RunwayState_Builder {
    }

}
