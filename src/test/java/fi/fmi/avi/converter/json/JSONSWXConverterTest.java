package fi.fmi.avi.converter.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
import fi.fmi.avi.model.PartialDateTime;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.PhenomenonGeometryWithHeight;
import fi.fmi.avi.model.SWX.AdvisoryNumberImpl;
import fi.fmi.avi.model.SWX.NextAdvisory;
import fi.fmi.avi.model.SWX.NextAdvisoryImpl;
import fi.fmi.avi.model.SWX.SWX;
import fi.fmi.avi.model.SWX.SWXAnalysis;
import fi.fmi.avi.model.SWX.immutable.SWXAnalysisImpl;
import fi.fmi.avi.model.SWX.immutable.SWXImpl;
import fi.fmi.avi.model.immutable.PhenomenonGeometryWithHeightImpl;
import fi.fmi.avi.model.immutable.PolygonsGeometryImpl;
import fi.fmi.avi.model.immutable.TacOrGeoGeometryImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JSONSWXTestConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class JSONSWXConverterTest {

    @Autowired
    private AviMessageConverter converter;

    private AdvisoryNumberImpl getAdvisoryNumber() {
        AdvisoryNumberImpl.Builder advisory = AdvisoryNumberImpl.builder().setYear(2020).setSerialNumber(1);

        return advisory.build();
    }

    private NextAdvisory getNextAdvisory(boolean hasNext) {
        NextAdvisoryImpl.Builder next = NextAdvisoryImpl.builder();

        if (hasNext) {
            PartialOrCompleteTimeInstant nextAdvisoryTime = PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2020-02-27T01:00Z[UTC]"));
            next.setTime(nextAdvisoryTime);
            next.setTimeSpecifier(NextAdvisory.Type.NEXT_ADVISORY_AT);
        } else {
            next.setTimeSpecifier(NextAdvisory.Type.NO_FURTHER_ADVISORIES);
        }

        return next.build();
    }

    private List<String> getRemarks() {
        List<String> remarks = new ArrayList<>();
        remarks.add("RADIATION LVL EXCEEDED 100 PCT OF BACKGROUND LVL AT FL350 AND ABV. THE CURRENT EVENT HAS PEAKED AND LVL SLW RTN TO BACKGROUND LVL."
                + " SEE WWW.SPACEWEATHERPROVIDER.WEB");

        return remarks;
    }

    private List<SWXAnalysis> getAnalyses(boolean hasObservation) {
        List<SWXAnalysis> analyses = new ArrayList<>();

        int day = 27;
        int hour = 1;

        for (int i = 0; i < 5; i++) {
            SWXAnalysisImpl.Builder analysis = SWXAnalysisImpl.builder();

            if (i == 0 && hasObservation) {
                analysis.setAnalysisType(SWXAnalysis.Type.OBSERVATION);
            } else {
                analysis.setAnalysisType(SWXAnalysis.Type.FORECAST);
            }

            String partialTime = "--" + day + "T" + hour + ":00Z";
            analysis.setAnalysis(getPhenomenon(partialTime));
            analyses.add(analysis.build());

            hour += 6;
            if (hour >= 24) {
                day += 1;
                hour = hour % 24;
            }

        }

        return analyses;
    }

    private PhenomenonGeometryWithHeight getPhenomenon(String partialTime) {
        PolygonsGeometryImpl.Builder polygon = PolygonsGeometryImpl.builder();
        polygon.addAllPolygons(
                Arrays.asList(Arrays.asList(-180.0, -90.0), Arrays.asList(-180.0, -60.0), Arrays.asList(180.0, -60.0), Arrays.asList(180.0, -90.0),
                        Arrays.asList(-180.0, -90.0)));

        PhenomenonGeometryWithHeightImpl.Builder phenomenon = new PhenomenonGeometryWithHeightImpl.Builder().setTime(
                PartialOrCompleteTimeInstant.of(PartialDateTime.parse(partialTime))).setGeometry(TacOrGeoGeometryImpl.of(polygon.build()));

        return phenomenon.build();
    }

    @Test
    public void testSWXSerialization() throws Exception {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new Jdk8Module());
        om.registerModule(new JavaTimeModule());

        InputStream is = JSONSigmetConverterTest.class.getResourceAsStream("swx1.json");
        Objects.requireNonNull(is);

        String reference = IOUtils.toString(is, "UTF-8");

        SWXImpl SWXObject = SWXImpl.builder()
                .setTranslated(false)
                .setIssuingCenterName("DONLON")
                .setIssueTime(PartialOrCompleteTimeInstant.builder().setCompleteTime(ZonedDateTime.parse("2020-02-27T01:00Z[UTC]")).build())
                .setStatus(SWX.STATUS.TEST)
                .addAllAnalyses(getAnalyses(false))
                .addAllPhenomena(Arrays.asList("HF COM MOD", "GNSS MOD"))
                .setAdvisoryNumber(getAdvisoryNumber())
                .setReplacementAdvisoryNumber(Optional.empty())
                .setRemarks(getRemarks())
                .setNextAdvisory(getNextAdvisory(true))
                .build();

        ConversionResult<String> result = converter.convertMessage(SWXObject, JSONConverter.SWX_POJO_TO_JSON_STRING, ConversionHints.EMPTY);
        assertTrue(ConversionResult.Status.SUCCESS == result.getStatus());
        assertTrue(result.getConvertedMessage().isPresent());

        JsonNode refRoot = om.readTree(reference);
        JsonNode convertedRoot = om.readTree(result.getConvertedMessage().get());
        System.err.println("EQUALS: " + refRoot.equals(convertedRoot));
        assertEquals("constructed and parsed tree not equal", refRoot, convertedRoot);
    }
}
