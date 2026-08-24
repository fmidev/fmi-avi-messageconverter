package fi.fmi.avi.model.taf.immutable;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.CloudForecast;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
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
 * See {@code METARImpl}'s javadoc (in {@code fi.fmi.avi.model.metar.immutable}) for the "detached
 * builder" pattern this class (and {@code Builder}) uses instead of extending an
 * Immutables-generated builder directly.
 */
@Value.Immutable
@Value.Style(init = "set*", get = { "is*", "get*" },
        passAnnotations = { com.fasterxml.jackson.databind.annotation.JsonDeserialize.class, com.fasterxml.jackson.annotation.JsonProperty.class },
        typeInnerBuilder = "InternalImmutableBuilder", builder = "internalBuilder")
@JsonDeserialize(builder = TAFChangeForecastImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "changeIndicator", "periodOfChange", "surfaceWind", "ceilingAndVisibilityOk", "prevailingVisibility", "prevailingVisibilityOperator",
        "forecastWeather", "noSignificantWeather", "cloud" })
public abstract class TAFChangeForecastImpl implements TAFChangeForecast, Serializable {

    private static final long serialVersionUID = -4546515627257056285L;

    public static Builder builder() {
        return new Builder();
    }

    public static TAFChangeForecastImpl immutableCopyOf(final TAFChangeForecast changeForecast) {
        requireNonNull(changeForecast);
        if (changeForecast instanceof TAFChangeForecastImpl) {
            return (TAFChangeForecastImpl) changeForecast;
        } else {
            return Builder.copyOf(changeForecast).build();
        }
    }

    public static Optional<TAFChangeForecastImpl> immutableCopyOf(final Optional<TAFChangeForecast> changeForecast) {
        requireNonNull(changeForecast);
        return changeForecast.map(TAFChangeForecastImpl::immutableCopyOf);
    }

    public Builder toBuilder() {
        return new Builder().mergeFrom(this);
    }

    public static class Builder extends AbstractTAFForecastBuilderImpl<TAFChangeForecastImpl, Builder> {

        private AviationCodeListUser.TAFChangeIndicator changeIndicator;
        private PartialOrCompleteTimePeriod periodOfChange;

        Builder() {
            setCeilingAndVisibilityOk(false);
            setNoSignificantWeather(false);
        }

        public AviationCodeListUser.TAFChangeIndicator getChangeIndicator() {
            return changeIndicator;
        }

        public Builder setChangeIndicator(final AviationCodeListUser.TAFChangeIndicator changeIndicator) {
            this.changeIndicator = requireNonNull(changeIndicator, "changeIndicator");
            return this;
        }

        public PartialOrCompleteTimePeriod getPeriodOfChange() {
            return periodOfChange;
        }

        public Builder setPeriodOfChange(final PartialOrCompleteTimePeriod periodOfChange) {
            this.periodOfChange = requireNonNull(periodOfChange, "periodOfChange");
            return this;
        }

        public static Builder copyOf(final TAFChangeForecast value) {
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
        public Builder mergeFrom(final TAFChangeForecastImpl template) {
            super.mergeFrom(template);
            this.changeIndicator = template.getChangeIndicator();
            this.periodOfChange = template.getPeriodOfChange();
            return this;
        }

        @Override
        public Builder mergeFrom(final Builder template) {
            super.mergeFrom(template);
            this.changeIndicator = template.changeIndicator;
            this.periodOfChange = template.periodOfChange;
            return this;
        }

        @Override
        public ImmutableTAFChangeForecastImpl build() {
            final ImmutableTAFChangeForecastImpl.Builder delegate = ImmutableTAFChangeForecastImpl.internalBuilder()//
                    .setCeilingAndVisibilityOk(isCeilingAndVisibilityOk())//
                    .setNoSignificantWeather(isNoSignificantWeather())//
                    .setChangeIndicator(requireNonNull(changeIndicator, "changeIndicator"))//
                    .setPeriodOfChange(requireNonNull(periodOfChange, "periodOfChange"));
            getPrevailingVisibility().ifPresent(delegate::setPrevailingVisibility);
            getPrevailingVisibilityOperator().ifPresent(delegate::setPrevailingVisibilityOperator);
            getSurfaceWind().ifPresent(delegate::setSurfaceWind);
            getForecastWeather().ifPresent(delegate::setForecastWeather);
            getCloud().ifPresent(delegate::setCloud);
            return delegate.build();
        }

        @Override
        @JsonProperty("prevailingVisibility")
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setPrevailingVisibility(final NumericMeasure prevailingVisibility) {
            return super.setPrevailingVisibility(prevailingVisibility);
        }

        @Override
        @JsonProperty("surfaceWind")
        @JsonDeserialize(as = SurfaceWindImpl.class)
        public Builder setSurfaceWind(final SurfaceWind surfaceWind) {
            return super.setSurfaceWind(surfaceWind);
        }

        @Override
        @JsonProperty("forecastWeather")
        @JsonDeserialize(contentAs = WeatherImpl.class)
        public Builder setForecastWeather(final List<Weather> forecastWeather) {
            return super.setForecastWeather(forecastWeather);
        }

        @Override
        @JsonProperty("cloud")
        @JsonDeserialize(as = CloudForecastImpl.class)
        public Builder setCloud(final CloudForecast cloud) {
            return super.setCloud(cloud);
        }
    }
}
