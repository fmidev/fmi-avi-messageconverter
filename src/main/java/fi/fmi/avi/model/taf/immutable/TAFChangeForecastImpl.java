package fi.fmi.avi.model.taf.immutable;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.CloudForecast;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.SurfaceWind;
import fi.fmi.avi.model.Weather;
import fi.fmi.avi.model.immutable.CloudForecastImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.SurfaceWindImpl;
import fi.fmi.avi.model.immutable.WeatherImpl;
import fi.fmi.avi.model.taf.TAFChangeForecast;
import fi.fmi.avi.model.taf.TAFForecast;
import fi.fmi.avi.model.taf.TAFForecastBuilderHelper;

/**
 * Created by rinne on 18/04/2018.
 */
@FreeBuilder
@JsonDeserialize(builder = TAFChangeForecastImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "changeIndicator", "periodOfChange", "surfaceWind", "ceilingAndVisibilityOk", "prevailingVisibility", "prevailingVisibilityOperator",
        "forecastWeather", "noSignificantWeather", "cloud" })
public abstract class TAFChangeForecastImpl implements TAFChangeForecast, Serializable {

    private static final long serialVersionUID = -4546515627257056285L;

    public static TAFChangeForecastImpl immutableCopyOf(final TAFChangeForecast changeForecast) {
        requireNonNull(changeForecast);
        if (changeForecast instanceof TAFChangeForecastImpl) {
            return (TAFChangeForecastImpl) changeForecast;
        } else {
            return Builder.from(changeForecast).build();
        }
    }

    public static Optional<TAFChangeForecastImpl> immutableCopyOf(final Optional<TAFChangeForecast> changeForecast) {
        requireNonNull(changeForecast);
        return changeForecast.map(TAFChangeForecastImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends TAFChangeForecastImpl_Builder implements TAFForecast.Builder<TAFChangeForecastImpl, Builder> {

        public Builder() {
            setCeilingAndVisibilityOk(false);
            setNoSignificantWeather(false);
        }

        public static Builder from(final TAFChangeForecast value) {
            if (value instanceof TAFChangeForecastImpl) {
                return ((TAFChangeForecastImpl) value).toBuilder();
            }
            return new Builder().copyFrom(value);
        }

        @Override
        public Builder copyFrom(final TAFForecast value) {
            if (value instanceof TAFChangeForecastImpl) {
                return clear().mergeFrom((TAFChangeForecastImpl) value);
            }
            TAFForecastBuilderHelper.copyFrom(this, value);
            if (value instanceof TAFChangeForecast) {
                copyTAFChangeForecastSpecificValuesFrom((TAFChangeForecast) value);
            }
            return this;
        }

        @Override
        public Builder mergeFromTAFForecast(final TAFForecast value) {
            TAFForecastBuilderHelper.mergeFromTAFForecast(this, value);
            if (value instanceof TAFChangeForecast) {
                copyTAFChangeForecastSpecificValuesFrom((TAFChangeForecast) value);
            }
            return this;
        }

        private void copyTAFChangeForecastSpecificValuesFrom(final TAFChangeForecast value) {
            setChangeIndicator(value.getChangeIndicator());
            setPeriodOfChange(value.getPeriodOfChange());
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setPrevailingVisibility(final NumericMeasure prevailingVisibility) {
            return super.setPrevailingVisibility(prevailingVisibility);
        }

        @Override
        @JsonDeserialize(as = SurfaceWindImpl.class)
        public Builder setSurfaceWind(final SurfaceWind surfaceWind) {
            return super.setSurfaceWind(surfaceWind);
        }

        @Override
        @JsonDeserialize(contentAs = WeatherImpl.class)
        public Builder setForecastWeather(final List<Weather> forecastWeather) {
            return super.setForecastWeather(forecastWeather);
        }

        @Override
        @JsonDeserialize(as = CloudForecastImpl.class)
        public Builder setCloud(final CloudForecast cloud) {
            return super.setCloud(cloud);
        }
    }
}
