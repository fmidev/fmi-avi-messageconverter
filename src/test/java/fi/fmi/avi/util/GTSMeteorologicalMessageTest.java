package fi.fmi.avi.util;

import static fi.fmi.avi.model.bulletin.MeteorologicalBulletinSpecialCharacter.CARRIAGE_RETURN;
import static fi.fmi.avi.model.bulletin.MeteorologicalBulletinSpecialCharacter.LINE_FEED;
import static fi.fmi.avi.util.GTSMeteorologicalMessage.stringOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.Test;

import com.google.common.collect.Sets;

import fi.fmi.avi.util.GTSMeteorologicalMessage.MessageFormat;
import fi.fmi.avi.util.GTSMeteorologicalMessageParseException.ErrorCode;

public class GTSMeteorologicalMessageTest {
    private static final String INITIAL_SEQ_NUMBER_STRING = "097";
    private static final int INITIAL_SEQ_NUMBER = 97;
    private static final String MESSAGE_STANDARD_STRING = "\u0001\r\r\n" //
            + "013" + "\r\r\n"//
            + "FTYU31 YUDO 160000" + "\r\r\n" //
            + "TAF YUDO 160000Z NIL=\r\r\n" //
            + "TAF YUDD 160000Z NIL=" + "\r\r\n\u0003";
    private static final String MESSAGE_SHORT_STRING = "\r\r\n"//
            + "FTYU31 YUDO 160000" + "\r\r\n" //
            + "TAF YUDO 160000Z NIL=\r\r\n" //
            + "TAF YUDD 160000Z NIL=";
    private static final String MESSAGE_HEADING_AND_TEXT = "FTYU31 YUDO 160000" + "\r\r\n" //
            + "TAF YUDO 160000Z NIL=\r\r\n" //
            + "TAF YUDD 160000Z NIL=";
    private static final String MESSAGE_HEADING = "FTYU31 YUDO 160000";
    private static final String MESSAGE_TEXT = "TAF YUDO 160000Z NIL=\r\r\n" //
            + "TAF YUDD 160000Z NIL=";
    private static final String MESSAGE_SEQ_NUMBER_STRING = "013";
    private static final int MESSAGE_STANDARD_LENGTH = 80;
    private static final int MESSAGE_SHORT_LENGTH = 69;
    private static final GTSMeteorologicalMessage MESSAGE_STANDARD = GTSMeteorologicalMessage.builder()//
            .setTransmissionSequenceNumber(MESSAGE_SEQ_NUMBER_STRING)//
            .setHeading(MESSAGE_HEADING)//
            .setText(MESSAGE_TEXT)//
            .build();
    private static final GTSMeteorologicalMessage MESSAGE_SHORT = GTSMeteorologicalMessage.builder()//
            .clearTransmissionSequenceNumber()//
            .setHeading(MESSAGE_HEADING)//
            .setText(MESSAGE_TEXT)//
            .build();

    private static GTSMeteorologicalMessage.Builder builder() {
        return GTSMeteorologicalMessage.builder()//
                .setTransmissionSequenceNumber(INITIAL_SEQ_NUMBER_STRING)//
                .setHeading("INIT99 IALH 012345")//
                .setText("INITIAL TEXT");
    }

    private static void assertEqualProperties(final GTSMeteorologicalMessage actual, final GTSMeteorologicalMessage expected) {
        assertThat(actual).as("actual").isNotNull();
        assertThat(actual.getLength()).as("getLength").isEqualTo(expected.getLength());
        assertThat(actual.getTransmissionSequenceNumber()).as("getTransmissionSequenceNumber").isEqualTo(expected.getTransmissionSequenceNumber());
        assertThat(actual.getTransmissionSequenceNumberAsInt()).as("getTransmissionSequenceNumberAsInt")
                .isEqualTo(expected.getTransmissionSequenceNumberAsInt());
        assertThat(actual.getHeading()).as("getHeading").isEqualTo(expected.getHeading());
        assertThat(actual.getText()).as("getText").isEqualTo(expected.getText());
    }

    private static void assertEqualProperties(final GTSMeteorologicalMessage.Builder actual, final GTSMeteorologicalMessage expected) {
        assertThat(actual).as("actual").isNotNull();
        if (expected.getTransmissionSequenceNumber().isEmpty()) {
            assertInitialTransmissionSequenceNumber(actual);
        }
        assertThat(actual.getTransmissionSequenceNumber()).as("getTransmissionSequenceNumber").isEqualTo(expected.getTransmissionSequenceNumber());
        assertThat(actual.getTransmissionSequenceNumberAsInt()).as("getTransmissionSequenceNumberAsInt")
                .isEqualTo(expected.getTransmissionSequenceNumberAsInt());
        assertThat(actual.getHeading()).as("getHeading").isEqualTo(expected.getHeading());
        assertThat(actual.getText()).as("getText").isEqualTo(expected.getText());
    }

    private static void assertInitialTransmissionSequenceNumber(final GTSMeteorologicalMessage.Builder message) {
        assertThat(message.getTransmissionSequenceNumber()).as("getTransmissionSequenceNumber").isEqualTo(INITIAL_SEQ_NUMBER_STRING);
        assertThat(message.getTransmissionSequenceNumberAsInt()).as("getTransmissionSequenceNumberAsInt").hasValue(INITIAL_SEQ_NUMBER);
        message.clearTransmissionSequenceNumber();
    }

    private static void assertGTSExchangeFileParseException(final ErrorCode expectedErrorCode, final int expectedIndex, final String expectedFailedMessage,
            final GTSMeteorologicalMessageParseException exception) {
        assertThat(exception.getMessage()).as("getMessage").isEqualTo(expectedErrorCode.message(expectedIndex));
        assertThat(exception.getErrorCode()).as("getErrorCode").isEqualTo(expectedErrorCode);
        assertThat(exception.getIndex()).as("getIndex").isEqualTo(expectedIndex);
        assertThat(exception.getFailedMessage()).as("getFailedMessage").isEqualTo(expectedFailedMessage);
    }

    private static Stream<Set<MessageFormat>> messageFormatsContaining(final MessageFormat requiredFormat) {
        return Sets.powerSet(EnumSet.allOf(MessageFormat.class)).stream()//
                .filter(set -> set.contains(requiredFormat));
    }

    private static Stream<Set<MessageFormat>> messageFormatsWithout(final MessageFormat omittedFormat) {
        return Sets.powerSet(EnumSet.allOf(MessageFormat.class)).stream()//
                .filter(set -> !set.isEmpty() && !set.contains(omittedFormat));
    }

    @Test
    public void parseHeadingAndTextLenient_results_equal_to_short_message() {
        final GTSMeteorologicalMessage.Builder builder = builder().parseHeadingAndTextLenient(MESSAGE_HEADING_AND_TEXT);
        assertEqualProperties(builder, MESSAGE_SHORT);
        assertEqualProperties(builder.build(), MESSAGE_SHORT);
    }

    @Test
    public void parseHeadingAndTextLenient_parses_empty_string_to_empty_heading_and_text() {
        final GTSMeteorologicalMessage.Builder builder = builder().parseHeadingAndTextLenient("");
        assertThat(builder.getHeading()).as("getHeading").isEmpty();
        assertThat(builder.getText()).as("getText").isEmpty();
    }

    @Test
    public void parseHeadingAndTextLenient_interprets_any_combination_of_CR_and_LF_chars_as_heading_and_text_separator() {
        final Stream<String> inputMessages = Stream.of(//
                        GTSMeteorologicalMessage.TEXT_PREFIX, //
                        stringOf(CARRIAGE_RETURN), //
                        stringOf(LINE_FEED), //
                        stringOf(CARRIAGE_RETURN, LINE_FEED), //
                        stringOf(LINE_FEED, CARRIAGE_RETURN), //
                        stringOf(LINE_FEED, CARRIAGE_RETURN, CARRIAGE_RETURN, LINE_FEED, LINE_FEED, CARRIAGE_RETURN, CARRIAGE_RETURN, CARRIAGE_RETURN, LINE_FEED))//
                .map(separator -> MESSAGE_HEADING + separator + MESSAGE_TEXT);
        assertThat(inputMessages).allSatisfy(message -> {
            final GTSMeteorologicalMessage.Builder builder = builder().parseHeadingAndTextLenient(message);
            assertThat(builder.getHeading()).as("getHeading").isEqualTo(MESSAGE_HEADING);
            assertThat(builder.getText()).as("getText").isEqualTo(MESSAGE_TEXT);
        });
    }

    @Test
    public void parse_parses_standard_message() {
        final GTSMeteorologicalMessage.Builder builder = builder().parse(MESSAGE_STANDARD_STRING);
        assertEqualProperties(builder, MESSAGE_STANDARD);
        assertEqualProperties(builder.build(), MESSAGE_STANDARD);
    }

    @Test
    public void parse_STANDARD_parses_standard_message() {
        messageFormatsContaining(MessageFormat.STANDARD)//
                .forEach(acceptedFormats -> {
                    final GTSMeteorologicalMessage.Builder builder = builder().parse(MESSAGE_STANDARD_STRING, acceptedFormats);
                    assertEqualProperties(builder, MESSAGE_STANDARD);
                    assertEqualProperties(builder.build(), MESSAGE_STANDARD);
                });
    }

    @Test
    public void parse_not_STANDARD_fails_on_standard_message() {
        messageFormatsWithout(MessageFormat.STANDARD)//
                .forEach(acceptedFormats -> {
                    assertThatExceptionOfType(GTSMeteorologicalMessageParseException.class)//
                            .isThrownBy(() -> builder().parse(MESSAGE_STANDARD_STRING, acceptedFormats))//
                            .satisfies(e -> assertGTSExchangeFileParseException(ErrorCode.UNEXPECTED_STARTING_LINE_PREFIX, 0, MESSAGE_STANDARD_STRING, e));
                });
    }

    @Test
    public void parse_parses_short_message() {
        final GTSMeteorologicalMessage.Builder builder = builder().parse(MESSAGE_SHORT_STRING);
        assertEqualProperties(builder, MESSAGE_SHORT);
        assertEqualProperties(builder.build(), MESSAGE_SHORT);
    }

    @Test
    public void parse_SHORT_parses_short_message() {
        messageFormatsContaining(MessageFormat.SHORT)//
                .forEach(acceptedFormats -> {
                    final GTSMeteorologicalMessage.Builder builder = builder().parse(MESSAGE_SHORT_STRING, acceptedFormats);
                    assertEqualProperties(builder, MESSAGE_SHORT);
                    assertEqualProperties(builder.build(), MESSAGE_SHORT);
                });
    }

    @Test
    public void parse_not_SHORT_fails_on_short_message() {
        messageFormatsWithout(MessageFormat.SHORT)//
                .forEach(acceptedFormats -> {
                    final ErrorCode expectedErrorCode = acceptedFormats.contains(MessageFormat.HEADING_AND_TEXT)
                            ? ErrorCode.UNEXPECTED_HEADING_PREFIX
                            : ErrorCode.MISSING_STARTING_LINE_PREFIX;
                    assertThatExceptionOfType(GTSMeteorologicalMessageParseException.class)//
                            .isThrownBy(() -> builder().parse(MESSAGE_SHORT_STRING, acceptedFormats))//
                            .satisfies(e -> assertGTSExchangeFileParseException(expectedErrorCode, 0, MESSAGE_SHORT_STRING, e));
                });
    }

    @Test
    public void parse_parses_heading_and_text() {
        final GTSMeteorologicalMessage.Builder builder = builder().parse(MESSAGE_HEADING_AND_TEXT);
        assertEqualProperties(builder, MESSAGE_SHORT);
        assertEqualProperties(builder.build(), MESSAGE_SHORT);
    }

    @Test
    public void parse_HEADING_AND_TEXT_parses_heading_and_text() {
        messageFormatsContaining(MessageFormat.HEADING_AND_TEXT)//
                .forEach(acceptedFormats -> {
                    final GTSMeteorologicalMessage.Builder builder = builder().parse(MESSAGE_HEADING_AND_TEXT, acceptedFormats);
                    assertEqualProperties(builder, MESSAGE_SHORT);
                    assertEqualProperties(builder.build(), MESSAGE_SHORT);
                });
    }

    @Test
    public void parse_not_HEADING_AND_TEXT_fails_on_heading_and_text() {
        messageFormatsWithout(MessageFormat.HEADING_AND_TEXT)//
                .forEach(acceptedFormats -> {
                    final ErrorCode expectedErrorCode = acceptedFormats.contains(MessageFormat.STANDARD)
                            ? ErrorCode.MISSING_STARTING_LINE_PREFIX
                            : ErrorCode.MISSING_HEADING_PREFIX;
                    assertThatExceptionOfType(GTSMeteorologicalMessageParseException.class)//
                            .isThrownBy(() -> builder().parse(MESSAGE_HEADING_AND_TEXT, acceptedFormats))//
                            .satisfies(e -> assertGTSExchangeFileParseException(expectedErrorCode, 0, MESSAGE_HEADING_AND_TEXT, e));
                });
    }

    @Test
    public void parse_fails_when_heading_and_text_separator_not_found() {
        messageFormatsContaining(MessageFormat.HEADING_AND_TEXT)//
                .forEach(acceptedFormats -> {
                    final String inputMessage = MESSAGE_HEADING + "\r\nTAF YUDO 160000Z NIL=";
                    assertThatExceptionOfType(GTSMeteorologicalMessageParseException.class)//
                            .isThrownBy(() -> builder().parse(inputMessage, acceptedFormats))//
                            .satisfies(e -> assertGTSExchangeFileParseException(ErrorCode.NO_SEPARATION_OF_HEADING_AND_TEXT, 0, inputMessage, e));
                });
    }

    @Test
    public void parse_fails_when_standard_message_heading_and_text_heading_contains_line_break_characters() {
        messageFormatsContaining(MessageFormat.HEADING_AND_TEXT)//
                .forEach(acceptedFormats -> {
                    final Stream<String> inputMessages = Stream.of(//
                                    stringOf(CARRIAGE_RETURN), //
                                    stringOf(LINE_FEED), //
                                    stringOf(CARRIAGE_RETURN, LINE_FEED), //
                                    stringOf(LINE_FEED, CARRIAGE_RETURN), //
                                    stringOf(LINE_FEED, CARRIAGE_RETURN, LINE_FEED, LINE_FEED, CARRIAGE_RETURN, LINE_FEED))// Contains no CR+CR+LF sequence
                            .map(separator -> MESSAGE_HEADING + separator + MESSAGE_TEXT);
                    assertThat(inputMessages)//
                            .allSatisfy(inputMessage -> assertThatExceptionOfType(GTSMeteorologicalMessageParseException.class)//
                                    .isThrownBy(() -> builder().parse(inputMessage, acceptedFormats))//
                                    .satisfies(e -> assertGTSExchangeFileParseException(ErrorCode.UNEXPECTED_LINE_BREAKS_IN_HEADING, MESSAGE_HEADING.length(),
                                            inputMessage, e)));
                });
    }

    @Test
    public void parse_fails_when_standard_message_transmission_sequence_number_contains_line_break_chars() {
        messageFormatsContaining(MessageFormat.STANDARD)//
                .forEach(acceptedFormats -> {
                    final String inputMessage = "\u0001\r\r\n" //
                            + "013\r" + "\r\r\n"//
                            + "FTYU31 YUDO 160000" + "\r\r\n" //
                            + "TAF YUDO 160000Z NIL=\r\r\n" //
                            + "TAF YUDD 160000Z NIL=" + "\r\r\n\u0003";
                    assertThatExceptionOfType(GTSMeteorologicalMessageParseException.class)//
                            .isThrownBy(() -> builder().parse(inputMessage, acceptedFormats))//
                            .satisfies(
                                    e -> assertGTSExchangeFileParseException(ErrorCode.UNEXPECTED_LINE_BREAKS_IN_TRANSMISSION_SEQUENCE_NUMBER, 7, inputMessage,
                                            e));
                });
    }

    @Test
    public void parse_fails_when_standard_message_does_not_end_with_expected_end_signals() {
        messageFormatsContaining(MessageFormat.STANDARD)//
                .forEach(acceptedFormats -> {
                    final String inputMessage = "\u0001\r\r\n" //
                            + "013" + "\r\r\n"//
                            + "FTYU31 YUDO 160000" + "\r\r\n" //
                            + "TAF YUDO 160000Z NIL=\r\r\n" //
                            + "TAF YUDD 160000Z NIL=" + "\r\n\u0003";
                    assertThatExceptionOfType(GTSMeteorologicalMessageParseException.class)//
                            .isThrownBy(() -> builder().parse(inputMessage))//
                            .satisfies(e -> assertGTSExchangeFileParseException(ErrorCode.MISSING_END_OF_MESSAGE_SIGNALS, 75, inputMessage, e));
                });
    }

    @Test
    public void parse_fails_when_standard_message_heading_contains_line_break_characters() {
        messageFormatsContaining(MessageFormat.STANDARD)//
                .forEach(acceptedFormats -> {
                    final Stream<String> inputMessages = Stream.of(//
                                    stringOf(CARRIAGE_RETURN), //
                                    stringOf(LINE_FEED), //
                                    stringOf(CARRIAGE_RETURN, LINE_FEED), //
                                    stringOf(LINE_FEED, CARRIAGE_RETURN), //
                                    stringOf(LINE_FEED, CARRIAGE_RETURN, LINE_FEED, LINE_FEED, CARRIAGE_RETURN, LINE_FEED))// Contains no CR+CR+LF sequence
                            .map(separator -> "\u0001\r\r\n" //
                                    + "013" + "\r\r\n"//
                                    + "FTYU31 YUDO 160000" + separator //
                                    + "TAF YUDO 160000Z NIL=" + "\r\r\n" //
                                    + "TAF YUDD 160000Z NIL=" + "\r\r\n\u0003");
                    assertThat(inputMessages)//
                            .allSatisfy(inputMessage -> assertThatExceptionOfType(GTSMeteorologicalMessageParseException.class)//
                                    .isThrownBy(() -> builder().parse(inputMessage, acceptedFormats))//
                                    .satisfies(e -> assertGTSExchangeFileParseException(ErrorCode.UNEXPECTED_LINE_BREAKS_IN_HEADING, 28, inputMessage, e)));
                });
    }

    @Test
    public void parse_fails_when_short_message_heading_contains_line_break_characters() {
        messageFormatsContaining(MessageFormat.SHORT)//
                .forEach(acceptedFormats -> {
                    final Stream<String> inputMessages = Stream.of(//
                                    stringOf(CARRIAGE_RETURN), //
                                    stringOf(LINE_FEED), //
                                    stringOf(CARRIAGE_RETURN, LINE_FEED), //
                                    stringOf(LINE_FEED, CARRIAGE_RETURN), //
                                    stringOf(LINE_FEED, CARRIAGE_RETURN, LINE_FEED, LINE_FEED, CARRIAGE_RETURN, LINE_FEED))// Contains no CR+CR+LF sequence
                            .map(separator -> "\r\r\n"//
                                    + "FTYU31 YUDO 160000" + separator //
                                    + "TAF YUDO 160000Z NIL=" + "\r\r\n" //
                                    + "TAF YUDD 160000Z NIL=");
                    assertThat(inputMessages)//
                            .allSatisfy(inputMessage -> assertThatExceptionOfType(GTSMeteorologicalMessageParseException.class)//
                                    .isThrownBy(() -> builder().parse(inputMessage, acceptedFormats))//
                                    .satisfies(e -> assertGTSExchangeFileParseException(ErrorCode.UNEXPECTED_LINE_BREAKS_IN_HEADING, 21, inputMessage, e)));
                });
    }

    @Test
    public void parse_fails_on_short_message_when_heading_is_not_preceded_by_expected_prefix() {
        messageFormatsContaining(MessageFormat.SHORT)//
                .forEach(acceptedFormats -> {
                    final String inputMessage = "\r\r" //
                            + "FTYU31 YUDO 160000" + "\r\r\n" //
                            + "TAF YUDO 160000Z NIL=\r\r\n" //
                            + "TAF YUDD 160000Z NIL=";
                    final ErrorCode expectedErrorCode;
                    if (acceptedFormats.contains(MessageFormat.HEADING_AND_TEXT)) {
                        expectedErrorCode = ErrorCode.UNEXPECTED_LINE_BREAKS_IN_HEADING;
                    } else if (acceptedFormats.contains(MessageFormat.STANDARD)) {
                        expectedErrorCode = ErrorCode.MISSING_STARTING_LINE_PREFIX;
                    } else {
                        expectedErrorCode = ErrorCode.MISSING_HEADING_PREFIX;
                    }
                    assertThatExceptionOfType(GTSMeteorologicalMessageParseException.class)//
                            .isThrownBy(() -> builder().parse(inputMessage, acceptedFormats))//
                            .satisfies(e -> assertGTSExchangeFileParseException(expectedErrorCode, 0, inputMessage, e));
                });
    }

    @Test
    public void parse_fails_on_short_message_when_heading_and_text_separator_not_found() {
        messageFormatsContaining(MessageFormat.SHORT)//
                .forEach(acceptedFormats -> {
                    final String inputMessage = "\r\r\n"//
                            + "FTYU31 YUDO 160000 " //
                            + "TAF YUDD 160000Z NIL=";
                    assertThatExceptionOfType(GTSMeteorologicalMessageParseException.class)//
                            .isThrownBy(() -> builder().parse(inputMessage, acceptedFormats))//
                            .satisfies(e -> assertGTSExchangeFileParseException(ErrorCode.NO_SEPARATION_OF_HEADING_AND_TEXT, 3, inputMessage, e));
                });
    }

    @Test
    public void parse_fails_on_short_message_when_actual_content_is_smaller_than_given_message_length() {
        messageFormatsContaining(MessageFormat.SHORT)//
                .forEach(acceptedFormats -> {
                    final String inputMessage = "\r\r\n"//
                            + "FTYU31 YUDO 160000" + "\r\r\n" //
                            + "TAF YUDO 160000Z NIL=" + "\r\r\n" //
                            + "TAF YUDD 160000Z NIL=";
                    assertThatExceptionOfType(GTSMeteorologicalMessageParseException.class)//
                            .isThrownBy(() -> builder().parse(inputMessage, 0, 100, acceptedFormats))//
                            .satisfies(e -> assertGTSExchangeFileParseException(ErrorCode.UNEXPECTED_END_OF_TEXT, 24, inputMessage, e));
                });
    }

    @Test
    public void transmissionSequenceNumber_is_empty_by_default() {
        final GTSMeteorologicalMessage.Builder builder = GTSMeteorologicalMessage.builder();
        assertThat(builder.getTransmissionSequenceNumber()).isEmpty();
    }

    @Test
    public void clearTransmissionSequenceNumber_sets_value_to_empty_string() {
        final GTSMeteorologicalMessage.Builder builder = builder()//
                .setTransmissionSequenceNumber("123");
        assertThat(builder.getTransmissionSequenceNumber()).isEqualTo("123");
        builder.clearTransmissionSequenceNumber();
        assertThat(builder.getTransmissionSequenceNumber()).isEmpty();
    }

    @Test
    public void getTransmissionSequenceNumberAsInt_returns_empty_when_getTransmissionSequenceNumber_is_empty() {
        final GTSMeteorologicalMessage.Builder builder = builder()//
                .clearTransmissionSequenceNumber();
        final GTSMeteorologicalMessage value = builder.buildPartial();
        assertThat(builder.getTransmissionSequenceNumber()).as("builder.getTransmissionSequenceNumber()").isEmpty();
        assertThat(builder.getTransmissionSequenceNumberAsInt()).as("builder.getTransmissionSequenceNumberAsInt()").isEmpty();
        assertThat(value.getTransmissionSequenceNumberAsInt()).as("value.getTransmissionSequenceNumberAsInt()").isEmpty();
    }

    @Test
    public void getTransmissionSequenceNumberAsInt_returns_empty_when_getTransmissionSequenceNumber_is_text() {
        final GTSMeteorologicalMessage.Builder builder = builder()//
                .setTransmissionSequenceNumber("ABC");
        final GTSMeteorologicalMessage value = builder.buildPartial();
        assertThat(builder.getTransmissionSequenceNumber()).as("builder.getTransmissionSequenceNumber()").isEqualTo("ABC");
        assertThat(builder.getTransmissionSequenceNumberAsInt()).as("builder.getTransmissionSequenceNumberAsInt()").isEmpty();
        assertThat(value.getTransmissionSequenceNumberAsInt()).as("value.getTransmissionSequenceNumberAsInt()").isEmpty();
    }

    @Test
    public void getTransmissionSequenceNumberAsInt_returns_int_when_getTransmissionSequenceNumber_is_numeric() {
        final GTSMeteorologicalMessage.Builder builder = builder()//
                .setTransmissionSequenceNumber("00073");
        final GTSMeteorologicalMessage value = builder.buildPartial();
        assertThat(builder.getTransmissionSequenceNumber()).as("builder.getTransmissionSequenceNumber()").isEqualTo("00073");
        assertThat(builder.getTransmissionSequenceNumberAsInt()).as("builder.getTransmissionSequenceNumberAsInt()").hasValue(73);
        assertThat(value.getTransmissionSequenceNumberAsInt()).as("value.getTransmissionSequenceNumberAsInt()").hasValue(73);
    }

    @Test
    public void setTransmissionSequenceNumberAsInt_sets_value_with_three_digits() {
        final GTSMeteorologicalMessage.Builder builder = builder()//
                .setTransmissionSequenceNumberAsInt(32);
        final GTSMeteorologicalMessage value = builder.buildPartial();
        assertThat(builder.getTransmissionSequenceNumberAsInt()).as("builder.getTransmissionSequenceNumberAsInt()").hasValue(32);
        assertThat(builder.getTransmissionSequenceNumber()).as("builder.getTransmissionSequenceNumber()").isEqualTo("032");
        assertThat(value.getTransmissionSequenceNumberAsInt()).as("value.getTransmissionSequenceNumberAsInt()").hasValue(32);
        assertThat(value.getTransmissionSequenceNumber()).as("value.getTransmissionSequenceNumber()").isEqualTo("032");
    }

    @Test
    public void getLength_given_STANDARD_returns_standard_format_length() {
        assertThat(MESSAGE_STANDARD.getLength(MessageFormat.STANDARD)).isEqualTo(MESSAGE_STANDARD_LENGTH);
    }

    @Test
    public void getLength_given_STANDARD_on_message_without_sequence_number_fails() {
        assertThatIllegalArgumentException()//
                .isThrownBy(() -> MESSAGE_SHORT.getLength(MessageFormat.STANDARD))//
                .withMessageContaining(MessageFormat.STANDARD.name());
    }

    @Test
    public void getLength_given_SHORT_returns_short_format_length() {
        assertThat(MESSAGE_STANDARD.getLength(MessageFormat.SHORT)).isEqualTo(MESSAGE_SHORT_LENGTH);
    }

    @Test
    public void getLength_given_HEADING_AND_TEXT_returns_length_of_heading_and_text() {
        assertThat(MESSAGE_STANDARD.getLength(MessageFormat.HEADING_AND_TEXT)).isEqualTo(MESSAGE_HEADING_AND_TEXT.length());
    }

    @Test
    public void supports_given_STANDARD_returns_true_when_sequence_number_exists() {
        assertThat(MESSAGE_STANDARD.supports(MessageFormat.STANDARD)).isTrue();
    }

    @Test
    public void supports_given_STANDARD_returns_false_when_sequence_number_is_missing() {
        assertThat(MESSAGE_SHORT.supports(MessageFormat.STANDARD)).isFalse();
    }

    @Test
    public void supports_given_SHORT_returns_always_true() {
        Stream.of(MESSAGE_STANDARD, MESSAGE_SHORT)//
                .forEach(message -> assertThat(message.supports(MessageFormat.SHORT)).isTrue());
    }

    @Test
    public void supports_given_HEADING_AND_TEXT_returns_always_true() {
        Stream.of(MESSAGE_STANDARD, MESSAGE_SHORT)//
                .forEach(message -> assertThat(message.supports(MessageFormat.HEADING_AND_TEXT)).isTrue());
    }

    @Test
    public void toString_returns_string_equal_to_original_standard_message() {
        final GTSMeteorologicalMessage message = GTSMeteorologicalMessage.parse(MESSAGE_STANDARD_STRING);
        assertThat(message.toString()).isEqualTo(MESSAGE_STANDARD_STRING);
    }

    @Test
    public void toString_returns_string_equal_to_original_short_message() {
        final GTSMeteorologicalMessage message = GTSMeteorologicalMessage.parse(MESSAGE_SHORT_STRING);
        assertThat(message.toString()).isEqualTo(MESSAGE_SHORT_STRING);
    }

    @Test
    public void toString_STANDARD_returns_string_equal_to_standard_message() {
        final GTSMeteorologicalMessage message = GTSMeteorologicalMessage.parse(MESSAGE_STANDARD_STRING);
        assertThat(message.toString(MessageFormat.STANDARD)).isEqualTo(MESSAGE_STANDARD_STRING);
    }

    @Test
    public void toString_STANDARD_fails_without_transmissionSequenceNumber() {
        final GTSMeteorologicalMessage message = GTSMeteorologicalMessage.parse(MESSAGE_SHORT_STRING);
        assertThatIllegalArgumentException()//
                .isThrownBy(() -> message.toString(MessageFormat.STANDARD))//
                .withMessageContaining(MessageFormat.STANDARD.name());
    }

    @Test
    public void toString_SHORT_returns_string_equal_to_short_message() {
        final GTSMeteorologicalMessage message = GTSMeteorologicalMessage.parse(MESSAGE_STANDARD_STRING);
        assertThat(message.toString(MessageFormat.SHORT)).isEqualTo(MESSAGE_SHORT_STRING);
    }

    @Test
    public void toString_HEADING_AND_TEXT_returns_string_equal_to_heading_and_text() {
        final GTSMeteorologicalMessage message = GTSMeteorologicalMessage.parse(MESSAGE_STANDARD_STRING);
        assertThat(message.toString(MessageFormat.HEADING_AND_TEXT)).isEqualTo(MESSAGE_HEADING_AND_TEXT);
    }
}
