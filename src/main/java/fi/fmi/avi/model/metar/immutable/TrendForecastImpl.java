package fi.fmi.avi.model.metar.immutable;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.Serializable;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.immutable.CloudForecastImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.metar.TrendForecast;

/**
 * Created by rinne on 13/04/2018.
 */
@FreeBuilder
@JsonDeserialize(builder = TrendForecastImpl.Builder.class)
public abstract class TrendForecastImpl implements TrendForecast, Serializable {

    public static TrendForecastImpl immutableCopyOf(final TrendForecast trendForecast) {
        checkNotNull(trendForecast);
        if (trendForecast instanceof TrendForecastImpl) {
            return (TrendForecastImpl) trendForecast;
        } else {
            return Builder.from(trendForecast).build();
        }
    }

    public static Optional<TrendForecastImpl> immutableCopyOf(final Optional<TrendForecast> trendForecast) {
        checkNotNull(trendForecast);
        return trendForecast.map(TrendForecastImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends TrendForecastImpl_Builder {

        public static Builder from(final TrendForecast value) {
            return new TrendForecastImpl.Builder().setPeriodOfChange(value.getPeriodOfChange()).setInstantOfChange(value.getInstantOfChange())
                    .setCeilingAndVisibilityOk(value.isCeilingAndVisibilityOk())
                    .setChangeIndicator(value.getChangeIndicator())
                    .setPrevailingVisibilityOperator(value.getPrevailingVisibilityOperator())
                    .setNoSignificantWeather(value.isNoSignificantWeather())
                    .setPrevailingVisibility(NumericMeasureImpl.immutableCopyOf(value.getPrevailingVisibility()))
                    .setSurfaceWind(TrendForecastSurfaceWindImpl.immutableCopyOf(value.getSurfaceWind()))
                    .setCloud(CloudForecastImpl.immutableCopyOf(value.getCloud()));
        }

        @Override
        public TrendForecastImpl build() {
            checkState(!(getPeriodOfChange().isPresent() && getInstantOfChange().isPresent()), "Both the period and the instant of change cannot be set");
            return super.build();
        }

    }
}
