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
import fi.fmi.avi.model.immutable.*;
import fi.fmi.avi.model.sigmet.*;
import fi.fmi.avi.model.sigmet.immutable.PhenomenonGeometryImpl;
import fi.fmi.avi.model.sigmet.immutable.PhenomenonGeometryWithHeightImpl;
import fi.fmi.avi.model.sigmet.immutable.SIGMETImpl;

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
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JSONSigmetTestConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class JSONSigmetConverterTest {

    @Autowired
    private AviMessageConverter converter;


    public void testSIGMETParsing() throws Exception {
        InputStream is = JSONSigmetConverterTest.class.getResourceAsStream("sigmet1.json");
        Objects.requireNonNull(is);
        String input = IOUtils.toString(is,"UTF-8");
        is.close();
        ConversionResult<SIGMET> result = converter.convertMessage(input, JSONConverter.JSON_STRING_TO_SIGMET_POJO, ConversionHints.EMPTY);
        System.err.println("SM:"+result.getStatus()+" ==>");
        System.err.println("==>"+result.getConvertedMessage().get().getSequenceNumber());
        assertTrue(ConversionResult.Status.SUCCESS == result.getStatus());
    }


    @Test
    public void testSIGMETSerialization() throws Exception {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new Jdk8Module());
        om.registerModule(new JavaTimeModule());


        InputStream is = JSONSigmetConverterTest.class.getResourceAsStream("sigmet1.json");
        Objects.requireNonNull(is);
        String reference = IOUtils.toString(is,"UTF-8");
        is.close();

        SIGMETImpl.Builder builder = new SIGMETImpl.Builder();

        UnitPropertyGroup mwo=new UnitPropertyGroupImpl.Builder().setPropertyGroup("De Bilt", "EHDB", "MWO").build();
        UnitPropertyGroup fir=new UnitPropertyGroupImpl.Builder().setPropertyGroup( "AMSTERDAM FIR", "EHAA", "FIR").build();

        Airspace airspace=new AirspaceImpl.Builder().setDesignator("EHAA").setType(Airspace.AirspaceType.FIR).setName("AMSTERDAM").build();

        String geomString="{ \"type\": \"Polygon\", \"polygons\":[[[5.0,52.0],[6.0,53.0],[4.0,54.0],[5.0,52.0]]]}";
        Geometry geom=(Geometry)om.readValue(geomString, Geometry.class);
        String fpaGeomString="{ \"type\": \"Polygon\", \"polygons\":[[[5.0,53.0],[6.0,54.0],[4.0,55.0],[5.0,53.0]]]}";
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
                .setTranslated(true)
                .setPermissibleUsage(AviationCodeListUser.PermissibleUsage.NON_OPERATIONAL)
                .setPermissibleUsageReason(AviationCodeListUser.PermissibleUsageReason.EXERCISE)
                .setSequenceNumber("1")
                .setIntensityChange(SigmetIntensityChange.NO_CHANGE)
                .setAnalysisType(SigmetAnalysisType.OBSERVATION)
                .setValidityPeriod(validPeriod.build())
                .setSigmetPhenomenon(AviationCodeListUser.AeronauticalSignificantWeatherPhenomenon.EMBD_TS)
                .setAnalysisGeometries(Arrays.asList(geomBuilder.build()))
                .setForecastGeometries(Arrays.asList(fpGeomBuilder.build())
                );

        SIGMET sigmet=builder.build();
        ConversionResult<String> result = converter.convertMessage(sigmet, JSONConverter.SIGMET_POJO_TO_JSON_STRING, ConversionHints.EMPTY);
        assertTrue(ConversionResult.Status.SUCCESS == result.getStatus());
        assertTrue(result.getConvertedMessage().isPresent());

        JsonNode refRoot=om.readTree(reference); 
        JsonNode convertedRoot=om.readTree(result.getConvertedMessage().get());
        System.err.println("EQUALS: "+refRoot.equals(convertedRoot));
        assertEquals("constructed and parsed tree not equal", refRoot, convertedRoot);
 //       BufferedReader refReader = new BufferedReader(new StringReader(reference));
 //       BufferedReader resultReader = new BufferedReader(new StringReader(result.getConvertedMessage().get()));
//        assertEquals("Strings do not match", reference, result.getConvertedMessage().get());

    }
}
