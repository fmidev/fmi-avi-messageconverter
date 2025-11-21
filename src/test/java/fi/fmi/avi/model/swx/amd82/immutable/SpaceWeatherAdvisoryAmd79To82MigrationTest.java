package fi.fmi.avi.model.swx.amd82.immutable;

import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.swx.amd79.immutable.SWXAmd79Tests;
import fi.fmi.avi.model.swx.amd79.immutable.SpaceWeatherAdvisoryAmd79Impl;
import fi.fmi.avi.model.swx.amd82.Effect;
import fi.fmi.avi.model.swx.amd82.Intensity;
import fi.fmi.avi.model.swx.amd82.NextAdvisory;
import org.junit.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class SpaceWeatherAdvisoryAmd79To82MigrationTest {
    private static final ZonedDateTime BASE_TIME = ZonedDateTime.parse("2026-07-27T03:00:00Z");
    private static final int ADVISORY_NUMBER = 17;
    private static final int REPLACE_ADVISORY_NUMBER = 16;

    @Test
    public void testFromAmd79() {
        final PartialOrCompleteTimeInstant nextAdvisoryTime = SWXAmd82Tests.dayHourMinuteZoneAndCompleteTime(BASE_TIME.plusHours(12));
        final SpaceWeatherAdvisoryAmd79Impl input = SpaceWeatherAdvisoryAmd79Impl.builder()
                .setIssueTime(SWXAmd82Tests.dayHourMinuteZoneAndCompleteTime(BASE_TIME))
                .setIssuingCenter(fi.fmi.avi.model.swx.amd79.immutable.IssuingCenterImpl.builder()
                        .setName("DONLON")
                        .setType("OTHER:SWXC")
                        .build())
                .setAdvisoryNumber(fi.fmi.avi.model.swx.amd79.immutable.AdvisoryNumberImpl.builder()
                        .setYear(BASE_TIME.getYear())
                        .setSerialNumber(ADVISORY_NUMBER)
                        .build())
                .setReplaceAdvisoryNumber(fi.fmi.avi.model.swx.amd79.immutable.AdvisoryNumberImpl.builder()
                        .setYear(BASE_TIME.getYear())
                        .setSerialNumber(REPLACE_ADVISORY_NUMBER)
                        .build())
                .addPhenomena(
                        fi.fmi.avi.model.swx.amd79.SpaceWeatherPhenomenon.RADIATION_MOD,
                        // Different intensities are logically not allowed, but this is not enforced by the model
                        // This is just to ensure we'll use the maximum intensity
                        fi.fmi.avi.model.swx.amd79.SpaceWeatherPhenomenon.RADIATION_SEV
                )
                .addAllAnalyses(SWXAmd79Tests.generateAnalyses(BASE_TIME))
                .setNextAdvisory(fi.fmi.avi.model.swx.amd79.immutable.NextAdvisoryImpl.builder()
                        .setTimeSpecifier(fi.fmi.avi.model.swx.amd79.NextAdvisory.Type.NEXT_ADVISORY_AT)
                        .setTime(nextAdvisoryTime)
                        .build())
                .build();

        final SpaceWeatherAdvisoryAmd82Impl result = SpaceWeatherAdvisoryAmd82Impl.Builder.fromAmd79(input).build();

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(SpaceWeatherAdvisoryAmd82Impl.builder()
                        .setIssueTime(SWXAmd82Tests.dayHourMinuteZoneAndCompleteTime(BASE_TIME))
                        .setIssuingCenter(IssuingCenterImpl.builder()
                                .setName("DONLON")
                                .setType("OTHER:SWXC")
                                .build())
                        .setEffect(Effect.RADIATION_AT_FLIGHT_LEVELS)
                        .setAdvisoryNumber(AdvisoryNumberImpl.builder()
                                .setYear(BASE_TIME.getYear())
                                .setSerialNumber(ADVISORY_NUMBER)
                                .build())
                        .addReplaceAdvisoryNumbers(AdvisoryNumberImpl.builder()
                                .setYear(BASE_TIME.getYear())
                                .setSerialNumber(REPLACE_ADVISORY_NUMBER)
                                .build())
                        .addAllAnalyses(SWXAmd82Tests.analysisBuilder(BASE_TIME)
                                .addIntensities(Intensity.SEVERE)
                                .generateAnalyses())
                        .setNextAdvisory(NextAdvisoryImpl.builder()
                                .setTimeSpecifier(NextAdvisory.Type.NEXT_ADVISORY_AT)
                                .setTime(nextAdvisoryTime)
                                .build())
                        .build());
    }

    @Test
    public void fromAmd79FailsWithMultipleEffects() {
        final PartialOrCompleteTimeInstant nextAdvisoryTime = SWXAmd82Tests.dayHourMinuteZoneAndCompleteTime(BASE_TIME.plusHours(12));
        final SpaceWeatherAdvisoryAmd79Impl input = SpaceWeatherAdvisoryAmd79Impl.builder()
                .setIssueTime(SWXAmd82Tests.dayHourMinuteZoneAndCompleteTime(BASE_TIME))
                .setIssuingCenter(fi.fmi.avi.model.swx.amd79.immutable.IssuingCenterImpl.builder()
                        .setName("DONLON")
                        .setType("OTHER:SWXC")
                        .build())
                .setAdvisoryNumber(fi.fmi.avi.model.swx.amd79.immutable.AdvisoryNumberImpl.builder()
                        .setYear(BASE_TIME.getYear())
                        .setSerialNumber(ADVISORY_NUMBER)
                        .build())
                .setReplaceAdvisoryNumber(fi.fmi.avi.model.swx.amd79.immutable.AdvisoryNumberImpl.builder()
                        .setYear(BASE_TIME.getYear())
                        .setSerialNumber(REPLACE_ADVISORY_NUMBER)
                        .build())
                .addPhenomena(
                        fi.fmi.avi.model.swx.amd79.SpaceWeatherPhenomenon.RADIATION_MOD,
                        fi.fmi.avi.model.swx.amd79.SpaceWeatherPhenomenon.GNSS_MOD
                )
                .addAllAnalyses(SWXAmd79Tests.generateAnalyses(BASE_TIME))
                .setNextAdvisory(fi.fmi.avi.model.swx.amd79.immutable.NextAdvisoryImpl.builder()
                        .setTimeSpecifier(fi.fmi.avi.model.swx.amd79.NextAdvisory.Type.NEXT_ADVISORY_AT)
                        .setTime(nextAdvisoryTime)
                        .build())
                .build();

        assertThatIllegalArgumentException().isThrownBy(() -> SpaceWeatherAdvisoryAmd82Impl.Builder.fromAmd79(input));
    }
}
