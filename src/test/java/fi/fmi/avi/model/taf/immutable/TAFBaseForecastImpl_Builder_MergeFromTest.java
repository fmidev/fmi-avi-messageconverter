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
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.Weather;
import fi.fmi.avi.model.immutable.CloudForecastImpl;
import fi.fmi.avi.model.immutable.CloudLayerImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.WeatherImpl;
import fi.fmi.avi.model.taf.TAFForecast;
import fi.fmi.avi.model.taf.TAFSurfaceWind;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.converters.Nullable;

@SuppressWarnings("UnnecessaryLocalVariable")
@RunWith(JUnitParamsRunner.class)
public class TAFBaseForecastImpl_Builder_MergeFromTest {
    private static final String FEET = "ft_i";
    private static final String METERS = "m";
    private static final String DEGREES = "deg";
    private static final String KNOT = "kn_i";
    private static final String DEGREES_CELSIUS = "Cel";
    private static final double VARIABLE_WIND_DIRECTION = Double.POSITIVE_INFINITY;

    private static Optional<NumericMeasure> meters(final double meters) {
        return Double.isNaN(meters) ? Optional.empty() : Optional.of(NumericMeasureImpl.of(meters, METERS));
    }

    private static CloudForecast newCloudForecast(final CloudLayer... cloudLayers) {
        return new CloudForecastImpl.Builder()//
                .setLayers(Collections.unmodifiableList(Arrays.asList(cloudLayers)))//
                .build();
    }

    private static CloudForecast newCloudForecast(final double verticalVisibilityFeet) {
        return new CloudForecastImpl.Builder()//
                .setVerticalVisibility(NumericMeasureImpl.of(verticalVisibilityFeet, FEET))//
                .build();
    }

    private static CloudLayer newCloudLayer(final AviationCodeListUser.CloudAmount amount, final double baseFeet) {
        return new CloudLayerImpl.Builder()//
                .setAmount(amount)//
                .setBase(NumericMeasureImpl.of(baseFeet, FEET))//
                .build();
    }

    private static Weather newWeather(final String code) {
        return WeatherImpl.fromCodes(code).get(0);
    }

    private static TAFSurfaceWind newSurfaceWind(final double meanDirectionDegrees, final double meanSpeed) {
        final TAFSurfaceWindImpl.Builder builder = new TAFSurfaceWindImpl.Builder();
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

    @Test
    public void empty_values_are_not_copied() {
        // given
        final TAFBaseForecastImpl.Builder builder = new TAFBaseForecastImpl.Builder()//
                .setPrevailingVisibility(meters(0.0))//
                .setPrevailingVisibilityOperator(AviationCodeListUser.RelationalOperator.ABOVE)//
                .setForecastWeather(Collections.singletonList(newWeather("RA")))//
                .setCloud(newCloudForecast(//
                        newCloudLayer(AviationCodeListUser.CloudAmount.FEW, 4000.0), //
                        newCloudLayer(AviationCodeListUser.CloudAmount.SCT, 6000.0)))//
                .setSurfaceWind(newSurfaceWind(45.0, 15.0))//
                .setCeilingAndVisibilityOk(true)//
                .setNoSignificantWeather(true)//
                .setTemperatures(Collections.emptyList());

        final TAFBaseForecastImpl source = new TAFBaseForecastImpl.Builder()//
                .clearPrevailingVisibility()//
                .clearPrevailingVisibilityOperator()//
                .clearForecastWeather()//
                .clearCloud()//
                .clearSurfaceWind()//
                .setCeilingAndVisibilityOk(false)//
                .setNoSignificantWeather(false)//
                .clearTemperatures()//
                .build();

        final TAFBaseForecastImpl expect = TAFBaseForecastImpl.Builder.from(builder.build())//
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
        final TAFBaseForecastImpl.Builder builder = new TAFBaseForecastImpl.Builder()//
                .setPrevailingVisibility(meters(0.0))//
                .setPrevailingVisibilityOperator(AviationCodeListUser.RelationalOperator.ABOVE)//
                .setForecastWeather(Collections.singletonList(newWeather("RA")))//
                .setCloud(newCloudForecast(//
                        newCloudLayer(AviationCodeListUser.CloudAmount.FEW, 4000.0), //
                        newCloudLayer(AviationCodeListUser.CloudAmount.SCT, 6000.0)))//
                .setSurfaceWind(newSurfaceWind(45.0, 15.0))//
                .setCeilingAndVisibilityOk(true)//
                .setNoSignificantWeather(true)//
                .setTemperatures(Collections.emptyList());

        final TAFBaseForecastImpl source = new TAFBaseForecastImpl.Builder()//
                .setPrevailingVisibility(meters(6000.0))//
                .setPrevailingVisibilityOperator(AviationCodeListUser.RelationalOperator.BELOW)//
                .setForecastWeather(Collections.singletonList(newWeather("DZ")))//
                .setCloud(newCloudForecast(//
                        newCloudLayer(AviationCodeListUser.CloudAmount.BKN, 3000.0), //
                        newCloudLayer(AviationCodeListUser.CloudAmount.OVC, 7000.0)))//
                .setSurfaceWind(newSurfaceWind(180.0, 25.0))//
                .setCeilingAndVisibilityOk(false)//
                .setNoSignificantWeather(false)//
                .setTemperatures(Collections.singletonList(new TAFAirTemperatureForecastImpl.Builder()//
                        .setMinTemperature(NumericMeasureImpl.of(0.0, DEGREES_CELSIUS))//
                        .setMinTemperatureTime(new PartialOrCompleteTimeInstant.Builder().build())//
                        .setMaxTemperature(NumericMeasureImpl.of(0.0, DEGREES_CELSIUS))//
                        .setMaxTemperatureTime(new PartialOrCompleteTimeInstant.Builder().build())//
                        .build()))//
                .build();

        final TAFBaseForecastImpl expect = source;

        // when
        builder.mergeFromTAFForecast(source);

        // then
        assertEquals(expect, builder.build());
    }

    @Test
    public void acceptsAnyTAFForecast() {
        // given
        final TAFBaseForecastImpl.Builder builder = new TAFBaseForecastImpl.Builder()//
                .setCeilingAndVisibilityOk(true)//
                .setNoSignificantWeather(true)//
                .setTemperatures(Collections.emptyList());

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

        final TAFBaseForecastImpl expect = new TAFBaseForecastImpl.Builder()//
                .copyFrom(source)//
                .setTemperatures(builder.getTemperatures())//
                .build();

        // when
        builder.mergeFromTAFForecast(source);

        // then
        assertEquals(expect, builder.build());
    }

    @Test
    public void CAVOK_is_copied() {
        // given
        final TAFBaseForecastImpl.Builder builder = new TAFBaseForecastImpl.Builder();

        final TAFBaseForecastImpl source = new TAFBaseForecastImpl.Builder()//
                .setCeilingAndVisibilityOk(true)//
                .build();

        final TAFBaseForecastImpl expect = source;

        // when
        builder.mergeFromTAFForecast(source);

        // then
        assertEquals(expect, builder.build());
    }

    @Test
    public void NSW_is_not_copied() {
        // given
        final TAFBaseForecastImpl.Builder builder = new TAFBaseForecastImpl.Builder();

        final TAFBaseForecastImpl source = new TAFBaseForecastImpl.Builder()//
                .setNoSignificantWeather(true)//
                .build();

        final TAFBaseForecastImpl expect = new TAFBaseForecastImpl.Builder().mergeFrom(source)//
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
        final TAFBaseForecastImpl.Builder builder = new TAFBaseForecastImpl.Builder()//
                .setForecastWeather(Collections.emptyList())//
                .setNoSignificantWeather(true);

        final TAFBaseForecastImpl source = new TAFBaseForecastImpl.Builder()//
                .clearForecastWeather()//
                .setNoSignificantWeather(true)//
                .build();

        final TAFBaseForecastImpl expect = new TAFBaseForecastImpl.Builder()//
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
        final TAFBaseForecastImpl.Builder builder = new TAFBaseForecastImpl.Builder()//
                .setPrevailingVisibility(meters(4000.0));

        final TAFBaseForecastImpl source = new TAFBaseForecastImpl.Builder()//
                .setCeilingAndVisibilityOk(true)//
                .setPrevailingVisibility(meters(prevailingVisibilityMeters))//
                .build();

        final TAFBaseForecastImpl expect = source;

        // when
        builder.mergeFromTAFForecast(source);

        // then
        assertEquals(expect, builder.build());
    }

    @Test
    public void prevailingVisibilityOperator_is_copied_whenever_prevailingVisibility_is_copied() {
        // given
        final TAFBaseForecastImpl.Builder builder = new TAFBaseForecastImpl.Builder()//
                .setPrevailingVisibility(meters(10_000.0))//
                .setPrevailingVisibilityOperator(AviationCodeListUser.RelationalOperator.ABOVE);

        final TAFBaseForecastImpl source = new TAFBaseForecastImpl.Builder()//
                .setPrevailingVisibility(meters(8000.0))//
                .clearPrevailingVisibilityOperator()//
                .build();

        final TAFBaseForecastImpl expect = source;

        // when
        builder.mergeFromTAFForecast(source);

        // then
        assertEquals(expect, builder.build());
    }

    @Parameters(source = WeathersProvider.class)
    @Test
    public void forecastWeather_is_always_copied_on_CAVOK(@Nullable final List<Weather> weather) {
        // given
        final TAFBaseForecastImpl.Builder builder = new TAFBaseForecastImpl.Builder()//
                .setForecastWeather(Collections.singletonList(newWeather("VA")));

        final TAFBaseForecastImpl source = new TAFBaseForecastImpl.Builder()//
                .setCeilingAndVisibilityOk(true)//
                .setNullableForecastWeather(weather)//
                .build();

        final TAFBaseForecastImpl expect = source;

        // when
        builder.mergeFromTAFForecast(source);

        // then
        assertEquals(expect, builder.build());
    }

    @Parameters(source = WeathersProvider.class)
    @Test
    public void forecastWeather_is_always_copied_on_NSW(@Nullable final List<Weather> weather) {
        // given
        final TAFBaseForecastImpl.Builder builder = new TAFBaseForecastImpl.Builder()//
                .setForecastWeather(Collections.singletonList(newWeather("VA")));

        final TAFBaseForecastImpl source = new TAFBaseForecastImpl.Builder()//
                .setNoSignificantWeather(true)//
                .setNullableForecastWeather(weather)//
                .build();

        final TAFBaseForecastImpl expect = new TAFBaseForecastImpl.Builder()//
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
        final TAFBaseForecastImpl.Builder builder = new TAFBaseForecastImpl.Builder()//
                .setCloud(newCloudForecast(30.0));

        final TAFBaseForecastImpl source = new TAFBaseForecastImpl.Builder()//
                .setCeilingAndVisibilityOk(true)//
                .setNullableCloud(cloudForecast)//
                .build();

        final TAFBaseForecastImpl expect = source;

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
        public static class Builder extends TAFBaseForecastImpl_Builder_MergeFromTest_TestTAFForecast_Builder {

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
