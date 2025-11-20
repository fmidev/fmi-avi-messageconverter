package fi.fmi.avi.model.swx.amd82.immutable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fi.fmi.avi.model.*;
import fi.fmi.avi.model.swx.amd82.*;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


public class SpaceWeatherAdvisoryAmd82Test {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final ZonedDateTime BASE_TIME = ZonedDateTime.parse("2020-02-27T01:00:00Z");
    private static final IssuingCenterImpl ISSUING_CENTER = IssuingCenterImpl.builder()
            .setName("DONLON")
            .setType("OTHER:SWXC")
            .build();
    private static final AdvisoryNumberImpl ADVISORY_NUMBER = AdvisoryNumberImpl.builder()
            .setYear(BASE_TIME.getYear())
            .setSerialNumber(1)
            .build();
    private static final List<String> REMARKS = Collections.singletonList("RADIATION LVL EXCEEDED 100 PCT OF BACKGROUND LVL AT FL350 AND ABV. THE CURRENT EVENT HAS PEAKED AND LVL SLW RTN TO BACKGROUND LVL."
            + " SEE WWW.SPACEWEATHERPROVIDER.WEB");
    private static final List<SpaceWeatherRegion.SpaceWeatherLocation> LOCATION_INDICATORS = Arrays.asList(
            SpaceWeatherRegion.SpaceWeatherLocation.HIGH_NORTHERN_HEMISPHERE,
            SpaceWeatherRegion.SpaceWeatherLocation.MIDDLE_NORTHERN_HEMISPHERE
    );

    @BeforeClass
    public static void setup() {
        OBJECT_MAPPER.registerModule(new Jdk8Module()).registerModule(new JavaTimeModule());
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static ZonedDateTime nullableCompleteTime(final Optional<PartialOrCompleteTimeInstant> partialOrCompleteTimeInstant) {
        return partialOrCompleteTimeInstant.flatMap(PartialOrCompleteTimeInstant::getCompleteTime).orElse(null);
    }

    private static ZonedDateTime nullableCompleteTime(final PartialOrCompleteTimeInstant partialOrCompleteTimeInstant) {
        return partialOrCompleteTimeInstant.getCompleteTime().orElse(null);
    }

    private static NextAdvisory getNextAdvisory(final boolean hasNext) {
        final NextAdvisoryImpl.Builder next = NextAdvisoryImpl.builder();

        if (hasNext) {
            next.setTimeSpecifier(NextAdvisory.Type.NEXT_ADVISORY_AT)
                    .setTime(SWXAmd82Tests.dayHourMinuteZoneAndCompleteTime(BASE_TIME.plusHours(24)));
        } else {
            next.setTimeSpecifier(NextAdvisory.Type.NO_FURTHER_ADVISORIES);
        }

        return next.build();
    }

    private static Stream<AdvisoryNumber> getReplacementNumbers(final int... serials) {
        return Arrays.stream(serials)
                .mapToObj(serial -> AdvisoryNumberImpl.builder()
                        .setYear(BASE_TIME.getYear())
                        .setSerialNumber(serial)
                        .build());
    }

    private static Stream<SpaceWeatherAdvisoryAnalysisImpl> generateAnalyses() {
        return SWXAmd82Tests.analysisBuilder(BASE_TIME)
                .addIntensities(Intensity.MODERATE)
                .setRegionsPerIntensityFromLocationIndicators()
                .addAllLocationIndicators(LOCATION_INDICATORS)
                .generateAnalyses();
    }

    @Test
    public void buildSWXWithDaySideRegion() throws Exception {
        final ZonedDateTime analysisTime = ZonedDateTime.parse("2025-10-31T11:00:00Z");
        final ZonedDateTime issueTime = ZonedDateTime.parse("2025-10-31T09:23:11Z[UTC]");

        final SpaceWeatherAdvisoryAmd82Impl advisory = SpaceWeatherAdvisoryAmd82Impl.builder()
                .setIssuingCenter(ISSUING_CENTER)
                .setEffect(Effect.HF_COMMUNICATIONS)
                .setIssueTime(PartialOrCompleteTimeInstant.of(issueTime))
                .setPermissibleUsageReason(AviationCodeListUser.PermissibleUsageReason.TEST)
                .setAdvisoryNumber(ADVISORY_NUMBER)
                .addAllAnalyses(SWXAmd82Tests.analysisBuilder(analysisTime)
                        .setRegionsPerIntensityFromLocationIndicators()
                        .addLocationIndicators(SpaceWeatherRegion.SpaceWeatherLocation.DAYSIDE)
                        .generateAnalyses())
                .setRemarks(REMARKS)
                .setNextAdvisory(getNextAdvisory(true))
                .build();

        final SpaceWeatherRegion dayside = advisory.getAnalyses().get(0).getIntensityAndRegions().get(0).getRegions().get(0);
        assertThat(dayside.getLocationIndicator()).hasValue(SpaceWeatherRegion.SpaceWeatherLocation.DAYSIDE);
        assertThat(dayside.getAirSpaceVolume()).isPresent();
        assertThat(dayside.getAirSpaceVolume().get().getHorizontalProjection())
                .isPresent()
                .get()
                .isInstanceOf(CircleByCenterPoint.class);

        final CircleByCenterPoint circle = (CircleByCenterPoint) dayside.getAirSpaceVolume().get().getHorizontalProjection().get();
        assertThat(circle.getRadius().getValue()).isEqualTo(10100d);
        assertThat(circle.getRadius().getUom()).isEqualTo("km");
        assertThat(circle.getCenterPointCoordinates()).containsExactly(-14.26d, 10.9d);

        final String serialized = OBJECT_MAPPER.writeValueAsString(advisory);
        final SpaceWeatherAdvisoryAmd82Impl deserialized = OBJECT_MAPPER.readValue(serialized, SpaceWeatherAdvisoryAmd82Impl.class);
        assertThat(deserialized).isEqualTo(advisory);
    }

    @Test
    public void buildSWXWithoutNextAdvisory() throws Exception {
        final SpaceWeatherAdvisoryAmd82Impl advisory = SpaceWeatherAdvisoryAmd82Impl.builder()
                .setIssuingCenter(ISSUING_CENTER)
                .setEffect(Effect.HF_COMMUNICATIONS)
                .setIssueTime(PartialOrCompleteTimeInstant.builder().setCompleteTime(ZonedDateTime.parse("2020-02-27T01:00Z[UTC]")).build())
                .setPermissibleUsageReason(AviationCodeListUser.PermissibleUsageReason.TEST)
                .setAdvisoryNumber(ADVISORY_NUMBER)
                .addAllAnalyses(generateAnalyses())
                .setRemarks(REMARKS)
                .setNextAdvisory(getNextAdvisory(false))
                .build();

        assertThat(advisory.getAdvisoryNumber().getSerialNumber()).as("serial number").isEqualTo(1);
        assertThat(advisory.getAdvisoryNumber().getYear()).as("year").isEqualTo(2020);
        assertThat(advisory.getAnalyses().get(0).getAnalysisType()).as("analysis type").isEqualTo(SpaceWeatherAdvisoryAnalysis.Type.OBSERVATION);
        assertThat(advisory.getAnalyses()).as("analyses").hasSize(5);
        assertThat(advisory.getNextAdvisory().getTime()).as("next advisory time").isNotPresent();
        assertThat(advisory.getNextAdvisory().getTimeSpecifier()).as("next advisory time specifier").isEqualTo(NextAdvisory.Type.NO_FURTHER_ADVISORIES);

        final String serialized = OBJECT_MAPPER.writeValueAsString(advisory);
        final SpaceWeatherAdvisoryAmd82Impl deserialized = OBJECT_MAPPER.readValue(serialized, SpaceWeatherAdvisoryAmd82Impl.class);

        assertThat(deserialized).isEqualTo(advisory);
    }

    @Test
    public void buildSWXWithoutObservation() throws Exception {
        final SpaceWeatherAdvisoryAmd82Impl advisory = SpaceWeatherAdvisoryAmd82Impl.builder()
                .setIssuingCenter(ISSUING_CENTER)
                .setEffect(Effect.HF_COMMUNICATIONS)
                .setIssueTime(PartialOrCompleteTimeInstant.builder().setCompleteTime(ZonedDateTime.parse("2020-02-27T01:00Z[UTC]")).build())
                .setPermissibleUsageReason(AviationCodeListUser.PermissibleUsageReason.TEST)
                .addAllAnalyses(generateAnalyses()
                        .map(analysis -> analysis.getAnalysisType() == SpaceWeatherAdvisoryAnalysis.Type.OBSERVATION
                                ? analysis.toBuilder()
                                .setAnalysisType(SpaceWeatherAdvisoryAnalysis.Type.FORECAST)
                                .build()
                                : analysis))
                .setAdvisoryNumber(ADVISORY_NUMBER)
                .setRemarks(REMARKS)
                .setNextAdvisory(getNextAdvisory(false))
                .build();

        assertThat(advisory.getAdvisoryNumber().getSerialNumber()).as("serial number").isEqualTo(1);
        assertThat(advisory.getAdvisoryNumber().getYear()).as("year").isEqualTo(2020);
        assertThat(advisory.getAnalyses()).as("analyses").hasSize(5);

        assertThat(advisory.getNextAdvisory().getTime()).as("next advisory time").isNotPresent();
        assertThat(advisory.getNextAdvisory().getTimeSpecifier()).as("next advisory time specifier").isEqualTo(NextAdvisory.Type.NO_FURTHER_ADVISORIES);

        final String serialized = OBJECT_MAPPER.writeValueAsString(advisory);
        final SpaceWeatherAdvisoryAmd82Impl deserialized = OBJECT_MAPPER.readValue(serialized, SpaceWeatherAdvisoryAmd82Impl.class);

        assertThat(deserialized).isEqualTo(advisory);
    }

    @Test
    public void swxSerializationTest() throws Exception {
        final SpaceWeatherAdvisoryAmd82Impl advisory = SpaceWeatherAdvisoryAmd82Impl.builder()
                .setIssuingCenter(ISSUING_CENTER)
                .setEffect(Effect.HF_COMMUNICATIONS)
                .setIssueTime(PartialOrCompleteTimeInstant.builder().setCompleteTime(ZonedDateTime.parse("2020-02-27T01:00Z[UTC]")).build())
                .setPermissibleUsageReason(AviationCodeListUser.PermissibleUsageReason.TEST)
                .setAdvisoryNumber(ADVISORY_NUMBER)
                .addAllAnalyses(generateAnalyses())
                .setRemarks(REMARKS)
                .setNextAdvisory(getNextAdvisory(true))
                .setReportStatus(AviationWeatherMessage.ReportStatus.NORMAL)
                .build();

        final String serialized = OBJECT_MAPPER.writeValueAsString(advisory);
        final SpaceWeatherAdvisoryAmd82Impl deserialized = OBJECT_MAPPER.readValue(serialized, SpaceWeatherAdvisoryAmd82Impl.class);

        assertThat(deserialized).isEqualTo(advisory);
    }

    @Test
    public void swxPartialTimeCompletionTest() {
        final NextAdvisory partialNextAdvisory = NextAdvisoryImpl.builder()//
                .setTimeSpecifier(NextAdvisory.Type.NEXT_ADVISORY_AT)//
                .setTime(PartialOrCompleteTimeInstant.builder().setPartialTime(PartialDateTime.ofHour(1)).build())//
                .build();

        final SpaceWeatherAdvisoryAmd82Impl advisory = SpaceWeatherAdvisoryAmd82Impl.builder()
                .setIssuingCenter(ISSUING_CENTER)
                .setEffect(Effect.HF_COMMUNICATIONS)
                .setIssueTime(PartialOrCompleteTimeInstant.builder().setPartialTime(PartialDateTime.ofDayHourMinute(27, 1, 31)).build())
                .setPermissibleUsageReason(AviationCodeListUser.PermissibleUsageReason.TEST)
                .setAdvisoryNumber(ADVISORY_NUMBER)
                .addAllAnalyses(generateAnalyses()
                        .map(analysis -> analysis.toBuilder()
                                .mutateTime(time -> time.clearCompleteTime())
                                .build())
                )
                .setRemarks(REMARKS)
                .setNextAdvisory(partialNextAdvisory)
                .setReportStatus(AviationWeatherMessage.ReportStatus.NORMAL)
                .build();

        final ZonedDateTime referenceTime = ZonedDateTime.parse("2020-02-27T00:00Z");
        final SpaceWeatherAdvisoryAmd82 completedAdvisory = advisory.toBuilder().withAllTimesComplete(referenceTime).build();
        assertThat(nullableCompleteTime(completedAdvisory.getIssueTime())).as("issueTime").isEqualTo(ZonedDateTime.parse("2020-02-27T01:31Z"));
        assertThat(nullableCompleteTime(completedAdvisory.getNextAdvisory().getTime())).as("nextAdvisory").isEqualTo(ZonedDateTime.parse("2020-02-28T01:00Z"));
        final Iterator<SpaceWeatherAdvisoryAnalysis> completedAnalyses = completedAdvisory.getAnalyses().iterator();
        assertThat(nullableCompleteTime(completedAnalyses.next().getTime())).as("observation").isEqualTo(ZonedDateTime.parse("2020-02-27T01:00Z"));
        assertThat(nullableCompleteTime(completedAnalyses.next().getTime())).as("forecast +6").isEqualTo(ZonedDateTime.parse("2020-02-27T02:00Z"));
        assertThat(nullableCompleteTime(completedAnalyses.next().getTime())).as("forecast +12").isEqualTo(ZonedDateTime.parse("2020-02-27T03:00Z"));
        assertThat(nullableCompleteTime(completedAnalyses.next().getTime())).as("forecast +18").isEqualTo(ZonedDateTime.parse("2020-02-27T04:00Z"));
        assertThat(nullableCompleteTime(completedAnalyses.next().getTime())).as("forecast +24").isEqualTo(ZonedDateTime.parse("2020-02-27T05:00Z"));
        assertThat(completedAnalyses.hasNext()).as("no more analyses").isFalse();
        assertThat(completedAdvisory.areAllTimeReferencesComplete()).as("all times are complete").isTrue();
    }

    @Test
    public void buildSWXWithMultipleReplaceAdvisoryNumbers() throws Exception {
        final SpaceWeatherAdvisoryAmd82Impl advisory = SpaceWeatherAdvisoryAmd82Impl.builder()
                .setIssuingCenter(ISSUING_CENTER)
                .setEffect(Effect.HF_COMMUNICATIONS)
                .setIssueTime(PartialOrCompleteTimeInstant.builder().setCompleteTime(ZonedDateTime.parse("2020-02-27T01:00Z[UTC]")).build())
                .setPermissibleUsageReason(AviationCodeListUser.PermissibleUsageReason.TEST)
                .setAdvisoryNumber(ADVISORY_NUMBER)
                .addAllReplaceAdvisoryNumbers(getReplacementNumbers(13, 14, 15))
                .addAllAnalyses(generateAnalyses())
                .setRemarks(REMARKS)
                .setNextAdvisory(getNextAdvisory(true))
                .build();

        assertThat(advisory.getReplaceAdvisoryNumbers()).hasSize(3);
        assertThat(advisory.getReplaceAdvisoryNumbers().get(0).getYear()).isEqualTo(2020);
        assertThat(advisory.getReplaceAdvisoryNumbers().get(0).getSerialNumber()).isEqualTo(13);
        assertThat(advisory.getReplaceAdvisoryNumbers().get(1).getYear()).isEqualTo(2020);
        assertThat(advisory.getReplaceAdvisoryNumbers().get(1).getSerialNumber()).isEqualTo(14);
        assertThat(advisory.getReplaceAdvisoryNumbers().get(2).getYear()).isEqualTo(2020);
        assertThat(advisory.getReplaceAdvisoryNumbers().get(2).getSerialNumber()).isEqualTo(15);


        final String serialized = OBJECT_MAPPER.writeValueAsString(advisory);
        final SpaceWeatherAdvisoryAmd82Impl deserialized = OBJECT_MAPPER.readValue(serialized, SpaceWeatherAdvisoryAmd82Impl.class);

        assertThat(deserialized).isEqualTo(advisory);
    }
}
