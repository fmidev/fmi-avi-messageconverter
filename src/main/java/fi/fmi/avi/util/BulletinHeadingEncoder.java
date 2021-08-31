package fi.fmi.avi.util;

import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.model.MessageFormat;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.bulletin.BulletinHeading;

import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public final class BulletinHeadingEncoder {
    static final char AUGMENTATION_NUMBER_MIN_CHAR = 'A';
    static final char AUGMENTATION_NUMBER_MAX_CHAR = 'Z';
    static final int AUGMENTATION_NUMBER_MIN = 1;
    static final int AUGMENTATION_NUMBER_MAX = AUGMENTATION_NUMBER_MAX_CHAR - AUGMENTATION_NUMBER_MIN_CHAR + AUGMENTATION_NUMBER_MIN;

    private BulletinHeadingEncoder() {
        throw new AssertionError();
    }

    @Deprecated
    public static String encode(final BulletinHeading input, final ConversionHints hints) {
        return encode(input, MessageFormat.TEXT, hints);
    }

    public static String encode(final BulletinHeading input, final MessageFormat messageFormat, final ConversionHints hints) {
        requireNonNull(input, "input");
        requireNonNull(messageFormat, "messageFormat");
        boolean useSpaces = true;
        if (hints != null && hints.containsKey(ConversionHints.KEY_BULLETIN_HEADING_SPACING)) {
            useSpaces = hints.get(ConversionHints.KEY_BULLETIN_HEADING_SPACING).equals(ConversionHints.VALUE_BULLETIN_HEADING_SPACING_SPACE);
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(getDataDesignators(input, messageFormat));
        if (useSpaces) {
            sb.append(' ');
        }
        sb.append(input.getLocationIndicator());
        if (useSpaces) {
            sb.append(' ');
        }
        appendIssueTime(sb, input.getIssueTime());
        checkBBBIndicatorDataConsistency(input);
        if (encodesBBBIndicator(input.getType())) {
            if (useSpaces) {
                sb.append(' ');
            }
            appendBBBIndicator(sb, input.getType(), input.getAugmentationNumber().orElse(1));
        }
        return sb.toString();
    }

    public static String getDataDesignators(final BulletinHeading input, final MessageFormat messageFormat) {
        requireNonNull(input, "input");
        requireNonNull(messageFormat, "messageFormat");
        if (messageFormat.equals(MessageFormat.TEXT)) {
            return input.getDataDesignatorsForTAC();
        } else if (messageFormat.equals(MessageFormat.XML)) {
            return input.getDataDesignatorsForXML();
        } else {
            throw new IllegalArgumentException("Unsupported messageFormat: " + messageFormat);
        }
    }

    public static String encodeIssueTime(final PartialOrCompleteTimeInstant issueTime) {
        requireNonNull(issueTime, "issueTime");
        return appendIssueTime(new StringBuilder(), issueTime).toString();
    }

    private static StringBuilder appendIssueTime(final StringBuilder sb, final PartialOrCompleteTimeInstant issueTime) {
        final OptionalInt day = issueTime.getDay();
        final OptionalInt hour = issueTime.getHour();
        final OptionalInt minute = issueTime.getMinute();
        if (!day.isPresent() || !hour.isPresent() || !minute.isPresent()) {
            final String emptyFields = Stream.of(//
                    day.isPresent() ? "" : "day", //
                    hour.isPresent() ? "" : "hour", //
                    minute.isPresent() ? "" : "minute")//
                    .filter(field -> !field.isEmpty())//
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException("Missing " + emptyFields + " from bulletin issue time " + issueTime);
        }
        return sb.append(String.format("%02d", day.getAsInt()))//
                .append(String.format("%02d", hour.getAsInt()))//
                .append(String.format("%02d", minute.getAsInt()));
    }

    private static void checkBBBIndicatorDataConsistency(final BulletinHeading input) {
        final boolean encodesBBBIndicator = encodesBBBIndicator(input.getType());
        final boolean hasAugmentationNumber = input.getAugmentationNumber().isPresent();
        if (encodesBBBIndicator && !hasAugmentationNumber) {
            throw new IllegalArgumentException("Missing bulletinAugmentationNumber; is required with type " + input.getType());
        } else if (!encodesBBBIndicator && hasAugmentationNumber) {
            throw new IllegalArgumentException("Bulletin contains augmentation number, but it is unsupported with type " + input.getType());
        }
    }

    private static boolean encodesBBBIndicator(final BulletinHeading.Type bulletinHeadingType) {
        return !bulletinHeadingType.getPrefix().isEmpty();
    }

    public static String encodeBBBIndicator(final BulletinHeading input) {
        requireNonNull(input, "input");
        checkBBBIndicatorDataConsistency(input);
        return encodeBBBIndicator(input.getType(), input.getAugmentationNumber().orElse(1));
    }

    public static String encodeBBBIndicator(final BulletinHeading.Type bulletinHeadingType, final int augmentationNumber) {
        requireNonNull(bulletinHeadingType, "bulletinHeadingType");
        if (!encodesBBBIndicator(bulletinHeadingType)) {
            return "";
        }
        return appendBBBIndicator(new StringBuilder(), bulletinHeadingType, augmentationNumber).toString();
    }

    private static StringBuilder appendBBBIndicator(final StringBuilder sb, final BulletinHeading.Type bulletinHeadingType, final int augmentationNumber) {
        return sb.append(bulletinHeadingType.getPrefix())//
                .append(encodeAugmentationNumber(augmentationNumber));
    }

    public static char encodeAugmentationNumber(final int augmentationNumber) {
        if (augmentationNumber < AUGMENTATION_NUMBER_MIN || augmentationNumber > AUGMENTATION_NUMBER_MAX) {
            throw new IllegalArgumentException(
                    "Illegal augmentation number <" + augmentationNumber + ">; the value must be between " + AUGMENTATION_NUMBER_MIN + " and "
                            + AUGMENTATION_NUMBER_MAX);
        }
        return (char) (augmentationNumber - AUGMENTATION_NUMBER_MIN + AUGMENTATION_NUMBER_MIN_CHAR);
    }
}
