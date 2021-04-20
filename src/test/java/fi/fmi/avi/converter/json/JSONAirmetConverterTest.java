package fi.fmi.avi.converter.json;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Objects;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.unitils.thirdparty.org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import fi.fmi.avi.converter.AviMessageConverter;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.converter.json.conf.JSONConverter;
import fi.fmi.avi.model.Airspace;
import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.model.Geometry;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.UnitPropertyGroup;
import fi.fmi.avi.model.immutable.AirspaceImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.PhenomenonGeometryWithHeightImpl;
import fi.fmi.avi.model.immutable.TacOrGeoGeometryImpl;
import fi.fmi.avi.model.immutable.UnitPropertyGroupImpl;
import fi.fmi.avi.model.sigmet.AIRMET;
import fi.fmi.avi.model.sigmet.SigmetAnalysisType;
import fi.fmi.avi.model.sigmet.SigmetIntensityChange;
import fi.fmi.avi.model.sigmet.immutable.AIRMETImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JSONAirmetTestConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class JSONAirmetConverterTest {

    @Autowired
    ObjectMapper om;
    @Autowired
    private AviMessageConverter converter;

    @Test
    public void testAIRMETParsing() throws Exception {
        final InputStream is = JSONAirmetConverterTest.class.getResourceAsStream("airmet2.json");
        Objects.requireNonNull(is);
        final String input = IOUtils.toString(is, "UTF-8");
        is.close();
        final ConversionResult<AIRMET> result = converter.convertMessage(input, JSONConverter.JSON_STRING_TO_AIRMET_POJO, ConversionHints.EMPTY);
        System.err.println("SM:" + result.getStatus() + " ==>");
        System.err.println("==>" + result.getConvertedMessage().get().getSequenceNumber());
        assertSame(ConversionResult.Status.SUCCESS, result.getStatus());
    }

    @Test
    public void testAIRMETparse() throws Exception {
        final InputStream is = JSONAirmetConverterTest.class.getResourceAsStream("airmet_moving.json");
        Objects.requireNonNull(is);
        final String reference = IOUtils.toString(is, "UTF-8");
        is.close();
        final AIRMET am = om.readValue(reference, AIRMETImpl.Builder.class).build();
        System.err.println("am:" + am);
    }

    @Test
    public void testAIRMETPointparse() throws Exception {
        final InputStream is = JSONAirmetConverterTest.class.getResourceAsStream("airmet_point_moving.json");
        Objects.requireNonNull(is);
        final String reference = IOUtils.toString(is, "UTF-8");
        is.close();
        final AIRMET am = om.readValue(reference, AIRMETImpl.Builder.class).build();
        System.err.println("am:" + am);
    }

    @Test
    public void testAIRMETSerialization() throws Exception {
/*        ObjectMapper om = new ObjectMapper();
        om.registerModule(new Jdk8Module());
        om.registerModule(new JavaTimeModule());
        om.registerModule(new JtsModule());
        om.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);*/

        final InputStream is = JSONAirmetConverterTest.class.getResourceAsStream("airmet2.json");
        Objects.requireNonNull(is);
        final String reference = IOUtils.toString(is, "UTF-8");
        is.close();
        final AIRMET am = om.readValue(reference, AIRMETImpl.Builder.class).build();
        System.err.println("am:" + am);

        final AIRMETImpl.Builder builder = AIRMETImpl.builder();

        final UnitPropertyGroup mwo = new UnitPropertyGroupImpl.Builder().setPropertyGroup("De Bilt", "EHDB", "MWO").build();
        final UnitPropertyGroup fir = new UnitPropertyGroupImpl.Builder().setPropertyGroup("AMSTERDAM FIR", "EHAA", "FIR").build();

        final Airspace airspace = new AirspaceImpl.Builder().setDesignator("EHAA").setType(Airspace.AirspaceType.FIR).setName("AMSTERDAM").build();

        final String geomString = "{\"type\": \"Polygon\", \"exteriorRingPositions\": [5.0,52.0,6.0,53.0,4.0,54.0,5.0,52.0]}";
        final Geometry geom = om.readValue(geomString, Geometry.class);

        final PartialOrCompleteTimeInstant.Builder issueTimeBuilder = PartialOrCompleteTimeInstant.builder();
        issueTimeBuilder.setCompleteTime(ZonedDateTime.parse("2017-08-27T11:30:00Z"));
        final PartialOrCompleteTimePeriod.Builder validPeriod = PartialOrCompleteTimePeriod.builder();
        validPeriod.setStartTime(PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2017-08-27T11:30:00Z")));
        validPeriod.setEndTime(PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2017-08-27T18:00:00Z")));

        final PhenomenonGeometryWithHeightImpl.Builder geomBuilder = new PhenomenonGeometryWithHeightImpl.Builder();
        geomBuilder.setApproximateLocation(false);
        geomBuilder.setGeometry(TacOrGeoGeometryImpl.of(geom));
        geomBuilder.setTime(PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2017-08-27T12:00:00Z")));
        geomBuilder.setLowerLimit(NumericMeasureImpl.of(10, "FL"));
        geomBuilder.setUpperLimit(NumericMeasureImpl.of(35, "FL"));
        geomBuilder.setMovingDirection(NumericMeasureImpl.of(180, "deg"))
        .setMovingSpeed(NumericMeasureImpl.of(10, "[kn_i]"))
        .setIntensityChange(SigmetIntensityChange.NO_CHANGE)
        .setAnalysisType(SigmetAnalysisType.OBSERVATION);

        builder.setReportStatus(AviationWeatherMessage.ReportStatus.NORMAL)
                .setCancelMessage(false)
                .setMeteorologicalWatchOffice(mwo)
                .setIssuingAirTrafficServicesUnit(fir)
                .setAirspace(airspace)
                .setIssueTime(issueTimeBuilder.build())
                .setTranslated(false)
                .setPermissibleUsage(AviationCodeListUser.PermissibleUsage.NON_OPERATIONAL)
                .setPermissibleUsageReason(AviationCodeListUser.PermissibleUsageReason.EXERCISE)
                .setSequenceNumber("1")
                .setValidityPeriod(validPeriod.build())
                .setAirmetPhenomenon(AviationCodeListUser.AeronauticalAirmetWeatherPhenomenon.MOD_ICE)
                .setAnalysisGeometries(Collections.singletonList(geomBuilder.build()));

        final AIRMET airmet = builder.build();

        final AIRMET airmetCopy = AIRMETImpl.immutableCopyOf(airmet);

        System.err.println(om.writeValueAsString(airmetCopy));

        final ConversionResult<String> result = converter.convertMessage(airmet, JSONConverter.AIRMET_POJO_TO_JSON_STRING, ConversionHints.EMPTY);
        assertSame(ConversionResult.Status.SUCCESS, result.getStatus());
        assertTrue(result.getConvertedMessage().isPresent());
        System.err.println("JSON:" + result.getConvertedMessage().get());

        //        JsonNode refRoot=om.readTree(reference);
        //        JsonNode convertedRoot=om.readTree(result.getConvertedMessage().get());
        //        System.err.println("EQUALS: "+refRoot.equals(convertedRoot));
        //        AIRMET convertedAirmet=om.readValue(result.getConvertedMessage().get(), AIRMETImpl.class);
        //        System.err.println("translated: "+convertedAirmet.isTranslated());

        //        ZonedDateTime now=ZonedDateTime.now();
        //        System.err.println("now: "+now+" "+convertedAirmet.getIssueTime());
        //        assertEquals("constructed and parsed tree not equal", airmet, convertedAirmet);

    }
}
