package fi.fmi.avi.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.assertj.core.description.Description;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;
import com.google.common.testing.SerializableTester;

import fi.fmi.avi.model.PartialDateTime.PartialField;
import fi.fmi.avi.model.PartialDateTime.ReferenceCondition;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public final class PartialDateTimeTest {
    private static final int TEST_DAY = 2;
    private static final int TEST_HOUR = 3;
    private static final int TEST_MINUTE = 4;
    private static final Map<PartialField, Integer> TEST_FIELD_VALUES = createTestFieldValues();
    private static final ZoneId TEST_ZONE = ZoneId.of("Z");
    private static final PartialDateTime SAMPLE_INSTANCE = PartialDateTime.parse("--02T03:04Z");
    private static final ZonedDateTime SAMPLE_ZONED_DATE_TIME = ZonedDateTime.parse("2000-01-02T03:04Z");
    private static final ZonedDateTime MIDNIGHT_ZONED_DATE_TIME = SAMPLE_ZONED_DATE_TIME.with(LocalTime.MIDNIGHT);
    private static final PartialDateTime EMPTY_INSTANCE = PartialDateTime.of(-1, -1, -1, null);

    private static Map<PartialField, Integer> createTestFieldValues() {
        final EnumMap<PartialField, Integer> map = new EnumMap<>(PartialField.class);
        map.put(PartialField.DAY, TEST_DAY);
        map.put(PartialField.HOUR, TEST_HOUR);
        map.put(PartialField.MINUTE, TEST_MINUTE);

        assertThat(map.keySet()).containsExactlyInAnyOrder(PartialField.values());

        return Maps.immutableEnumMap(map);
    }

    private static PartialDateTime createPartialDateTime(final Map<PartialField, Integer> testFieldValues) {
        return PartialDateTime.of(testFieldValues.keySet(), testFieldValues.values().stream().mapToInt(Integer::intValue).toArray());
    }

    private static PartialDateTime createPartialDateTimeViaTACString(final Map<PartialField, Integer> testFieldValues) {
        PartialField precision = PartialField.MINUTE;
        final StringBuilder tacStringBuilder = new StringBuilder();
        for (final Map.Entry<PartialField, Integer> entry : testFieldValues.entrySet()) {
            precision = entry.getKey();
            tacStringBuilder.append(String.format("%02d", entry.getValue()));
        }

        return PartialDateTime.parseTACString(tacStringBuilder.toString(), precision);
    }

    private static PartialDateTime createPartialDateTime(final int day, final int hour, final int minute, final String zoneId) {
        return PartialDateTime.of(day, hour, minute, zoneId.isEmpty() ? null : ZoneId.of(zoneId));
    }

    private static EnumSet<PartialField> complementOf(final Collection<PartialField> source) {
        if (source instanceof EnumSet) {
            return EnumSet.complementOf((EnumSet<PartialField>) source);
        }
        final EnumSet<PartialField> complement = EnumSet.allOf(PartialField.class);
        complement.removeIf(source::contains);
        return complement;
    }

    private static EnumSet<PartialField> existingFields(final int day, final int hour, final int minute) {
        final EnumSet<PartialField> existingFields = EnumSet.noneOf(PartialField.class);
        if (day >= 0) {
            existingFields.add(PartialField.DAY);
        }
        if (hour >= 0) {
            existingFields.add(PartialField.HOUR);
        }
        if (minute >= 0) {
            existingFields.add(PartialField.MINUTE);
        }
        return existingFields;
    }

    private static Optional<ZoneId> createZone(final String zoneId) {
        return zoneId.isEmpty() ? Optional.empty() : Optional.of(ZoneId.of(zoneId));
    }

    private static PartialField precision(final int day, final int hour, final int minute) {
        if (minute >= 0) {
            return PartialField.MINUTE;
        } else if (hour >= 0) {
            return PartialField.HOUR;
        } else if (day >= 0) {
            return PartialField.DAY;
        } else {
            return PartialField.MINUTE;
        }
    }

    @Test
    public void testNulls() {
        final NullPointerTester tester = new NullPointerTester()//
                .setDefault(ZonedDateTime.class, ZonedDateTime.ofInstant(Instant.EPOCH, TEST_ZONE))//
                .setDefault(ZoneId.class, TEST_ZONE);

        tester.testAllPublicConstructors(PartialDateTime.class);
        tester.testAllPublicStaticMethods(PartialDateTime.class);
        tester.testAllPublicInstanceMethods(SAMPLE_INSTANCE);

        tester.testAllPublicStaticMethods(PartialField.class);
        for (final PartialField field : PartialField.values()) {
            tester.testAllPublicInstanceMethods(field);
        }
    }

    @Test
    public void testEquals() {
        new EqualsTester()//
                .addEqualityGroup(SAMPLE_INSTANCE, SAMPLE_INSTANCE, PartialDateTime.parse(SAMPLE_INSTANCE.toString()))//
                .addEqualityGroup(PartialDateTime.parse("--T::"))//
                .addEqualityGroup(PartialDateTime.parse("--00T::"))//
                .addEqualityGroup(PartialDateTime.parse("--T00::"))//
                .addEqualityGroup(PartialDateTime.parse("--T:00:"))//
                .addEqualityGroup(PartialDateTime.parse("--00T00::"))//
                .addEqualityGroup(PartialDateTime.parse("--T00:00:"))//
                .addEqualityGroup(PartialDateTime.parse("--00T00:00:"))//
                .addEqualityGroup(PartialDateTime.parse("--T::Z"))//
                .addEqualityGroup(PartialDateTime.parse("--00T::Z"))//
                .addEqualityGroup(PartialDateTime.parse("--T00::Z"))//
                .addEqualityGroup(PartialDateTime.parse("--T:00:Z"))//
                .addEqualityGroup(PartialDateTime.parse("--00T00::Z"))//
                .addEqualityGroup(PartialDateTime.parse("--T00:00:Z"))//
                .addEqualityGroup(PartialDateTime.parse("--00T00:00:Z"))//

                .addEqualityGroup(PartialDateTime.parse("--01T::"))//
                .addEqualityGroup(PartialDateTime.parse("--T01::"))//
                .addEqualityGroup(PartialDateTime.parse("--T:01:"))//
                .addEqualityGroup(PartialDateTime.parse("--01T01::"))//
                .addEqualityGroup(PartialDateTime.parse("--T01:01:"))//
                .addEqualityGroup(PartialDateTime.parse("--01T01:01:"))//
                .addEqualityGroup(PartialDateTime.parse("--01T::Z"))//
                .addEqualityGroup(PartialDateTime.parse("--T01::Z"))//
                .addEqualityGroup(PartialDateTime.parse("--T:01:Z"))//
                .addEqualityGroup(PartialDateTime.parse("--01T01::Z"))//
                .addEqualityGroup(PartialDateTime.parse("--T01:01:Z"))//
                .addEqualityGroup(PartialDateTime.parse("--01T01:01:Z"))//
                .testEquals();
    }

    @Test
    public void testSerializable() {
        SerializableTester.reserializeAndAssert(SAMPLE_INSTANCE);
    }

    @Parameters
    @Test
    public void testBuildFailOnNonContinuousFields(final Map<PartialField, Integer> testFieldValues) {
        final Class<DateTimeException> expectedException = DateTimeException.class;
        final ExpectedExceptionDescription description = new ExpectedExceptionDescription(expectedException);
        assertThatExceptionOfType(expectedException).as(description)//
                .isThrownBy(() -> description.setUnexpectedResult(createPartialDateTime(testFieldValues)))//
                .withMessageContaining(testFieldValues.keySet().toString());
    }

    public List<Map<PartialField, Integer>> parametersForTestBuildFailOnNonContinuousFields() {
        return Sets.powerSet(TEST_FIELD_VALUES.keySet()).stream()//
                .map(set -> Maps.immutableEnumMap(Maps.asMap(set, TEST_FIELD_VALUES::get)))//
                .filter(map -> !PartialDateTime.hasContinuousEnums(map.keySet()))//
                .collect(Collectors.toList());
    }

    @Parameters(source = TestFieldValuesProvider.class)
    @Test
    public void testGetPresentFields(final Map<PartialField, Integer> testFieldValues) {
        final PartialDateTime partialDateTime = createPartialDateTime(testFieldValues);

        assertThat(partialDateTime.getPresentFields())//
                .as("expected to contain fields that were set")//
                .containsExactlyInAnyOrderElementsOf(testFieldValues.keySet());
    }

    @Parameters(source = TestFieldValuesProvider.class)
    @Test
    public void testGet(final Map<PartialField, Integer> testFieldValues) {
        final PartialDateTime partialDateTime = createPartialDateTime(testFieldValues);

        assertThat(testFieldValues)//
                .allSatisfy((field, value) -> assertThat(partialDateTime.get(field))//
                        .as("expect existing field %s", field)//
                        .hasValue(value));
        assertThat(complementOf(testFieldValues.keySet()))//
                .allSatisfy(field -> assertThat(partialDateTime.get(field))//
                        .as("expect nonexistent field %s", field)//
                        .isNotPresent());
    }

    @Parameters(source = TestFieldValuesProvider.class)
    @Test
    public void testWith(final Map<PartialField, Integer> testFieldValues) {
        PartialDateTime partialDateTime = EMPTY_INSTANCE;
        for (final Map.Entry<PartialField, Integer> entry : testFieldValues.entrySet()) {
            partialDateTime = partialDateTime.with(entry.getKey(), entry.getValue());
        }
        final PartialDateTime expected = createPartialDateTime(testFieldValues);
        assertThat(partialDateTime).isEqualTo(expected);
        for (final Map.Entry<PartialField, Integer> entry : testFieldValues.entrySet()) {
            partialDateTime = partialDateTime.with(entry.getKey(), entry.getValue());
        }
        assertThat(partialDateTime).isEqualTo(expected);
    }

    @Parameters
    @Test
    public void testWithout(final Map<PartialField, Integer> testFieldValues) {
        final PartialDateTime partialDateTime = createPartialDateTime(testFieldValues);

        for (final PartialField field : partialDateTime.getPresentFields()) {
            final PartialDateTime withFieldCleared = partialDateTime.without(field);
            final EnumSet<PartialField> expectedFields = EnumSet.copyOf(partialDateTime.getPresentFields());
            expectedFields.remove(field);

            assertThat(withFieldCleared.get(field)).as(field.toString()).isNotPresent();
            assertThat(withFieldCleared.getPresentFields()).as(field.toString()).containsExactlyInAnyOrderElementsOf(expectedFields);
        }
    }

    public List<Map<PartialField, Integer>> parametersForTestWithout() {
        // Use only two fields to avoid uncontinuous fields error on without (e.g. without middle field)
        return Sets.combinations(TEST_FIELD_VALUES.keySet(), 2).stream()//
                .map(set -> Maps.immutableEnumMap(Maps.asMap(set, TEST_FIELD_VALUES::get)))//
                .filter(map -> PartialDateTime.hasContinuousEnums(map.keySet()))//
                .collect(Collectors.toList());
    }

    @Test
    public void testGetMinuteEmpty() {
        assertThat(EMPTY_INSTANCE.getMinute()).isNotPresent();
    }

    @Test
    public void testGetMinute() {
        final PartialField field = PartialField.MINUTE;
        final int value = TEST_FIELD_VALUES.get(field);
        final PartialDateTime partialDateTime = EMPTY_INSTANCE.with(field, value);
        assertThat(partialDateTime.getMinute()).hasValue(value);
    }

    @Test
    public void testWithMinute() {
        final PartialField field = PartialField.MINUTE;
        final int value = TEST_FIELD_VALUES.get(field);
        final PartialDateTime partialDateTime = EMPTY_INSTANCE.withMinute(value);
        assertThat(partialDateTime.get(field)).hasValue(value);
        assertThat(partialDateTime.withMinute(value)).isEqualTo(partialDateTime);
    }

    @Test
    public void testWithoutMinute() {
        final PartialField field = PartialField.MINUTE;
        assertThat(SAMPLE_INSTANCE.withoutMinute().get(field)).as(field.toString()).isNotPresent();
    }

    @Test
    public void testGetHourEmpty() {
        assertThat(EMPTY_INSTANCE.getHour()).isNotPresent();
    }

    @Test
    public void testGetHour() {
        final PartialField field = PartialField.HOUR;
        final int value = TEST_FIELD_VALUES.get(field);
        final PartialDateTime partialDateTime = EMPTY_INSTANCE.with(field, value);
        assertThat(partialDateTime.getHour()).hasValue(value);
    }

    @Test
    public void testWithHour() {
        final PartialField field = PartialField.HOUR;
        final int value = TEST_FIELD_VALUES.get(field);
        final PartialDateTime partialDateTime = EMPTY_INSTANCE.withHour(value);
        assertThat(partialDateTime.get(field)).hasValue(value);
        assertThat(partialDateTime.withHour(value)).isEqualTo(partialDateTime);
    }

    @Test
    public void testWithoutHour() {
        final PartialField field = PartialField.HOUR;
        final PartialDateTime partialDateTime = SAMPLE_INSTANCE.without(PartialField.DAY);
        assertThat(partialDateTime.withoutHour().get(field)).as(field.toString()).isNotPresent();
    }

    @Test
    public void testGetDayEmpty() {
        assertThat(EMPTY_INSTANCE.getDay()).isNotPresent();
    }

    @Test
    public void testGetDay() {
        final PartialField field = PartialField.DAY;
        final int value = TEST_FIELD_VALUES.get(field);
        final PartialDateTime partialDateTime = EMPTY_INSTANCE.with(field, value);
        assertThat(partialDateTime.getDay()).hasValue(value);
    }

    @Test
    public void testWithDay() {
        final PartialField field = PartialField.DAY;
        final int value = TEST_FIELD_VALUES.get(field);
        final PartialDateTime partialDateTime = EMPTY_INSTANCE.withDay(value);
        assertThat(partialDateTime.get(field)).hasValue(value);
        assertThat(partialDateTime.withDay(value)).isEqualTo(partialDateTime);
    }

    @Test
    public void testWithoutDay() {
        final PartialField field = PartialField.DAY;
        assertThat(SAMPLE_INSTANCE.withoutDay().get(field)).as(field.toString()).isNotPresent();
    }

    @Test
    public void testGetZoneEmpty() {
        assertThat(EMPTY_INSTANCE.getZone()).isNotPresent();
    }

    @Test
    public void testGetZone() {
        final ZoneId zone = ZoneOffset.UTC;
        final PartialDateTime partialDateTime = EMPTY_INSTANCE.withZone(zone);
        assertThat(partialDateTime.getZone()).hasValue(zone);
    }

    @Test
    public void testWithoutZone() {
        assertThat(SAMPLE_INSTANCE.withoutZone().getZone()).isNotPresent();
    }

    @Parameters({ //
            "true, --T24:0", "true, --T24:", "true, --T0:0", "true, --T0:", //
            "false, --T:", "false, --T23:0", "false, --T23:", "false, --T24:1", "false, --T:0", "false, --T25:0", "false, --T25:" //
    })
    @Test
    public void testIsMidnight(final boolean expectedResult, final String partialDateTimeString) {
        final PartialDateTime partialDateTime = PartialDateTime.parse(partialDateTimeString);
        assertThat(partialDateTime.isMidnight()).as(partialDateTime.toString()).isEqualTo(expectedResult);
    }

    @Parameters({ //
            "true, --T24:0", "true, --T24:", //
            "false, --T0:0", "false, --T0:", "false, --T23:0", "false, --T23:", "false, --T24:1", "false, --T:0", "false, --T25:0", "false, --T25:" //
    })
    @Test
    public void testIsMidnight24h(final boolean expectedResult, final String partialDateTimeString) {
        final PartialDateTime partialDateTime = PartialDateTime.parse(partialDateTimeString);
        assertThat(partialDateTime.isMidnight24h()).as(partialDateTime.toString()).isEqualTo(expectedResult);
    }

    @Parameters({ //
            "--T00:, --T00:, 2000-01", //
            "--T06:, --T06:, 2000-01", //
            "--T00:, --T24:, 2000-01", //
            "--02T00:, --02T00:, 2000-01", //
            "--02T06:, --02T06:, 2000-01", //
            "--03T00:, --02T24:, 2000-01", //
            "--01T00:, --31T24:, 2000-01", //
            "--29T00:, --28T24:, 2000-02", //
            "--01T00:, --28T24:, 2001-02", //
    })
    @Test
    public void testWithMidnight00h(final String expected, final String partialDateTime, final String reference) {
        testWithMidnight00h(PartialDateTime.parse(expected), PartialDateTime.parse(partialDateTime), YearMonth.parse(reference));
    }

    private void testWithMidnight00h(final PartialDateTime expected, final PartialDateTime partialDateTime, final YearMonth reference) {
        assertThat(partialDateTime.withMidnight00h(reference)).as("%s.withMidnight00h(%s)", partialDateTime, reference).isEqualTo(expected);
    }

    @Parameters({ //
            "--T24:, --T24:, 2000-01", //
            "--T06:, --T06:, 2000-01", //
            "--T24:, --T00:, 2000-01", //
            "--02T24:, --02T24:, 2000-01", //
            "--02T06:, --02T06:, 2000-01", //
            "--02T24:, --03T00:, 2000-01", //
            "--31T24:, --01T00:, 2000-01", //
            "--29T24:, --01T00:, 2000-03", //
            "--28T24:, --01T00:, 2001-03", //
    })
    @Test
    public void testWithMidnight24h(final String expected, final String partialDateTime, final String reference) {
        testWithMidnight24h(PartialDateTime.parse(expected), PartialDateTime.parse(partialDateTime), YearMonth.parse(reference));
    }

    private void testWithMidnight24h(final PartialDateTime expected, final PartialDateTime partialDateTime, final YearMonth reference) {
        assertThat(partialDateTime.withMidnight24h(reference)).as("%s.withMidnight24h(%s)", partialDateTime, reference).isEqualTo(expected);
    }

    @Parameters
    @Test
    public void testRepresentsStrictly(final boolean expected, final PartialDateTime partialDateTime, final Temporal temporal) {
        assertThat(partialDateTime.representsStrict(temporal)).as("%s.representsStrict(%s)", partialDateTime, temporal).isEqualTo(expected);
    }

    public List<Object[]> parametersForTestRepresentsStrictly() {
        final YearMonth yearMonth = YearMonth.of(2000, 2);
        final LocalDate localDate = LocalDate.of(2000, 2, 3);
        final LocalTime localTime = LocalTime.of(4, 5, 6, 7);
        final LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        final ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, TEST_ZONE);
        return Stream.of(//
                parametersForTestRepresents(true, "--T:", yearMonth, localDate, localTime, localDateTime, zonedDateTime), //
                parametersForTestRepresents(true, "--03T:", localDate, localDateTime, zonedDateTime), //
                parametersForTestRepresents(false, "--03T:", yearMonth, localTime), //
                parametersForTestRepresents(true, "--T04:", localTime, localDateTime, zonedDateTime), //
                parametersForTestRepresents(false, "--T04:", yearMonth, localDate), //
                parametersForTestRepresents(true, "--T:05", localTime, localDateTime, zonedDateTime), //
                parametersForTestRepresents(false, "--T:05", yearMonth, localDate), //
                parametersForTestRepresents(true, "--03T04:", localDateTime, zonedDateTime), //
                parametersForTestRepresents(false, "--03T04:", yearMonth, localDate, localTime), //
                parametersForTestRepresents(true, "--T04:05", localTime, localDateTime, zonedDateTime), //
                parametersForTestRepresents(false, "--T04:05", yearMonth, localDate), //
                parametersForTestRepresents(true, "--03T04:05", localDateTime, zonedDateTime), //
                parametersForTestRepresents(false, "--03T04:05", yearMonth, localDate, localTime), //

                parametersForTestRepresents(true, "--T:Z", zonedDateTime), //
                parametersForTestRepresents(false, "--T:Z", yearMonth, localDate, localTime, localDateTime), //
                parametersForTestRepresents(true, "--03T:Z", zonedDateTime), //
                parametersForTestRepresents(false, "--03T:Z", yearMonth, localDate, localTime, localDateTime), //
                parametersForTestRepresents(true, "--T04:Z", zonedDateTime), //
                parametersForTestRepresents(false, "--T04:Z", yearMonth, localDate, localTime, localDateTime), //
                parametersForTestRepresents(true, "--T:05Z", zonedDateTime), //
                parametersForTestRepresents(false, "--T:05Z", yearMonth, localDate, localTime, localDateTime), //
                parametersForTestRepresents(true, "--03T04:Z", zonedDateTime), //
                parametersForTestRepresents(false, "--03T04:Z", yearMonth, localDate, localTime, localDateTime), //
                parametersForTestRepresents(true, "--T04:05Z", zonedDateTime), //
                parametersForTestRepresents(false, "--T04:05Z", yearMonth, localDate, localTime, localDateTime), //
                parametersForTestRepresents(true, "--03T04:05Z", zonedDateTime), //
                parametersForTestRepresents(false, "--03T04:05Z", yearMonth, localDate, localTime, localDateTime), //

                parametersForTestRepresents(false, "--07T:", yearMonth, localDate, localTime, localDateTime, zonedDateTime), //
                parametersForTestRepresents(false, "--T07:", yearMonth, localDate, localTime, localDateTime, zonedDateTime), //
                parametersForTestRepresents(false, "--T:07", yearMonth, localDate, localTime, localDateTime, zonedDateTime), //
                parametersForTestRepresents(false, "--07T07:", yearMonth, localDate, localTime, localDateTime, zonedDateTime), //
                parametersForTestRepresents(false, "--T07:07", yearMonth, localDate, localTime, localDateTime, zonedDateTime), //
                parametersForTestRepresents(false, "--07T07:07", yearMonth, localDate, localTime, localDateTime, zonedDateTime), //
                parametersForTestRepresents(false, "--07T:Z", yearMonth, localDate, localTime, localDateTime, zonedDateTime), //
                parametersForTestRepresents(false, "--T07:Z", yearMonth, localDate, localTime, localDateTime, zonedDateTime), //
                parametersForTestRepresents(false, "--T:07Z", yearMonth, localDate, localTime, localDateTime, zonedDateTime), //
                parametersForTestRepresents(false, "--07T07:Z", yearMonth, localDate, localTime, localDateTime, zonedDateTime), //
                parametersForTestRepresents(false, "--T07:07Z", yearMonth, localDate, localTime, localDateTime, zonedDateTime), //
                parametersForTestRepresents(false, "--07T07:07Z", yearMonth, localDate, localTime, localDateTime, zonedDateTime) //
        )//
                .flatMap(t -> t)//
                .collect(Collectors.toList());
    }

    @Parameters
    @Test
    public void testRepresentsLoosely(final boolean expected, final PartialDateTime partialDateTime, final Temporal temporal) {
        assertThat(partialDateTime.represents(temporal)).as("%s.represents(%s)", partialDateTime, temporal).isEqualTo(expected);
    }

    public List<Object[]> parametersForTestRepresentsLoosely() {
        final YearMonth yearMonth = YearMonth.of(2000, 2);
        final LocalDate localDate = LocalDate.of(2000, 2, 3);
        final LocalTime localTime = LocalTime.of(4, 5, 6, 7);
        final LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        final ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, TEST_ZONE);
        return Stream.of(//
                parametersForTestRepresents(true, "--T:", yearMonth, localDate, localTime, localDateTime, zonedDateTime), //
                parametersForTestRepresents(true, "--03T:", localDate, localDateTime, zonedDateTime, yearMonth, localTime), //
                parametersForTestRepresents(true, "--T04:", localTime, localDateTime, zonedDateTime, yearMonth, localDate), //
                parametersForTestRepresents(true, "--T:05", localTime, localDateTime, zonedDateTime, yearMonth, localDate), //
                parametersForTestRepresents(true, "--03T04:", localDateTime, zonedDateTime, yearMonth, localDate, localTime), //
                parametersForTestRepresents(true, "--T04:05", localTime, localDateTime, zonedDateTime, yearMonth, localDate), //
                parametersForTestRepresents(true, "--03T04:05", localDateTime, zonedDateTime, yearMonth, localDate, localTime), //

                parametersForTestRepresents(true, "--T:Z", yearMonth, localDate, localTime, localDateTime, zonedDateTime), //
                parametersForTestRepresents(true, "--03T:Z", localDate, localDateTime, zonedDateTime, yearMonth, localTime), //
                parametersForTestRepresents(true, "--T04:Z", localTime, localDateTime, zonedDateTime, yearMonth, localDate), //
                parametersForTestRepresents(true, "--T:05Z", localTime, localDateTime, zonedDateTime, yearMonth, localDate), //
                parametersForTestRepresents(true, "--03T04:Z", localDateTime, zonedDateTime, yearMonth, localDate, localTime), //
                parametersForTestRepresents(true, "--T04:05Z", localTime, localDateTime, zonedDateTime, yearMonth, localDate), //
                parametersForTestRepresents(true, "--03T04:05Z", localDateTime, zonedDateTime, yearMonth, localDate, localTime), //

                parametersForTestRepresents(true, "--07T:", yearMonth, localTime), //
                parametersForTestRepresents(false, "--07T:", localDate, localDateTime, zonedDateTime), //
                parametersForTestRepresents(true, "--T07:", yearMonth, localDate), //
                parametersForTestRepresents(false, "--T07:", localTime, localDateTime, zonedDateTime), //
                parametersForTestRepresents(true, "--T:07", yearMonth, localDate), //
                parametersForTestRepresents(false, "--T:07", localTime, localDateTime, zonedDateTime), //
                parametersForTestRepresents(true, "--07T07:", yearMonth), //
                parametersForTestRepresents(false, "--07T07:", localDate, localTime, localDateTime, zonedDateTime), //
                parametersForTestRepresents(true, "--T07:07", yearMonth, localDate), //
                parametersForTestRepresents(false, "--T07:07", localTime, localDateTime, zonedDateTime), //
                parametersForTestRepresents(true, "--07T07:07", yearMonth), //
                parametersForTestRepresents(false, "--07T07:07", localDate, localTime, localDateTime, zonedDateTime), //
                parametersForTestRepresents(true, "--07T:Z", yearMonth, localTime), //
                parametersForTestRepresents(false, "--07T:Z", localDate, localDateTime, zonedDateTime), //
                parametersForTestRepresents(true, "--T07:Z", yearMonth, localDate), //
                parametersForTestRepresents(false, "--T07:Z", localTime, localDateTime, zonedDateTime), //
                parametersForTestRepresents(true, "--T:07Z", yearMonth, localDate), //
                parametersForTestRepresents(false, "--T:07Z", localTime, localDateTime, zonedDateTime), //
                parametersForTestRepresents(true, "--07T07:Z", yearMonth), //
                parametersForTestRepresents(false, "--07T07:Z", localDate, localTime, localDateTime, zonedDateTime), //
                parametersForTestRepresents(true, "--T07:07Z", yearMonth, localDate), //
                parametersForTestRepresents(false, "--T07:07Z", localTime, localDateTime, zonedDateTime), //
                parametersForTestRepresents(true, "--07T07:07Z", yearMonth), //
                parametersForTestRepresents(false, "--07T07:07Z", localDate, localTime, localDateTime, zonedDateTime) //
        )//
                .flatMap(t -> t)//
                .collect(Collectors.toList());
    }

    private Stream<Object[]> parametersForTestRepresents(final boolean expected, final String partialDateTime, final Temporal... temporals) {
        final PartialDateTime parsedPartialDateTime = PartialDateTime.parse(partialDateTime);
        return Stream.of(temporals)//
                .map(temporal -> new Object[] {//
                        expected, parsedPartialDateTime, temporal //
                });
    }

    @Parameters(source = TACStringProvider.class)
    @Test
    public void testToTACString(final String expected, final int day, final int hour, final int minute, final String zoneId) {
        final PartialDateTime partialDateTime = createPartialDateTime(day, hour, minute, zoneId);
        assertThat(partialDateTime.toTACString()).isEqualTo(expected);
    }

    @Parameters(source = PartialDateTimeStringProvider.class)
    @Test
    public void testToString(final String expected, final int day, final int hour, final int minute, final String zoneId) {
        final PartialDateTime partialDateTime = createPartialDateTime(day, hour, minute, zoneId);
        assertThat(partialDateTime.toString()).isEqualTo(expected);
    }

    @Parameters({ //
            "2000-02-03T04:05Z, --03T04:05Z, 2000-02", //
            "2000-02-04T00:00Z, --03T24:00Z, 2000-02", //
    })
    @Test
    public void testToZonedDateTimeYearMonth(final String expected, final String partialDateTime, final String issueDate) {
        testToZonedDateTimeYearMonth(ZonedDateTime.parse(expected), PartialDateTime.parse(partialDateTime), YearMonth.parse(issueDate));
    }

    private void testToZonedDateTimeYearMonth(final ZonedDateTime expected, final PartialDateTime partialDateTime, final YearMonth issueDate) {
        assertThat(partialDateTime.toZonedDateTime(issueDate)).as("%s.toZonedDateTime(%s)", partialDateTime, issueDate).isEqualTo(expected);
    }

    @Parameters({ //
            "--T00:, 2000-01", //
            "--T:00, 2000-01", //
            "--32T00:, 2000-01", //
            "--01T25:, 2000-01", //
            "--01T00:61, 2000-01", //
            "--29T00:00Z, 2001-02", // non-leap year
            "--29T24:00Z, 2001-02", // non-leap year
    })
    @Test
    public void testToZonedDateTimeYearMonthInvalid(final String partialDateTime, final String issueDate) {
        testToZonedDateTimeYearMonthInvalid(PartialDateTime.parse(partialDateTime), YearMonth.parse(issueDate));
    }

    private void testToZonedDateTimeYearMonthInvalid(final PartialDateTime partialDateTime, final YearMonth issueDate) {
        final Class<DateTimeException> expectedException = DateTimeException.class;
        final ExpectedExceptionDescription description = new ExpectedExceptionDescription(expectedException);
        assertThatExceptionOfType(expectedException).as(description)//
                .isThrownBy(() -> description.setUnexpectedResult(partialDateTime.toZonedDateTime(issueDate)))//
                .withMessageContaining(partialDateTime.toString())//
                .satisfies(exception -> {
                    if (partialDateTime.getDay().isPresent()) {
                        assertThat(exception).hasMessageContaining(issueDate.toString());
                    }
                });
    }

    @Parameters({ //
            "2000-01-02T03:04Z, --02T03:04Z, 2000-01-02", //
            "2000-01-02T03:04Z, --02T03:04Z, 2000-01-01", //
            "2000-02-02T03:04Z, --02T03:04Z, 2000-01-03", // issue day > day of partial
            "2000-02-03T00:00Z, --02T24:00Z, 2000-01-03", // issue day > day of partial midnight 24h
            "2000-01-02T03:04+01:00, --02T03:04+01:00, 2000-01-02", //
            "2000-01-02T03:04Z, --02T03:04, 2000-01-02", // default zone UTC
            "2000-01-02T03:04Z, --T03:04Z, 2000-01-02", // partial missing day
            "2000-02-29T00:00Z, --29T00:00Z, 2000-02-28", // leap year
            "2000-03-01T00:00Z, --29T24:00Z, 2000-02-28", // midnight on leap year
    })
    @Test
    public void testToZonedDateTimeLocalDate(final String expected, final String partialDateTime, final String issueDate) {
        testToZonedDateTimeLocalDate(ZonedDateTime.parse(expected), PartialDateTime.parse(partialDateTime), LocalDate.parse(issueDate));
    }

    private void testToZonedDateTimeLocalDate(final ZonedDateTime expected, final PartialDateTime partialDateTime, final LocalDate issueDate) {
        assertThat(partialDateTime.toZonedDateTime(issueDate)).as("%s.toZonedDateTime(%s)", partialDateTime, issueDate).isEqualTo(expected);
    }

    @Parameters({ //
            "--T:00, 2000-01-01", //
            "--32T00:, 2000-01-01", //
            "--T25:, 2000-01-01", //
            "--T00:61, 2000-01-01", //
            "--29T00:00Z, 2001-02-28", // non-leap year
            "--29T24:00Z, 2001-02-28", // non-leap year
    })
    @Test
    public void testToZonedDateTimeLocalDateInvalid(final String partialDateTime, final String issueDate) {
        testToZonedDateTimeLocalDateInvalid(PartialDateTime.parse(partialDateTime), LocalDate.parse(issueDate));
    }

    private void testToZonedDateTimeLocalDateInvalid(final PartialDateTime partialDateTime, final LocalDate issueDate) {
        final Class<? extends Throwable> expectedException = DateTimeException.class;
        final ExpectedExceptionDescription description = new ExpectedExceptionDescription(expectedException);
        assertThatExceptionOfType(expectedException).as(description)//
                .isThrownBy(() -> description.setUnexpectedResult(partialDateTime.toZonedDateTime(issueDate)))//
                .withMessageContaining(partialDateTime.toString())//
                .satisfies(exception -> {
                    if (partialDateTime.getHour().isPresent()) {
                        assertThat(exception).hasMessageContaining(issueDate.toString());
                    }
                });
    }

    @Parameters({ //
            // AFTER
            // Partial with all fields
            "2000-02-02T03:04Z, --02T03:04Z, 2000-01-02T03:04Z,             AFTER", //
            "2000-01-02T03:04Z, --02T03:04Z, 2000-01-02T03:03:59.999Z,      AFTER", //
            "2000-01-01T00:00Z, --01T00:00Z, 1999-12-31T23:59:59.999Z,      AFTER", //

            // Partial with day only
            "2000-02-02T00:00Z, --02T:,      2000-01-02T00:00Z,             AFTER", //
            "2000-01-02T00:00Z, --02T:,      2000-01-01T23:59:59.999Z,      AFTER", //
            "2000-01-01T00:00Z, --01T:,      1999-12-31T23:59:59.999Z,      AFTER", //

            // Partial with hour, minute and zone
            "2000-01-03T03:04Z, --T03:04Z,   2000-01-02T03:04Z, AFTER", //
            "2000-01-02T03:04Z, --T03:04Z,   2000-01-02T03:03:59.999Z,      AFTER", //
            "2000-01-01T00:00Z, --T00:00Z,   1999-12-31T23:59:59.999Z,      AFTER", //

            // Partial with hour only
            "2000-01-02T03:00Z, --T03:,      2000-01-01T03:00Z,             AFTER", //
            "2000-01-01T03:00Z, --T03:,      2000-01-01T02:59:59.999Z,      AFTER", //
            "2000-01-01T00:00Z, --T00:,      1999-12-31T23:59:59.999Z,      AFTER", //

            // Partial with minute and zone only
            "2000-01-01T01:04Z, --T:04Z,     2000-01-01T00:04Z, AFTER", //
            "2000-01-01T00:04Z, --T:04Z,     2000-01-01T00:03:59.999Z,      AFTER", //
            "2000-01-01T00:00Z, --T:00Z,     1999-12-31T23:59:59.999Z,      AFTER", //

            // Empty partial
            "2000-01-01T00:01Z, --T:,        2000-01-01T00:00Z, AFTER", //
            "2000-01-01T00:00Z, --T:,        1999-12-31T23:59:59.999Z,      AFTER", //

            // Midnight 00:00 on 28th of February on leap year and non-leap year
            "2000-03-29T00:00Z, --29T00:00Z, 2000-02-29T00:00Z,             AFTER", // leap year
            "2000-02-29T00:00Z, --29T00:00Z, 2000-02-28T23:59:59.999Z,      AFTER", // leap year
            "2001-03-29T00:00Z, --29T00:00Z, 2001-02-28T23:59:59.999Z,      AFTER", // non-leap year
            "2001-03-29T00:00Z, --29T00:00Z, 2001-01-29T00:00Z,             AFTER", // non-leap year

            // Midnight 24:00 on 28th of February on leap year and non-leap year
            "2000-03-29T00:00Z, --28T24:00Z, 2000-02-29T00:00Z,             AFTER", // leap year
            "2000-02-29T00:00Z, --28T24:00Z, 2000-02-28T23:59:59.999Z,      AFTER", // leap year
            "2001-03-29T00:00Z, --28T24:00Z, 2001-03-01T00:00Z,             AFTER", // (non-leap year)
            "2001-03-01T00:00Z, --28T24:00Z, 2001-01-29T00:00Z,             AFTER", // (non-leap year)
            "2001-03-01T00:00Z, --28T24:00Z, 2001-02-28T23:59:59.999Z,      AFTER", // (non-leap year)

            // Reference with non-UTC zone offset
            "2000-01-02T03:00Z, --T03:Z,     2000-01-01T04:00+01:00,        AFTER", //
            "2000-01-01T03:00Z, --T03:Z,     2000-01-01T03:59:59.999+01:00, AFTER", //
            "2000-01-01T00:00Z, --T00:Z,     2000-01-01T00:59:59.999+01:00, AFTER", //
            "2000-01-01T00:00Z, --T00:Z,     1999-12-31T22:59:59.999-01:00, AFTER", //

            // Partial with non-UTC zone offset
            "2000-01-02T00:00+01:00, --T00:+01:00, 2000-01-01T00:00Z,       AFTER", //
            "2000-01-02T00:00+01:00, --T00:+01:00, 1999-12-31T23:00Z,       AFTER", //
            "2000-01-01T00:00+01:00, --T00:+01:00, 1999-12-31T22:59:59.999Z, AFTER", //

            // NOT_BEFORE
            // Partial with all fields
            "2000-01-02T03:04Z, --02T03:04Z, 2000-01-02T03:04Z,             NOT_BEFORE", //
            "2000-02-02T03:04Z, --02T03:04Z, 2000-01-02T03:04:00.001Z,      NOT_BEFORE", //
            "2000-01-02T03:04Z, --02T03:04Z, 2000-01-02T03:03:59.999Z,      NOT_BEFORE", //
            "2000-01-01T00:00Z, --01T00:00Z, 1999-12-31T23:59:59.999Z,      NOT_BEFORE", //

            // Partial with day only
            "2000-01-02T00:00Z, --02T:,      2000-01-02T00:00Z,             NOT_BEFORE", //
            "2000-02-02T00:00Z, --02T:,      2000-01-02T00:00:00.001Z,      NOT_BEFORE", //
            "2000-01-02T00:00Z, --02T:,      2000-01-01T23:59:59.999Z,      NOT_BEFORE", //
            "2000-01-01T00:00Z, --01T:,      1999-12-31T23:59:59.999Z,      NOT_BEFORE", //

            // Partial with hour, minute and zone
            "2000-01-02T03:04Z, --T03:04Z,   2000-01-02T03:04Z,             NOT_BEFORE", //
            "2000-01-03T03:04Z, --T03:04Z,   2000-01-02T03:04:00.001Z,      NOT_BEFORE", //
            "2000-01-02T03:04Z, --T03:04Z,   2000-01-02T03:03:59.999Z,      NOT_BEFORE", //
            "2000-01-01T00:00Z, --T00:00Z,   1999-12-31T23:59:59.999Z,      NOT_BEFORE", //

            // Partial with hour only
            "2000-01-01T03:00Z, --T03:,      2000-01-01T03:00Z,             NOT_BEFORE", //
            "2000-01-02T03:00Z, --T03:,      2000-01-01T03:00:00.001Z,      NOT_BEFORE", //
            "2000-01-01T03:00Z, --T03:,      2000-01-01T02:59:59.999Z,      NOT_BEFORE", //
            "2000-01-01T00:00Z, --T00:,      1999-12-31T23:59:59.999Z,      NOT_BEFORE", //

            // Partial with minute and zone only
            "2000-01-01T00:04Z, --T:04Z,     2000-01-01T00:04Z,             NOT_BEFORE", //
            "2000-01-01T01:04Z, --T:04Z,     2000-01-01T00:04:00.001Z,      NOT_BEFORE", //
            "2000-01-01T00:04Z, --T:04Z,     2000-01-01T00:03:59.999Z,      NOT_BEFORE", //
            "2000-01-01T00:00Z, --T:00Z,     1999-12-31T23:59:59.999Z,      NOT_BEFORE", //

            // Empty partial
            "2000-01-01T00:00Z, --T:,        2000-01-01T00:00Z,             NOT_BEFORE", //
            "2000-01-01T00:01Z, --T:,        2000-01-01T00:00:00.001Z,      NOT_BEFORE", //
            "2000-01-01T00:00Z, --T:,        1999-12-31T23:59:59.999Z,      NOT_BEFORE", //

            // Midnight 00:00 on 28th of February on leap year and non-leap year
            "2000-02-29T00:00Z, --29T00:00Z, 2000-02-29T00:00Z,             NOT_BEFORE", // leap year
            "2000-03-29T00:00Z, --29T00:00Z, 2000-02-29T00:00:00.001Z,      NOT_BEFORE", // leap year
            "2000-02-29T00:00Z, --29T00:00Z, 2000-02-28T23:59:59.999Z,      NOT_BEFORE", // leap year
            "2001-01-29T00:00Z, --29T00:00Z, 2001-01-29T00:00Z,             NOT_BEFORE", // non-leap year
            "2001-03-29T00:00Z, --29T00:00Z, 2001-01-29T00:00:00.001Z,      NOT_BEFORE", // non-leap year
            "2001-03-29T00:00Z, --29T00:00Z, 2001-02-28T23:59:59.999Z,      NOT_BEFORE", // non-leap year

            // Midnight 24:00 on 28th of February on leap year and non-leap year
            "2000-02-29T00:00Z, --28T24:00Z, 2000-02-29T00:00Z,             NOT_BEFORE", // leap year
            "2000-03-29T00:00Z, --28T24:00Z, 2000-02-29T00:00:00.001Z,      NOT_BEFORE", // leap year
            "2000-02-29T00:00Z, --28T24:00Z, 2000-02-28T23:59:59.999Z,      NOT_BEFORE", // leap year
            "2001-03-29T00:00Z, --28T24:00Z, 2001-03-01T00:00Z,             NOT_BEFORE", // (non-leap year)
            "2001-01-29T00:00Z, --28T24:00Z, 2001-01-29T00:00Z,             NOT_BEFORE", // (non-leap year)
            "2001-03-01T00:00Z, --28T24:00Z, 2001-01-29T00:00:00.001Z,      NOT_BEFORE", // (non-leap year)
            "2001-03-01T00:00Z, --28T24:00Z, 2001-02-28T23:59:59.999Z,      NOT_BEFORE", // (non-leap year)

            // Reference with non-UTC zone offset
            "2000-01-01T03:00Z, --T03:Z, 2000-01-01T04:00+01:00,            NOT_BEFORE", //
            "2000-01-02T03:00Z, --T03:Z, 2000-01-01T04:00:00.001+01:00,     NOT_BEFORE", //
            "2000-01-01T03:00Z, --T03:Z, 2000-01-01T03:59:59.999+01:00,     NOT_BEFORE", //
            "2000-01-01T00:00Z, --T00:Z, 2000-01-01T01:00+01:00,            NOT_BEFORE", //
            "2000-01-01T00:00Z, --T00:Z, 1999-12-31T22:59:59.999-01:00,     NOT_BEFORE", //

            // Partial with non-UTC zone offset
            "2000-01-02T00:00+01:00, --T00:+01:00, 2000-01-01T00:00Z,       NOT_BEFORE", //
            "2000-01-01T00:00+01:00, --T00:+01:00, 1999-12-31T23:00Z,       NOT_BEFORE", //
            "2000-01-02T00:00+01:00, --T00:+01:00, 1999-12-31T23:00:00.001Z, NOT_BEFORE", //
            "2000-01-01T00:00+01:00, --T00:+01:00, 1999-12-31T22:59:59.999Z, NOT_BEFORE", //

            // BEFORE
            // Partial with all fields
            "1999-12-02T03:04Z, --02T03:04Z, 2000-01-02T03:04Z,             BEFORE", //
            "2000-01-02T03:04Z, --02T03:04Z, 2000-01-02T03:04:00.001Z,      BEFORE", //

            // Partial with day only
            "1999-12-02T00:00Z, --02T:,      2000-01-02T00:00Z,             BEFORE", //
            "2000-01-02T00:00Z, --02T:,      2000-01-02T00:00:00.001Z,      BEFORE", //

            // Partial with hour, minute and zone
            "2000-01-01T03:04Z, --T03:04Z,   2000-01-02T03:04Z,             BEFORE", //
            "2000-01-02T03:04Z, --T03:04Z,   2000-01-02T03:04:00.001Z,      BEFORE", //
            "1999-12-31T00:00Z, --T00:00Z,   2000-01-01T00:00Z,             BEFORE", //

            // Partial with hour only
            "2000-01-01T03:00Z, --T03:,      2000-01-02T03:00Z,             BEFORE", //
            "2000-01-01T03:00Z, --T03:,      2000-01-01T03:00:00.001Z,      BEFORE", //
            "1999-12-31T00:00Z, --T00:,      2000-01-01T00:00Z,             BEFORE", //

            // Partial with minute and zone only
            "2000-01-01T02:04Z, --T:04Z,     2000-01-01T03:04Z,             BEFORE", //
            "2000-01-01T03:04Z, --T:04Z,     2000-01-01T03:04:00.001Z,      BEFORE", //
            "1999-12-31T23:00Z, --T:00Z,     2000-01-01T00:00Z,             BEFORE", //

            // Empty partial
            "1999-12-31T23:59Z, --T:,        2000-01-01T00:00Z,             BEFORE", //
            "2000-01-01T00:00Z, --T:,        2000-01-01T00:00:00.001Z,      BEFORE", //

            // Midnight 00:00 on 28th of February on leap year and non-leap year
            "2000-01-29T00:00Z, --29T00:00Z, 2000-02-29T00:00Z,             BEFORE", // leap year
            "2000-02-29T00:00Z, --29T00:00Z, 2000-02-29T00:00:00.001Z,      BEFORE", // leap year
            "2001-01-29T00:00Z, --29T00:00Z, 2001-03-29T00:00Z,             BEFORE", // non-leap year

            // Midnight 24:00 on 28th of February on leap year and non-leap year
            "2000-01-29T00:00Z, --28T24:00Z, 2000-02-29T00:00Z,             BEFORE", // leap year
            "2000-02-29T00:00Z, --28T24:00Z, 2000-02-29T00:00:00.001Z,      BEFORE", // leap year
            "2001-03-01T00:00Z, --28T24:00Z, 2001-03-29T00:00Z,             BEFORE", // (non-leap year)
            "2001-01-29T00:00Z, --28T24:00Z, 2001-03-01T00:00Z,             BEFORE", // (non-leap year)
            "2001-01-29T00:00Z, --28T24:00Z, 2001-02-28T23:59:59.999Z,      BEFORE", // (non-leap year)

            // Reference with non-UTC zone offset
            "1999-12-31T03:00Z, --T03:Z,     2000-01-01T04:00+01:00,        BEFORE", //
            "2000-01-01T03:00Z, --T03:Z,     2000-01-01T04:00:00.001+01:00, BEFORE", //
            "2000-01-01T00:00Z, --T00:Z,     2000-01-01T01:00:00.001+01:00, BEFORE", //
            "2000-01-01T00:00Z, --T00:Z,     1999-12-31T23:00:00.001-01:00, BEFORE", //

            // Partial with non-UTC zone offset
            "2000-01-01T00:00+01:00, --T00:+01:00, 1999-12-31T23:00:00.001Z, BEFORE", //
            "1999-12-31T00:00+01:00, --T00:+01:00, 1999-12-31T23:00Z,       BEFORE", //

            // Partial with all fields
            "2000-01-02T03:04Z, --02T03:04Z, 2000-01-02T03:04Z,             NOT_AFTER", //
            "2000-01-02T03:04Z, --02T03:04Z, 2000-01-02T03:04:00.001Z,      NOT_AFTER", //
            "1999-12-02T03:04Z, --02T03:04Z, 2000-01-02T03:03:59.999Z,      NOT_AFTER", //

            // Partial with day only
            "2000-01-02T00:00Z, --02T:,      2000-01-02T00:00Z,             NOT_AFTER", //
            "2000-01-02T00:00Z, --02T:,      2000-01-02T00:00:00.001Z,      NOT_AFTER", //
            "1999-12-02T00:00Z, --02T:,      2000-01-01T23:59:59.999Z,      NOT_AFTER", //

            // Partial with hour, minute and zone
            "2000-01-02T03:04Z, --T03:04Z,   2000-01-02T03:04Z,             NOT_AFTER", //
            "2000-01-02T03:04Z, --T03:04Z,   2000-01-02T03:04:00.001Z,      NOT_AFTER", //
            "1999-12-31T03:04Z, --T03:04Z,   2000-01-01T03:03:59.999Z,      NOT_AFTER", //

            // Partial with hour only
            "2000-01-02T03:00Z, --T03:,      2000-01-02T03:00Z,             NOT_AFTER", //
            "2000-01-01T03:00Z, --T03:,      2000-01-01T03:00:00.001Z,      NOT_AFTER", //
            "1999-12-31T03:00Z, --T03:,      2000-01-01T02:59:59.999Z,      NOT_AFTER", //

            // Partial with minute and zone only
            "2000-01-01T03:04Z, --T:04Z,     2000-01-01T03:04Z,             NOT_AFTER", //
            "2000-01-01T03:04Z, --T:04Z,     2000-01-01T03:04:00.001Z,      NOT_AFTER", //
            "1999-12-31T23:04Z, --T:04Z,     2000-01-01T00:03:59.999Z,      NOT_AFTER", //

            // Empty partial
            "2000-01-01T00:00Z, --T:,        2000-01-01T00:00Z,             NOT_AFTER", //
            "2000-01-01T00:00Z, --T:,        2000-01-01T00:00:00.001Z,      NOT_AFTER", //
            "1999-12-31T23:59Z, --T:,        1999-12-31T23:59:59.999Z,      NOT_AFTER", //

            // Midnight 00:00 on 28th of February on leap year and non-leap year
            "2000-02-29T00:00Z, --29T00:00Z, 2000-02-29T00:00Z,             NOT_AFTER", // leap year
            "2000-02-29T00:00Z, --29T00:00Z, 2000-02-29T00:00:00.001Z,      NOT_AFTER", // leap year
            "2000-02-29T00:00Z, --29T00:00Z, 2000-03-28T23:59:59.999Z,      NOT_AFTER", // leap year
            "2001-03-29T00:00Z, --29T00:00Z, 2001-03-29T00:00Z,             NOT_AFTER", // non-leap year
            "2001-01-29T00:00Z, --29T00:00Z, 2001-03-28T23:59:59.999Z,      NOT_AFTER", // non-leap year

            // Midnight 24:00 on 28th of February on leap year and non-leap year
            "2000-02-29T00:00Z, --28T24:00Z, 2000-02-29T00:00Z,             NOT_AFTER", // leap year
            "2000-02-29T00:00Z, --28T24:00Z, 2000-02-29T00:00:00.001Z,      NOT_AFTER", // leap year
            "2000-02-29T00:00Z, --28T24:00Z, 2000-03-28T23:59:59.999Z,      NOT_AFTER", // leap year
            "2001-03-29T00:00Z, --28T24:00Z, 2001-03-29T00:00Z,             NOT_AFTER", // (non-leap year)
            "2001-03-01T00:00Z, --28T24:00Z, 2001-03-01T00:00Z,             NOT_AFTER", // (non-leap year)
            "2001-01-29T00:00Z, --28T24:00Z, 2001-02-28T23:59:59.999Z,      NOT_AFTER", // (non-leap year)

            // Reference with non-UTC zone offset
            "2000-01-01T03:00Z, --T03:Z,     2000-01-01T04:00+01:00,        NOT_AFTER", //
            "2000-01-01T03:00Z, --T03:Z,     2000-01-01T04:00:00.001+01:00, NOT_AFTER", //
            "1999-12-31T03:00Z, --T03:Z,     2000-01-01T03:59:59.999+01:00, NOT_AFTER", //
            "2000-01-01T00:00Z, --T00:Z,     2000-01-01T01:00+01:00,        NOT_AFTER", //
            "2000-01-01T00:00Z, --T00:Z,     1999-12-31T23:00-01:00,        NOT_AFTER", //

            // Partial with non-UTC zone offset
            "2000-01-01T00:00+01:00, --T00:+01:00, 1999-12-31T23:00:00.001Z, NOT_AFTER", //
            "2000-01-01T00:00+01:00, --T00:+01:00, 1999-12-31T23:00Z,       NOT_AFTER", //
            "1999-12-31T00:00+01:00, --T00:+01:00, 1999-12-31T22:59:59.999Z, NOT_AFTER", //

    })
    @Test
    public void testToZonedDateTimeCondition(final String expected, final String partialDateTime, final String referenceTime,
            final ReferenceCondition condition) {
        testToZonedDateTimeCondition(ZonedDateTime.parse(expected), PartialDateTime.parse(partialDateTime), ZonedDateTime.parse(referenceTime), condition);
    }

    private void testToZonedDateTimeCondition(final ZonedDateTime expected, final PartialDateTime partialDateTime, final ZonedDateTime referenceTime,
            final ReferenceCondition condition) {
        assertThat(partialDateTime.toZonedDateTime(referenceTime, condition)).as("%s.toZonedDateTime(%s, %s)", partialDateTime, referenceTime, condition)
                .isEqualTo(expected);
    }

    @Parameters({ //
            // AFTER
            // Partial with all fields
            "2000-01-02T03:04Z, --02T03:04Z, 2000-01-02T03:04:00.001Z, AFTER,      false, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just after
            "2000-01-02T03:04Z, --02T03:04Z, 2000-01-02T03:03:59.999Z, AFTER,      true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just before
            "2000-01-01T00:00Z, --01T00:00Z, 1999-12-31T23:59:59.999Z, AFTER,      true,  2000-01-01T00:00Z, 2000-01-01T00:00:00.001Z", // just before
            "2000-01-01T00:00Z, --01T00:00Z, 2000-01-16T11:59:59.999Z, AFTER,      false, 2000-01-01T00:00Z, 2000-01-01T00:00:00.001Z", // maximal after
            "2000-02-01T00:00Z, --01T00:00Z, 2000-01-16T12:00Z,        AFTER,      true,  2000-02-01T00:00Z, 2000-02-01T00:00:00.001Z", // minimal before
            "2000-01-01T00:00Z, --01T00:00Z, 1999-12-16T12:00Z,        AFTER,      true,  2000-01-01T00:00Z, 2000-01-01T00:00:00.001Z", // minimal before
            "1999-12-01T00:00Z, --01T00:00Z, 1999-12-16T11:59:59.999Z, AFTER,      false, 1999-12-01T00:00Z, 1999-12-01T00:00:00.001Z", // maximal after

            // Reference at range end
            "2000-01-03T04:05Z, --03T04:05,  2000-02-03T04:05Z,        AFTER,      false, 2000-01-01T00:00Z, 2000-02-03T04:05Z       ", // outside range
            "2000-02-03T04:05Z, --03T04:05,  2000-02-03T04:05Z,        AFTER,      false, 2000-01-01T00:00Z, 2000-02-03T04:05:00.001Z", // just within range
            "2000-01-31T00:00Z, --31T00:00,  2000-03-31T00:00Z,        AFTER,      false, 2000-01-01T00:00Z, 2000-03-31T00:00Z       ", // outside range
            "2000-03-31T00:00Z, --31T00:00,  2000-03-31T00:00Z,        AFTER,      false, 2000-01-01T00:00Z, 2000-03-31T00:00:00.001Z", // just within range

            "2000-01-03T04:00Z, --03T04:,    2000-02-03T04:00Z,        AFTER,      false, 2000-01-01T00:00Z, 2000-02-03T04:00Z       ", // outside range
            "2000-02-03T04:00Z, --03T04:,    2000-02-03T04:00Z,        AFTER,      false, 2000-01-01T00:00Z, 2000-02-03T04:00:00.001Z", // just within range
            "2000-01-31T00:00Z, --31T00:,    2000-03-31T00:00Z,        AFTER,      false, 2000-01-01T00:00Z, 2000-03-31T00:00Z       ", // outside range
            "2000-03-31T00:00Z, --31T00:,    2000-03-31T00:00Z,        AFTER,      false, 2000-01-01T00:00Z, 2000-03-31T00:00:00.001Z", // just within range

            "2000-01-03T00:00Z, --03T:,      2000-02-03T00:00Z,        AFTER,      false, 2000-01-01T00:00Z, 2000-02-03T00:00Z       ", // outside range
            "2000-02-03T00:00Z, --03T:,      2000-02-03T00:00Z,        AFTER,      false, 2000-01-01T00:00Z, 2000-02-03T00:00:00.001Z", // just within range
            "2000-01-31T00:00Z, --31T:,      2000-03-31T00:00Z,        AFTER,      false, 2000-01-01T00:00Z, 2000-03-31T00:00Z       ", // outside range
            "2000-03-31T00:00Z, --31T:,      2000-03-31T00:00Z,        AFTER,      false, 2000-01-01T00:00Z, 2000-03-31T00:00:00.001Z", // just within range

            "2000-02-02T04:05Z, --T04:05,    2000-02-03T04:05Z,        AFTER,      false, 2000-01-01T00:00Z, 2000-02-03T04:05Z       ", // outside range
            "2000-02-03T04:05Z, --T04:05,    2000-02-03T04:05Z,        AFTER,      false, 2000-01-01T00:00Z, 2000-02-03T04:05:00.001Z", // just within range

            "2000-02-02T04:00Z, --T04:,      2000-02-03T04:00Z,        AFTER,      false, 2000-01-01T00:00Z, 2000-02-03T04:00Z       ", // outside range
            "2000-02-03T04:00Z, --T04:,      2000-02-03T04:00Z,        AFTER,      false, 2000-01-01T00:00Z, 2000-02-03T04:00:00.001Z", // just within range

            "2000-02-03T03:05Z, --T:05,      2000-02-03T04:05Z,        AFTER,      false, 2000-01-01T00:00Z, 2000-02-03T04:05Z       ", // outside range
            "2000-02-03T04:05Z, --T:05,      2000-02-03T04:05Z,        AFTER,      false, 2000-01-01T00:00Z, 2000-02-03T04:05:00.001Z", // just within range

            "2000-02-03T04:04Z, --T:,        2000-02-03T04:05Z,        AFTER,      false, 2000-01-01T00:00Z, 2000-02-03T04:05Z       ", // outside range
            "2000-02-03T04:05Z, --T:,        2000-02-03T04:05Z,        AFTER,      false, 2000-01-01T00:00Z, 2000-02-03T04:05:00.001Z", // just within range

            // Reference before range
            "2000-01-02T03:04Z, --02T03:04,  1999-12-02T03:04Z,        AFTER,      true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable
            "2000-01-02T03:04Z, --02T03:04,  1999-12-02T03:04:00.001Z, AFTER,      true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just after
            "2000-01-02T03:04Z, --02T03:04,  1999-12-02T03:03:59.999Z, AFTER,      true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just before

            "2000-01-02T03:00Z, --02T03:,    1999-12-02T03:00Z,        AFTER,      true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // representable
            "2000-01-02T03:00Z, --02T03:,    1999-12-02T03:00:00.001Z, AFTER,      true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just after
            "2000-01-02T03:00Z, --02T03:,    1999-12-02T02:59:59.999Z, AFTER,      true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just before

            "2000-01-02T00:00Z, --02T:,      1999-12-02T00:00Z,        AFTER,      true,  2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // representable
            "2000-01-02T00:00Z, --02T:,      1999-12-02T00:00:00.001Z, AFTER,      true,  2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // just after
            "2000-01-02T00:00Z, --02T:,      1999-12-01T23:59:59.999Z, AFTER,      true,  2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // just before

            "2000-01-02T03:04Z, --T03:04,    2000-01-01T03:04Z,        AFTER,      true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable
            "2000-01-02T03:04Z, --T03:04,    2000-01-01T03:04:00.001Z, AFTER,      true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just after
            "2000-01-02T03:04Z, --T03:04,    2000-01-01T03:03:59.999Z, AFTER,      true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just before

            "2000-01-02T03:00Z, --T03:,      2000-01-01T03:00Z,        AFTER,      true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // representable
            "2000-01-02T03:00Z, --T03:,      2000-01-01T03:00:00.001Z, AFTER,      true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just after
            "2000-01-02T03:00Z, --T03:,      2000-01-01T02:59:59.999Z, AFTER,      true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just before

            "2000-01-02T03:04Z, --T:,        1999-12-02T03:04Z,        AFTER,      true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable

            // Reference after range
            "2000-01-02T03:04Z, --02T03:04,  2000-02-02T03:04Z,        AFTER,      false, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable
            "2000-01-02T03:04Z, --02T03:04,  2000-02-02T03:04:00.001Z, AFTER,      false, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just after
            "2000-01-02T03:04Z, --02T03:04,  2000-02-02T03:03:59.999Z, AFTER,      false, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just before

            "2000-01-02T03:00Z, --02T03:,    2000-02-02T03:00Z,        AFTER,      false, 2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // representable
            "2000-01-02T03:00Z, --02T03:,    2000-02-02T03:00:00.001Z, AFTER,      false, 2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just after
            "2000-01-02T03:00Z, --02T03:,    2000-02-02T02:59:59.999Z, AFTER,      false, 2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just before

            "2000-01-02T00:00Z, --02T:,      2000-02-02T00:00Z,        AFTER,      false, 2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // representable
            "2000-01-02T00:00Z, --02T:,      2000-02-02T00:00:00.001Z, AFTER,      false, 2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // just after
            "2000-01-02T00:00Z, --02T:,      2000-02-01T23:59:59.999Z, AFTER,      false, 2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // just before

            "2000-01-02T03:04Z, --T03:04,    2000-02-01T03:04Z,        AFTER,      false, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable
            "2000-01-02T03:04Z, --T03:04,    2000-02-01T03:04:00.001Z, AFTER,      false, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just after
            "2000-01-02T03:04Z, --T03:04,    2000-02-01T03:03:59.999Z, AFTER,      false, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just before

            "2000-01-02T03:00Z, --T03:,      2000-02-01T03:00Z,        AFTER,      false, 2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // representable
            "2000-01-02T03:00Z, --T03:,      2000-02-01T03:00:00.001Z, AFTER,      false, 2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just after
            "2000-01-02T03:00Z, --T03:,      2000-02-01T02:59:59.999Z, AFTER,      false, 2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just before

            "2000-01-02T03:04Z, --T:,        2000-02-02T03:04Z,        AFTER,      false, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable

            // NOT_BEFORE
            // Partial with all fields
            "2000-01-02T03:04Z, --02T03:04Z, 2000-01-02T03:04Z,        NOT_BEFORE, true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // exactly
            "2000-01-02T03:04Z, --02T03:04Z, 2000-01-02T03:04:00.001Z, NOT_BEFORE, false, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just after
            "2000-01-02T03:04Z, --02T03:04Z, 2000-01-02T03:03:59.999Z, NOT_BEFORE, true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just before
            "2000-01-01T00:00Z, --01T00:00Z, 1999-12-31T23:59:59.999Z, NOT_BEFORE, true,  2000-01-01T00:00Z, 2000-01-01T00:00:00.001Z", // just before
            "2000-01-01T00:00Z, --01T00:00Z, 2000-01-16T11:59:59.999Z, NOT_BEFORE, false, 2000-01-01T00:00Z, 2000-01-01T00:00:00.001Z", // maximal after
            "2000-02-01T00:00Z, --01T00:00Z, 2000-01-16T12:00Z,        NOT_BEFORE, true,  2000-02-01T00:00Z, 2000-02-01T00:00:00.001Z", // minimal before
            "2000-01-01T00:00Z, --01T00:00Z, 1999-12-16T12:00Z,        NOT_BEFORE, true,  2000-01-01T00:00Z, 2000-01-01T00:00:00.001Z", // minimal before
            "1999-12-01T00:00Z, --01T00:00Z, 1999-12-16T11:59:59.999Z, NOT_BEFORE, false, 1999-12-01T00:00Z, 1999-12-01T00:00:00.001Z", // maximal after

            // Reference at range end
            "2000-01-03T04:05Z, --03T04:05,  2000-02-03T04:05Z,        NOT_BEFORE, false, 2000-01-01T00:00Z, 2000-02-03T04:05Z       ", // outside range
            "2000-02-03T04:05Z, --03T04:05,  2000-02-03T04:05Z,        NOT_BEFORE, true,  2000-01-01T00:00Z, 2000-02-03T04:05:00.001Z", // just within range
            "2000-01-31T00:00Z, --31T00:00,  2000-03-31T00:00Z,        NOT_BEFORE, false, 2000-01-01T00:00Z, 2000-03-31T00:00Z       ", // outside range
            "2000-03-31T00:00Z, --31T00:00,  2000-03-31T00:00Z,        NOT_BEFORE, true,  2000-01-01T00:00Z, 2000-03-31T00:00:00.001Z", // just within range

            "2000-01-03T04:00Z, --03T04:,    2000-02-03T04:00Z,        NOT_BEFORE, false, 2000-01-01T00:00Z, 2000-02-03T04:00Z       ", // outside range
            "2000-02-03T04:00Z, --03T04:,    2000-02-03T04:00Z,        NOT_BEFORE, true,  2000-01-01T00:00Z, 2000-02-03T04:00:00.001Z", // just within range
            "2000-01-31T00:00Z, --31T00:,    2000-03-31T00:00Z,        NOT_BEFORE, false, 2000-01-01T00:00Z, 2000-03-31T00:00Z       ", // outside range
            "2000-03-31T00:00Z, --31T00:,    2000-03-31T00:00Z,        NOT_BEFORE, true,  2000-01-01T00:00Z, 2000-03-31T00:00:00.001Z", // just within range

            "2000-01-03T00:00Z, --03T:,      2000-02-03T00:00Z,        NOT_BEFORE, false, 2000-01-01T00:00Z, 2000-02-03T00:00Z       ", // outside range
            "2000-02-03T00:00Z, --03T:,      2000-02-03T00:00Z,        NOT_BEFORE, true,  2000-01-01T00:00Z, 2000-02-03T00:00:00.001Z", // just within range
            "2000-01-31T00:00Z, --31T:,      2000-03-31T00:00Z,        NOT_BEFORE, false, 2000-01-01T00:00Z, 2000-03-31T00:00Z       ", // outside range
            "2000-03-31T00:00Z, --31T:,      2000-03-31T00:00Z,        NOT_BEFORE, true,  2000-01-01T00:00Z, 2000-03-31T00:00:00.001Z", // just within range

            "2000-02-02T04:05Z, --T04:05,    2000-02-03T04:05Z,        NOT_BEFORE, false, 2000-01-01T00:00Z, 2000-02-03T04:05Z       ", // outside range
            "2000-02-03T04:05Z, --T04:05,    2000-02-03T04:05Z,        NOT_BEFORE, true,  2000-01-01T00:00Z, 2000-02-03T04:05:00.001Z", // just within range

            "2000-02-02T04:00Z, --T04:,      2000-02-03T04:00Z,        NOT_BEFORE, false, 2000-01-01T00:00Z, 2000-02-03T04:00Z       ", // outside range
            "2000-02-03T04:00Z, --T04:,      2000-02-03T04:00Z,        NOT_BEFORE, true,  2000-01-01T00:00Z, 2000-02-03T04:00:00.001Z", // just within range

            "2000-02-03T03:05Z, --T:05,      2000-02-03T04:05Z,        NOT_BEFORE, false, 2000-01-01T00:00Z, 2000-02-03T04:05Z       ", // outside range
            "2000-02-03T04:05Z, --T:05,      2000-02-03T04:05Z,        NOT_BEFORE, true,  2000-01-01T00:00Z, 2000-02-03T04:05:00.001Z", // just within range

            "2000-02-03T04:04Z, --T:,        2000-02-03T04:05Z,        NOT_BEFORE, false, 2000-01-01T00:00Z, 2000-02-03T04:05Z       ", // outside range
            "2000-02-03T04:05Z, --T:,        2000-02-03T04:05Z,        NOT_BEFORE, true,  2000-01-01T00:00Z, 2000-02-03T04:05:00.001Z", // just within range

            // Reference before range
            "2000-01-02T03:04Z, --02T03:04,  1999-12-02T03:04Z,        NOT_BEFORE, true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable
            "2000-01-02T03:04Z, --02T03:04,  1999-12-02T03:04:00.001Z, NOT_BEFORE, true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just after
            "2000-01-02T03:04Z, --02T03:04,  1999-12-02T03:03:59.999Z, NOT_BEFORE, true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just before

            "2000-01-02T03:00Z, --02T03:,    1999-12-02T03:00Z,        NOT_BEFORE, true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // representable
            "2000-01-02T03:00Z, --02T03:,    1999-12-02T03:00:00.001Z, NOT_BEFORE, true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just after
            "2000-01-02T03:00Z, --02T03:,    1999-12-02T02:59:59.999Z, NOT_BEFORE, true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just before

            "2000-01-02T00:00Z, --02T:,      1999-12-02T00:00Z,        NOT_BEFORE, true,  2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // representable
            "2000-01-02T00:00Z, --02T:,      1999-12-02T00:00:00.001Z, NOT_BEFORE, true,  2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // just after
            "2000-01-02T00:00Z, --02T:,      1999-12-01T23:59:59.999Z, NOT_BEFORE, true,  2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // just before

            "2000-01-02T03:04Z, --T03:04,    2000-01-01T03:04Z,        NOT_BEFORE, true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable
            "2000-01-02T03:04Z, --T03:04,    2000-01-01T03:04:00.001Z, NOT_BEFORE, true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just after
            "2000-01-02T03:04Z, --T03:04,    2000-01-01T03:03:59.999Z, NOT_BEFORE, true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just before

            "2000-01-02T03:00Z, --T03:,      2000-01-01T03:00Z,        NOT_BEFORE, true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // representable
            "2000-01-02T03:00Z, --T03:,      2000-01-01T03:00:00.001Z, NOT_BEFORE, true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just after
            "2000-01-02T03:00Z, --T03:,      2000-01-01T02:59:59.999Z, NOT_BEFORE, true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just before

            "2000-01-02T03:04Z, --T:,        1999-12-02T03:04Z,        NOT_BEFORE, true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable

            // Reference after range
            "2000-01-02T03:04Z, --02T03:04,  2000-02-02T03:04Z,        NOT_BEFORE, false, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable
            "2000-01-02T03:04Z, --02T03:04,  2000-02-02T03:04:00.001Z, NOT_BEFORE, false, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just after
            "2000-01-02T03:04Z, --02T03:04,  2000-02-02T03:03:59.999Z, NOT_BEFORE, false, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just before

            "2000-01-02T03:00Z, --02T03:,    2000-02-02T03:00Z,        NOT_BEFORE, false, 2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // representable
            "2000-01-02T03:00Z, --02T03:,    2000-02-02T03:00:00.001Z, NOT_BEFORE, false, 2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just after
            "2000-01-02T03:00Z, --02T03:,    2000-02-02T02:59:59.999Z, NOT_BEFORE, false, 2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just before

            "2000-01-02T00:00Z, --02T:,      2000-02-02T00:00Z,        NOT_BEFORE, false, 2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // representable
            "2000-01-02T00:00Z, --02T:,      2000-02-02T00:00:00.001Z, NOT_BEFORE, false, 2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // just after
            "2000-01-02T00:00Z, --02T:,      2000-02-01T23:59:59.999Z, NOT_BEFORE, false, 2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // just before

            "2000-01-02T03:04Z, --T03:04,    2000-02-01T03:04Z,        NOT_BEFORE, false, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable
            "2000-01-02T03:04Z, --T03:04,    2000-02-01T03:04:00.001Z, NOT_BEFORE, false, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just after
            "2000-01-02T03:04Z, --T03:04,    2000-02-01T03:03:59.999Z, NOT_BEFORE, false, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just before

            "2000-01-02T03:00Z, --T03:,      2000-02-01T03:00Z,        NOT_BEFORE, false, 2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // representable
            "2000-01-02T03:00Z, --T03:,      2000-02-01T03:00:00.001Z, NOT_BEFORE, false, 2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just after
            "2000-01-02T03:00Z, --T03:,      2000-02-01T02:59:59.999Z, NOT_BEFORE, false, 2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just before

            "2000-01-02T03:04Z, --T:,        2000-02-02T03:04Z,        NOT_BEFORE, false, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable

            // BEFORE
            // Partial with all fields
            "2000-01-02T03:04Z, --02T03:04Z, 2000-01-02T03:04:00.001Z, BEFORE,     true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just after
            "2000-01-02T03:04Z, --02T03:04Z, 2000-01-02T03:03:59.999Z, BEFORE,     false, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just before
            "2000-01-01T00:00Z, --01T00:00Z, 1999-12-31T23:59:59.999Z, BEFORE,     false, 2000-01-01T00:00Z, 2000-01-01T00:00:00.001Z", // just before
            "2000-01-01T00:00Z, --01T00:00Z, 2000-01-16T11:59:59.999Z, BEFORE,     true,  2000-01-01T00:00Z, 2000-01-01T00:00:00.001Z", // maximal after
            "2000-02-01T00:00Z, --01T00:00Z, 2000-01-16T12:00Z,        BEFORE,     false, 2000-02-01T00:00Z, 2000-02-01T00:00:00.001Z", // minimal before
            "2000-01-01T00:00Z, --01T00:00Z, 1999-12-16T12:00Z,        BEFORE,     false, 2000-01-01T00:00Z, 2000-01-01T00:00:00.001Z", // minimal before
            "1999-12-01T00:00Z, --01T00:00Z, 1999-12-16T11:59:59.999Z, BEFORE,     true,  1999-12-01T00:00Z, 1999-12-01T00:00:00.001Z", // maximal after

            // Reference at range end
            "2000-01-03T04:05Z, --03T04:05,  2000-02-03T04:05Z,        BEFORE,     true,  2000-01-01T00:00Z, 2000-02-03T04:05Z       ", // outside range
            "2000-01-03T04:05Z, --03T04:05,  2000-02-03T04:05Z,        BEFORE,     true,  2000-01-01T00:00Z, 2000-02-03T04:05:00.001Z", // just within range
            "2000-01-31T00:00Z, --31T00:00,  2000-03-31T00:00Z,        BEFORE,     true,  2000-01-01T00:00Z, 2000-03-31T00:00Z       ", // outside range
            "2000-01-31T00:00Z, --31T00:00,  2000-03-31T00:00Z,        BEFORE,     true,  2000-01-01T00:00Z, 2000-03-31T00:00:00.001Z", // just within range

            "2000-01-03T04:00Z, --03T04:,    2000-02-03T04:00Z,        BEFORE,     true,  2000-01-01T00:00Z, 2000-02-03T04:00Z       ", // outside range
            "2000-01-03T04:00Z, --03T04:,    2000-02-03T04:00Z,        BEFORE,     true,  2000-01-01T00:00Z, 2000-02-03T04:00:00.001Z", // just within range
            "2000-01-31T00:00Z, --31T00:,    2000-03-31T00:00Z,        BEFORE,     true,  2000-01-01T00:00Z, 2000-03-31T00:00Z       ", // outside range
            "2000-01-31T00:00Z, --31T00:,    2000-03-31T00:00Z,        BEFORE,     true,  2000-01-01T00:00Z, 2000-03-31T00:00:00.001Z", // just within range

            "2000-01-03T00:00Z, --03T:,      2000-02-03T00:00Z,        BEFORE,     true,  2000-01-01T00:00Z, 2000-02-03T00:00Z       ", // outside range
            "2000-01-03T00:00Z, --03T:,      2000-02-03T00:00Z,        BEFORE,     true,  2000-01-01T00:00Z, 2000-02-03T00:00:00.001Z", // just within range
            "2000-01-31T00:00Z, --31T:,      2000-03-31T00:00Z,        BEFORE,     true,  2000-01-01T00:00Z, 2000-03-31T00:00Z       ", // outside range
            "2000-01-31T00:00Z, --31T:,      2000-03-31T00:00Z,        BEFORE,     true,  2000-01-01T00:00Z, 2000-03-31T00:00:00.001Z", // just within range

            "2000-02-02T04:05Z, --T04:05,    2000-02-03T04:05Z,        BEFORE,     true,  2000-01-01T00:00Z, 2000-02-03T04:05Z       ", // outside range
            "2000-02-02T04:05Z, --T04:05,    2000-02-03T04:05Z,        BEFORE,     true,  2000-01-01T00:00Z, 2000-02-03T04:05:00.001Z", // just within range

            "2000-02-02T04:00Z, --T04:,      2000-02-03T04:00Z,        BEFORE,     true,  2000-01-01T00:00Z, 2000-02-03T04:00Z       ", // outside range
            "2000-02-02T04:00Z, --T04:,      2000-02-03T04:00Z,        BEFORE,     true,  2000-01-01T00:00Z, 2000-02-03T04:00:00.001Z", // just within range

            "2000-02-03T03:05Z, --T:05,      2000-02-03T04:05Z,        BEFORE,     true,  2000-01-01T00:00Z, 2000-02-03T04:05Z       ", // outside range
            "2000-02-03T03:05Z, --T:05,      2000-02-03T04:05Z,        BEFORE,     true,  2000-01-01T00:00Z, 2000-02-03T04:05:00.001Z", // just within range

            "2000-02-03T04:04Z, --T:,        2000-02-03T04:05Z,        BEFORE,     true,  2000-01-01T00:00Z, 2000-02-03T04:05Z       ", // outside range
            "2000-02-03T04:04Z, --T:,        2000-02-03T04:05Z,        BEFORE,     true,  2000-01-01T00:00Z, 2000-02-03T04:05:00.001Z", // just within range

            // Reference before range
            "2000-01-02T03:04Z, --02T03:04,  1999-12-02T03:04Z,        BEFORE,     false, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable
            "2000-01-02T03:04Z, --02T03:04,  1999-12-02T03:04:00.001Z, BEFORE,     false, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just after
            "2000-01-02T03:04Z, --02T03:04,  1999-12-02T03:03:59.999Z, BEFORE,     false, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just before

            "2000-01-02T03:00Z, --02T03:,    1999-12-02T03:00Z,        BEFORE,     false, 2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // representable
            "2000-01-02T03:00Z, --02T03:,    1999-12-02T03:00:00.001Z, BEFORE,     false, 2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just after
            "2000-01-02T03:00Z, --02T03:,    1999-12-02T02:59:59.999Z, BEFORE,     false, 2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just before

            "2000-01-02T00:00Z, --02T:,      1999-12-02T00:00Z,        BEFORE,     false, 2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // representable
            "2000-01-02T00:00Z, --02T:,      1999-12-02T00:00:00.001Z, BEFORE,     false, 2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // just after
            "2000-01-02T00:00Z, --02T:,      1999-12-01T23:59:59.999Z, BEFORE,     false, 2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // just before

            "2000-01-02T03:04Z, --T03:04,    2000-01-01T03:04Z,        BEFORE,     false, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable
            "2000-01-02T03:04Z, --T03:04,    2000-01-01T03:04:00.001Z, BEFORE,     false, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just after
            "2000-01-02T03:04Z, --T03:04,    2000-01-01T03:03:59.999Z, BEFORE,     false, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just before

            "2000-01-02T03:00Z, --T03:,      2000-01-01T03:00Z,        BEFORE,     false, 2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // representable
            "2000-01-02T03:00Z, --T03:,      2000-01-01T03:00:00.001Z, BEFORE,     false, 2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just after
            "2000-01-02T03:00Z, --T03:,      2000-01-01T02:59:59.999Z, BEFORE,     false, 2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just before

            "2000-01-02T03:04Z, --T:,        1999-12-02T03:04Z,        BEFORE,     false, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable

            // Reference after range
            "2000-01-02T03:04Z, --02T03:04,  2000-02-02T03:04Z,        BEFORE,     true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable
            "2000-01-02T03:04Z, --02T03:04,  2000-02-02T03:04:00.001Z, BEFORE,     true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just after
            "2000-01-02T03:04Z, --02T03:04,  2000-02-02T03:03:59.999Z, BEFORE,     true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just before

            "2000-01-02T03:00Z, --02T03:,    2000-02-02T03:00Z,        BEFORE,     true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // representable
            "2000-01-02T03:00Z, --02T03:,    2000-02-02T03:00:00.001Z, BEFORE,     true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just after
            "2000-01-02T03:00Z, --02T03:,    2000-02-02T02:59:59.999Z, BEFORE,     true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just before

            "2000-01-02T00:00Z, --02T:,      2000-02-02T00:00Z,        BEFORE,     true,  2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // representable
            "2000-01-02T00:00Z, --02T:,      2000-02-02T00:00:00.001Z, BEFORE,     true,  2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // just after
            "2000-01-02T00:00Z, --02T:,      2000-02-01T23:59:59.999Z, BEFORE,     true,  2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // just before

            "2000-01-02T03:04Z, --T03:04,    2000-02-01T03:04Z,        BEFORE,     true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable
            "2000-01-02T03:04Z, --T03:04,    2000-02-01T03:04:00.001Z, BEFORE,     true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just after
            "2000-01-02T03:04Z, --T03:04,    2000-02-01T03:03:59.999Z, BEFORE,     true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just before

            "2000-01-02T03:00Z, --T03:,      2000-02-01T03:00Z,        BEFORE,     true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // representable
            "2000-01-02T03:00Z, --T03:,      2000-02-01T03:00:00.001Z, BEFORE,     true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just after
            "2000-01-02T03:00Z, --T03:,      2000-02-01T02:59:59.999Z, BEFORE,     true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just before

            "2000-01-02T03:04Z, --T:,        2000-02-02T03:04Z,        BEFORE,     true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable

            // NOT_AFTER
            // Partial with all fields
            "2000-01-02T03:04Z, --02T03:04Z, 2000-01-02T03:04Z,        NOT_AFTER,  true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // exactly
            "2000-01-02T03:04Z, --02T03:04Z, 2000-01-02T03:04:00.001Z, NOT_AFTER,  true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just after
            "2000-01-02T03:04Z, --02T03:04Z, 2000-01-02T03:03:59.999Z, NOT_AFTER,  false, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just before
            "2000-01-01T00:00Z, --01T00:00Z, 1999-12-31T23:59:59.999Z, NOT_AFTER,  false, 2000-01-01T00:00Z, 2000-01-01T00:00:00.001Z", // just before
            "2000-01-01T00:00Z, --01T00:00Z, 2000-01-16T11:59:59.999Z, NOT_AFTER,  true,  2000-01-01T00:00Z, 2000-01-01T00:00:00.001Z", // maximal after
            "2000-02-01T00:00Z, --01T00:00Z, 2000-01-16T12:00Z,        NOT_AFTER,  false, 2000-02-01T00:00Z, 2000-02-01T00:00:00.001Z", // minimal before
            "2000-01-01T00:00Z, --01T00:00Z, 1999-12-16T12:00Z,        NOT_AFTER,  false, 2000-01-01T00:00Z, 2000-01-01T00:00:00.001Z", // minimal before
            "1999-12-01T00:00Z, --01T00:00Z, 1999-12-16T11:59:59.999Z, NOT_AFTER,  true,  1999-12-01T00:00Z, 1999-12-01T00:00:00.001Z", // maximal after

            // Reference at range end
            "2000-01-03T04:05Z, --03T04:05,  2000-02-03T04:05Z,        NOT_AFTER,  true,  2000-01-01T00:00Z, 2000-02-03T04:05Z       ", // outside range
            "2000-02-03T04:05Z, --03T04:05,  2000-02-03T04:05Z,        NOT_AFTER,  true,  2000-01-01T00:00Z, 2000-02-03T04:05:00.001Z", // just within range
            "2000-01-31T00:00Z, --31T00:00,  2000-03-31T00:00Z,        NOT_AFTER,  true,  2000-01-01T00:00Z, 2000-03-31T00:00Z       ", // outside range
            "2000-03-31T00:00Z, --31T00:00,  2000-03-31T00:00Z,        NOT_AFTER,  true,  2000-01-01T00:00Z, 2000-03-31T00:00:00.001Z", // just within range

            "2000-01-03T04:00Z, --03T04:,    2000-02-03T04:00Z,        NOT_AFTER,  true,  2000-01-01T00:00Z, 2000-02-03T04:00Z       ", // outside range
            "2000-02-03T04:00Z, --03T04:,    2000-02-03T04:00Z,        NOT_AFTER,  true,  2000-01-01T00:00Z, 2000-02-03T04:00:00.001Z", // just within range
            "2000-01-31T00:00Z, --31T00:,    2000-03-31T00:00Z,        NOT_AFTER,  true,  2000-01-01T00:00Z, 2000-03-31T00:00Z       ", // outside range
            "2000-03-31T00:00Z, --31T00:,    2000-03-31T00:00Z,        NOT_AFTER,  true,  2000-01-01T00:00Z, 2000-03-31T00:00:00.001Z", // just within range

            "2000-01-03T00:00Z, --03T:,      2000-02-03T00:00Z,        NOT_AFTER,  true,  2000-01-01T00:00Z, 2000-02-03T00:00Z       ", // outside range
            "2000-02-03T00:00Z, --03T:,      2000-02-03T00:00Z,        NOT_AFTER,  true,  2000-01-01T00:00Z, 2000-02-03T00:00:00.001Z", // just within range
            "2000-01-31T00:00Z, --31T:,      2000-03-31T00:00Z,        NOT_AFTER,  true,  2000-01-01T00:00Z, 2000-03-31T00:00Z       ", // outside range
            "2000-03-31T00:00Z, --31T:,      2000-03-31T00:00Z,        NOT_AFTER,  true,  2000-01-01T00:00Z, 2000-03-31T00:00:00.001Z", // just within range

            "2000-02-02T04:05Z, --T04:05,    2000-02-03T04:05Z,        NOT_AFTER,  true,  2000-01-01T00:00Z, 2000-02-03T04:05Z       ", // outside range
            "2000-02-03T04:05Z, --T04:05,    2000-02-03T04:05Z,        NOT_AFTER,  true,  2000-01-01T00:00Z, 2000-02-03T04:05:00.001Z", // just within range

            "2000-02-02T04:00Z, --T04:,      2000-02-03T04:00Z,        NOT_AFTER,  true,  2000-01-01T00:00Z, 2000-02-03T04:00Z       ", // outside range
            "2000-02-03T04:00Z, --T04:,      2000-02-03T04:00Z,        NOT_AFTER,  true,  2000-01-01T00:00Z, 2000-02-03T04:00:00.001Z", // just within range

            "2000-02-03T03:05Z, --T:05,      2000-02-03T04:05Z,        NOT_AFTER,  true,  2000-01-01T00:00Z, 2000-02-03T04:05Z       ", // outside range
            "2000-02-03T04:05Z, --T:05,      2000-02-03T04:05Z,        NOT_AFTER,  true,  2000-01-01T00:00Z, 2000-02-03T04:05:00.001Z", // just within range

            "2000-02-03T04:04Z, --T:,        2000-02-03T04:05Z,        NOT_AFTER,  true,  2000-01-01T00:00Z, 2000-02-03T04:05Z       ", // outside range
            "2000-02-03T04:05Z, --T:,        2000-02-03T04:05Z,        NOT_AFTER,  true,  2000-01-01T00:00Z, 2000-02-03T04:05:00.001Z", // just within range

            // Reference before range
            "2000-01-02T03:04Z, --02T03:04,  1999-12-02T03:04Z,        NOT_AFTER,  false, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable
            "2000-01-02T03:04Z, --02T03:04,  1999-12-02T03:04:00.001Z, NOT_AFTER,  false, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just after
            "2000-01-02T03:04Z, --02T03:04,  1999-12-02T03:03:59.999Z, NOT_AFTER,  false, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just before

            "2000-01-02T03:00Z, --02T03:,    1999-12-02T03:00Z,        NOT_AFTER,  false, 2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // representable
            "2000-01-02T03:00Z, --02T03:,    1999-12-02T03:00:00.001Z, NOT_AFTER,  false, 2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just after
            "2000-01-02T03:00Z, --02T03:,    1999-12-02T02:59:59.999Z, NOT_AFTER,  false, 2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just before

            "2000-01-02T00:00Z, --02T:,      1999-12-02T00:00Z,        NOT_AFTER,  false, 2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // representable
            "2000-01-02T00:00Z, --02T:,      1999-12-02T00:00:00.001Z, NOT_AFTER,  false, 2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // just after
            "2000-01-02T00:00Z, --02T:,      1999-12-01T23:59:59.999Z, NOT_AFTER,  false, 2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // just before

            "2000-01-02T03:04Z, --T03:04,    2000-01-01T03:04Z,        NOT_AFTER,  false, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable
            "2000-01-02T03:04Z, --T03:04,    2000-01-01T03:04:00.001Z, NOT_AFTER,  false, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just after
            "2000-01-02T03:04Z, --T03:04,    2000-01-01T03:03:59.999Z, NOT_AFTER,  false, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just before

            "2000-01-02T03:00Z, --T03:,      2000-01-01T03:00Z,        NOT_AFTER,  false, 2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // representable
            "2000-01-02T03:00Z, --T03:,      2000-01-01T03:00:00.001Z, NOT_AFTER,  false, 2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just after
            "2000-01-02T03:00Z, --T03:,      2000-01-01T02:59:59.999Z, NOT_AFTER,  false, 2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just before

            "2000-01-02T03:04Z, --T:,        1999-12-02T03:04Z,        NOT_AFTER,  false, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable

            // Reference after range
            "2000-01-02T03:04Z, --02T03:04,  2000-02-02T03:04Z,        NOT_AFTER,  true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable
            "2000-01-02T03:04Z, --02T03:04,  2000-02-02T03:04:00.001Z, NOT_AFTER,  true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just after
            "2000-01-02T03:04Z, --02T03:04,  2000-02-02T03:03:59.999Z, NOT_AFTER,  true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just before

            "2000-01-02T03:00Z, --02T03:,    2000-02-02T03:00Z,        NOT_AFTER,  true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // representable
            "2000-01-02T03:00Z, --02T03:,    2000-02-02T03:00:00.001Z, NOT_AFTER,  true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just after
            "2000-01-02T03:00Z, --02T03:,    2000-02-02T02:59:59.999Z, NOT_AFTER,  true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just before

            "2000-01-02T00:00Z, --02T:,      2000-02-02T00:00Z,        NOT_AFTER,  true,  2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // representable
            "2000-01-02T00:00Z, --02T:,      2000-02-02T00:00:00.001Z, NOT_AFTER,  true,  2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // just after
            "2000-01-02T00:00Z, --02T:,      2000-02-01T23:59:59.999Z, NOT_AFTER,  true,  2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // just before

            "2000-01-02T03:04Z, --T03:04,    2000-02-01T03:04Z,        NOT_AFTER,  true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable
            "2000-01-02T03:04Z, --T03:04,    2000-02-01T03:04:00.001Z, NOT_AFTER,  true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just after
            "2000-01-02T03:04Z, --T03:04,    2000-02-01T03:03:59.999Z, NOT_AFTER,  true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just before

            "2000-01-02T03:00Z, --T03:,      2000-02-01T03:00Z,        NOT_AFTER,  true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // representable
            "2000-01-02T03:00Z, --T03:,      2000-02-01T03:00:00.001Z, NOT_AFTER,  true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just after
            "2000-01-02T03:00Z, --T03:,      2000-02-01T02:59:59.999Z, NOT_AFTER,  true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just before

            "2000-01-02T03:04Z, --T:,        2000-02-02T03:04Z,        NOT_AFTER,  true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable
    })
    @Test
    public void testToZonedDateTimeConditionRange(final String expected, final String partialDateTime, final String referenceTime,
            final ReferenceCondition condition, final boolean strictCondition, final String rangeStartInclusive, final String rangeEndExclusive) {
        testToZonedDateTimeConditionRange(ZonedDateTime.parse(expected), PartialDateTime.parse(partialDateTime), ZonedDateTime.parse(referenceTime), condition,
                strictCondition, ZonedDateTime.parse(rangeStartInclusive), ZonedDateTime.parse(rangeEndExclusive));
    }

    private void testToZonedDateTimeConditionRange(final ZonedDateTime expected, final PartialDateTime partialDateTime, final ZonedDateTime referenceTime,
            final ReferenceCondition condition, final boolean strictCondition, final ZonedDateTime rangeStartInclusive, final ZonedDateTime rangeEndExclusive) {
        assertThat(partialDateTime.toZonedDateTime(referenceTime, condition, strictCondition, rangeStartInclusive, rangeEndExclusive))//
                .as("%s.toZonedDateTime(%s, %s, %s, %s, %s)", partialDateTime, referenceTime, condition, strictCondition, rangeStartInclusive,
                        rangeEndExclusive)//
                .isEqualTo(expected);
    }

    @Parameters({//
            "--T:Z,       2000-01-02T03:04Z,        AFTER,      true,  2000-01-02T03:04Z, 2000-01-02T03:03Z       ", // invalid range

            // AFTER
            // Partial with all fields
            "--02T03:04Z, 2000-01-02T03:04Z,        AFTER,      true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // exactly
            "--02T03:04Z, 2000-01-02T03:04:00.001Z, AFTER,      true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just after
            "--01T00:00Z, 2000-01-16T11:59:59.999Z, AFTER,      true,  2000-01-01T00:00Z, 2000-01-01T00:00:00.001Z", // maximal after
            "--01T00:00Z, 1999-12-16T11:59:59.999Z, AFTER,      true,  1999-12-01T00:00Z, 1999-12-01T00:00:00.001Z", // maximal after

            // Reference at range end
            "--03T04:05,  2000-02-03T04:05Z,        AFTER,      true,  2000-01-01T00:00Z, 2000-02-03T04:05Z       ", // outside range
            "--03T04:05,  2000-02-03T04:05Z,        AFTER,      true,  2000-01-01T00:00Z, 2000-02-03T04:05:00.001Z", // just within range
            "--31T00:00,  2000-03-31T00:00Z,        AFTER,      true,  2000-01-01T00:00Z, 2000-03-31T00:00Z       ", // outside range
            "--31T00:00,  2000-03-31T00:00Z,        AFTER,      true,  2000-01-01T00:00Z, 2000-03-31T00:00:00.001Z", // just within range

            "--03T04:,    2000-02-03T04:00Z,        AFTER,      true,  2000-01-01T00:00Z, 2000-02-03T04:00Z       ", // outside range
            "--03T04:,    2000-02-03T04:00Z,        AFTER,      true,  2000-01-01T00:00Z, 2000-02-03T04:00:00.001Z", // just within range
            "--31T00:,    2000-03-31T00:00Z,        AFTER,      true,  2000-01-01T00:00Z, 2000-03-31T00:00Z       ", // outside range
            "--31T00:,    2000-03-31T00:00Z,        AFTER,      true,  2000-01-01T00:00Z, 2000-03-31T00:00:00.001Z", // just within range

            "--03T:,      2000-02-03T00:00Z,        AFTER,      true,  2000-01-01T00:00Z, 2000-02-03T00:00Z       ", // outside range
            "--03T:,      2000-02-03T00:00Z,        AFTER,      true,  2000-01-01T00:00Z, 2000-02-03T00:00:00.001Z", // just within range
            "--31T:,      2000-03-31T00:00Z,        AFTER,      true,  2000-01-01T00:00Z, 2000-03-31T00:00Z       ", // outside range
            "--31T:,      2000-03-31T00:00Z,        AFTER,      true,  2000-01-01T00:00Z, 2000-03-31T00:00:00.001Z", // just within range

            "--T04:05,    2000-02-03T04:05Z,        AFTER,      true,  2000-01-01T00:00Z, 2000-02-03T04:05Z       ", // outside range
            "--T04:05,    2000-02-03T04:05Z,        AFTER,      true,  2000-01-01T00:00Z, 2000-02-03T04:05:00.001Z", // just within range

            "--T04:,      2000-02-03T04:00Z,        AFTER,      true,  2000-01-01T00:00Z, 2000-02-03T04:00Z       ", // outside range
            "--T04:,      2000-02-03T04:00Z,        AFTER,      true,  2000-01-01T00:00Z, 2000-02-03T04:00:00.001Z", // just within range

            "--T:05,      2000-02-03T04:05Z,        AFTER,      true,  2000-01-01T00:00Z, 2000-02-03T04:05Z       ", // outside range
            "--T:05,      2000-02-03T04:05Z,        AFTER,      true,  2000-01-01T00:00Z, 2000-02-03T04:05:00.001Z", // just within range

            "--T:,        2000-02-03T04:05Z,        AFTER,      true,  2000-01-01T00:00Z, 2000-02-03T04:05Z       ", // outside range
            "--T:,        2000-02-03T04:05Z,        AFTER,      true,  2000-01-01T00:00Z, 2000-02-03T04:05:00.001Z", // just within range

            // Reference before range
            // <none>

            // Reference after range
            "--02T03:04,  2000-02-02T03:04Z,        AFTER,      true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable
            "--02T03:04,  2000-02-02T03:04:00.001Z, AFTER,      true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just after
            "--02T03:04,  2000-02-02T03:03:59.999Z, AFTER,      true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just before

            "--02T03:,    2000-02-02T03:00Z,        AFTER,      true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // representable
            "--02T03:,    2000-02-02T03:00:00.001Z, AFTER,      true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just after
            "--02T03:,    2000-02-02T02:59:59.999Z, AFTER,      true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just before

            "--02T:,      2000-02-02T00:00Z,        AFTER,      true,  2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // representable
            "--02T:,      2000-02-02T00:00:00.001Z, AFTER,      true,  2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // just after
            "--02T:,      2000-02-01T23:59:59.999Z, AFTER,      true,  2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // just before

            "--T03:04,    2000-02-01T03:04Z,        AFTER,      true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable
            "--T03:04,    2000-02-01T03:04:00.001Z, AFTER,      true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just after
            "--T03:04,    2000-02-01T03:03:59.999Z, AFTER,      true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just before

            "--T03:,      2000-02-01T03:00Z,        AFTER,      true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // representable
            "--T03:,      2000-02-01T03:00:00.001Z, AFTER,      true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just after
            "--T03:,      2000-02-01T02:59:59.999Z, AFTER,      true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just before

            "--T:,        2000-02-02T03:04Z,        AFTER,      true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable

            // NOT_BEFORE
            // Partial with all fields
            "--02T03:04Z, 2000-01-02T03:04:00.001Z, NOT_BEFORE, true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just after
            "--01T00:00Z, 2000-01-16T11:59:59.999Z, NOT_BEFORE, true,  2000-01-01T00:00Z, 2000-01-01T00:00:00.001Z", // maximal after
            "--01T00:00Z, 1999-12-16T11:59:59.999Z, NOT_BEFORE, true,  1999-12-01T00:00Z, 1999-12-01T00:00:00.001Z", // maximal after

            // Reference at range end
            "--03T04:05,  2000-02-03T04:05Z,        NOT_BEFORE, true,  2000-01-01T00:00Z, 2000-02-03T04:05Z       ", // outside range
            "--31T00:00,  2000-03-31T00:00Z,        NOT_BEFORE, true,  2000-01-01T00:00Z, 2000-03-31T00:00Z       ", // outside range

            "--03T04:,    2000-02-03T04:00Z,        NOT_BEFORE, true,  2000-01-01T00:00Z, 2000-02-03T04:00Z       ", // outside range
            "--31T00:,    2000-03-31T00:00Z,        NOT_BEFORE, true,  2000-01-01T00:00Z, 2000-03-31T00:00Z       ", // outside range

            "--03T:,      2000-02-03T00:00Z,        NOT_BEFORE, true,  2000-01-01T00:00Z, 2000-02-03T00:00Z       ", // outside range
            "--31T:,      2000-03-31T00:00Z,        NOT_BEFORE, true,  2000-01-01T00:00Z, 2000-03-31T00:00Z       ", // outside range

            "--T04:05,    2000-02-03T04:05Z,        NOT_BEFORE, true,  2000-01-01T00:00Z, 2000-02-03T04:05Z       ", // outside range

            "--T04:,      2000-02-03T04:00Z,        NOT_BEFORE, true,  2000-01-01T00:00Z, 2000-02-03T04:00Z       ", // outside range

            "--T:05,      2000-02-03T04:05Z,        NOT_BEFORE, true,  2000-01-01T00:00Z, 2000-02-03T04:05Z       ", // outside range

            "--T:,        2000-02-03T04:05Z,        NOT_BEFORE, true,  2000-01-01T00:00Z, 2000-02-03T04:05Z       ", // outside range

            // Reference before range
            // <none>

            // Reference after range
            "--02T03:04,  2000-02-02T03:04Z,        NOT_BEFORE, true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable
            "--02T03:04,  2000-02-02T03:04:00.001Z, NOT_BEFORE, true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just after
            "--02T03:04,  2000-02-02T03:03:59.999Z, NOT_BEFORE, true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just before

            "--02T03:,    2000-02-02T03:00Z,        NOT_BEFORE, true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // representable
            "--02T03:,    2000-02-02T03:00:00.001Z, NOT_BEFORE, true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just after
            "--02T03:,    2000-02-02T02:59:59.999Z, NOT_BEFORE, true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just before

            "--02T:,      2000-02-02T00:00Z,        NOT_BEFORE, true,  2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // representable
            "--02T:,      2000-02-02T00:00:00.001Z, NOT_BEFORE, true,  2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // just after
            "--02T:,      2000-02-01T23:59:59.999Z, NOT_BEFORE, true,  2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // just before

            "--T03:04,    2000-02-01T03:04Z,        NOT_BEFORE, true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable
            "--T03:04,    2000-02-01T03:04:00.001Z, NOT_BEFORE, true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just after
            "--T03:04,    2000-02-01T03:03:59.999Z, NOT_BEFORE, true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just before

            "--T03:,      2000-02-01T03:00Z,        NOT_BEFORE, true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // representable
            "--T03:,      2000-02-01T03:00:00.001Z, NOT_BEFORE, true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just after
            "--T03:,      2000-02-01T02:59:59.999Z, NOT_BEFORE, true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just before

            "--T:,        2000-02-02T03:04Z,        NOT_BEFORE, true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable

            // BEFORE
            // Partial with all fields
            "--02T03:04Z, 2000-01-02T03:04Z,        BEFORE,     true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // exactly
            "--02T03:04Z, 2000-01-02T03:03:59.999Z, BEFORE,     true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just before
            "--01T00:00Z, 1999-12-31T23:59:59.999Z, BEFORE,     true,  2000-01-01T00:00Z, 2000-01-01T00:00:00.001Z", // just before
            "--01T00:00Z, 2000-01-16T12:00Z,        BEFORE,     true,  2000-02-01T00:00Z, 2000-02-01T00:00:00.001Z", // minimal before
            "--01T00:00Z, 1999-12-16T12:00Z,        BEFORE,     true,  2000-01-01T00:00Z, 2000-01-01T00:00:00.001Z", // minimal before

            // Reference at range end
            // <none>

            // Reference before range
            "--02T03:04,  1999-12-02T03:04Z,        BEFORE,     true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable
            "--02T03:04,  1999-12-02T03:04:00.001Z, BEFORE,     true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just after
            "--02T03:04,  1999-12-02T03:03:59.999Z, BEFORE,     true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just before

            "--02T03:,    1999-12-02T03:00Z,        BEFORE,     true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // representable
            "--02T03:,    1999-12-02T03:00:00.001Z, BEFORE,     true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just after
            "--02T03:,    1999-12-02T02:59:59.999Z, BEFORE,     true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just before

            "--02T:,      1999-12-02T00:00Z,        BEFORE,     true,  2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // representable
            "--02T:,      1999-12-02T00:00:00.001Z, BEFORE,     true,  2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // just after
            "--02T:,      1999-12-01T23:59:59.999Z, BEFORE,     true,  2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // just before

            "--T03:04,    2000-01-01T03:04Z,        BEFORE,     true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable
            "--T03:04,    2000-01-01T03:04:00.001Z, BEFORE,     true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just after
            "--T03:04,    2000-01-01T03:03:59.999Z, BEFORE,     true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just before

            "--T03:,      2000-01-01T03:00Z,        BEFORE,     true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // representable
            "--T03:,      2000-01-01T03:00:00.001Z, BEFORE,     true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just after
            "--T03:,      2000-01-01T02:59:59.999Z, BEFORE,     true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just before

            "--T:,        1999-12-02T03:04Z,        BEFORE,     true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable

            // Reference after range
            // <none>

            // NOT_AFTER
            // Partial with all fields
            "--02T03:04Z, 2000-01-02T03:03:59.999Z, NOT_AFTER,  true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just before
            "--01T00:00Z, 1999-12-31T23:59:59.999Z, NOT_AFTER,  true,  2000-01-01T00:00Z, 2000-01-01T00:00:00.001Z", // just before
            "--01T00:00Z, 2000-01-16T12:00Z,        NOT_AFTER,  true,  2000-02-01T00:00Z, 2000-02-01T00:00:00.001Z", // minimal before
            "--01T00:00Z, 1999-12-16T12:00Z,        NOT_AFTER,  true,  2000-01-01T00:00Z, 2000-01-01T00:00:00.001Z", // minimal before

            // Reference at range end
            // <none>

            // Reference before range
            "--02T03:04,  1999-12-02T03:04Z,        NOT_AFTER,  true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable
            "--02T03:04,  1999-12-02T03:04:00.001Z, NOT_AFTER,  true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just after
            "--02T03:04,  1999-12-02T03:03:59.999Z, NOT_AFTER,  true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just before

            "--02T03:,    1999-12-02T03:00Z,        NOT_AFTER,  true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // representable
            "--02T03:,    1999-12-02T03:00:00.001Z, NOT_AFTER,  true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just after
            "--02T03:,    1999-12-02T02:59:59.999Z, NOT_AFTER,  true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just before

            "--02T:,      1999-12-02T00:00Z,        NOT_AFTER,  true,  2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // representable
            "--02T:,      1999-12-02T00:00:00.001Z, NOT_AFTER,  true,  2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // just after
            "--02T:,      1999-12-01T23:59:59.999Z, NOT_AFTER,  true,  2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // just before

            "--T03:04,    2000-01-01T03:04Z,        NOT_AFTER,  true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable
            "--T03:04,    2000-01-01T03:04:00.001Z, NOT_AFTER,  true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just after
            "--T03:04,    2000-01-01T03:03:59.999Z, NOT_AFTER,  true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just before

            "--T03:,      2000-01-01T03:00Z,        NOT_AFTER,  true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // representable
            "--T03:,      2000-01-01T03:00:00.001Z, NOT_AFTER,  true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just after
            "--T03:,      2000-01-01T02:59:59.999Z, NOT_AFTER,  true,  2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just before

            "--T:,        1999-12-02T03:04Z,        NOT_AFTER,  true,  2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable

            // Reference after range
            // <none>
    })
    @Test
    public void testToZonedDateTimeConditionRangeInvalid(final String partialDateTime, final String referenceTime, final ReferenceCondition condition,
            final boolean strictCondition, final String rangeStartInclusive, final String rangeEndExclusive) {
        testToZonedDateTimeConditionRangeInvalid(PartialDateTime.parse(partialDateTime), ZonedDateTime.parse(referenceTime), condition, strictCondition,
                ZonedDateTime.parse(rangeStartInclusive), ZonedDateTime.parse(rangeEndExclusive));
    }

    private void testToZonedDateTimeConditionRangeInvalid(final PartialDateTime partialDateTime, final ZonedDateTime referenceTime,
            final ReferenceCondition condition, final boolean strictCondition, final ZonedDateTime rangeStartInclusive, final ZonedDateTime rangeEndExclusive) {
        final Class<? extends Throwable> expectedException = DateTimeException.class;
        final ExpectedExceptionDescription description = new ExpectedExceptionDescription(expectedException);
        assertThatExceptionOfType(expectedException).as(description)//
                .isThrownBy(() -> description.setUnexpectedResult(
                        partialDateTime.toZonedDateTime(referenceTime, condition, strictCondition, rangeStartInclusive, rangeEndExclusive)))//
                .satisfies(exception -> {
                    if (PartialDateTime.DateTimeRanges.isValid(rangeStartInclusive, rangeEndExclusive)) {
                        assertThat(exception)//
                                .hasMessageContaining(partialDateTime.toString())//
                                .hasMessageContaining(referenceTime.toString());
                    }
                })//
                .withMessageContaining(rangeStartInclusive.toString())//
                .withMessageContaining(rangeEndExclusive.toString());
    }

    @Parameters({ //
            // Partial with all fields
            "2000-01-02T03:04Z, --02T03:04Z, 2000-01-02T03:04Z", // exactly expected
            "2000-01-02T03:04Z, --02T03:04Z, 2000-01-02T03:04:00.001Z", // just after expected
            "2000-01-02T03:04Z, --02T03:04Z, 2000-01-02T03:03:59.999Z", // just before expected
            "2000-01-01T00:00Z, --01T00:00Z, 1999-12-31T23:59:59.999Z", // just before expected
            "2000-01-01T00:00Z, --01T00:00Z, 2000-01-16T11:59:59.999Z", // maximal after expected
            "2000-02-01T00:00Z, --01T00:00Z, 2000-01-16T12:00Z", // minimal before expected
            "2000-01-01T00:00Z, --01T00:00Z, 1999-12-16T12:00Z", // minimal before expected
            "1999-12-01T00:00Z, --01T00:00Z, 1999-12-16T11:59:59.999Z", // maximal after expected

            // Partial with day only
            "2000-01-02T00:00Z, --02T:, 2000-01-02T00:00Z", //
            "2000-01-02T00:00Z, --02T:, 2000-01-02T00:00:00.001Z", //
            "2000-01-02T00:00Z, --02T:, 2000-01-01T23:59:59.999Z", //
            "2000-01-01T00:00Z, --01T:, 1999-12-31T23:59:59.999Z", //
            "2000-01-01T00:00Z, --01T:, 2000-01-16T11:59:59.999Z", //
            "2000-02-01T00:00Z, --01T:, 2000-01-16T12:00Z", //
            "2000-01-01T00:00Z, --01T:, 1999-12-16T12:00Z", //
            "1999-12-01T00:00Z, --01T:, 1999-12-16T11:59:59.999Z", //

            // Partial with hour, minute and zone
            "2000-01-01T03:04Z, --T03:04Z, 2000-01-01T03:04Z", //
            "2000-01-01T03:04Z, --T03:04Z, 2000-01-01T03:04:00.001Z", //
            "2000-01-01T03:04Z, --T03:04Z, 2000-01-01T03:03:59.999Z", //
            "2000-01-01T00:00Z, --T00:00Z, 1999-12-31T23:59:59.999Z", //
            "2000-01-01T00:00Z, --T00:00Z, 2000-01-01T11:59:59.999Z", //
            "2000-01-02T00:00Z, --T00:00Z, 2000-01-01T12:00Z", //
            "2000-01-01T00:00Z, --T00:00Z, 1999-12-31T12:00Z", //
            "1999-12-31T00:00Z, --T00:00Z, 1999-12-31T11:59:59.999Z", //

            // Partial with hour only
            "2000-01-01T03:00Z, --T03:, 2000-01-01T03:00Z", //
            "2000-01-01T03:00Z, --T03:, 2000-01-01T03:00:00.001Z", //
            "2000-01-01T03:00Z, --T03:, 2000-01-01T02:59:59.999Z", //
            "2000-01-01T00:00Z, --T00:, 1999-12-31T23:59:59.999Z", //
            "2000-01-01T00:00Z, --T00:, 2000-01-01T11:59:59.999Z", //
            "2000-01-02T00:00Z, --T00:, 2000-01-01T12:00Z", //
            "2000-01-01T00:00Z, --T00:, 1999-12-31T12:00Z", //
            "1999-12-31T00:00Z, --T00:, 1999-12-31T11:59:59.999Z", //

            // Partial with minute and zone only
            "2000-01-01T00:04Z, --T:04Z, 2000-01-01T00:04Z", //
            "2000-01-01T00:04Z, --T:04Z, 2000-01-01T00:04:00.001Z", //
            "2000-01-01T00:04Z, --T:04Z, 2000-01-01T00:03:59.999Z", //
            "2000-01-01T00:00Z, --T:00Z, 1999-12-31T23:59:59.999Z", //
            "2000-01-01T00:00Z, --T:00Z, 1999-12-31T23:59:59.999Z", //
            "2000-01-01T00:00Z, --T:00Z, 2000-01-01T00:29:59.999Z", //
            "2000-01-01T01:00Z, --T:00Z, 2000-01-01T00:30Z", //
            "2000-01-01T00:00Z, --T:00Z, 1999-12-31T23:30Z", //
            "1999-12-31T23:00Z, --T:00Z, 1999-12-31T23:29:59.999Z", //

            // Empty partial
            "2000-01-01T00:00Z, --T:, 2000-01-01T00:00Z", //
            "2000-01-01T00:00Z, --T:, 2000-01-01T00:00:00.001Z", //
            "2000-01-01T00:00Z, --T:, 1999-12-31T23:59:59.999Z", //
            "2000-01-01T00:00Z, --T:, 2000-01-01T00:00:29.999Z", //
            "2000-01-01T00:01Z, --T:, 2000-01-01T00:00:30Z", //
            "2000-01-01T00:00Z, --T:, 1999-12-31T23:59:30Z", //
            "1999-12-31T23:59Z, --T:, 1999-12-31T23:59:29.999Z", //

            // Midnight 00:00 on 28th of February on leap year and non-leap year
            "2000-02-29T00:00Z, --29T00:00Z, 2000-02-29T00:00Z", // exactly expected (leap year)
            "2000-02-29T00:00Z, --29T00:00Z, 2000-03-14T11:59:59.999Z", // maximal after expected (leap year)
            "2000-03-29T00:00Z, --29T00:00Z, 2000-03-14T12:00Z", //
            "2000-02-29T00:00Z, --29T00:00Z, 2000-02-13T12:00Z", // minimal before expected (leap year)
            "2000-01-29T00:00Z, --29T00:00Z, 2000-02-13T11:59:59.999Z", //
            "2001-03-29T00:00Z, --29T00:00Z, 2001-02-28T23:59:59.999Z", // falling to previous month (non-leap year)
            "2001-03-29T00:00Z, --29T00:00Z, 2001-03-01T00:00Z", // falling to next month (non-leap year)

            // Midnight 24:00 on 28th of February on leap year and non-leap year
            "2000-02-29T00:00Z, --28T24:00Z, 2000-02-28T00:00Z", //
            "2000-02-29T00:00Z, --28T24:00Z, 2000-02-29T00:00Z", //
            "2000-02-29T00:00Z, --28T24:00Z, 2000-02-29T00:00:00.001Z", //
            "2000-02-29T00:00Z, --28T24:00Z, 2000-02-28T23:59:59.999Z", //
            "2000-02-29T00:00Z, --28T24:00Z, 2000-03-14T11:59:59.999Z", //
            "2000-03-29T00:00Z, --28T24:00Z, 2000-03-14T12:00Z", //
            "2000-02-29T00:00Z, --28T24:00Z, 2000-02-13T12:00Z", //
            "2000-01-29T00:00Z, --28T24:00Z, 2000-02-13T11:59:59.999Z", //
            "2001-03-01T00:00Z, --28T24:00Z, 2001-02-28T23:59:59.999Z", // (non-leap year)
            "2001-03-01T00:00Z, --28T24:00Z, 2001-03-01T00:00Z", // (non-leap year)

            // Reference with non-UTC zone offset
            "2000-01-01T00:00Z, --T00:Z, 2000-01-01T00:00+01:00", //
            "2000-01-01T00:00Z, --T00:Z, 2000-01-01T12:59:59.999+01:00", //
            "2000-01-02T00:00Z, --T00:Z, 2000-01-01T13:00+01:00", //
            "2000-01-01T00:00Z, --T00:Z, 1999-12-31T13:00+01:00", //
            "1999-12-31T00:00Z, --T00:Z, 1999-12-31T12:59:59.999+01:00", //

            // Partial with non-UTC zone offset
            "2000-01-01T00:00+01:00, --T00:+01:00, 2000-01-01T00:00Z", //
            "2000-01-01T00:00+01:00, --T00:+01:00, 2000-01-01T10:59:59.999Z", //
            "2000-01-02T00:00+01:00, --T00:+01:00, 2000-01-01T11:00Z", //
            "2000-01-01T00:00+01:00, --T00:+01:00, 1999-12-31T11:00Z", //
            "1999-12-31T00:00+01:00, --T00:+01:00, 1999-12-31T10:59:59.999Z", //

    })
    @Test
    public void testToZonedDateTimeNear(final String expected, final String partialDateTime, final String referenceTime) {
        testToZonedDateTimeNear(ZonedDateTime.parse(expected), PartialDateTime.parse(partialDateTime), ZonedDateTime.parse(referenceTime));
    }

    private void testToZonedDateTimeNear(final ZonedDateTime expected, final PartialDateTime partialDateTime, final ZonedDateTime referenceTime) {
        assertThat(partialDateTime.toZonedDateTimeNear(referenceTime)).as("%s.toZonedDateTimeNear(%s)", partialDateTime, referenceTime).isEqualTo(expected);
    }

    @Parameters({ //
            "--32T00:, 2000-01-01T00:00Z", //
            "--T25:, 2000-01-01T00:00Z", //
            "--T00:61, 2000-01-01T00:00Z", //
    })
    @Test
    public void testToZonedDateTimeNearInvalid(final String partialDateTime, final String referenceTime) {
        testToZonedDateTimeNearInvalid(PartialDateTime.parse(partialDateTime), ZonedDateTime.parse(referenceTime));
    }

    private void testToZonedDateTimeNearInvalid(final PartialDateTime partialDateTime, final ZonedDateTime referenceTime) {
        final Class<? extends Throwable> expectedException = DateTimeException.class;
        final ExpectedExceptionDescription description = new ExpectedExceptionDescription(expectedException);
        assertThatExceptionOfType(expectedException).as(description)//
                .isThrownBy(() -> description.setUnexpectedResult(partialDateTime.toZonedDateTimeNear(referenceTime)))//
                .withMessageContaining(partialDateTime.toString())//
                .withMessageContaining(referenceTime.toString());
    }

    @Parameters({ //
            // Partial with all fields
            "2000-01-02T03:04Z, --02T03:04Z, 2000-01-02T03:04Z,        2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // exactly expected
            "2000-01-02T03:04Z, --02T03:04Z, 2000-01-02T03:04:00.001Z, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just after expected
            "2000-01-02T03:04Z, --02T03:04Z, 2000-01-02T03:03:59.999Z, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just before expected
            "2000-01-01T00:00Z, --01T00:00Z, 1999-12-31T23:59:59.999Z, 2000-01-01T00:00Z, 2000-01-01T00:00:00.001Z", // just before expected
            "2000-01-01T00:00Z, --01T00:00Z, 2000-01-16T11:59:59.999Z, 2000-01-01T00:00Z, 2000-01-01T00:00:00.001Z", // maximal after expected
            "2000-02-01T00:00Z, --01T00:00Z, 2000-01-16T12:00Z,        2000-02-01T00:00Z, 2000-02-01T00:00:00.001Z", // minimal before expected
            "2000-01-01T00:00Z, --01T00:00Z, 1999-12-16T12:00Z,        2000-01-01T00:00Z, 2000-01-01T00:00:00.001Z", // minimal before expected
            "1999-12-01T00:00Z, --01T00:00Z, 1999-12-16T11:59:59.999Z, 1999-12-01T00:00Z, 1999-12-01T00:00:00.001Z", // maximal after expected

            // Reference at range end
            "2000-01-03T04:05Z, --03T04:05,  2000-02-03T04:05Z,        2000-01-01T00:00Z, 2000-02-03T04:05Z       ", // outside range
            "2000-02-03T04:05Z, --03T04:05,  2000-02-03T04:05Z,        2000-01-01T00:00Z, 2000-02-03T04:05:00.001Z", // just within range
            "2000-01-31T00:00Z, --31T00:00,  2000-03-31T00:00Z,        2000-01-01T00:00Z, 2000-03-31T00:00Z       ", // outside range
            "2000-03-31T00:00Z, --31T00:00,  2000-03-31T00:00Z,        2000-01-01T00:00Z, 2000-03-31T00:00:00.001Z", // just within range

            "2000-01-03T04:00Z, --03T04:,    2000-02-03T04:00Z,        2000-01-01T00:00Z, 2000-02-03T04:00Z       ", // outside range
            "2000-02-03T04:00Z, --03T04:,    2000-02-03T04:00Z,        2000-01-01T00:00Z, 2000-02-03T04:00:00.001Z", // just within range
            "2000-01-31T00:00Z, --31T00:,    2000-03-31T00:00Z,        2000-01-01T00:00Z, 2000-03-31T00:00Z       ", // outside range
            "2000-03-31T00:00Z, --31T00:,    2000-03-31T00:00Z,        2000-01-01T00:00Z, 2000-03-31T00:00:00.001Z", // just within range

            "2000-01-03T00:00Z, --03T:,      2000-02-03T00:00Z,        2000-01-01T00:00Z, 2000-02-03T00:00Z       ", // outside range
            "2000-02-03T00:00Z, --03T:,      2000-02-03T00:00Z,        2000-01-01T00:00Z, 2000-02-03T00:00:00.001Z", // just within range
            "2000-01-31T00:00Z, --31T:,      2000-03-31T00:00Z,        2000-01-01T00:00Z, 2000-03-31T00:00Z       ", // outside range
            "2000-03-31T00:00Z, --31T:,      2000-03-31T00:00Z,        2000-01-01T00:00Z, 2000-03-31T00:00:00.001Z", // just within range

            "2000-02-02T04:05Z, --T04:05,    2000-02-03T04:05Z,        2000-01-01T00:00Z, 2000-02-03T04:05Z       ", // outside range
            "2000-02-03T04:05Z, --T04:05,    2000-02-03T04:05Z,        2000-01-01T00:00Z, 2000-02-03T04:05:00.001Z", // just within range

            "2000-02-02T04:00Z, --T04:,      2000-02-03T04:00Z,        2000-01-01T00:00Z, 2000-02-03T04:00Z       ", // outside range
            "2000-02-03T04:00Z, --T04:,      2000-02-03T04:00Z,        2000-01-01T00:00Z, 2000-02-03T04:00:00.001Z", // just within range

            "2000-02-03T03:05Z, --T:05,      2000-02-03T04:05Z,        2000-01-01T00:00Z, 2000-02-03T04:05Z       ", // outside range
            "2000-02-03T04:05Z, --T:05,      2000-02-03T04:05Z,        2000-01-01T00:00Z, 2000-02-03T04:05:00.001Z", // just within range

            "2000-02-03T04:04Z, --T:,        2000-02-03T04:05Z,        2000-01-01T00:00Z, 2000-02-03T04:05Z       ", // outside range
            "2000-02-03T04:05Z, --T:,        2000-02-03T04:05Z,        2000-01-01T00:00Z, 2000-02-03T04:05:00.001Z", // just within range

            // Reference before range
            "2000-01-02T03:04Z, --02T03:04,  1999-12-02T03:04Z,        2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable
            "2000-01-02T03:04Z, --02T03:04,  1999-12-02T03:04:00.001Z, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just after representable
            "2000-01-02T03:04Z, --02T03:04,  1999-12-02T03:03:59.999Z, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just before representable

            "2000-01-02T03:00Z, --02T03:,    1999-12-02T03:00Z,        2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // representable
            "2000-01-02T03:00Z, --02T03:,    1999-12-02T03:00:00.001Z, 2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just after representable
            "2000-01-02T03:00Z, --02T03:,    1999-12-02T02:59:59.999Z, 2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just before representable

            "2000-01-02T00:00Z, --02T:,      1999-12-02T00:00Z,        2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // representable
            "2000-01-02T00:00Z, --02T:,      1999-12-02T00:00:00.001Z, 2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // just after representable
            "2000-01-02T00:00Z, --02T:,      1999-12-01T23:59:59.999Z, 2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // just before representable

            "2000-01-02T03:04Z, --T03:04,    2000-01-01T03:04Z,        2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable
            "2000-01-02T03:04Z, --T03:04,    2000-01-01T03:04:00.001Z, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just after representable
            "2000-01-02T03:04Z, --T03:04,    2000-01-01T03:03:59.999Z, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just before representable

            "2000-01-02T03:00Z, --T03:,      2000-01-01T03:00Z,        2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // representable
            "2000-01-02T03:00Z, --T03:,      2000-01-01T03:00:00.001Z, 2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just after representable
            "2000-01-02T03:00Z, --T03:,      2000-01-01T02:59:59.999Z, 2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just before representable

            "2000-01-02T03:04Z, --T:,        1999-12-02T03:04Z,        2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable

            // Reference after range
            "2000-01-02T03:04Z, --02T03:04,  2000-02-02T03:04Z,        2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable
            "2000-01-02T03:04Z, --02T03:04,  2000-02-02T03:04:00.001Z, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just after representable
            "2000-01-02T03:04Z, --02T03:04,  2000-02-02T03:03:59.999Z, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just before representable

            "2000-01-02T03:00Z, --02T03:,    2000-02-02T03:00Z,        2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // representable
            "2000-01-02T03:00Z, --02T03:,    2000-02-02T03:00:00.001Z, 2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just after representable
            "2000-01-02T03:00Z, --02T03:,    2000-02-02T02:59:59.999Z, 2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just before representable

            "2000-01-02T00:00Z, --02T:,      2000-02-02T00:00Z,        2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // representable
            "2000-01-02T00:00Z, --02T:,      2000-02-02T00:00:00.001Z, 2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // just after representable
            "2000-01-02T00:00Z, --02T:,      2000-02-01T23:59:59.999Z, 2000-01-02T00:00Z, 2000-01-02T00:00:00.001Z", // just before representable

            "2000-01-02T03:04Z, --T03:04,    2000-02-01T03:04Z,        2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable
            "2000-01-02T03:04Z, --T03:04,    2000-02-01T03:04:00.001Z, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just after representable
            "2000-01-02T03:04Z, --T03:04,    2000-02-01T03:03:59.999Z, 2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // just before representable

            "2000-01-02T03:00Z, --T03:,      2000-02-01T03:00Z,        2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // representable
            "2000-01-02T03:00Z, --T03:,      2000-02-01T03:00:00.001Z, 2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just after representable
            "2000-01-02T03:00Z, --T03:,      2000-02-01T02:59:59.999Z, 2000-01-02T03:00Z, 2000-01-02T03:00:00.001Z", // just before representable

            "2000-01-02T03:04Z, --T:,        2000-02-02T03:04Z,        2000-01-02T03:04Z, 2000-01-02T03:04:00.001Z", // representable
    })
    @Test
    public void testToZonedDateTimeNearWithinRange(final String expected, final String partialDateTime, final String referenceTime,
            final String rangeStartInclusive, final String rangeEndExclusive) {
        testToZonedDateTimeNearWithinRange(ZonedDateTime.parse(expected), PartialDateTime.parse(partialDateTime), ZonedDateTime.parse(referenceTime),
                ZonedDateTime.parse(rangeStartInclusive), ZonedDateTime.parse(rangeEndExclusive));
    }

    public void testToZonedDateTimeNearWithinRange(final ZonedDateTime expected, final PartialDateTime partialDateTime, final ZonedDateTime referenceTime,
            final ZonedDateTime rangeStartInclusive, final ZonedDateTime rangeEndExclusive) {
        assertThat(partialDateTime.toZonedDateTimeNear(referenceTime, rangeStartInclusive, rangeEndExclusive))//
                .as("%s.toZonedDateTimeNear(%s, %s, %s)", partialDateTime, referenceTime, rangeStartInclusive, rangeEndExclusive)//
                .isEqualTo(expected);
    }

    @Parameters({ //
            // Invalid partial time
            "--32T00:,   2000-01-01T00:00Z, 2000-01-01T00:00Z,    2000-01-01T00:00:00.001Z", //
            "--T25:,     2000-01-01T00:00Z, 2000-01-01T00:00Z,    2000-01-01T00:00:00.001Z", //
            "--T00:61,   2000-01-01T00:00Z, 2000-01-01T00:00Z,    2000-01-01T00:00:00.001Z", //

            // Completable only outside range
            "--02T03:04, 2000-01-02T03:04Z, 2000-01-01T00:00Z,    2000-01-02T03:04Z       ", // Completable at exclusive end
            "--02T03:04, 2000-01-02T03:04Z, 2000-01-01T00:00Z,    2000-01-01T00:00:01Z    ", // Not completable within range

            "--T03:04,   2000-01-02T03:04Z, 2000-01-02T00:00Z,    2000-01-02T03:04Z       ", // Completable at exclusive end
            "--T03:04,   2000-01-02T03:04Z, 2000-01-01T00:00Z,    2000-01-01T00:00:01Z    ", // Not completable within range

            "--T:04,     2000-01-02T03:04Z, 2000-01-02T03:00Z,    2000-01-02T03:04Z       ", // Completable at exclusive end
            "--T:04,     2000-01-02T03:04Z, 2000-01-01T00:00Z,    2000-01-01T00:00:01Z    ", // Not completable within range

            "--T:,       2000-01-02T03:04Z, 2000-01-01T00:00:01Z, 2000-01-01T00:00:02Z    ", // Not completable within range

            "--T:,       2000-01-01T00:00Z, 2000-01-01T00:00Z,    2000-01-01T00:00Z       ", // Range end at range start
            "--T:,       2000-01-01T00:00Z, 2000-01-01T00:00:01Z, 2000-01-01T00:00:00Z    ", // Range end before range start
    })
    @Test
    public void testToZonedDateTimeNearWithinRangeInvalid(final String partialDateTime, final String referenceTime, final String rangeStartInclusive,
            final String rangeEndExclusive) {
        testToZonedDateTimeNearWithinRangeInvalid(PartialDateTime.parse(partialDateTime), ZonedDateTime.parse(referenceTime),
                ZonedDateTime.parse(rangeStartInclusive), ZonedDateTime.parse(rangeEndExclusive));
    }

    private void testToZonedDateTimeNearWithinRangeInvalid(final PartialDateTime partialDateTime, final ZonedDateTime referenceTime,
            final ZonedDateTime rangeStartInclusive, final ZonedDateTime rangeEndExclusive) {
        final Class<? extends Throwable> expectedException = DateTimeException.class;
        final ExpectedExceptionDescription description = new ExpectedExceptionDescription(expectedException);
        assertThatExceptionOfType(expectedException).as(description)//
                .isThrownBy(() -> description.setUnexpectedResult(partialDateTime.toZonedDateTimeNear(referenceTime, rangeStartInclusive, rangeEndExclusive)))//
                .satisfies(exception -> {
                    if (PartialDateTime.DateTimeRanges.isValid(rangeStartInclusive, rangeEndExclusive)) {
                        assertThat(exception)//
                                .hasMessageContaining(partialDateTime.toString())//
                                .hasMessageContaining(referenceTime.toString());
                    }
                })//
                .withMessageContaining(rangeStartInclusive.toString())//
                .withMessageContaining(rangeEndExclusive.toString());
    }

    @Test
    public void testToZonedDateTimeNearWithinMaxRange() {
        final ZoneOffset sourceZone = ZoneOffset.ofHours(6);
        final ZoneOffset targetZone = ZoneOffset.ofHours(-6);
        final ZonedDateTime reference = ZonedDateTime.of(2000, 1, 2, 3, 4, 0, 0, targetZone);
        final ZonedDateTime rangeStart = LocalDateTime.MIN.atZone(sourceZone);
        final ZonedDateTime rangeEnd = LocalDateTime.MAX.atZone(sourceZone);
        final PartialDateTime partialDateTime = PartialDateTime.of(reference, EnumSet.allOf(PartialField.class), true, PartialDateTime.MIDNIGHT_0_HOUR);

        // Different zones may cause range bounds over LocalDateTime.MIN/MAX. This should not cause completion fail.
        final ZonedDateTime result = partialDateTime.toZonedDateTimeNear(reference, rangeStart, rangeEnd);

        assertThat(result).as("%s in %s", reference, targetZone).isEqualTo(reference);
    }

    @Test
    public void testOfDayHourMinuteZoneInts() {
        final PartialDateTime partialDateTime = PartialDateTime.ofDayHourMinuteZone(TEST_DAY, TEST_HOUR, TEST_MINUTE, TEST_ZONE);

        assertThat(partialDateTime.getDay()).as("day").hasValue(TEST_DAY);
        assertThat(partialDateTime.getHour()).as("hour").hasValue(TEST_HOUR);
        assertThat(partialDateTime.getMinute()).as("minute").hasValue(TEST_MINUTE);
        assertThat(partialDateTime.getZone()).as("zone").hasValue(TEST_ZONE);
    }

    @Parameters({//
            "--02T03:04Z, 2000-01-02T03:04Z, false", //
            "--02T03:04Z, 2000-01-02T03:04Z, true", //
            "--02T00:00Z, 2000-01-02T00:00Z, false", //
            "--01T24:00Z, 2000-01-02T00:00Z, true", //
    })
    @Test
    public void testOfDayHourMinuteZoneTemporal(final String expected, final String zonedDateTime, final boolean midnight24h) {
        final ZonedDateTime dateTime = ZonedDateTime.parse(zonedDateTime);
        assertThat(PartialDateTime.ofDayHourMinuteZone(dateTime.toLocalDateTime(), midnight24h, dateTime.getZone())).isEqualTo(PartialDateTime.parse(expected));
    }

    @Parameters({//
            "--02T03:04Z, 2000-01-02T03:04Z, false", //
            "--02T03:04Z, 2000-01-02T03:04Z, true", //
            "--02T00:00Z, 2000-01-02T00:00Z, false", //
            "--01T24:00Z, 2000-01-02T00:00Z, true", //
    })
    @Test
    public void testOfDayHourMinuteZoneZonedDateTime(final String expected, final String zonedDateTime, final boolean midnight24h) {
        assertThat(PartialDateTime.ofDayHourMinuteZone(ZonedDateTime.parse(zonedDateTime), midnight24h)).isEqualTo(PartialDateTime.parse(expected));
    }

    @Test
    public void testOfDayHourMinuteInts() {
        assertThat(PartialDateTime.ofDayHourMinute(TEST_DAY, TEST_HOUR, TEST_MINUTE)).isEqualTo(PartialDateTime.parse("--02T03:04"));
    }

    @Parameters({//
            "--02T03:04, 2000-01-02T03:04, false", //
            "--02T03:04, 2000-01-02T03:04, true", //
            "--02T00:00, 2000-01-02T00:00, false", //
            "--01T24:00, 2000-01-02T00:00, true", //
    })
    @Test
    public void testOfDayHourMinuteTemporal(final String expected, final String zonedDateTime, final boolean midnight24h) {
        assertThat(PartialDateTime.ofDayHourMinute(LocalDateTime.parse(zonedDateTime), midnight24h)).isEqualTo(PartialDateTime.parse(expected));
    }

    @Test
    public void testOfDayHourInts() {
        assertThat(PartialDateTime.ofDayHour(TEST_DAY, TEST_HOUR)).isEqualTo(PartialDateTime.parse("--02T03:"));
    }

    @Parameters({//
            "--02T03:, 2000-01-02T03:04, false", //
            "--02T03:, 2000-01-02T03:04, true", //
            "--02T00:, 2000-01-02T00:00, false", //
            "--01T24:, 2000-01-02T00:00, true", //
    })
    @Test
    public void testOfDayHourTemporal(final String expected, final String zonedDateTime, final boolean midnight24h) {
        assertThat(PartialDateTime.ofDayHour(LocalDateTime.parse(zonedDateTime), midnight24h)).isEqualTo(PartialDateTime.parse(expected));
    }

    @Test
    public void testOfHourMinuteInts() {
        assertThat(PartialDateTime.ofHourMinute(TEST_HOUR, TEST_MINUTE)).isEqualTo(PartialDateTime.parse("--T03:04"));
    }

    @Parameters({//
            "--T03:04, 2000-01-02T03:04, false", //
            "--T03:04, 2000-01-02T03:04, true", //
            "--T00:00, 2000-01-02T00:00, false", //
            "--T24:00, 2000-01-02T00:00, true", //
    })
    @Test
    public void testOfHourMinuteTemporal(final String expected, final String zonedDateTime, final boolean midnight24h) {
        assertThat(PartialDateTime.ofHourMinute(LocalDateTime.parse(zonedDateTime), midnight24h)).isEqualTo(PartialDateTime.parse(expected));
    }

    @Test
    public void testOfHourInts() {
        assertThat(PartialDateTime.ofHour(TEST_HOUR)).isEqualTo(PartialDateTime.parse("--T03:"));
    }

    @Parameters({//
            "--T03:, 2000-01-02T03:04, false", //
            "--T03:, 2000-01-02T03:04, true", //
            "--T00:, 2000-01-02T00:00, false", //
            "--T24:, 2000-01-02T00:00, true", //
    })
    @Test
    public void testOfHourTemporal(final String expected, final String zonedDateTime, final boolean midnight24h) {
        assertThat(PartialDateTime.ofHour(LocalDateTime.parse(zonedDateTime), midnight24h)).isEqualTo(PartialDateTime.parse(expected));
    }

    @Parameters
    @Test
    public void testOfFieldInt(final PartialField field, final int value) {
        assertThat(PartialDateTime.of(field, value).get(field)).hasValue(value);
    }

    public List<Object[]> parametersForTestOfFieldInt() {
        return TEST_FIELD_VALUES.entrySet().stream()//
                .map(entry -> new Object[] { entry.getKey(), entry.getValue() })//
                .collect(Collectors.toList());
    }

    @Parameters(source = TestFieldValuesProvider.class)
    @Test
    public void testOfFieldsInts(final Map<PartialField, Integer> testFieldValues) {
        final PartialDateTime expected = createPartialDateTimeViaTACString(testFieldValues);
        final Set<PartialField> fields = testFieldValues.keySet();
        final int[] values = testFieldValues.values().stream().mapToInt(Integer::intValue).toArray();

        assertThat(PartialDateTime.of(fields, values)).isEqualTo(expected);
    }

    @Parameters({//
            "--T:, 2000-01-02T03:04:05.006Z, , false, 00", //
            "--02T:, 2000-01-02T03:04:05.006Z, DAY, false, 00", //
            "--T03:, 2000-01-02T03:04:05.006Z, HOUR, false, 00", //
            "--T:04, 2000-01-02T03:04:05.006Z, MINUTE, false, 00", //
            "--02T03:, 2000-01-02T03:04:05.006Z, DAY:HOUR, false, 00", //
            "--T03:04, 2000-01-02T03:04:05.006Z, HOUR:MINUTE, false, 00", //
            "--02T03:04, 2000-01-02T03:04:05.006Z, DAY:HOUR:MINUTE, false, 00", //

            "--T:Z, 2000-01-02T03:04:05.006Z, , true, 00", //
            "--02T:Z, 2000-01-02T03:04:05.006Z, DAY, true, 00", //
            "--T03:Z, 2000-01-02T03:04:05.006Z, HOUR, true, 00", //
            "--T:04Z, 2000-01-02T03:04:05.006Z, MINUTE, true, 00", //
            "--02T03:Z, 2000-01-02T03:04:05.006Z, DAY:HOUR, true, 00", //
            "--T03:04Z, 2000-01-02T03:04:05.006Z, HOUR:MINUTE, true, 00", //
            "--02T03:04Z, 2000-01-02T03:04:05.006Z, DAY:HOUR:MINUTE, true, 00", //

            "--T:Z, 2000-01-02T00:00Z, , true, 00", //
            "--02T:Z, 2000-01-02T00:00Z, DAY, true, 00", //
            "--T00:Z, 2000-01-02T00:00Z, HOUR, true, 00", //
            "--T:00Z, 2000-01-02T00:00Z, MINUTE, true, 00", //
            "--02T00:Z, 2000-01-02T00:00Z, DAY:HOUR, true, 00", //
            "--T00:00Z, 2000-01-02T00:00Z, HOUR:MINUTE, true, 00", //
            "--02T00:00Z, 2000-01-02T00:00Z, DAY:HOUR:MINUTE, true, 00", //

            "--T:Z, 2000-01-02T00:00Z, , true, 24", //
            "--01T:Z, 2000-01-02T00:00Z, DAY, true, 24", // day change on midnight 24h
            "--T24:Z, 2000-01-02T00:00Z, HOUR, true, 24", //
            "--T:00Z, 2000-01-02T00:00Z, MINUTE, true, 24", //
            "--01T24:Z, 2000-01-02T00:00Z, DAY:HOUR, true, 24", //
            "--T24:00Z, 2000-01-02T00:00Z, HOUR:MINUTE, true, 24", //
            "--01T24:00Z, 2000-01-02T00:00Z, DAY:HOUR:MINUTE, true, 24", //
    })
    @Test
    public void testOfZonedDateTime(final String expected, final String zonedDateTime, final String fields, final boolean useZone, final int midnightHour) {
        testOfZonedDateTime(//
                PartialDateTime.parse(expected), //
                ZonedDateTime.parse(zonedDateTime), //
                Stream.of(fields.split(":"))//
                        .filter(string -> !string.isEmpty())//
                        .map(PartialField::valueOf)//
                        .collect(Collectors.toSet()), //
                useZone, //
                midnightHour //
        );
    }

    private void testOfZonedDateTime(final PartialDateTime expected, final ZonedDateTime zonedDateTime, final Set<PartialField> fields, final boolean useZone,
            final int midnightHour) {
        assertThat(PartialDateTime.of(zonedDateTime, fields, useZone, midnightHour)).isEqualTo(expected);
    }

    @Parameters(source = PartialDateTimeStringProvider.class)
    @Test
    public void testParse(final String partialDateTimeString, final int day, final int hour, final int minute, final String zoneId) {
        final PartialDateTime partialDateTime = PartialDateTime.parse(partialDateTimeString);
        assertThat(partialDateTime.getDay().orElse(-1)).as("day").isEqualTo(day);
        assertThat(partialDateTime.getHour().orElse(-1)).as("hour").isEqualTo(hour);
        assertThat(partialDateTime.getMinute().orElse(-1)).as("minute").isEqualTo(minute);
        assertThat(partialDateTime.getZone()).as("zone").isEqualTo(createZone(zoneId));
    }

    @Parameters({ //
            "", //
            "02T03:04:Z", //
            "-02T03:04:Z", //
            "--02T", //
            "--02TZ", //
            "T03:04", //
            "T03:04:", //
            "T03:04:Z", //
            "2018-01-02T03:04:Z", //
            "-01-02T03:04:Z", //
            "--02T03:04:00Z", //
            "--b2T03:04:Z", //
            "--0bT03:04:Z", //
            "--02T03:04:00", //
            "--02T03:04:X" //
    })
    @Test
    public void testParseInvalid(final String partialDateTimeString) {
        final Class<DateTimeParseException> expectedException = DateTimeParseException.class;
        final ExpectedExceptionDescription description = new ExpectedExceptionDescription(expectedException);
        assertThatExceptionOfType(expectedException).as(description)//
                .isThrownBy(() -> description.setUnexpectedResult(PartialDateTime.parse(partialDateTimeString)))//
                .withMessageContaining(partialDateTimeString);
    }

    @Parameters(source = TACStringProvider.class)
    @Test
    public void testParseTACString(final String tacString, final int day, final int hour, final int minute, final String zoneId) {
        final Optional<ZoneId> zone = createZone(zoneId);
        final PartialDateTime partialDateTime = PartialDateTime.parseTACString(tacString, precision(day, hour, minute));
        assertThat(partialDateTime.getDay().orElse(-1)).as("day").isEqualTo(day);
        assertThat(partialDateTime.getHour().orElse(-1)).as("hour").isEqualTo(hour);
        assertThat(partialDateTime.getMinute().orElse(-1)).as("minute").isEqualTo(minute);
        assertThat(partialDateTime.getZone()).as("zone").isEqualTo(zone);
    }

    @Parameters({ //
            "--T:04:, 04, MINUTE", //
            "--T03:04:, 0304, MINUTE", //
            "--02T03:04:, 020304, MINUTE", //
            "--T03::, 03, HOUR", //
            "--02T03::, 0203, HOUR", //
            "--02T03:04:, 020304, HOUR", //
            "--02T::, 02, DAY", //
            "--02T03::, 0203, DAY", //
            "--02T03:04:, 020304, DAY", //
            "--T:04:Z, 04Z, MINUTE", //
            "--T03:04:Z, 0304Z, MINUTE", //
            "--02T03:04:Z, 020304Z, MINUTE", //
            "--T03::Z, 03Z, HOUR", //
            "--02T03::Z, 0203Z, HOUR", //
            "--02T03:04:Z, 020304Z, HOUR", //
            "--02T::Z, 02Z, DAY", //
            "--02T03::Z, 0203Z, DAY", //
            "--02T03:04:Z, 020304Z, DAY", //
    })
    @Test
    public void testParseTACStringWithPrecision(final String expectedPartialDateTimeString, final String tacString, final PartialField precision) {
        final PartialDateTime partialDateTime = PartialDateTime.parseTACString(tacString, precision);
        final PartialDateTime expected = PartialDateTime.parse(expectedPartialDateTimeString);
        assertThat(partialDateTime).isEqualTo(expected);
    }

    @Parameters({ "0, 0", "0, 0Z", "0, 2", "0, 2Z", "2, 000", "2, 000Z", "2, 023", "2, 023Z", "4, 00000", "4, 00000Z", "4, 02034", "4, 02034Z", "6, 02030405",
            "6, 02030405Z", "0, 0b", "0, b2", "2, 020b", "2, 02b3" })
    @Test
    public void testParseTACStringInvalid(final int errorIndex, final String tacString) {
        final Class<DateTimeParseException> expectedException = DateTimeParseException.class;
        final ExpectedExceptionDescription description = new ExpectedExceptionDescription(expectedException);
        assertThatExceptionOfType(expectedException).as(description)//
                .isThrownBy(() -> description.setUnexpectedResult(PartialDateTime.parseTACString(tacString, PartialField.MINUTE)))//
                .withMessageContaining(tacString)//
                .satisfies(exception -> assertThat(exception.getParsedString()).as("parsedString").isEqualTo(tacString))//
                .satisfies(exception -> assertThat(exception.getErrorIndex()).as("errorIndex").isEqualTo(errorIndex));
    }

    @Parameters(source = TACStringProvider.class)
    @Test
    public void testParseTACStringStrict(final String tacString, final int day, final int hour, final int minute, final String zoneId) {
        final Optional<ZoneId> zone = createZone(zoneId);
        final PartialDateTime partialDateTime = PartialDateTime.parseTACStringStrict(tacString, existingFields(day, hour, minute), zone.isPresent());
        assertThat(partialDateTime.getDay().orElse(-1)).as("day").isEqualTo(day);
        assertThat(partialDateTime.getHour().orElse(-1)).as("hour").isEqualTo(hour);
        assertThat(partialDateTime.getMinute().orElse(-1)).as("minute").isEqualTo(minute);
        assertThat(partialDateTime.getZone()).as("zone").isEqualTo(zone);
    }

    @Parameters({ //
            "0, , DAY, false", //
            "2, 02, DAY:HOUR, false", //
            "4, 0203, DAY:HOUR:MINUTE, false", //
            "0, 0203, DAY:MINUTE, false", // Uncontinuous fields
            "0, , , true", //
            "2, 02, DAY, true", //
            "4, 0203, DAY:HOUR, true", //
            "6, 020304, DAY:HOUR:MINUTE, true", //
            "0, Z, , false", //
            "2, 02Z, DAY, false", //
            "4, 0203Z, DAY:HOUR, false", //
            "6, 020304Z, DAY:HOUR:MINUTE, false", //
            "0, b20304Z, DAY:HOUR:MINUTE, true", //
            "0, 0b0304Z, DAY:HOUR:MINUTE, true", //
            "6, 02030405, DAY:HOUR:MINUTE, false", //
            "6, 02030405, DAY:HOUR:MINUTE, true", //
            "6, 02030405Z, DAY:HOUR:MINUTE, false", //
            "6, 02030405Z, DAY:HOUR:MINUTE, true", //
    })
    @Test
    public void testParseTACStringStrictInvalid(final int errorIndex, final String tacString, final String hasFieldsString, final boolean hasZone) {
        final EnumSet<PartialField> hasFields = Stream.of(hasFieldsString.split(":"))//
                .filter(fieldName -> !fieldName.isEmpty())//
                .map(PartialField::valueOf)//
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(PartialField.class)));
        final Class<DateTimeParseException> expectedException = DateTimeParseException.class;
        final ExpectedExceptionDescription description = new ExpectedExceptionDescription(expectedException);
        assertThatExceptionOfType(expectedException).as(description)//
                .isThrownBy(() -> description.setUnexpectedResult(PartialDateTime.parseTACStringStrict(tacString, hasFields, hasZone)))//
                .withMessageContaining(tacString)//
                .satisfies(exception -> assertThat(exception.getParsedString()).as("parsedString").isEqualTo(tacString))//
                .satisfies(exception -> assertThat(exception.getErrorIndex()).as("errorIndex").isEqualTo(errorIndex));
    }

    private static final class ExpectedExceptionDescription extends Description {
        private final Class<? extends Throwable> expectedException;
        private Object unexpectedResult;

        private ExpectedExceptionDescription(final Class<? extends Throwable> expectedException) {
            this.expectedException = expectedException;
        }

        @Override
        public String value() {
            return String.format(Locale.US, "Expected %s but got result: %s", expectedException, unexpectedResult);
        }

        public void setUnexpectedResult(final Object unexpectedResult) {
            this.unexpectedResult = unexpectedResult;
        }
    }

    public static final class TestFieldValuesProvider {
        public static List<Map<PartialField, Integer>> provideTestFieldValues() {
            return Sets.powerSet(TEST_FIELD_VALUES.keySet()).stream()//
                    .map(set -> Maps.immutableEnumMap(Maps.asMap(set, TEST_FIELD_VALUES::get)))//
                    .filter(map -> PartialDateTime.hasContinuousEnums(map.keySet()))//
                    .collect(Collectors.toList());
        }
    }

    public static final class PartialDateTimeStringProvider {
        public static List<Object[]> provideStrings() {
            return Arrays.asList(//
                    new Object[] { "--T:", -1, -1, -1, "" }, //
                    new Object[] { "--00T:", 0, -1, -1, "" }, //
                    new Object[] { "--T00:", -1, 0, -1, "" }, //
                    new Object[] { "--T:00", -1, -1, 0, "" }, //
                    new Object[] { "--02T:", 2, -1, -1, "" }, //
                    new Object[] { "--T03:", -1, 3, -1, "" }, //
                    new Object[] { "--T:04", -1, -1, 4, "" }, //
                    new Object[] { "--02T03:", 2, 3, -1, "" }, //
                    new Object[] { "--T03:04", -1, 3, 4, "" }, //
                    new Object[] { "--02T03:04", 2, 3, 4, "" }, //
                    new Object[] { "--T:Z", -1, -1, -1, "Z" }, //
                    new Object[] { "--02T:Z", 2, -1, -1, "Z" }, //
                    new Object[] { "--T03:Z", -1, 3, -1, "Z" }, //
                    new Object[] { "--T:04Z", -1, -1, 4, "Z" }, //
                    new Object[] { "--02T03:Z", 2, 3, -1, "Z" }, //
                    new Object[] { "--T03:04Z", -1, 3, 4, "Z" }, //
                    new Object[] { "--02T03:04Z", 2, 3, 4, "Z" }, //
                    new Object[] { "--02T03:04Z", 2, 3, 4, "+00:00" }, //
                    new Object[] { "--T:+01:00", -1, -1, -1, "+01:00" }, //
                    new Object[] { "--T:-01:00", -1, -1, -1, "-01:00" }, //
                    new Object[] { "--T:+00:01", -1, -1, -1, "+00:01" }, //
                    new Object[] { "--T:Europe/Helsinki", -1, -1, -1, "Europe/Helsinki" } //
            );
        }
    }

    public static final class TACStringProvider {
        public static List<Object[]> provideTACStrings() {
            return Arrays.asList(//
                    new Object[] { "", -1, -1, -1, "" }, //
                    new Object[] { "00", 0, -1, -1, "" }, //
                    new Object[] { "00", -1, 0, -1, "" }, //
                    new Object[] { "00", -1, -1, 0, "" }, //
                    new Object[] { "02", 2, -1, -1, "" }, //
                    new Object[] { "03", -1, 3, -1, "" }, //
                    new Object[] { "04", -1, -1, 4, "" }, //
                    new Object[] { "0203", 2, 3, -1, "" }, //
                    new Object[] { "0304", -1, 3, 4, "" }, //
                    new Object[] { "020304", 2, 3, 4, "" }, //
                    new Object[] { "Z", -1, -1, -1, "Z" }, //
                    new Object[] { "02Z", 2, -1, -1, "Z" }, //
                    new Object[] { "03Z", -1, 3, -1, "Z" }, //
                    new Object[] { "04Z", -1, -1, 4, "Z" }, //
                    new Object[] { "0203Z", 2, 3, -1, "Z" }, //
                    new Object[] { "0304Z", -1, 3, 4, "Z" }, //
                    new Object[] { "020304Z", 2, 3, 4, "Z" } //
            );
        }
    }
}
