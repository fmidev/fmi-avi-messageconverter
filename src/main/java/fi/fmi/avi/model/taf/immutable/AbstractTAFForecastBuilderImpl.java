package fi.fmi.avi.model.taf.immutable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.CloudForecast;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.SurfaceWind;
import fi.fmi.avi.model.Weather;
import fi.fmi.avi.model.taf.TAFForecast;
import fi.fmi.avi.model.taf.TAFForecastBuilderHelper;

/**
 * Shared, hand-written base for {@link TAFBaseForecastImpl.Builder} and
 * {@link TAFChangeForecastImpl.Builder}. See {@code METARImpl}'s javadoc (in the sibling
 * {@code fi.fmi.avi.model.metar.immutable} package) for the "detached builder" pattern this class
 * uses instead of extending an Immutables-generated builder directly - the same generic-builder
 * self-type conflict that forced that pattern for METAR/SPECI is present here too, for the same
 * reason: {@link TAFForecast.Builder} declares abstract getters that Immutables' generated
 * builders do not provide.
 *
 * @param <T> the concrete value type ({@link TAFBaseForecastImpl} or {@link TAFChangeForecastImpl})
 * @param <B> the concrete builder type
 */
public abstract class AbstractTAFForecastBuilderImpl<T extends TAFForecast, B extends AbstractTAFForecastBuilderImpl<T, B>>
        implements TAFForecast.Builder<T, B> {

    protected boolean ceilingAndVisibilityOk;
    protected boolean noSignificantWeather;
    // NOTE: these fields carry @JsonIgnore because Jackson's builder-style deserialization otherwise binds JSON
    // properties directly to same-named fields (bypassing the @JsonDeserialize/@JsonProperty hints placed only on
    // the corresponding setter overrides in TAFBaseForecastImpl/TAFChangeForecastImpl), regardless of field
    // visibility; see doc/immutables-migration.md.
    @JsonIgnore
    protected Optional<NumericMeasure> prevailingVisibility = Optional.empty();
    protected Optional<AviationCodeListUser.RelationalOperator> prevailingVisibilityOperator = Optional.empty();
    @JsonIgnore
    protected Optional<SurfaceWind> surfaceWind = Optional.empty();
    @JsonIgnore
    protected Optional<List<Weather>> forecastWeather = Optional.empty();
    @JsonIgnore
    protected Optional<CloudForecast> cloud = Optional.empty();

    @SuppressWarnings("unchecked")
    protected final B self() {
        return (B) this;
    }

    @Override
    public boolean isCeilingAndVisibilityOk() {
        return ceilingAndVisibilityOk;
    }

    @Override
    public B setCeilingAndVisibilityOk(final boolean ceilingAndVisibilityOk) {
        this.ceilingAndVisibilityOk = ceilingAndVisibilityOk;
        return self();
    }

    @Override
    public boolean isNoSignificantWeather() {
        return noSignificantWeather;
    }

    @Override
    public B setNoSignificantWeather(final boolean noSignificantWeather) {
        this.noSignificantWeather = noSignificantWeather;
        return self();
    }

    @Override
    public Optional<NumericMeasure> getPrevailingVisibility() {
        return prevailingVisibility;
    }

    @Override
    public B setPrevailingVisibility(final NumericMeasure prevailingVisibility) {
        this.prevailingVisibility = Optional.of(Objects.requireNonNull(prevailingVisibility, "prevailingVisibility"));
        return self();
    }

    @Override
    public B setPrevailingVisibility(final Optional<? extends NumericMeasure> prevailingVisibility) {
        Objects.requireNonNull(prevailingVisibility, "prevailingVisibility");
        this.prevailingVisibility = prevailingVisibility.isPresent() ? Optional.of(prevailingVisibility.get()) : Optional.empty();
        return self();
    }

    @Override
    public B clearPrevailingVisibility() {
        this.prevailingVisibility = Optional.empty();
        return self();
    }

    @Override
    public Optional<AviationCodeListUser.RelationalOperator> getPrevailingVisibilityOperator() {
        return prevailingVisibilityOperator;
    }

    @Override
    public B setPrevailingVisibilityOperator(final AviationCodeListUser.RelationalOperator prevailingVisibilityOperator) {
        this.prevailingVisibilityOperator = Optional.of(Objects.requireNonNull(prevailingVisibilityOperator, "prevailingVisibilityOperator"));
        return self();
    }

    @Override
    public B setPrevailingVisibilityOperator(final Optional<? extends AviationCodeListUser.RelationalOperator> prevailingVisibilityOperator) {
        Objects.requireNonNull(prevailingVisibilityOperator, "prevailingVisibilityOperator");
        this.prevailingVisibilityOperator = prevailingVisibilityOperator.isPresent() ? Optional.of(prevailingVisibilityOperator.get()) : Optional.empty();
        return self();
    }

    @Override
    public B clearPrevailingVisibilityOperator() {
        this.prevailingVisibilityOperator = Optional.empty();
        return self();
    }

    @Override
    public Optional<SurfaceWind> getSurfaceWind() {
        return surfaceWind;
    }

    @Override
    public B setSurfaceWind(final SurfaceWind surfaceWind) {
        this.surfaceWind = Optional.of(Objects.requireNonNull(surfaceWind, "surfaceWind"));
        return self();
    }

    @Override
    public B setSurfaceWind(final Optional<? extends SurfaceWind> surfaceWind) {
        Objects.requireNonNull(surfaceWind, "surfaceWind");
        this.surfaceWind = surfaceWind.isPresent() ? Optional.of(surfaceWind.get()) : Optional.empty();
        return self();
    }

    @Override
    public B clearSurfaceWind() {
        this.surfaceWind = Optional.empty();
        return self();
    }

    @Override
    public Optional<List<Weather>> getForecastWeather() {
        return forecastWeather;
    }

    @Override
    public B setForecastWeather(final List<Weather> forecastWeather) {
        this.forecastWeather = Optional.of(Objects.requireNonNull(forecastWeather, "forecastWeather"));
        return self();
    }

    @Override
    public B setForecastWeather(final Optional<? extends List<Weather>> forecastWeather) {
        Objects.requireNonNull(forecastWeather, "forecastWeather");
        this.forecastWeather = forecastWeather.isPresent() ? Optional.of(forecastWeather.get()) : Optional.empty();
        return self();
    }

    @Override
    public B clearForecastWeather() {
        this.forecastWeather = Optional.empty();
        return self();
    }

    @Override
    public Optional<CloudForecast> getCloud() {
        return cloud;
    }

    @Override
    public B setCloud(final CloudForecast cloud) {
        this.cloud = Optional.of(Objects.requireNonNull(cloud, "cloud"));
        return self();
    }

    @Override
    public B setCloud(final Optional<? extends CloudForecast> cloud) {
        Objects.requireNonNull(cloud, "cloud");
        this.cloud = cloud.isPresent() ? Optional.of(cloud.get()) : Optional.empty();
        return self();
    }

    @Override
    public B clearCloud() {
        this.cloud = Optional.empty();
        return self();
    }

    @Override
    public B clear() {
        this.ceilingAndVisibilityOk = false;
        this.noSignificantWeather = false;
        this.prevailingVisibility = Optional.empty();
        this.prevailingVisibilityOperator = Optional.empty();
        this.surfaceWind = Optional.empty();
        this.forecastWeather = Optional.empty();
        this.cloud = Optional.empty();
        return self();
    }

    @Override
    public B mergeFrom(final T value) {
        Objects.requireNonNull(value, "value");
        this.ceilingAndVisibilityOk = value.isCeilingAndVisibilityOk();
        this.noSignificantWeather = value.isNoSignificantWeather();
        this.prevailingVisibility = value.getPrevailingVisibility();
        this.prevailingVisibilityOperator = value.getPrevailingVisibilityOperator();
        this.surfaceWind = value.getSurfaceWind();
        this.forecastWeather = value.getForecastWeather();
        this.cloud = value.getCloud();
        return self();
    }

    @Override
    public B mergeFrom(final B template) {
        Objects.requireNonNull(template, "template");
        this.ceilingAndVisibilityOk = template.ceilingAndVisibilityOk;
        this.noSignificantWeather = template.noSignificantWeather;
        this.prevailingVisibility = template.prevailingVisibility;
        this.prevailingVisibilityOperator = template.prevailingVisibilityOperator;
        this.surfaceWind = template.surfaceWind;
        this.forecastWeather = template.forecastWeather;
        this.cloud = template.cloud;
        return self();
    }

    @Override
    public B copyFrom(final TAFForecast value) {
        Objects.requireNonNull(value, "value");
        TAFForecastBuilderHelper.copyFrom(self(), value);
        return self();
    }

    @Override
    public B mergeFromTAFForecast(final TAFForecast value) {
        Objects.requireNonNull(value, "value");
        TAFForecastBuilderHelper.mergeFromTAFForecast(self(), value);
        return self();
    }
}

