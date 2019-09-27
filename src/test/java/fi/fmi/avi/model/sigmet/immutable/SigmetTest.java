package fi.fmi.avi.model.sigmet.immutable;

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
import fi.fmi.avi.model.immutable.TacOrGeoGeometryImpl;
import fi.fmi.avi.model.immutable.UnitPropertyGroupImpl;
import fi.fmi.avi.model.sigmet.PhenomenonGeometry;
import fi.fmi.avi.model.sigmet.PhenomenonGeometryWithHeight;
import fi.fmi.avi.model.sigmet.SIGMET;
import fi.fmi.avi.model.sigmet.SigmetAnalysisType;
import fi.fmi.avi.model.sigmet.SigmetIntensityChange;

import org.junit.Test;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Optional;

public class SigmetTest {

    ObjectMapper om=new ObjectMapper().registerModule(new JavaTimeModule()).registerModule(new Jdk8Module()).enable(SerializationFeature.INDENT_OUTPUT);

    static String testGeoJson1="{\"type\":\"Polygon\",\"polygons\":[[[0,52],[0,60],[10,60],[10,52],[0,52]]]}}";

    static String testGeoJson2="{\"type\":\"Polygon\",\"polygons\":[[[0,52],[0,60],[5,60],[5,52],[0,52]]]}}";

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

    public PhenomenonGeometry getForecast() {
        Optional<Geometry> fcGeometry=Optional.empty();

        try {
            fcGeometry=Optional.ofNullable(om.readValue(testGeoJson2, Geometry.class));
        } catch (IOException e) {
            e.printStackTrace();
        }

        PhenomenonGeometryImpl.Builder an=new PhenomenonGeometryImpl.Builder()
                .setTime(PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2018-10-22T18:00:00Z")))
                .setGeometry(TacOrGeoGeometryImpl.of(fcGeometry.get()))
                .setApproximateLocation(false)
                ;
        return an.build();
    }

    public SIGMET buildSigmet() {
        Airspace airspace=new AirspaceImpl.Builder().setDesignator("EHAA").setType(Airspace.AirspaceType.FIR).setName("AMSTERDAM").build();


        SIGMETImpl.Builder sm=new SIGMETImpl.Builder()
                .setIssueTime(PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2018-10-22T14:00:00Z")))
                .setIssuingAirTrafficServicesUnit(new UnitPropertyGroupImpl.Builder().setPropertyGroup("AMSTERDAM FIR", "EHAM", "FIR").build())
                .setMeteorologicalWatchOffice(new UnitPropertyGroupImpl.Builder().setPropertyGroup("De Bilt", "EHDB", "MWO").build())
                .setAirspace(airspace)
                .setSequenceNumber("1")
                .setStatus(AviationCodeListUser.SigmetAirmetReportStatus.NORMAL)
                .setValidityPeriod(new PartialOrCompleteTimePeriod.Builder()
                        .setStartTime(PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2018-10-22T14:00:00Z")))
                        .setEndTime(PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2018-10-22T18:00:00Z")))
                        .build())
                .setAnalysisGeometries(Arrays.asList(getAnalysis()))
                .setForecastGeometries(Arrays.asList(getForecast()))
                .setAnalysisType(SigmetAnalysisType.OBSERVATION)
                .setIntensityChange(SigmetIntensityChange.WEAKENING)

                //                .setAnalysis(Arrays.asList(getAnalysis()))
                .setSigmetPhenomenon(AviationCodeListUser.AeronauticalSignificantWeatherPhenomenon.EMBD_TS)
                .setTranslated(false)
        ;
        return sm.build();
    }

    @Test
    public void testBuild() throws IOException {
        SIGMET sm=buildSigmet();
        assert(sm.areAllTimeReferencesComplete());
        System.err.println("TAC: "+sm.toString());
        String json=om.writeValueAsString(sm);
        System.err.println("JSON: "+json);
        JsonNode smNode=om.readTree(json.getBytes());
        assert(!smNode.isNull());
        assert(smNode.has("status"));
        assert(smNode.get("status").asText().equals("NORMAL"));
    }
}
