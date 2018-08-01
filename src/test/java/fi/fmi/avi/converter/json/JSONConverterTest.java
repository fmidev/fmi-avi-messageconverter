package fi.fmi.avi.converter.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Objects;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.unitils.thirdparty.org.apache.commons.io.IOUtils;

import fi.fmi.avi.converter.AviMessageConverter;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.converter.json.conf.JSONConverter;
import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.immutable.AerodromeImpl;
import fi.fmi.avi.model.immutable.CloudForecastImpl;
import fi.fmi.avi.model.immutable.CloudLayerImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.WeatherImpl;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.immutable.TAFBaseForecastImpl;
import fi.fmi.avi.model.taf.immutable.TAFChangeForecastImpl;
import fi.fmi.avi.model.taf.immutable.TAFImpl;
import fi.fmi.avi.model.taf.immutable.TAFSurfaceWindImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JSONTestConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class JSONConverterTest {

    @Autowired
    private AviMessageConverter converter;

    @Test
    public void testTAFParsing() throws Exception {
        InputStream is = JSONConverterTest.class.getResourceAsStream("taf1.json");
        Objects.nonNull(is);
        String input = IOUtils.toString(is,"UTF-8");
        is.close();
        ConversionResult<TAF> result = converter.convertMessage(input, JSONConverter.JSON_STRING_TO_TAF_POJO, ConversionHints.EMPTY);
        assertTrue(ConversionResult.Status.SUCCESS == result.getStatus());
    }

    @Test
    public void testTAFSerialization() throws Exception {
        InputStream is = JSONConverterTest.class.getResourceAsStream("taf1.json");
        Objects.nonNull(is);
        String reference = IOUtils.toString(is,"UTF-8");
        is.close();

        TAFImpl.Builder builder = new TAFImpl.Builder();
        builder.setStatus(AviationCodeListUser.TAFStatus.NORMAL)
                .setIssueTime(PartialOrCompleteTimeInstant.createIssueTime("271137Z"))
                .setAerodrome(new AerodromeImpl.Builder().setDesignator("EFVA").build())
                .setValidityTime(PartialOrCompleteTimePeriod.createValidityTime("2712/2812"))
                .setBaseForecast(new TAFBaseForecastImpl.Builder()
                        .setForecastWeather(WeatherImpl.fromCodes("-RA"))
                        .setPrevailingVisibility(NumericMeasureImpl.of(8000.0, "m"))
                        .setSurfaceWind(new TAFSurfaceWindImpl.Builder()
                                .setMeanWindDirection(NumericMeasureImpl.of(140,"deg"))
                                .setMeanWindSpeed(NumericMeasureImpl.of(15.0, "[kn_i]"))
                                .setWindGust(NumericMeasureImpl.of(25.0, "[kn_i]"))
                                .build())
                        .setCloud(new CloudForecastImpl.Builder()
                                .setLayers(Arrays.asList(new CloudLayerImpl.Builder()
                                        .setBase(NumericMeasureImpl.of(2000, "[ft_i]"))
                                        .setAmount(AviationCodeListUser.CloudAmount.SCT)
                                        .build(),
                                        new CloudLayerImpl.Builder()
                                                .setBase(NumericMeasureImpl.of(5000, "[ft_i]"))
                                                .setAmount(AviationCodeListUser.CloudAmount.OVC)
                                                .build()))
                                .build()
                                )
                        .build());
        builder.setChangeForecasts(Arrays.asList(
                new TAFChangeForecastImpl.Builder()
                        .setForecastWeather(WeatherImpl.fromCodes("-RA"))
                        .setChangeIndicator(AviationCodeListUser.TAFChangeIndicator.BECOMING)
                        .setPeriodOfChange(PartialOrCompleteTimePeriod.createValidityTimeDHDH("2715/2717"))
                        .setPrevailingVisibility(NumericMeasureImpl.of(5000.0, "m"))
                        .setCloud(new CloudForecastImpl.Builder()
                                .setLayers(Arrays.asList(new CloudLayerImpl.Builder()
                                                .setBase(NumericMeasureImpl.of(700, "[ft_i]"))
                                                .setAmount(AviationCodeListUser.CloudAmount.BKN)
                                                .build()))
                                .build()
                        )
                        .build(),
                new TAFChangeForecastImpl.Builder()
                        .setForecastWeather(WeatherImpl.fromCodes("RASN"))
                        .setChangeIndicator(AviationCodeListUser.TAFChangeIndicator.PROBABILITY_40)
                        .setPeriodOfChange(PartialOrCompleteTimePeriod.createValidityTimeDHDH("2715/2720"))
                        .setPrevailingVisibility(NumericMeasureImpl.of(4000.0, "m"))
                        .build(),
                new TAFChangeForecastImpl.Builder()
                        .setChangeIndicator(AviationCodeListUser.TAFChangeIndicator.BECOMING)
                        .setPeriodOfChange(PartialOrCompleteTimePeriod.createValidityTimeDHDH("2720/2722"))
                        .setSurfaceWind(new TAFSurfaceWindImpl.Builder()
                                .setMeanWindDirection(NumericMeasureImpl.of(160, "deg"))
                                .setMeanWindSpeed(NumericMeasureImpl.of(12.0, "[kn_i]"))
                                .build())
                        .build(),
                new TAFChangeForecastImpl.Builder()
                        .setChangeIndicator(AviationCodeListUser.TAFChangeIndicator.TEMPORARY_FLUCTUATIONS)
                        .setPeriodOfChange(PartialOrCompleteTimePeriod.createValidityTimeDHDH("2720/2724"))
                        .setPrevailingVisibility(NumericMeasureImpl.of(8000.0, "m"))
                        .build(),

                new TAFChangeForecastImpl.Builder()
                        .setForecastWeather(WeatherImpl.fromCodes("RASN"))
                        .setChangeIndicator(AviationCodeListUser.TAFChangeIndicator.PROBABILITY_40)
                        .setPeriodOfChange(PartialOrCompleteTimePeriod.createValidityTimeDHDH("2802/2806"))
                        .setPrevailingVisibility(NumericMeasureImpl.of(3000.0, "m"))
                        .setCloud(new CloudForecastImpl.Builder()
                                .setLayers(Arrays.asList(new CloudLayerImpl.Builder()
                                        .setBase(NumericMeasureImpl.of(400, "[ft_i]"))
                                        .setAmount(AviationCodeListUser.CloudAmount.BKN)
                                        .build()))
                                .build()
                        )
                        .build())
        );

        ConversionResult<String> result = converter.convertMessage(builder.build(), JSONConverter.TAF_POJO_TO_JSON_STRING, ConversionHints.EMPTY);
        assertTrue(ConversionResult.Status.SUCCESS == result.getStatus());
        assertTrue(result.getConvertedMessage().isPresent());

        BufferedReader refReader = new BufferedReader(new StringReader(reference));
        BufferedReader resultReader = new BufferedReader(new StringReader(result.getConvertedMessage().get()));
        String line = null;
        int lineNo = 0;
        while ((line = refReader.readLine()) != null) {
            lineNo++;
            assertEquals("Line " + lineNo + " does not match", line, resultReader.readLine());
        }
        assertTrue(resultReader.readLine() == null);
    }

}
