package fi.fmi.avi.model.taf;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;

import javax.annotation.Nullable;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.CloudForecast;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.SurfaceWind;
import fi.fmi.avi.model.Weather;

/**
 * Created by rinne on 02/05/2018.
 */
public interface TAFForecast extends AviationCodeListUser {

    boolean isCeilingAndVisibilityOk();

    Optional<NumericMeasure> getPrevailingVisibility();

    Optional<AviationCodeListUser.RelationalOperator> getPrevailingVisibilityOperator();

    Optional<SurfaceWind> getSurfaceWind();

    Optional<List<Weather>> getForecastWeather();

    boolean isNoSignificantWeather();

    Optional<CloudForecast> getCloud();

    TAFForecast.Builder<? extends TAFForecast, ? extends TAFForecast.Builder> toBuilder();

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    interface Builder<T extends TAFForecast, B extends Builder<T, B>> {
        /**
         * Returns a newly-created {@link TAFForecast} based on the contents of the {@code Builder}.
         *
         * @throws IllegalStateException
         *         if any field has not been set
         */
        T build();

        /**
         * Sets all property values using the given {@code TAFForecast} as a template.
         */
        B mergeFrom(T value);

        /**
         * Copies values from the given {@code Builder}. Does not affect any properties not set on the
         * input.
         */
        B mergeFrom(B template);

        /**
         * Copies all property values from the given {@code TAFForecast}.
         * Properties specific to {@link TAFBaseForecast} are copied when {@code value} is an instance of {@code TAFBaseForecast}.
         *
         * @param value
         *         copy source
         *
         * @return this builder
         */
        B copyFrom(TAFForecast value);

        /**
         * Merges the given {@code TAFForecast} into this builder.
         * All existing properties of {@code value} are copied into this builder and properties that are empty in {@code value} are left as is in this builder
         * with some exceptions depending on property values of {@code value}:
         *
         * <ul>
         * <li>
         * {@code prevailingVisibility}, {@code forecastWeather} and {@code cloudForecast} are always copied when {@code ceilingAndVisibilityOk == true}.
         * </li>
         * <li>{@code prevailingVisibilityOperator} is copied always whenever {@code prevailingVisibility} is copied.</li>
         * <li>{@code forecastWeather} is always copied when {@code noSignificantWeather == true}.</li>
         * <li>{@code noSignificantWeather} is always set to {@code false}</li>
         * </ul>
         *
         * @param value
         *         merge source
         *
         * @return this builder
         */
        B mergeFromTAFForecast(TAFForecast value);

        /**
         * Resets the state of this builder.
         */
        B clear();

        /**
         * Replaces the value to be returned by {@link TAFForecast#isCeilingAndVisibilityOk()} by applying
         * {@code mapper} to it and using the result.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mapper} is null or returns null
         * @throws IllegalStateException
         *         if the field has not been set
         */
        default B mapCeilingAndVisibilityOk(final UnaryOperator<Boolean> mapper) {
            Objects.requireNonNull(mapper);
            return setCeilingAndVisibilityOk(mapper.apply(isCeilingAndVisibilityOk()));
        }

        /**
         * Returns the value that will be returned by {@link TAFForecast#isCeilingAndVisibilityOk()}.
         *
         * @throws IllegalStateException
         *         if the field has not been set
         */
        boolean isCeilingAndVisibilityOk();

        /**
         * Sets the value to be returned by {@link TAFForecast#isCeilingAndVisibilityOk()}.
         *
         * @return this {@code Builder} object
         */
        B setCeilingAndVisibilityOk(boolean ceilingAndVisibilityOk);

        /**
         * Sets the value to be returned by {@link TAFForecast#getPrevailingVisibility()}.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code prevailingVisibility} is null
         */
        B setPrevailingVisibility(NumericMeasure prevailingVisibility);

        /**
         * Sets the value to be returned by {@link TAFForecast#getPrevailingVisibility()}.
         *
         * @return this {@code Builder} object
         */
        default B setNullablePrevailingVisibility(@Nullable final NumericMeasure prevailingVisibility) {
            if (prevailingVisibility != null) {
                return setPrevailingVisibility(prevailingVisibility);
            } else {
                return clearPrevailingVisibility();
            }
        }

        /**
         * If the value to be returned by {@link TAFForecast#getPrevailingVisibility()} is present,
         * replaces it by applying {@code mapper} to it and using the result.
         *
         * <p>If the result is null, clears the value.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mapper} is null
         */
        default B mapPrevailingVisibility(final UnaryOperator<NumericMeasure> mapper) {
            return setPrevailingVisibility(getPrevailingVisibility().map(mapper));
        }

        /**
         * Sets the value to be returned by {@link TAFForecast#getPrevailingVisibility()} to {@link
         * Optional#empty() Optional.empty()}.
         *
         * @return this {@code Builder} object
         */
        B clearPrevailingVisibility();

        /**
         * Returns the value that will be returned by {@link TAFForecast#getPrevailingVisibility()}.
         */
        Optional<NumericMeasure> getPrevailingVisibility();

        /**
         * Sets the value to be returned by {@link TAFForecast#getPrevailingVisibility()}.
         *
         * @return this {@code Builder} object
         */
        default B setPrevailingVisibility(final Optional<? extends NumericMeasure> prevailingVisibility) {
            if (prevailingVisibility.isPresent()) {
                return setPrevailingVisibility(prevailingVisibility.get());
            } else {
                return clearPrevailingVisibility();
            }
        }

        /**
         * Sets the value to be returned by {@link TAFForecast#getPrevailingVisibilityOperator()}.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code prevailingVisibilityOperator} is null
         */
        B setPrevailingVisibilityOperator(AviationCodeListUser.RelationalOperator prevailingVisibilityOperator);

        /**
         * Sets the value to be returned by {@link TAFForecast#getPrevailingVisibilityOperator()}.
         *
         * @return this {@code Builder} object
         */
        default B setNullablePrevailingVisibilityOperator(@Nullable final AviationCodeListUser.RelationalOperator prevailingVisibilityOperator) {
            if (prevailingVisibilityOperator != null) {
                return setPrevailingVisibilityOperator(prevailingVisibilityOperator);
            } else {
                return clearPrevailingVisibilityOperator();
            }
        }

        /**
         * If the value to be returned by {@link TAFForecast#getPrevailingVisibilityOperator()} is
         * present, replaces it by applying {@code mapper} to it and using the result.
         *
         * <p>If the result is null, clears the value.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mapper} is null
         */
        default B mapPrevailingVisibilityOperator(final UnaryOperator<AviationCodeListUser.RelationalOperator> mapper) {
            return setPrevailingVisibilityOperator(getPrevailingVisibilityOperator().map(mapper));
        }

        /**
         * Sets the value to be returned by {@link TAFForecast#getPrevailingVisibilityOperator()} to
         * {@link Optional#empty() Optional.empty()}.
         *
         * @return this {@code Builder} object
         */
        B clearPrevailingVisibilityOperator();

        /**
         * Returns the value that will be returned by {@link
         * TAFForecast#getPrevailingVisibilityOperator()}.
         */
        Optional<AviationCodeListUser.RelationalOperator> getPrevailingVisibilityOperator();

        /**
         * Sets the value to be returned by {@link TAFForecast#getPrevailingVisibilityOperator()}.
         *
         * @return this {@code Builder} object
         */
        default B setPrevailingVisibilityOperator(final Optional<? extends AviationCodeListUser.RelationalOperator> prevailingVisibilityOperator) {
            if (prevailingVisibilityOperator.isPresent()) {
                return setPrevailingVisibilityOperator(prevailingVisibilityOperator.get());
            } else {
                return clearPrevailingVisibilityOperator();
            }
        }

        /**
         * Sets the value to be returned by {@link TAFForecast#getSurfaceWind()}.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code surfaceWind} is null
         */
        B setSurfaceWind(SurfaceWind surfaceWind);

        /**
         * Sets the value to be returned by {@link TAFForecast#getSurfaceWind()}.
         *
         * @return this {@code Builder} object
         */
        default B setNullableSurfaceWind(@Nullable final SurfaceWind surfaceWind) {
            if (surfaceWind != null) {
                return setSurfaceWind(surfaceWind);
            } else {
                return clearSurfaceWind();
            }
        }

        /**
         * If the value to be returned by {@link TAFForecast#getSurfaceWind()} is present, replaces it by
         * applying {@code mapper} to it and using the result.
         *
         * <p>If the result is null, clears the value.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mapper} is null
         */
        default B mapSurfaceWind(final UnaryOperator<SurfaceWind> mapper) {
            return setSurfaceWind(getSurfaceWind().map(mapper));
        }

        /**
         * Sets the value to be returned by {@link TAFForecast#getSurfaceWind()} to {@link
         * Optional#empty() Optional.empty()}.
         *
         * @return this {@code Builder} object
         */
        B clearSurfaceWind();

        /**
         * Returns the value that will be returned by {@link TAFForecast#getSurfaceWind()}.
         */
        Optional<SurfaceWind> getSurfaceWind();

        /**
         * Sets the value to be returned by {@link TAFForecast#getSurfaceWind()}.
         *
         * @return this {@code Builder} object
         */
        default B setSurfaceWind(final Optional<? extends SurfaceWind> surfaceWind) {
            if (surfaceWind.isPresent()) {
                return setSurfaceWind(surfaceWind.get());
            } else {
                return clearSurfaceWind();
            }
        }

        /**
         * Sets the value to be returned by {@link TAFForecast#getForecastWeather()}.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code forecastWeather} is null
         */
        B setForecastWeather(List<Weather> forecastWeather);

        /**
         * Sets the value to be returned by {@link TAFForecast#getForecastWeather()}.
         *
         * @return this {@code Builder} object
         */
        default B setNullableForecastWeather(@Nullable final List<Weather> forecastWeather) {
            if (forecastWeather != null) {
                return setForecastWeather(forecastWeather);
            } else {
                return clearForecastWeather();
            }
        }

        /**
         * If the value to be returned by {@link TAFForecast#getForecastWeather()} is present, replaces it
         * by applying {@code mapper} to it and using the result.
         *
         * <p>If the result is null, clears the value.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mapper} is null
         */
        default B mapForecastWeather(final UnaryOperator<List<Weather>> mapper) {
            return setForecastWeather(getForecastWeather().map(mapper));
        }

        /**
         * Sets the value to be returned by {@link TAFForecast#getForecastWeather()} to {@link
         * Optional#empty() Optional.empty()}.
         *
         * @return this {@code Builder} object
         */
        B clearForecastWeather();

        /**
         * Returns the value that will be returned by {@link TAFForecast#getForecastWeather()}.
         */
        Optional<List<Weather>> getForecastWeather();

        /**
         * Sets the value to be returned by {@link TAFForecast#getForecastWeather()}.
         *
         * @return this {@code Builder} object
         */
        default B setForecastWeather(final Optional<? extends List<Weather>> forecastWeather) {
            if (forecastWeather.isPresent()) {
                return setForecastWeather(forecastWeather.get());
            } else {
                return clearForecastWeather();
            }
        }

        /**
         * Replaces the value to be returned by {@link TAFForecast#isNoSignificantWeather()} by applying
         * {@code mapper} to it and using the result.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mapper} is null or returns null
         * @throws IllegalStateException
         *         if the field has not been set
         */
        default B mapNoSignificantWeather(final UnaryOperator<Boolean> mapper) {
            Objects.requireNonNull(mapper);
            return setNoSignificantWeather(mapper.apply(isNoSignificantWeather()));
        }

        /**
         * Returns the value that will be returned by {@link TAFForecast#isNoSignificantWeather()}.
         *
         * @throws IllegalStateException
         *         if the field has not been set
         */
        boolean isNoSignificantWeather();

        /**
         * Sets the value to be returned by {@link TAFForecast#isNoSignificantWeather()}.
         *
         * @return this {@code Builder} object
         */
        B setNoSignificantWeather(boolean noSignificantWeather);

        /**
         * Sets the value to be returned by {@link TAFForecast#getCloud()}.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code cloud} is null
         */
        B setCloud(CloudForecast cloud);

        /**
         * Sets the value to be returned by {@link TAFForecast#getCloud()}.
         *
         * @return this {@code Builder} object
         */
        default B setNullableCloud(@Nullable final CloudForecast cloud) {
            if (cloud != null) {
                return setCloud(cloud);
            } else {
                return clearCloud();
            }
        }

        /**
         * If the value to be returned by {@link TAFForecast#getCloud()} is present, replaces it by
         * applying {@code mapper} to it and using the result.
         *
         * <p>If the result is null, clears the value.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mapper} is null
         */
        default B mapCloud(final UnaryOperator<CloudForecast> mapper) {
            return setCloud(getCloud().map(mapper));
        }

        /**
         * Sets the value to be returned by {@link TAFForecast#getCloud()} to {@link Optional#empty()
         * Optional.empty()}.
         *
         * @return this {@code Builder} object
         */
        B clearCloud();

        /**
         * Returns the value that will be returned by {@link TAFForecast#getCloud()}.
         */
        Optional<CloudForecast> getCloud();

        /**
         * Sets the value to be returned by {@link TAFForecast#getCloud()}.
         *
         * @return this {@code Builder} object
         */
        default B setCloud(final Optional<? extends CloudForecast> cloud) {
            if (cloud.isPresent()) {
                return setCloud(cloud.get());
            } else {
                return clearCloud();
            }
        }
    }
}
