package fi.fmi.avi.util;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.inferred.freebuilder.FreeBuilder;

import fi.fmi.avi.util.GTSDataExchangeParseException.ErrorCode;
import fi.fmi.avi.util.GTSMeteorologicalMessage.MessageFormat;

public final class GTSDataExchangeTranscoder {
    /**
     * Number of characters in message length string.
     */
    private static final int MESSAGE_LENGTH_LENGTH = 8;
    /**
     * Number of characters in format identifier string.
     */
    private static final int FORMAT_IDENTIFIER_LENGTH = 2;
    /**
     * Number of characters in message length and format identifier preceding the message.
     * This is not included in the message length.
     */
    private static final int MESSAGE_LENGTH_AND_FORMAT_CHARS_LENGTH = MESSAGE_LENGTH_LENGTH + FORMAT_IDENTIFIER_LENGTH;
    private static final Set<Protocol> ALL_PROTOCOLS = Collections.unmodifiableSet(EnumSet.allOf(Protocol.class));
    private static final Pattern MESSAGE_START_PATTERN = Pattern.compile("[0-9]{8}" + Arrays.stream(FormatIdentifier.values())//
            .map(formatIdentifier -> Pattern.quote(formatIdentifier.getCode() + formatIdentifier.getMessageFormat().getPrefix()))//
            .collect(Collectors.joining("|", "(", ")")));

    private GTSDataExchangeTranscoder() {
        throw new AssertionError();
    }

    /**
     * Parses provided string for a GTS exchange data content.
     * Parsing will accept any of supported {@link Protocol protocols}.
     * The string is read up to message length stated in the beginning of the string. Any remaining part of the string is simply ignored.
     *
     * <p>
     * This method does a string parsing considering the GTS exchange file structure and the signal sequences within it. It does not however look into the
     * contents of the heading or text part in any way, except for checking that heading does not contain any line break (CR and LF) characters.
     * The heading and text are only stored as is.
     * </p>
     *
     * @param fileContent
     *         string to parse
     *
     * @return result of parsing
     */
    public static ParseResult parse(final String fileContent) {
        return parse(fileContent, 0, ALL_PROTOCOLS);
    }

    /**
     * Parses provided string for a GTS exchange data content.
     * Parsing will recognize only the provided {@code requiredProtocol}.
     * The string is read up to message length stated in the beginning of the string. Any remaining part of the string is simply ignored.
     *
     * <p>
     * This method does a string parsing considering the GTS exchange file structure and the signal sequences within it. It does not however look into the
     * contents of the heading or text part in any way, except for checking that heading does not contain any line break (CR and LF) characters.
     * The heading and text are only stored as is.
     * </p>
     *
     * @param fileContent
     *         string to parse
     * @param requiredProtocol
     *         required protocol
     *
     * @return result of parsing
     */
    public static ParseResult parse(final String fileContent, final Protocol requiredProtocol) {
        requireNonNull(requiredProtocol, "requiredProtocol");
        return parse(fileContent, 0, Collections.singleton(requiredProtocol));
    }

    /**
     * Parses provided string for a GTS exchange data content.
     * Parsing will start at {@code offset} index and recognize only {@code acceptedProtocols}.
     * The string is read up to message length stated in the beginning of the string. Any remaining part of the string is simply ignored.
     *
     * <p>
     * This method does a string parsing considering the GTS exchange file structure and the signal sequences within it. It does not however look into the
     * contents of the heading or text part in any way, except for checking that heading does not contain any line break (CR and LF) characters.
     * The heading and text are only stored as is.
     * </p>
     *
     * @param fileContent
     *         string to parse
     * @param offset
     *         offset indicating start index of parsing
     * @param acceptedProtocols
     *         accepted protocols
     *
     * @return result of parsing
     */
    public static ParseResult parse(final String fileContent, final int offset, final Set<Protocol> acceptedProtocols) {
        requireNonNull(fileContent, "fileContent");
        requireNonNull(acceptedProtocols, "acceptedProtocols");
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must not be negative; was: " + offset);
        }
        if (acceptedProtocols.isEmpty()) {
            throw new IllegalArgumentException("acceptedProtocols must not be empty");
        }

        final ParseResult.Builder builder = ParseResult.builder()//
                .setStartIndex(offset);
        int currentIndex = offset;

        // parse message length
        final int messageLength;
        try {
            messageLength = Integer.parseInt(fileContent.substring(currentIndex, currentIndex + MESSAGE_LENGTH_LENGTH));
        } catch (final IndexOutOfBoundsException e) {
            return builder.buildError(new GTSDataExchangeParseException(ErrorCode.UNEXPECTED_END_OF_MESSAGE_LENGTH, currentIndex, fileContent, e));
        } catch (final NumberFormatException e) {
            return builder.buildError(new GTSDataExchangeParseException(ErrorCode.INVALID_MESSAGE_LENGTH, currentIndex, fileContent, e));
        }
        currentIndex += MESSAGE_LENGTH_LENGTH;

        // parse format identifier / protocol and message format
        final FormatIdentifier formatIdentifier;
        try {
            formatIdentifier = FormatIdentifier.fromCode(fileContent.substring(currentIndex, currentIndex + FORMAT_IDENTIFIER_LENGTH));
        } catch (final IndexOutOfBoundsException e) {
            return builder.buildError(new GTSDataExchangeParseException(ErrorCode.UNEXPECTED_END_OF_FORMAT_IDENTIFIER, currentIndex, fileContent, e));
        } catch (final IllegalArgumentException e) {
            return builder.buildError(new GTSDataExchangeParseException(ErrorCode.INVALID_FORMAT_IDENTIFIER, currentIndex, fileContent));
        }
        builder.setProtocol(formatIdentifier.getProtocol());
        currentIndex += FORMAT_IDENTIFIER_LENGTH;

        // parse message
        try {
            builder.setMessage(GTSMeteorologicalMessage.builder()//
                    .parse(fileContent, currentIndex, messageLength, Collections.singleton(formatIdentifier.getMessageFormat()))//
                    .build());
        } catch (final GTSDataParseException e) {
            return builder.buildError(e);
        }

        return builder.build();
    }

    /**
     * Parses the provided string into GTS exchange file templates.
     * Parsing will accept any of supported {@link Protocol protocols}.
     * The string can contain multiple bulletins.
     *
     * @param fileContent
     *         file content
     *
     * @return a list of parse results
     */
    public static List<ParseResult> parseAll(final String fileContent) {
        return parseAll(fileContent, ALL_PROTOCOLS);
    }

    /**
     * Parses the provided string into GTS exchange file templates.
     * Parsing will recognize only the provided {@code requiredProtocol}.
     * The string can contain multiple bulletins.
     *
     * @param fileContent
     *         file content
     * @param requiredProtocol
     *         required protocol
     *
     * @return a list of parse results
     */
    public static List<ParseResult> parseAll(final String fileContent, final Protocol requiredProtocol) {
        requireNonNull(requiredProtocol, "requiredProtocol");
        return parseAll(fileContent, Collections.singleton(requiredProtocol));
    }

    /**
     * Parses the provided string into GTS exchange file templates.
     * Parsing will recognize only {@code acceptedProtocols}.
     * The string can contain multiple bulletins.
     *
     * @param fileContent
     *         file content
     * @param acceptedProtocols
     *         accepted protocols
     *
     * @return a list of parse results
     */
    public static List<ParseResult> parseAll(final String fileContent, final Set<Protocol> acceptedProtocols) {
        requireNonNull(fileContent, "fileContent");
        requireNonNull(acceptedProtocols, "acceptedProtocols");
        if (acceptedProtocols.isEmpty()) {
            throw new IllegalArgumentException("acceptedProtocols must not be empty");
        }

        final List<ParseResult> results = new ArrayList<>();
        int nextIndex = 0;
        while (hasNonWhitespaceContent(fileContent, nextIndex)) {
            final int currentIndex = nextIndex;
            final ParseResult result = parse(fileContent, currentIndex, acceptedProtocols);
            results.add(result);
            nextIndex = result.getMessage()//
                    .map(template -> currentIndex + MESSAGE_LENGTH_AND_FORMAT_CHARS_LENGTH + template.getLength())//
                    .orElseGet(() -> {
                        final Matcher matcher = MESSAGE_START_PATTERN.matcher(fileContent);
                        if (matcher.find(currentIndex + 1)) {
                            return matcher.start();
                        } else {
                            return fileContent.length();
                        }
                    });
        }
        return Collections.unmodifiableList(results);
    }

    private static boolean hasNonWhitespaceContent(final String fileContent, final int offset) {
        for (int i = offset; i < fileContent.length(); i++) {
            if (!Character.isWhitespace(fileContent.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the provided {@code message} as string conforming to specified {@code protocol}.
     *
     * @param message
     *         message
     * @param protocol
     *         protocol
     *
     * @return message as string
     */
    public static String toString(final GTSMeteorologicalMessage message, final Protocol protocol) {
        requireNonNull(message, "message");
        requireNonNull(protocol, "protocol");
        return toString(message, protocol, MessageFormat.get(message));
    }

    /**
     * Returns the provided {@code message} as string conforming to specified {@code protocol} and {@code messageFormat}.
     *
     * @param message
     *         message
     * @param protocol
     *         protocol
     * @param messageFormat
     *         message format
     *
     * @return message as string
     *
     * @throws IllegalArgumentException
     *         if message cannot be formatted in provided {@code protocol} and {@code messageFormat}
     */
    public static String toString(final GTSMeteorologicalMessage message, final Protocol protocol, final MessageFormat messageFormat) {
        requireNonNull(message, "message");
        requireNonNull(protocol, "protocol");
        requireNonNull(messageFormat, "messageFormat");
        return String.format(Locale.ROOT, "%08d%s%s", //
                message.getLength(messageFormat), //
                FormatIdentifier.fromProtocolAndFormat(protocol, messageFormat).getCode(), //
                message.toString(messageFormat));
    }

    public enum Protocol {
        FTP, SOCKET
    }

    private enum FormatIdentifier {
        FTP_STANDARD("00", Protocol.FTP, MessageFormat.STANDARD), //
        FTP_SHORT("01", Protocol.FTP, MessageFormat.SHORT), //
        SOCKET_ALPHANUMERIC("AN", Protocol.SOCKET, MessageFormat.STANDARD);

        private static final List<FormatIdentifier> VALUES = Collections.unmodifiableList(Arrays.asList(values()));

        private final String code;
        private final Protocol protocol;
        private final MessageFormat messageFormat;

        FormatIdentifier(final String code, final Protocol protocol, final MessageFormat messageFormat) {
            this.protocol = protocol;
            this.code = code;
            this.messageFormat = messageFormat;
        }

        public static FormatIdentifier fromCode(final String code) {
            for (final FormatIdentifier identifier : VALUES) {
                if (identifier.getCode().equals(code)) {
                    return identifier;
                }
            }
            throw new IllegalArgumentException("Unsupported code: " + code);
        }

        public static FormatIdentifier fromProtocolAndFormat(final Protocol protocol, final MessageFormat messageFormat) {
            for (final FormatIdentifier identifier : VALUES) {
                if (identifier.getProtocol() == protocol && identifier.getMessageFormat() == messageFormat) {
                    return identifier;
                }
            }
            throw new IllegalArgumentException("Unsupported protocol/messageFormat: " + protocol + "/" + messageFormat);
        }

        public String getCode() {
            return code;
        }

        public Protocol getProtocol() {
            return protocol;
        }

        public MessageFormat getMessageFormat() {
            return messageFormat;
        }
    }

    @FreeBuilder
    public static abstract class ParseResult {
        ParseResult() {
        }

        static Builder builder() {
            return new Builder();
        }

        /**
         * Returns the start index of parsing.
         *
         * @return start index of parsing
         */
        public abstract int getStartIndex();

        /**
         * Returns protocol, when recognized.
         *
         * @return protocol or empty Optional
         */
        public abstract Optional<Protocol> getProtocol();

        /**
         * Returns the GTS exchange file template.
         * When this is present, {@link ParseResult#getError()} will return an empty optional.
         *
         * @return GTS exchange file template or an empty Optional
         */
        public abstract Optional<GTSMeteorologicalMessage> getMessage();

        /**
         * Returns the parse error when the message does not meet requirements of WMO Doc. 386 specification or could not be parsed for some other reason.
         * When this is present, {@link ParseResult#getMessage()} will return an empty optional.
         *
         * @return exception providing details of the parse error or an empty optional
         */
        public abstract Optional<GTSDataParseException> getError();

        abstract Builder toBuilder();

        static class Builder extends GTSDataExchangeTranscoder_ParseResult_Builder {
            Builder() {
            }

            @Override
            public ParseResult build() {
                if (getMessage().isPresent() == getError().isPresent()) {
                    throw new IllegalStateException(
                            "Message and error are mutually exclusive and cannot be " + (getMessage().isPresent() ? "present" : "empty") + " simultaneously");
                }
                return super.build();
            }

            public ParseResult buildError(final GTSDataParseException error) {
                return setError(error)//
                        .clearMessage()//
                        .build();
            }
        }
    }
}
