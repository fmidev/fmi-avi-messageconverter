package fi.fmi.avi.converter.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fi.fmi.avi.converter.AviMessageConverter;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionIssue;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.converter.json.conf.JSONConverter;
import fi.fmi.avi.model.*;
import fi.fmi.avi.model.immutable.*;
import fi.fmi.avi.model.sigmet.SIGMET;
import fi.fmi.avi.model.sigmet.SigmetAnalysisType;
import fi.fmi.avi.model.sigmet.SigmetIntensityChange;
import fi.fmi.avi.model.sigmet.immutable.SIGMETImpl;
import fi.fmi.avi.model.sigmet.immutable.VAInfoImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.unitils.thirdparty.org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JSONVASigmetTestConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class JSONVASigmetConverterTest {

    @Autowired
    private AviMessageConverter converter;

    @Test
    public void testSIGMETParsing() throws Exception {
        final InputStream is = JSONVASigmetConverterTest.class.getResourceAsStream("vasigmet1.json");
        Objects.requireNonNull(is);
        final String input = IOUtils.toString(is, "UTF-8");
        is.close();
        final ConversionResult<SIGMET> result = converter.convertMessage(input,
                JSONConverter.JSON_STRING_TO_SIGMET_POJO, ConversionHints.EMPTY);
        for (final ConversionIssue iss : result.getConversionIssues()) {
            System.err.println("  ISS:" + iss.getMessage() + " " + iss.getCause());
        }
        assertSame(ConversionResult.Status.SUCCESS, result.getStatus());
    }

    @Test
    public void testSIGMETParsingNOVAEXP() throws Exception {
        final InputStream is = JSONVASigmetConverterTest.class.getResourceAsStream("vasigmet_novaexp.json");
        Objects.requireNonNull(is);
        final String input = IOUtils.toString(is, "UTF-8");
        is.close();
        final ConversionResult<SIGMET> result = converter.convertMessage(input,
                JSONConverter.JSON_STRING_TO_SIGMET_POJO, ConversionHints.EMPTY);
        for (final ConversionIssue iss : result.getConversionIssues()) {
            System.err.println("  ISS:" + iss.getMessage() + " " + iss.getCause());
        }
        assertSame(ConversionResult.Status.SUCCESS, result.getStatus());
    }

    @Test
    public void testVASIGMETSerialization() throws Exception {
        final ObjectMapper om = new ObjectMapper();
        om.registerModule(new Jdk8Module());
        om.registerModule(new JavaTimeModule());

        final InputStream is = JSONVASigmetConverterTest.class.getResourceAsStream("vasigmet1.json");
        Objects.requireNonNull(is);
        final String reference = IOUtils.toString(is, "UTF-8");
        is.close();

        final SIGMETImpl.Builder builder = SIGMETImpl.builder();

        final UnitPropertyGroup mwo = new UnitPropertyGroupImpl.Builder().setPropertyGroup("De Bilt", "EHDB", "MWO")
                .build();
        final UnitPropertyGroup fir = new UnitPropertyGroupImpl.Builder().setPropertyGroup("AMSTERDAM", "EHAA", "FIR")
                .build();

        final Airspace airspace = new AirspaceImpl.Builder().setDesignator("EHAA").setType(Airspace.AirspaceType.FIR)
                .setName("AMSTERDAM").build();

        final String geomString = "{ \"type\": \"Polygon\", \"exteriorRingPositions\":[5.0,52.0,6.0,53.0,4.0,54.0,5.0,52.0]}";
        final Geometry geom = om.readValue(geomString, Geometry.class);
        final String fpaGeomString = "{ \"type\": \"Polygon\", \"exteriorRingPositions\":[5.0,53.0,6.0,54.0,4.0,55.0,5.0,53.0]}";
        final Geometry fpaGeom = om.readValue(fpaGeomString, Geometry.class);

        final PhenomenonGeometryWithHeightImpl.Builder geomBuilder = PhenomenonGeometryWithHeightImpl.builder();
        geomBuilder.setGeometry(TacOrGeoGeometryImpl.of(geom));
        geomBuilder.setTime(PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2017-08-27T12:00:00Z")));
        geomBuilder.setLowerLimit(NumericMeasureImpl.of(10, "FL"));
        geomBuilder.setUpperLimit(NumericMeasureImpl.of(35, "FL"));
        geomBuilder.setApproximateLocation(false);
        geomBuilder.setIntensityChange(SigmetIntensityChange.NO_CHANGE);
        geomBuilder.setAnalysisType(SigmetAnalysisType.OBSERVATION);

        final PhenomenonGeometryImpl.Builder fpGeomBuilder = PhenomenonGeometryImpl.builder();
        fpGeomBuilder.setGeometry(TacOrGeoGeometryImpl.of(fpaGeom));
        fpGeomBuilder.setTime(PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2017-08-27T18:00:00Z")));
        fpGeomBuilder.setApproximateLocation(false);

        final PartialOrCompleteTimeInstant.Builder issueTimeBuilder = PartialOrCompleteTimeInstant.builder();
        issueTimeBuilder.setCompleteTime(ZonedDateTime.parse("2017-08-27T11:30:00Z"));
        final PartialOrCompleteTimePeriod.Builder validPeriod = PartialOrCompleteTimePeriod.builder();
        validPeriod.setStartTime(PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2017-08-27T11:30:00Z")));
        validPeriod.setEndTime(PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2017-08-27T18:00:00Z")));

        final VolcanoDescriptionImpl.Builder volcanoBuilder = new VolcanoDescriptionImpl.Builder();
        final ElevatedPointImpl.Builder gpBuilder = ElevatedPointImpl.builder();
        gpBuilder.addAllCoordinates(Arrays.stream(new Double[] { 52.0, 5.2 }));
        gpBuilder.setCrs(CoordinateReferenceSystemImpl.wgs84());
        volcanoBuilder.setVolcanoPosition(gpBuilder.build());
        volcanoBuilder.setVolcanoName("GRIMSVOTN");

        final VAInfoImpl.Builder vaInfoBuilder = new VAInfoImpl.Builder();
        vaInfoBuilder.setVolcano(volcanoBuilder.build());

        builder.setReportStatus(AviationWeatherMessage.ReportStatus.NORMAL)
                .setCancelMessage(false)
                .setMeteorologicalWatchOffice(mwo)
                .setIssuingAirTrafficServicesUnit(fir)
                .setAirspace(airspace)
                .setIssueTime(issueTimeBuilder.build())
                .setPermissibleUsage(AviationCodeListUser.PermissibleUsage.NON_OPERATIONAL)
                .setPermissibleUsageReason(AviationCodeListUser.PermissibleUsageReason.EXERCISE)
                .setSequenceNumber("1")
                .setTranslated(false)
                .setPhenomenonType(AviationCodeListUser.SigmetPhenomenonType.VOLCANIC_ASH_SIGMET)
                .setPhenomenon(AviationCodeListUser.AeronauticalSignificantWeatherPhenomenon.VA)
                .setValidityPeriod(validPeriod.build())

                .setAnalysisGeometries(Collections.singletonList(geomBuilder.build()))
                .setForecastGeometries(Collections.singletonList(fpGeomBuilder.build()))
                .setVAInfo(vaInfoBuilder.build());

        final SIGMET vaSigmet = builder.build();

        final ConversionResult<String> result = converter.convertMessage(vaSigmet,
                JSONConverter.SIGMET_POJO_TO_JSON_STRING, ConversionHints.EMPTY);
        assertSame(ConversionResult.Status.SUCCESS, result.getStatus());
        assertTrue(result.getConvertedMessage().isPresent());

        final SIGMET refVaSigmet = om.readValue(reference, SIGMETImpl.class);

        final SIGMET newVaSigmet = om.readValue(result.getConvertedMessage().get(), SIGMETImpl.class);

        final JsonNode refTree = om.readTree(reference);
        final JsonNode newTree = om.readTree(result.getConvertedMessage().get());

        assertEquals("Strings do not match ", refVaSigmet, newVaSigmet);

        assertEquals("Strings do not match ", refTree, newTree);

    }
}
