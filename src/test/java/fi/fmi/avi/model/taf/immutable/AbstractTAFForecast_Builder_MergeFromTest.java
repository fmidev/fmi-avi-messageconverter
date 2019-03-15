package fi.fmi.avi.model.taf.immutable;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.CloudForecast;
import fi.fmi.avi.model.CloudLayer;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.SurfaceWind;
import fi.fmi.avi.model.Weather;
import fi.fmi.avi.model.immutable.CloudForecastImpl;
import fi.fmi.avi.model.immutable.CloudLayerImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.SurfaceWindImpl;
import fi.fmi.avi.model.immutable.WeatherImpl;
import fi.fmi.avi.model.taf.TAFForecast;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.converters.Nullable;

@SuppressWarnings("UnnecessaryLocalVariable")
@RunWith(JUnitParamsRunner.class)
public abstract class AbstractTAFForecast_Builder_MergeFromTest<T extends TAFForecast, B extends TAFForecast.Builder<T, B>> {
    protected static final String FEET = "ft_i";
    protected static final String METERS = "m";
    protected static final String DEGREES = "deg";
    protected static final String KNOT = "kn_i";
    protected static final String DEGREES_CELSIUS = "Cel";
    protected static final double VARIABLE_WIND_DIRECTION = Double.POSITIVE_INFINITY;

    protected static Optional<NumericMeasure> meters(final double meters) {
        return Double.isNaN(meters) ? Optional.empty() : Optional.of(NumericMeasureImpl.of(meters, METERS));
    }

    protected static CloudForecast newCloudForecast(final CloudLayer... cloudLayers) {
        return new CloudForecastImpl.Builder()//
                .setLayers(Collections.unmodifiableList(Arrays.asList(cloudLayers)))//
                .build();
    }

    protected static CloudForecast newCloudForecast(final double verticalVisibilityFeet) {
        return new CloudForecastImpl.Builder()//
                .setVerticalVisibility(NumericMeasureImpl.of(verticalVisibilityFeet, FEET))//
                .build();
    }

    protected static CloudLayer newCloudLayer(final AviationCodeListUser.CloudAmount amount, final double baseFeet) {
        return new CloudLayerImpl.Builder()//
                .setAmount(amount)//
                .setBase(NumericMeasureImpl.of(baseFeet, FEET))//
                .build();
    }

    protected static Weather newWeather(final String code) {
        return WeatherImpl.fromCodes(code).get(0);
    }

    protected static SurfaceWind newSurfaceWind(final double meanDirectionDegrees, final double meanSpeed) {
        final SurfaceWindImpl.Builder builder = new SurfaceWindImpl.Builder();
        final boolean variableDirection = meanDirectionDegrees == VARIABLE_WIND_DIRECTION;
        if (variableDirection) {
            builder.setVariableDirection(true);
        } else {
            builder.setMeanWindDirection(NumericMeasureImpl.of(meanDirectionDegrees, DEGREES));
        }
        builder.setMeanWindSpeed(NumericMeasureImpl.of(meanSpeed, KNOT));
        if (!Double.isNaN(Double.NaN)) {
            builder.setWindGust(NumericMeasureImpl.of(Double.NaN, KNOT));
        }
        return builder.build();
    }

    protected abstract B newBuilder();

    protected abstract B clearOptionalSpecializedValues(final B builder);

    protected abstract B populateOptionalEmptySpecializedValues(final B builder);

    protected abstract B populateOptionalSpecializedValues(final B builder);

    protected abstract B copyOptionalSpecializedValues(final B from, final B to);

    @Test
    public void empty_values_are_not_copied() {
        // given
        final B builder = populateOptionalEmptySpecializedValues(newBuilder()//
                .setPrevailingVisibility(meters(0.0))//
                .setPrevailingVisibilityOperator(AviationCodeListUser.RelationalOperator.ABOVE)//
                .setForecastWeather(Collections.singletonList(newWeather("RA")))//
                .setCloud(newCloudForecast(//
                        newCloudLayer(AviationCodeListUser.CloudAmount.FEW, 4000.0), //
                        newCloudLayer(AviationCodeListUser.CloudAmount.SCT, 6000.0)))//
                .setSurfaceWind(newSurfaceWind(45.0, 15.0))//
                .setCeilingAndVisibilityOk(true)//
                .setNoSignificantWeather(true));

        final T source = clearOptionalSpecializedValues(newBuilder()//
                .clearPrevailingVisibility()//
                .clearPrevailingVisibilityOperator()//
                .clearForecastWeather()//
                .clearCloud()//
                .clearSurfaceWind()//
                .setCeilingAndVisibilityOk(false)//
                .setNoSignificantWeather(false))//
                .build();

        final T expect = newBuilder().copyFrom(builder.build())//
                .setCeilingAndVisibilityOk(false)//
                .setNoSignificantWeather(false)//
                .build();

        // when
        builder.mergeFromTAFForecast(source);

        // then
        assertEquals(expect, builder.build());
    }

    @Test
    public void existing_values_are_copied() {
        // given
        final B builder = newBuilder()//
                .setPrevailingVisibility(meters(0.0))//
                .setPrevailingVisibilityOperator(AviationCodeListUser.RelationalOperator.ABOVE)//
                .setForecastWeather(Collections.singletonList(newWeather("RA")))//
                .setCloud(newCloudForecast(//
                        newCloudLayer(AviationCodeListUser.CloudAmount.FEW, 4000.0), //
                        newCloudLayer(AviationCodeListUser.CloudAmount.SCT, 6000.0)))//
                .setSurfaceWind(newSurfaceWind(45.0, 15.0))//
                .setCeilingAndVisibilityOk(true)//
                .setNoSignificantWeather(true);
        populateOptionalEmptySpecializedValues(builder);

        final T source = populateOptionalSpecializedValues(newBuilder()//
                .setPrevailingVisibility(meters(6000.0))//
                .setPrevailingVisibilityOperator(AviationCodeListUser.RelationalOperator.BELOW)//
                .setForecastWeather(Collections.singletonList(newWeather("DZ")))//
                .setCloud(newCloudForecast(//
                        newCloudLayer(AviationCodeListUser.CloudAmount.BKN, 3000.0), //
                        newCloudLayer(AviationCodeListUser.CloudAmount.OVC, 7000.0)))//
                .setSurfaceWind(newSurfaceWind(180.0, 25.0))//
                .setCeilingAndVisibilityOk(false)//
                .setNoSignificantWeather(false))//
                .build();

        final T expect = source;

        // when
        builder.mergeFromTAFForecast(source);

        // then
        assertEquals(expect, builder.build());
    }

    @Test
    public void acceptsAnyTAFForecast() {
        // given
        final B builder = populateOptionalEmptySpecializedValues(newBuilder()//
                .setCeilingAndVisibilityOk(true)//
                .setNoSignificantWeather(true));

        final TAFForecast source = new TestTAFForecast.Builder()//
                .setPrevailingVisibility(meters(6000.0))//
                .setPrevailingVisibilityOperator(AviationCodeListUser.RelationalOperator.BELOW)//
                .setForecastWeather(Collections.singletonList(newWeather("DZ")))//
                .setCloud(newCloudForecast(//
                        newCloudLayer(AviationCodeListUser.CloudAmount.BKN, 3000.0), //
                        newCloudLayer(AviationCodeListUser.CloudAmount.OVC, 7000.0)))//
                .setSurfaceWind(newSurfaceWind(180.0, 25.0))//
                .setCeilingAndVisibilityOk(false)//
                .setNoSignificantWeather(false)//
                .build();

        final T expect = copyOptionalSpecializedValues(builder, newBuilder()//
                .copyFrom(source))//
                .build();

        // when
        builder.mergeFromTAFForecast(source);

        // then
        assertEquals(expect, builder.build());
    }

    @Test
    public void CAVOK_is_copied() {
        // given
        final B builder = newBuilder();

        final T source = newBuilder()//
                .setCeilingAndVisibilityOk(true)//
                .build();

        final T expect = source;

        // when
        builder.mergeFromTAFForecast(source);

        // then
        assertEquals(expect, builder.build());
    }

    @Test
    public void NSW_is_not_copied() {
        // given
        final B builder = newBuilder();

        final T source = newBuilder()//
                .setNoSignificantWeather(true)//
                .build();

        final T expect = newBuilder().mergeFrom(source)//
                .setNoSignificantWeather(false)//
                .build();

        // when
        builder.mergeFromTAFForecast(source);

        // then
        assertEquals(expect, builder.build());
    }

    @Test
    public void NSW_on_base_forecast_is_set_false() {
        // given
        final B builder = newBuilder()//
                .setForecastWeather(Collections.emptyList())//
                .setNoSignificantWeather(true);

        final T source = newBuilder()//
                .clearForecastWeather()//
                .setNoSignificantWeather(true)//
                .build();

        final T expect = newBuilder()//
                .clearForecastWeather()//
                .setNoSignificantWeather(false)//
                .build();

        // when
        builder.mergeFromTAFForecast(source);

        // then
        assertEquals(expect, builder.build());
    }

    @Parameters({ "10000.0", "NaN" })
    @Test
    public void prevailingVisibility_is_always_copied_on_CAVOK(final double prevailingVisibilityMeters) {
        // given
        final B builder = newBuilder()//
                .setPrevailingVisibility(meters(4000.0));

        final T source = newBuilder()//
                .setCeilingAndVisibilityOk(true)//
                .setPrevailingVisibility(meters(prevailingVisibilityMeters))//
                .build();

        final T expect = source;

        // when
        builder.mergeFromTAFForecast(source);

        // then
        assertEquals(expect, builder.build());
    }

    @Test
    public void prevailingVisibilityOperator_is_copied_whenever_prevailingVisibility_is_copied() {
        // given
        final B builder = newBuilder()//
                .setPrevailingVisibility(meters(10_000.0))//
                .setPrevailingVisibilityOperator(AviationCodeListUser.RelationalOperator.ABOVE);

        final T source = newBuilder()//
                .setPrevailingVisibility(meters(8000.0))//
                .clearPrevailingVisibilityOperator()//
                .build();

        final T expect = source;

        // when
        builder.mergeFromTAFForecast(source);

        // then
        assertEquals(expect, builder.build());
    }

    @Parameters(source = WeathersProvider.class)
    @Test
    public void forecastWeather_is_always_copied_on_CAVOK(@Nullable final List<Weather> weather) {
        // given
        final B builder = newBuilder()//
                .setForecastWeather(Collections.singletonList(newWeather("VA")));

        final T source = newBuilder()//
                .setCeilingAndVisibilityOk(true)//
                .setNullableForecastWeather(weather)//
                .build();

        final T expect = source;

        // when
        builder.mergeFromTAFForecast(source);

        // then
        assertEquals(expect, builder.build());
    }

    @Parameters(source = WeathersProvider.class)
    @Test
    public void forecastWeather_is_always_copied_on_NSW(@Nullable final List<Weather> weather) {
        // given
        final B builder = newBuilder()//
                .setForecastWeather(Collections.singletonList(newWeather("VA")));

        final T source = newBuilder()//
                .setNoSignificantWeather(true)//
                .setNullableForecastWeather(weather)//
                .build();

        final T expect = newBuilder()//
                .mergeFrom(source)//
                .setNoSignificantWeather(false)//
                .build();

        // when
        builder.mergeFromTAFForecast(source);

        // then
        assertEquals(expect, builder.build());
    }

    @Parameters
    @Test
    public void cloudForecast_is_always_copied_on_CAVOK(@Nullable final CloudForecast cloudForecast) {
        // given
        final B builder = newBuilder()//
                .setCloud(newCloudForecast(30.0));

        final T source = newBuilder()//
                .setCeilingAndVisibilityOk(true)//
                .setNullableCloud(cloudForecast)//
                .build();

        final T expect = source;

        // when
        builder.mergeFromTAFForecast(source);

        // then
        assertEquals(expect, builder.build());
    }

    @SuppressWarnings("unused")
    public Object parametersForCloudForecast_is_always_copied_on_CAVOK() {
        return new Object[] {//
                newCloudForecast(newCloudLayer(AviationCodeListUser.CloudAmount.SCT, 100.0)), //
                null//
        };
    }

    @FreeBuilder
    abstract static class TestTAFForecast implements TAFForecast {

        @Override
        public  TAFForecast.Builder<? extends TAFForecast, ? extends TAFForecast.Builder> toBuilder() {
            return null;
        }

        public static class Builder extends AbstractTAFForecast_Builder_MergeFromTest_TestTAFForecast_Builder {
        }
    }

    @SuppressWarnings("unused")
    public static class WeathersProvider {
        public static Object provideWeathers() {
            return new Object[] {//
                    new Object[] { Collections.singletonList(newWeather("DU")) }, //
                    new Object[] { Collections.emptyList() }, //
                    new Object[] { null }//
            };
        }
    }
}
