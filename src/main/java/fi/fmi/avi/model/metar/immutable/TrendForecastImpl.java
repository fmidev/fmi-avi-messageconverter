package fi.fmi.avi.model.metar.immutable;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.immutables.value.Value;

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
import fi.fmi.avi.model.metar.TrendForecast;

/**
 * Created by rinne on 13/04/2018.
 */
@Value.Immutable
@JsonDeserialize(builder = TrendForecastImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"changeIndicator", "periodOfChange", "instantOfChange", "surfaceWind", "ceilingAndVisibilityOk",
        "prevailingVisibility", "prevailingVisibilityOperator", "noSignificantWeather", "forecastWeather",
        "cloud", "colorState"})
public abstract class TrendForecastImpl implements TrendForecast, Serializable {

    private static final long serialVersionUID = 6616569232494572943L;

    public static Builder builder(){
        return new Builder();
    }

    public static TrendForecastImpl immutableCopyOf(final TrendForecast trendForecast) {
        Objects.requireNonNull(trendForecast);
        if (trendForecast instanceof TrendForecastImpl) {
            return (TrendForecastImpl) trendForecast;
        } else {
            return Builder.copyOf(trendForecast).build();
        }
    }

    public static Optional<TrendForecastImpl> immutableCopyOf(final Optional<TrendForecast> trendForecast) {
        Objects.requireNonNull(trendForecast);
        return trendForecast.map(TrendForecastImpl::immutableCopyOf);
    }

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    @Override
    @JsonDeserialize(contentAs = NumericMeasureImpl.class)
    public abstract Optional<NumericMeasure> getPrevailingVisibility();

    @Override
    @JsonDeserialize(contentAs = SurfaceWindImpl.class)
    public abstract Optional<SurfaceWind> getSurfaceWind();

    @Override
    @JsonDeserialize(contentAs = CloudForecastImpl.class)
    public abstract Optional<CloudForecast> getCloud();

    // NOTE: Immutables generates a final setForecastWeather(...) setter, which cannot be overridden to
    // carry @JsonDeserialize(contentAs = ...) (see doc/immutables-migration.md). The hint moves onto this
    // abstract getter re-declaration instead; the package's passAnnotations style (see package-info.java)
    // propagates it onto the generated builder setter for Jackson's builder-based deserialization.
    // NOTE: no per-property @JsonDeserialize(contentAs=...) hint here: see SIGMETImpl.getAnalysisGeometries() for why
    // (Optional<List<X>> on a non-detached builder). Weather instead carries its own class-level
    // @JsonDeserialize(as=...) hint (see doc/immutables-migration.md).
    @Override
    public abstract Optional<List<Weather>> getForecastWeather();

    public static class Builder extends ImmutableTrendForecastImpl.Builder {

        Builder() {
            setCeilingAndVisibilityOk(false);
            setNoSignificantWeather(false);
        }

        public static Builder copyOf(final TrendForecast value) {
            if (value instanceof TrendForecastImpl) {
                return ((TrendForecastImpl) value).toBuilder();
            } else {
                final Builder retval = TrendForecastImpl.builder()//
                        .setPeriodOfChange(value.getPeriodOfChange())
                        .setInstantOfChange(value.getInstantOfChange())
                        .setCeilingAndVisibilityOk(value.isCeilingAndVisibilityOk())
                        .setChangeIndicator(value.getChangeIndicator())
                        .setPrevailingVisibilityOperator(value.getPrevailingVisibilityOperator())
                        .setNoSignificantWeather(value.isNoSignificantWeather())
                        .setPrevailingVisibility(NumericMeasureImpl.immutableCopyOf(value.getPrevailingVisibility()))
                        .setSurfaceWind(SurfaceWindImpl.immutableCopyOf(value.getSurfaceWind()))
                        .setCloud(CloudForecastImpl.immutableCopyOf(value.getCloud()));

                value.getForecastWeather()
                        .map(layers -> retval.setForecastWeather(
                                Collections.unmodifiableList(layers.stream().map(WeatherImpl::immutableCopyOf).collect(Collectors.toList()))));
                return retval;
            }
        }


        @Override
        public ImmutableTrendForecastImpl build() {
            final ImmutableTrendForecastImpl result = super.build();
            if (result.getPeriodOfChange().isPresent() && result.getInstantOfChange().isPresent()) {
                throw new IllegalStateException("Both the period and the instant of change cannot be set");
            }
            return result;
        }

    }
}
