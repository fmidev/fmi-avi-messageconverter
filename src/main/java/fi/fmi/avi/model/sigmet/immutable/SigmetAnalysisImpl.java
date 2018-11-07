package fi.fmi.avi.model.sigmet.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.sigmet.SigmetAnalysis;

@FreeBuilder
@JsonDeserialize(builder = SigmetAnalysisImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class SigmetAnalysisImpl implements SigmetAnalysis, Serializable {
    public static SigmetAnalysisImpl immutableCopyOf(final SigmetAnalysis sigmetAnalysis) {
        Objects.requireNonNull(sigmetAnalysis);
        if (sigmetAnalysis instanceof SigmetAnalysisImpl) {
            return (SigmetAnalysisImpl) sigmetAnalysis;
        } else {
            return Builder.from(sigmetAnalysis).build();
        }
    }

    public static Optional<SigmetAnalysisImpl> immutableCopyOf(final Optional<SigmetAnalysis> sigmetAnalysis) {
        Objects.requireNonNull(sigmetAnalysis);
        return sigmetAnalysis.map(SigmetAnalysisImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends SigmetAnalysisImpl_Builder {

        public static Builder from(final SigmetAnalysis value) {
            if (value instanceof SigmetAnalysisImpl) {
                return ((SigmetAnalysisImpl) value).toBuilder();
            } else {
                return new Builder().setAnalysisTime(value.getAnalysisTime())
                        .setAnalysisGeometry(value.getAnalysisGeometry())
                        .setAnalysisType(value.getAnalysisType())
                        .setLowerLimit(value.getLowerLimit())
                        .setLowerLimitOperator(value.getLowerLimitOperator())
                        .setUpperLimit(value.getUpperLimit())
                        .setUpperLimitOperator(value.getUpperLimitOperator())
                        .setMovingDirection(value.getMovingDirection())
                        .setMovingSpeed(value.getMovingSpeed())
                        .setIntensityChange(value.getIntensityChange())
                        .setForecastTime(value.getForecastTime())
                        .setForecastGeometry(value.getForecastGeometry())
                        .setForecastApproximateLocation(value.getForecastApproximateLocation())
                        .setAnalysisApproximateLocation(value.getAnalysisApproximateLocation());
            }
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setUpperLimit(final NumericMeasure upperLimit) {
            return super.setUpperLimit(upperLimit);
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setLowerLimit(final NumericMeasure lowerLimit) {
            return super.setLowerLimit(lowerLimit);
        }

        @Override
        public Builder setUpperLimitOperator(final AviationCodeListUser.RelationalOperator operator) {
            return super.setUpperLimitOperator(operator);
        }

        @Override
        public Builder setLowerLimitOperator(final AviationCodeListUser.RelationalOperator operator) {
            return super.setLowerLimitOperator(operator);
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setMovingSpeed(final NumericMeasure movingSpeed) {
            return super.setMovingSpeed(movingSpeed);
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setMovingDirection(final NumericMeasure movingDirection) {
            return super.setMovingDirection(movingDirection);
        }

        @Override
        @JsonDeserialize(as = PartialOrCompleteTimeInstant.class)
        public Builder setAnalysisTime(final PartialOrCompleteTimeInstant analysisTime) {
            return super.setAnalysisTime(analysisTime);
        }

        @Override
        @JsonDeserialize(as = PartialOrCompleteTimeInstant.class)
        public Builder setForecastTime(final PartialOrCompleteTimeInstant forecastTime) {
            return super.setForecastTime(forecastTime);
        }

    }
}
