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
import java.time.format.DateTimeParseException;
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

/**
 * Representation of a partial date-time.
 *
 * <p id="continuous-fields">
 * A PartialDateTime may consists of any <em>continuous</em> combination of {@link PartialField#DAY day}, {@link PartialField#HOUR hour} and
 * {@link PartialField#MINUTE} with and without time zone. A continuous combination is a combination containing all fields between most and least significant
 * field. E.g. combinations of just day, day and hour or all day, hour and minute. But combination of day and minute is not allowed as it is missing hour in
 * between.
 * </p>
 *
 * <p>
 * Field values are not validated against valid calendar values, but may be anything between 0-99. Validation shall be done outside this class, e.g. when
 * completing a PartialDateTime into a {@link ZonedDateTime}.
 * </p>
 *
 * <p>
 * This class provides JSON serialization and deserialization support with <a href="https://github.com/FasterXML/jackson">Jackson</a>.
 * The serialized representation of an instance is the {@link #toString()} format.
 * </p>
 */
@JsonSerialize(converter = PartialDateTime.ToJsonConverter.class)
@JsonDeserialize(converter = PartialDateTime.FromJsonConverter.class)
public final class PartialDateTime implements Serializable {
    /**
     * Midnight hour 24.
     */
    public static final int MIDNIGHT_24_HOUR = 24;
    /**
     * Midnight hour 0.
     */
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

    /**
     * Obtain an instance of optional values.
     *
     * @param day
     *         day or {@code -1} if absent
     * @param hour
     *         hour or {@code -1} if absent
     * @param minute
     *         minute or {@code -1} if absent
     * @param zone
     *         zone or {@code null} if absent
     *
     * @return instance with given fields
     *
     * @throws DateTimeException
     *         if a field value is outside valid range or <a href="#continuous-fields">uncontinuous</a> field values are given
     */
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

    /**
     * Obtain an instance of day, hour, minute and zone.
     *
     * @param day
     *         day
     * @param hour
     *         hour
     * @param minute
     *         minute
     * @param zone
     *         zone
     *
     * @return instance with day, hour minute and zone
     *
     * @throws DateTimeException
     *         if a field value is outside valid range
     */
    public static PartialDateTime ofDayHourMinuteZone(final int day, final int hour, final int minute, final ZoneId zone) {
        requireNonNull(zone, "zone");
        int fieldValues = INITIAL_FIELD_VALUES;
        fieldValues = PartialField.DAY.withRawFieldValue(fieldValues, PartialField.DAY.checkValueWithinValidRange(day));
        fieldValues = PartialField.HOUR.withRawFieldValue(fieldValues, PartialField.HOUR.checkValueWithinValidRange(hour));
        fieldValues = PartialField.MINUTE.withRawFieldValue(fieldValues, PartialField.MINUTE.checkValueWithinValidRange(minute));
        return new PartialDateTime(fieldValues, zone);
    }

    /**
     * Obtain an instance of day, hour minute and zone.
     *
     * @param dateTime
     *         datetime of desired day, hour, minute and zone
     * @param midnight24h
     *         {@code true} when midnight hour 24, {@code false} for midnight hour 0
     *
     * @return instance with day, hour, minute and zone
     */
    public static PartialDateTime ofDayHourMinuteZone(final ZonedDateTime dateTime, final boolean midnight24h) {
        requireNonNull(dateTime, "dateTime");
        return ofDayHourMinuteZone(dateTime, midnight24h, dateTime.getZone());
    }

    /**
     * Obtain an instance of day, hour minute and zone.
     *
     * @param temporal
     *         temporal of desired day, hour and minute
     * @param midnight24h
     *         {@code true} when midnight hour 24, {@code false} for midnight hour 0
     * @param zone
     *         zone
     *
     * @return instance with day, hour, minute and zone
     */
    public static PartialDateTime ofDayHourMinuteZone(final Temporal temporal, final boolean midnight24h, final ZoneId zone) {
        requireNonNull(temporal, "temporal");
        final boolean applyMidnight24h = midnight24h && isMidnight(temporal);
        return ofDayHourMinuteZone(PartialField.DAY.get(temporal, applyMidnight24h), PartialField.HOUR.get(temporal, applyMidnight24h),
                PartialField.MINUTE.get(temporal, applyMidnight24h), zone);
    }

    /**
     * Obtain an instance of day, hour and minute.
     *
     * @param day
     *         day
     * @param hour
     *         hour
     * @param minute
     *         minute
     *
     * @return instance with day, hour and minute
     *
     * @throws DateTimeException
     *         if a field value is outside valid range
     */
    public static PartialDateTime ofDayHourMinute(final int day, final int hour, final int minute) {
        int fieldValues = INITIAL_FIELD_VALUES;
        fieldValues = PartialField.DAY.withRawFieldValue(fieldValues, PartialField.DAY.checkValueWithinValidRange(day));
        fieldValues = PartialField.HOUR.withRawFieldValue(fieldValues, PartialField.HOUR.checkValueWithinValidRange(hour));
        fieldValues = PartialField.MINUTE.withRawFieldValue(fieldValues, PartialField.MINUTE.checkValueWithinValidRange(minute));
        return new PartialDateTime(fieldValues, null);
    }

    /**
     * Obtain an instance of day, hour and minute.
     *
     * @param temporal
     *         temporal of desired day, hour and minute
     * @param midnight24h
     *         {@code true} when midnight hour 24, {@code false} for midnight hour 0
     *
     * @return instance with day, hour and minute
     */
    public static PartialDateTime ofDayHourMinute(final Temporal temporal, final boolean midnight24h) {
        requireNonNull(temporal, "temporal");
        final boolean applyMidnight24h = midnight24h && isMidnight(temporal);
        return ofDayHourMinute(PartialField.DAY.get(temporal, applyMidnight24h), PartialField.HOUR.get(temporal, applyMidnight24h),
                PartialField.MINUTE.get(temporal, applyMidnight24h));
    }

    /**
     * Obtain an instance of day and hour.
     *
     * @param day
     *         day
     * @param hour
     *         hour
     *
     * @return instance with day and hour
     *
     * @throws DateTimeException
     *         if a field value is outside valid range
     */
    public static PartialDateTime ofDayHour(final int day, final int hour) {
        int fieldValues = INITIAL_FIELD_VALUES;
        fieldValues = PartialField.DAY.withRawFieldValue(fieldValues, PartialField.DAY.checkValueWithinValidRange(day));
        fieldValues = PartialField.HOUR.withRawFieldValue(fieldValues, PartialField.HOUR.checkValueWithinValidRange(hour));
        return new PartialDateTime(fieldValues, null);
    }

    /**
     * Obtain an instance of day and hour.
     *
     * @param temporal
     *         temporal of desired day and hour
     * @param midnight24h
     *         {@code true} when midnight hour 24, {@code false} for midnight hour 0
     *
     * @return instance with day and hour
     */
    public static PartialDateTime ofDayHour(final Temporal temporal, final boolean midnight24h) {
        requireNonNull(temporal, "temporal");
        final boolean applyMidnight24h = midnight24h && isMidnight(temporal);
        return ofDayHour(PartialField.DAY.get(temporal, applyMidnight24h), PartialField.HOUR.get(temporal, applyMidnight24h));
    }

    /**
     * Obtain an instance of hour and minute.
     *
     * @param hour
     *         hour
     * @param minute
     *         minute
     *
     * @return instance with hour and minute
     *
     * @throws DateTimeException
     *         if a field value is outside valid range
     */
    public static PartialDateTime ofHourMinute(final int hour, final int minute) {
        int fieldValues = INITIAL_FIELD_VALUES;
        fieldValues = PartialField.HOUR.withRawFieldValue(fieldValues, PartialField.HOUR.checkValueWithinValidRange(hour));
        fieldValues = PartialField.MINUTE.withRawFieldValue(fieldValues, PartialField.MINUTE.checkValueWithinValidRange(minute));
        return new PartialDateTime(fieldValues, null);
    }

    /**
     * Obtain an instance of hour and minute.
     *
     * @param temporal
     *         temporal of desired hour and minute
     * @param midnight24h
     *         {@code true} when midnight hour 24, {@code false} for midnight hour 0
     *
     * @return instance with hour and minute
     */
    public static PartialDateTime ofHourMinute(final Temporal temporal, final boolean midnight24h) {
        requireNonNull(temporal, "temporal");
        final boolean applyMidnight24h = midnight24h && isMidnight(temporal);
        return ofHourMinute(PartialField.HOUR.get(temporal, applyMidnight24h), PartialField.MINUTE.get(temporal, applyMidnight24h));
    }

    /**
     * Obtain an instance of hour.
     *
     * @param hour
     *         hour
     *
     * @return instance with hour
     *
     * @throws DateTimeException
     *         if a hour is outside valid range
     */
    public static PartialDateTime ofHour(final int hour) {
        int fieldValues = INITIAL_FIELD_VALUES;
        fieldValues = PartialField.HOUR.withRawFieldValue(fieldValues, PartialField.HOUR.checkValueWithinValidRange(hour));
        return new PartialDateTime(fieldValues, null);
    }

    /**
     * Obtain an instance of hour.
     *
     * @param temporal
     *         temporal of desired hour
     * @param midnight24h
     *         {@code true} when midnight hour 24, {@code false} for midnight hour 0
     *
     * @return instance with hour
     */
    public static PartialDateTime ofHour(final Temporal temporal, final boolean midnight24h) {
        requireNonNull(temporal, "temporal");
        final boolean applyMidnight24h = midnight24h && isMidnight(temporal);
        return ofHour(PartialField.HOUR.get(temporal, applyMidnight24h));
    }

    /**
     * Obtain an instance of singe field.
     *
     * @param field
     *         field
     * @param value
     *         field value
     *
     * @return instance with single field
     *
     * @throws DateTimeException
     *         if field value is outside valid range
     */
    public static PartialDateTime of(final PartialField field, final int value) {
        requireNonNull(field, "field");
        field.checkValueWithinValidRange(value);
        return new PartialDateTime(field.withRawFieldValue(INITIAL_FIELD_VALUES, value), null);
    }

    /**
     * Obtain an instance of provided fields.
     *
     * @param fields
     *         fields
     * @param values
     *         values in order from most significant field to least significant field ({@link PartialField} enum order)
     *
     * @return instance with given field values
     *
     * @throws DateTimeException
     *         if a field value is outside valid range or number of fields and values mismatch
     */
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
            throw new DateTimeException(String.format("Too many values: %d; expected %d", values.length, i));
        }
        return new PartialDateTime(fieldValues, null);
    }

    /**
     * Obtain an instance of {@code fields} from {@code dateTime}, optionally containing zone and having chosen midnight hour if midnight.
     *
     * @param dateTime
     *         field values and zone
     * @param fields
     *         fields to populate new PartialDateTime with
     * @param useZone
     *         {@code true} to use zone of {@code dateTime}, {@code false} to omit zone
     * @param midnightHour
     *         either {@value #MIDNIGHT_0_HOUR} or {@value #MIDNIGHT_24_HOUR}
     *
     * @return instance with given field values
     *
     * @throws DateTimeException
     *         if field value is outside valid range
     */
    public static PartialDateTime of(final ZonedDateTime dateTime, final Set<PartialField> fields, final boolean useZone, final int midnightHour) {
        requireNonNull(dateTime, "dateTime");
        requireNonNull(fields, "fields");
        if (midnightHour != MIDNIGHT_0_HOUR && midnightHour != MIDNIGHT_24_HOUR) {
            throw new DateTimeException(String.format("midnightHour must be either %s or %s; was: %s", MIDNIGHT_0_HOUR, MIDNIGHT_24_HOUR, midnightHour));
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

    /**
     * Obtain an instance from string.
     * See {@link #toString()} for description of string format.
     *
     * @param partialDateTimeString
     *         string to parse
     *
     * @return instance from string
     *
     * @throws DateTimeParseException
     *         if string cannot be parsed
     */
    public static PartialDateTime parse(final String partialDateTimeString) {
        requireNonNull(partialDateTimeString, "partialDateTimeString");
        final Matcher matcher = PARTIAL_TIME_STRING_PATTERN.matcher(partialDateTimeString);
        if (!matcher.matches()) {
            throw new DateTimeParseException("Invalid partialDateTimeString '" + partialDateTimeString + "'", partialDateTimeString, 0);
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
            throw new DateTimeParseException("Invalid partialDateTimeString '" + partialDateTimeString + "'", partialDateTimeString, 0, exception);
        }
        return new PartialDateTime(fieldValues, zone);
    }

    /**
     * Obtain an instance from TAC string.
     *
     * <p>
     * This method attempts to parse field values available in TAC string and optional time zone in the end. The {@code precision} parameter is used as a
     * hint for least significant field. E.g. {@code "0102", HOUR} results in day 01 and hour 02, while {@code "0102", MINUTE} results in hour 01 and day 02.
     * But as {@code precision} is only a hint, {@code "010203, HOUR} results in day 01, hour 02 and minute 03.
     * </p>
     *
     * @param tacString
     *         TAC string to parse
     * @param precision
     *         least significant field hint
     *
     * @return instance from TAC string
     *
     * @throws DateTimeParseException
     *         if string cannot be parsed
     */
    public static PartialDateTime parseTACString(final String tacString, final PartialField precision) {
        requireNonNull(tacString, "tacString");
        requireNonNull(precision, "precision");

        int fieldValues = INITIAL_FIELD_VALUES;
        @Nullable
        final ZoneId zone;

        int tacStringIndex = 0;
        try {
            final int[] parsedFieldValues = new int[PartialField.VALUES.length];
            int parsedFieldValuesIndex = 0;
            while (tacStringIndex < tacString.length() //
                    && Character.isDigit(tacString.charAt(tacStringIndex)) //
                    && Character.isDigit(tacString.charAt(tacStringIndex + 1))) {
                try {
                    parsedFieldValues[parsedFieldValuesIndex] = Integer.parseInt(tacString.substring(tacStringIndex, tacStringIndex + 2));
                } catch (final IndexOutOfBoundsException exception) {
                    throw new DateTimeParseException("Too many fields", tacString, tacStringIndex, exception);
                }
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
                    tacStringIndex = i * 2;
                    final PartialField field = fields[i];
                    fieldValues = field.withRawFieldValue(fieldValues, field.checkValueWithinValidRange(parsedFieldValues[i]));
                }
            }

            return new PartialDateTime(fieldValues, zone);
        } catch (final RuntimeException exception) {
            throw new DateTimeParseException("Invalid tacString: '" + tacString + "'", tacString, tacStringIndex, exception);
        }
    }

    /**
     * Obtain an instance from TAC string using strict parsing.
     * The given string is expected to contain exactly given fields ({@code hasFields}) and zone depending on {@code hasZone}.
     * Parse fails if given TAC string fails to satisfy given requirements.
     *
     * @param tacString
     *         TAC string to parse
     * @param hasFields
     *         exact fields to parse
     * @param hasZone
     *         {@code true} if TAC string is expected to contain zone, {@code false} otherwise
     *
     * @return instance from TAC string
     *
     * @throws DateTimeParseException
     *         if string cannot be parsed
     * @throws DateTimeException
     *         if {@code hasFields} is <a href="#continuous-fields">uncontinuous</a>
     */
    public static PartialDateTime parseTACStringStrict(final String tacString, final Set<PartialField> hasFields, final boolean hasZone) {
        requireNonNull(tacString, "tacString");
        requireNonNull(hasFields, "hasFields");

        int fieldValues = INITIAL_FIELD_VALUES;
        @Nullable
        final ZoneId zone;

        int index = 0;
        try {
            if (!hasContinuousEnums(hasFields)) {
                throw new DateTimeException("Uncontinuous fields: " + hasFields);
            }

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
                throw new DateTimeParseException(tacString + " is missing zone", tacString, index);
            } else if (!zoneId.isEmpty()) {
                throw new DateTimeParseException(tacString + " has unexpected zone: " + zoneId, tacString, index);
            } else {
                zone = null;
            }
        } catch (final DateTimeParseException exception) {
            throw exception;
        } catch (final RuntimeException exception) {
            throw new DateTimeParseException("Invalid tacString: '" + tacString + "'", tacString, index, exception);
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
            throw new DateTimeException("Uncontinuous fields: " + fields);
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

    /**
     * Returns a set of fields this PartialDateTime contains.
     *
     * @return set of present fields
     */
    public Set<PartialField> getPresentFields() {
        return getPresentFields(fieldValues);
    }

    /**
     * Gets the optional value of specified field from this partial date-time as an {@code int}.
     *
     * @param field
     *         the field to get
     *
     * @return optional value for the field, empty if field is unset
     */
    public OptionalInt get(final PartialField field) {
        requireNonNull(field, "field");
        final int value = field.getRawFieldValue(fieldValues);
        return field.isValueWithinValidRange(value) ? OptionalInt.of(value) : OptionalInt.empty();
    }

    /**
     * Queries, whether this partial date-time has a value for specified field or not.
     *
     * @param field
     *         the field to query
     *
     * @return {@code true} if this partial date-time contains specified field, {@code false} otherwise
     */
    private boolean has(final PartialField field) {
        return hasField(fieldValues, field);
    }

    /**
     * Returns a copy of this {@code PartialDateTime} with the specifield field being set to specified value.
     *
     * @param field
     *         field to be altered in the result
     * @param value
     *         value of specified field to set in the result
     *
     * @return a {@code PartialDateTime} based on this object with specified field set to specifield value
     *
     * @throws DateTimeException
     *         if {@code value} is outside it's range or if fields of result would be <a href="#continuous-fields">uncontinuous</a>
     */
    public PartialDateTime with(final PartialField field, final int value) {
        requireNonNull(field, "field");
        final int newFieldValues = field.withRawFieldValue(this.fieldValues, field.checkValueWithinValidRange(value));
        return newFieldValues == fieldValues ? this : new PartialDateTime(newFieldValues, zone);
    }

    /**
     * Returns a copy of this {@code PartialDateTime} with the specified field being unset.
     *
     * @param field
     *         field to be unset in the result
     *
     * @return a {@code PartialDateTime} based on this object with specifield field unset
     *
     * @throws DateTimeException
     *         if fields of result would be <a href="#continuous-fields">uncontinuous</a>
     */
    public PartialDateTime without(final PartialField field) {
        requireNonNull(field, "field");
        final int newFieldValues = field.withRawFieldValue(this.fieldValues, EMPTY_FIELD_VALUE);
        return newFieldValues == fieldValues ? this : new PartialDateTime(newFieldValues, zone);
    }

    /**
     * Gets the minute field if present.
     *
     * @return minute
     */
    public OptionalInt getMinute() {
        return get(PartialField.MINUTE);
    }

    /**
     * Returns a copy of this {@code PartialDateTime} with the minute altered.
     *
     * @param minute
     *         the minute to set
     *
     * @return a {@code PartialDateTime} based on this partial date-time with the requested minute
     *
     * @throws DateTimeException
     *         if minute value is invalid or if fields of result would be <a href="#continuous-fields">uncontinuous</a>
     */
    public PartialDateTime withMinute(final int minute) {
        return with(PartialField.MINUTE, minute);
    }

    /**
     * Returns a copy of this {@code PartialDateTime} with the minute field being unset.
     *
     * @return a {@code PartialDateTime} based on this object with minute field unset
     *
     * @throws DateTimeException
     *         if fields of result would be <a href="#continuous-fields">uncontinuous</a>
     */
    public PartialDateTime withoutMinute() {
        return without(PartialField.MINUTE);
    }

    /**
     * Gets the hour field if present.
     *
     * @return hour
     */
    public OptionalInt getHour() {
        return get(PartialField.HOUR);
    }

    /**
     * Returns a copy of this {@code PartialDateTime} with the hour altered.
     *
     * @param hour
     *         the hour to set
     *
     * @return a {@code PartialDateTime} based on this partial date-time with the requested hour
     *
     * @throws DateTimeException
     *         if hour value is invalid or if fields of result would be <a href="#continuous-fields">uncontinuous</a>
     */
    public PartialDateTime withHour(final int hour) {
        return with(PartialField.HOUR, hour);
    }

    /**
     * Returns a copy of this {@code PartialDateTime} with the hour field being unset.
     *
     * @return a {@code PartialDateTime} based on this object with hour field unset
     *
     * @throws DateTimeException
     *         if fields of result would be <a href="#continuous-fields">uncontinuous</a>
     */
    public PartialDateTime withoutHour() {
        return without(PartialField.HOUR);
    }

    /**
     * Gets the day field if present.
     *
     * @return day
     */
    public OptionalInt getDay() {
        return get(PartialField.DAY);
    }

    /**
     * Returns a copy of this {@code PartialDateTime} with the day altered.
     *
     * @param day
     *         the day to set
     *
     * @return a {@code PartialDateTime} based on this partial date-time with the requested day
     *
     * @throws DateTimeException
     *         if day value is invalid or if fields of result would be <a href="#continuous-fields">uncontinuous</a>
     */
    public PartialDateTime withDay(final int day) {
        return with(PartialField.DAY, day);
    }

    /**
     * Returns a copy of this {@code PartialDateTime} with the day field being unset.
     *
     * @return a {@code PartialDateTime} based on this object with day field unset
     *
     * @throws DateTimeException
     *         if fields of result would be <a href="#continuous-fields">uncontinuous</a>
     */
    public PartialDateTime withoutDay() {
        return without(PartialField.DAY);
    }

    /**
     * Gets the zone if present.
     *
     * @return zone
     */
    public Optional<ZoneId> getZone() {
        return Optional.ofNullable(zone);
    }

    /**
     * Returns a copy of this {@code PartialDateTime} with the zone altered.
     *
     * @param zone
     *         the zone to set
     *
     * @return a {@code PartialDateTime} based on this partial date-time with the requested zone
     */
    public PartialDateTime withZone(final ZoneId zone) {
        return new PartialDateTime(fieldValues, requireNonNull(zone, "zone"));
    }

    /**
     * Returns a copy of this {@code PartialDateTime} with the zone field being unset.
     *
     * @return a {@code PartialDateTime} based on this object with zone field unset
     */
    public PartialDateTime withoutZone() {
        return new PartialDateTime(fieldValues, null);
    }

    /**
     * Indicates whether this partial date-time represent midnight.
     * Midnight is specified as hour being either {@value #MIDNIGHT_0_HOUR} or {@value #MIDNIGHT_24_HOUR} and minute is either {@value #MIDNIGHT_MINUTE} or
     * unset.
     *
     * @return {@code true} if this partial date-time represents midnight, {@code false} otherwise
     */
    public boolean isMidnight() {
        final int rawHour = PartialField.HOUR.getRawFieldValue(fieldValues);
        final int rawMinute = PartialField.MINUTE.getRawFieldValue(fieldValues);
        return (rawHour == MIDNIGHT_0_HOUR || rawHour == MIDNIGHT_24_HOUR) //
                && (rawMinute == MIDNIGHT_MINUTE || !PartialField.MINUTE.isValueWithinValidRange(rawMinute));
    }

    private boolean isMidnight(final int midnightHour) {
        assert midnightHour == MIDNIGHT_0_HOUR || midnightHour == MIDNIGHT_24_HOUR : "Unexpected midnight hour: " + midnightHour;
        final int rawHour = PartialField.HOUR.getRawFieldValue(fieldValues);
        final int rawMinute = PartialField.MINUTE.getRawFieldValue(fieldValues);
        return rawHour == midnightHour //
                && (rawMinute == MIDNIGHT_MINUTE || !PartialField.MINUTE.isValueWithinValidRange(rawMinute));
    }

    /**
     * Indicates whether this partial date-time represents midnight with hour as {@value #MIDNIGHT_24_HOUR} (the last instant of the time of day).
     * When such instance is completed into a {@link ZonedDateTime}, the day of month will be next day and hour 0 in the resulting {@code ZonedDateTime}..
     *
     * @return {@code true} if this partial date-time represents midnight with hour as {@value #MIDNIGHT_24_HOUR}, {@code false} otherwise
     */
    public boolean isMidnight24h() {
        return isMidnight(MIDNIGHT_24_HOUR);
    }

    /**
     * Returns a copy of this {@code PartialDateTime} with hour set to {@value #MIDNIGHT_0_HOUR} if this object represents midnight.
     * The day field, if exists, is adjusted accordingly in context of {@code reference}.
     * If this object does not represent midnight or already represents midnight with hour {@value #MIDNIGHT_0_HOUR}, same instance is returned.
     *
     * @param reference
     *         context for day adjust
     *
     * @return a {@code PartialDateTime} based on this partial date-time with hour {@value #MIDNIGHT_0_HOUR} if representing midnight
     */
    public PartialDateTime withMidnight00h(final YearMonth reference) {
        return withMidnightHour(MIDNIGHT_0_HOUR, requireNonNull(reference, "reference"));
    }

    /**
     * Returns a copy of this {@code PartialDateTime} with hour set to {@value #MIDNIGHT_24_HOUR} if this object represents midnight.
     * The day field, if exists, is adjusted accordingly in context of {@code reference}.
     * If this object does not represent midnight or already represents midnight with hour {@value #MIDNIGHT_24_HOUR}, same instance is returned.
     *
     * @param reference
     *         context for day adjust
     *
     * @return a {@code PartialDateTime} based on this partial date-time with hour {@value #MIDNIGHT_24_HOUR} if representing midnight
     */
    public PartialDateTime withMidnight24h(final YearMonth reference) {
        return withMidnightHour(MIDNIGHT_24_HOUR, requireNonNull(reference, "reference"));
    }

    private PartialDateTime withMidnightHour(final int midnightHour, final YearMonth reference) {
        if (!isMidnight() || isMidnight(midnightHour)) {
            return this;
        }
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

    /**
     * Completes this partial date-time into a {@code ZonedDateTime} by applying specified year and month of aviation message issue time into this partial.
     * If this partial date-time is missing zone, UTC (Z) is used as default.
     *
     * @param issueYearMonth
     *         year and month of aviation message issue time
     *
     * @return complete zoned date-time
     *
     * @throws DateTimeException
     *         if day is missing or this partial date-time cannot be completed in context of specified {@code issueYearMonth}
     */
    public ZonedDateTime toZonedDateTime(final YearMonth issueYearMonth) {
        requireNonNull(issueYearMonth, "issueYearMonth");
        final int day = getDay().orElseThrow(() -> new DateTimeException(String.format("%s missing field %s", this, PartialField.DAY)));
        final LocalDate issueDate;
        try {
            issueDate = issueYearMonth.atDay(day);
        } catch (final DateTimeException exception) {
            throw new DateTimeException(String.format("Unable to complete %s with %s", this, issueYearMonth), exception);
        }
        return toZonedDateTime(issueDate);
    }

    /**
     * Completes this partial date-time into a {@code ZonedDateTime} by applying specified date of aviation message issue time into this partial.
     *
     * <p>
     * If this partial date-time is missing day, then {@code issueDate} day of month is used as day. Otherwise {@code issueDate} day is used as reference and
     * if it is greater than day of partial, partial day is assumed to represent a day in next month to reference date.
     * If this partial date-time is missing zone, UTC (Z) is used as default.
     * </p>
     *
     * @param issueDate
     *         date of aviation message issue time
     *
     * @return complete zoned date-time
     *
     * @throws DateTimeException
     *         if this partial date-time cannot be completed in context of specified {@code issueDate}
     */
    public ZonedDateTime toZonedDateTime(final LocalDate issueDate) {
        requireNonNull(issueDate, "issueDate");

        final int day = getDay().orElse(issueDate.getDayOfMonth());
        final int hour = getHour().orElseThrow(() -> new DateTimeException(String.format("%s missing field %s", this, PartialField.HOUR)));
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

    /**
     * Completes this partial date-time into a {@code ZonedDateTime} that is nearest to {@code referenceTime} in time but always after {@code referenceTime}.
     *
     * @param referenceTime
     *         reference time for completion
     *
     * @return a {@code ZonedDateTime} that is nearest to {@code referenceTime} in time but always after {@code referenceTime}
     */
    public ZonedDateTime toZonedDateTimeAfter(final ZonedDateTime referenceTime) {
        requireNonNull(referenceTime, "referenceTime");
        return toZonedDateTimeOnSideOf(referenceTime, ChronoZonedDateTime::isAfter, 1);
    }

    /**
     * Completes this partial date-time into a {@code ZonedDateTime} that is equal or nearest to {@code referenceTime} in time but never before
     * {@code referenceTime}.
     *
     * @param referenceTime
     *         reference time for completion
     *
     * @return a {@code ZonedDateTime} that is equal or nearest to {@code referenceTime} in time but never before {@code referenceTime}
     */
    public ZonedDateTime toZonedDateTimeNotBefore(final ZonedDateTime referenceTime) {
        requireNonNull(referenceTime, "referenceTime");
        return toZonedDateTimeOnSideOf(referenceTime, (candidate, reference) -> !candidate.isBefore(reference), 1);
    }

    /**
     * Completes this partial date-time into a {@code ZonedDateTime} that is nearest to {@code referenceTime} in time but always before {@code referenceTime}.
     *
     * @param referenceTime
     *         reference time for completion
     *
     * @return a {@code ZonedDateTime} that is nearest to {@code referenceTime} in time but always before {@code referenceTime}
     */
    public ZonedDateTime toZonedDateTimeBefore(final ZonedDateTime referenceTime) {
        requireNonNull(referenceTime, "referenceTime");
        return toZonedDateTimeOnSideOf(referenceTime, ChronoZonedDateTime::isBefore, -1);
    }

    /**
     * Completes this partial date-time into a {@code ZonedDateTime} that is equal or nearest to {@code referenceTime} in time but never after
     * {@code referenceTime}.
     *
     * @param referenceTime
     *         reference time for completion
     *
     * @return a {@code ZonedDateTime} that is equal or nearest to {@code referenceTime} in time but never after {@code referenceTime}
     */
    public ZonedDateTime toZonedDateTimeNotAfter(final ZonedDateTime referenceTime) {
        requireNonNull(referenceTime, "referenceTime");
        return toZonedDateTimeOnSideOf(referenceTime, (candidate, reference) -> !candidate.isAfter(reference), -1);
    }

    private ZonedDateTime toZonedDateTimeOnSideOf(final ZonedDateTime referenceTime, final BiPredicate<ZonedDateTime, ZonedDateTime> condition,
            final int fallbackDirection) {
        try {
            @Nullable
            final ZonedDateTime zonedDateTime = toZonedDateTimeOnSideOf(referenceTime, condition, fallbackDirection, referenceTime, fallbackDirection < 0 ? 2 : 1);
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

    /**
     * Completes this partial date-time into a {@code ZonedDateTime} that is equal or nearest to {@code referenceTime} in time,
     * no matter if result is before or after {@code referenceTime}.
     *
     * @param referenceTime
     *         reference time for completion
     *
     * @return a {@code ZonedDateTime} that is equal or nearest to {@code referenceTime} in time
     */
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

    /**
     * Indicates whether this partial date-time represents given temporal.
     * That is, whether all fields and zone that exist in both this partial date-time and provided {@code temporal} are equal.
     *
     * @param temporal
     *         temporal to inspect against
     *
     * @return {@code true} if this partial date-time represents provided {@code temporal}, otherwise {@code false}
     */
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

    /**
     * Returns a TAC string representation of this partial date-time.
     *
     * @return a TAC string representation of this partial date-time
     */
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

    /**
     * Returns a string representation of this partial date-time.
     * The string format is based on ISO 8601 format {@code YYYY-MM-DDTHH:MM<zone>}, where any missing field is simply omitted (represented as an empty string).
     * The date or time part are never omitted completely.
     *
     * <p>
     * Examples:
     * </p>
     *
     * <table>
     * <caption>Examples of PartialDateTime string representations</caption>
     * <tr><th>PartialDateTime</th><th>String representation</th></tr>
     * <tr><td>ofDayHourMinuteZone(31, 8, 10, ZoneId.of("Z"))</td><td>--31T08:10Z</td></tr>
     * <tr><td>ofHourMinuteZone(9, 30, ZoneId.of("+02:00"))</td><td>--T09:30+02:00</td></tr>
     * <tr><td>ofDay(31)</td><td>--31T:</td></tr>
     * <tr><td>ofMinute(10)</td><td>--T:08</td></tr>
     * <tr><td>of(-1, -1, -1, null)</td><td>--T:</td></tr>
     * </table>
     *
     * <p>
     * The returned string may be used as input for {@link #parse(String)}. Formally, when {@code instance} is any instance of {@code PartialDateTime},
     * {@code PartialDateTime.parse(instance.toString()).equals(instance)} is always {@code true}.
     * </p>
     *
     * @return a string representation of this partial date-time
     */
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

    /**
     * Checks whether this {@code PartialDateTime} is equal to other {@code PartialDateTime}.
     * Two {@code PartialDateTime} objects are considered equal, when they contain equal fields and zone with equal values.
     *
     * @param obj
     *         the object to check, null returns false
     *
     * @return {@code true} if this is equal to {@code obj}, {@code false} otherwise
     */
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
        /**
         * Day of month.
         */
        DAY(ChronoField.DAY_OF_MONTH) {
            @Override
            int get(final Temporal temporal, final boolean midnight24h) {
                return (midnight24h ? temporal.minus(1, ChronoUnit.DAYS) : temporal).get(getTemporalField());
            }
        }, //
        /**
         * Hour of day.
         */
        HOUR(ChronoField.HOUR_OF_DAY) {
            @Override
            int get(final Temporal temporal, final boolean midnight24h) {
                return midnight24h ? MIDNIGHT_24_HOUR : temporal.get(getTemporalField());
            }
        }, //
        /**
         * Minute of hour.
         */
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
                throw new DateTimeException(String.format("Field %s value %d is not within range [%d,%d]", this, value, MIN_FIELD_VALUE, MAX_FIELD_VALUE));
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
                throw new DateTimeException("Value out of bounds: " + value);
            }
            return fieldValues & ~(FIELD_MASK << bitIndex) | value << bitIndex;
        }

        /**
         * Returns the corresponding {@link ChronoField}.
         *
         * @return the corresponding {@link ChronoField}
         */
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
