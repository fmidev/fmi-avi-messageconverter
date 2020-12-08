package fi.fmi.avi.converter.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.unitils.thirdparty.org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import fi.fmi.avi.converter.AviMessageConverter;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.converter.json.conf.JSONConverter;
import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.PartialDateTime;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.bulletin.DataTypeDesignatorT1;
import fi.fmi.avi.model.bulletin.DataTypeDesignatorT2;
import fi.fmi.avi.model.bulletin.GenericMeteorologicalBulletin;
import fi.fmi.avi.model.bulletin.immutable.BulletinHeadingImpl;
import fi.fmi.avi.model.immutable.AerodromeImpl;
import fi.fmi.avi.model.immutable.CloudForecastImpl;
import fi.fmi.avi.model.immutable.CloudLayerImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.SurfaceWindImpl;
import fi.fmi.avi.model.immutable.WeatherImpl;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JSONTestConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class JSONConverterTest {

    @Autowired
    private AviMessageConverter converter;

    @Test
    public void testTAFParsing() throws Exception {
        final InputStream is = JSONConverterTest.class.getResourceAsStream("taf1.json");
        Objects.requireNonNull(is);
        final String input = IOUtils.toString(is,"UTF-8");
        is.close();
        final ConversionResult<TAF> result = converter.convertMessage(input, JSONConverter.JSON_STRING_TO_TAF_POJO, ConversionHints.EMPTY);
        assertTrue(ConversionResult.Status.SUCCESS == result.getStatus());
    }

    @Test
    public void testMETARParsing() throws Exception {
        final InputStream is = JSONConverterTest.class.getResourceAsStream("metar1.json");
        Objects.requireNonNull(is);
        final String input = IOUtils.toString(is, "UTF-8");
        is.close();
        final ConversionResult<METAR> result = converter.convertMessage(input, JSONConverter.JSON_STRING_TO_METAR_POJO, ConversionHints.EMPTY);
        assertTrue(ConversionResult.Status.SUCCESS == result.getStatus());
    }

    @Test
    public void testSIGMETParsing() throws Exception {
        final InputStream is = JSONConverterTest.class.getResourceAsStream("sigmet1.json");
        Objects.requireNonNull(is);
        final String input = IOUtils.toString(is, "UTF-8");
        is.close();
        final ConversionResult<SIGMET> result = converter.convertMessage(input, JSONConverter.JSON_STRING_TO_SIGMET_POJO, ConversionHints.EMPTY);
        assertTrue(ConversionResult.Status.SUCCESS == result.getStatus());
    }

    @Test
    public void testTAFBulletinParsing() throws Exception {
        final InputStream is = JSONConverterTest.class.getResourceAsStream("tafBulletin1.json");
        Objects.requireNonNull(is);
        final String input = IOUtils.toString(is, "UTF-8");
        is.close();
        final ConversionResult<TAFBulletin> result = converter.convertMessage(input, JSONConverter.JSON_STRING_TO_TAF_BULLETIN_POJO, ConversionHints.EMPTY);
        assertTrue(ConversionResult.Status.SUCCESS == result.getStatus());
    }

    @Test
    public void testSIGMETBulletinParsing() throws Exception {
        final InputStream is = JSONConverterTest.class.getResourceAsStream("sigmetBulletin1.json");
        Objects.requireNonNull(is);
        final String input = IOUtils.toString(is, "UTF-8");
        is.close();
        final ConversionResult<SIGMETBulletin> result = converter.convertMessage(input, JSONConverter.JSON_STRING_TO_SIGMET_BULLETIN_POJO, ConversionHints.EMPTY);
        assertTrue(ConversionResult.Status.SUCCESS == result.getStatus());
    }

    @Test
    public void testGenericBulletinParsing() throws Exception {
        final InputStream is = JSONConverterTest.class.getResourceAsStream("generic-bulletin1.json");
        Objects.requireNonNull(is);
        final String input = IOUtils.toString(is, "UTF-8");
        is.close();
        final ConversionResult<GenericMeteorologicalBulletin> result = converter.convertMessage(input, JSONConverter.JSON_STRING_TO_GENERIC_BULLETIN_POJO, ConversionHints.EMPTY);
        assertTrue(ConversionResult.Status.SUCCESS == result.getStatus());
    }

    @Test
    public void testGenericCustomBulletinParsing() throws Exception {
        final InputStream is = JSONConverterTest.class.getResourceAsStream("custom-bulletin1.json");
        Objects.requireNonNull(is);
        final String input = IOUtils.toString(is, "UTF-8");
        is.close();
        final ConversionResult<GenericMeteorologicalBulletin> result = converter.convertMessage(input, JSONConverter.JSON_STRING_TO_GENERIC_BULLETIN_POJO, ConversionHints.EMPTY);
        assertTrue(ConversionResult.Status.SUCCESS == result.getStatus());
    }


    @Test
    public void testTAFSerialization() throws Exception {
        final InputStream is = JSONConverterTest.class.getResourceAsStream("taf1.json");
        Objects.requireNonNull(is);
        final ObjectMapper om = new ObjectMapper();
        om.registerModule(new Jdk8Module());
        om.registerModule(new JavaTimeModule());
        final JsonNode reference = om.readerFor(TAFImpl.class).readTree(is);
        is.close();

        final TAFImpl.Builder builder = TAFImpl.builder();
        builder.setStatus(AviationCodeListUser.TAFStatus.NORMAL)
                .setIssueTime(PartialOrCompleteTimeInstant.createIssueTime("271137Z"))
                .setAerodrome(AerodromeImpl.builder().setDesignator("EFVA").build())
                .setValidityTime(PartialOrCompleteTimePeriod.createValidityTime("2712/2812"))
                .setBaseForecast(TAFBaseForecastImpl.builder()
                        .setForecastWeather(WeatherImpl.fromCodes("-RA"))
                        .setPrevailingVisibility(NumericMeasureImpl.of(8000.0, "m"))//
                        .setSurfaceWind(SurfaceWindImpl.builder()
                                .setMeanWindDirection(NumericMeasureImpl.of(140,"deg"))
                                .setMeanWindSpeed(NumericMeasureImpl.of(15.0, "[kn_i]"))
                                .setWindGust(NumericMeasureImpl.of(25.0, "[kn_i]"))
                                .build())
                        .setCloud(CloudForecastImpl.builder()
                                .setLayers(Arrays.asList(CloudLayerImpl.builder()
                                        .setBase(NumericMeasureImpl.of(2000, "[ft_i]"))
                                        .setAmount(AviationCodeListUser.CloudAmount.SCT)
                                        .build(),
                                        CloudLayerImpl.builder()
                                                .setBase(NumericMeasureImpl.of(5000, "[ft_i]"))
                                                .setAmount(AviationCodeListUser.CloudAmount.OVC)
                                                .build()))
                                .build()
                                )
                        .build());
        builder.setChangeForecasts(Arrays.asList(
                TAFChangeForecastImpl.builder()
                        .setForecastWeather(WeatherImpl.fromCodes("-RA"))
                        .setChangeIndicator(AviationCodeListUser.TAFChangeIndicator.BECOMING)
                        .setPeriodOfChange(PartialOrCompleteTimePeriod.createValidityTimeDHDH("2715/2717"))
                        .setPrevailingVisibility(NumericMeasureImpl.of(5000.0, "m"))
                        .setCloud(CloudForecastImpl.builder()
                                .setLayers(Collections.singletonList(CloudLayerImpl.builder().setBase(NumericMeasureImpl.of(700, "[ft_i]"))
                                        .setAmount(AviationCodeListUser.CloudAmount.BKN)
                                        .build()))
                                .build()
                        )
                        .build(),
                TAFChangeForecastImpl.builder()
                        .setForecastWeather(WeatherImpl.fromCodes("RASN"))
                        .setChangeIndicator(AviationCodeListUser.TAFChangeIndicator.PROBABILITY_40)
                        .setPeriodOfChange(PartialOrCompleteTimePeriod.createValidityTimeDHDH("2715/2720"))
                        .setPrevailingVisibility(NumericMeasureImpl.of(4000.0, "m"))
                        .build(),
                TAFChangeForecastImpl.builder()
                        .setChangeIndicator(AviationCodeListUser.TAFChangeIndicator.BECOMING)
                        .setPeriodOfChange(PartialOrCompleteTimePeriod.createValidityTimeDHDH("2720/2722"))//
                        .setSurfaceWind(SurfaceWindImpl.builder()
                                .setMeanWindDirection(NumericMeasureImpl.of(160, "deg"))
                                .setMeanWindSpeed(NumericMeasureImpl.of(12.0, "[kn_i]"))
                                .build())
                        .build(),
                TAFChangeForecastImpl.builder()
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
                                .setLayers(Collections.singletonList(CloudLayerImpl.builder().setBase(NumericMeasureImpl.of(400, "[ft_i]"))
                                        .setAmount(AviationCodeListUser.CloudAmount.BKN)
                                        .build())).build()).build()));

        final ConversionResult<String> result = converter.convertMessage(builder.build(), JSONConverter.TAF_POJO_TO_JSON_STRING, ConversionHints.EMPTY);
        assertTrue(ConversionResult.Status.SUCCESS == result.getStatus());
        assertTrue(result.getConvertedMessage().isPresent());
        final JsonNode resultJSON = om.readTree(result.getConvertedMessage().get());
        assertEquals(reference, resultJSON);
    }

    @Test
    public void testSIGMETBulletinSerialization() throws Exception {
        final InputStream is = JSONConverterTest.class.getResourceAsStream("sigmet1.json");
        Objects.requireNonNull(is);
        final String reference = IOUtils.toString(is, "UTF-8");
        is.close();
        final ConversionResult<SIGMET> result = converter.convertMessage(reference, JSONConverter.JSON_STRING_TO_SIGMET_POJO, ConversionHints.EMPTY);
        System.err.println(result.getStatus()+" "+result.getConversionIssues());
        System.err.println("sigmet:"+result.getConvertedMessage().get());

        final SIGMETBulletinImpl.Builder builder = SIGMETBulletinImpl.builder()//
                .setHeading(BulletinHeadingImpl.builder()//
                        .setGeographicalDesignator("FI")//
                        .setLocationIndicator("EFKL")//
                        .setBulletinNumber(31)//
                        .setDataTypeDesignatorT1ForTAC(DataTypeDesignatorT1.WARNINGS)
                        .setDataTypeDesignatorT2(DataTypeDesignatorT2.WarningsDataTypeDesignatorT2.WRN_SIGMET)//
                        .setIssueTime(PartialOrCompleteTimeInstant.of(PartialDateTime.ofDayHourMinute(2, 5, 0)))//
                        .build());

        builder.addMessages(SIGMETImpl.Builder.from(result.getConvertedMessage().get())
                .setTranslatedTAC("EFIN SIGMET 1 VALID 170750/170950 EFKL-\n"//
                        + "EFIN FINLAND FIR SEV TURB FCST AT 0740Z\n"//
                        + "S OF LINE N5953 E01931 -\n"//
                        + "N6001 E02312 - N6008 E02606 - N6008\n"//
                        + "E02628 FL220-340 MOV N 15KT\n"//
                        + "WKN=").setTranslated(false).build());
        final SIGMETBulletin msg = builder.build();

        final ConversionResult<String> jsonResult = this.converter.convertMessage(msg, JSONConverter.SIGMET_BULLETIN_POJO_TO_JSON_STRING, ConversionHints.EMPTY);
        assertEquals(ConversionResult.Status.SUCCESS, jsonResult.getStatus());

        TestCase.assertTrue(jsonResult.getConvertedMessage().isPresent());
        //TODO: better testing of the content

    }
}
