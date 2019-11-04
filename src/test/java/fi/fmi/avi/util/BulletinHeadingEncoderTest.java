package fi.fmi.avi.util;

import static org.junit.Assert.assertEquals;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    @Parameters
    public void testEncode(final BulletinHeading heading, final String expected) {
        final String actual = BulletinHeadingEncoder.encode(heading, MessageFormat.TEXT, ConversionHints.EMPTY);
        assertEquals(expected, actual);
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
        final String actual = BulletinHeadingEncoder.encodeBBBIndicator(heading.getType(), heading.getBulletinAugmentationNumber().orElse(1));
        assertEquals(expected, actual);
    }

    @Test
    @Parameters(source = BBBIndicatorParametersProvider.class)
    public void testEncodeBBBIndicatorBulletinHeading(final BulletinHeading heading, final String expected) {
        final String actual = BulletinHeadingEncoder.encodeBBBIndicator(heading);
        assertEquals(expected, actual);
    }

    @Parameters
    @Test
    public void testEncodeIssueTime(final PartialOrCompleteTimeInstant issueTime, final String expected) {
        final String actual = BulletinHeadingEncoder.encodeIssueTime(issueTime);
        assertEquals(expected, actual);
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
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(issueTime.toString());
        for (final String expectedMissingField : expectedMissingFields) {
            thrown.expectMessage(expectedMissingField);
        }
        BulletinHeadingEncoder.encodeIssueTime(issueTime);
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
        assertEquals(26, BulletinHeadingEncoder.AUGMENTATION_NUMBER_MAX);
    }

    @Test
    public void encodeAugmentationNumber_GivenMinimum_ShouldReturnMinimumChar() {
        assertEquals(BulletinHeadingEncoder.AUGMENTATION_NUMBER_MIN_CHAR,
                BulletinHeadingEncoder.encodeAugmentationNumber(BulletinHeadingEncoder.AUGMENTATION_NUMBER_MIN));
    }

    @Test
    public void encodeAugmentationNumber_GivenMaximum_ShouldReturnMaximumChar() {
        assertEquals(BulletinHeadingEncoder.AUGMENTATION_NUMBER_MAX_CHAR,
                BulletinHeadingEncoder.encodeAugmentationNumber(BulletinHeadingEncoder.AUGMENTATION_NUMBER_MAX));
    }

    @Test
    public void encodeAugmentationNumber_GivenNumberSmallerThanMinimum_ShouldThrowException() {
        final int number = BulletinHeadingEncoder.AUGMENTATION_NUMBER_MIN - 1;
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("<" + number + ">");
        //noinspection ResultOfMethodCallIgnored
        BulletinHeadingEncoder.encodeAugmentationNumber(number);
    }

    @Test
    public void encodeAugmentationNumber_GivenNumberGreaterThanMaximum_ShouldThrowException() {
        final int number = BulletinHeadingEncoder.AUGMENTATION_NUMBER_MAX + 1;
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("<" + number + ">");
        //noinspection ResultOfMethodCallIgnored
        BulletinHeadingEncoder.encodeAugmentationNumber(number);
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
