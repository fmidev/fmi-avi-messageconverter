package fi.fmi.avi.converter.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fi.fmi.avi.converter.AviMessageConverter;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.converter.json.conf.JSONConverter;
import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.PartialDateTime;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.swx.VerticalLimitsImpl;
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
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JSONSpaceWeatherAdvisoryTestConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class JSONSpaceWeatherAdvisoryAmd82ConverterTest {

    private static final IssuingCenterImpl ISSUING_CENTER = IssuingCenterImpl.builder()
            .setName("DONLON")
            .setType("OTHER:SWXC")
            .build();
    private static final VerticalLimitsImpl VERTICAL_LIMITS = VerticalLimitsImpl.builder()
            .setOperator(AviationCodeListUser.RelationalOperator.ABOVE)
            .setLowerLimit(NumericMeasureImpl.of(350.0, "uom"))
            .setVerticalReference("Reference")
            .build();
    private static final List<String> REMARKS = Collections.singletonList(
            "RADIATION LVL EXCEEDED 100 PCT OF BACKGROUND LVL AT FL350 AND ABV. "
                    + "THE CURRENT EVENT HAS PEAKED AND LVL SLW RTN TO BACKGROUND LVL."
                    + " SEE WWW.SPACEWEATHERPROVIDER.WEB");

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

    private Stream<SpaceWeatherAdvisoryAnalysis> generateAnalyses() {
        final PartialOrCompleteTimeInstant time = PartialOrCompleteTimeInstant.builder()
                .setPartialTime(PartialDateTime.ofDayHourMinuteZone(27, 1, 0, ZoneOffset.UTC))
                .build();
        return IntStream.range(0, 5)
                .mapToObj(i -> SpaceWeatherAdvisoryAnalysisImpl.builder()
                        .setTime(time)
                        .setAnalysisType(i == 0
                                ? SpaceWeatherAdvisoryAnalysis.Type.OBSERVATION
                                : SpaceWeatherAdvisoryAnalysis.Type.FORECAST)
                        .addIntensityAndRegions(SpaceWeatherIntensityAndRegionImpl.builder()
                                .setIntensity(Intensity.MODERATE)
                                .addAllRegions(Stream.of(
                                                        SpaceWeatherRegion.SpaceWeatherLocation.HIGH_NORTHERN_HEMISPHERE,
                                                        SpaceWeatherRegion.SpaceWeatherLocation.MIDDLE_NORTHERN_HEMISPHERE
                                                )
                                                .map(locationIndicator -> SpaceWeatherRegionImpl.fromLocationIndicator(
                                                        locationIndicator, null, null, null, VERTICAL_LIMITS))
                                )
                                .build())
                        .setNilReason(SpaceWeatherAdvisoryAnalysis.NilReason.NO_INFORMATION_AVAILABLE)
                        .build());
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
                .setIssuingCenter(ISSUING_CENTER)
                .setEffect(Effect.HF_COMMUNICATIONS)
                .setIssueTime(PartialOrCompleteTimeInstant.builder().setCompleteTime(ZonedDateTime.parse("2020-02-27T01:00Z[UTC]")).build())
                .addAllAnalyses(generateAnalyses())
                .setPermissibleUsageReason(AviationCodeListUser.PermissibleUsageReason.TEST)
                .setAdvisoryNumber(advisoryNumber(2020, 1))
                .addReplaceAdvisoryNumbers(
                        advisoryNumber(2019, 40),
                        advisoryNumber(2019, 41),
                        advisoryNumber(2019, 42)
                )
                .setRemarks(REMARKS)
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
