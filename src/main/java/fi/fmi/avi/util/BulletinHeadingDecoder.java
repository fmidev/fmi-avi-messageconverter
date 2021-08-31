package fi.fmi.avi.util;

import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.model.PartialDateTime;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.bulletin.BulletinHeading;
import fi.fmi.avi.model.bulletin.DataTypeDesignatorT1;
import fi.fmi.avi.model.bulletin.DataTypeDesignatorT2;
import fi.fmi.avi.model.bulletin.immutable.BulletinHeadingImpl;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BulletinHeadingDecoder {
    // Abbreviated heading with lenient augmentation indicator
    private static final Pattern ABBREVIATED_HEADING = Pattern.compile(
            "^(?<TT>[A-Z]{2})(?<AA>[A-Z]{2})(?<ii>[0-9]{2})\\s*(?<CCCC>[A-Z]{4})\\s*(?<YY>[0-9]{2})(?<GG>[0-9]{2})(?<gg>[0-9]{2})\\s*(?<BBB>[A-Z0-9]+)?$");
    // Strict augmentation indicator
    private static final Pattern AUGMENTATION_INDICATOR = Pattern.compile("(?<BBB>(CC|RR|AA)[A-Z])?");

    private BulletinHeadingDecoder() {
        throw new AssertionError();
    }

    public static BulletinHeading decode(final String input, final ConversionHints hints) throws IllegalArgumentException {
        final Matcher m = ABBREVIATED_HEADING.matcher(input);
        final String illegalInputMessage = "String '" + input + "' does not match the Abbreviated heading formats 'T1T2A1A2iiCCCCYYGGgg[BBB]' "
                + "or 'T1T2A1A2ii CCCC YYGGgg[ BBB]' as defined in " + "WMO-No. 386 Manual on the Global Telecommunication System, 2015 edition (updated 2017)";
        if (!m.matches()) {
            throw new IllegalArgumentException(illegalInputMessage);
        }

        BulletinHeading.Type type = BulletinHeading.Type.NORMAL;
        Integer bulletinAugmentationNumber = null;

        String originalBbb = m.group("BBB");
        if (originalBbb != null) {
            String interpretedBbb = originalBbb;
            if (hints != null && hints.containsKey(ConversionHints.KEY_BULLETIN_HEADING_AUGMENTATION_INDICATOR_EXTENSION)) {
                final Object value = hints.get(ConversionHints.KEY_BULLETIN_HEADING_AUGMENTATION_INDICATOR_EXTENSION);
                if (value instanceof BulletinHeadingIndicatorInterpreter) {
                    interpretedBbb = ((BulletinHeadingIndicatorInterpreter) value).apply(originalBbb);
                }
            }

            if (!interpretedBbb.isEmpty()) {
                final Matcher bbbMatcher = AUGMENTATION_INDICATOR.matcher(interpretedBbb);
                if (!bbbMatcher.matches()) {
                    throw new IllegalArgumentException(illegalInputMessage);
                }

                type = BulletinHeading.Type.fromCode(interpretedBbb.substring(0, 2));
                bulletinAugmentationNumber = decodeAugmentationNumber(interpretedBbb.charAt(2));
            }
        } else {
            originalBbb = "";
        }

        final DataTypeDesignatorT1 t1 = DataTypeDesignatorT1.fromCode(m.group("TT").charAt(0));
        final char t2Code = m.group("TT").charAt(1);
        final DataTypeDesignatorT2 t2 = t1.t2FromCode(t2Code)
                .map(designator -> (DataTypeDesignatorT2) designator)
                .orElse(DataTypeDesignatorT2.fromExtensionCode(t2Code));
        final String issueTime = "--" + m.group("YY") + "T" + m.group("GG") + ":" + m.group("gg");
        return BulletinHeadingImpl.builder()//
                .setLocationIndicator(m.group("CCCC"))//
                .setGeographicalDesignator(m.group("AA"))//
                .setBulletinNumber(Integer.parseInt(m.group("ii")))//
                .setType(type)//
                .setAugmentationNumber(Optional.ofNullable(bulletinAugmentationNumber))//
                .setOriginalAugmentationIndicator(originalBbb)//
                .setDataTypeDesignatorT1ForTAC(t1)//
                .setDataTypeDesignatorT2(t2)//
                .setIssueTime(PartialOrCompleteTimeInstant.of(PartialDateTime.parse(issueTime)))//
                .build();
    }

    public static int decodeAugmentationNumber(final char tacChar) {
        if (tacChar < BulletinHeadingEncoder.AUGMENTATION_NUMBER_MIN_CHAR || tacChar > BulletinHeadingEncoder.AUGMENTATION_NUMBER_MAX_CHAR) {
            throw new IllegalArgumentException(
                    "Illegal augmentation number TAC char '" + tacChar + "'; the value must be between '" + BulletinHeadingEncoder.AUGMENTATION_NUMBER_MIN_CHAR
                            + "' and " + "'" + BulletinHeadingEncoder.AUGMENTATION_NUMBER_MAX_CHAR + "'");
        }
        return tacChar - BulletinHeadingEncoder.AUGMENTATION_NUMBER_MIN_CHAR + BulletinHeadingEncoder.AUGMENTATION_NUMBER_MIN;
    }

}
