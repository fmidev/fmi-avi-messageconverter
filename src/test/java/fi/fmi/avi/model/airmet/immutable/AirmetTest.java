package fi.fmi.avi.model.airmet.immutable;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Optional;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import fi.fmi.avi.model.Airspace;
import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.Geometry;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.immutable.AirspaceImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.TacOrGeoGeometryImpl;
import fi.fmi.avi.model.immutable.UnitPropertyGroupImpl;
import fi.fmi.avi.model.sigmet.AIRMET;
import fi.fmi.avi.model.PhenomenonGeometryWithHeight;
import fi.fmi.avi.model.sigmet.SigmetAnalysisType;
import fi.fmi.avi.model.sigmet.SigmetIntensityChange;
import fi.fmi.avi.model.sigmet.immutable.AIRMETImpl;
import fi.fmi.avi.model.sigmet.immutable.AirmetCloudLevelsImpl;
import fi.fmi.avi.model.immutable.PhenomenonGeometryWithHeightImpl;

public class AirmetTest {

    ObjectMapper om=new ObjectMapper().registerModule(new JavaTimeModule()).registerModule(new Jdk8Module()).enable(SerializationFeature.INDENT_OUTPUT);

    static String testGeoJson1="{\"type\":\"Polygon\",\"polygons\":[[0,52],[0,60],[10,60],[10,52],[0,52]]}}";

    static String testGeoJson2="{\"type\":\"Polygon\",\"polygons\":[[0,52],[0,60],[5,60],[5,52],[0,52]]}}";

    public PhenomenonGeometryWithHeight getAnalysis() {
        Optional<Geometry> anGeometry=Optional.empty();

        try {
            anGeometry=Optional.ofNullable(om.readValue(testGeoJson1, Geometry.class));
        } catch (IOException e) {
            e.printStackTrace();
        }

        PhenomenonGeometryWithHeightImpl.Builder an=new PhenomenonGeometryWithHeightImpl.Builder()
                .setTime(PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2018-10-22T13:50:00Z")))
                .setGeometry(TacOrGeoGeometryImpl.of(anGeometry.get()))
                .setApproximateLocation(false)
                ;
        return an.build();
    }


    public AIRMET buildAirmet() {
        AviationCodeListUser.AeronauticalAirmetWeatherPhenomenon airmetPhenomenon= AviationCodeListUser.AeronauticalAirmetWeatherPhenomenon.BKN_CLD;

        AirmetCloudLevelsImpl.Builder levels = new AirmetCloudLevelsImpl.Builder()
                .setCloudBase(NumericMeasureImpl.of(0, "SFC"))
                .setCloudTop(NumericMeasureImpl.of(7000, "[ft_i]"));

        Airspace airspace=new AirspaceImpl.Builder().setDesignator("EHAA").setType(Airspace.AirspaceType.FIR).setName("AMSTERDAM").build();


        AIRMETImpl.Builder sm=new AIRMETImpl.Builder()
                .setIssueTime(PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2018-10-22T14:00:00Z")))
                .setIssuingAirTrafficServicesUnit(new UnitPropertyGroupImpl.Builder().setPropertyGroup("AMSTERDAM", "EHAA", "FIR").build())
                .setMeteorologicalWatchOffice(new UnitPropertyGroupImpl.Builder().setPropertyGroup("De Bilt", "EHDB", "MWO").build())
                .setAirspace(airspace)
                .setSequenceNumber("1")
                .setStatus(AviationCodeListUser.SigmetAirmetReportStatus.NORMAL)
                .setValidityPeriod(new PartialOrCompleteTimePeriod.Builder()
                        .setStartTime(PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2018-10-22T14:00:00Z")))
                        .setEndTime(PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2018-10-22T18:00:00Z")))
                        .build())
                .setAnalysisGeometries(Arrays.asList(getAnalysis()))
                .setAnalysisType(SigmetAnalysisType.OBSERVATION)
                .setIntensityChange(SigmetIntensityChange.WEAKENING)

                //                .setAnalysis(Arrays.asList(getAnalysis()))
                .setAirmetPhenomenon(AviationCodeListUser.AeronauticalAirmetWeatherPhenomenon.BKN_CLD)
                .setCloudLevels(levels.build())
                .setTranslated(false)
        ;

        return sm.build();
    }

    @Test
    public void testBuild() throws IOException {
        AIRMET sm=buildAirmet();
        assert(sm.areAllTimeReferencesComplete());
        System.err.println("TAC: "+sm.toString());
        String json=om.writeValueAsString(sm);
        System.err.println("JSON: "+json);
        JsonNode smNode=om.readTree(json.getBytes());
        assert(!smNode.isNull());
        assert(smNode.has("status"));
        assert(smNode.get("status").asText().equals("NORMAL"));

        AIRMET readBackAirmet=om.readValue(json, AIRMETImpl.class);
        System.err.println("bottom: "+readBackAirmet.getCloudLevels().get().getCloudBase().getValue());
    }
}
