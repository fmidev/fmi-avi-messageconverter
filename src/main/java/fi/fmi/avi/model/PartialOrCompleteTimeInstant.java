package fi.fmi.avi.model;

import static java.util.Objects.requireNonNull;

import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.EnumSet;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Function;

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

    private static final long serialVersionUID = -3820077096763961462L;

    public static Builder builder() {
        return new Builder();
    }

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
        return builder().setPartialTime(partialDateTime).build();
    }

    public static PartialOrCompleteTimeInstant of(final ZonedDateTime completeTime) {
        return builder().setCompleteTime(completeTime).build();
    }

    public static PartialOrCompleteTimeInstant of(final PartialDateTime partialDateTime, final ZonedDateTime completeTime) {
        requireNonNull(partialDateTime, "partialDateTime");
        requireNonNull(completeTime, "completeTime");
        return builder()//
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

    public abstract Builder toBuilder();

    public static class Builder extends PartialOrCompleteTimeInstant_Builder {

        @Deprecated
        public Builder() {
        }

        @Override
        public PartialOrCompleteTimeInstant build() {
            getPartialTime().ifPresent(partialTime -> //
                    getCompleteTime().ifPresent(completeTime -> {
                        if (!partialTime.representsStrict(completeTime)) {
                            throw new IllegalStateException(String.format("completeTime %s does not represent partialTime %s", completeTime, partialTime));
                        }
                    }));
            return super.build();
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

        public Builder completePartialAt(final YearMonth issueYearMonth) {
            if (getPartialTime().isPresent()) {
                return setCompleteTime(getPartialTime().get().toZonedDateTime(issueYearMonth));
            } else if (getCompleteTime().isPresent()) {
                return mapCompleteTime(completeTime -> PartialDateTime.ofDayHourMinuteZone(completeTime, false).toZonedDateTime(issueYearMonth));
            } else {
                throw new IllegalStateException("Neither of partialTime or completeTime is present");
            }
        }

        public Builder completePartialAt(final YearMonth issueYearMonth, final int issueDayOfMonth) {
            if (getPartialTime().isPresent()) {
                return setCompleteTime(getPartialTime().get().toZonedDateTime(issueYearMonth.atDay(issueDayOfMonth)));
            } else if (getCompleteTime().isPresent()) {
                return mapCompleteTime(
                        completeTime -> PartialDateTime.ofDayHourMinuteZone(completeTime, false).toZonedDateTime(issueYearMonth.atDay(issueDayOfMonth)));
            } else {
                throw new IllegalStateException("Neither of partialTime or completeTime is present");
            }
        }

        public Builder completePartialNear(final ZonedDateTime reference) {
            requireNonNull(reference, "reference");
            return completePartial(partial -> partial.toZonedDateTimeNear(reference));
        }

        public Builder completePartial(final Function<PartialDateTime, ZonedDateTime> completion) {
            requireNonNull(completion, "completion");
            return setCompleteTime(getPartialTime().map(completion));
        }
    }
}
