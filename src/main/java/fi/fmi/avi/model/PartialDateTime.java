package fi.fmi.avi.model;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.util.StdConverter;

@JsonSerialize(converter = PartialDateTime.ToJsonConverter.class)
@JsonDeserialize(converter = PartialDateTime.FromJsonConverter.class)
public final class PartialDateTime implements Serializable {
    public static final int MIDNIGHT_24_HOUR = 24;
    public static final int MIDNIGHT_0_HOUR = 0;
    static final int MIDNIGHT_MINUTE = 0;

    private static final long serialVersionUID = -1367240702836282527L;

    private static final int FIELD_SIZE_IN_BITS = 7;
    private static final int FIELD_MASK = ~(-1 << FIELD_SIZE_IN_BITS);
    private static final int EMPTY_FIELD_VALUE = FIELD_MASK;
    private static final Pattern PARTIAL_TIME_STRING_PATTERN = Pattern.compile(
            "^--(?<DAY>[0-9]{1,2})?T(?<HOUR>[0-9]{1,2})?:(?<MINUTE>[0-9]{1,2})?:?(?<ZONE>.*)?$");
    /**
     * All fields are initially {@link #EMPTY_FIELD_VALUE}.
     */
    private static final int INITIAL_FIELD_VALUES = ~(-1 << PartialField.VALUES.length * FIELD_SIZE_IN_BITS);

    @Nullable
    private final ZoneId zone;
    private final int fieldValues;

    private PartialDateTime(final int fieldValues, @Nullable final ZoneId zone) {
        this.fieldValues = checkHasContinuousFieldRange(fieldValues);
        this.zone = zone;
    }

    public static PartialDateTime of(final int day, final int hour, final int minute, @Nullable final ZoneId zone) {
        int fieldValues = INITIAL_FIELD_VALUES;
        if (day >= 0) {
            fieldValues = PartialField.DAY.withRawFieldValue(fieldValues, PartialField.DAY.checkValueWithinValidRange(day));
        }
        if (hour >= 0) {
            fieldValues = PartialField.HOUR.withRawFieldValue(fieldValues, PartialField.HOUR.checkValueWithinValidRange(hour));
        }
        if (minute >= 0) {
            fieldValues = PartialField.MINUTE.withRawFieldValue(fieldValues, PartialField.MINUTE.checkValueWithinValidRange(minute));
        }
        return new PartialDateTime(fieldValues, zone);
    }

    public static PartialDateTime ofDayHourMinuteZone(final int day, final int hour, final int minute, final ZoneId zone) {
        requireNonNull(zone, "zone");
        int fieldValues = INITIAL_FIELD_VALUES;
        fieldValues = PartialField.DAY.withRawFieldValue(fieldValues, PartialField.DAY.checkValueWithinValidRange(day));
        fieldValues = PartialField.HOUR.withRawFieldValue(fieldValues, PartialField.HOUR.checkValueWithinValidRange(hour));
        fieldValues = PartialField.MINUTE.withRawFieldValue(fieldValues, PartialField.MINUTE.checkValueWithinValidRange(minute));
        return new PartialDateTime(fieldValues, zone);
    }

    public static PartialDateTime ofDayHourMinuteZone(final ZonedDateTime dateTime, final boolean midnight24h) {
        requireNonNull(dateTime, "dateTime");
        return ofDayHourMinuteZone(dateTime, midnight24h, dateTime.getZone());
    }

    public static PartialDateTime ofDayHourMinuteZone(final Temporal temporal, final boolean midnight24h, final ZoneId zone) {
        requireNonNull(temporal, "temporal");
        final boolean applyMidnight24h = midnight24h && isMidnight(temporal);
        return ofDayHourMinuteZone(PartialField.DAY.get(temporal, applyMidnight24h), PartialField.HOUR.get(temporal, applyMidnight24h),
                PartialField.MINUTE.get(temporal, applyMidnight24h), zone);
    }

    public static PartialDateTime ofDayHourMinute(final int day, final int hour, final int minute) {
        int fieldValues = INITIAL_FIELD_VALUES;
        fieldValues = PartialField.DAY.withRawFieldValue(fieldValues, PartialField.DAY.checkValueWithinValidRange(day));
        fieldValues = PartialField.HOUR.withRawFieldValue(fieldValues, PartialField.HOUR.checkValueWithinValidRange(hour));
        fieldValues = PartialField.MINUTE.withRawFieldValue(fieldValues, PartialField.MINUTE.checkValueWithinValidRange(minute));
        return new PartialDateTime(fieldValues, null);
    }

    public static PartialDateTime ofDayHourMinute(final Temporal temporal, final boolean midnight24h) {
        requireNonNull(temporal, "temporal");
        final boolean applyMidnight24h = midnight24h && isMidnight(temporal);
        return ofDayHourMinute(PartialField.DAY.get(temporal, applyMidnight24h), PartialField.HOUR.get(temporal, applyMidnight24h),
                PartialField.MINUTE.get(temporal, applyMidnight24h));
    }

    public static PartialDateTime ofDayHour(final int day, final int hour) {
        int fieldValues = INITIAL_FIELD_VALUES;
        fieldValues = PartialField.DAY.withRawFieldValue(fieldValues, PartialField.DAY.checkValueWithinValidRange(day));
        fieldValues = PartialField.HOUR.withRawFieldValue(fieldValues, PartialField.HOUR.checkValueWithinValidRange(hour));
        return new PartialDateTime(fieldValues, null);
    }

    public static PartialDateTime ofDayHour(final Temporal temporal, final boolean midnight24h) {
        requireNonNull(temporal, "temporal");
        final boolean applyMidnight24h = midnight24h && isMidnight(temporal);
        return ofDayHour(PartialField.DAY.get(temporal, applyMidnight24h), PartialField.HOUR.get(temporal, applyMidnight24h));
    }

    public static PartialDateTime ofHourMinute(final int hour, final int minute) {
        int fieldValues = INITIAL_FIELD_VALUES;
        fieldValues = PartialField.HOUR.withRawFieldValue(fieldValues, PartialField.HOUR.checkValueWithinValidRange(hour));
        fieldValues = PartialField.MINUTE.withRawFieldValue(fieldValues, PartialField.MINUTE.checkValueWithinValidRange(minute));
        return new PartialDateTime(fieldValues, null);
    }

    public static PartialDateTime ofHourMinute(final Temporal temporal, final boolean midnight24h) {
        requireNonNull(temporal, "temporal");
        final boolean applyMidnight24h = midnight24h && isMidnight(temporal);
        return ofHourMinute(PartialField.HOUR.get(temporal, applyMidnight24h), PartialField.MINUTE.get(temporal, applyMidnight24h));
    }

    public static PartialDateTime ofHour(final int hour) {
        int fieldValues = INITIAL_FIELD_VALUES;
        fieldValues = PartialField.HOUR.withRawFieldValue(fieldValues, PartialField.HOUR.checkValueWithinValidRange(hour));
        return new PartialDateTime(fieldValues, null);
    }

    public static PartialDateTime ofHour(final Temporal temporal, final boolean midnight24h) {
        requireNonNull(temporal, "temporal");
        final boolean applyMidnight24h = midnight24h && isMidnight(temporal);
        return ofHour(PartialField.HOUR.get(temporal, applyMidnight24h));
    }

    public static PartialDateTime of(final PartialField field, final int value) {
        requireNonNull(field, "field");
        field.checkValueWithinValidRange(value);
        return new PartialDateTime(field.withRawFieldValue(INITIAL_FIELD_VALUES, value), null);
    }

    public static PartialDateTime of(final Set<PartialField> fields, final int... values) {
        requireNonNull(fields, "fields");
        requireNonNull(values, "values");
        int fieldValues = INITIAL_FIELD_VALUES;
        final Iterator<PartialField> fieldIterator = fields.iterator();
        int i = 0;
        while (fieldIterator.hasNext()) {
            final PartialField field = requireNonNull(fieldIterator.next(), "null field in fields");
            final int value = values[i];
            fieldValues = field.withRawFieldValue(fieldValues, field.checkValueWithinValidRange(value));
            i += 1;
        }
        if (values.length > i) {
            throw new IllegalArgumentException(String.format("Too many values: %d; expected %d", values.length, i));
        }
        return new PartialDateTime(fieldValues, null);
    }

    public static PartialDateTime of(final ZonedDateTime dateTime, final Set<PartialField> fields, final boolean useZone, final int midnightHour) {
        requireNonNull(dateTime, "dateTime");
        requireNonNull(fields, "fields");
        if (midnightHour != MIDNIGHT_0_HOUR && midnightHour != MIDNIGHT_24_HOUR) {
            throw new IllegalArgumentException(String.format("midnightHour must be either %s or %s; was: %s", MIDNIGHT_0_HOUR, MIDNIGHT_24_HOUR, midnightHour));
        }

        final boolean applyMidnight24h = midnightHour == MIDNIGHT_24_HOUR && isMidnight(dateTime);
        int fieldValues = INITIAL_FIELD_VALUES;
        for (final PartialField field : fields) {
            fieldValues = field.withRawFieldValue(fieldValues, field.get(dateTime, applyMidnight24h));
        }
        @Nullable
        final ZoneId zone = useZone ? dateTime.getZone() : null;
        return new PartialDateTime(fieldValues, zone);
    }

    public static PartialDateTime parse(final String partialDateTimeString) {
        requireNonNull(partialDateTimeString, "partialDateTimeString");
        final Matcher matcher = PARTIAL_TIME_STRING_PATTERN.matcher(partialDateTimeString);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid partialDateTimeString '" + partialDateTimeString + "'");
        }

        int fieldValues = INITIAL_FIELD_VALUES;
        @Nullable
        final ZoneId zone;

        try {
            for (final PartialField field : PartialField.VALUES) {
                @Nullable
                final String fieldValueString = matcher.group(field.name());
                if (fieldValueString != null) {
                    fieldValues = field.withRawFieldValue(fieldValues, field.checkValueWithinValidRange(Integer.parseInt(fieldValueString)));
                }
            }
            zone = Optional.ofNullable(matcher.group("ZONE"))//
                    .filter(offsetId -> !offsetId.isEmpty())//
                    .map(PartialDateTime::parseZone)//
                    .orElse(null);
        } catch (final RuntimeException exception) {
            throw new IllegalArgumentException("Invalid partialDateTimeString: '" + partialDateTimeString + "'", exception);
        }
        return new PartialDateTime(fieldValues, zone);
    }

    public static PartialDateTime parseTACString(final String tacString, final PartialField precision) {
        requireNonNull(tacString, "tacString");
        requireNonNull(precision, "precision");

        int fieldValues = INITIAL_FIELD_VALUES;
        @Nullable
        final ZoneId zone;

        try {
            final int[] parsedFieldValues = new int[PartialField.VALUES.length];
            int tacStringIndex = 0;
            int parsedFieldValuesIndex = 0;
            while (tacStringIndex < tacString.length() //
                    && Character.isDigit(tacString.charAt(tacStringIndex)) //
                    && Character.isDigit(tacString.charAt(tacStringIndex + 1))) {
                parsedFieldValues[parsedFieldValuesIndex] = Integer.parseInt(tacString.substring(tacStringIndex, tacStringIndex + 2));
                tacStringIndex += 2;
                parsedFieldValuesIndex += 1;
            }
            final int parsedFieldsSize = parsedFieldValuesIndex;

            final String zoneId = tacString.substring(tacStringIndex);
            zone = !zoneId.isEmpty() ? parseZone(zoneId) : null;

            if (parsedFieldsSize > 0) {
                final PartialField firstField = PartialField.VALUES[Math.max(precision.ordinal() - (parsedFieldsSize - 1), 0)];
                final PartialField lastField = PartialField.VALUES[Math.max(precision.ordinal(), parsedFieldsSize - 1)];
                final PartialField[] fields = EnumSet.range(firstField, lastField).toArray(new PartialField[0]);
                for (int i = 0; i < fields.length; i++) {
                    final PartialField field = fields[i];
                    fieldValues = field.withRawFieldValue(fieldValues, field.checkValueWithinValidRange(parsedFieldValues[i]));
                }
            }

            return new PartialDateTime(fieldValues, zone);
        } catch (final RuntimeException exception) {
            throw new IllegalArgumentException("Invalid tacString: '" + tacString + "'", exception);
        }
    }

    public static PartialDateTime parseTACStringStrict(final String tacString, final Set<PartialField> hasFields, final boolean hasZone) {
        requireNonNull(tacString, "tacString");
        requireNonNull(hasFields, "hasFields");

        int fieldValues = INITIAL_FIELD_VALUES;
        @Nullable
        final ZoneId zone;

        try {
            if (!hasContinuousEnums(hasFields)) {
                throw new IllegalArgumentException("Uncontinuous fields: " + hasFields);
            }

            int index = 0;
            for (final PartialField field : PartialField.VALUES) {
                if (hasFields.contains(field)) {
                    fieldValues = field.withRawFieldValue(fieldValues,
                            field.checkValueWithinValidRange(Integer.parseInt(tacString.substring(index, index + 2))));
                    index += 2;
                } else {
                    fieldValues = field.withRawFieldValue(fieldValues, EMPTY_FIELD_VALUE);
                }
            }
            final String zoneId = tacString.substring(index);
            if (hasZone && !zoneId.isEmpty()) {
                zone = parseZone(zoneId);
            } else if (hasZone) {
                throw new IllegalArgumentException("Missing zone");
            } else if (!zoneId.isEmpty()) {
                throw new IllegalArgumentException("Unexpected zone: " + zoneId);
            } else {
                zone = null;
            }
        } catch (final RuntimeException exception) {
            throw new IllegalArgumentException("Invalid tacString: '" + tacString + "'", exception);
        }
        return new PartialDateTime(fieldValues, zone);
    }

    private static ZoneId parseZone(final String zoneId) {
        try {
            return ZoneId.of(zoneId);
        } catch (final DateTimeException exception) {
            throw new IllegalArgumentException("Invalid zone offset: " + zoneId, exception);
        }
    }

    static <E extends Enum<E>> boolean hasContinuousEnums(final Iterable<E> enums) {
        int previous = -1;
        for (final E entry : enums) {
            final int current = entry.ordinal();
            if (previous >= 0 && current - 1 != previous) {
                return false;
            }
            previous = current;
        }
        return true;
    }

    private static int checkHasContinuousFieldRange(final int fieldValues) {
        final Set<PartialField> fields = getPresentFields(fieldValues);
        if (!hasContinuousEnums(fields)) {
            throw new IllegalStateException("Uncontinuous fields: " + fields);
        }
        return fieldValues;
    }

    private static Set<PartialField> getPresentFields(final int fieldValues) {
        final EnumSet<PartialField> fields = EnumSet.allOf(PartialField.class);
        fields.removeIf(field -> !hasField(fieldValues, field));
        return fields;
    }

    private static boolean hasField(final int fieldValues, final PartialField field) {
        return field.isValueWithinValidRange(field.getRawFieldValue(fieldValues));
    }

    private static boolean isMidnight(final Temporal temporal) {
        return Stream.of(ChronoField.MILLI_OF_SECOND, ChronoField.SECOND_OF_MINUTE, ChronoField.MINUTE_OF_HOUR, ChronoField.HOUR_OF_DAY)//
                .allMatch(field -> !temporal.isSupported(field) || temporal.get(field) == 0);
    }

    public Set<PartialField> getPresentFields() {
        return getPresentFields(fieldValues);
    }

    public OptionalInt get(final PartialField field) {
        requireNonNull(field, "field");
        final int value = field.getRawFieldValue(fieldValues);
        return field.isValueWithinValidRange(value) ? OptionalInt.of(value) : OptionalInt.empty();
    }

    private boolean has(final PartialField field) {
        return hasField(fieldValues, field);
    }

    public PartialDateTime with(final PartialField field, final int value) {
        requireNonNull(field, "field");
        final int newFieldValues = field.withRawFieldValue(this.fieldValues, field.checkValueWithinValidRange(value));
        return newFieldValues == fieldValues ? this : new PartialDateTime(newFieldValues, zone);
    }

    public PartialDateTime without(final PartialField field) {
        requireNonNull(field, "field");
        final int newFieldValues = field.withRawFieldValue(this.fieldValues, EMPTY_FIELD_VALUE);
        return newFieldValues == fieldValues ? this : new PartialDateTime(newFieldValues, zone);
    }

    public OptionalInt getMinute() {
        return get(PartialField.MINUTE);
    }

    public PartialDateTime withMinute(final int minute) {
        return with(PartialField.MINUTE, minute);
    }

    public PartialDateTime withoutMinute() {
        return without(PartialField.MINUTE);
    }

    public OptionalInt getHour() {
        return get(PartialField.HOUR);
    }

    public PartialDateTime withHour(final int hour) {
        return with(PartialField.HOUR, hour);
    }

    public PartialDateTime withoutHour() {
        return without(PartialField.HOUR);
    }

    public OptionalInt getDay() {
        return get(PartialField.DAY);
    }

    public PartialDateTime withDay(final int day) {
        return with(PartialField.DAY, day);
    }

    public PartialDateTime withoutDay() {
        return without(PartialField.DAY);
    }

    public Optional<ZoneId> getZone() {
        return Optional.ofNullable(zone);
    }

    public PartialDateTime withZone(final ZoneId zone) {
        return new PartialDateTime(fieldValues, requireNonNull(zone, "zone"));
    }

    public PartialDateTime withoutZone() {
        return new PartialDateTime(fieldValues, null);
    }

    public boolean isMidnight() {
        final int rawHour = PartialField.HOUR.getRawFieldValue(fieldValues);
        final int rawMinute = PartialField.MINUTE.getRawFieldValue(fieldValues);
        return (rawHour == MIDNIGHT_0_HOUR || rawHour == MIDNIGHT_24_HOUR) //
                && (rawMinute == MIDNIGHT_MINUTE || !PartialField.MINUTE.isValueWithinValidRange(rawMinute));
    }

    /**
     * Indicates if this time instance was initialized as time '2400' indicating midnight
     * (the last instance of time of the day). As a time instance this is equal to '00:00' of the
     * next day, but it may have implications on how the time is serialized in TAC format.
     *
     * @return true if initiated as partial time "2400"
     */
    public boolean isMidnight24h() {
        final int rawHour = PartialField.HOUR.getRawFieldValue(fieldValues);
        final int rawMinute = PartialField.MINUTE.getRawFieldValue(fieldValues);
        return rawHour == MIDNIGHT_24_HOUR //
                && (rawMinute == MIDNIGHT_MINUTE || !PartialField.MINUTE.isValueWithinValidRange(rawMinute));
    }

    public PartialDateTime withMidnight00h(final YearMonth reference) {
        requireNonNull(reference, "reference");
        if (!isMidnight24h()) {
            return this;
        }
        return withMidnightHour(MIDNIGHT_0_HOUR, reference);
    }

    public PartialDateTime withMidnight24h(final YearMonth reference) {
        requireNonNull(reference, "reference");
        if (!isMidnight() || isMidnight24h()) {
            return this;
        }
        return withMidnightHour(MIDNIGHT_24_HOUR, reference);
    }

    private PartialDateTime withMidnightHour(final int midnightHour, final YearMonth reference) {
        int newFieldValues = fieldValues;
        newFieldValues = PartialField.HOUR.withRawFieldValue(newFieldValues, midnightHour);

        final int rawDay = PartialField.DAY.getRawFieldValue(fieldValues);
        if (PartialField.DAY.isValueWithinValidRange(rawDay)) {
            final int dayShift = midnightHour == MIDNIGHT_24_HOUR ? -1 : 1;
            final int shiftedDay = reference.atDay(rawDay).plusDays(dayShift).getDayOfMonth();
            newFieldValues = PartialField.DAY.withRawFieldValue(newFieldValues, shiftedDay);
        }

        return new PartialDateTime(newFieldValues, zone);
    }

    public ZonedDateTime toZonedDateTime(final YearMonth issueYearMonth) {
        requireNonNull(issueYearMonth, "issueYearMonth");
        final int day = getDay().orElseThrow(() -> new IllegalStateException(String.format("%s missing field %s", this, PartialField.DAY)));
        final LocalDate issueDate;
        try {
            issueDate = issueYearMonth.atDay(day);
        } catch (final DateTimeException exception) {
            throw new DateTimeException(String.format("Unable to complete %s with %s", this, issueYearMonth), exception);
        }
        return toZonedDateTime(issueDate);
    }

    public ZonedDateTime toZonedDateTime(final LocalDate issueDate) {
        requireNonNull(issueDate, "issueDate");

        final int day = getDay().orElse(issueDate.getDayOfMonth());
        final int hour = getHour().orElseThrow(() -> new IllegalStateException(String.format("%s missing field %s", this, PartialField.HOUR)));
        final int minute = getMinute().orElse(0);
        final ZoneId resultingZone = getZone().orElse(ZoneId.of("Z"));
        final int plusMonths = day < issueDate.getDayOfMonth() ? 1 : 0; // issue day > day of partial, assume next month

        try {
            if (isMidnight24h()) {
                return ZonedDateTime.of(LocalDateTime.of(issueDate.getYear(), issueDate.getMonth(), day, 0, 0), resultingZone)//
                        .plusDays(1)//
                        .plusMonths(plusMonths);
            } else {
                return ZonedDateTime.of(LocalDateTime.of(issueDate.getYear(), issueDate.getMonth(), day, hour, minute), resultingZone)//
                        .plusMonths(plusMonths);
            }
        } catch (final DateTimeException exception) {
            throw new DateTimeException(String.format("Unable to complete %s with %s", this, issueDate), exception);
        }
    }

    private ZonedDateTime toZonedDateTime(final ZonedDateTime referenceTime) {
        if (zone != null && !zone.equals(referenceTime.getZone())) {
            return toZonedDateTime(referenceTime.withZoneSameInstant(zone));
        }
        if (isMidnight24h()) {
            return ZonedDateTime.of(LocalDateTime.of(//
                    referenceTime.getYear(), //
                    referenceTime.getMonth(), //
                    getDay().orElse(referenceTime.getDayOfMonth()), //
                    0, //
                    0), //
                    getZone().orElse(referenceTime.getZone())) //
                    .plusDays(1L);
        } else {
            final int precision = getPrecisionOrdinal();
            return ZonedDateTime.of(LocalDateTime.of(//
                    referenceTime.getYear(), //
                    referenceTime.getMonth(), //
                    getDay().orElse(precision > PartialField.DAY.ordinal() ? referenceTime.getDayOfMonth() : 1), //
                    getHour().orElse(precision > PartialField.HOUR.ordinal() ? referenceTime.getHour() : 0), //
                    getMinute().orElse(precision > PartialField.MINUTE.ordinal() ? referenceTime.getMinute() : 0)), //
                    getZone().orElse(referenceTime.getZone()));
        }
    }

    private int getPrecisionOrdinal() {
        for (int i = PartialField.VALUES.length - 1; i >= 0; i--) {
            final PartialField field = PartialField.VALUES[i];
            if (has(field)) {
                return field.ordinal();
            }
        }
        return PartialField.VALUES.length;
    }

    public ZonedDateTime toZonedDateTimeAfter(final ZonedDateTime referenceTime) {
        requireNonNull(referenceTime, "referenceTime");
        return toZonedDateTimeOnSideOf(referenceTime, ChronoZonedDateTime::isAfter, 1);
    }

    public ZonedDateTime toZonedDateTimeNotBefore(final ZonedDateTime referenceTime) {
        requireNonNull(referenceTime, "referenceTime");
        return toZonedDateTimeOnSideOf(referenceTime, (candidate, reference) -> !candidate.isBefore(reference), 1);
    }

    public ZonedDateTime toZonedDateTimeBefore(final ZonedDateTime referenceTime) {
        requireNonNull(referenceTime, "referenceTime");
        return toZonedDateTimeOnSideOf(referenceTime, ChronoZonedDateTime::isBefore, -1);
    }

    public ZonedDateTime toZonedDateTimeNotAfter(final ZonedDateTime referenceTime) {
        requireNonNull(referenceTime, "referenceTime");
        return toZonedDateTimeOnSideOf(referenceTime, (candidate, reference) -> !candidate.isAfter(reference), -1);
    }

    private ZonedDateTime toZonedDateTimeOnSideOf(final ZonedDateTime referenceTime, final BiPredicate<ZonedDateTime, ZonedDateTime> condition,
            final int fallbackDirection) {
        try {
            @Nullable
            final ZonedDateTime zonedDateTime = toZonedDateTimeOnSideOf(referenceTime, condition, fallbackDirection, referenceTime,
                    fallbackDirection < 0 ? 2 : 1);
            if (zonedDateTime == null) {
                throw new DateTimeException(String.format("Cannot resolve valid instant represented by %s.", this));
            } else {
                return zonedDateTime;
            }
        } catch (final DateTimeException exception) {
            throw new DateTimeException(String.format("Unable to complete %s with %s", this, referenceTime), exception);
        }
    }

    @Nullable
    private ZonedDateTime toZonedDateTimeOnSideOf(final ZonedDateTime referenceTime, final BiPredicate<ZonedDateTime, ZonedDateTime> condition,
            final int fallbackDirection, final ZonedDateTime candidateReference, final int retries) {
        final ZonedDateTime candidate = getNearestCandidate(candidateReference, fallbackDirection);
        if (represents(candidate) && condition.test(candidate, referenceTime)) {
            return candidate;
        } else if (retries > 0) {
            return toZonedDateTimeOnSideOf(referenceTime, condition, fallbackDirection, shiftReference(candidateReference, fallbackDirection), retries - 1);
        } else {
            return null;
        }
    }

    public ZonedDateTime toZonedDateTimeNear(final ZonedDateTime referenceTime) {
        requireNonNull(referenceTime, "referenceTime");
        try {
            final ZonedDateTime candidate = getNearestCandidate(referenceTime, -1);
            if (candidate.isBefore(referenceTime)) {
                return representedNearestToReference(candidate, referenceTime, getNearestCandidate(shiftReference(referenceTime, 1), 1));
            } else if (candidate.isAfter(referenceTime)) {
                return representedNearestToReference(getNearestCandidate(shiftReference(referenceTime, -1), -1), referenceTime, candidate);
            } else if (!represents(candidate)) {
                return representedNearestToReference(getNearestCandidate(shiftReference(referenceTime, -1), -1), referenceTime,
                        getNearestCandidate(shiftReference(referenceTime, 1), 1));
            } else {
                return referenceTime;
            }
        } catch (final DateTimeException exception) {
            throw new DateTimeException(String.format("Unable to complete %s with %s", this, referenceTime), exception);
        }
    }

    private ZonedDateTime getNearestCandidate(final ZonedDateTime referenceTime, final int fallbackDirection) {
        try {
            return toZonedDateTime(referenceTime);
        } catch (final DateTimeException originatingException) {
            // a partial field is greater than allowed in referenceTime, indicating that referenceTime represents an instant within next unit; try previous
            try {
                return toZonedDateTime(shiftReference(referenceTime, fallbackDirection));
            } catch (final DateTimeException ignored) {
                throw originatingException;
            }
        }
    }

    private ZonedDateTime shiftReference(final ZonedDateTime candidate, final int nth) {
        if (has(PartialField.DAY)) {
            return candidate.plusMonths(nth);
        } else if (has(PartialField.HOUR)) {
            return candidate.plusDays(nth);
        } else if (has(PartialField.MINUTE)) {
            return candidate.plusHours(nth);
        } else {
            return candidate.plusMinutes(nth);
        }
    }

    private ZonedDateTime representedNearestToReference(final ZonedDateTime candidateBefore, final ZonedDateTime referenceTime,
            final ZonedDateTime candidateAfter) {
        final boolean representsCandidateBefore = represents(candidateBefore);
        final boolean representsCandidateAfter = represents(candidateAfter);
        if (representsCandidateBefore && representsCandidateAfter) {
            return nearestToReference(candidateBefore, referenceTime, candidateAfter);
        } else if (representsCandidateBefore) {
            return candidateBefore;
        } else if (representsCandidateAfter) {
            return candidateAfter;
        } else {
            throw new DateTimeException(
                    String.format("Cannot resolve valid instant represented by %s. Both candidates fail (%s, %s).", this, candidateBefore, candidateAfter));
        }
    }

    private ZonedDateTime nearestToReference(final ZonedDateTime candidateBefore, final ZonedDateTime referenceTime, final ZonedDateTime candidateAfter) {
        final Duration durationBefore = Duration.between(candidateBefore, referenceTime);
        final Duration durationAfter = Duration.between(referenceTime, candidateAfter);
        final int comparisonResult = durationBefore.compareTo(durationAfter);
        if (comparisonResult < 0) {
            return candidateBefore;
        } else {
            return candidateAfter;
        }
    }

    public boolean represents(final Temporal temporal) {
        final boolean midnight24h = isMidnight24h();
        for (final PartialField field : PartialField.VALUES) {
            final int rawValue = field.getRawFieldValue(fieldValues);
            if (field.isValueWithinValidRange(rawValue) //
                    && !(temporal.isSupported(field.chronoField) && rawValue == field.get(temporal, midnight24h))) {
                return false;
            }
        }
        return zone == null || temporal instanceof ChronoZonedDateTime && zone.equals(((ChronoZonedDateTime<?>) temporal).getZone());
    }

    public String toTACString() {
        final StringBuilder builder = new StringBuilder();
        for (final PartialField field : PartialField.VALUES) {
            tryAppendFieldValue(builder, field);
        }
        if (zone != null) {
            builder.append(zone);
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder()//
                .append("--");
        tryAppendFieldValue(builder, PartialField.DAY);
        builder.append('T');
        tryAppendFieldValue(builder, PartialField.HOUR);
        builder.append(':');
        tryAppendFieldValue(builder, PartialField.MINUTE);
        if (zone != null) {
            builder.append(zone);
        }
        return builder.toString();
    }

    private void tryAppendFieldValue(final StringBuilder builder, final PartialField field) {
        final int fieldValue = field.getRawFieldValue(fieldValues);
        if (!field.isValueWithinValidRange(fieldValue)) {
            return;
        }
        if (fieldValue < 10) {
            builder.append('0');
        }
        builder.append(fieldValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldValues, zone);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof PartialDateTime) {
            final PartialDateTime other = (PartialDateTime) obj;
            return fieldValues == other.fieldValues //
                    && Objects.equals(this.zone, other.zone);
        } else {
            return false;
        }
    }

    public enum PartialField {
        DAY(ChronoField.DAY_OF_MONTH) {
            @Override
            int get(final Temporal temporal, final boolean midnight24h) {
                return (midnight24h ? temporal.minus(1, ChronoUnit.DAYS) : temporal).get(getTemporalField());
            }
        }, //
        HOUR(ChronoField.HOUR_OF_DAY) {
            @Override
            int get(final Temporal temporal, final boolean midnight24h) {
                return midnight24h ? MIDNIGHT_24_HOUR : temporal.get(getTemporalField());
            }
        }, //
        MINUTE(ChronoField.MINUTE_OF_HOUR) {
            @Override
            int get(final Temporal temporal, final boolean midnight24h) {
                return temporal.get(getTemporalField());
            }
        };

        static final PartialField[] VALUES = values();
        /**
         * Minimum value of a field (inclusive).
         */
        private static final int MIN_FIELD_VALUE = 0;
        /**
         * Maximum value of a field (inclusive).
         */
        private static final int MAX_FIELD_VALUE = 99;

        private final int bitIndex;
        private final ChronoField chronoField;

        PartialField(final ChronoField chronoField) {
            this.chronoField = chronoField;
            this.bitIndex = FIELD_SIZE_IN_BITS * ordinal();
        }

        int checkValueWithinValidRange(final int value) {
            if (!isValueWithinValidRange(value)) {
                throw new IllegalArgumentException(
                        String.format("Field %s value %d is not within range [%d,%d]", this, value, MIN_FIELD_VALUE, MAX_FIELD_VALUE));
            }
            return value;
        }

        boolean isValueWithinValidRange(final int value) {
            return value >= MIN_FIELD_VALUE && value <= MAX_FIELD_VALUE;
        }

        int getRawFieldValue(final int fieldValues) {
            return (fieldValues >> bitIndex) & FIELD_MASK;
        }

        int withRawFieldValue(final int fieldValues, final int value) {
            if (value >> FIELD_SIZE_IN_BITS != 0) {
                throw new IllegalArgumentException("Value out of bounds: " + value);
            }
            return fieldValues & ~(FIELD_MASK << bitIndex) | value << bitIndex;
        }

        public ChronoField getTemporalField() {
            return chronoField;
        }

        abstract int get(Temporal temporal, boolean midnight24h);
    }

    static final class FromJsonConverter extends StdConverter<String, PartialDateTime> {
        @Override
        public PartialDateTime convert(final String value) {
            return PartialDateTime.parse(value);
        }
    }

    static final class ToJsonConverter extends StdConverter<PartialDateTime, String> {
        @Override
        public String convert(final PartialDateTime value) {
            return value.toString();
        }
    }
}
