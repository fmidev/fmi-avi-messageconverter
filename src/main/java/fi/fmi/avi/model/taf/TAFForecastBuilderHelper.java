package fi.fmi.avi.model.taf;

import static fi.fmi.avi.model.BuilderHelper.toImmutableList;
import static java.util.Objects.requireNonNull;

import java.util.Optional;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.immutable.CloudForecastImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.SurfaceWindImpl;
import fi.fmi.avi.model.immutable.WeatherImpl;

/**
 * Helper methods for implementations of {@link TAFForecast.Builder}.
 */
public final class TAFForecastBuilderHelper {
    private TAFForecastBuilderHelper() {
        throw new UnsupportedOperationException();
    }

    /**
     * Copies all property values from the given {@code TAFForecast}.
     * Properties specific to {@link TAFBaseForecast} are copied when {@code value} is an instance of {@code TAFBaseForecast}.
     *
     * @param builder
     *         builder to copy into
     * @param value
     *         copy source
     */
    public static void copyFrom(final TAFForecast.Builder<?, ?> builder, final TAFForecast value) {
        requireNonNull(builder, "builder");
        requireNonNull(value, "value");
        builder.setCeilingAndVisibilityOk(value.isCeilingAndVisibilityOk());
        builder.setCloud(CloudForecastImpl.immutableCopyOf(value.getCloud()));
        builder.setNoSignificantWeather(value.isNoSignificantWeather());
        builder.setPrevailingVisibility(NumericMeasureImpl.immutableCopyOf(value.getPrevailingVisibility()));
        builder.setPrevailingVisibilityOperator(value.getPrevailingVisibilityOperator());
        builder.setSurfaceWind(SurfaceWindImpl.immutableCopyOf(value.getSurfaceWind()));
        builder.setForecastWeather(value.getForecastWeather()//
                .map(list -> toImmutableList(list, WeatherImpl::immutableCopyOf)));
    }

    /**
     * Merges the given {@code TAFForecast} into specified {@code builder}.
     * All existing properties of {@code value} are copied into the builder and properties that are empty in {@code value} are left as is in the builder
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
     * @param builder
     *         builder to merge into
     * @param value
     *         merge source
     */
    public static void mergeFromTAFForecast(final TAFForecast.Builder<?, ?> builder, final TAFForecast value) {
        requireNonNull(builder, "builder");
        requireNonNull(value, "value");
        mergeCeilingAndVisibilityOk(builder, value);
        mergePrevailingVisibility(builder, value);
        mergePrevailingVisibilityOperator(builder, value);
        mergeSurfaceWind(builder, value);
        mergeForecastWeather(builder, value);
        mergeCloud(builder, value);
    }

    private static void mergeCeilingAndVisibilityOk(final TAFForecast.Builder<?, ?> builder, final TAFForecast from) {
        builder.setCeilingAndVisibilityOk(from.isCeilingAndVisibilityOk());
    }

    private static void mergePrevailingVisibility(final TAFForecast.Builder<?, ?> builder, final TAFForecast from) {
        if (from.isCeilingAndVisibilityOk()) {
            builder.setPrevailingVisibility(from.getPrevailingVisibility().map(NumericMeasureImpl::immutableCopyOf));
        } else {
            from.getPrevailingVisibility()//
                    .map(NumericMeasureImpl::immutableCopyOf)//
                    .ifPresent(builder::setPrevailingVisibility);
        }
    }

    private static void mergePrevailingVisibilityOperator(final TAFForecast.Builder<?, ?> builder, final TAFForecast from) {
        final Optional<AviationCodeListUser.RelationalOperator> prevailingVisibilityOperator = from.getPrevailingVisibilityOperator();
        if (prevailingVisibilityOperator.isPresent() || from.getPrevailingVisibility().isPresent()) {
            builder.setPrevailingVisibilityOperator(prevailingVisibilityOperator);
        }
    }

    private static void mergeSurfaceWind(final TAFForecast.Builder<?, ?> builder, final TAFForecast from) {
        from.getSurfaceWind()//
                .map(SurfaceWindImpl::immutableCopyOf)//
                .ifPresent(builder::setSurfaceWind);
    }

    private static void mergeForecastWeather(final TAFForecast.Builder<?, ?> builder, final TAFForecast from) {
        if (from.isCeilingAndVisibilityOk() || from.isNoSignificantWeather()) {
            builder.setForecastWeather(from.getForecastWeather()//
                    .map(list -> toImmutableList(list, WeatherImpl::immutableCopyOf)));
        } else {
            from.getForecastWeather()//
                    .map(list -> toImmutableList(list, WeatherImpl::immutableCopyOf))//
                    .ifPresent(builder::setForecastWeather);
        }
        builder.setNoSignificantWeather(false);
    }

    private static void mergeCloud(final TAFForecast.Builder<?, ?> builder, final TAFForecast from) {
        if (from.isCeilingAndVisibilityOk()) {
            builder.setCloud(from.getCloud().map(CloudForecastImpl::immutableCopyOf));
        } else {
            from.getCloud()//
                    .map(CloudForecastImpl::immutableCopyOf)//
                    .ifPresent(builder::setCloud);
        }
    }
}
