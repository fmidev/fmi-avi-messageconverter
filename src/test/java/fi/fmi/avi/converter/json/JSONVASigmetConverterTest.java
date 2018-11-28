package fi.fmi.avi.converter.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import fi.fmi.avi.converter.AviMessageConverter;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionIssue;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.converter.json.conf.JSONConverter;
import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.UnitPropertyGroup;
import fi.fmi.avi.model.VolcanoDescription;
import fi.fmi.avi.model.immutable.GeoPositionImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.UnitPropertyGroupImpl;
import fi.fmi.avi.model.immutable.VolcanoDescriptionImpl;
import fi.fmi.avi.model.sigmet.SIGMET;
import fi.fmi.avi.model.sigmet.SigmetAnalysis;
import fi.fmi.avi.model.sigmet.SigmetAnalysisType;
import fi.fmi.avi.model.sigmet.SigmetIntensityChange;
import fi.fmi.avi.model.sigmet.VASIGMET;
import fi.fmi.avi.model.sigmet.immutable.SIGMETImpl;
import fi.fmi.avi.model.sigmet.immutable.SigmetAnalysisImpl;
import fi.fmi.avi.model.sigmet.immutable.VASIGMETImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JSONVASigmetTestConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class JSONVASigmetConverterTest {

    @Autowired
    private AviMessageConverter converter;


    public void test1() {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new Jdk8Module());
        om.registerModule(new JavaTimeModule());
        om.registerModule(new JtsModule());
        String input="{     \"time\": \"2017-08-24T12:30:00Z\",\"lowerLimit\": {\"value\": 10, \"uom\": \"FL\"},"+
        "\"analysisType\": \"OBSERVATION\",\"intensityChange\": \"NO_CHANGE\", \"approximateLocation\":false, \"geometry\":"+
                "{\"type\":\"Polygon\", \"coordinates\":[[[5.0,52.0],[6.0,53.0],[4.0,54.0],[5.0,52.0]]]}"+
                "}";
        try {
            Object o = om.readValue(input, SigmetAnalysisImpl.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSIGMETParsing() throws Exception {
        InputStream is = JSONVASigmetConverterTest.class.getResourceAsStream("vasigmet1.json");
        Objects.requireNonNull(is);
        String input = IOUtils.toString(is,"UTF-8");
        is.close();
        ConversionResult<SIGMET> result = converter.convertMessage(input, JSONConverter.JSON_STRING_TO_SIGMET_POJO, ConversionHints.EMPTY);
        for (ConversionIssue iss:result.getConversionIssues()){
            System.err.println("  ISS:"+iss.getMessage()+" "+iss.getCause());
        }
        System.err.println("SM:"+result.getStatus()+" ==>");
        System.err.println("==>"+result.getConvertedMessage().get().getSequenceNumber());
        assertTrue(ConversionResult.Status.SUCCESS == result.getStatus());
    }

//@Test
    public void testVASIGMETSerialization() throws Exception {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new Jdk8Module());
        om.registerModule(new JavaTimeModule());
        om.registerModule(new JtsModule());


        InputStream is = JSONVASigmetConverterTest.class.getResourceAsStream("vasigmet1.json");
        Objects.requireNonNull(is);
        String reference = IOUtils.toString(is,"UTF-8");
        is.close();

        VASIGMETImpl.Builder builder = new VASIGMETImpl.Builder();

        UnitPropertyGroup mwo=new UnitPropertyGroupImpl.Builder().setPropertyGroup("EHDB", "EHDB", "MWO").build();
        UnitPropertyGroup fir=new UnitPropertyGroupImpl.Builder().setPropertyGroup("EHAA", "EHAA", "FIR").build();

        String geomString="{ \"type\": \"Polygon\", \"coordinates\":[[[5.0,52.0],[6.0,53.0],[4.0,54.0],[5.0,52.0]]]}";
        Geometry geom=(Geometry)om.readValue(geomString, Geometry.class);
        String fpaGeomString="{ \"type\": \"Polygon\", \"coordinates\":[[[5.0,53.0],[6.0,54.0],[4.0,55.0],[5.0,53.0]]]}";
        Geometry fpaGeom=(Geometry)om.readValue(fpaGeomString, Geometry.class);

        SigmetAnalysis analysis1=new SigmetAnalysisImpl.Builder()
                .setAnalysisTime(PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2017-08-27T12:00:00Z")))
                .setLowerLimit(NumericMeasureImpl.of(10, "FL"))
                .setUpperLimit(NumericMeasureImpl.of(35,"FL"))
                .setAnalysisType(SigmetAnalysisType.OBSERVATION)
                .setAnalysisApproximateLocation(false)
                .setAnalysisGeometry(geom)
                .setIntensityChange(SigmetIntensityChange.NO_CHANGE)
                .setForecastGeometry(fpaGeom)
                .setForecastTime(PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2017-08-27T18:00:00Z")))
                .build();
        List<SigmetAnalysis> analysis=new ArrayList<>();
        analysis.add(analysis1);

        PartialOrCompleteTimeInstant.Builder issueTimeBuilder=new PartialOrCompleteTimeInstant.Builder();
        issueTimeBuilder.setCompleteTime(ZonedDateTime.parse("2017-08-27T11:30:00Z"));
        PartialOrCompleteTimePeriod.Builder validPeriod=new PartialOrCompleteTimePeriod.Builder();
        validPeriod.setStartTime(PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2017-08-27T11:30:00Z")));
        validPeriod.setEndTime(PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2017-08-27T18:00:00Z")));

        VolcanoDescriptionImpl.Builder volcanoBuilder=new VolcanoDescriptionImpl.Builder();
        GeoPositionImpl.Builder gpBuilder = new GeoPositionImpl.Builder();
        gpBuilder.setCoordinates(new Double[] { 52.0, 5.2 });
        gpBuilder.setCoordinateReferenceSystemId("EPSG:4326");
        volcanoBuilder.setVolcanoPosition(gpBuilder.build());

        builder.setStatus(AviationCodeListUser.SigmetAirmetReportStatus.NORMAL)
                .setMeteorologicalWatchOffice(mwo)
                .setIssuingAirTrafficServicesUnit(fir)
                .setIssueTime(issueTimeBuilder.build())
                .setTranslated(false)
                .setPermissibleUsage(AviationCodeListUser.PermissibleUsage.NON_OPERATIONAL)
                .setPermissibleUsageReason(AviationCodeListUser.PermissibleUsageReason.EXERCISE)
                .setSequenceNumber("1")
                .setSigmetPhenomenon(AviationCodeListUser.AeronauticalSignificantWeatherPhenomenon.EMBD_TS)
                .setValidityPeriod(validPeriod.build())
                .setAnalysis(analysis)
                .setVolcano(volcanoBuilder.build());

        VASIGMET vaSigmet=builder.build();

        ConversionResult<String> result = converter.convertMessage(vaSigmet, JSONConverter.SIGMET_POJO_TO_JSON_STRING, ConversionHints.EMPTY);
        assertTrue(ConversionResult.Status.SUCCESS == result.getStatus());
        assertTrue(result.getConvertedMessage().isPresent());

        BufferedReader refReader = new BufferedReader(new StringReader(reference));
        BufferedReader resultReader = new BufferedReader(new StringReader(result.getConvertedMessage().get()));
        assertEquals("Strings do not match", reference, result.getConvertedMessage().get());

        String line = null;
        int lineNo = 0;
        while ((line = refReader.readLine()) != null) {
            lineNo++;
            assertEquals("Line " + lineNo + " does not match", line, resultReader.readLine());
        }
        assertTrue(resultReader.readLine() == null);

    }
}
