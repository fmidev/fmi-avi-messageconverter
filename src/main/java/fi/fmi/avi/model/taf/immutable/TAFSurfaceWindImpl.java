package fi.fmi.avi.model.taf.immutable;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.taf.TAFSurfaceWind;

/**
 * Created by rinne on 18/04/2018.
 */
@FreeBuilder
@JsonDeserialize(builder = TAFSurfaceWindImpl.Builder.class)
public abstract class TAFSurfaceWindImpl implements TAFSurfaceWind, Serializable {

    public static TAFSurfaceWindImpl immutableCopyOf(final TAFSurfaceWind surfaceWind) {
        checkNotNull(surfaceWind);
        if (surfaceWind instanceof TAFSurfaceWindImpl) {
            return (TAFSurfaceWindImpl) surfaceWind;
        } else {
            return Builder.from(surfaceWind).build();
        }
    }

    public static Optional<TAFSurfaceWindImpl> immutableCopyOf(final Optional<TAFSurfaceWind> surfaceWind) {
        checkNotNull(surfaceWind);
        return surfaceWind.map(TAFSurfaceWindImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends TAFSurfaceWindImpl_Builder {

        public static Builder from(final TAFSurfaceWind value) {
            return new TAFSurfaceWindImpl.Builder().setMeanWindSpeed(NumericMeasureImpl.immutableCopyOf(value.getMeanWindSpeed()))
                    .setMeanWindDirection(NumericMeasureImpl.immutableCopyOf(value.getMeanWindDirection()))
                    .setWindGust(NumericMeasureImpl.immutableCopyOf(value.getWindGust()))
                    .setVariableDirection(value.isVariableDirection());
        }
    }
}
