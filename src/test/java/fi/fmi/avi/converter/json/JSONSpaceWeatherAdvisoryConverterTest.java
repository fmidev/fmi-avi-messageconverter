package fi.fmi.avi.converter.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.math.BigInteger;
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
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.PartialDateTime;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.PointGeometry;
import fi.fmi.avi.model.SpaceWeatherAdvisory.AirspaceVolume;
import fi.fmi.avi.model.SpaceWeatherAdvisory.IssuingCenter;
import fi.fmi.avi.model.SpaceWeatherAdvisory.NextAdvisory;
import fi.fmi.avi.model.SpaceWeatherAdvisory.SpaceWeatherAdvisory;
import fi.fmi.avi.model.SpaceWeatherAdvisory.SpaceWeatherAdvisoryAnalysis;
import fi.fmi.avi.model.SpaceWeatherAdvisory.immutable.AdvisoryNumberImpl;
import fi.fmi.avi.model.SpaceWeatherAdvisory.immutable.AirspaceVolumeImpl;
import fi.fmi.avi.model.SpaceWeatherAdvisory.immutable.IssuingCenterImpl;
import fi.fmi.avi.model.SpaceWeatherAdvisory.immutable.NextAdvisoryImpl;
import fi.fmi.avi.model.SpaceWeatherAdvisory.immutable.SpaceWeatherAdvisoryAnalysisImpl;
import fi.fmi.avi.model.SpaceWeatherAdvisory.immutable.SpaceWeatherAdvisoryImpl;
import fi.fmi.avi.model.SpaceWeatherAdvisory.immutable.SpaceWeatherRegionImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.PointGeometryImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JSONSpaceWeatherAdvisoryTestConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class JSONSpaceWeatherAdvisoryConverterTest {

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

    private List<SpaceWeatherAdvisoryAnalysis> getAnalyses(boolean hasObservation) {
        List<SpaceWeatherAdvisoryAnalysis> analyses = new ArrayList<>();

        int day = 27;
        int hour = 1;
        for (int i = 0; i < 5; i++) {
            SpaceWeatherAdvisoryAnalysisImpl.Builder analysis = SpaceWeatherAdvisoryAnalysisImpl.builder();

            SpaceWeatherRegionImpl.Builder region = SpaceWeatherRegionImpl.builder();

            String partialTime = "--" + day + "T" + hour + ":00Z";

            region.setAirSpaceVolume(getAirspaceVolume());
            region.setLocationIndicator("HNH");
            analysis.setRegion(Arrays.asList(region.build(), region.setLocationIndicator("MNH").build()));

            if (i == 0 && hasObservation) {
                analysis.setAnalysisType(SpaceWeatherAdvisoryAnalysis.Type.OBSERVATION);
            } else {
                analysis.setAnalysisType(SpaceWeatherAdvisoryAnalysis.Type.FORECAST);
            }
            analysis.setTime(PartialOrCompleteTimeInstant.builder().setPartialTime(PartialDateTime.parse(partialTime)).build());
            analysis.setNoInformationAvailable(true);
            analysis.setNoPhenomenaExpected(true);

            analyses.add(analysis.build());
        }

        return analyses;
    }

    private AirspaceVolume getAirspaceVolume() {
        AirspaceVolumeImpl.Builder airspaceVolume = AirspaceVolumeImpl.builder();
        airspaceVolume.setUpperLimitReference("Reference");
        airspaceVolume.setSrsName("Dimension");
        airspaceVolume.setSrsDimension(BigInteger.valueOf(2));
        airspaceVolume.setAxisLabels(Arrays.asList("lat", "lon"));

        PointGeometry geometry = PointGeometryImpl.builder()
                .setPoint(Arrays.asList(-180.0, 90.0, -180.0, 60.0, 180.0, 60.0, 180.0, 90.0, -180.0, 90.0))
                .build();
        airspaceVolume.setGeometry(geometry);

        NumericMeasure nm = NumericMeasureImpl.builder().setUom("uom").setValue(Double.valueOf(350)).build();
        airspaceVolume.setUpperLimit(nm);

        return airspaceVolume.build();
    }

    private IssuingCenter getIssuingCenter() {
        IssuingCenterImpl.Builder issuingCenter = IssuingCenterImpl.builder();
        issuingCenter.setName("DONLON");
        issuingCenter.setType("OTHER:SWXC");
        issuingCenter.setInterpretation("SNAPSHOT");
        return issuingCenter.build();
    }

    @Test
    public void testSWXSerialization() throws Exception {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new Jdk8Module());
        om.registerModule(new JavaTimeModule());

        InputStream is = JSONSigmetConverterTest.class.getResourceAsStream("swx1.json");
        Objects.requireNonNull(is);

        String reference = IOUtils.toString(is, "UTF-8");

        SpaceWeatherAdvisoryImpl SWXObject = SpaceWeatherAdvisoryImpl.builder()
                .setIssuingCenter(getIssuingCenter())
                .setIssueTime(PartialOrCompleteTimeInstant.builder().setCompleteTime(ZonedDateTime.parse("2020-02-27T01:00Z[UTC]")).build())
                .setStatus(SpaceWeatherAdvisory.Status.TEST)
                .addAllAnalyses(getAnalyses(false))
                .addAllPhenomena(Arrays.asList("HF COM MOD", "GNSS MOD"))
                .setAdvisoryNumber(getAdvisoryNumber())
                .setReplaceAdvisoryNumber(Optional.empty())
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
