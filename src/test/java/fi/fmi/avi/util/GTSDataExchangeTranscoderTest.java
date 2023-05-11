package fi.fmi.avi.util;

import static fi.fmi.avi.model.bulletin.MeteorologicalBulletinSpecialCharacter.CARRIAGE_RETURN;
import static fi.fmi.avi.model.bulletin.MeteorologicalBulletinSpecialCharacter.LINE_FEED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;

import com.google.common.collect.Sets;

import fi.fmi.avi.model.bulletin.MeteorologicalBulletinSpecialCharacter;
import fi.fmi.avi.util.GTSDataExchangeParseException.ErrorCode;
import fi.fmi.avi.util.GTSDataExchangeTranscoder.ParseResult;
import fi.fmi.avi.util.GTSDataExchangeTranscoder.Protocol;
import fi.fmi.avi.util.GTSMeteorologicalMessage.MessageFormat;

public class GTSDataExchangeTranscoderTest {
    private static final String FTP_STANDARD_MESSAGE = "00000080" + "00" + "\u0001\r\r\n" //
            + "013" + "\r\r\n"//
            + "FTYU31 YUDO 160000" + "\r\r\n" //
            + "TAF YUDO 160000Z NIL=\r\r\n" //
            + "TAF YUDD 160000Z NIL=" + "\r\r\n\u0003";
    private static final String FTP_SHORT_MESSAGE = "00000069" + "01" + "\r\r\n"//
            + "FTYU31 YUDO 160000" + "\r\r\n" //
            + "TAF YUDO 160000Z NIL=\r\r\n" //
            + "TAF YUDD 160000Z NIL=";
    private static final String SOCKET_ALPHANUMERIC_MESSAGE = "00000080" + "AN" + "\u0001\r\r\n" //
            + "013" + "\r\r\n"//
            + "FTYU31 YUDO 160000" + "\r\r\n" //
            + "TAF YUDO 160000Z NIL=\r\r\n" //
            + "TAF YUDD 160000Z NIL=" + "\r\r\n\u0003";
    private static final String MESSAGE_HEADING = "FTYU31 YUDO 160000";
    private static final String MESSAGE_TEXT = "TAF YUDO 160000Z NIL=\r\r\n" //
            + "TAF YUDD 160000Z NIL=";
    private static final String MESSAGE_SEQ_NUMBER_STRING = "013";
    private static final GTSMeteorologicalMessage STANDARD_MESSAGE = GTSMeteorologicalMessage.builder()//
            .setTransmissionSequenceNumber(MESSAGE_SEQ_NUMBER_STRING)//
            .setHeading(MESSAGE_HEADING)//
            .setText(MESSAGE_TEXT)//
            .build();
    private static final ParseResult FTP_STANDARD_PARSE_RESULT = ParseResult.builder()//
            .setProtocol(Protocol.FTP)//
            .setStartIndex(0)//
            .setMessage(STANDARD_MESSAGE)//
            .build();
    private static final ParseResult SOCKET_ALPHANUMERIC_PARSE_RESULT = ParseResult.builder()//
            .setProtocol(Protocol.SOCKET)//
            .setStartIndex(0)//
            .setMessage(STANDARD_MESSAGE)//
            .build();
    private static final GTSMeteorologicalMessage SHORT_MESSAGE = GTSMeteorologicalMessage.builder()//
            .clearTransmissionSequenceNumber()//
            .setHeading(MESSAGE_HEADING)//
            .setText(MESSAGE_TEXT)//
            .build();
    private static final ParseResult FTP_SHORTPARSE_RESULT = ParseResult.builder()//
            .setProtocol(Protocol.FTP)//
            .setStartIndex(0)//
            .setMessage(SHORT_MESSAGE)//
            .build();
    private static final Set<Protocol> ALL_PROTOCOLS = Collections.unmodifiableSet(EnumSet.allOf(Protocol.class));

    static String stringOf(final MeteorologicalBulletinSpecialCharacter... specialCharacters) {
        return Arrays.stream(specialCharacters)//
                .map(MeteorologicalBulletinSpecialCharacter::getContent)//
                .collect(Collectors.joining());
    }

    private static String checkMessageLength(final String gtsMessage) {
        final int reportedMessageLength = Integer.parseInt(gtsMessage.substring(0, 8));
        final int actualMessageLength = gtsMessage.length() - 10;
        assertThat(actualMessageLength)//
                .as("test integrity: message length")//
                .isEqualTo(reportedMessageLength);
        return gtsMessage;
    }

    private static void assertEqualProperties(final ParseResult actual, final ParseResult expected) {
        assertThat(actual).as("actual").isNotNull();
        assertThat(actual.getProtocol()).as("protocol").isEqualTo(expected.getProtocol());
        assertThat(actual.getStartIndex()).as("startIndex").isEqualTo(expected.getStartIndex());
        assertThat(actual.getMessage()).as("message").isPresent();
        assertThat(expected.getMessage()).as("expected message").isPresent();
        assertEqualProperties(actual.getMessage().get(), expected.getMessage().get());
        assertThat(actual).isEqualTo(expected);
    }

    private static void assertEqualProperties(final GTSMeteorologicalMessage actual, final GTSMeteorologicalMessage expected) {
        assertThat(actual).as("actual").isNotNull();
        assertThat(actual.getLength()).as("getLength").isEqualTo(expected.getLength());
        assertThat(actual.getTransmissionSequenceNumber()).as("getTransmissionSequenceNumber").isEqualTo(expected.getTransmissionSequenceNumber());
        assertThat(actual.getTransmissionSequenceNumberAsInt()).as("getTransmissionSequenceNumberAsInt")
                .isEqualTo(expected.getTransmissionSequenceNumberAsInt());
        assertThat(actual.getHeading()).as("getHeading").isEqualTo(expected.getHeading());
        assertThat(actual.getText()).as("getText").isEqualTo(expected.getText());
        assertThat(actual).isEqualTo(expected);
    }

    private static void assertError(final ParseResult result, final Protocol expectedProtocol, final GTSDataParseException.ErrorCode expectedErrorCode,
            final int expectedIndex, final String expectedFailedMessage) {
        assertThat(result.getError()).as("error").isNotEmpty();
        assertGTSDataParseException(expectedErrorCode, expectedIndex, expectedFailedMessage, result.getError().get());
        assertThat(result.getMessage()).as("message").isEmpty();
        assertThat(result.getProtocol().orElse(null)).as("protocol").isEqualTo(expectedProtocol);
    }

    private static void assertGTSDataParseException(final GTSDataParseException.ErrorCode expectedErrorCode, final int expectedIndex,
            final String expectedFailedMessage, final GTSDataParseException exception) {
        final Class<?> expectedExceptionType = expectedErrorCode.getClass().getDeclaringClass();
        assertThat(exception).isInstanceOf(expectedExceptionType);
        assertThat(exception.getMessage()).as("getMessage").isEqualTo(expectedErrorCode.message(expectedIndex));
        assertThat(exception.getErrorCode()).as("getErrorCode").isEqualTo(expectedErrorCode);
        assertThat(exception.getIndex()).as("getIndex").isEqualTo(expectedIndex);
        assertThat(exception.getFailedMessage()).as("getFailedMessage").isEqualTo(expectedFailedMessage);
    }

    private static Stream<Set<Protocol>> protocolsContaining(final Protocol requiredProtocol) {
        return Sets.powerSet(EnumSet.allOf(Protocol.class)).stream()//
                .filter(set -> set.contains(requiredProtocol));
    }

    private static Stream<Set<Protocol>> allProtocols() {
        return Sets.powerSet(EnumSet.allOf(Protocol.class)).stream()//
                .filter(set -> !set.isEmpty());
    }

    @Test
    public void parse_given_negative_offset_throws_exception() {
        assertThatIllegalArgumentException()//
                .isThrownBy(() -> GTSDataExchangeTranscoder.parse(FTP_STANDARD_MESSAGE, -1, ALL_PROTOCOLS))//
                .withMessageContaining("Offset")//
                .withMessageContaining("negative");
    }

    @Test
    public void parse_given_empty_protocols_throws_exception() {
        assertThatIllegalArgumentException()//
                .isThrownBy(() -> GTSDataExchangeTranscoder.parse(FTP_STANDARD_MESSAGE, 0, Collections.emptySet()))//
                .withMessageContaining("acceptedProtocols")//
                .withMessageContaining("empty");
    }

    @Test
    public void parse_parses_FTP_STANDARD_message() {
        protocolsContaining(Protocol.FTP)//
                .forEach(protocols -> {
                    final ParseResult result = GTSDataExchangeTranscoder.parse(checkMessageLength(FTP_STANDARD_MESSAGE), 0, protocols);
                    assertEqualProperties(result, FTP_STANDARD_PARSE_RESULT);
                });
    }

    @Test
    public void parse_parses_FTP_SHORT_message() {
        protocolsContaining(Protocol.FTP)//
                .forEach(protocols -> {
                    final ParseResult result = GTSDataExchangeTranscoder.parse(checkMessageLength(FTP_SHORT_MESSAGE), 0, protocols);
                    assertEqualProperties(result, FTP_SHORTPARSE_RESULT);
                });
    }

    @Test
    public void parse_parses_SOCKET_ALPHANUMERIC_message() {
        protocolsContaining(Protocol.SOCKET)//
                .forEach(protocols -> {
                    final ParseResult result = GTSDataExchangeTranscoder.parse(checkMessageLength(SOCKET_ALPHANUMERIC_MESSAGE), 0, protocols);
                    assertEqualProperties(result, SOCKET_ALPHANUMERIC_PARSE_RESULT);
                });
    }

    @Test
    public void parse_fails_when_message_ends_before_complete_message_length() {
        final String inputMessage = "1234567";
        allProtocols()//
                .forEach(protocols -> {
                    final ParseResult result = GTSDataExchangeTranscoder.parse(inputMessage, 0, protocols);
                    assertError(result, null, ErrorCode.UNEXPECTED_END_OF_MESSAGE_LENGTH, 0, inputMessage);
                });
    }

    @Test
    public void parse_fails_when_message_length_is_not_a_number() {
        final String messageLengthString = "1234567A";
        final String inputMessage = new StringBuilder(FTP_STANDARD_MESSAGE)//
                .replace(0, messageLengthString.length(), messageLengthString)//
                .toString();
        allProtocols()//
                .forEach(protocols -> {
                    final ParseResult result = GTSDataExchangeTranscoder.parse(inputMessage, 0, protocols);
                    assertError(result, null, ErrorCode.INVALID_MESSAGE_LENGTH, 0, inputMessage);
                });
    }

    @Test
    public void parse_fails_when_message_ends_before_complete_format_identifier() {
        final String inputMessage = "123456780";
        allProtocols()//
                .forEach(protocols -> {
                    final ParseResult result = GTSDataExchangeTranscoder.parse(inputMessage, 0, protocols);
                    assertError(result, null, ErrorCode.UNEXPECTED_END_OF_FORMAT_IDENTIFIER, 8, inputMessage);
                });
    }

    @Test
    public void parse_fails_when_message_ends_before_complete_format_identifier2() {
        final String inputMessage = "123456780" + GTSMeteorologicalMessage.END_OF_MESSAGE_SIGNALS;
        allProtocols()//
                .forEach(protocols -> {
                    final ParseResult result = GTSDataExchangeTranscoder.parse(inputMessage, 0, protocols);
                    assertError(result, null, ErrorCode.INVALID_FORMAT_IDENTIFIER, 8, inputMessage);
                });
    }

    @Test
    public void parse_fails_when_format_identifier_is_not_a_number() {
        final int formatIdentifierIndex = 8;
        final String formatIdentifierString = "0A";
        final String inputMessage = new StringBuilder(checkMessageLength(FTP_STANDARD_MESSAGE))//
                .replace(formatIdentifierIndex, formatIdentifierIndex + formatIdentifierString.length(), formatIdentifierString)//
                .toString();
        allProtocols()//
                .forEach(protocols -> {
                    final ParseResult result = GTSDataExchangeTranscoder.parse(inputMessage, 0, protocols);
                    assertError(result, null, ErrorCode.INVALID_FORMAT_IDENTIFIER, 8, inputMessage);
                });
    }

    @Test
    public void parse_fails_when_format_identifier_is_not_a_supported_number() {
        final int formatIdentifierIndex = 8;
        final Stream<String> invalidFormatIdentifiers = IntStream.rangeClosed(2, 99)//
                .mapToObj(formatIdentifier -> String.format(Locale.ROOT, "%02d", formatIdentifier));
        assertThat(invalidFormatIdentifiers).allSatisfy(formatIdentifierString -> {
            final String inputMessage = new StringBuilder(FTP_STANDARD_MESSAGE)//
                    .replace(formatIdentifierIndex, formatIdentifierIndex + formatIdentifierString.length(), formatIdentifierString)//
                    .toString();
            allProtocols()//
                    .forEach(protocols -> {
                        final ParseResult result = GTSDataExchangeTranscoder.parse(inputMessage, 0, protocols);
                        assertError(result, null, ErrorCode.INVALID_FORMAT_IDENTIFIER, 8, inputMessage);
                    });
        });
    }

    @Test
    public void parse_fails_on_FTP_STANDARD_message_when_starting_line_prefix_is_not_found_at_expected_index() {
        final String inputMessage = "00000003" + "00" + "\u0001\r\r";
        allProtocols()//
                .forEach(protocols -> {
                    final ParseResult result = GTSDataExchangeTranscoder.parse(checkMessageLength(inputMessage), 0, protocols);
                    assertError(result, Protocol.FTP, GTSMeteorologicalMessageParseException.ErrorCode.MISSING_STARTING_LINE_PREFIX, 10, inputMessage);
                });
    }

    @Test
    public void parse_fails_on_SOCKET_ALPHANUMERIC_message_when_starting_line_prefix_is_not_found_at_expected_index() {
        final String inputMessage = "00000003" + "AN" + "\u0001\r\r";
        allProtocols()//
                .forEach(protocols -> {
                    final ParseResult result = GTSDataExchangeTranscoder.parse(checkMessageLength(inputMessage), 0, protocols);
                    assertError(result, Protocol.SOCKET, GTSMeteorologicalMessageParseException.ErrorCode.MISSING_STARTING_LINE_PREFIX, 10, inputMessage);
                });
    }

    @Test
    public void parse_fails_on_FTP_STANDARD_message_when_transmission_sequence_number_is_not_followed_by_heading_prefix() {
        final String inputMessage = "00000027" + "00" + "\u0001\r\r\n" //
                + "013" + "\r\r"//
                + "FTYU31 YUDO 160000";
        allProtocols()//
                .forEach(protocols -> {
                    final ParseResult result = GTSDataExchangeTranscoder.parse(checkMessageLength(inputMessage), 0, protocols);
                    assertError(result, Protocol.FTP, GTSMeteorologicalMessageParseException.ErrorCode.UNEXPECTED_END_OF_TRANSMISSION_SEQUENCE_NUMBER, 14,
                            inputMessage);
                });
    }

    @Test
    public void parse_fails_on_SOCKET_ALPHANUMERIC_message_when_transmission_sequence_number_is_not_followed_by_heading_prefix() {
        final String inputMessage = "00000027" + "AN" + "\u0001\r\r\n" //
                + "013" + "\r\r"//
                + "FTYU31 YUDO 160000";
        allProtocols()//
                .forEach(protocols -> {
                    final ParseResult result = GTSDataExchangeTranscoder.parse(checkMessageLength(inputMessage), 0, protocols);
                    assertError(result, Protocol.SOCKET, GTSMeteorologicalMessageParseException.ErrorCode.UNEXPECTED_END_OF_TRANSMISSION_SEQUENCE_NUMBER, 14,
                            inputMessage);
                });
    }

    @Test
    public void parse_fails_on_FTP_STANDARD_message_when_transmission_sequence_number_contains_line_break_chars() {
        final String inputMessage = "00000079" + "00" + "\u0001\r\r\n" //
                + "013" + "\r\r"//
                + "FTYU31 YUDO 160000" + "\r\r\n" //
                + "TAF YUDO 160000Z NIL=\r\r\n" //
                + "TAF YUDD 160000Z NIL=" + "\r\r\n\u0003";
        allProtocols()//
                .forEach(protocols -> {
                    final ParseResult result = GTSDataExchangeTranscoder.parse(checkMessageLength(inputMessage), 0, protocols);
                    assertError(result, Protocol.FTP, GTSMeteorologicalMessageParseException.ErrorCode.UNEXPECTED_LINE_BREAKS_IN_TRANSMISSION_SEQUENCE_NUMBER,
                            17, inputMessage);
                });
    }

    @Test
    public void parse_fails_on_SOCKET_ALPHANUMERIC_message_when_transmission_sequence_number_contains_line_break_chars() {
        final String inputMessage = "00000079" + "AN" + "\u0001\r\r\n" //
                + "013" + "\r\r"//
                + "FTYU31 YUDO 160000" + "\r\r\n" //
                + "TAF YUDO 160000Z NIL=\r\r\n" //
                + "TAF YUDD 160000Z NIL=" + "\r\r\n\u0003";
        allProtocols()//
                .forEach(protocols -> {
                    final ParseResult result = GTSDataExchangeTranscoder.parse(checkMessageLength(inputMessage), 0, protocols);
                    assertError(result, Protocol.SOCKET,
                            GTSMeteorologicalMessageParseException.ErrorCode.UNEXPECTED_LINE_BREAKS_IN_TRANSMISSION_SEQUENCE_NUMBER, 17, inputMessage);
                });
    }

    @Test
    public void parse_fails_on_FTP_STANDARD_message_when_message_does_not_end_with_expected_end_signals() {
        final String inputMessage = "00000079" + "00" + "\u0001\r\r\n" //
                + "013" + "\r\r\n"//
                + "FTYU31 YUDO 160000" + "\r\r\n" //
                + "TAF YUDO 160000Z NIL=\r\r\n" //
                + "TAF YUDD 160000Z NIL=" + "\r\n\u0003";
        allProtocols()//
                .forEach(protocols -> {
                    final ParseResult result = GTSDataExchangeTranscoder.parse(checkMessageLength(inputMessage), 0, protocols);
                    assertError(result, Protocol.FTP, GTSMeteorologicalMessageParseException.ErrorCode.MISSING_END_OF_MESSAGE_SIGNALS, 85, inputMessage);
                });
    }

    @Test
    public void parse_fails_on_SOCKET_ALPHANUMERIC_message_when_message_does_not_end_with_expected_end_signals() {
        final String inputMessage = "00000079" + "AN" + "\u0001\r\r\n" //
                + "013" + "\r\r\n"//
                + "FTYU31 YUDO 160000" + "\r\r\n" //
                + "TAF YUDO 160000Z NIL=\r\r\n" //
                + "TAF YUDD 160000Z NIL=" + "\r\n\u0003";
        allProtocols()//
                .forEach(protocols -> {
                    final ParseResult result = GTSDataExchangeTranscoder.parse(checkMessageLength(inputMessage), 0, protocols);
                    assertError(result, Protocol.SOCKET, GTSMeteorologicalMessageParseException.ErrorCode.MISSING_END_OF_MESSAGE_SIGNALS, 85, inputMessage);
                });
    }

    @Test
    public void parse_fails_on_FTP_STANDARD_message_when_heading_contains_line_break_characters() {
        final Stream<String> inputMessages = Stream.of(//
                        stringOf(CARRIAGE_RETURN), //
                        stringOf(LINE_FEED), //
                        stringOf(CARRIAGE_RETURN, LINE_FEED), //
                        stringOf(LINE_FEED, CARRIAGE_RETURN), //
                        stringOf(LINE_FEED, CARRIAGE_RETURN, LINE_FEED, LINE_FEED, CARRIAGE_RETURN, LINE_FEED))// Contains no CR+CR+LF sequence
                .map(separator -> {
                    final String message = "\u0001\r\r\n" //
                            + "013" + "\r\r\n"//
                            + "FTYU31 YUDO 160000" + separator //
                            + "TAF YUDO 160000Z NIL=" + "\r\r\n" //
                            + "TAF YUDD 160000Z NIL=" + "\r\r\n\u0003";
                    return String.format(Locale.ROOT, "%08d00%s", message.length(), message);
                });
        assertThat(inputMessages)//
                .allSatisfy(inputMessage -> allProtocols()//
                        .forEach(protocols -> {
                            final ParseResult result = GTSDataExchangeTranscoder.parse(checkMessageLength(inputMessage), 0, protocols);
                            assertError(result, Protocol.FTP, GTSMeteorologicalMessageParseException.ErrorCode.UNEXPECTED_LINE_BREAKS_IN_HEADING, 38,
                                    inputMessage);
                        }));
    }

    @Test
    public void parse_fails_on_SOCKET_ALPHANUMERIC_message_when_heading_contains_line_break_characters() {
        final Stream<String> inputMessages = Stream.of(//
                        stringOf(CARRIAGE_RETURN), //
                        stringOf(LINE_FEED), //
                        stringOf(CARRIAGE_RETURN, LINE_FEED), //
                        stringOf(LINE_FEED, CARRIAGE_RETURN), //
                        stringOf(LINE_FEED, CARRIAGE_RETURN, LINE_FEED, LINE_FEED, CARRIAGE_RETURN, LINE_FEED))// Contains no CR+CR+LF sequence
                .map(separator -> {
                    final String message = "\u0001\r\r\n" //
                            + "013" + "\r\r\n"//
                            + "FTYU31 YUDO 160000" + separator //
                            + "TAF YUDO 160000Z NIL=" + "\r\r\n" //
                            + "TAF YUDD 160000Z NIL=" + "\r\r\n\u0003";
                    return String.format(Locale.ROOT, "%08d00%s", message.length(), message);
                });
        assertThat(inputMessages)//
                .allSatisfy(inputMessage -> allProtocols()//
                        .forEach(protocols -> {
                            final ParseResult result = GTSDataExchangeTranscoder.parse(checkMessageLength(inputMessage), 0, protocols);
                            assertError(result, Protocol.FTP, GTSMeteorologicalMessageParseException.ErrorCode.UNEXPECTED_LINE_BREAKS_IN_HEADING, 38,
                                    inputMessage);
                        }));
    }

    @Test
    public void parse_fails_on_FTP_SHORT_message_when_heading_contains_line_break_characters() {
        final Stream<String> inputMessages = Stream.of(//
                        stringOf(CARRIAGE_RETURN), //
                        stringOf(LINE_FEED), //
                        stringOf(CARRIAGE_RETURN, LINE_FEED), //
                        stringOf(LINE_FEED, CARRIAGE_RETURN), //
                        stringOf(LINE_FEED, CARRIAGE_RETURN, LINE_FEED, LINE_FEED, CARRIAGE_RETURN, LINE_FEED))// Contains no CR+CR+LF sequence
                .map(separator -> {
                    final String message = "\r\r\n"//
                            + "FTYU31 YUDO 160000" + separator //
                            + "TAF YUDO 160000Z NIL=" + "\r\r\n" //
                            + "TAF YUDD 160000Z NIL=";
                    return String.format(Locale.ROOT, "%08d01%s", message.length(), message);
                });
        assertThat(inputMessages)//
                .allSatisfy(inputMessage -> allProtocols()//
                        .forEach(protocols -> {
                            final ParseResult result = GTSDataExchangeTranscoder.parse(checkMessageLength(inputMessage), 0, protocols);
                            assertError(result, Protocol.FTP, GTSMeteorologicalMessageParseException.ErrorCode.UNEXPECTED_LINE_BREAKS_IN_HEADING, 31,
                                    inputMessage);
                        }));
    }

    @Test
    public void parse_fails_on_FTP_SHORT_message_when_heading_is_not_preceded_by_expected_prefix() {
        final String inputMessage = "00000068" + "01" + "\r\r" //
                + "FTYU31 YUDO 160000" + "\r\r\n" //
                + "TAF YUDO 160000Z NIL=\r\r\n" //
                + "TAF YUDD 160000Z NIL=";
        allProtocols()//
                .forEach(protocols -> {
                    final ParseResult result = GTSDataExchangeTranscoder.parse(checkMessageLength(inputMessage), 0, protocols);
                    assertError(result, Protocol.FTP, GTSMeteorologicalMessageParseException.ErrorCode.MISSING_HEADING_PREFIX, 10, inputMessage);
                });
    }

    @Test
    public void parse_fails_on_FTP_SHORT_message_when_heading_and_text_separator_not_found() {
        final String inputMessage = "00000043" + "01" + "\r\r\n"//
                + "FTYU31 YUDO 160000 " //
                + "TAF YUDD 160000Z NIL=";
        allProtocols()//
                .forEach(protocols -> {
                    final ParseResult result = GTSDataExchangeTranscoder.parse(checkMessageLength(inputMessage), 0, protocols);
                    assertError(result, Protocol.FTP, GTSMeteorologicalMessageParseException.ErrorCode.NO_SEPARATION_OF_HEADING_AND_TEXT, 13, inputMessage);
                });
    }

    @Test
    public void parse_fails_on_FTP_SHORT_message_when_actual_content_is_smaller_than_reported_message_length() {
        final String inputMessage = "00000100" + "01" + "\r\r\n"//
                + "FTYU31 YUDO 160000" + "\r\r\n" //
                + "TAF YUDO 160000Z NIL=" + "\r\r\n" //
                + "TAF YUDD 160000Z NIL=";
        allProtocols()//
                .forEach(protocols -> {
                    final ParseResult result = GTSDataExchangeTranscoder.parse(inputMessage, 0, protocols);
                    assertError(result, Protocol.FTP, GTSMeteorologicalMessageParseException.ErrorCode.UNEXPECTED_END_OF_TEXT, 34, inputMessage);
                });
    }

    @Test
    public void parseAll_returns_empty_list_given_empty_string() {
        allProtocols().forEach(protocols -> {
            final List<ParseResult> messages = GTSDataExchangeTranscoder.parseAll("", protocols);
            assertThat(messages).isEmpty();
        });
    }

    @Test
    public void parseAll_parses_multiple_messages_in_supported_protocols_and_formats() {
        final StringBuilder inputMessageBuilder = new StringBuilder();
        inputMessageBuilder.append(FTP_STANDARD_MESSAGE);
        final int message2Index = inputMessageBuilder.length();
        inputMessageBuilder.append(FTP_SHORT_MESSAGE);
        final int message3Index = inputMessageBuilder.length();
        inputMessageBuilder.append(SOCKET_ALPHANUMERIC_MESSAGE);
        final String inputMessage = inputMessageBuilder.toString();
        final Iterator<ParseResult> messages = GTSDataExchangeTranscoder.parseAll(inputMessage).iterator();
        assertEqualProperties(messages.next(), FTP_STANDARD_PARSE_RESULT);
        assertEqualProperties(messages.next(), FTP_SHORTPARSE_RESULT.toBuilder().setStartIndex(message2Index).build());
        assertEqualProperties(messages.next(), SOCKET_ALPHANUMERIC_PARSE_RESULT.toBuilder().setStartIndex(message3Index).build());
        assertThat(messages.hasNext()).as("Expect no more messages").isFalse();
    }

    @Test
    public void parseAll_ignores_trailing_whitespace() {
        protocolsContaining(Protocol.FTP)//
                .forEach(protocols -> {
                    final Iterator<ParseResult> messages = GTSDataExchangeTranscoder.parseAll(FTP_STANDARD_MESSAGE + " \r\n", protocols).iterator();
                    assertEqualProperties(messages.next(), FTP_STANDARD_PARSE_RESULT);
                    assertThat(messages.hasNext()).as("Expect no more messages").isFalse();
                });
    }

    @Test
    public void parseAll_returns_single_error() {
        final String inputMessage = "000";
        allProtocols()//
                .forEach(protocols -> {
                    final Iterator<ParseResult> messages = GTSDataExchangeTranscoder.parseAll(inputMessage, protocols).iterator();
                    assertError(messages.next(), null, ErrorCode.UNEXPECTED_END_OF_MESSAGE_LENGTH, 0, inputMessage);
                    assertThat(messages.hasNext()).as("Expect no more messages").isFalse();
                });
    }

    @Test
    public void parseAll_returns_whitespaces_between_messages_as_errors() {
        final StringBuilder inputMessageBuilder = new StringBuilder();
        inputMessageBuilder.append(FTP_STANDARD_MESSAGE);
        final int error1Index = inputMessageBuilder.length();
        inputMessageBuilder.append("\n");
        final int message2Index = inputMessageBuilder.length();
        inputMessageBuilder.append(FTP_SHORT_MESSAGE);
        final int error2Index = inputMessageBuilder.length();
        inputMessageBuilder.append(" ");
        final int message3Index = inputMessageBuilder.length();
        inputMessageBuilder.append(SOCKET_ALPHANUMERIC_MESSAGE);
        final String inputMessage = inputMessageBuilder.toString();

        final Iterator<ParseResult> messages = GTSDataExchangeTranscoder.parseAll(inputMessage).iterator();
        assertEqualProperties(messages.next(), FTP_STANDARD_PARSE_RESULT);
        assertError(messages.next(), null, ErrorCode.INVALID_MESSAGE_LENGTH, error1Index, inputMessage);
        assertEqualProperties(messages.next(), FTP_SHORTPARSE_RESULT.toBuilder().setStartIndex(message2Index).build());
        assertError(messages.next(), null, ErrorCode.INVALID_MESSAGE_LENGTH, error2Index, inputMessage);
        assertEqualProperties(messages.next(), SOCKET_ALPHANUMERIC_PARSE_RESULT.toBuilder().setStartIndex(message3Index).build());
        assertThat(messages.hasNext()).as("Expect no more messages").isFalse();
    }

    @Test
    public void parseAll_continues_after_error() {
        final String inputMessage1 = "00000100" + "00" + "\u0001\r\r\n" //
                + "013" + "\r\r\n"//
                + "FTYU31 YUDO 160000" + "\r\r\n" //
                + "TAF YUDO 160000Z NIL=\r\r\n" //
                + "TAF YUDD 160000Z NIL=" + "\r\r\n\u0003";
        final String inputMessage = inputMessage1 + FTP_STANDARD_MESSAGE;
        final int message2Index = inputMessage1.length();
        protocolsContaining(Protocol.FTP)//
                .forEach(protocols -> {
                    final Iterator<ParseResult> messages = GTSDataExchangeTranscoder.parseAll(inputMessage, protocols).iterator();
                    assertError(messages.next(), Protocol.FTP, GTSMeteorologicalMessageParseException.ErrorCode.MISSING_END_OF_MESSAGE_SIGNALS, 106,
                            inputMessage);
                    assertEqualProperties(messages.next(), FTP_STANDARD_PARSE_RESULT.toBuilder().setStartIndex(message2Index).build());
                    assertThat(messages.hasNext()).as("Expect no more messages").isFalse();
                });
    }

    @Test
    public void toString_given_standard_message_returns_string_equal_to_original_FTP_STANDARD_message() {
        assertThat(GTSDataExchangeTranscoder.toString(STANDARD_MESSAGE, Protocol.FTP)).isEqualTo(FTP_STANDARD_MESSAGE);
    }

    @Test
    public void toString_given_short_message_returns_string_equal_to_original_FTP_SHORT_message() {
        assertThat(GTSDataExchangeTranscoder.toString(SHORT_MESSAGE, Protocol.FTP)).isEqualTo(FTP_SHORT_MESSAGE);
    }

    @Test
    public void toString_given_standard_message_returns_string_equal_to_original_SOCKET_ALPHANUMERIC_message() {
        assertThat(GTSDataExchangeTranscoder.toString(STANDARD_MESSAGE, Protocol.SOCKET)).isEqualTo(SOCKET_ALPHANUMERIC_MESSAGE);
    }

    @Test
    public void toString_given_FTP_STANDARD_outputs_expected_message() {
        assertThat(GTSDataExchangeTranscoder.toString(STANDARD_MESSAGE, Protocol.FTP, MessageFormat.STANDARD)).isEqualTo(FTP_STANDARD_MESSAGE);
    }

    @Test
    public void toString_given_FTP_SHORT_outputs_expected_message() {
        assertThat(GTSDataExchangeTranscoder.toString(STANDARD_MESSAGE, Protocol.FTP, MessageFormat.SHORT)).isEqualTo(FTP_SHORT_MESSAGE);
    }

    @Test
    public void toString_given_SOCKET_STANDARD_outputs_expected_message() {
        assertThat(GTSDataExchangeTranscoder.toString(STANDARD_MESSAGE, Protocol.SOCKET, MessageFormat.STANDARD)).isEqualTo(SOCKET_ALPHANUMERIC_MESSAGE);
    }

    @Test
    public void toString_given_HEADING_AND_TEXT_fails() {
        Arrays.stream(Protocol.values())//
                .forEach(protocol -> assertThatIllegalArgumentException()//
                        .isThrownBy(() -> GTSDataExchangeTranscoder.toString(STANDARD_MESSAGE, protocol, MessageFormat.HEADING_AND_TEXT)));
    }

    @Test
    public void toString_given_SOCKET_supports_only_STANDARD_message_format() {
        Arrays.stream(MessageFormat.values())//
                .filter(format -> format != MessageFormat.STANDARD)//
                .forEach(format -> assertThatIllegalArgumentException()//
                        .isThrownBy(() -> GTSDataExchangeTranscoder.toString(STANDARD_MESSAGE, Protocol.SOCKET, format)));
    }

    @Test
    public void parseResult_build_fails_when_both_message_and_error_are_empty() {
        final ParseResult.Builder builder = ParseResult.builder()//
                .setStartIndex(0)//
                .clearMessage()//
                .clearError();
        assertThatIllegalStateException()//
                .isThrownBy(() -> builder.build())//
                .withMessageContaining("Message")//
                .withMessageContaining("error")//
                .withMessageContaining("empty");
    }

    @Test
    public void parseResult_build_fails_when_both_message_and_error_are_present() {
        final ParseResult.Builder builder = ParseResult.builder()//
                .setStartIndex(0)//
                .setMessage(STANDARD_MESSAGE)//
                .setError(new GTSDataExchangeParseException(ErrorCode.INVALID_MESSAGE_LENGTH, 0, ""));
        assertThatIllegalStateException()//
                .isThrownBy(() -> builder.build())//
                .withMessageContaining("Message")//
                .withMessageContaining("error")//
                .withMessageContaining("present");
    }
}
