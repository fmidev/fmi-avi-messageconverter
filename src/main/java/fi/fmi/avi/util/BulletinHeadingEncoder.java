package fi.fmi.avi.util;

import java.util.Optional;
import java.util.OptionalInt;

import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.model.bulletin.BulletinHeading;

public class BulletinHeadingEncoder {

    public static String encode(final BulletinHeading input, final ConversionHints hints) throws IllegalArgumentException {
        boolean useSpaces = true;
        if (hints != null && hints.containsKey(ConversionHints.KEY_BULLETIN_HEADING_SPACING)) {
            useSpaces = hints.get(ConversionHints.KEY_BULLETIN_HEADING_SPACING).equals(ConversionHints.VALUE_BULLETIN_HEADING_SPACING_SPACE);
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(input.getDataDesignatorsForTAC());
        if (useSpaces) {
            sb.append(' ');
        }
        sb.append(input.getLocationIndicator());
        if (useSpaces) {
            sb.append(' ');
        }
        final OptionalInt day = input.getIssueTime().getDay();
        final OptionalInt hour = input.getIssueTime().getHour();
        final OptionalInt minute = input.getIssueTime().getMinute();
        if (day.isPresent() && hour.isPresent() && minute.isPresent()) {
            sb.append(String.format("%02d", day.getAsInt()));
            sb.append(String.format("%02d", hour.getAsInt()));
            sb.append(String.format("%02d", minute.getAsInt()));
        } else {
            throw new IllegalArgumentException("Day, hour or minute missing from "
                    + "bulletin issue time");
        }
        final Optional<Integer> augNumber = input.getBulletinAugmentationNumber();
        if (augNumber.isPresent()) {
            if (input.getType() == BulletinHeading.Type.NORMAL) {
                throw new IllegalArgumentException(
                        "Bulletin contains augmentation number, but the type is " + BulletinHeading.Type.NORMAL);
            }
            final int seqNumber = Character.codePointAt("A", 0) + augNumber.get().intValue() - 1;
            //Using Character.codePointAt here is a bit overdo here since we know that we are always operating with single char ASCII codes
            if (seqNumber < Character.codePointAt("A", 0) || seqNumber > Character.codePointAt("Z", 0)) {
                throw new IllegalArgumentException(
                        "Illegal bulletin augmentation number '" + augNumber.get() + "', the value must be between 1 and  " + ('Z' - 'A' + 1));
            }
            if (useSpaces) {
                sb.append(' ');
            }
            sb.append(input.getType().getPrefix());
            sb.append(String.valueOf(Character.toChars(seqNumber)));
        }
        return sb.toString();
    }
}
