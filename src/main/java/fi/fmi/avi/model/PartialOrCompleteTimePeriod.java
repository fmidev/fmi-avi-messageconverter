package fi.fmi.avi.model;

import static java.util.Objects.requireNonNull;

import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.PartialDateTime.PartialField;

/**
 * Created by rinne on 04/04/2018.
 */
@FreeBuilder
@JsonDeserialize(builder = PartialOrCompleteTimePeriod.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "startTime", "endTime" })
public abstract class PartialOrCompleteTimePeriod extends PartialOrCompleteTime {

    private static final Pattern DAY_HOUR_HOUR_PATTERN = Pattern.compile("^(?<day>[0-9]{2})(?<startHour>[0-9]{2})(?<endHour>[0-9]{2})$");
    private static final Pattern DAY_HOUR_DAY_HOUR_PATTERN = Pattern.compile(
            "^(?<startDay>[0-9]{2})(?<startHour>[0-9]{2})/(?<endDay>[0-9]{2})(?<endHour>[0-9]{2})$");

    public static List<PartialOrCompleteTime> completeAscendingPartialTimes(final Iterable<? extends PartialOrCompleteTime> input,
            final ZonedDateTime referenceTime) {
        requireNonNull(input, "input");
        requireNonNull(referenceTime, "referenceTime");

        final List<PartialOrCompleteTime> result = input instanceof Collection ? new ArrayList<>(((Collection<?>) input).size()) : new ArrayList<>();
        final ZonedDateTime[] completionFloor = new ZonedDateTime[] { referenceTime }; // Use as mutable reference
        //Assumption: the start times come in chronological order, but the periods may be (partly) overlapping
        int index = 0;
        for (final PartialOrCompleteTime partialOrCompleteTime : input) {
            if (partialOrCompleteTime == null) {
                throw new NullPointerException("null element at index " + index);
            } else if (partialOrCompleteTime instanceof PartialOrCompleteTimeInstant) {
                final PartialOrCompleteTimeInstant completed = ((PartialOrCompleteTimeInstant) partialOrCompleteTime).toBuilder()
                        .completePartial(partial -> partial.toZonedDateTimeNear(completionFloor[0]))
                        .build();
                result.add(completed);
                completionFloor[0] = completed.getCompleteTime().orElse(completionFloor[0]);
            } else if (partialOrCompleteTime instanceof PartialOrCompleteTimePeriod) {
                final PartialOrCompleteTimePeriod completed = ((PartialOrCompleteTimePeriod) partialOrCompleteTime).toBuilder()//
                        .completePartial(partial -> partial.toZonedDateTimeNear(completionFloor[0]))//
                        .build();
                result.add(completed);
                completionFloor[0] = completed.getStartTime()//
                        .map(PartialOrCompleteTimeInstant::getCompleteTime)//
                        .orElse(completed.getEndTime()//
                                .flatMap(PartialOrCompleteTimeInstant::getCompleteTime))//
                        .orElse(completionFloor[0]);
            } else {
                throw new IllegalArgumentException("Unknown PartialOrCompleteTime: " + partialOrCompleteTime.getClass() + " <" + partialOrCompleteTime + ">");
            }
            index += 1;
        }

        return result.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(result);
    }

    public static PartialOrCompleteTimePeriod completePartialTimeReference(final PartialOrCompleteTimePeriod input, final ZonedDateTime reference) {
        requireNonNull(input, "input");
        requireNonNull(reference, "reference");
        return input.toBuilder().completePartialStartingNear(reference).build();
    }

    public static PartialOrCompleteTimePeriod createValidityTimeDHDH(final String partialTimePeriod) throws IllegalArgumentException {
        final Matcher matcher = DAY_HOUR_DAY_HOUR_PATTERN.matcher(partialTimePeriod);
        if (matcher.matches()) {
            return new PartialOrCompleteTimePeriod.Builder()//
                    .setStartTime(PartialOrCompleteTimeInstant.of(
                            PartialDateTime.ofDayHour(TimePatternGroup.startDay.intValue(matcher), TimePatternGroup.startHour.intValue(matcher))))//
                    .setEndTime(PartialOrCompleteTimeInstant.of(
                            PartialDateTime.ofDayHour(TimePatternGroup.endDay.intValue(matcher), TimePatternGroup.endHour.intValue(matcher))))//
                    .build();
        } else {
            throw new IllegalArgumentException("time period does not match pattern " + DAY_HOUR_DAY_HOUR_PATTERN);
        }
    }

    public static PartialOrCompleteTimePeriod createValidityTimeDHH(final String partialTimePeriod) throws IllegalArgumentException {
        final Matcher matcher = DAY_HOUR_HOUR_PATTERN.matcher(partialTimePeriod);
        if (matcher.matches()) {
            return new PartialOrCompleteTimePeriod.Builder()//
                    .setStartTime(PartialOrCompleteTimeInstant.of(
                            PartialDateTime.ofDayHour(TimePatternGroup.day.intValue(matcher), TimePatternGroup.startHour.intValue(matcher))))//
                    .setEndTime(PartialOrCompleteTimeInstant.of(
                            PartialDateTime.ofDayHour(TimePatternGroup.day.intValue(matcher), TimePatternGroup.endHour.intValue(matcher))))//
                    .build();
        } else {
            throw new IllegalArgumentException("time period does not match pattern " + DAY_HOUR_HOUR_PATTERN);
        }
    }

    public static PartialOrCompleteTimePeriod createValidityTime(final String partialTimePeriod) throws IllegalArgumentException {
        try {
            return createValidityTimeDHDH(partialTimePeriod);
        } catch (final IllegalArgumentException iae) {
            return createValidityTimeDHH(partialTimePeriod);
        }
    }

    public abstract Optional<PartialOrCompleteTimeInstant> getStartTime();

    public abstract Optional<PartialOrCompleteTimeInstant> getEndTime();

    public abstract Builder toBuilder();

    @JsonIgnore
    public boolean isComplete() {
        final Optional<PartialOrCompleteTimeInstant> start = getStartTime();
        final Optional<PartialOrCompleteTimeInstant> end = getEndTime();
        if (start.isPresent()) {
            if (!start.get().getCompleteTime().isPresent()) {
                return false;
            }
        }
        if (end.isPresent()) {
            return end.get().getCompleteTime().isPresent();
        }
        return true;
    }

    private enum TimePatternGroup {
        day, startDay, endDay, hour, startHour, endHour, minute;

        public int intValue(final Matcher matcher) {
            return Integer.parseInt(stringValue(matcher));
        }

        public String stringValue(final Matcher matcher) {
            return matcher.group(name());
        }
    }

    public static class Builder extends PartialOrCompleteTimePeriod_Builder {
        public Builder setTrendTimeGroupToken(final String token) {
            requireNonNull(token, "token");
            final String kind = token.substring(0, Math.min(2, token.length()));
            final String time = token.substring(Math.min(2, token.length()));
            final PartialOrCompleteTimeInstant partial = PartialOrCompleteTimeInstant.of(
                    PartialDateTime.parseTACStringStrict(time, EnumSet.of(PartialField.HOUR, PartialField.MINUTE), false));
            if ("FM".equals(kind)) {
                return setStartTime(partial);
            } else if ("TL".equals(kind)) {
                return setEndTime(partial);
            } else {
                throw new IllegalArgumentException("token does not begin with FM or TL: " + token);
            }
        }

        @Override
        public Builder setEndTime(final PartialOrCompleteTimeInstant time) {
            return super.setEndTime(tryToMidnight24h(time));
        }

        private PartialOrCompleteTimeInstant tryToMidnight24h(final PartialOrCompleteTimeInstant time) {
            if (time.getPartialTime().map(PartialDateTime::isMidnight24h).orElse(true) || !time.getCompleteTime().isPresent()) {
                return time;
            }
            final ZonedDateTime completeTime = time.getCompleteTime().get();
            return time.toBuilder()//
                    .mapPartialTime(partialTime -> partialTime.withMidnight24h(YearMonth.from(completeTime)))//
                    .build();
        }

        public Builder completePartialStartingNear(final ZonedDateTime reference) {
            requireNonNull(reference, "reference");
            return completePartial(partial -> partial.toZonedDateTimeNear(reference));
        }

        public Builder completePartial(final Function<PartialDateTime, ZonedDateTime> startCompletion) {
            requireNonNull(startCompletion, "startCompletion");
            mapStartTime(partialOrComplete -> partialOrComplete.toBuilder().completePartial(startCompletion).build());
            return mapEndTime(partialOrComplete -> partialOrComplete.toBuilder()//
                    .completePartial(getStartTime()//
                            .flatMap(PartialOrCompleteTimeInstant::getCompleteTime)
                            .map(completeTime -> (Function<PartialDateTime, ZonedDateTime>) partial -> partial.toZonedDateTimeAfter(completeTime))
                            .orElse(startCompletion))//
                    .build());
        }

        public Builder completePartialEndingNear(final ZonedDateTime reference) {
            requireNonNull(reference, "reference");
            return completePartialBackwards(partial -> partial.toZonedDateTimeNear(reference));
        }

        public Builder completePartialBackwards(final Function<PartialDateTime, ZonedDateTime> endCompletion) {
            requireNonNull(endCompletion, "endCompletion");
            mapEndTime(partialOrComplete -> partialOrComplete.toBuilder()//
                    .completePartial(endCompletion)//
                    .build());
            return mapStartTime(partialOrComplete -> partialOrComplete.toBuilder()//
                    .completePartial(getStartTime()//
                            .flatMap(PartialOrCompleteTimeInstant::getCompleteTime)//
                            .map(zonedDateTime -> (Function<PartialDateTime, ZonedDateTime>) partial -> partial.toZonedDateTimeBefore(zonedDateTime))//
                            .orElse(endCompletion))//
                    .build());
        }
    }
}
