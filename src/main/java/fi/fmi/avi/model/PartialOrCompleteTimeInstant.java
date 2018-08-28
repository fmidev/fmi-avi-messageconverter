package fi.fmi.avi.model;

import static java.util.Objects.requireNonNull;

import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.EnumSet;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.PartialDateTime.PartialField;

/**
 * Created by rinne on 27/10/17.
 */
@FreeBuilder
@JsonDeserialize(builder = PartialOrCompleteTimeInstant.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "completeTime", "partialTime", "partialTimePattern" })
public abstract class PartialOrCompleteTimeInstant extends PartialOrCompleteTime {

    public static PartialOrCompleteTimeInstant createIssueTime(final String partialDateTime) {
        return of(PartialDateTime.parseTACString(partialDateTime, PartialField.MINUTE));
    }

    public static PartialOrCompleteTimeInstant createDayHourInstant(final String partialDateTime) {
        return of(PartialDateTime.parseTACString(partialDateTime, PartialField.HOUR));
    }

    public static PartialOrCompleteTimeInstant createHourInstant(final String partialDateTime) {
        return of(PartialDateTime.parseTACString(partialDateTime, PartialField.HOUR));
    }

    public static PartialOrCompleteTimeInstant createHourMinuteInstant(final String partialDateTime) {
        return of(PartialDateTime.parseTACString(partialDateTime, PartialField.MINUTE));
    }

    public static PartialOrCompleteTimeInstant of(final PartialDateTime partialDateTime) {
        return new Builder().setPartialTime(partialDateTime).build();
    }

    public static PartialOrCompleteTimeInstant of(final ZonedDateTime completeTime) {
        return new Builder().setCompleteTime(completeTime).build();
    }

    public static PartialOrCompleteTimeInstant of(final PartialDateTime partialDateTime, final ZonedDateTime completeTime) {
        requireNonNull(partialDateTime, "partialDateTime");
        requireNonNull(completeTime, "completeTime");
        return new Builder()//
                .setPartialTime(partialDateTime)//
                .setCompleteTime(completeTime)//
                .build();
    }

    public static PartialOrCompleteTimeInstant of(final ZonedDateTime completeTime, final Set<PartialField> partialFields, final boolean partialHasZone,
            final int midnightHour) {
        requireNonNull(completeTime, "completeTime");
        requireNonNull(partialFields, "partialFields");
        return of(PartialDateTime.of(completeTime, partialFields, partialHasZone, midnightHour), completeTime);
    }

    @Deprecated
    @JsonIgnore
    public Optional<String> getPartialTimeString() {
        return getPartialTime().map(PartialDateTime::toTACString);
    }

    public abstract Optional<PartialDateTime> getPartialTime();

    /**
     * Returns the fully resolved time of this instance, if available.
     *
     * @return the full date time of this time instance
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", shape = JsonFormat.Shape.STRING)
    public abstract Optional<ZonedDateTime> getCompleteTime();

    /**
     * Indicates if this time instance was initialized as time '2400' indicating midnight
     * (the last instance of time of the day). As a time instance this is equal to '00:00' of the
     * next day, but it may have implications on how the time is serialized in TAC format.
     *
     * @return true if initiated as partial time "2400"
     */
    @JsonIgnore
    public boolean isMidnight24h() {
        return getPartialTime().map(PartialDateTime::isMidnight24h).orElse(false);
    }

    /**
     * Returns the minute-of-hour of this PartialOrCompleteTimeInstant.
     *
     * @return the minute-of-hour
     */
    @JsonIgnore
    public OptionalInt getMinute() {
        return getPartialTime()//
                .map(PartialDateTime::getMinute)//
                .orElseGet(() -> getCompleteTime()//
                        .map(completeTime -> OptionalInt.of(completeTime.getMinute()))//
                        .orElse(OptionalInt.empty()));
    }

    /**
     * Returns the hour-of-day of this PartialOrCompleteTimeInstant. Note that
     * if the {@link #isMidnight24h()} is true, and the complete time is present,
     * the returned value is always 24.
     *
     * @return the hour-of-day
     */
    @JsonIgnore
    public OptionalInt getHour() {
        return getPartialTime()//
                .map(PartialDateTime::getHour)//
                .orElseGet(() -> getCompleteTime()//
                        .map(completeTime -> OptionalInt.of(completeTime.getHour()))//
                        .orElse(OptionalInt.empty()));
    }

    /**
     * Returns the day-of-month of this PartialOrCompleteTimeInstant. Note that
     * if the {@link #isMidnight24h()} is true, and the complete date-time is present
     * the returned value is the previous day-of-month of the complete date-time.
     *
     * @return the day-of-month
     */
    @JsonIgnore
    public OptionalInt getDay() {
        return getPartialTime()//
                .map(PartialDateTime::getDay)//
                .orElseGet(() -> getCompleteTime()//
                        .map(completeTime -> OptionalInt.of(completeTime.getDayOfMonth()))//
                        .orElse(OptionalInt.empty()));
    }

    abstract Builder toBuilder();

    public static class Builder extends PartialOrCompleteTimeInstant_Builder {
        public Builder() {
            getPartialTime().ifPresent(partialTime -> //
                    getCompleteTime().ifPresent(completeTime -> {
                        if (!partialTime.represents(completeTime)) {
                            throw new IllegalStateException(String.format("completeTime %s does not represent partialTime %s", completeTime, partialTime));
                        }
                    }));
        }

        public Builder setTrendTimeGroupToken(final String token) {
            requireNonNull(token, "token");
            final String kind = token.substring(0, Math.min(2, token.length()));
            final String time = token.substring(Math.min(2, token.length()));
            final PartialDateTime partial = PartialDateTime.parseTACStringStrict(time, EnumSet.of(PartialField.HOUR, PartialField.MINUTE), false);
            if ("AT".equals(kind)) {
                return setPartialTime(partial);
            } else {
                throw new IllegalArgumentException("token does not begin with FM or TL: " + token);
            }
        }

        public PartialOrCompleteTimeInstant.Builder completedWithIssueYearMonth(final YearMonth issueYearMonth) {
            if (getCompleteTime().isPresent()) {
                return completedWithIssueYearMonthDay(issueYearMonth, getCompleteTime().get().getDayOfMonth());
            } else {
                return setCompleteTime(getPartialTime()//
                        .orElseThrow(() -> new IllegalStateException("partialTime must be present"))//
                        .toZonedDateTime(issueYearMonth));
            }
        }

        public PartialOrCompleteTimeInstant.Builder completedWithIssueYearMonthDay(final YearMonth issueYearMonth, final int issueDayOfMonth) {
            if (getCompleteTime().isPresent()) {
                return this.setCompleteTime(ZonedDateTime.from(getCompleteTime().get())
                        .withYear(issueYearMonth.getYear())
                        .withMonth(issueYearMonth.getMonthValue())
                        .withDayOfMonth(issueDayOfMonth));
            } else {
                return setCompleteTime(getPartialTime()//
                        .orElseThrow(() -> new IllegalStateException("partialTime must be present"))//
                        .toZonedDateTime(issueYearMonth.atDay(issueDayOfMonth)));
            }
        }
    }
}
