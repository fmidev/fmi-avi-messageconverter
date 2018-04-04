package fi.fmi.avi.model;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;

/**
 * Created by rinne on 27/10/17.
 */
@FreeBuilder
@JsonDeserialize(builder = PartialOrCompleteTimeInstance.Builder.class)
public interface PartialOrCompleteTimeInstance {

    Pattern DAY_HOUR_MINUTE_TZ_PATTERN = Pattern.compile("^(FM)?(?<day>[0-9]{2})?(?<hour>[0-9]{2})(?<minute>[0-9]{2})(?<timezone>[A-Z]+)?$");
    Pattern DAY_HOUR_PATTERN = Pattern.compile("^(?<day>[0-9]{2})(?<hour>[0-9]{2})$");
    Pattern HOUR_PATTERN = Pattern.compile("^(?<hour>[0-9]{2})$");

    /**
     * Indicates the partial time pattern this time instance was created to handle.
     * This pattern is used to complete this time instance. The value must be one of
     * {@link #DAY_HOUR_MINUTE_TZ_PATTERN}, {@link #DAY_HOUR_PATTERN} or {@link #HOUR_PATTERN}.
     *
     * @return the time format pattern
     */
    Pattern partialTimePattern();

    /**
     * Returns the partial part of this time instance as String.
     * The consistency with {@link #partialTimePattern()} is enforced.
     *
     * @return the partial time instance as String.
     */
    String partialTime();

    /**
     * Returns the fully resolved time of this instance, if available.
     *
     * @return the full date time of this time instance
     */
    Optional<ZonedDateTime> completeTime();

    /**
     * Returns the fully resolved date time of this time instance in format
     * {@link DateTimeFormatter#ISO_OFFSET_DATE_TIME}, if available.
     *
     * @return the full date time of this time instance in ISO offset data time format
     */
    Optional<String> completeTimeAsISOString();

    /**
     * Indicates if this time instance was initialized as time '2400' indicating midnight
     * (the last instance of time of the day). As a time instance this is equal to '00:00' of the
     * next day, but it may have implications on how the time is serialized in TAC format.
     *
     * @return
     */
    boolean midnight24h();

    default int partialTimeMinute() {
        Pattern timePattern = partialTimePattern();
        if (timePattern.pattern().contains("?<minute>")) {
            Matcher m = timePattern.matcher(this.partialTime());
            Preconditions.checkState(m.matches(), "Partial time does not '" + partialTime() + "' does not match timePattern '" + timePattern + "'");
            return Integer.parseInt(m.group("minute"));
        } else {
            return -1;
        }
    }

    default int partialTimeHour() {
        Pattern timePattern = partialTimePattern();
        if (timePattern.pattern().contains("?<hour>")) {
            Matcher m = timePattern.matcher(this.partialTime());
            Preconditions.checkState(m.matches(), "Partial time does not '" + partialTime() + "' does not match timePattern '" + timePattern + "'");
            return Integer.parseInt(m.group("hour"));
        } else {
            return -1;
        }
    }

    default int partialTimeDay() {
        Pattern timePattern = partialTimePattern();
        if (timePattern.pattern().contains("?<day>")) {
            Matcher m = timePattern.matcher(this.partialTime());
            Preconditions.checkState(m.matches(), "Partial time does not '" + partialTime() + "' does not match timePattern '" + timePattern + "'");
            return Integer.parseInt(m.group("day"));
        } else {
            return -1;
        }
    }

    Builder toBuilder();

    class Builder extends PartialOrCompleteTimeInstance_Builder {

        public Builder() {
            super();
            midnight24h(false);
        }

        @Override
        public PartialOrCompleteTimeInstance.Builder partialTimePattern(final Pattern pattern) {
            Preconditions.checkArgument(DAY_HOUR_MINUTE_TZ_PATTERN.equals(pattern) || DAY_HOUR_PATTERN.equals(pattern) || HOUR_PATTERN.equals(pattern),
                    "Pattern must be equal to " + DAY_HOUR_MINUTE_TZ_PATTERN + ", " + DAY_HOUR_PATTERN + ", or " + HOUR_PATTERN);
            return super.partialTimePattern(pattern);
        }

        @Override
        public PartialOrCompleteTimeInstance.Builder completeTime(final ZonedDateTime dateTime) {
            PartialOrCompleteTimeInstance.Builder retval = super.completeTime(dateTime);
            String asISODateTime = dateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            if (!completeTimeAsISOString().isPresent() || !completeTimeAsISOString().get().equals(asISODateTime)) {
                retval = retval.completeTimeAsISOString(asISODateTime);
            }
            return retval;
        }

        @Override
        public PartialOrCompleteTimeInstance.Builder completeTimeAsISOString(final String dateTime) {
            ZonedDateTime parsed = ZonedDateTime.parse(dateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            PartialOrCompleteTimeInstance.Builder retval = super.completeTimeAsISOString(dateTime);
            if (!completeTime().isPresent() || !completeTime().get().equals(parsed)) {
                retval = completeTime(parsed);
            }
            return retval;
        }

        public PartialOrCompleteTimeInstance.Builder completedWithYearMonth(final YearMonth yearMonth) throws IllegalArgumentException {
            Pattern timePattern = this.partialTimePattern();
            Preconditions.checkState(timePattern.pattern().contains("?<day>"),
                    "The current timePattern " + timePattern + " does not match dayOfMonth, day " + "must be given to complete. Use method "
                            + "completeWithYearMonthDay(YearMonth, int) or fix the timePattern");
            Matcher m = this.partialTimePattern().matcher(this.partialTime());
            Preconditions.checkState(m.matches(), "Partial time does not '" + partialTime() + "' does not match timePattern '" + timePattern + "'");
            Preconditions.checkState(m.group("day") != null,
                    "Partial time does not contain dayOfMonth, use method " + "completeWithYearMonthDay(YearMonth, int)");
            return completedWithYearMonthDay(yearMonth, Integer.parseInt(m.group("day")));
        }

        public PartialOrCompleteTimeInstance.Builder completedWithYearMonthDay(final YearMonth yearMonth, int dayOfMonth) throws IllegalArgumentException {
            PartialOrCompleteTimeInstance.Builder retval = this;
            Pattern timePattern = this.partialTimePattern();
            Preconditions.checkArgument(yearMonth != null, "yearMonth must not be null");
            Preconditions.checkArgument(dayOfMonth >= 1 && dayOfMonth <= 31, "dayOfMonth must be between 1 and 31");
            Preconditions.checkState(partialTime() != null, "Partial time is null, cannot complete");
            Preconditions.checkState(timePattern.pattern().contains("?<hour>"), "The current timePattern " + timePattern + " does not match hour");
            Matcher m = timePattern.matcher(partialTime());
            Preconditions.checkState(m.matches(), "Partial time '" + partialTime() + "' does not match timePattern '" + partialTimePattern() + "'");

            ZoneId tzId;
            int minute;
            int hour = Integer.parseInt(m.group("hour"));

            if (timePattern.pattern().contains("?<minute>") && m.group("minute") != null) {
                minute = Integer.parseInt(m.group("minute"));
            } else {
                minute = 0;
            }

            if (timePattern.pattern().contains("?<timezone>") && m.group("timezone") != null) {
                tzId = ZoneId.of(m.group("timezone"));
            } else {
                tzId = ZoneId.of("Z");
            }
            ZonedDateTime completeTime;
            try {
                if (hour == 24 && minute == 0) {
                    completeTime = ZonedDateTime.of(LocalDateTime.of(yearMonth.getYear(), yearMonth.getMonth(), dayOfMonth, 0, 0), tzId);
                    completeTime.plusDays(1);
                    retval = midnight24h(true);
                } else {
                    completeTime = ZonedDateTime.of(LocalDateTime.of(yearMonth.getYear(), yearMonth.getMonth(), dayOfMonth, hour, minute), tzId);
                }
                return retval.completeTime(completeTime);
            } catch (DateTimeException dte) {
                throw new IllegalArgumentException("Unable to complete date with given arguments", dte);
            }
        }

    }
}
