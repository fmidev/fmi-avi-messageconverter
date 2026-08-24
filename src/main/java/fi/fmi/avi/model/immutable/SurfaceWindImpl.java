package fi.fmi.avi.model.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.SurfaceWind;

/**
 * Created by rinne on 18/04/2018.
 */
@Value.Immutable
@JsonDeserialize(builder = SurfaceWindImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"meanWindDirection", "variableDirection", "meanWindSpeed", "meanWindSpeedOperator",
        "windGust", "windGustOperator"})
public abstract class SurfaceWindImpl implements SurfaceWind, Serializable {

    private static final long serialVersionUID = -1854059197765450606L;

    public static Builder builder() {
        return new Builder();
    }

    public static SurfaceWindImpl immutableCopyOf(final SurfaceWind surfaceWind) {
        Objects.requireNonNull(surfaceWind);
        if (surfaceWind instanceof SurfaceWindImpl) {
            return (SurfaceWindImpl) surfaceWind;
        } else {
            return Builder.copyOf(surfaceWind).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<SurfaceWindImpl> immutableCopyOf(final Optional<SurfaceWind> surfaceWind) {
        Objects.requireNonNull(surfaceWind);
        return surfaceWind.map(SurfaceWindImpl::immutableCopyOf);
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

    @SuppressWarnings("EmptyMethod")
    public static class Builder extends ImmutableSurfaceWindImpl.Builder {

        Builder() {
            setVariableDirection(false);
        }

        public static Builder copyOf(final SurfaceWind value) {
            if (value instanceof SurfaceWindImpl) {
                return ((SurfaceWindImpl) value).toBuilder();
            } else {
                return SurfaceWindImpl.builder()//
                        .setMeanWindSpeed(NumericMeasureImpl.immutableCopyOf(value.getMeanWindSpeed()))
                        .setMeanWindSpeedOperator(value.getMeanWindSpeedOperator())
                        .setMeanWindDirection(NumericMeasureImpl.immutableCopyOf(value.getMeanWindDirection()))
                        .setWindGust(NumericMeasureImpl.immutableCopyOf(value.getWindGust()))
                        .setWindGustOperator(value.getWindGustOperator())
                        .setVariableDirection(value.isVariableDirection());
            }
        }

        @Override
        public ImmutableSurfaceWindImpl build() {
            final ImmutableSurfaceWindImpl result = super.build();
            if (!result.isVariableDirection() && !result.getMeanWindDirection().isPresent()) {
                throw new IllegalStateException("MeanWindDirection must be present if variableDirection is false");
            }
            return result;
        }
    }
}
