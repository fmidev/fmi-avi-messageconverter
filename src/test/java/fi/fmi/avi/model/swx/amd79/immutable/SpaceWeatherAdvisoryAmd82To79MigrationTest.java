package fi.fmi.avi.model.swx.amd79.immutable;

import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.swx.amd79.NextAdvisory;
import fi.fmi.avi.model.swx.amd79.SpaceWeatherAdvisoryAnalysis;
import fi.fmi.avi.model.swx.amd79.SpaceWeatherPhenomenon;
import fi.fmi.avi.model.swx.amd79.SpaceWeatherRegion;
import fi.fmi.avi.model.swx.amd82.immutable.SWXAmd82Tests;
import fi.fmi.avi.model.swx.amd82.immutable.SpaceWeatherAdvisoryAmd82Impl;
import org.junit.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class SpaceWeatherAdvisoryAmd82To79MigrationTest {
    private static final ZonedDateTime BASE_TIME = ZonedDateTime.parse("2026-07-27T03:00:00Z");
    private static final IssuingCenterImpl ISSUING_CENTER_AMD79 = IssuingCenterImpl.builder()
            .setName("DONLON")
            .setType("OTHER:SWXC")
            .build();
    private static final fi.fmi.avi.model.swx.amd82.immutable.IssuingCenterImpl ISSUING_CENTER_AMD82 = fi.fmi.avi.model.swx.amd82.immutable.IssuingCenterImpl.builder()
            .setName("DONLON")
            .setType("OTHER:SWXC")
            .build();
    private static final int ADVISORY_NUMBER = 17;
    private static final int REPLACE_ADVISORY_NUMBER = 16;

    @Test
    public void testFromAmd82() {
        final PartialOrCompleteTimeInstant nextAdvisoryTime = SWXAmd79Tests.dayHourMinuteZoneAndCompleteTime(BASE_TIME.plusHours(12));
        final SpaceWeatherAdvisoryAmd82Impl input = SpaceWeatherAdvisoryAmd82Impl.builder()
                .setIssueTime(SWXAmd79Tests.dayHourMinuteZoneAndCompleteTime(BASE_TIME))
                .setIssuingCenter(ISSUING_CENTER_AMD82)
                .setEffect(fi.fmi.avi.model.swx.amd82.Effect.RADIATION_AT_FLIGHT_LEVELS)
                .setAdvisoryNumber(fi.fmi.avi.model.swx.amd82.immutable.AdvisoryNumberImpl.builder()
                        .setYear(BASE_TIME.getYear())
                        .setSerialNumber(ADVISORY_NUMBER)
                        .build())
                .addReplaceAdvisoryNumbers(fi.fmi.avi.model.swx.amd82.immutable.AdvisoryNumberImpl.builder()
                        .setYear(BASE_TIME.getYear())
                        .setSerialNumber(REPLACE_ADVISORY_NUMBER)
                        .build())
                .addAllAnalyses(SWXAmd82Tests.analysisBuilder(BASE_TIME)
                        .addObservationIntensities(fi.fmi.avi.model.swx.amd82.Intensity.SEVERE)
                        .addForecastIntensities(fi.fmi.avi.model.swx.amd82.Intensity.MODERATE)
                        .generateAnalyses()
                )
                .setNextAdvisory(fi.fmi.avi.model.swx.amd82.immutable.NextAdvisoryImpl.builder()
                        .setTimeSpecifier(fi.fmi.avi.model.swx.amd82.NextAdvisory.Type.NEXT_ADVISORY_AT)
                        .setTime(nextAdvisoryTime)
                        .build())
                .build();

        final SpaceWeatherAdvisoryAmd79Impl result = SpaceWeatherAdvisoryAmd79Impl.Builder.fromAmd82(input, false).build();

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(SpaceWeatherAdvisoryAmd79Impl.builder()
                        .setIssueTime(SWXAmd79Tests.dayHourMinuteZoneAndCompleteTime(BASE_TIME))
                        .setIssuingCenter(ISSUING_CENTER_AMD79)
                        .setAdvisoryNumber(AdvisoryNumberImpl.builder()
                                .setYear(BASE_TIME.getYear())
                                .setSerialNumber(ADVISORY_NUMBER)
                                .build())
                        .setReplaceAdvisoryNumber(AdvisoryNumberImpl.builder()
                                .setYear(BASE_TIME.getYear())
                                .setSerialNumber(REPLACE_ADVISORY_NUMBER)
                                .build())
                        .addPhenomena(SpaceWeatherPhenomenon.RADIATION_SEV)
                        .addAllAnalyses(SWXAmd79Tests.generateAnalyses(BASE_TIME))
                        .setNextAdvisory(NextAdvisoryImpl.builder()
                                .setTimeSpecifier(NextAdvisory.Type.NEXT_ADVISORY_AT)
                                .setTime(nextAdvisoryTime)
                                .build())
                        .build());
    }

    @Test
    public void fromAmd82UsesAllAnalysesToDetermineIntensity() {
        final PartialOrCompleteTimeInstant nextAdvisoryTime = SWXAmd79Tests.dayHourMinuteZoneAndCompleteTime(BASE_TIME.plusHours(12));
        final SpaceWeatherAdvisoryAmd82Impl input = SpaceWeatherAdvisoryAmd82Impl.builder()
                .setIssueTime(SWXAmd79Tests.dayHourMinuteZoneAndCompleteTime(BASE_TIME))
                .setIssuingCenter(ISSUING_CENTER_AMD82)
                .setEffect(fi.fmi.avi.model.swx.amd82.Effect.RADIATION_AT_FLIGHT_LEVELS)
                .setAdvisoryNumber(fi.fmi.avi.model.swx.amd82.immutable.AdvisoryNumberImpl.builder()
                        .setYear(BASE_TIME.getYear())
                        .setSerialNumber(ADVISORY_NUMBER)
                        .build())
                .addReplaceAdvisoryNumbers(fi.fmi.avi.model.swx.amd82.immutable.AdvisoryNumberImpl.builder()
                        .setYear(BASE_TIME.getYear())
                        .setSerialNumber(REPLACE_ADVISORY_NUMBER)
                        .build())
                .addAllAnalyses(SWXAmd82Tests.analysisBuilder(BASE_TIME)
                        .addObservationIntensities(fi.fmi.avi.model.swx.amd82.Intensity.MODERATE)
                        .addForecastIntensities(fi.fmi.avi.model.swx.amd82.Intensity.SEVERE)
                        .addAllLocationIndicators(SWXAmd82Tests.LATITUDE_BANDS)
                        .generateAnalyses())
                .setNextAdvisory(fi.fmi.avi.model.swx.amd82.immutable.NextAdvisoryImpl.builder()
                        .setTimeSpecifier(fi.fmi.avi.model.swx.amd82.NextAdvisory.Type.NEXT_ADVISORY_AT)
                        .setTime(nextAdvisoryTime)
                        .build())
                .build();

        final SpaceWeatherAdvisoryAmd79Impl result = SpaceWeatherAdvisoryAmd79Impl.Builder.fromAmd82(input, false).build();

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(SpaceWeatherAdvisoryAmd79Impl.builder()
                        .setIssueTime(SWXAmd79Tests.dayHourMinuteZoneAndCompleteTime(BASE_TIME))
                        .setIssuingCenter(ISSUING_CENTER_AMD79)
                        .setAdvisoryNumber(AdvisoryNumberImpl.builder()
                                .setYear(BASE_TIME.getYear())
                                .setSerialNumber(ADVISORY_NUMBER)
                                .build())
                        .setReplaceAdvisoryNumber(AdvisoryNumberImpl.builder()
                                .setYear(BASE_TIME.getYear())
                                .setSerialNumber(REPLACE_ADVISORY_NUMBER)
                                .build())
                        .addPhenomena(SpaceWeatherPhenomenon.RADIATION_SEV)
                        .addAllAnalyses(SWXAmd79Tests.generateAnalyses(BASE_TIME))
                        .setNextAdvisory(NextAdvisoryImpl.builder()
                                .setTimeSpecifier(NextAdvisory.Type.NEXT_ADVISORY_AT)
                                .setTime(nextAdvisoryTime)
                                .build())
                        .build());
    }

    @Test
    public void fromAmd82withNilObservation() {
        final PartialOrCompleteTimeInstant nextAdvisoryTime = SWXAmd79Tests.dayHourMinuteZoneAndCompleteTime(BASE_TIME.plusHours(12));
        final SpaceWeatherAdvisoryAmd82Impl input = SpaceWeatherAdvisoryAmd82Impl.builder()
                .setIssueTime(SWXAmd79Tests.dayHourMinuteZoneAndCompleteTime(BASE_TIME))
                .setIssuingCenter(ISSUING_CENTER_AMD82)
                .setEffect(fi.fmi.avi.model.swx.amd82.Effect.RADIATION_AT_FLIGHT_LEVELS)
                .setAdvisoryNumber(fi.fmi.avi.model.swx.amd82.immutable.AdvisoryNumberImpl.builder()
                        .setYear(BASE_TIME.getYear())
                        .setSerialNumber(ADVISORY_NUMBER)
                        .build())
                .addReplaceAdvisoryNumbers(fi.fmi.avi.model.swx.amd82.immutable.AdvisoryNumberImpl.builder()
                        .setYear(BASE_TIME.getYear())
                        .setSerialNumber(REPLACE_ADVISORY_NUMBER)
                        .build())
                .addAllAnalyses(SWXAmd82Tests.analysisBuilder(BASE_TIME)
                        .addObservationIntensities(fi.fmi.avi.model.swx.amd82.Intensity.SEVERE)
                        .addForecastIntensities(fi.fmi.avi.model.swx.amd82.Intensity.MODERATE)
                        .addAllLocationIndicators(SWXAmd82Tests.LATITUDE_BANDS)
                        .generateAnalyses()
                        .map(analysis -> analysis.getAnalysisType() == fi.fmi.avi.model.swx.amd82.SpaceWeatherAdvisoryAnalysis.Type.OBSERVATION
                                ? fi.fmi.avi.model.swx.amd82.immutable.SpaceWeatherAdvisoryAnalysisImpl.Builder.from(analysis)
                                .setNilReason(fi.fmi.avi.model.swx.amd82.SpaceWeatherAdvisoryAnalysis.NilReason.NO_SWX_EXPECTED)
                                .clearIntensityAndRegions()
                                .build()
                                : analysis
                        )
                )
                .setNextAdvisory(fi.fmi.avi.model.swx.amd82.immutable.NextAdvisoryImpl.builder()
                        .setTimeSpecifier(fi.fmi.avi.model.swx.amd82.NextAdvisory.Type.NEXT_ADVISORY_AT)
                        .setTime(nextAdvisoryTime)
                        .build())
                .build();

        final SpaceWeatherAdvisoryAmd79Impl result = SpaceWeatherAdvisoryAmd79Impl.Builder.fromAmd82(input, false).build();

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(SpaceWeatherAdvisoryAmd79Impl.builder()
                        .setIssueTime(SWXAmd79Tests.dayHourMinuteZoneAndCompleteTime(BASE_TIME))
                        .setIssuingCenter(ISSUING_CENTER_AMD79)
                        .setAdvisoryNumber(AdvisoryNumberImpl.builder()
                                .setYear(BASE_TIME.getYear())
                                .setSerialNumber(ADVISORY_NUMBER)
                                .build())
                        .setReplaceAdvisoryNumber(AdvisoryNumberImpl.builder()
                                .setYear(BASE_TIME.getYear())
                                .setSerialNumber(REPLACE_ADVISORY_NUMBER)
                                .build())
                        .addPhenomena(SpaceWeatherPhenomenon.RADIATION_MOD)
                        .addAllAnalyses(SWXAmd79Tests.generateAnalyses(BASE_TIME)
                                .map(analysis ->
                                        analysis.getAnalysisType() == SpaceWeatherAdvisoryAnalysis.Type.OBSERVATION
                                                ? SpaceWeatherAdvisoryAnalysisImpl.Builder.from(analysis)
                                                .setNilPhenomenonReason(SpaceWeatherAdvisoryAnalysis.NilPhenomenonReason.NO_PHENOMENON_EXPECTED)
                                                .clearRegions()
                                                .build()
                                                : analysis
                                )
                        )
                        .setNextAdvisory(NextAdvisoryImpl.builder()
                                .setTimeSpecifier(NextAdvisory.Type.NEXT_ADVISORY_AT)
                                .setTime(nextAdvisoryTime)
                                .build())
                        .build());
    }

    @Test
    public void fromAmd82withAllAnalysesNil() {
        final PartialOrCompleteTimeInstant nextAdvisoryTime = SWXAmd79Tests.dayHourMinuteZoneAndCompleteTime(BASE_TIME.plusHours(12));
        final SpaceWeatherAdvisoryAmd82Impl input = SpaceWeatherAdvisoryAmd82Impl.builder()
                .setIssueTime(SWXAmd79Tests.dayHourMinuteZoneAndCompleteTime(BASE_TIME))
                .setIssuingCenter(ISSUING_CENTER_AMD82)
                .setEffect(fi.fmi.avi.model.swx.amd82.Effect.RADIATION_AT_FLIGHT_LEVELS)
                .setAdvisoryNumber(fi.fmi.avi.model.swx.amd82.immutable.AdvisoryNumberImpl.builder()
                        .setYear(BASE_TIME.getYear())
                        .setSerialNumber(ADVISORY_NUMBER)
                        .build())
                .addReplaceAdvisoryNumbers(fi.fmi.avi.model.swx.amd82.immutable.AdvisoryNumberImpl.builder()
                        .setYear(BASE_TIME.getYear())
                        .setSerialNumber(REPLACE_ADVISORY_NUMBER)
                        .build())
                .addAllAnalyses(SWXAmd82Tests.analysisBuilder(BASE_TIME)
                        .setIntensityAndRegionCount(0)
                        .addNilReasons(fi.fmi.avi.model.swx.amd82.SpaceWeatherAdvisoryAnalysis.NilReason.values())
                        .generateAnalyses())
                .setNextAdvisory(fi.fmi.avi.model.swx.amd82.immutable.NextAdvisoryImpl.builder()
                        .setTimeSpecifier(fi.fmi.avi.model.swx.amd82.NextAdvisory.Type.NEXT_ADVISORY_AT)
                        .setTime(nextAdvisoryTime)
                        .build())
                .build();

        final SpaceWeatherAdvisoryAmd79Impl result = SpaceWeatherAdvisoryAmd79Impl.Builder.fromAmd82(input, false).build();

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(SpaceWeatherAdvisoryAmd79Impl.builder()
                        .setIssueTime(SWXAmd79Tests.dayHourMinuteZoneAndCompleteTime(BASE_TIME))
                        .setIssuingCenter(ISSUING_CENTER_AMD79)
                        .setAdvisoryNumber(AdvisoryNumberImpl.builder()
                                .setYear(BASE_TIME.getYear())
                                .setSerialNumber(ADVISORY_NUMBER)
                                .build())
                        .setReplaceAdvisoryNumber(AdvisoryNumberImpl.builder()
                                .setYear(BASE_TIME.getYear())
                                .setSerialNumber(REPLACE_ADVISORY_NUMBER)
                                .build())
                        .addPhenomena(SpaceWeatherPhenomenon.RADIATION_MOD)
                        .addAllAnalyses(SWXAmd79Tests.analysisBuilder(BASE_TIME)
                                .setRegionsCount(0)
                                .addNilPhenomenonReasons(SpaceWeatherAdvisoryAnalysis.NilPhenomenonReason.values())
                                .generateAnalyses())
                        .setNextAdvisory(NextAdvisoryImpl.builder()
                                .setTimeSpecifier(NextAdvisory.Type.NEXT_ADVISORY_AT)
                                .setTime(nextAdvisoryTime)
                                .build())
                        .build());
    }

    @Test
    public void fromAmd82withDayside() {
        final PartialOrCompleteTimeInstant nextAdvisoryTime = SWXAmd79Tests.dayHourMinuteZoneAndCompleteTime(BASE_TIME.plusHours(12));
        final SpaceWeatherAdvisoryAmd82Impl input = SpaceWeatherAdvisoryAmd82Impl.builder()
                .setIssueTime(SWXAmd79Tests.dayHourMinuteZoneAndCompleteTime(BASE_TIME))
                .setIssuingCenter(ISSUING_CENTER_AMD82)
                .setEffect(fi.fmi.avi.model.swx.amd82.Effect.RADIATION_AT_FLIGHT_LEVELS)
                .setAdvisoryNumber(fi.fmi.avi.model.swx.amd82.immutable.AdvisoryNumberImpl.builder()
                        .setYear(BASE_TIME.getYear())
                        .setSerialNumber(ADVISORY_NUMBER)
                        .build())
                .addReplaceAdvisoryNumbers(fi.fmi.avi.model.swx.amd82.immutable.AdvisoryNumberImpl.builder()
                        .setYear(BASE_TIME.getYear())
                        .setSerialNumber(REPLACE_ADVISORY_NUMBER)
                        .build())
                .addAllAnalyses(SWXAmd82Tests.analysisBuilder(BASE_TIME)
                        .addIntensities(fi.fmi.avi.model.swx.amd82.Intensity.MODERATE)
                        .setRegionsPerIntensityFromLocationIndicators()
                        .addLocationIndicators(fi.fmi.avi.model.swx.amd82.SpaceWeatherRegion.SpaceWeatherLocation.DAYSIDE)
                        .generateAnalyses())
                .setNextAdvisory(fi.fmi.avi.model.swx.amd82.immutable.NextAdvisoryImpl.builder()
                        .setTimeSpecifier(fi.fmi.avi.model.swx.amd82.NextAdvisory.Type.NEXT_ADVISORY_AT)
                        .setTime(nextAdvisoryTime)
                        .build())
                .build();

        final SpaceWeatherAdvisoryAmd79Impl result = SpaceWeatherAdvisoryAmd79Impl.Builder.fromAmd82(input, false).build();

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(SpaceWeatherAdvisoryAmd79Impl.builder()
                        .setIssueTime(SWXAmd79Tests.dayHourMinuteZoneAndCompleteTime(BASE_TIME))
                        .setIssuingCenter(ISSUING_CENTER_AMD79)
                        .setAdvisoryNumber(AdvisoryNumberImpl.builder()
                                .setYear(BASE_TIME.getYear())
                                .setSerialNumber(ADVISORY_NUMBER)
                                .build())
                        .setReplaceAdvisoryNumber(AdvisoryNumberImpl.builder()
                                .setYear(BASE_TIME.getYear())
                                .setSerialNumber(REPLACE_ADVISORY_NUMBER)
                                .build())
                        .addPhenomena(SpaceWeatherPhenomenon.RADIATION_MOD)
                        .addAllAnalyses(SWXAmd79Tests.analysisBuilder(BASE_TIME)
                                .setRegionsCountFromLocationIndicators()
                                .addLocationIndicators(SpaceWeatherRegion.SpaceWeatherLocation.DAYLIGHT_SIDE)
                                .generateAnalyses())
                        .setNextAdvisory(NextAdvisoryImpl.builder()
                                .setTimeSpecifier(NextAdvisory.Type.NEXT_ADVISORY_AT)
                                .setTime(nextAdvisoryTime)
                                .build())
                        .build());
    }

    @Test
    public void fromAmd82FailsOnNightside() {
        final PartialOrCompleteTimeInstant nextAdvisoryTime = SWXAmd79Tests.dayHourMinuteZoneAndCompleteTime(BASE_TIME.plusHours(12));
        final SpaceWeatherAdvisoryAmd82Impl input = SpaceWeatherAdvisoryAmd82Impl.builder()
                .setIssueTime(SWXAmd79Tests.dayHourMinuteZoneAndCompleteTime(BASE_TIME))
                .setIssuingCenter(ISSUING_CENTER_AMD82)
                .setEffect(fi.fmi.avi.model.swx.amd82.Effect.RADIATION_AT_FLIGHT_LEVELS)
                .setAdvisoryNumber(fi.fmi.avi.model.swx.amd82.immutable.AdvisoryNumberImpl.builder()
                        .setYear(BASE_TIME.getYear())
                        .setSerialNumber(ADVISORY_NUMBER)
                        .build())
                .addReplaceAdvisoryNumbers(fi.fmi.avi.model.swx.amd82.immutable.AdvisoryNumberImpl.builder()
                        .setYear(BASE_TIME.getYear())
                        .setSerialNumber(REPLACE_ADVISORY_NUMBER)
                        .build())
                .addAllAnalyses(SWXAmd82Tests.analysisBuilder(BASE_TIME)
                        .addIntensities(fi.fmi.avi.model.swx.amd82.Intensity.MODERATE)
                        .setRegionsPerIntensityFromLocationIndicators()
                        .addLocationIndicators(fi.fmi.avi.model.swx.amd82.SpaceWeatherRegion.SpaceWeatherLocation.NIGHTSIDE)
                        .generateAnalyses())
                .setNextAdvisory(fi.fmi.avi.model.swx.amd82.immutable.NextAdvisoryImpl.builder()
                        .setTimeSpecifier(fi.fmi.avi.model.swx.amd82.NextAdvisory.Type.NEXT_ADVISORY_AT)
                        .setTime(nextAdvisoryTime)
                        .build())
                .build();

        assertThatIllegalArgumentException()
                .isThrownBy(() -> SpaceWeatherAdvisoryAmd79Impl.Builder.fromAmd82(input, false))
                .withMessageContaining("Unable to convert regions");
    }

    @Test
    public void fromAmd82FailsOnNightsideAsLastRegion() {
        final PartialOrCompleteTimeInstant nextAdvisoryTime = SWXAmd79Tests.dayHourMinuteZoneAndCompleteTime(BASE_TIME.plusHours(12));
        final SpaceWeatherAdvisoryAmd82Impl input = SpaceWeatherAdvisoryAmd82Impl.builder()
                .setIssueTime(SWXAmd79Tests.dayHourMinuteZoneAndCompleteTime(BASE_TIME))
                .setIssuingCenter(ISSUING_CENTER_AMD82)
                .setEffect(fi.fmi.avi.model.swx.amd82.Effect.RADIATION_AT_FLIGHT_LEVELS)
                .setAdvisoryNumber(fi.fmi.avi.model.swx.amd82.immutable.AdvisoryNumberImpl.builder()
                        .setYear(BASE_TIME.getYear())
                        .setSerialNumber(ADVISORY_NUMBER)
                        .build())
                .addReplaceAdvisoryNumbers(fi.fmi.avi.model.swx.amd82.immutable.AdvisoryNumberImpl.builder()
                        .setYear(BASE_TIME.getYear())
                        .setSerialNumber(REPLACE_ADVISORY_NUMBER)
                        .build())
                .addAllAnalyses(SWXAmd82Tests.analysisBuilder(BASE_TIME)
                        .addIntensities(fi.fmi.avi.model.swx.amd82.Intensity.MODERATE)
                        .setRegionsPerIntensityFromLocationIndicators()
                        .addLocationIndicators(
                                fi.fmi.avi.model.swx.amd82.SpaceWeatherRegion.SpaceWeatherLocation.HIGH_NORTHERN_HEMISPHERE,
                                fi.fmi.avi.model.swx.amd82.SpaceWeatherRegion.SpaceWeatherLocation.MIDDLE_NORTHERN_HEMISPHERE,
                                fi.fmi.avi.model.swx.amd82.SpaceWeatherRegion.SpaceWeatherLocation.NIGHTSIDE)
                        .generateAnalyses())
                .setNextAdvisory(fi.fmi.avi.model.swx.amd82.immutable.NextAdvisoryImpl.builder()
                        .setTimeSpecifier(fi.fmi.avi.model.swx.amd82.NextAdvisory.Type.NEXT_ADVISORY_AT)
                        .setTime(nextAdvisoryTime)
                        .build())
                .build();

        assertThatIllegalArgumentException()
                .isThrownBy(() -> SpaceWeatherAdvisoryAmd79Impl.Builder.fromAmd82(input, false))
                .withMessageContaining("Unable to convert regions");
    }

    @Test
    public void fromAmd82FailsOnDaysideAsLastRegion() {
        final PartialOrCompleteTimeInstant nextAdvisoryTime = SWXAmd79Tests.dayHourMinuteZoneAndCompleteTime(BASE_TIME.plusHours(12));
        final SpaceWeatherAdvisoryAmd82Impl input = SpaceWeatherAdvisoryAmd82Impl.builder()
                .setIssueTime(SWXAmd79Tests.dayHourMinuteZoneAndCompleteTime(BASE_TIME))
                .setIssuingCenter(ISSUING_CENTER_AMD82)
                .setEffect(fi.fmi.avi.model.swx.amd82.Effect.RADIATION_AT_FLIGHT_LEVELS)
                .setAdvisoryNumber(fi.fmi.avi.model.swx.amd82.immutable.AdvisoryNumberImpl.builder()
                        .setYear(BASE_TIME.getYear())
                        .setSerialNumber(ADVISORY_NUMBER)
                        .build())
                .addReplaceAdvisoryNumbers(fi.fmi.avi.model.swx.amd82.immutable.AdvisoryNumberImpl.builder()
                        .setYear(BASE_TIME.getYear())
                        .setSerialNumber(REPLACE_ADVISORY_NUMBER)
                        .build())
                .addAllAnalyses(SWXAmd82Tests.analysisBuilder(BASE_TIME)
                        .addIntensities(fi.fmi.avi.model.swx.amd82.Intensity.MODERATE)
                        .setRegionsPerIntensityFromLocationIndicators()
                        .addLocationIndicators(
                                fi.fmi.avi.model.swx.amd82.SpaceWeatherRegion.SpaceWeatherLocation.HIGH_NORTHERN_HEMISPHERE,
                                fi.fmi.avi.model.swx.amd82.SpaceWeatherRegion.SpaceWeatherLocation.MIDDLE_NORTHERN_HEMISPHERE,
                                fi.fmi.avi.model.swx.amd82.SpaceWeatherRegion.SpaceWeatherLocation.DAYSIDE)
                        .generateAnalyses())
                .setNextAdvisory(fi.fmi.avi.model.swx.amd82.immutable.NextAdvisoryImpl.builder()
                        .setTimeSpecifier(fi.fmi.avi.model.swx.amd82.NextAdvisory.Type.NEXT_ADVISORY_AT)
                        .setTime(nextAdvisoryTime)
                        .build())
                .build();

        assertThatIllegalArgumentException()
                .isThrownBy(() -> SpaceWeatherAdvisoryAmd79Impl.Builder.fromAmd82(input, false))
                .withMessageContaining("Unable to convert regions");
    }

    @Test
    public void fromAmd82FailsOnMultipleIntensityAndRegions() {
        final PartialOrCompleteTimeInstant nextAdvisoryTime = SWXAmd79Tests.dayHourMinuteZoneAndCompleteTime(BASE_TIME.plusHours(12));
        final SpaceWeatherAdvisoryAmd82Impl input = SpaceWeatherAdvisoryAmd82Impl.builder()
                .setIssueTime(SWXAmd79Tests.dayHourMinuteZoneAndCompleteTime(BASE_TIME))
                .setIssuingCenter(ISSUING_CENTER_AMD82)
                .setEffect(fi.fmi.avi.model.swx.amd82.Effect.RADIATION_AT_FLIGHT_LEVELS)
                .setAdvisoryNumber(fi.fmi.avi.model.swx.amd82.immutable.AdvisoryNumberImpl.builder()
                        .setYear(BASE_TIME.getYear())
                        .setSerialNumber(ADVISORY_NUMBER)
                        .build())
                .addReplaceAdvisoryNumbers(fi.fmi.avi.model.swx.amd82.immutable.AdvisoryNumberImpl.builder()
                        .setYear(BASE_TIME.getYear())
                        .setSerialNumber(REPLACE_ADVISORY_NUMBER)
                        .build())
                .addAllAnalyses(SWXAmd82Tests.analysisBuilder(BASE_TIME)
                        .addIntensities(fi.fmi.avi.model.swx.amd82.Intensity.values())
                        .setIntensityAndRegionCount(2)
                        .generateAnalyses())
                .setNextAdvisory(fi.fmi.avi.model.swx.amd82.immutable.NextAdvisoryImpl.builder()
                        .setTimeSpecifier(fi.fmi.avi.model.swx.amd82.NextAdvisory.Type.NEXT_ADVISORY_AT)
                        .setTime(nextAdvisoryTime)
                        .build())
                .build();

        assertThatIllegalArgumentException()
                .isThrownBy(() -> SpaceWeatherAdvisoryAmd79Impl.Builder.fromAmd82(input, false))
                .withMessageContaining("Cannot convert multiple intensity and regions");
    }

    @Test
    public void testFromAmd82LenientReturnsFirstMostSevereConvertibleRegions() {
        final PartialOrCompleteTimeInstant nextAdvisoryTime = SWXAmd79Tests.dayHourMinuteZoneAndCompleteTime(BASE_TIME.plusHours(12));
        final SpaceWeatherAdvisoryAmd82Impl input = SpaceWeatherAdvisoryAmd82Impl.builder()
                .setIssueTime(SWXAmd79Tests.dayHourMinuteZoneAndCompleteTime(BASE_TIME))
                .setIssuingCenter(ISSUING_CENTER_AMD82)
                .setEffect(fi.fmi.avi.model.swx.amd82.Effect.RADIATION_AT_FLIGHT_LEVELS)
                .setAdvisoryNumber(fi.fmi.avi.model.swx.amd82.immutable.AdvisoryNumberImpl.builder()
                        .setYear(BASE_TIME.getYear())
                        .setSerialNumber(ADVISORY_NUMBER)
                        .build())
                .addReplaceAdvisoryNumbers(fi.fmi.avi.model.swx.amd82.immutable.AdvisoryNumberImpl.builder()
                        .setYear(BASE_TIME.getYear())
                        .setSerialNumber(REPLACE_ADVISORY_NUMBER)
                        .build())
                .addAllAnalyses(SWXAmd82Tests.analysisBuilder(BASE_TIME)
                        .setIntensityAndRegionCount(4)
                        .setRegionsPerIntensityCount(2)
                        .addIntensities(
                                fi.fmi.avi.model.swx.amd82.Intensity.MODERATE,
                                fi.fmi.avi.model.swx.amd82.Intensity.SEVERE,
                                fi.fmi.avi.model.swx.amd82.Intensity.SEVERE,
                                fi.fmi.avi.model.swx.amd82.Intensity.SEVERE)
                        .addLocationIndicators(
                                // MODERATE
                                fi.fmi.avi.model.swx.amd82.SpaceWeatherRegion.SpaceWeatherLocation.HIGH_NORTHERN_HEMISPHERE,
                                fi.fmi.avi.model.swx.amd82.SpaceWeatherRegion.SpaceWeatherLocation.MIDDLE_NORTHERN_HEMISPHERE,
                                // SEVERE
                                fi.fmi.avi.model.swx.amd82.SpaceWeatherRegion.SpaceWeatherLocation.EQUATORIAL_LATITUDES_NORTHERN_HEMISPHERE,
                                fi.fmi.avi.model.swx.amd82.SpaceWeatherRegion.SpaceWeatherLocation.DAYSIDE,
                                // SEVERE
                                fi.fmi.avi.model.swx.amd82.SpaceWeatherRegion.SpaceWeatherLocation.EQUATORIAL_LATITUDES_SOUTHERN_HEMISPHERE,
                                fi.fmi.avi.model.swx.amd82.SpaceWeatherRegion.SpaceWeatherLocation.MIDDLE_LATITUDES_SOUTHERN_HEMISPHERE,
                                // SEVERE
                                fi.fmi.avi.model.swx.amd82.SpaceWeatherRegion.SpaceWeatherLocation.HIGH_LATITUDES_SOUTHERN_HEMISPHERE,
                                fi.fmi.avi.model.swx.amd82.SpaceWeatherRegion.SpaceWeatherLocation.HIGH_NORTHERN_HEMISPHERE
                        )
                        .generateAnalyses()
                )
                .setNextAdvisory(fi.fmi.avi.model.swx.amd82.immutable.NextAdvisoryImpl.builder()
                        .setTimeSpecifier(fi.fmi.avi.model.swx.amd82.NextAdvisory.Type.NEXT_ADVISORY_AT)
                        .setTime(nextAdvisoryTime)
                        .build())
                .build();

        final SpaceWeatherAdvisoryAmd79Impl result = SpaceWeatherAdvisoryAmd79Impl.Builder.fromAmd82(input, true).build();

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(SpaceWeatherAdvisoryAmd79Impl.builder()
                        .setIssueTime(SWXAmd79Tests.dayHourMinuteZoneAndCompleteTime(BASE_TIME))
                        .setIssuingCenter(ISSUING_CENTER_AMD79)
                        .setAdvisoryNumber(AdvisoryNumberImpl.builder()
                                .setYear(BASE_TIME.getYear())
                                .setSerialNumber(ADVISORY_NUMBER)
                                .build())
                        .setReplaceAdvisoryNumber(AdvisoryNumberImpl.builder()
                                .setYear(BASE_TIME.getYear())
                                .setSerialNumber(REPLACE_ADVISORY_NUMBER)
                                .build())
                        .addPhenomena(SpaceWeatherPhenomenon.RADIATION_SEV)
                        .addAllAnalyses(SWXAmd79Tests.analysisBuilder(BASE_TIME)
                                .setRegionsCountFromLocationIndicators()
                                .addLocationIndicators(
                                        SpaceWeatherRegion.SpaceWeatherLocation.EQUATORIAL_LATITUDES_SOUTHERN_HEMISPHERE,
                                        SpaceWeatherRegion.SpaceWeatherLocation.MIDDLE_LATITUDES_SOUTHERN_HEMISPHERE
                                )
                                .generateAnalyses())
                        .setNextAdvisory(NextAdvisoryImpl.builder()
                                .setTimeSpecifier(NextAdvisory.Type.NEXT_ADVISORY_AT)
                                .setTime(nextAdvisoryTime)
                                .build())
                        .build());
    }
}
