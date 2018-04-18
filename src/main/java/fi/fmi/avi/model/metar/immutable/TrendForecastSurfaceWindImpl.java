package fi.fmi.avi.model.metar.immutable;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.metar.TrendForecastSurfaceWind;

/**
 * Created by rinne on 13/04/2018.
 */
@FreeBuilder
@JsonDeserialize(builder = TrendForecastSurfaceWindImpl.Builder.class)
public abstract class TrendForecastSurfaceWindImpl implements TrendForecastSurfaceWind, Serializable {

    public static TrendForecastSurfaceWindImpl immutableCopyOf(final TrendForecastSurfaceWind surfaceWind) {
        checkNotNull(surfaceWind);
        if (surfaceWind instanceof TrendForecastSurfaceWindImpl) {
            return (TrendForecastSurfaceWindImpl) surfaceWind;
        } else {
            return Builder.from(surfaceWind).build();
        }
    }

    public static Optional<TrendForecastSurfaceWindImpl> immutableCopyOf(final Optional<TrendForecastSurfaceWind> surfaceWind) {
        checkNotNull(surfaceWind);
        return surfaceWind.map(TrendForecastSurfaceWindImpl::immutableCopyOf);
    }

    abstract Builder toBuilder();

    public static class Builder extends TrendForecastSurfaceWindImpl_Builder {

        public static Builder from(final TrendForecastSurfaceWind value) {
            return new TrendForecastSurfaceWindImpl.Builder().setMeanWindSpeed(NumericMeasureImpl.immutableCopyOf(value.getMeanWindSpeed()))
                    .setMeanWindDirection(NumericMeasureImpl.immutableCopyOf(value.getMeanWindDirection()))
                    .setWindGust(NumericMeasureImpl.immutableCopyOf(value.getWindGust()));

        }
    }

}
