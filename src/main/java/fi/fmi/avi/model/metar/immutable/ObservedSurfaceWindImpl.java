package fi.fmi.avi.model.metar.immutable;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.metar.ObservedSurfaceWind;

/**
 * Created by rinne on 13/04/2018.
 */

@FreeBuilder
@JsonDeserialize(builder = ObservedSurfaceWindImpl.Builder.class)
public abstract class ObservedSurfaceWindImpl implements ObservedSurfaceWind, Serializable {

    public static ObservedSurfaceWindImpl immutableCopyOf(final ObservedSurfaceWind observedSurfaceWind) {
        checkNotNull(observedSurfaceWind);
        if (observedSurfaceWind instanceof ObservedSurfaceWindImpl) {
            return (ObservedSurfaceWindImpl) observedSurfaceWind;
        } else {
            return Builder.from(observedSurfaceWind).build();
        }
    }

    public static Optional<ObservedSurfaceWindImpl> immutableCopyOf(final Optional<ObservedSurfaceWind> observedSurfaceWind) {
        checkNotNull(observedSurfaceWind);
        return observedSurfaceWind.map(ObservedSurfaceWindImpl::immutableCopyOf);
    }

    abstract Builder toBuilder();

    public static class Builder extends ObservedSurfaceWindImpl_Builder {

        public static Builder from(final ObservedSurfaceWind value) {
            return new ObservedSurfaceWindImpl.Builder().setMeanWindDirection(NumericMeasureImpl.immutableCopyOf(value.getMeanWindDirection()))
                    .setMeanWindSpeed(NumericMeasureImpl.immutableCopyOf(value.getMeanWindSpeed()))
                    .setVariableDirection(value.isVariableDirection())
                    .setWindGust(NumericMeasureImpl.immutableCopyOf(value.getWindGust()))
                    .setExtremeClockwiseWindDirection(NumericMeasureImpl.immutableCopyOf(value.getExtremeClockwiseWindDirection()))
                    .setExtremeCounterClockwiseWindDirection(NumericMeasureImpl.immutableCopyOf(value.getExtremeCounterClockwiseWindDirection()));
        }
    }
}
