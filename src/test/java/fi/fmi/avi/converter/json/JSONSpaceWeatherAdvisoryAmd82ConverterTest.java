package fi.fmi.avi.converter.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.ImmutableList;
import fi.fmi.avi.converter.AviMessageConverter;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.converter.json.conf.JSONConverter;
import fi.fmi.avi.model.*;
import fi.fmi.avi.model.immutable.CoordinateReferenceSystemImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.PolygonGeometryImpl;
import fi.fmi.avi.model.swx.amd82.*;
import fi.fmi.avi.model.swx.amd82.immutable.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.unitils.thirdparty.org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JSONSpaceWeatherAdvisoryTestConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class JSONSpaceWeatherAdvisoryAmd82ConverterTest {

    @Autowired
    private AviMessageConverter converter;

    private static AdvisoryNumberImpl advisoryNumber(final int year, final int serialNumber) {
        return AdvisoryNumberImpl.builder().setYear(year).setSerialNumber(serialNumber).build();
    }

    private NextAdvisory getNextAdvisory(final boolean hasNext) {
        final NextAdvisoryImpl.Builder next = NextAdvisoryImpl.builder();

        if (hasNext) {
            final PartialOrCompleteTimeInstant nextAdvisoryTime = PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2020-02-27T01:00Z[UTC]"));
            next.setTime(nextAdvisoryTime);
            next.setTimeSpecifier(NextAdvisory.Type.NEXT_ADVISORY_AT);
        } else {
            next.setTimeSpecifier(NextAdvisory.Type.NO_FURTHER_ADVISORIES);
        }

        return next.build();
    }

    private List<String> getRemarks() {
        final List<String> remarks = new ArrayList<>();
        remarks.add("RADIATION LVL EXCEEDED 100 PCT OF BACKGROUND LVL AT FL350 AND ABV. THE CURRENT EVENT HAS PEAKED AND LVL SLW RTN TO BACKGROUND LVL."
                + " SEE WWW.SPACEWEATHERPROVIDER.WEB");

        return remarks;
    }

    private List<SpaceWeatherAdvisoryAnalysis> getAnalyses(final boolean hasObservation) {
        final List<SpaceWeatherAdvisoryAnalysis> analyses = new ArrayList<>();

        final int day = 27;
        final int hour = 1;
        for (int i = 0; i < 5; i++) {
            final SpaceWeatherAdvisoryAnalysisImpl.Builder analysis = SpaceWeatherAdvisoryAnalysisImpl.builder();

            final SpaceWeatherRegionImpl.Builder region = SpaceWeatherRegionImpl.builder();

            final String partialTime = "--" + day + "T" + hour + ":00Z";

            region.setAirSpaceVolume(getAirspaceVolume());
            region.setLocationIndicator(SpaceWeatherRegion.SpaceWeatherLocation.HIGH_NORTHERN_HEMISPHERE);
            analysis.addAllRegions(
                    Arrays.asList(region.build(), region.setLocationIndicator(SpaceWeatherRegion.SpaceWeatherLocation.MIDDLE_NORTHERN_HEMISPHERE).build()));

            if (i == 0 && hasObservation) {
                analysis.setAnalysisType(SpaceWeatherAdvisoryAnalysis.Type.OBSERVATION);
            } else {
                analysis.setAnalysisType(SpaceWeatherAdvisoryAnalysis.Type.FORECAST);
            }
            analysis.setTime(PartialOrCompleteTimeInstant.builder().setPartialTime(PartialDateTime.parse(partialTime)).build());
            analysis.setNilPhenomenonReason(SpaceWeatherAdvisoryAnalysis.NilPhenomenonReason.NO_INFORMATION_AVAILABLE);

            analyses.add(analysis.build());
        }

        return analyses;
    }

    private AirspaceVolume getAirspaceVolume() {
        final AirspaceVolumeImpl.Builder airspaceVolume = AirspaceVolumeImpl.builder();
        airspaceVolume.setUpperLimitReference("Reference");

        final PolygonGeometry geometry = PolygonGeometryImpl.builder()
                .addAllExteriorRingPositions(Arrays.asList(-180.0, 90.0, -180.0, 60.0, 180.0, 60.0, 180.0, 90.0, -180.0, 90.0))
                .setCrs(CoordinateReferenceSystemImpl.wgs84())
                .build();
        airspaceVolume.setHorizontalProjection(geometry);

        final NumericMeasure nm = NumericMeasureImpl.builder().setUom("uom").setValue(350.0).build();
        airspaceVolume.setUpperLimit(nm);

        return airspaceVolume.build();
    }

    private IssuingCenter getIssuingCenter() {
        final IssuingCenterImpl.Builder issuingCenter = IssuingCenterImpl.builder();
        issuingCenter.setName("DONLON");
        issuingCenter.setType("OTHER:SWXC");
        return issuingCenter.build();
    }

    @Test
    public void testSWXSerialization() throws Exception {
        final ObjectMapper om = new ObjectMapper();
        om.registerModule(new Jdk8Module());
        om.registerModule(new JavaTimeModule());

        final InputStream is = JSONSigmetConverterTest.class.getResourceAsStream("swx-amd82.json");
        Objects.requireNonNull(is);

        final String reference = IOUtils.toString(is, "UTF-8");

        final SpaceWeatherAdvisoryAmd82Impl advisory = SpaceWeatherAdvisoryAmd82Impl.builder()
                .setIssuingCenter(getIssuingCenter())
                .setIssueTime(PartialOrCompleteTimeInstant.builder().setCompleteTime(ZonedDateTime.parse("2020-02-27T01:00Z[UTC]")).build())
                .addAllAnalyses(getAnalyses(false))
                .setPermissibleUsageReason(AviationCodeListUser.PermissibleUsageReason.TEST)
                .addAllPhenomena(Arrays.asList(SpaceWeatherPhenomenon.fromWMOCodeListValue("http://codes.wmo.int/49-2/SpaceWxPhenomena/HF_COM_MOD"),
                        SpaceWeatherPhenomenon.fromWMOCodeListValue("http://codes.wmo.int/49-2/SpaceWxPhenomena/GNSS_MOD")))
                .setAdvisoryNumber(advisoryNumber(2020, 1))
                .addAllReplaceAdvisoryNumber(ImmutableList.of(
                        advisoryNumber(2019, 40),
                        advisoryNumber(2019, 41),
                        advisoryNumber(2019, 42))
                )
                .setRemarks(getRemarks())
                .setNextAdvisory(getNextAdvisory(true))
                .build();

        final ConversionResult<String> result = converter.convertMessage(advisory, JSONConverter.SWX_AMD82_POJO_TO_JSON_STRING, ConversionHints.EMPTY);
        assertSame(ConversionResult.Status.SUCCESS, result.getStatus());
        assertTrue(result.getConvertedMessage().isPresent());

        final JsonNode refRoot = om.readTree(reference);
        final JsonNode convertedRoot = om.readTree(result.getConvertedMessage().get());
        assertEquals("constructed and parsed tree not equal", refRoot, convertedRoot);
    }
}
