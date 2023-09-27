package fi.fmi.avi.model.metar.immutable;


import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.RunwayDirection;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.RunwayDirectionImpl;
import fi.fmi.avi.model.metar.RunwayState;

/**
 * Created by rinne on 13/04/2018.
 */
@FreeBuilder
@JsonDeserialize(builder = RunwayStateImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "runwayDirection", "appliedToAllRunways", "deposit", "contamination", "depthOfDeposit", "depthOperator", "depthNotMeasurable",
        "depthInsignificant", "estimatedSurfaceFriction", "estimatedSurfaceFrictionUnreliable", "brakingAction", "repetition", "runwayNotOperational",
        "cleared" })
public abstract class RunwayStateImpl implements RunwayState, Serializable {

    private static final long serialVersionUID = 5824760002670364242L;

    public static Builder builder() {
        return new Builder();
    }

    public static RunwayStateImpl immutableCopyOf(final RunwayState runwayState) {
        Objects.requireNonNull(runwayState);
        if (runwayState instanceof RunwayStateImpl) {
            return (RunwayStateImpl) runwayState;
        } else {
            return Builder.from(runwayState).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<RunwayStateImpl> immutableCopyOf(final Optional<RunwayState> runwayState) {
        Objects.requireNonNull(runwayState);
        return runwayState.map(RunwayStateImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends RunwayStateImpl_Builder {

        Builder() {
            setAppliedToAllRunways(false);
            setEstimatedSurfaceFrictionUnreliable(false);
            setRepetition(false);
            setDepthNotMeasurable(false);
            setDepthInsignificant(false);
            setRunwayNotOperational(false);
            setCleared(false);
        }

        public static Builder from(final RunwayState value) {
            if (value instanceof RunwayStateImpl) {
                return ((RunwayStateImpl) value).toBuilder();
            } else {
                return RunwayStateImpl.builder().setAppliedToAllRunways(value.isAppliedToAllRunways())
                        .setBrakingAction(value.getBrakingAction())
                        .setCleared(value.isCleared())
                        .setContamination(value.getContamination())
                        .setDeposit(value.getDeposit())
                        .setDepthNotMeasurable(value.isDepthNotMeasurable())
                        .setDepthInsignificant(value.isDepthInsignificant())
                        .setDepthOfDeposit(NumericMeasureImpl.immutableCopyOf(value.getDepthOfDeposit()))
                        .setDepthOperator(value.getDepthOperator())
                        .setEstimatedSurfaceFriction(value.getEstimatedSurfaceFriction())
                        .setRepetition(value.isRepetition());
            }
        }
        @Override
        @JsonDeserialize(as = RunwayDirectionImpl.class)
        public Builder setRunwayDirection(final RunwayDirection runwayDirection) {
            return super.setRunwayDirection(runwayDirection);
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setDepthOfDeposit(final NumericMeasure depthOfDeposit) {
            return super.setDepthOfDeposit(depthOfDeposit);
        }
    }

}
