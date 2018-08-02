package fi.fmi.avi.model.taf.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.taf.TAFSurfaceWind;

/**
 * Created by rinne on 18/04/2018.
 */
@FreeBuilder
@JsonDeserialize(builder = TAFSurfaceWindImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"meanWindDirection", "variableDirection", "meanWindSpeed", "meanWindSpeedOperator",
        "windGust", "windGustOperator"})
public abstract class TAFSurfaceWindImpl implements TAFSurfaceWind, Serializable {

    public static TAFSurfaceWindImpl immutableCopyOf(final TAFSurfaceWind surfaceWind) {
        Objects.requireNonNull(surfaceWind);
        if (surfaceWind instanceof TAFSurfaceWindImpl) {
            return (TAFSurfaceWindImpl) surfaceWind;
        } else {
            return Builder.from(surfaceWind).build();
        }
    }

    public static Optional<TAFSurfaceWindImpl> immutableCopyOf(final Optional<TAFSurfaceWind> surfaceWind) {
        Objects.requireNonNull(surfaceWind);
        return surfaceWind.map(TAFSurfaceWindImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends TAFSurfaceWindImpl_Builder {

        public static Builder from(final TAFSurfaceWind value) {
            if (value instanceof TAFSurfaceWindImpl) {
                return ((TAFSurfaceWindImpl) value).toBuilder();
            } else {
                return new TAFSurfaceWindImpl.Builder()//
                        .setMeanWindSpeed(NumericMeasureImpl.immutableCopyOf(value.getMeanWindSpeed()))
                        .setMeanWindSpeedOperator(value.getMeanWindSpeedOperator())
                        .setMeanWindDirection(NumericMeasureImpl.immutableCopyOf(value.getMeanWindDirection()))
                        .setWindGust(NumericMeasureImpl.immutableCopyOf(value.getWindGust()))
                        .setWindGustOperator(value.getWindGustOperator())
                        .setVariableDirection(value.isVariableDirection());
            }
        }

        public Builder() {
            setVariableDirection(false);
        }

        @Override
        public TAFSurfaceWindImpl build() {
            if (!isVariableDirection() && !getMeanWindDirection().isPresent()) {
                throw new IllegalStateException("MeanWindDirection must be present if variableDirection is false");
            }
            return super.build();
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setMeanWindDirection(final NumericMeasure meanWindDirection) {
            return super.setMeanWindDirection(meanWindDirection);
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setMeanWindSpeed(final NumericMeasure meanWindSpeed) {
            return super.setMeanWindSpeed(meanWindSpeed);
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setWindGust(final NumericMeasure windGust) {
            return super.setWindGust(windGust);
        }
    }
}
