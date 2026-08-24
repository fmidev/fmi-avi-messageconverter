package fi.fmi.avi.model.metar.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.metar.ObservedSurfaceWind;

/**
 * Created by rinne on 13/04/2018.
 */

@Value.Immutable
@JsonDeserialize(builder = ObservedSurfaceWindImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"meanWindDirection", "variableDirection", "meanWindSpeed", "meanWindSpeedOperator", "widnGust",
        "windGustOperator", "extremeClockwiseWindDirection", "extremeCounterClockwiseWindDirection"})
public abstract class ObservedSurfaceWindImpl implements ObservedSurfaceWind, Serializable {

    private static final long serialVersionUID = 5086615958779121088L;

    public static Builder builder() {
        return new Builder();
    }

    public static ObservedSurfaceWindImpl immutableCopyOf(final ObservedSurfaceWind observedSurfaceWind) {
        Objects.requireNonNull(observedSurfaceWind);
        if (observedSurfaceWind instanceof ObservedSurfaceWindImpl) {
            return (ObservedSurfaceWindImpl) observedSurfaceWind;
        } else {
            return Builder.copyOf(observedSurfaceWind).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<ObservedSurfaceWindImpl> immutableCopyOf(final Optional<ObservedSurfaceWind> observedSurfaceWind) {
        Objects.requireNonNull(observedSurfaceWind);
        return observedSurfaceWind.map(ObservedSurfaceWindImpl::immutableCopyOf);
    }

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    @Override
    @JsonDeserialize(contentAs = NumericMeasureImpl.class)
    public abstract Optional<NumericMeasure> getMeanWindDirection();

    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public abstract NumericMeasure getMeanWindSpeed();

    @Override
    @JsonDeserialize(contentAs = NumericMeasureImpl.class)
    public abstract Optional<NumericMeasure> getWindGust();

    @Override
    @JsonDeserialize(contentAs = NumericMeasureImpl.class)
    public abstract Optional<NumericMeasure> getExtremeClockwiseWindDirection();

    @Override
    @JsonDeserialize(contentAs = NumericMeasureImpl.class)
    public abstract Optional<NumericMeasure> getExtremeCounterClockwiseWindDirection();

    public static class Builder extends ImmutableObservedSurfaceWindImpl.Builder {

        Builder() {
            setVariableDirection(false);
        }

        @Override
        public ImmutableObservedSurfaceWindImpl build() {
            final ImmutableObservedSurfaceWindImpl result = super.build();
            if (!result.isVariableDirection() && !result.getMeanWindDirection().isPresent()) {
                throw new IllegalStateException("MeanWindDirection must be present if variableDirection is false");
            }
            return result;
        }

        public static Builder copyOf(final ObservedSurfaceWind value) {
            if (value instanceof ObservedSurfaceWindImpl) {
                return ((ObservedSurfaceWindImpl) value).toBuilder();
            } else {
                return ObservedSurfaceWindImpl.builder()//
                        .setMeanWindDirection(NumericMeasureImpl.immutableCopyOf(value.getMeanWindDirection()))
                        .setMeanWindSpeed(NumericMeasureImpl.immutableCopyOf(value.getMeanWindSpeed()))//
                        .setMeanWindSpeedOperator(value.getMeanWindSpeedOperator())
                        .setVariableDirection(value.isVariableDirection())
                        .setWindGust(NumericMeasureImpl.immutableCopyOf(value.getWindGust()))//
                        .setWindGustOperator(value.getWindGustOperator())
                        .setExtremeClockwiseWindDirection(NumericMeasureImpl.immutableCopyOf(value.getExtremeClockwiseWindDirection()))
                        .setExtremeCounterClockwiseWindDirection(NumericMeasureImpl.immutableCopyOf(value.getExtremeCounterClockwiseWindDirection()));
            }
        }







    }
}
