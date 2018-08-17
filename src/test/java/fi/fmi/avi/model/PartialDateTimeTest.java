package fi.fmi.avi.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;
import com.google.common.testing.SerializableTester;

import fi.fmi.avi.model.PartialDateTime.PartialField;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@SuppressWarnings("Duplicates")
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

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private static Map<PartialField, Integer> createTestFieldValues() {
        final EnumMap<PartialField, Integer> map = new EnumMap<>(PartialField.class);
        map.put(PartialField.DAY, TEST_DAY);
        map.put(PartialField.HOUR, TEST_HOUR);
        map.put(PartialField.MINUTE, TEST_MINUTE);

        assertEquals(EnumSet.allOf(PartialField.class), map.keySet());

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
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(testFieldValues.keySet().toString());
        createPartialDateTime(testFieldValues);
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

        assertEquals("expected to contain fields that were set", testFieldValues.keySet(), partialDateTime.getPresentFields());
    }

    @Parameters(source = TestFieldValuesProvider.class)
    @Test
    public void testGet(final Map<PartialField, Integer> testFieldValues) {
        final PartialDateTime partialDateTime = createPartialDateTime(testFieldValues);

        testFieldValues//
                .forEach((field, value) -> assertEquals("expect existing field " + field, OptionalInt.of(value), partialDateTime.get(field)));
        complementOf(testFieldValues.keySet())//
                .forEach(field -> assertEquals("expect nonexistent field " + field, OptionalInt.empty(), partialDateTime.get(field)));
    }

    @Parameters(source = TestFieldValuesProvider.class)
    @Test
    public void testWith(final Map<PartialField, Integer> testFieldValues) {
        PartialDateTime partialDateTime = EMPTY_INSTANCE;
        for (final Map.Entry<PartialField, Integer> entry : testFieldValues.entrySet()) {
            partialDateTime = partialDateTime.with(entry.getKey(), entry.getValue());
        }
        final PartialDateTime expected = createPartialDateTime(testFieldValues);
        assertEquals(expected, partialDateTime);
        for (final Map.Entry<PartialField, Integer> entry : testFieldValues.entrySet()) {
            partialDateTime = partialDateTime.with(entry.getKey(), entry.getValue());
        }
        assertEquals(expected, partialDateTime);
    }

    @Parameters
    @Test
    public void testWithout(final Map<PartialField, Integer> testFieldValues) {
        final PartialDateTime partialDateTime = createPartialDateTime(testFieldValues);

        for (final PartialField field : partialDateTime.getPresentFields()) {
            final PartialDateTime withFieldCleared = partialDateTime.without(field);
            final EnumSet<PartialField> expectedFields = EnumSet.copyOf(partialDateTime.getPresentFields());
            expectedFields.remove(field);

            assertFalse(field.toString(), withFieldCleared.get(field).isPresent());
            assertEquals(field.toString(), expectedFields, withFieldCleared.getPresentFields());
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
        assertEquals(OptionalInt.empty(), EMPTY_INSTANCE.getMinute());
    }

    @Test
    public void testGetMinute() {
        final PartialField field = PartialField.MINUTE;
        final int value = TEST_FIELD_VALUES.get(field);
        final PartialDateTime partialDateTime = EMPTY_INSTANCE.with(field, value);
        assertEquals(value, partialDateTime.getMinute().orElseThrow(() -> new AssertionError("empty " + field)));
    }

    @Test
    public void testWithMinute() {
        final PartialField field = PartialField.MINUTE;
        final int value = TEST_FIELD_VALUES.get(field);
        final PartialDateTime partialDateTime = EMPTY_INSTANCE.withMinute(value);
        assertEquals(value, partialDateTime.get(field).orElseThrow(() -> new AssertionError("empty " + field)));
        assertEquals(partialDateTime, partialDateTime.withMinute(value));
    }

    @Test
    public void testWithoutMinute() {
        final PartialField field = PartialField.MINUTE;
        assertFalse(field.toString(), SAMPLE_INSTANCE.withoutMinute().get(field).isPresent());
    }

    @Test
    public void testGetHourEmpty() {
        assertEquals(OptionalInt.empty(), EMPTY_INSTANCE.getHour());
    }

    @Test
    public void testGetHour() {
        final PartialField field = PartialField.HOUR;
        final int value = TEST_FIELD_VALUES.get(field);
        final PartialDateTime partialDateTime = EMPTY_INSTANCE.with(field, value);
        assertEquals(value, partialDateTime.getHour().orElseThrow(() -> new AssertionError("empty " + field)));
    }

    @Test
    public void testWithHour() {
        final PartialField field = PartialField.HOUR;
        final int value = TEST_FIELD_VALUES.get(field);
        final PartialDateTime partialDateTime = EMPTY_INSTANCE.withHour(value);
        assertEquals(value, partialDateTime.get(field).orElseThrow(() -> new AssertionError("empty " + field)));
        assertEquals(partialDateTime, partialDateTime.withHour(value));
    }

    @Test
    public void testWithoutHour() {
        final PartialField field = PartialField.HOUR;
        final PartialDateTime partialDateTime = SAMPLE_INSTANCE.without(PartialField.DAY);
        assertFalse(field.toString(), partialDateTime.withoutHour().get(field).isPresent());
    }

    @Test
    public void testGetDayEmpty() {
        assertEquals(OptionalInt.empty(), EMPTY_INSTANCE.getDay());
    }

    @Test
    public void testGetDay() {
        final PartialField field = PartialField.DAY;
        final int value = TEST_FIELD_VALUES.get(field);
        final PartialDateTime partialDateTime = EMPTY_INSTANCE.with(field, value);
        assertEquals(value, partialDateTime.getDay().orElseThrow(() -> new AssertionError("empty " + field)));
    }

    @Test
    public void testWithDay() {
        final PartialField field = PartialField.DAY;
        final int value = TEST_FIELD_VALUES.get(field);
        final PartialDateTime partialDateTime = EMPTY_INSTANCE.withDay(value);
        assertEquals(value, partialDateTime.get(field).orElseThrow(() -> new AssertionError("empty " + field)));
        assertEquals(partialDateTime, partialDateTime.withDay(value));
    }

    @Test
    public void testWithoutDay() {
        final PartialField field = PartialField.DAY;
        assertFalse(field.toString(), SAMPLE_INSTANCE.withoutDay().get(field).isPresent());
    }

    @Test
    public void testGetZoneEmpty() {
        assertEquals(Optional.empty(), EMPTY_INSTANCE.getZone());
    }

    @Test
    public void testGetZone() {
        final ZoneId zone = ZoneOffset.UTC;
        final PartialDateTime partialDateTime = EMPTY_INSTANCE.withZone(zone);
        assertEquals(zone, partialDateTime.getZone().orElseThrow(() -> new AssertionError("empty zone")));
    }

    @Test
    public void testWithoutZone() {
        assertFalse(SAMPLE_INSTANCE.withoutZone().getZone().isPresent());
    }

    @Parameters({ //
            "true, --T24:0", "true, --T24:", "true, --T0:0", "true, --T0:", //
            "false, --T:", "false, --T23:0", "false, --T23:", "false, --T24:1", "false, --T:0", "false, --T25:0", "false, --T25:" //
    })
    @Test
    public void testIsMidnight(final boolean expectedResult, final String partialDateTimeString) {
        final PartialDateTime partialDateTime = PartialDateTime.parse(partialDateTimeString);
        assertEquals(partialDateTime.toString(), expectedResult, partialDateTime.isMidnight());
    }

    @Parameters({ //
            "true, --T24:0", "true, --T24:", //
            "false, --T0:0", "false, --T0:", "false, --T23:0", "false, --T23:", "false, --T24:1", "false, --T:0", "false, --T25:0", "false, --T25:" //
    })
    @Test
    public void testIsMidnight24h(final boolean expectedResult, final String partialDateTimeString) {
        final PartialDateTime partialDateTime = PartialDateTime.parse(partialDateTimeString);
        assertEquals(partialDateTime.toString(), expectedResult, partialDateTime.isMidnight24h());
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
        assertEquals(String.format("%s.withMidnight00h(%s)", partialDateTime, reference), expected, partialDateTime.withMidnight00h(reference));
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
        assertEquals(String.format("%s.withMidnight24h(%s)", partialDateTime, reference), expected, partialDateTime.withMidnight24h(reference));
    }

    @Parameters
    @Test
    public void testRepresents(final boolean expected, final PartialDateTime partialDateTime, final Temporal temporal) {
        assertEquals(String.format("%s.represents(%s)", partialDateTime, temporal), expected, partialDateTime.represents(temporal));
    }

    public List<Object[]> parametersForTestRepresents() {
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
        assertEquals(expected, partialDateTime.toTACString());
    }

    @Parameters(source = PartialDateTimeStringProvider.class)
    @Test
    public void testToString(final String expected, final int day, final int hour, final int minute, final String zoneId) {
        final PartialDateTime partialDateTime = createPartialDateTime(day, hour, minute, zoneId);
        assertEquals(expected, partialDateTime.toString());
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
        assertEquals(String.format("%s.toZonedDateTimeNear(%s)", partialDateTime, issueDate), expected, partialDateTime.toZonedDateTime(issueDate));
    }

    @Parameters({ //
            "--T00:, 2000-01, java.lang.IllegalStateException", //
            "--T:00, 2000-01, java.lang.IllegalStateException", //
            "--32T00:, 2000-01, java.time.DateTimeException", //
            "--01T25:, 2000-01, java.time.DateTimeException", //
            "--01T00:61, 2000-01, java.time.DateTimeException", //
            "--29T00:00Z, 2001-02, java.time.DateTimeException", // non-leap year
            "--29T24:00Z, 2001-02, java.time.DateTimeException", // non-leap year
    })
    @Test
    public void testToZonedDateTimeYearMonthInvalid(final String partialDateTime, final String issueDate, final Class<? extends Throwable> expectedException) {
        testToZonedDateTimeYearMonthInvalid(PartialDateTime.parse(partialDateTime), YearMonth.parse(issueDate), expectedException);
    }

    private void testToZonedDateTimeYearMonthInvalid(final PartialDateTime partialDateTime, final YearMonth issueDate,
            final Class<? extends Throwable> expectedException) {
        thrown.expect(expectedException);
        thrown.expectMessage(partialDateTime.toString());
        if (DateTimeException.class.equals(expectedException)) {
            thrown.expectMessage(issueDate.toString());
        }
        final ZonedDateTime result = partialDateTime.toZonedDateTime(issueDate);
        fail(String.format("Expected %s but got result: %s", expectedException, result));
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
        assertEquals(String.format("%s.toZonedDateTimeNear(%s)", partialDateTime, issueDate), expected, partialDateTime.toZonedDateTime(issueDate));
    }

    @Parameters({ //
            "--T:00, 2000-01-01, java.lang.IllegalStateException", //
            "--32T00:, 2000-01-01, java.time.DateTimeException", //
            "--T25:, 2000-01-01, java.time.DateTimeException", //
            "--T00:61, 2000-01-01, java.time.DateTimeException", //
            "--29T00:00Z, 2001-02-28, java.time.DateTimeException", // non-leap year
            "--29T24:00Z, 2001-02-28, java.time.DateTimeException", // non-leap year
    })
    @Test
    public void testToZonedDateTimeLocalDateInvalid(final String partialDateTime, final String issueDate, final Class<? extends Throwable> expectedException) {
        testToZonedDateTimeLocalDateInvalid(PartialDateTime.parse(partialDateTime), LocalDate.parse(issueDate), expectedException);
    }

    private void testToZonedDateTimeLocalDateInvalid(final PartialDateTime partialDateTime, final LocalDate issueDate,
            final Class<? extends Throwable> expectedException) {
        thrown.expect(expectedException);
        thrown.expectMessage(partialDateTime.toString());
        if (DateTimeException.class.equals(expectedException)) {
            thrown.expectMessage(issueDate.toString());
        }
        final ZonedDateTime result = partialDateTime.toZonedDateTime(issueDate);
        fail(String.format("Expected %s but got result: %s", expectedException, result));
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
        assertEquals(String.format("%s.toZonedDateTimeNear(%s)", partialDateTime, referenceTime), expected, partialDateTime.toZonedDateTimeNear(referenceTime));
    }

    @Parameters({ //
            "--32T00:, 2000-01-01T00:00Z, java.time.DateTimeException", //
            "--T25:, 2000-01-01T00:00Z, java.time.DateTimeException", //
            "--T00:61, 2000-01-01T00:00Z, java.time.DateTimeException", //
    })
    @Test
    public void testToZonedDateTimeNearInvalid(final String partialDateTime, final String referenceTime, final Class<? extends Throwable> expectedException) {
        testToZonedDateTimeNearInvalid(PartialDateTime.parse(partialDateTime), ZonedDateTime.parse(referenceTime), expectedException);
    }

    private void testToZonedDateTimeNearInvalid(final PartialDateTime partialDateTime, final ZonedDateTime referenceTime,
            final Class<? extends Throwable> expectedException) {
        thrown.expect(expectedException);
        thrown.expectMessage(partialDateTime.toString());
        if (DateTimeException.class.equals(expectedException)) {
            thrown.expectMessage(referenceTime.toString());
        }
        final ZonedDateTime result = partialDateTime.toZonedDateTimeNear(referenceTime);
        fail(String.format("Expected %s but got result: %s", expectedException, result));
    }

    @Test
    public void testOfDayHourMinuteZoneInts() {
        final PartialDateTime partialDateTime = PartialDateTime.ofDayHourMinuteZone(TEST_DAY, TEST_HOUR, TEST_MINUTE, TEST_ZONE);

        assertEquals("day", OptionalInt.of(TEST_DAY), partialDateTime.getDay());
        assertEquals("hour", OptionalInt.of(TEST_HOUR), partialDateTime.getHour());
        assertEquals("minute", OptionalInt.of(TEST_MINUTE), partialDateTime.getMinute());
        assertEquals("zone", Optional.of(TEST_ZONE), partialDateTime.getZone());
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
        assertEquals(PartialDateTime.parse(expected), PartialDateTime.ofDayHourMinuteZone(dateTime.toLocalDateTime(), midnight24h, dateTime.getZone()));
    }

    @Parameters({//
            "--02T03:04Z, 2000-01-02T03:04Z, false", //
            "--02T03:04Z, 2000-01-02T03:04Z, true", //
            "--02T00:00Z, 2000-01-02T00:00Z, false", //
            "--01T24:00Z, 2000-01-02T00:00Z, true", //
    })
    @Test
    public void testOfDayHourMinuteZoneZonedDateTime(final String expected, final String zonedDateTime, final boolean midnight24h) {
        assertEquals(PartialDateTime.parse(expected), PartialDateTime.ofDayHourMinuteZone(ZonedDateTime.parse(zonedDateTime), midnight24h));
    }

    @Test
    public void testOfDayHourMinuteInts() {
        assertEquals(PartialDateTime.parse("--02T03:04"), PartialDateTime.ofDayHourMinute(TEST_DAY, TEST_HOUR, TEST_MINUTE));
    }

    @Parameters({//
            "--02T03:04, 2000-01-02T03:04, false", //
            "--02T03:04, 2000-01-02T03:04, true", //
            "--02T00:00, 2000-01-02T00:00, false", //
            "--01T24:00, 2000-01-02T00:00, true", //
    })
    @Test
    public void testOfDayHourMinuteTemporal(final String expected, final String zonedDateTime, final boolean midnight24h) {
        assertEquals(PartialDateTime.parse(expected), PartialDateTime.ofDayHourMinute(LocalDateTime.parse(zonedDateTime), midnight24h));
    }

    @Test
    public void testOfDayHourInts() {
        assertEquals(PartialDateTime.parse("--02T03:"), PartialDateTime.ofDayHour(TEST_DAY, TEST_HOUR));
    }

    @Parameters({//
            "--02T03:, 2000-01-02T03:04, false", //
            "--02T03:, 2000-01-02T03:04, true", //
            "--02T00:, 2000-01-02T00:00, false", //
            "--01T24:, 2000-01-02T00:00, true", //
    })
    @Test
    public void testOfDayHourTemporal(final String expected, final String zonedDateTime, final boolean midnight24h) {
        assertEquals(PartialDateTime.parse(expected), PartialDateTime.ofDayHour(LocalDateTime.parse(zonedDateTime), midnight24h));
    }

    @Test
    public void testOfHourMinuteInts() {
        assertEquals(PartialDateTime.parse("--T03:04"), PartialDateTime.ofHourMinute(TEST_HOUR, TEST_MINUTE));
    }

    @Parameters({//
            "--T03:04, 2000-01-02T03:04, false", //
            "--T03:04, 2000-01-02T03:04, true", //
            "--T00:00, 2000-01-02T00:00, false", //
            "--T24:00, 2000-01-02T00:00, true", //
    })
    @Test
    public void testOfHourMinuteTemporal(final String expected, final String zonedDateTime, final boolean midnight24h) {
        assertEquals(PartialDateTime.parse(expected), PartialDateTime.ofHourMinute(LocalDateTime.parse(zonedDateTime), midnight24h));
    }

    @Test
    public void testOfHourInts() {
        assertEquals(PartialDateTime.parse("--T03:"), PartialDateTime.ofHour(TEST_HOUR));
    }

    @Parameters({//
            "--T03:, 2000-01-02T03:04, false", //
            "--T03:, 2000-01-02T03:04, true", //
            "--T00:, 2000-01-02T00:00, false", //
            "--T24:, 2000-01-02T00:00, true", //
    })
    @Test
    public void testOfHourTemporal(final String expected, final String zonedDateTime, final boolean midnight24h) {
        assertEquals(PartialDateTime.parse(expected), PartialDateTime.ofHour(LocalDateTime.parse(zonedDateTime), midnight24h));
    }

    @Parameters
    @Test
    public void testOfFieldInt(final PartialField field, final int value) {
        assertEquals(OptionalInt.of(value), PartialDateTime.of(field, value).get(field));
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

        assertEquals(expected, PartialDateTime.of(fields, values));
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
        assertEquals(expected, PartialDateTime.of(zonedDateTime, fields, useZone, midnightHour));
    }

    @Parameters(source = PartialDateTimeStringProvider.class)
    @Test
    public void testParse(final String partialDateTimeString, final int day, final int hour, final int minute, final String zoneId) {
        final PartialDateTime partialDateTime = PartialDateTime.parse(partialDateTimeString);
        assertEquals("day", day, partialDateTime.getDay().orElse(-1));
        assertEquals("hour", hour, partialDateTime.getHour().orElse(-1));
        assertEquals("minute", minute, partialDateTime.getMinute().orElse(-1));
        assertEquals("zone", createZone(zoneId), partialDateTime.getZone());
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
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(partialDateTimeString);
        PartialDateTime.parse(partialDateTimeString);
    }

    @Parameters(source = TACStringProvider.class)
    @Test
    public void testParseTACString(final String tacString, final int day, final int hour, final int minute, final String zoneId) {
        final Optional<ZoneId> zone = createZone(zoneId);
        final PartialDateTime partialDateTime = PartialDateTime.parseTACString(tacString, precision(day, hour, minute));
        assertEquals("day", day, partialDateTime.getDay().orElse(-1));
        assertEquals("hour", hour, partialDateTime.getHour().orElse(-1));
        assertEquals("minute", minute, partialDateTime.getMinute().orElse(-1));
        assertEquals("zone", zone, partialDateTime.getZone());
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
        assertEquals(expected, partialDateTime);
    }

    @Parameters({ "0", "0Z", "2", "2Z", "000", "000Z", "023", "023Z", "00000", "00000Z", "02034", "02034Z", "02030405", "02030405Z", "0b", "b2", "020b",
            "02b3" })
    @Test
    public void testParseTACStringInvalid(final String tacString) {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(tacString);
        PartialDateTime.parseTACString(tacString, PartialField.MINUTE);
    }

    @Parameters(source = TACStringProvider.class)
    @Test
    public void testParseTACStringStrict(final String tacString, final int day, final int hour, final int minute, final String zoneId) {
        final Optional<ZoneId> zone = createZone(zoneId);
        final PartialDateTime partialDateTime = PartialDateTime.parseTACStringStrict(tacString, existingFields(day, hour, minute), zone.isPresent());
        assertEquals("day", day, partialDateTime.getDay().orElse(-1));
        assertEquals("hour", hour, partialDateTime.getHour().orElse(-1));
        assertEquals("minute", minute, partialDateTime.getMinute().orElse(-1));
        assertEquals("zone", zone, partialDateTime.getZone());
    }

    @Parameters({ //
            ", DAY, false", //
            "02, DAY:HOUR, false", //
            "0203, DAY:HOUR:MINUTE, false", //
            "0203, DAY:MINUTE, false", // Uncontinuous fields
            ", , true", //
            "02, DAY, true", //
            "0203, DAY:HOUR, true", //
            "020304, DAY:HOUR:MINUTE, true", //
            "Z, , false", //
            "02Z, DAY, false", //
            "0203Z, DAY:HOUR, false", //
            "020304Z, DAY:HOUR:MINUTE, false", //
            "b20304Z, DAY:HOUR:MINUTE, true", //
            "0b0304Z, DAY:HOUR:MINUTE, true", //
            "02030405, DAY:HOUR:MINUTE, false", //
            "02030405, DAY:HOUR:MINUTE, true", //
            "02030405Z, DAY:HOUR:MINUTE, false", //
            "02030405Z, DAY:HOUR:MINUTE, true", //
    })
    @Test
    public void testParseTACStringStrictInvalid(final String tacString, final String hasFieldsString, final boolean hasZone) {
        final EnumSet<PartialField> hasFields = Stream.of(hasFieldsString.split(":"))//
                .filter(fieldName -> !fieldName.isEmpty())//
                .map(PartialField::valueOf)//
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(PartialField.class)));
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(tacString);
        PartialDateTime.parseTACStringStrict(tacString, hasFields, hasZone);
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
