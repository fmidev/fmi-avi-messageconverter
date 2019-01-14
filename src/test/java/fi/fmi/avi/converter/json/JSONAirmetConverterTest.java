package fi.fmi.avi.converter.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.locationtech.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.unitils.thirdparty.org.apache.commons.io.IOUtils;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import fi.fmi.avi.converter.AviMessageConverter;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.converter.json.conf.JSONConverter;
import fi.fmi.avi.model.Airspace;
import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.UnitPropertyGroup;
import fi.fmi.avi.model.immutable.AirspaceImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.TacOrGeoGeometryImpl;
import fi.fmi.avi.model.immutable.UnitPropertyGroupImpl;
import fi.fmi.avi.model.sigmet.AIRMET;
import fi.fmi.avi.model.sigmet.SigmetAnalysisType;
import fi.fmi.avi.model.sigmet.SigmetIntensityChange;
import fi.fmi.avi.model.sigmet.immutable.PhenomenonGeometryImpl;
import fi.fmi.avi.model.sigmet.immutable.PhenomenonGeometryWithHeightImpl;
import fi.fmi.avi.model.sigmet.immutable.AIRMETImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JSONAirmetTestConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class JSONAirmetConverterTest {

    @Autowired
    private AviMessageConverter converter;


    public void testAIRMETParsing() throws Exception {
        InputStream is = JSONAirmetConverterTest.class.getResourceAsStream("airmet1.json");
        Objects.requireNonNull(is);
        String input = IOUtils.toString(is,"UTF-8");
        is.close();
        ConversionResult<AIRMET> result = converter.convertMessage(input, JSONConverter.JSON_STRING_TO_AIRMET_POJO, ConversionHints.EMPTY);
        System.err.println("SM:"+result.getStatus()+" ==>");
        System.err.println("==>"+result.getConvertedMessage().get().getSequenceNumber());
        assertTrue(ConversionResult.Status.SUCCESS == result.getStatus());
    }


    @Autowired
    ObjectMapper om;

    @Test
    public void testAIRMETSerialization() throws Exception {
/*        ObjectMapper om = new ObjectMapper();
        om.registerModule(new Jdk8Module());
        om.registerModule(new JavaTimeModule());
        om.registerModule(new JtsModule());
        om.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);*/

        InputStream is = JSONAirmetConverterTest.class.getResourceAsStream("airmet1.json");
        Objects.requireNonNull(is);
        String reference = IOUtils.toString(is,"UTF-8");
        is.close();

        AIRMETImpl.Builder builder = new AIRMETImpl.Builder();

        UnitPropertyGroup mwo=new UnitPropertyGroupImpl.Builder().setPropertyGroup("De Bilt", "EHDB", "MWO").build();
        UnitPropertyGroup fir=new UnitPropertyGroupImpl.Builder().setPropertyGroup( "AMSTERDAM FIR", "EHAA", "FIR").build();

        Airspace airspace=new AirspaceImpl.Builder().setDesignator("EHAA").setType(Airspace.AirspaceType.FIR).setName("AMSTERDAM").build();

        String geomString="{ \"type\": \"Polygon\", \"coordinates\":[[[5.0,52.0],[6.0,53.0],[4.0,54.0],[5.0,52.0]]]}";
        Geometry geom=(Geometry)om.readValue(geomString, Geometry.class);
        String fpaGeomString="{ \"type\": \"Polygon\", \"coordinates\":[[[5.0,53.0],[6.0,54.0],[4.0,55.0],[5.0,53.0]]]}";
        Geometry fpaGeom=(Geometry)om.readValue(fpaGeomString, Geometry.class);

        PartialOrCompleteTimeInstant.Builder issueTimeBuilder=new PartialOrCompleteTimeInstant.Builder();
        issueTimeBuilder.setCompleteTime(ZonedDateTime.parse("2017-08-27T11:30:00Z"));
        PartialOrCompleteTimePeriod.Builder validPeriod=new PartialOrCompleteTimePeriod.Builder();
        validPeriod.setStartTime(PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2017-08-27T11:30:00Z")));
        validPeriod.setEndTime(PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2017-08-27T18:00:00Z")));

        PhenomenonGeometryWithHeightImpl.Builder geomBuilder = new PhenomenonGeometryWithHeightImpl.Builder();
        geomBuilder.setApproximateLocation(false);
        List<Geometry> geoms=Arrays.asList(geom);
        geomBuilder.setGeometry(TacOrGeoGeometryImpl.of(geom));
        geomBuilder.setTime(PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2017-08-27T12:00:00Z")));
        geomBuilder.setLowerLimit(NumericMeasureImpl.of(10, "FL"));
        geomBuilder.setUpperLimit(NumericMeasureImpl.of(35,"FL"));

        PhenomenonGeometryImpl.Builder fpGeomBuilder = new PhenomenonGeometryImpl.Builder();
        fpGeomBuilder.setApproximateLocation(false);
        fpGeomBuilder.setGeometry(TacOrGeoGeometryImpl.of(fpaGeom));
        fpGeomBuilder.setTime(PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2017-08-27T18:00:00Z")));

        builder.setStatus(AviationCodeListUser.SigmetAirmetReportStatus.NORMAL)
                .setMeteorologicalWatchOffice(mwo)
                .setIssuingAirTrafficServicesUnit(fir)
                .setAirspace(airspace)
                .setIssueTime(issueTimeBuilder.build())
                .setTranslated(false)
                .setPermissibleUsage(AviationCodeListUser.PermissibleUsage.NON_OPERATIONAL)
                .setPermissibleUsageReason(AviationCodeListUser.PermissibleUsageReason.EXERCISE)
                .setSequenceNumber("1")
                .setIntensityChange(SigmetIntensityChange.NO_CHANGE)
                .setAnalysisType(SigmetAnalysisType.OBSERVATION)
                .setValidityPeriod(validPeriod.build())
                .setAirmetPhenomenon(AviationCodeListUser.AeronauticalAirmetWeatherPhenomenon.MOD_ICE)
                .setAnalysisGeometries(Arrays.asList(geomBuilder.build()))
                ;

        AIRMET airmet=builder.build();
        ConversionResult<String> result = converter.convertMessage(airmet, JSONConverter.AIRMET_POJO_TO_JSON_STRING, ConversionHints.EMPTY);
        assertTrue(ConversionResult.Status.SUCCESS == result.getStatus());
        assertTrue(result.getConvertedMessage().isPresent());

        JsonNode refRoot=om.readTree(reference); 
        JsonNode convertedRoot=om.readTree(result.getConvertedMessage().get());
        System.err.println("EQUALS: "+refRoot.equals(convertedRoot));
        AIRMET convertedAirmet=om.readValue(result.getConvertedMessage().get(), AIRMETImpl.class);
        System.err.println("translated: "+convertedAirmet.isTranslated());

        ZonedDateTime now=ZonedDateTime.now();
        System.err.println("now: "+now+" "+convertedAirmet.getIssueTime());
        assertEquals("constructed and parsed tree not equal", airmet, convertedAirmet);
 //       BufferedReader refReader = new BufferedReader(new StringReader(reference));
 //       BufferedReader resultReader = new BufferedReader(new StringReader(result.getConvertedMessage().get()));
//        assertEquals("Strings do not match", reference, result.getConvertedMessage().get());

    }
}
