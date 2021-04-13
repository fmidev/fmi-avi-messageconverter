package fi.fmi.avi.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableMap;

import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.bulletin.BulletinHeading;
import fi.fmi.avi.model.bulletin.DataTypeDesignatorT1;
import fi.fmi.avi.model.bulletin.DataTypeDesignatorT2;
import fi.fmi.avi.model.bulletin.immutable.BulletinHeadingImpl;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class BulletinHeadingDecoderTest {

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

    private static final Map<String, String> AUGMENTATION_INDICATOR_REPLACEMENTS = ImmutableMap.of("COR", "CCA", "RTD", "RRA", "AMD", "AAA");
    private static final ConversionHints EXTENDED_AUGMENTATION_IDENTIFIERS = new ConversionHints();

    static {
        EXTENDED_AUGMENTATION_IDENTIFIERS.put(ConversionHints.KEY_BULLETIN_HEADING_AUGMENTATION_INDICATOR_EXTENSION,
                (BulletinHeadingIndicatorInterpreter) key -> AUGMENTATION_INDICATOR_REPLACEMENTS.getOrDefault(key, key));
    }

    @Test
    @Parameters
    public void decode_bulletin_headings(final String input, final BulletinHeading expected, final ConversionHints conversionHints) {
        assertThat(BulletinHeadingDecoder.decode(input, conversionHints)).isEqualTo(expected);
    }

    public Object parametersForDecode_bulletin_headings() {
        return new Object[] {//
                new Object[] { "FTFI31 EFLK 250200", TAF_BULLETIN_HEADING, ConversionHints.EMPTY }, //
                new Object[] { "FCFI31 EFLK 250200 AAB", TAF_BULLETIN_HEADING.toBuilder()
                        .setDataTypeDesignatorT2(DataTypeDesignatorT2.ForecastsDataTypeDesignatorT2.FCT_AERODROME_VT_SHORT)
                        .setType(BulletinHeading.Type.AMENDED)
                        .setBulletinAugmentationNumber(2).build(), ConversionHints.EMPTY }, //
                new Object[] { "FTFI31 EFLK 250200 CCC",
                        TAF_BULLETIN_HEADING.toBuilder().setType(BulletinHeading.Type.CORRECTED).setBulletinAugmentationNumber(3).build(),
                        ConversionHints.EMPTY }, //
                new Object[] { "FTFI32 AAAA 250200 RRA", TAF_BULLETIN_HEADING.toBuilder()
                        .setType(BulletinHeading.Type.DELAYED)
                        .setBulletinAugmentationNumber(1)
                        .setBulletinNumber(32)
                        .setLocationIndicator("AAAA").build(), ConversionHints.EMPTY }, //
                // Extended augmentation indicators
                new Object[] { "FTFI31 EFLK 250200 COR",
                        TAF_BULLETIN_HEADING.toBuilder().setType(BulletinHeading.Type.CORRECTED).setBulletinAugmentationNumber(1).build(),
                        EXTENDED_AUGMENTATION_IDENTIFIERS }, //
                new Object[] { "FTFI31 EFLK 250200 RTD",
                        TAF_BULLETIN_HEADING.toBuilder().setType(BulletinHeading.Type.DELAYED).setBulletinAugmentationNumber(1).build(),
                        EXTENDED_AUGMENTATION_IDENTIFIERS }, //
                new Object[] { "FTFI31 EFLK 250200 AMD",
                        TAF_BULLETIN_HEADING.toBuilder().setType(BulletinHeading.Type.AMENDED).setBulletinAugmentationNumber(1).build(),
                        EXTENDED_AUGMENTATION_IDENTIFIERS }, //
                // SIGMETs
                new Object[] { "WSFI31 EFLK 231008", SIGMET_BULLETIN_HEADING, ConversionHints.EMPTY },
                new Object[] { "WSFI32 AAAA 231008", SIGMET_BULLETIN_HEADING.toBuilder().setBulletinNumber(32).setLocationIndicator("AAAA").build(),
                        ConversionHints.EMPTY } };
    }

    @Test
    @Parameters
    public void decode_bulletin_headings_throws_exception_on_error(final String input, final ConversionHints conversionHints,
            final Class<Throwable> expectedException) {
        assertThatExceptionOfType(expectedException).isThrownBy(() -> BulletinHeadingDecoder.decode(input, conversionHints));
    }

    public Object parametersForDecode_bulletin_headings_throws_exception_on_error() {
        return new Object[] {//
                // Extended augmentation indicators
                new Object[] { "FTFI31 EFLK 250200 INVALID", EXTENDED_AUGMENTATION_IDENTIFIERS, IllegalArgumentException.class }, //
                new Object[] { "FTFI31 EFLK 250200 RTD", ConversionHints.EMPTY, IllegalArgumentException.class }, //
        };
    }

    @Test
    public void decodeAugmentationNumber_GivenMinimumChar_ShouldReturnMinimumNumber() {
        assertThat(BulletinHeadingDecoder.decodeAugmentationNumber(BulletinHeadingEncoder.AUGMENTATION_NUMBER_MIN_CHAR)).isEqualTo(
                BulletinHeadingEncoder.AUGMENTATION_NUMBER_MIN);
    }

    @Test
    public void decodeAugmentationNumber_GivenMaximumChar_ShouldReturnMaximumNumber() {
        assertThat(BulletinHeadingDecoder.decodeAugmentationNumber(BulletinHeadingEncoder.AUGMENTATION_NUMBER_MAX_CHAR)).isEqualTo(
                BulletinHeadingEncoder.AUGMENTATION_NUMBER_MAX);
    }

    @Test
    public void decodeAugmentationNumber_GivenCharSmallerThanMinimum_ShouldThrowException() {
        final char tacChar = BulletinHeadingEncoder.AUGMENTATION_NUMBER_MIN_CHAR - 1;
        assertThatIllegalArgumentException()//
                .isThrownBy(() -> {
                    //noinspection ResultOfMethodCallIgnored
                    BulletinHeadingDecoder.decodeAugmentationNumber(tacChar);
                })//
                .withMessageContaining("'" + tacChar + "'");
    }

    @Test
    public void decodeAugmentationNumber_GivenCharGreaterThanMaximum_ShouldThrowException() {
        final char tacChar = BulletinHeadingEncoder.AUGMENTATION_NUMBER_MAX_CHAR + 1;
        assertThatIllegalArgumentException()//
                .isThrownBy(() -> {
                    //noinspection ResultOfMethodCallIgnored
                    BulletinHeadingDecoder.decodeAugmentationNumber(tacChar);
                })//
                .withMessageContaining("'" + tacChar + "'");
    }
}
