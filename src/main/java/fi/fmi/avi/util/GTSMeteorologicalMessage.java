package fi.fmi.avi.util;

import static fi.fmi.avi.model.bulletin.MeteorologicalBulletinSpecialCharacter.CARRIAGE_RETURN;
import static fi.fmi.avi.model.bulletin.MeteorologicalBulletinSpecialCharacter.END_OF_TEXT;
import static fi.fmi.avi.model.bulletin.MeteorologicalBulletinSpecialCharacter.LINE_FEED;
import static fi.fmi.avi.model.bulletin.MeteorologicalBulletinSpecialCharacter.START_OF_HEADING;
import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.inferred.freebuilder.FreeBuilder;

import fi.fmi.avi.model.bulletin.MeteorologicalBulletinSpecialCharacter;
import fi.fmi.avi.util.GTSMeteorologicalMessageParseException.ErrorCode;

@FreeBuilder
public abstract class GTSMeteorologicalMessage implements Serializable {
    public static final String STARTING_LINE_PREFIX = stringOf(START_OF_HEADING, CARRIAGE_RETURN, CARRIAGE_RETURN, LINE_FEED);
    public static final String HEADING_PREFIX = stringOf(CARRIAGE_RETURN, CARRIAGE_RETURN, LINE_FEED);
    public static final String TEXT_PREFIX = stringOf(CARRIAGE_RETURN, CARRIAGE_RETURN, LINE_FEED);
    public static final String END_OF_MESSAGE_SIGNALS = stringOf(CARRIAGE_RETURN, CARRIAGE_RETURN, LINE_FEED, END_OF_TEXT);

    private static final long serialVersionUID = 9215784958510861534L;

    static String stringOf(final MeteorologicalBulletinSpecialCharacter... specialCharacters) {
        return Arrays.stream(specialCharacters)//
                .map(MeteorologicalBulletinSpecialCharacter::getContent)//
                .collect(Collectors.joining());
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Parses the provided string in {@link MessageFormat#STANDARD standard} or {@link MessageFormat#SHORT short} format into a GTS meteorological message.
     *
     * @param content
     *         alphanumerical GTS meteorological message
     *
     * @return parsed GTS meteorological message
     *
     * @throws GTSMeteorologicalMessageParseException
     *         if provided {@code message} cannot be parsed or does not meet requirements of WMO Doc. 386 specification.
     * @see Builder#parse(String)
     */
    public static GTSMeteorologicalMessage parse(final String content) {
        return builder().parse(content).build();
    }

    /**
     * Parses the provided string in format specified by {@code requiredFormat} into a GTS meteorological message.
     *
     * @param content
     *         alphanumerical GTS meteorological message
     *
     * @return parsed GTS meteorological message
     *
     * @throws GTSMeteorologicalMessageParseException
     *         if provided {@code message} cannot be parsed or does not meet requirements of WMO Doc. 386 specification.
     * @see Builder#parse(String, MessageFormat)
     */
    public static GTSMeteorologicalMessage parse(final String content, final MessageFormat requiredFormat) {
        return builder().parse(content, requiredFormat).build();
    }

    /**
     * Parses the provided string in any format specified by {@code acceptedFormats} into a GTS meteorological message.
     *
     * @param content
     *         alphanumerical GTS meteorological message
     *
     * @return parsed GTS meteorological message
     *
     * @throws GTSMeteorologicalMessageParseException
     *         if provided {@code message} cannot be parsed or does not meet requirements of WMO Doc. 386 specification.
     * @see Builder#parse(String, Set)
     */
    public static GTSMeteorologicalMessage parse(final String content, final Set<MessageFormat> acceptedFormats) {
        return builder().parse(content, acceptedFormats).build();
    }

    /**
     * Parses the provided string into a GTS meteorological message.
     * The string is leniently interpreted as a bulletin heading and text content.
     *
     * @param content
     *         bulletin heading and text content
     *
     * @return parsed GTS meteorological message
     *
     * @see Builder#parseHeadingAndTextLenient(String)
     */
    public static GTSMeteorologicalMessage parseHeadingAndTextLenient(final String content) {
        requireNonNull(content, "content");
        return builder().parseHeadingAndTextLenient(content).build();
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

    /**
     * Returns the message length.
     *
     * @return message length
     */
    public int getLength() {
        return getLength(MessageFormat.get(this));
    }

    /**
     * Returns the message length in provided {@code messageFormat}.
     *
     * @param messageFormat
     *         message format
     *
     * @return length in provided {@code messageFormat}
     *
     * @throws IllegalArgumentException
     *         if message does not {@link #supports(MessageFormat) support} the provided {@code messageFormat}
     */
    public int getLength(final MessageFormat messageFormat) {
        requireNonNull(messageFormat, "messageFormat");
        return messageFormat.length(this);
    }

    /**
     * Returns transmission sequence number as a string, or empty string if not present.
     *
     * @return transmission sequence number as a string, or empty string if not present.
     */
    public abstract String getTransmissionSequenceNumber();

    /**
     * Returns transmission sequence number as an integer, or empty if not present.
     *
     * @return transmission sequence number as an integer, or empty if not present.
     */
    public Optional<Integer> getTransmissionSequenceNumberAsInt() {
        return transmissionSequenceNumberToInt(getTransmissionSequenceNumber());
    }

    /**
     * Returns message heading without initial {@link #HEADING_PREFIX}.
     *
     * @return message heading without initial {@link #HEADING_PREFIX}
     */
    public abstract String getHeading();

    /**
     * Returns message text part without initial {@link #TEXT_PREFIX}.
     *
     * @return message text part without initial {@link #TEXT_PREFIX}
     */
    public abstract String getText();

    public abstract Builder toBuilder();

    public boolean supports(final MessageFormat messageFormat) {
        requireNonNull(messageFormat, "messageFormat");
        return messageFormat.supports(this);
    }

    /**
     * Returns this meteorological message formatted as string.
     *
     * @return this meteorological message formatted as string
     */
    public String toString() {
        return toString(MessageFormat.get(this));
    }

    /**
     * Returns this meteorological message as string formatted in provided {@code messageFormat}.
     *
     * @param messageFormat
     *         message format
     *
     * @return this meteorological message as string formatted in provided {@code messageFormat}
     *
     * @throws IllegalArgumentException
     *         if message does not {@link #supports(MessageFormat) support} the provided {@code messageFormat}
     */
    public String toString(final MessageFormat messageFormat) {
        requireNonNull(messageFormat, "messageFormat");
        final StringBuilder builder = new StringBuilder();
        messageFormat.appendTo(builder, this);
        return builder.toString();
    }

    public enum MessageFormat {
        /**
         * Standard GTS meteorological message, consisting of starting line, heading and text.
         * File format identifier {@code 00}.
         */
        STANDARD(STARTING_LINE_PREFIX) {
            @Override
            boolean supports(final GTSMeteorologicalMessage message) {
                return !message.getTransmissionSequenceNumber().isEmpty();
            }

            @Override
            int length(final GTSMeteorologicalMessage message) {
                checkIsSupported(message);
                return STARTING_LINE_PREFIX.length() //
                        + message.getTransmissionSequenceNumber().length() //
                        + SHORT.length(message) //
                        + END_OF_MESSAGE_SIGNALS.length();
            }

            @Override
            void appendTo(final StringBuilder builder, final GTSMeteorologicalMessage message) {
                checkIsSupported(message);
                builder//
                        .append(STARTING_LINE_PREFIX)//
                        .append(message.getTransmissionSequenceNumber());
                SHORT.appendTo(builder, message);
                builder.append(END_OF_MESSAGE_SIGNALS);
            }
        }, //
        /**
         * A shortened GTS meteorological message, missing starting line and end-of-message.
         * File format identifier {@code 01}.
         */
        SHORT(HEADING_PREFIX) {
            @Override
            boolean supports(final GTSMeteorologicalMessage message) {
                requireNonNull(message, "message");
                return true;
            }

            @Override
            int length(final GTSMeteorologicalMessage message) {
                return HEADING_PREFIX.length() //
                        + HEADING_AND_TEXT.length(message);
            }

            @Override
            void appendTo(final StringBuilder builder, final GTSMeteorologicalMessage message) {
                builder.append(HEADING_PREFIX);
                HEADING_AND_TEXT.appendTo(builder, message);
            }
        }, //
        /**
         * A non-standard message format consisting of only heading and text separated by {@link #TEXT_PREFIX}.
         */
        HEADING_AND_TEXT("") {
            @Override
            boolean supports(final GTSMeteorologicalMessage message) {
                requireNonNull(message, "message");
                return true;
            }

            @Override
            int length(final GTSMeteorologicalMessage message) {
                return message.getHeading().length() //
                        + TEXT_PREFIX.length() //
                        + message.getText().length();
            }

            @Override
            void appendTo(final StringBuilder builder, final GTSMeteorologicalMessage message) {
                builder.append(message.getHeading())//
                        .append(TEXT_PREFIX)//
                        .append(message.getText());
            }
        };

        private final String prefix;

        MessageFormat(final String prefix) {
            this.prefix = prefix;
        }

        static MessageFormat get(final GTSMeteorologicalMessage message) {
            requireNonNull(message, "message");
            return message.getTransmissionSequenceNumber().isEmpty() ? SHORT : STANDARD;
        }

        String getPrefix() {
            return prefix;
        }

        void checkIsSupported(final GTSMeteorologicalMessage message) {
            requireNonNull(message, "message");
            if (!supports(message)) {
                throw new IllegalArgumentException("Unsupported MessageFormat: " + this);
            }
        }

        abstract boolean supports(final GTSMeteorologicalMessage message);

        abstract int length(final GTSMeteorologicalMessage message);

        abstract void appendTo(final StringBuilder builder, final GTSMeteorologicalMessage message);
    }

    private static class ParsingContext {
        private final String content;
        private final Set<MessageFormat> acceptedFormats;
        private int currentIndex;

        ParsingContext(final String content, final Set<MessageFormat> acceptedFormats, final int offset) {
            this.content = requireNonNull(content, "content");
            this.acceptedFormats = requireNonNull(acceptedFormats, "acceptedFormats");
            this.currentIndex = offset;
            if (acceptedFormats.isEmpty()) {
                throw new IllegalArgumentException("acceptedFormats must not be empty");
            }
        }

        public String getContent() {
            return content;
        }

        public int getCurrentIndex() {
            return currentIndex;
        }

        public void setCurrentIndex(final int currentIndex) {
            this.currentIndex = currentIndex;
        }

        public void increaseCurrentIndex(final int increase) {
            currentIndex += increase;
        }

        public boolean accepted(final MessageFormat messageFormat) {
            return acceptedFormats.contains(messageFormat);
        }

        public boolean noneAccepted(final MessageFormat... messageFormats) {
            for (final MessageFormat messageFormat : messageFormats) {
                if (accepted(messageFormat)) {
                    return false;
                }
            }
            return true;
        }
    }

    public static class Builder extends GTSMeteorologicalMessage_Builder {
        private static final Set<MessageFormat> ALL_MESSAGE_FORMATS = Collections.unmodifiableSet(EnumSet.allOf(MessageFormat.class));
        private static final Pattern ANY_SEQUENCE_OF_LINE_BREAKS = Pattern.compile(
                String.format(Locale.ROOT, "[%s]+", Pattern.quote(stringOf(CARRIAGE_RETURN, LINE_FEED))));

        Builder() {
            setTransmissionSequenceNumber("");
        }

        private static int indexOfAnyLineBreak(final CharSequence content) {
            final Matcher matcher = ANY_SEQUENCE_OF_LINE_BREAKS.matcher(content);
            if (matcher.find()) {
                return matcher.start();
            } else {
                return -1;
            }
        }

        /**
         * Parses provided string of GTS meteorological message content in {@link MessageFormat#STANDARD standard} or {@link MessageFormat#SHORT short} format
         * and populates this builder with parsed message.
         * Parsing will accept any of supported {@link MessageFormat message formats}.
         *
         * <p>
         * If the parsed message is missing the transmission sequence number, the {@link #setTransmissionSequenceNumber(String)} property will not be changed.
         * In case you want to ensure transmission sequence number is set or reset correctly, invoke {@link #clearTransmissionSequenceNumber()} before this
         * method.
         * </p>
         *
         * <p>
         * This method does a string parsing considering the GTS meteorological message structure and the signal sequences within it. It does not however
         * look into the contents of the heading or text part in any way, except for checking that heading does not contain any line break (CR and LF)
         * characters. The heading and text are stored in the data structure as is.
         * </p>
         *
         * @param content
         *         GTS meteorological message string to parse
         *
         * @return this builder
         *
         * @throws GTSMeteorologicalMessageParseException
         *         if provided {@code message} cannot be parsed or does not meet requirements of WMO Doc. 386 specification.
         */
        public Builder parse(final String content) {
            return parse(content, ALL_MESSAGE_FORMATS);
        }

        /**
         * Parses provided string of GTS meteorological message content in format specified by {@code requiredFormat}
         * and populates this builder with parsed message.
         * Parsing will recognize only the provided {@code requiredFormat}.
         *
         * <p>
         * If the parsed message is missing the transmission sequence number, the {@link #setTransmissionSequenceNumber(String)} property will not be changed.
         * In case you want to ensure transmission sequence number is set or reset correctly, invoke {@link #clearTransmissionSequenceNumber()} before this
         * method.
         * </p>
         *
         * <p>
         * This method does a string parsing considering the GTS meteorological message structure and the signal sequences within it. It does not however
         * look into the contents of the heading or text part in any way, except for checking that heading does not contain any line break (CR and LF)
         * characters. The heading and text are stored in the data structure as is.
         * </p>
         *
         * @param content
         *         GTS meteorological message string to parse
         * @param requiredFormat
         *         message format to parse as
         *
         * @return this builder
         *
         * @throws GTSMeteorologicalMessageParseException
         *         if provided {@code message} cannot be parsed or does not meet requirements of WMO Doc. 386 specification.
         */
        public Builder parse(final String content, final MessageFormat requiredFormat) {
            requireNonNull(requiredFormat, "requiredFormat");
            return parse(content, Collections.singleton(requiredFormat));
        }

        /**
         * Parses provided string of GTS meteorological message content in any format specified by {@code acceptedFormats}
         * and populates this builder with parsed message.
         * Parsing will recognize only {@code acceptedFormats}.
         *
         * <p>
         * If the parsed message is missing the transmission sequence number, the {@link #setTransmissionSequenceNumber(String)} property will not be changed.
         * In case you want to ensure transmission sequence number is set or reset correctly, invoke {@link #clearTransmissionSequenceNumber()} before this
         * method.
         * </p>
         *
         * <p>
         * This method does a string parsing considering the GTS meteorological message structure and the signal sequences within it. It does not however
         * look into the contents of the heading or text part in any way, except for checking that heading does not contain any line break (CR and LF)
         * characters. The heading and text are stored in the data structure as is.
         * </p>
         *
         * @param content
         *         GTS meteorological message string to parse
         * @param acceptedFormats
         *         accepted message formats
         *
         * @return this builder
         *
         * @throws GTSMeteorologicalMessageParseException
         *         if provided {@code message} cannot be parsed or does not meet requirements of WMO Doc. 386 specification.
         */
        public Builder parse(final String content, final Set<MessageFormat> acceptedFormats) {
            requireNonNull(content, "content");
            return parse(content, 0, content.length(), acceptedFormats);
        }

        /**
         * Parses specified part of provided GTS meteorological message string in any format specified by {@code acceptedFormats}
         * and populates this builder with parsed message.
         * Parsing will start at {@code offset} index for provided {@code messageLength} and recognize only {@code acceptedProtocols}.
         *
         * <p>
         * If the parsed message is missing the transmission sequence number, the {@link #setTransmissionSequenceNumber(String)} property will not be changed.
         * In case you want to ensure transmission sequence number is set or reset correctly, invoke {@link #clearTransmissionSequenceNumber()} before this
         * method.
         * </p>
         *
         * <p>
         * This method does a string parsing considering the GTS meteorological message structure and the signal sequences within it. It does not however
         * look into the contents of the heading or text part in any way, except for checking that heading does not contain any line break (CR and LF)
         * characters. The heading and text are stored in the data structure as is.
         * </p>
         *
         * @param content
         *         GTS meteorological message string to parse
         * @param offset
         *         offset index to start parsing from
         * @param messageLength
         *         length to parse starting from offset index
         * @param acceptedFormats
         *         accepted message formats
         *
         * @return this builder
         *
         * @throws GTSMeteorologicalMessageParseException
         *         if provided {@code message} cannot be parsed or does not meet requirements of WMO Doc. 386 specification.
         */
        public Builder parse(final String content, final int offset, final int messageLength, final Set<MessageFormat> acceptedFormats) {
            requireNonNull(content, "content");
            requireNonNull(acceptedFormats, "acceptedFormats");
            return parse(new ParsingContext(content, acceptedFormats, offset), messageLength);
        }

        private Builder parse(final ParsingContext context, final int messageLength) {
            final int messageEndIndex = context.getCurrentIndex() + messageLength;
            final String transmissionSequenceNumber = parseTransmissionSequenceNumber(context);
            final boolean standardMessageFormat = !transmissionSequenceNumber.isEmpty();
            forwardHeadingPrefix(context, standardMessageFormat);
            final int textEndIndex = lookupTextEndIndex(context.getContent(), messageEndIndex, standardMessageFormat);
            final String heading = parseHeading(context, textEndIndex);
            final String text = parseText(context, textEndIndex);

            setHeading(heading);
            setText(text);
            if (standardMessageFormat) {
                setTransmissionSequenceNumber(transmissionSequenceNumber);
            }
            return this;
        }

        private String parseTransmissionSequenceNumber(final ParsingContext context) {
            final String content = context.getContent();
            final String transmissionSequenceNumber;
            if (content.startsWith(STARTING_LINE_PREFIX, context.getCurrentIndex())) {
                if (!context.accepted(MessageFormat.STANDARD)) {
                    throw new GTSMeteorologicalMessageParseException(ErrorCode.UNEXPECTED_STARTING_LINE_PREFIX, context.getCurrentIndex(), content);
                }
                context.increaseCurrentIndex(STARTING_LINE_PREFIX.length());
                final int seqNoEndIndex = content.indexOf(HEADING_PREFIX, context.getCurrentIndex());
                if (seqNoEndIndex < 0) {
                    throw new GTSMeteorologicalMessageParseException(ErrorCode.UNEXPECTED_END_OF_TRANSMISSION_SEQUENCE_NUMBER, context.getCurrentIndex(),
                            content);
                }
                transmissionSequenceNumber = content.substring(context.getCurrentIndex(), seqNoEndIndex);
                final int lineBreakIndex = indexOfAnyLineBreak(transmissionSequenceNumber);
                if (lineBreakIndex >= 0) {
                    throw new GTSMeteorologicalMessageParseException(ErrorCode.UNEXPECTED_LINE_BREAKS_IN_TRANSMISSION_SEQUENCE_NUMBER,
                            context.getCurrentIndex() + lineBreakIndex, content);
                }
                context.setCurrentIndex(seqNoEndIndex);
            } else {
                if (context.accepted(MessageFormat.STANDARD) && context.noneAccepted(MessageFormat.SHORT, MessageFormat.HEADING_AND_TEXT)) {
                    throw new GTSMeteorologicalMessageParseException(ErrorCode.MISSING_STARTING_LINE_PREFIX, context.getCurrentIndex(), content);
                }
                transmissionSequenceNumber = "";
            }
            return transmissionSequenceNumber;
        }

        private void forwardHeadingPrefix(final ParsingContext context, final boolean standardMessageFormat) {
            if (context.content.startsWith(HEADING_PREFIX, context.getCurrentIndex())) {
                if (!standardMessageFormat && !context.accepted(MessageFormat.SHORT)) {
                    throw new GTSMeteorologicalMessageParseException(ErrorCode.UNEXPECTED_HEADING_PREFIX, context.getCurrentIndex(), context.getContent());
                }
                context.increaseCurrentIndex(HEADING_PREFIX.length());
            } else if (!context.accepted(MessageFormat.HEADING_AND_TEXT)) {
                if (!standardMessageFormat && context.accepted(MessageFormat.STANDARD)) {
                    throw new GTSMeteorologicalMessageParseException(ErrorCode.MISSING_STARTING_LINE_PREFIX, context.getCurrentIndex(), context.getContent());
                } else if (context.accepted(MessageFormat.STANDARD) || context.accepted(MessageFormat.SHORT)) {
                    throw new GTSMeteorologicalMessageParseException(ErrorCode.MISSING_HEADING_PREFIX, context.getCurrentIndex(), context.getContent());
                }
            }
        }

        private int lookupTextEndIndex(final String content, final int messageEndIndex, final boolean standardMessageFormat) {
            final int textEndIndex;
            if (standardMessageFormat) {
                textEndIndex = messageEndIndex - END_OF_MESSAGE_SIGNALS.length();
                if (!content.startsWith(END_OF_MESSAGE_SIGNALS, textEndIndex)) {
                    throw new GTSMeteorologicalMessageParseException(ErrorCode.MISSING_END_OF_MESSAGE_SIGNALS, textEndIndex, content);
                }
            } else {
                textEndIndex = messageEndIndex;
            }
            return textEndIndex;
        }

        private String parseHeading(final ParsingContext context, final int endIndex) {
            final String content = context.getContent();
            final String truncatedFileContent = content.substring(0, Math.min(content.length(), context.getCurrentIndex() + endIndex));
            final int textPrefixIndex = truncatedFileContent.indexOf(TEXT_PREFIX, context.getCurrentIndex());
            if (textPrefixIndex < 0) {
                throw new GTSMeteorologicalMessageParseException(ErrorCode.NO_SEPARATION_OF_HEADING_AND_TEXT, context.getCurrentIndex(), content);
            }
            final String heading = content.substring(context.getCurrentIndex(), textPrefixIndex);
            final int headingLineBreaksIndex = indexOfAnyLineBreak(heading);
            if (headingLineBreaksIndex >= 0) {
                throw new GTSMeteorologicalMessageParseException(ErrorCode.UNEXPECTED_LINE_BREAKS_IN_HEADING,
                        context.getCurrentIndex() + headingLineBreaksIndex, content);
            }
            context.setCurrentIndex(textPrefixIndex + TEXT_PREFIX.length());
            return heading;
        }

        private String parseText(final ParsingContext context, final int endIndex) {
            final String content = context.getContent();
            final String text;
            try {
                text = content.substring(context.getCurrentIndex(), endIndex);
            } catch (final IndexOutOfBoundsException e) {
                throw new GTSMeteorologicalMessageParseException(ErrorCode.UNEXPECTED_END_OF_TEXT, context.getCurrentIndex(), content, e);
            }
            context.setCurrentIndex(endIndex);
            return text;
        }

        /**
         * Parses provided string for bulletin heading and text content and populates this builder with parsed headingAndText.
         * This parsing method is lenient. The first line of provided {@code headingAndText} is interpreted as the heading. Then any sequence of CR and LF
         * characters is skipped, and the remaining part of the {@code headingAndText} string is interpreted as text content as is. If provided
         * {@code headingAndText} contains no CR or LF characters, the whole string is interpreted as heading and text will be empty. If provided
         * {@code headingAndText} is an empty string, both heading and text will be empty.
         *
         * <p>
         * Note that this method does not affect any other property of this builder than {@code heading} and {@code text}.
         * </p>
         *
         * @param headingAndText
         *         heading and text string to parse
         *
         * @return this builder
         */
        public Builder parseHeadingAndTextLenient(final String headingAndText) {
            requireNonNull(headingAndText, "headingAndText");
            final String[] splitHeadingAndText = ANY_SEQUENCE_OF_LINE_BREAKS.split(headingAndText, 2);
            return setHeading(splitHeadingAndText.length > 0 ? splitHeadingAndText[0] : "")//
                    .setText(splitHeadingAndText.length > 1 ? splitHeadingAndText[1] : "");
        }

        public Builder clearTransmissionSequenceNumber() {
            return setTransmissionSequenceNumber("");
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
