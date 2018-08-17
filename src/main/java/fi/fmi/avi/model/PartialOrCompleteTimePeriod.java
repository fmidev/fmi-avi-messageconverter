package fi.fmi.avi.model;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    public static List<PartialOrCompleteTime> completePartialTimeReferenceList(final List<? extends PartialOrCompleteTime> input,
            final ZonedDateTime referenceTime) {
        requireNonNull(input, "Input list cannot be null");

        //Assumption: the start times come in chronological order, but the periods may be (partly) overlapping
        final List<PartialOrCompleteTime> revisedList = new ArrayList<>(input.size());
        KeyTimePair<?> kpp = new KeyTimePair<>();
        kpp.key = referenceTime;
        for (final PartialOrCompleteTime periodOrInstant : input) {
            kpp = completePartialTimeReferenceInternal(periodOrInstant, kpp.key);
            revisedList.add(kpp.time);
        }
        return revisedList;
    }

    public static PartialOrCompleteTimePeriod completePartialTimeReference(final PartialOrCompleteTimePeriod input, final ZonedDateTime key) {
        return (PartialOrCompleteTimePeriod) completePartialTimeReferenceInternal(input, key).time;
    }

    public static PartialOrCompleteTimePeriod completePartialTimeReferenceBackwards(final PartialOrCompleteTimePeriod input, final ZonedDateTime key) {
        return (PartialOrCompleteTimePeriod) completePartialTimeReferenceInternalBackwards(input, key).time;
    }

    public static PartialOrCompleteTimeInstant completePartialTimeReference(final PartialOrCompleteTimeInstant input, final ZonedDateTime key) {
        return (PartialOrCompleteTimeInstant) completePartialTimeReferenceInternal(input, key).time;
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

    private static KeyTimePair<PartialOrCompleteTime> completePartialTimeReferenceInternal(final PartialOrCompleteTime input, final ZonedDateTime key) {
        if (input != null) {
            if (input instanceof PartialOrCompleteTimePeriod) {
                final PartialOrCompleteTimePeriod period = (PartialOrCompleteTimePeriod) input;
                final KeyTimePair<PartialOrCompleteTime> retval = new KeyTimePair<>();
                if (period.getStartTime().isPresent()) {
                    final KeyTimePair<PartialOrCompleteTime> startTimePair = completeSingularTime(period.getStartTime().get(), key);

                    final PartialOrCompleteTimeInstant startTime = (PartialOrCompleteTimeInstant) startTimePair.time;
                    ZonedDateTime ref = startTimePair.key;

                    if (!startTime.getCompleteTime().isPresent()) {
                        throw new RuntimeException(
                                "Could not complete start time " + period.getStartTime().get() + " with " + key + ", this should not happen");
                    }

                    if (period.getEndTime().isPresent()) {
                        ref = ZonedDateTime.of(LocalDateTime.from(startTime.getCompleteTime().get()), ref.getZone());
                        PartialOrCompleteTimeInstant endTimeToSet = period.getEndTime().get();
                        final int endHour = endTimeToSet.getHour();
                        final int endDay = endTimeToSet.getDay();
                        int endMinute = endTimeToSet.getMinute();
                        if (endMinute == -1) {
                            endMinute = 0;
                        }

                        if (endDay == -1) {
                            if (endHour <= ref.getHour()) {
                                ref = ref.plusDays(1);
                            }
                        } else {
                            //We know the day
                            if (endDay < ref.getDayOfMonth()) {
                                //Roll over to the next month
                                ref = ref.plusMonths(1);
                            }
                            ref = ref.withDayOfMonth(endDay);
                        }
                        if (endTimeToSet.isMidnight24h()) {
                            ref = ref.plusDays(1).withHour(0).withMinute(0);
                        } else {
                            ref = ref.withHour(endHour).withMinute(endMinute);
                        }
                        endTimeToSet = period.getEndTime().get().toBuilder().setCompleteTime(ref).build();
                        retval.key = ref;
                        retval.time = period.toBuilder().setStartTime(startTime).setEndTime(endTimeToSet).build();
                    } else {
                        retval.key = ref;
                        retval.time = period.toBuilder().setStartTime(startTime).build();
                    }
                } else if (period.getEndTime().isPresent()) {
                    //Only the end time is present, this can happen at least in Trends with only the "TL" time given.
                    //Handle as only the start time was given:
                    final KeyTimePair<PartialOrCompleteTime> endTimePair = completeSingularTime(period.getEndTime().get(), key);
                    retval.key = endTimePair.key;
                    retval.time = period.toBuilder().setEndTime((PartialOrCompleteTimeInstant) endTimePair.time).build();
                }
                return retval;
            } else if (input instanceof PartialOrCompleteTimeInstant) {
                final PartialOrCompleteTimeInstant instant = (PartialOrCompleteTimeInstant) input;
                return completeSingularTime(instant, key);
            }
        }
        return null;
    }

    private static KeyTimePair<PartialOrCompleteTime> completePartialTimeReferenceInternalBackwards(final PartialOrCompleteTime input,
            final ZonedDateTime key) {
        if (input != null) {
            if (input instanceof PartialOrCompleteTimePeriod) {
                final PartialOrCompleteTimePeriod period = (PartialOrCompleteTimePeriod) input;
                final KeyTimePair<PartialOrCompleteTime> retval = new KeyTimePair<>();
                if (period.getEndTime().isPresent()) {
                    final KeyTimePair<PartialOrCompleteTime> endTimePair = completeSingularTimeBackwards(period.getEndTime().get(), key);

                    final PartialOrCompleteTimeInstant endTime = (PartialOrCompleteTimeInstant) endTimePair.time;
                    ZonedDateTime ref = endTimePair.key;

                    if (!endTime.getCompleteTime().isPresent()) {
                        throw new RuntimeException("Could not complete end time " + period.getEndTime().get() + " with " + key + ", this should not happen");
                    }

                    if (period.getStartTime().isPresent()) {
                        ref = ZonedDateTime.of(LocalDateTime.from(endTime.getCompleteTime().get()), ref.getZone());
                        PartialOrCompleteTimeInstant startTimeToSet = period.getStartTime().get();
                        final int startHour = startTimeToSet.getHour();
                        final int startDay = startTimeToSet.getDay();
                        int startMinute = startTimeToSet.getMinute();
                        if (startMinute == -1) {
                            startMinute = 0;
                        }

                        if (startDay == -1) {
                            if (startHour >= ref.getHour()) {
                                ref = ref.minusDays(1);
                            }
                        } else {
                            //We know the day
                            if (startDay > ref.getDayOfMonth()) {
                                //Roll back to the prev month
                                ref = ref.minusMonths(1);
                            }
                            ref = ref.withDayOfMonth(startDay);
                        }
                        ref = ref.withHour(startHour).withMinute(startMinute);
                        startTimeToSet = period.getStartTime().get().toBuilder().setCompleteTime(ref).build();
                        retval.key = ref;
                        retval.time = period.toBuilder().setStartTime(startTimeToSet).setEndTime(endTime).build();
                    } else {
                        retval.key = ref;
                        retval.time = period.toBuilder().setEndTime(endTime).build();
                    }
                } else if (period.getStartTime().isPresent()) {
                    //Only the start time is present.
                    final KeyTimePair<PartialOrCompleteTime> startTimePair = completeSingularTime(period.getStartTime().get(), key);
                    retval.key = startTimePair.key;
                    retval.time = period.toBuilder().setStartTime((PartialOrCompleteTimeInstant) startTimePair.time).build();
                }
                return retval;
            } else if (input instanceof PartialOrCompleteTimeInstant) {
                final PartialOrCompleteTimeInstant instant = (PartialOrCompleteTimeInstant) input;
                return completeSingularTimeBackwards(instant, key);
            }
        }
        return null;
    }

    private static KeyTimePair<PartialOrCompleteTime> completeSingularTime(final PartialOrCompleteTimeInstant input, final ZonedDateTime reference) {
        final KeyTimePair<PartialOrCompleteTime> retval = new KeyTimePair<>();
        retval.key = ZonedDateTime.from(reference);
        final int hour = input.getHour();
        final int day = input.getDay();
        int minute = input.getMinute();
        if (minute == -1) {
            minute = 0;
        }
        if (day == -1) {
            if (hour < reference.getHour()) {
                //Roll over to the next day
                retval.key = retval.key.plusDays(1);
            }
        } else {
            if (day < retval.key.getDayOfMonth()) {
                //Roll over to the next month
                retval.key = retval.key.plusMonths(1);
            }
            retval.key = retval.key.withDayOfMonth(day);
        }
        retval.key = retval.key.withHour(hour).withMinute(minute);
        retval.time = input.toBuilder().setCompleteTime(retval.key).build();
        return retval;
    }

    private static KeyTimePair<PartialOrCompleteTime> completeSingularTimeBackwards(final PartialOrCompleteTimeInstant input, final ZonedDateTime reference) {
        final KeyTimePair<PartialOrCompleteTime> retval = new KeyTimePair<>();
        retval.key = ZonedDateTime.from(reference);
        int hour = input.getHour();
        int day = input.getDay();
        int minute = input.getMinute();
        if (minute == -1) {
            minute = 0;
        }
        if (hour == 24 && minute == 0) {
            day = day + 1;
            hour = 0;
            minute = 0;
        }
        if (day == -1) {
            if (hour > reference.getHour()) {
                //Roll back to the prev day
                retval.key = retval.key.minusDays(1);
            }
        } else {
            if (day > retval.key.getDayOfMonth()) {
                //Roll bck to the prev month
                retval.key = retval.key.minusMonths(1);
            }
            retval.key = retval.key.withDayOfMonth(day);
        }
        retval.key = retval.key.withHour(hour).withMinute(minute);
        retval.time = input.toBuilder().setCompleteTime(retval.key).build();
        return retval;
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

    public boolean equals(final Object o) {
        if (o instanceof PartialOrCompleteTimePeriod) {
            final PartialOrCompleteTimePeriod toMatch = (PartialOrCompleteTimePeriod) o;
            return this.getStartTime().equals(toMatch.getStartTime()) && this.getEndTime().equals(toMatch.getEndTime());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(this.getStartTime(), this.getEndTime());
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
            if (time.getCompleteTime().isPresent() && time.getPartialTime().isPresent()) {
                final ZonedDateTime completeTime = time.getCompleteTime().get();
                final PartialDateTime partialDateTime = time.getPartialTime().get();
                if (completeTime.toLocalTime().equals(LocalTime.MIDNIGHT) && !partialDateTime.isMidnight24h()) {
                    return super.setEndTime(time.toBuilder().mapPartialTime(partial -> partial.withMidnight24h(YearMonth.from(completeTime))).build());
                }
            }
            return super.setEndTime(time);
        }
    }

    private static class KeyTimePair<T extends PartialOrCompleteTime> {
        public ZonedDateTime key;
        public T time;
    }
}
