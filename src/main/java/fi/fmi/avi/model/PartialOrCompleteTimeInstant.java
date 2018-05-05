package fi.fmi.avi.model;

import static com.google.common.base.Preconditions.checkState;
import static fi.fmi.avi.model.PartialOrCompleteTimeInstant.TimePattern.*;

import java.sql.Time;
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;

/**
 * Created by rinne on 27/10/17.
 */
@FreeBuilder
@JsonDeserialize(builder = PartialOrCompleteTimeInstant.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class PartialOrCompleteTimeInstant extends PartialOrCompleteTime {

    public enum TimePattern {
        DayHourMinuteZone("^(?<day>[0-9]{2})?(?<hour>[0-9]{2})(?<minute>[0-9]{2})(?<timezone>[A-Z]+)$"),
        FromDayHourMinute("^(FM)(?<day>[0-9]{2})?(?<hour>[0-9]{2})(?<minute>[0-9]{2})$"),
        FromHourMinute("^(FM)(?<hour>[0-9]{2})(?<minute>[0-9]{2})$"),
        DayHour("^(?<day>[0-9]{2})(?<hour>[0-9]{2})$"),
        HourMinute("^(?<hour>[0-9]{2})(?<minute>[0-9]{2})$"),
        Hour("^(?<hour>[0-9]{2})$");

        private Pattern p;
        TimePattern(final String pattern) {
            p = Pattern.compile(pattern);
        }

        Matcher matcher(final CharSequence sequence) {
            return p.matcher(sequence);
        }

        boolean contains(final String s) {
            return p.pattern().contains(s);
        }

        boolean matches(final CharSequence sequence) {
            return p.matcher(sequence).matches();
        }
    }

    public static PartialOrCompleteTimeInstant createIssueTime(final String partialDateTime) {
        return new Builder().setPartialTimePattern(DayHourMinuteZone).setPartialTime(partialDateTime).build();
    }

    public static PartialOrCompleteTimeInstant createDayHourInstant(final String partialDateTime) {
        return new Builder().setPartialTimePattern(DayHour).setPartialTime(partialDateTime).build();
    }

    public static PartialOrCompleteTimeInstant createHourInstant(final String partialDateTime) {
        return new Builder().setPartialTimePattern(Hour).setPartialTime(partialDateTime).build();
    }

    public static PartialOrCompleteTimeInstant createHourMinuteInstant(final String partialDateTime) {
        return new Builder().setPartialTimePattern(HourMinute).setPartialTime(partialDateTime).build();
    }

    /**
     * Indicates the partial time pattern this time instance was created to handle.
     * This pattern is used to complete this time instance.
     *
     * @return the time format pattern
     */
    @JsonIgnore
    public abstract Optional<TimePattern> getPartialTimePattern();

    /**
     * Returns the partial part of this time instance as String.
     * The consistency with {@link #getPartialTimePattern()} is enforced.
     *
     * @return the partial time instance as String.
     */
    public abstract Optional<String> getPartialTime();

    /**
     * Returns the fully resolved time of this instance, if available.
     *
     * @return the full date time of this time instance
     */
    public abstract Optional<ZonedDateTime> getCompleteTime();

    /**
     * Returns the fully resolved date time of this time instance in format
     * {@link DateTimeFormatter#ISO_OFFSET_DATE_TIME}, if available.
     *
     * @return the full date time of this time instance in ISO offset data time format
     */
    public abstract Optional<String> getCompleteTimeAsISOString();

    /**
     * Indicates if this time instance was initialized as time '2400' indicating midnight
     * (the last instance of time of the day). As a time instance this is equal to '00:00' of the
     * next day, but it may have implications on how the time is serialized in TAC format.
     *
     * @return
     */
    @JsonIgnore
    public abstract boolean isMidnight24h();

    @JsonIgnore
    public int getMinute() {
        if (getCompleteTime().isPresent()) {
            return getCompleteTime().get().getMinute();
        } else if (getPartialTime().isPresent() && getPartialTimePattern().isPresent()) {
            TimePattern timePattern = getPartialTimePattern().get();
            if (timePattern.contains("?<minute>")) {
                Matcher m = timePattern.matcher(this.getPartialTime().get());
                checkState(m.matches(), "Partial time '" + getPartialTime().get() + "' does not match timePattern '" + timePattern + "'");
                return Integer.parseInt(m.group("minute"));
            }
        }
        return -1;
    }

    @JsonIgnore
    public int getHour() {
        if (getCompleteTime().isPresent()) {
            return getCompleteTime().get().getHour();
        } else if (getPartialTime().isPresent() && getPartialTimePattern().isPresent()) {
            TimePattern timePattern = getPartialTimePattern().get();
            if (timePattern.contains("?<hour>")) {
                Matcher m = timePattern.matcher(getPartialTime().get());
                checkState(m.matches(), "Partial time '" + getPartialTime().get() + "' does not match timePattern '" + timePattern + "'");
                return Integer.parseInt(m.group("hour"));
            }
        }
        return -1;
    }

    @JsonIgnore
    public int getDay() {
        if (getCompleteTime().isPresent()) {
            return getCompleteTime().get().getDayOfMonth();
        } else if (getPartialTime().isPresent() && getPartialTimePattern().isPresent()) {
            TimePattern timePattern = getPartialTimePattern().get();
            if (timePattern.contains("?<day>")) {
                Matcher m = timePattern.matcher(getPartialTime().get());
                checkState(m.matches(), "Partial time '" + getPartialTime().get() + "' does not match timePattern '" + timePattern + "'");
                return Integer.parseInt(m.group("day"));
            }
        }
        return -1;
    }

    abstract Builder toBuilder();

    public static class Builder extends PartialOrCompleteTimeInstant_Builder {

        public static Pattern TREND_TIME_GROUP = Pattern.compile("^(?<kind>AT)(?<hour>[0-9]{2})(?<minute>[0-9]{2})$");

        public Builder withTrendTimeGroupToken(final String token) {
            Matcher m = TREND_TIME_GROUP.matcher(token);
            if (m.matches()) {
                return setPartialTimePattern(HourMinute).setPartialTime(m.group("hour") + m.group("minute"));
            } else {
                throw new IllegalArgumentException("token does not match pattern " + TREND_TIME_GROUP);
            }
        }

        @Override
        public PartialOrCompleteTimeInstant build() {
            if (getPartialTime().isPresent() && !getPartialTimePattern().isPresent()) {
                setPartialTimePattern(tryDeterminePatternFor(getPartialTime().get()));
                checkState(getPartialTimePattern().isPresent(), "Could not automatically determine date time pattern for '" + getPartialTime().get() + "'");
            }
            ensureMidnight24Updated();
            return super.build();
        }

        @Override
        public PartialOrCompleteTimeInstant.Builder setCompleteTime(final ZonedDateTime dateTime) {
            PartialOrCompleteTimeInstant.Builder retval = super.setCompleteTime(dateTime);
            String asISODateTime = dateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            if (!getCompleteTimeAsISOString().isPresent() || !getCompleteTimeAsISOString().get().equals(asISODateTime)) {
                retval = retval.setCompleteTimeAsISOString(asISODateTime);
            }
            return retval;
        }

        @Override
        public PartialOrCompleteTimeInstant.Builder setCompleteTimeAsISOString(final String dateTime) {
            ZonedDateTime parsed = ZonedDateTime.parse(dateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            PartialOrCompleteTimeInstant.Builder retval = super.setCompleteTimeAsISOString(dateTime);
            if (!getCompleteTime().isPresent() || !getCompleteTime().get().equals(parsed)) {
                retval = setCompleteTime(parsed);
            }
            return retval;
        }

        public PartialOrCompleteTimeInstant.Builder completedWithIssueYearMonth(final YearMonth issueYearMonth) throws IllegalArgumentException {
            checkState(getPartialTime().isPresent() && getPartialTimePattern().isPresent(), "partialTime and partialTimePattern must be present");
            TimePattern timePattern = getPartialTimePattern().get();
            checkState(timePattern.contains("?<day>"),
                    "The current timePattern " + timePattern + " does not match dayOfMonth, day " + "must be given to complete. Use method "
                            + "completeWithYearMonthDay(YearMonth, int) or fix the timePattern");
            Matcher m = timePattern.matcher(getPartialTime().get());
            checkState(m.matches(), "Partial time does not '" + getPartialTime() + "' does not match timePattern '" + timePattern + "'");
            checkState(m.group("day") != null,
                    "Partial time does not contain dayOfMonth, use method " + "completeWithYearMonthDay(YearMonth, int)");
            return completedWithIssueYearMonthDay(issueYearMonth, Integer.parseInt(m.group("day")));
        }

        public PartialOrCompleteTimeInstant.Builder completedWithIssueYearMonthDay(final YearMonth issueYearMonth, int issueDayOfMonth)
                throws IllegalArgumentException {
            checkState(getPartialTime().isPresent() && getPartialTimePattern().isPresent(), "partialTime and partialTimePattern must be present");
            PartialOrCompleteTimeInstant.Builder retval = this;
            String partialTime = getPartialTime().get();
            TimePattern timePattern = getPartialTimePattern().get();
            Preconditions.checkArgument(issueYearMonth != null, "issueYearMonth must not be null");
            Preconditions.checkArgument(issueDayOfMonth >= 1 && issueDayOfMonth <= 31, "issueDayOfMonth must be between 1 and 31");
            checkState(partialTime != null, "Partial time is null, cannot complete");
            checkState(timePattern.contains("?<hour>"), "The current timePattern " + timePattern + " does not match hour");
            Matcher m = timePattern.matcher(partialTime);
            checkState(m.matches(), "Partial time '" + partialTime + "' does not match timePattern '" + timePattern + "'");
            ensureMidnight24Updated();

            ZoneId tzId;
            int minute;
            int day;

            if (timePattern.contains("?<day>")) {
                String ds = m.group("day");
                if (ds != null) {
                    day = Integer.parseInt(ds);
                } else {
                    day = issueDayOfMonth;
                }
            } else {
                day = issueDayOfMonth;
            }

            int hour = Integer.parseInt(m.group("hour"));

            if (timePattern.contains("?<minute>") && m.group("minute") != null) {
                minute = Integer.parseInt(m.group("minute"));
            } else {
                minute = 0;
            }

            if (timePattern.contains("?<timezone>") && m.group("timezone") != null) {
                tzId = ZoneId.of(m.group("timezone"));
            } else {
                tzId = ZoneId.of("Z");
            }
            ZonedDateTime completeTime;
            try {
                if (isMidnight24h()) {
                    completeTime = ZonedDateTime.of(LocalDateTime.of(issueYearMonth.getYear(), issueYearMonth.getMonth(), day, 0, 0), tzId);
                    completeTime.plusDays(1);
                } else {
                    completeTime = ZonedDateTime.of(LocalDateTime.of(issueYearMonth.getYear(), issueYearMonth.getMonth(), day, hour, minute), tzId);
                }
                if (day < issueDayOfMonth) {
                    //issue day > my current day, assume next month:
                    completeTime = completeTime.plusMonths(1);
                }
                return retval.setCompleteTime(completeTime);
            } catch (DateTimeException dte) {
                throw new IllegalArgumentException("Unable to complete date with given arguments", dte);
            }
        }

        private void ensureMidnight24Updated() {
            if (!getPartialTime().isPresent()) {
                return;
            }
            checkState(getPartialTime().isPresent() && getPartialTimePattern().isPresent(), "partialTime and partialTimePattern must be present");
            TimePattern timePattern = getPartialTimePattern().get();
            String partialTime = getPartialTime().get();
            checkState(timePattern.contains("?<hour>"), "The current timePattern " + timePattern + " does not match hour");
            Matcher m = timePattern.matcher(partialTime);
            checkState(m.matches(), "Partial time '" + partialTime + "' does not match timePattern '" + timePattern + "'");
            int hour = Integer.parseInt(m.group("hour"));
            int minute;
            if (timePattern.contains("?<minute>") && m.group("minute") != null) {
                minute = Integer.parseInt(m.group("minute"));
            } else {
                minute = 0;
            }
            setMidnight24h(hour == 24 && minute == 0);
        }

        private Optional<TimePattern> tryDeterminePatternFor(final String value) {
            if (FromDayHourMinute.matches(value)) {
                return Optional.of(FromDayHourMinute);
            }

            if (FromHourMinute.matches(value)) {
                return Optional.of(FromHourMinute);
            }

            if (DayHourMinuteZone.matches(value)) {
                return Optional.of(DayHourMinuteZone);
            }

            if (Hour.matches(value)) {
                return Optional.of(Hour);
            }

            return Optional.empty();
        }
    }
}
