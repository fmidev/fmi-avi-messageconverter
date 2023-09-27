package fi.fmi.avi.converter.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fi.fmi.avi.converter.AviMessageConverter;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.converter.json.conf.JSONConverter;
import fi.fmi.avi.model.*;
import fi.fmi.avi.model.bulletin.DataTypeDesignatorT1;
import fi.fmi.avi.model.bulletin.DataTypeDesignatorT2;
import fi.fmi.avi.model.bulletin.GenericMeteorologicalBulletin;
import fi.fmi.avi.model.bulletin.immutable.BulletinHeadingImpl;
import fi.fmi.avi.model.immutable.*;
import fi.fmi.avi.model.metar.METAR;
import fi.fmi.avi.model.sigmet.SIGMET;
import fi.fmi.avi.model.sigmet.SIGMETBulletin;
import fi.fmi.avi.model.sigmet.immutable.SIGMETBulletinImpl;
import fi.fmi.avi.model.sigmet.immutable.SIGMETImpl;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.TAFBulletin;
import fi.fmi.avi.model.taf.immutable.TAFBaseForecastImpl;
import fi.fmi.avi.model.taf.immutable.TAFChangeForecastImpl;
import fi.fmi.avi.model.taf.immutable.TAFImpl;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.unitils.thirdparty.org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JSONTestConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class JSONConverterTest {

    @Autowired
    private AviMessageConverter converter;

    private static void assertSuccess(final ConversionResult<?> result) {
        assertEquals("Expected SUCCESS, but had issues: " + result.getConversionIssues(), //
                ConversionResult.Status.SUCCESS, result.getStatus());
    }

    private static String readResource(final String resourceName) throws IOException {
        try (InputStream inputStream = JSONConverterTest.class.getResourceAsStream(resourceName)) {
            requireNonNull(inputStream, "inputStream");
            return IOUtils.toString(inputStream, "UTF-8");
        }
    }

    @Test
    public void testTAFParsing() throws Exception {
        final String input = readResource("taf1.json");
        final ConversionResult<TAF> result = converter.convertMessage(input, JSONConverter.JSON_STRING_TO_TAF_POJO, ConversionHints.EMPTY);
        assertSuccess(result);
    }

    @Test
    public void testMETARParsing() throws Exception {
        final String input = readResource("metar1.json");
        final ConversionResult<METAR> result = converter.convertMessage(input, JSONConverter.JSON_STRING_TO_METAR_POJO, ConversionHints.EMPTY);
        assertSuccess(result);
    }

    @Test
    public void testSIGMETParsing() throws Exception {
        final String input = readResource("sigmet1.json");
        final ConversionResult<SIGMET> result = converter.convertMessage(input, JSONConverter.JSON_STRING_TO_SIGMET_POJO, ConversionHints.EMPTY);
        assertSuccess(result);
    }

    @Test
    public void testTAFBulletinParsing() throws Exception {
        final String input = readResource("tafBulletin1.json");
        final ConversionResult<TAFBulletin> result = converter.convertMessage(input, JSONConverter.JSON_STRING_TO_TAF_BULLETIN_POJO, ConversionHints.EMPTY);
        assertSuccess(result);
    }

    @Test
    public void testSIGMETBulletinParsing() throws Exception {
        final String input = readResource("sigmetBulletin1.json");
        final ConversionResult<SIGMETBulletin> result = converter.convertMessage(input, JSONConverter.JSON_STRING_TO_SIGMET_BULLETIN_POJO,
                ConversionHints.EMPTY);
        assertSuccess(result);
    }

    @Test
    public void testGenericBulletinParsing() throws Exception {
        final String input = readResource("generic-bulletin1.json");
        final ConversionResult<GenericMeteorologicalBulletin> result = converter.convertMessage(input, JSONConverter.JSON_STRING_TO_GENERIC_BULLETIN_POJO,
                ConversionHints.EMPTY);
        assertSuccess(result);
    }

    @Test
    public void testGenericCustomBulletinParsing() throws Exception {
        final String input = readResource("custom-bulletin1.json");
        final ConversionResult<GenericMeteorologicalBulletin> result = converter.convertMessage(input, JSONConverter.JSON_STRING_TO_GENERIC_BULLETIN_POJO,
                ConversionHints.EMPTY);
        assertSuccess(result);
    }

    @Test
    public void testTAFSerialization() throws Exception {
        final ObjectMapper om;
        final JsonNode reference;
        try (InputStream is = JSONConverterTest.class.getResourceAsStream("taf1.json")) {
            requireNonNull(is);
            om = new ObjectMapper();
            om.registerModule(new Jdk8Module());
            om.registerModule(new JavaTimeModule());
            reference = om.readerFor(TAFImpl.class).readTree(is);
        }

        final TAFImpl.Builder builder = TAFImpl.builder();
        builder.setReportStatus(AviationWeatherMessage.ReportStatus.NORMAL)
                .setIssueTime(PartialOrCompleteTimeInstant.createIssueTime("271137Z"))
                .setAerodrome(AerodromeImpl.builder().setDesignator("EFVA").build())
                .setValidityTime(PartialOrCompleteTimePeriod.createValidityTime("2712/2812"))
                .setBaseForecast(TAFBaseForecastImpl.builder()
                        .setForecastWeather(WeatherImpl.fromCodes("-RA"))
                        .setPrevailingVisibility(NumericMeasureImpl.of(8000.0, "m"))//
                        .setSurfaceWind(SurfaceWindImpl.builder()
                                .setMeanWindDirection(NumericMeasureImpl.of(140, "deg"))
                                .setMeanWindSpeed(NumericMeasureImpl.of(15.0, "[kn_i]"))
                                .setWindGust(NumericMeasureImpl.of(25.0, "[kn_i]"))
                                .build())
                        .setCloud(CloudForecastImpl.builder()
                                .setLayers(Arrays.asList(CloudLayerImpl.builder()
                                        .setBase(NumericMeasureImpl.of(2000, "[ft_i]"))
                                        .setAmount(AviationCodeListUser.CloudAmount.SCT)
                                        .build(), CloudLayerImpl.builder()
                                        .setBase(NumericMeasureImpl.of(5000, "[ft_i]"))
                                        .setAmount(AviationCodeListUser.CloudAmount.OVC)
                                        .build()))
                                .build())
                        .build());
        builder.setChangeForecasts(Arrays.asList(TAFChangeForecastImpl.builder()
                        .setForecastWeather(WeatherImpl.fromCodes("-RA"))
                        .setChangeIndicator(AviationCodeListUser.TAFChangeIndicator.BECOMING)
                        .setPeriodOfChange(PartialOrCompleteTimePeriod.createValidityTimeDHDH("2715/2717"))
                        .setPrevailingVisibility(NumericMeasureImpl.of(5000.0, "m"))
                        .setCloud(CloudForecastImpl.builder()
                                .setLayers(Collections.singletonList(
                                        CloudLayerImpl.builder().setBase(NumericMeasureImpl.of(700, "[ft_i]")).setAmount(AviationCodeListUser.CloudAmount.BKN).build()))
                                .build())
                        .build(), TAFChangeForecastImpl.builder()
                        .setForecastWeather(WeatherImpl.fromCodes("RASN"))
                        .setChangeIndicator(AviationCodeListUser.TAFChangeIndicator.PROBABILITY_40)
                        .setPeriodOfChange(PartialOrCompleteTimePeriod.createValidityTimeDHDH("2715/2720"))
                        .setPrevailingVisibility(NumericMeasureImpl.of(4000.0, "m"))
                        .build(), TAFChangeForecastImpl.builder()
                        .setChangeIndicator(AviationCodeListUser.TAFChangeIndicator.BECOMING)
                        .setPeriodOfChange(PartialOrCompleteTimePeriod.createValidityTimeDHDH("2720/2722"))//
                        .setSurfaceWind(SurfaceWindImpl.builder()
                                .setMeanWindDirection(NumericMeasureImpl.of(160, "deg"))
                                .setMeanWindSpeed(NumericMeasureImpl.of(12.0, "[kn_i]"))
                                .build())
                        .build(), TAFChangeForecastImpl.builder()
                        .setChangeIndicator(AviationCodeListUser.TAFChangeIndicator.TEMPORARY_FLUCTUATIONS)
                        .setPeriodOfChange(PartialOrCompleteTimePeriod.createValidityTimeDHDH("2720/2724"))
                        .setPrevailingVisibility(NumericMeasureImpl.of(8000.0, "m"))
                        .build(),

                TAFChangeForecastImpl.builder()
                        .setForecastWeather(WeatherImpl.fromCodes("RASN"))
                        .setChangeIndicator(AviationCodeListUser.TAFChangeIndicator.PROBABILITY_40)
                        .setPeriodOfChange(PartialOrCompleteTimePeriod.createValidityTimeDHDH("2802/2806"))
                        .setPrevailingVisibility(NumericMeasureImpl.of(3000.0, "m"))
                        .setCloud(CloudForecastImpl.builder()
                                .setLayers(Collections.singletonList(CloudLayerImpl.builder()
                                        .setBase(NumericMeasureImpl.of(400, "[ft_i]"))
                                        .setAmount(AviationCodeListUser.CloudAmount.BKN)
                                        .build()))
                                .build())
                        .build()));

        final ConversionResult<String> result = converter.convertMessage(builder.build(), JSONConverter.TAF_POJO_TO_JSON_STRING, ConversionHints.EMPTY);
        assertSuccess(result);
        assertTrue(result.getConvertedMessage().isPresent());
        final JsonNode resultJSON = om.readTree(result.getConvertedMessage().get());
        assertEquals(reference, resultJSON);
    }

    @Test
    public void testSIGMETBulletinSerialization() throws Exception {
        final String reference = readResource("sigmet1.json");
        final ConversionResult<SIGMET> result = converter.convertMessage(reference, JSONConverter.JSON_STRING_TO_SIGMET_POJO, ConversionHints.EMPTY);
        assertSuccess(result);

        final SIGMETBulletinImpl.Builder builder = SIGMETBulletinImpl.builder()//
                .setHeading(BulletinHeadingImpl.builder()//
                        .setGeographicalDesignator("FI")//
                        .setLocationIndicator("EFKL")//
                        .setBulletinNumber(31)//
                        .setDataTypeDesignatorT1ForTAC(DataTypeDesignatorT1.WARNINGS)
                        .setDataTypeDesignatorT2(DataTypeDesignatorT2.WarningsDataTypeDesignatorT2.WRN_SIGMET)//
                        .setIssueTime(PartialOrCompleteTimeInstant.of(PartialDateTime.ofDayHourMinute(2, 5, 0)))//
                        .build());

        builder.addMessages(SIGMETImpl.Builder.from(result.getConvertedMessage().get()).setTranslatedTAC("EFIN SIGMET 1 VALID 170750/170950 EFKL-\n"//
                + "EFIN FINLAND FIR SEV TURB FCST AT 0740Z\n"//
                + "S OF LINE N5953 E01931 -\n"//
                + "N6001 E02312 - N6008 E02606 - N6008\n"//
                + "E02628 FL220-340 MOV N 15KT\n"//
                + "WKN=").setTranslated(false).build());
        final SIGMETBulletin msg = builder.build();

        final ConversionResult<String> jsonResult = this.converter.convertMessage(msg, JSONConverter.SIGMET_BULLETIN_POJO_TO_JSON_STRING,
                ConversionHints.EMPTY);
        assertEquals(ConversionResult.Status.SUCCESS, jsonResult.getStatus());

        TestCase.assertTrue(jsonResult.getConvertedMessage().isPresent());
        //TODO: better testing of the content

    }
}
