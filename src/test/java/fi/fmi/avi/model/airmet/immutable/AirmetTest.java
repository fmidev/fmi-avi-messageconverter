package fi.fmi.avi.model.airmet.immutable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Optional;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import fi.fmi.avi.model.Airspace;
import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.model.Geometry;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.PhenomenonGeometryWithHeight;
import fi.fmi.avi.model.immutable.AirspaceImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.PhenomenonGeometryWithHeightImpl;
import fi.fmi.avi.model.immutable.TacOrGeoGeometryImpl;
import fi.fmi.avi.model.immutable.UnitPropertyGroupImpl;
import fi.fmi.avi.model.sigmet.AIRMET;
import fi.fmi.avi.model.sigmet.SigmetAnalysisType;
import fi.fmi.avi.model.sigmet.immutable.AIRMETImpl;
import fi.fmi.avi.model.sigmet.immutable.AirmetCloudLevelsImpl;

public class AirmetTest {

    private static final String TEST_GEO_JSON_1 = "{\"type\":\"Polygon\",\"exteriorRingPositions\":[0,52,0,60,10,60,10,52,0,52]}}";
    private static final String TEST_GEO_JSON_2 = "{\"type\":\"Polygon\",\"exteriorRingPositions\":[0,52,0,60,5,60,5,52,0,52]}}";
    private final ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule())
            .registerModule(new Jdk8Module())
            .enable(SerializationFeature.INDENT_OUTPUT);

    public PhenomenonGeometryWithHeight getAnalysis() {
        Optional<Geometry> anGeometry = Optional.empty();

        try {
            anGeometry = Optional.ofNullable(om.readValue(TEST_GEO_JSON_1, Geometry.class));
        } catch (final IOException e) {
            e.printStackTrace();
        }

        final PhenomenonGeometryWithHeightImpl.Builder an = new PhenomenonGeometryWithHeightImpl.Builder().setTime(
                PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2018-10-22T13:50:00Z")))
                .setGeometry(TacOrGeoGeometryImpl.of(anGeometry.get()))
                .setApproximateLocation(false)
                .setAnalysisType(SigmetAnalysisType.OBSERVATION)
                ;
        return an.build();
    }

    public AIRMET buildAirmet() {
        final AirmetCloudLevelsImpl.Builder levels = new AirmetCloudLevelsImpl.Builder().setCloudBase(NumericMeasureImpl.of(0, "SFC"))
                .setCloudTop(NumericMeasureImpl.of(7000, "[ft_i]"));

        final Airspace airspace = new AirspaceImpl.Builder().setDesignator("EHAA").setType(Airspace.AirspaceType.FIR).setName("AMSTERDAM").build();

        final AIRMETImpl.Builder sm = AIRMETImpl.builder()
                .setIssueTime(PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2018-10-22T14:00:00Z")))
                .setIssuingAirTrafficServicesUnit(new UnitPropertyGroupImpl.Builder().setPropertyGroup("AMSTERDAM", "EHAA", "FIR").build())
                .setMeteorologicalWatchOffice(new UnitPropertyGroupImpl.Builder().setPropertyGroup("De Bilt", "EHDB", "MWO").build())
                .setAirspace(airspace)
                .setSequenceNumber("1")
                .setReportStatus(AviationWeatherMessage.ReportStatus.NORMAL)
                .setValidityPeriod(PartialOrCompleteTimePeriod.builder()
                        .setStartTime(PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2018-10-22T14:00:00Z")))
                        .setEndTime(PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2018-10-22T18:00:00Z")))
                        .build())
                .setAnalysisGeometries(Collections.singletonList(getAnalysis()))

                .setAirmetPhenomenon(AviationCodeListUser.AeronauticalAirmetWeatherPhenomenon.BKN_CLD)
                .setCloudLevels(levels.build())
                .setTranslated(false);

        return sm.build();
    }

    @Test
    public void testBuild() throws IOException {
        final AIRMET sm = buildAirmet();
        assertTrue(sm.areAllTimeReferencesComplete());
        System.err.println("TAC: " + sm.toString());
        final String json = om.writeValueAsString(sm);
        System.err.println("JSON: " + json);
        final JsonNode smNode = om.readTree(json.getBytes(StandardCharsets.UTF_8));
        assertFalse(smNode.isNull());
        assertTrue(smNode.has("reportStatus"));
        assertEquals("NORMAL", smNode.get("reportStatus").asText());
        assertTrue(!smNode.has("cancelMessage") || smNode.get("cancelMessage").asBoolean() == false);

        final AIRMET readBackAirmet = om.readValue(json, AIRMETImpl.class);
        System.err.println("bottom: " + readBackAirmet.getCloudLevels().get().getCloudBase().getValue());
    }
}
