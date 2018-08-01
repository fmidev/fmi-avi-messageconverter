package fi.fmi.avi.model;

import static fi.fmi.avi.model.PartialOrCompleteTimeInstant.TimePattern.DayHour;
import static fi.fmi.avi.model.PartialOrCompleteTimeInstant.TimePattern.DayHourMinute;
import static fi.fmi.avi.model.PartialOrCompleteTimeInstant.TimePattern.DayHourMinuteZone;
import static fi.fmi.avi.model.PartialOrCompleteTimeInstant.TimePattern.Hour;
import static fi.fmi.avi.model.PartialOrCompleteTimeInstant.TimePattern.HourMinute;
import static org.inferred.freebuilder.shaded.com.google.common.base.Preconditions.checkArgument;
import static org.inferred.freebuilder.shaded.com.google.common.base.Preconditions.checkState;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Created by rinne on 27/10/17.
 */
@FreeBuilder
@JsonDeserialize(builder = PartialOrCompleteTimeInstant.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"completeTime", "partialTime", "partialTimePattern"})
public abstract class PartialOrCompleteTimeInstant extends PartialOrCompleteTime {

    public enum TimePattern {
        DayHourMinuteZone("^(?<day>[0-9]{2})?(?<hour>[0-9]{2})(?<minute>[0-9]{2})(?<timezone>[A-Z]+)$"),
        DayHourMinute("^(?<day>[0-9]{2})?(?<hour>[0-9]{2})(?<minute>[0-9]{2})$"),
        HourMinute("^(?<hour>[0-9]{2})(?<minute>[0-9]{2})$"),
        DayHour("^(?<day>[0-9]{2})(?<hour>[0-9]{2})$"),
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
    @JsonFormat( pattern="yyyy-MM-dd'T'HH:mm:ssXXX", shape=JsonFormat.Shape.STRING)
    public abstract Optional<ZonedDateTime> getCompleteTime();

    /**
     * Indicates if this time instance was initialized as time '2400' indicating midnight
     * (the last instance of time of the day). As a time instance this is equal to '00:00' of the
     * next day, but it may have implications on how the time is serialized in TAC format.
     *
     * @return true if initiated as partial time "2400"
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
                Matcher m = timePattern.matcher(getPartialTime().get());
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

    public boolean equals(Object o) {
        if (o instanceof PartialOrCompleteTimeInstant) {
            PartialOrCompleteTimeInstant toMatch = (PartialOrCompleteTimeInstant) o;
            if (this.getCompleteTime().isPresent() && toMatch.getCompleteTime().isPresent()) {
                return this.getCompleteTime().equals(toMatch.getCompleteTime());
            } else if (this.getPartialTime().isPresent() && this.getPartialTimePattern().isPresent() && toMatch.getPartialTime().isPresent()
                    && toMatch.getPartialTimePattern().isPresent()) {
                return this.getPartialTime().equals(toMatch.getPartialTime()) && this.getPartialTimePattern().equals(toMatch.getPartialTimePattern());
            } else {
                return super.equals(o);
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        if (this.getCompleteTime().isPresent()) {
            return this.getCompleteTime().hashCode();
        } else if (this.getPartialTime().isPresent() && this.getPartialTimePattern().isPresent()) {
            return Objects.hash(this.getPartialTime().get(), this.getPartialTimePattern().get());
        } else {
            return super.hashCode();
        }
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

        public PartialOrCompleteTimeInstant.Builder completedWithIssueYearMonth(final YearMonth issueYearMonth) throws IllegalArgumentException {
            if (getCompleteTime().isPresent()) {
                return completedWithIssueYearMonthDay(issueYearMonth, getCompleteTime().get().getDayOfMonth());
            } else {
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
        }

        public PartialOrCompleteTimeInstant.Builder completedWithIssueYearMonthDay(final YearMonth issueYearMonth, int issueDayOfMonth)
                throws IllegalArgumentException {
            if (getCompleteTime().isPresent()) {
                return this.setCompleteTime(ZonedDateTime.from(getCompleteTime().get())
                        .withYear(issueYearMonth.getYear())
                        .withMonth(issueYearMonth.getMonthValue())
                        .withDayOfMonth(issueDayOfMonth));
            } else {
                checkState(getPartialTime().isPresent() && getPartialTimePattern().isPresent(), "partialTime and partialTimePattern must be present");
                PartialOrCompleteTimeInstant.Builder retval = this;
                String partialTime = getPartialTime().get();
                TimePattern timePattern = getPartialTimePattern().get();
                checkArgument(issueYearMonth != null, "issueYearMonth must not be null");
                checkArgument(issueDayOfMonth >= 1 && issueDayOfMonth <= 31, "issueDayOfMonth must be between 1 and 31");
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
        }

        private void ensureMidnight24Updated() {
            if (!getPartialTime().isPresent()) {
                setMidnight24h(false);
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
            if (DayHourMinuteZone.matches(value)) {
                return Optional.of(DayHourMinuteZone);
            }

            if (DayHourMinute.matches(value)) {
                return Optional.of(DayHourMinute);
            }

            if (HourMinute.matches(value)) {
                return Optional.of(HourMinute);
            }

            if (Hour.matches(value)) {
                return Optional.of(Hour);
            }

            return Optional.empty();
        }
    }
}
