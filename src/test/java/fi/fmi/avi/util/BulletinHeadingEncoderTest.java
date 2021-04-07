package fi.fmi.avi.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;

import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.model.MessageFormat;
import fi.fmi.avi.model.PartialDateTime;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.bulletin.BulletinHeading;
import fi.fmi.avi.model.bulletin.DataTypeDesignatorT1;
import fi.fmi.avi.model.bulletin.DataTypeDesignatorT2;
import fi.fmi.avi.model.bulletin.immutable.BulletinHeadingImpl;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class BulletinHeadingEncoderTest {
    private static final String GEOGRAPHICAL_DESIGNATOR = "FI";
    private static final int DEFAULT_BULLETIN_NUMBER = 31;
    private static final String DEFAULT_LOCATION_INDICATOR = "EFLK";
    private static final PartialOrCompleteTimeInstant ISSUE_TIME = PartialOrCompleteTimeInstant.createDayHourInstant("250200");
    private static final PartialOrCompleteTimeInstant SIGMET_ISSUE_TIME = PartialOrCompleteTimeInstant.createDayHourInstant("231008");

    private static final BulletinHeadingImpl TAF_BULLETIN_HEADING = BulletinHeadingImpl.builder()//
            .setGeographicalDesignator(GEOGRAPHICAL_DESIGNATOR)//
            .setLocationIndicator(DEFAULT_LOCATION_INDICATOR)//
            .setBulletinNumber(DEFAULT_BULLETIN_NUMBER)//
            .setType(BulletinHeading.Type.NORMAL)//
            .setDataTypeDesignatorT1ForTAC(DataTypeDesignatorT1.FORECASTS)//
            .setDataTypeDesignatorT2(DataTypeDesignatorT2.ForecastsDataTypeDesignatorT2.FCT_AERODROME_VT_LONG)//
            .setIssueTime(ISSUE_TIME).build();
    private static final BulletinHeadingImpl SIGMET_BULLETIN_HEADING = BulletinHeadingImpl.builder()//
            .setDataTypeDesignatorT2(DataTypeDesignatorT2.WarningsDataTypeDesignatorT2.WRN_SIGMET)//
            .setGeographicalDesignator(GEOGRAPHICAL_DESIGNATOR)//
            .setType(BulletinHeading.Type.NORMAL)//
            .setLocationIndicator(DEFAULT_LOCATION_INDICATOR)//
            .setBulletinNumber(DEFAULT_BULLETIN_NUMBER)//
            .setDataTypeDesignatorT1ForTAC(DataTypeDesignatorT1.WARNINGS)//
            .setDataTypeDesignatorT2(DataTypeDesignatorT2.WarningsDataTypeDesignatorT2.WRN_SIGMET)//
            .setIssueTime(SIGMET_ISSUE_TIME).build();

    @Test
    @Parameters
    public void testEncode(final BulletinHeading heading, final String expected) {
        assertThat(BulletinHeadingEncoder.encode(heading, MessageFormat.TEXT, ConversionHints.EMPTY)).isEqualTo(expected);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public Object parametersForTestEncode() {
        final PartialOrCompleteTimeInstant issuetime1 = PartialOrCompleteTimeInstant.createDayHourInstant("251200");
        final PartialOrCompleteTimeInstant issueTime2 = PartialOrCompleteTimeInstant.of(
                PartialOrCompleteTimeInstant.createDayHourInstant("251400").getPartialTime().get(), ZonedDateTime.parse("2019-02-25T14:00Z"));
        final PartialOrCompleteTimeInstant issueTime3 = PartialOrCompleteTimeInstant.of(issueTime2.getCompleteTime().get());

        return new Object[] {//
                new Object[] { TAF_BULLETIN_HEADING, "FTFI31 EFLK 250200" },//
                new Object[] { TAF_BULLETIN_HEADING.toBuilder()
                        .setDataTypeDesignatorT2(DataTypeDesignatorT2.ForecastsDataTypeDesignatorT2.FCT_AERODROME_VT_SHORT)
                        .setIssueTime(issueTime2)//
                        .setType(BulletinHeading.Type.AMENDED)
                        .setBulletinAugmentationNumber(2).build(), "FCFI31 EFLK 251400 AAB" },//
                new Object[] { TAF_BULLETIN_HEADING.toBuilder()
                        .setType(BulletinHeading.Type.CORRECTED)
                        .setBulletinAugmentationNumber(3)
                        .setIssueTime(issuetime1).build(), "FTFI31 EFLK 251200 CCC" },//
                new Object[] { TAF_BULLETIN_HEADING.toBuilder()
                        .setType(BulletinHeading.Type.DELAYED)
                        .setBulletinAugmentationNumber(1)
                        .setBulletinNumber(32)
                        .setIssueTime(issuetime1)
                        .setLocationIndicator("AAAA").build(), "FTFI32 AAAA 251200 RRA" },//
                // SIGMETs
                new Object[] { SIGMET_BULLETIN_HEADING.toBuilder().setIssueTime(issueTime3).build(), "WSFI31 EFLK 251400" },
                new Object[] { SIGMET_BULLETIN_HEADING.toBuilder().setBulletinNumber(32).setLocationIndicator("AAAA").setIssueTime(issuetime1).build(),
                        "WSFI32 AAAA 251200" } };
    }

    @Test
    @Parameters(source = BBBIndicatorParametersProvider.class)
    public void testEncodeBBBIndicatorBulletinHeadingTypeString(final BulletinHeading heading, final String expected) {
        assertThat(BulletinHeadingEncoder.encodeBBBIndicator(heading.getType(), heading.getBulletinAugmentationNumber().orElse(1))).isEqualTo(expected);
    }

    @Test
    @Parameters(source = BBBIndicatorParametersProvider.class)
    public void testEncodeBBBIndicatorBulletinHeading(final BulletinHeading heading, final String expected) {
        assertThat(BulletinHeadingEncoder.encodeBBBIndicator(heading)).isEqualTo(expected);
    }

    @Parameters
    @Test
    public void testEncodeIssueTime(final PartialOrCompleteTimeInstant issueTime, final String expected) {
        assertThat(BulletinHeadingEncoder.encodeIssueTime(issueTime)).isEqualTo(expected);
    }

    public Object parametersForTestEncodeIssueTime() {
        return new Object[][] {//
                new Object[] { PartialOrCompleteTimeInstant.of(PartialDateTime.of(12, 34, 51, ZoneOffset.UTC)), "123451" }, //
                new Object[] { PartialOrCompleteTimeInstant.of(PartialDateTime.of(2, 3, 4, ZoneOffset.UTC)), "020304" }, //
        };
    }

    @Parameters
    @Test
    public void testEncodeIssueTimeMissingFields(final PartialOrCompleteTimeInstant issueTime, final Collection<String> expectedMissingFields) {
        assertThatIllegalArgumentException()//
                .isThrownBy(() -> BulletinHeadingEncoder.encodeIssueTime(issueTime))//
                .withMessageContaining(issueTime.toString())//
                .satisfies(
                        exception -> expectedMissingFields.forEach(expectedMissingField -> assertThat(exception).hasMessageContaining(expectedMissingField)));
    }

    public Object parametersForTestEncodeIssueTimeMissingFields() {
        final String day = "day";
        final String hour = "hour";
        final String minute = "minute";
        return new Object[][] {//
                new Object[] { PartialOrCompleteTimeInstant.of(PartialDateTime.ofHourMinute(1, 1)), Collections.singletonList(day) }, //
                new Object[] { PartialOrCompleteTimeInstant.of(PartialDateTime.ofDayHour(1, 1)), Collections.singletonList(minute) }, //
                new Object[] { PartialOrCompleteTimeInstant.of(PartialDateTime.of(PartialDateTime.PartialField.DAY, 1)), Arrays.asList(hour, minute) }, //
                new Object[] { PartialOrCompleteTimeInstant.of(PartialDateTime.of(PartialDateTime.PartialField.HOUR, 1)), Arrays.asList(day, minute) }, //
                new Object[] { PartialOrCompleteTimeInstant.of(PartialDateTime.of(PartialDateTime.PartialField.MINUTE, 1)), Arrays.asList(day, hour) }, //
        };
    }

    @Test
    public void augmentationNumberMaximum_is_26() {
        assertThat(BulletinHeadingEncoder.AUGMENTATION_NUMBER_MAX).isEqualTo(26);
    }

    @Test
    public void encodeAugmentationNumber_GivenMinimum_ShouldReturnMinimumChar() {
        assertThat(BulletinHeadingEncoder.encodeAugmentationNumber(BulletinHeadingEncoder.AUGMENTATION_NUMBER_MIN))//
                .isEqualTo(BulletinHeadingEncoder.AUGMENTATION_NUMBER_MIN_CHAR);
    }

    @Test
    public void encodeAugmentationNumber_GivenMaximum_ShouldReturnMaximumChar() {
        assertThat(BulletinHeadingEncoder.encodeAugmentationNumber(BulletinHeadingEncoder.AUGMENTATION_NUMBER_MAX))//
                .isEqualTo(BulletinHeadingEncoder.AUGMENTATION_NUMBER_MAX_CHAR);
    }

    @Test
    public void encodeAugmentationNumber_GivenNumberSmallerThanMinimum_ShouldThrowException() {
        final int number = BulletinHeadingEncoder.AUGMENTATION_NUMBER_MIN - 1;
        assertThatIllegalArgumentException()//
                .isThrownBy(() -> {
                    //noinspection ResultOfMethodCallIgnored
                    BulletinHeadingEncoder.encodeAugmentationNumber(number);
                })//
                .withMessageContaining("<" + number + ">");
    }

    @Test
    public void encodeAugmentationNumber_GivenNumberGreaterThanMaximum_ShouldThrowException() {
        final int number = BulletinHeadingEncoder.AUGMENTATION_NUMBER_MAX + 1;
        assertThatIllegalArgumentException()//
                .isThrownBy(() -> {
                    //noinspection ResultOfMethodCallIgnored
                    BulletinHeadingEncoder.encodeAugmentationNumber(number);
                })//
                .withMessageContaining("<" + number + ">");
    }

    public static class BBBIndicatorParametersProvider {
        public static Object[][] provideBBBIndicatorParameters() {
            return new Object[][] {//
                    new Object[] { TAF_BULLETIN_HEADING, "" }, //
                    new Object[] { TAF_BULLETIN_HEADING.toBuilder().setType(BulletinHeading.Type.AMENDED).setBulletinAugmentationNumber(3).build(), "AAC" }, //
                    new Object[] { TAF_BULLETIN_HEADING.toBuilder().setType(BulletinHeading.Type.CORRECTED).setBulletinAugmentationNumber(2).build(), "CCB" },//
                    new Object[] { TAF_BULLETIN_HEADING.toBuilder().setType(BulletinHeading.Type.DELAYED).setBulletinAugmentationNumber(4).build(), "RRD" }, //
            };
        }
    }
}
