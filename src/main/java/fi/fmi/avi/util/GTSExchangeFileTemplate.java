package fi.fmi.avi.util;

import static fi.fmi.avi.model.bulletin.MeteorologicalBulletinSpecialCharacter.CARRIAGE_RETURN;
import static fi.fmi.avi.model.bulletin.MeteorologicalBulletinSpecialCharacter.END_OF_TEXT;
import static fi.fmi.avi.model.bulletin.MeteorologicalBulletinSpecialCharacter.LINE_FEED;
import static fi.fmi.avi.model.bulletin.MeteorologicalBulletinSpecialCharacter.START_OF_HEADING;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.inferred.freebuilder.FreeBuilder;

import fi.fmi.avi.model.bulletin.MeteorologicalBulletinSpecialCharacter;

@FreeBuilder
public abstract class GTSExchangeFileTemplate {
    public static final String STARTING_LINE_PREFIX = stringOf(START_OF_HEADING, CARRIAGE_RETURN, CARRIAGE_RETURN, LINE_FEED);
    public static final String HEADING_PREFIX = stringOf(CARRIAGE_RETURN, CARRIAGE_RETURN, LINE_FEED);
    public static final String TEXT_PREFIX = stringOf(CARRIAGE_RETURN, CARRIAGE_RETURN, LINE_FEED);
    public static final String END_OF_MESSAGE_SIGNALS = stringOf(CARRIAGE_RETURN, CARRIAGE_RETURN, LINE_FEED, END_OF_TEXT);

    private static String stringOf(final MeteorologicalBulletinSpecialCharacter... specialCharacters) {
        return Arrays.stream(specialCharacters)//
                .map(MeteorologicalBulletinSpecialCharacter::getContent)//
                .collect(Collectors.joining());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static List<GTSExchangeFileTemplate> parseAll(final String data) {
        requireNonNull(data, "data");
        final List<GTSExchangeFileTemplate> templates = new ArrayList<>();
        String remainingData = data;
        while (!remainingData.isEmpty()) {
            final GTSExchangeFileTemplate template = parse(remainingData);
            templates.add(template);
            final int nextMessageIndex = template.getMessageLength() + 10; // add message length 8 chars + format identifier 2 chars
            remainingData = nextMessageIndex > remainingData.length() ? "" : remainingData.substring(nextMessageIndex);
        }
        return Collections.unmodifiableList(templates);
    }

    private static GTSExchangeFileTemplate parse(final String data) {
        requireNonNull(data, "data");
        return builder().parse(data).build();
    }

    private static Optional<Integer> transmissionSequenceNumberToInt(final String transmissionSequenceNumber) {
        return Optional.of(transmissionSequenceNumber)//
                .filter(seqNo -> !seqNo.isEmpty())//
                .map(seqNo -> {
                    try {
                        return Integer.parseInt(seqNo);
                    } catch (final NumberFormatException ignored) {
                        return null;
                    }
                });
    }

    public int getMessageLength() {
        final int formatIdentifier = getFormatIdentifier();
        if (formatIdentifier == 0) {
            return STARTING_LINE_PREFIX.length() //
                    + getTransmissionSequenceNumber().length() //
                    + HEADING_PREFIX.length() //
                    + getHeading().length() //
                    + TEXT_PREFIX.length() //
                    + getText().length() //
                    + END_OF_MESSAGE_SIGNALS.length();
        } else if (formatIdentifier == 1) {
            return HEADING_PREFIX.length() //
                    + getHeading().length() //
                    + TEXT_PREFIX.length() //
                    + getText().length();
        } else {
            throw new IllegalStateException("Illegal formatIdentifier: " + formatIdentifier);
        }
    }

    public int getFormatIdentifier() {
        return getTransmissionSequenceNumber().isEmpty() ? 1 : 0;
    }

    public abstract String getTransmissionSequenceNumber();

    public Optional<Integer> getTransmissionSequenceNumberAsInt() {
        return transmissionSequenceNumberToInt(getTransmissionSequenceNumber());
    }

    public abstract String getHeading();

    /**
     * Returns message text part without initial {@link #TEXT_PREFIX}.
     *
     * @return message text part without initial {@link #TEXT_PREFIX}
     */
    public abstract String getText();

    public abstract Builder toBuilder();

    public String toHeadingAndTextString() {
        return getHeading() + TEXT_PREFIX + getText();
    }

    public String toString() {
        final int formatIdentifier = getFormatIdentifier();
        if (formatIdentifier == 0) {
            return String.format(Locale.ROOT, "%08d%02d%s%s%s%s%s%s%s", //
                    getMessageLength(), //
                    getFormatIdentifier(), //
                    STARTING_LINE_PREFIX, //
                    getTransmissionSequenceNumber(), //
                    HEADING_PREFIX, //
                    getHeading(), //
                    TEXT_PREFIX, //
                    getText(), //
                    END_OF_MESSAGE_SIGNALS);
        } else if (formatIdentifier == 1) {
            return String.format(Locale.ROOT, "%08d%02d%s%s%s%s", //
                    getMessageLength(), //
                    getFormatIdentifier(), //
                    HEADING_PREFIX, //
                    getHeading(), //
                    TEXT_PREFIX, //
                    getText());
        } else {
            throw new IllegalStateException("Illegal formatIdentifier: " + formatIdentifier);
        }
    }

    public static class Builder extends GTSExchangeFileTemplate_Builder {

        private static final int MESSAGE_LENGTH_START_INDEX = 0;
        private static final int MESSAGE_LENGTH_END_INDEX = MESSAGE_LENGTH_START_INDEX + 8;
        private static final int FORMAT_IDENTIFIER_START_INDEX = MESSAGE_LENGTH_END_INDEX;
        private static final int FORMAT_IDENTIFIER_END_INDEX = FORMAT_IDENTIFIER_START_INDEX + 2;
        private static final int TRANSMISSION_SEQUENCE_NUMBER_START_INDEX = FORMAT_IDENTIFIER_END_INDEX + STARTING_LINE_PREFIX.length();

        Builder() {
        }

        private static String truncate(final String data) {
            return data.length() <= 256 ? data : data.substring(0, 253) + "...";
        }

        /**
         * Parses provided string for a GTS exchange file content and populates this builder with parsed data.
         * The string is read up to message length stated in the beginning of the string. Any remaining part of the string is simply ignored
         *
         * @param data
         *         string to parse
         *
         * @return this builder
         *
         * @throws IllegalArgumentException
         *         if provided {@code data} cannot be parsed or does not meet requirements of WMO Doc. 386 specification.
         */
        public Builder parse(final String data) {
            requireNonNull(data, "data");

            final int messageLength = Integer.parseInt(data.substring(MESSAGE_LENGTH_START_INDEX, MESSAGE_LENGTH_END_INDEX));
            final int formatIdentifier = Integer.parseInt(data.substring(FORMAT_IDENTIFIER_START_INDEX, FORMAT_IDENTIFIER_END_INDEX));
            final String transmissionSequenceNumber;
            final int headingStartIndex;
            if (formatIdentifier == 0) {
                if (!data.startsWith(STARTING_LINE_PREFIX, TRANSMISSION_SEQUENCE_NUMBER_START_INDEX - STARTING_LINE_PREFIX.length())) {
                    throw new IllegalArgumentException("Starting line is not preceded by expected prefix in <" + truncate(data) + ">");
                }
                final int seqNoEndIndex = data.indexOf(HEADING_PREFIX, TRANSMISSION_SEQUENCE_NUMBER_START_INDEX);
                if (seqNoEndIndex < 0) {
                    throw new IllegalArgumentException("Unable to parse transmission sequence number from data <" + truncate(data) + ">");
                }
                transmissionSequenceNumber = data.substring(seqNoEndIndex, seqNoEndIndex);
                headingStartIndex = seqNoEndIndex + HEADING_PREFIX.length();
            } else if (formatIdentifier == 1) {
                transmissionSequenceNumber = "";
                headingStartIndex = FORMAT_IDENTIFIER_END_INDEX + HEADING_PREFIX.length();
            } else {
                throw new IllegalStateException("Illegal formatIdentifier: " + formatIdentifier);
            }
            if (!data.startsWith(HEADING_PREFIX, headingStartIndex - HEADING_PREFIX.length())) {
                throw new IllegalArgumentException("Heading is not preceded by expected prefix in <" + truncate(data) + ">");
            }
            final int messageEndIndex = FORMAT_IDENTIFIER_END_INDEX + messageLength;
            if (formatIdentifier == 0 && !data.startsWith(END_OF_MESSAGE_SIGNALS, messageEndIndex - END_OF_MESSAGE_SIGNALS.length())) {
                throw new IllegalArgumentException("Message does not end with expected signals: <" + truncate(data) + ">");
            }
            parseHeadingAndText(data.substring(headingStartIndex, messageEndIndex));
            setTransmissionSequenceNumber(transmissionSequenceNumber);
            return this;
        }

        /**
         * Parses provided string for bulletin heading and text content and populates this builder with parsed data.
         * The whole remaining part of the string after heading is interpreted as text content.
         *
         * <p>
         * Note that this method does not affect any other property of this builder than {@code heading} and {@code text}.
         * </p>
         *
         * @param data
         *         string to parse
         *
         * @return this builder
         *
         * @throws IllegalArgumentException
         *         if provided {@code data} cannot be parsed
         */
        public Builder parseHeadingAndText(final String data) {
            requireNonNull(data, "data");
            final int textPrefixIndex = data.indexOf(TEXT_PREFIX);
            if (textPrefixIndex < 0) {
                throw new IllegalStateException("Data not recognized as heading and text: <" + truncate(data) + ">");
            }
            setHeading(data.substring(0, textPrefixIndex));
            setText(data.substring(textPrefixIndex + TEXT_PREFIX.length()));
            return this;
        }

        public Optional<Integer> getTransmissionSequenceNumberAsInt() {
            return transmissionSequenceNumberToInt(getTransmissionSequenceNumber());
        }

        public Builder setTransmissionSequenceNumberAsInt(final int number) {
            return setTransmissionSequenceNumberAsInt(number, 3);
        }

        public Builder setTransmissionSequenceNumberAsInt(final int number, final int numberOfDigits) {
            if (number < 0) {
                throw new IllegalArgumentException("number is negative: " + number);
            }
            if (numberOfDigits < 1) {
                throw new IllegalArgumentException("numberOfDigits is smaller than 1: " + numberOfDigits);
            }
            final String sequenceNumberString = String.format(Locale.ROOT, "%0" + numberOfDigits + "d", number);
            if (sequenceNumberString.length() > numberOfDigits) {
                throw new IllegalArgumentException("number greater than maximum of " + numberOfDigits + " digits: " + number);
            }
            return setTransmissionSequenceNumber(sequenceNumberString);
        }
    }
}
