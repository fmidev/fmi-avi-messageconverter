package fi.fmi.avi.model.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.SurfaceWind;

/**
 * Created by rinne on 18/04/2018.
 */
@FreeBuilder
@JsonDeserialize(builder = SurfaceWindImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"meanWindDirection", "variableDirection", "meanWindSpeed", "meanWindSpeedOperator",
        "windGust", "windGustOperator"})
public abstract class SurfaceWindImpl implements SurfaceWind, Serializable {

    private static final long serialVersionUID = -1854059197765450606L;

    public static SurfaceWindImpl immutableCopyOf(final SurfaceWind surfaceWind) {
        Objects.requireNonNull(surfaceWind);
        if (surfaceWind instanceof SurfaceWindImpl) {
            return (SurfaceWindImpl) surfaceWind;
        } else {
            return Builder.from(surfaceWind).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<SurfaceWindImpl> immutableCopyOf(final Optional<SurfaceWind> surfaceWind) {
        Objects.requireNonNull(surfaceWind);
        return surfaceWind.map(SurfaceWindImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    @SuppressWarnings("EmptyMethod")
    public static class Builder extends SurfaceWindImpl_Builder {

        public static Builder from(final SurfaceWind value) {
            if (value instanceof SurfaceWindImpl) {
                return ((SurfaceWindImpl) value).toBuilder();
            } else {
                return new SurfaceWindImpl.Builder()//
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
        public SurfaceWindImpl build() {
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
