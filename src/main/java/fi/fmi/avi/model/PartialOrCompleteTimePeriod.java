package fi.fmi.avi.model;


import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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

/**
 * Created by rinne on 04/04/2018.
 */
@FreeBuilder
@JsonDeserialize(builder = PartialOrCompleteTimePeriod.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"startTime", "endTime"})
public abstract class PartialOrCompleteTimePeriod extends PartialOrCompleteTime {

    private static Pattern DAY_HOUR_HOUR_PATTERN = Pattern.compile("^(?<day>[0-9]{2})(?<startHour>[0-9]{2})(?<endHour>[0-9]{2})$");
    private static Pattern DAY_HOUR_DAY_HOUR_PATTERN = Pattern.compile("^(?<startDay>[0-9]{2})(?<startHour>[0-9]{2})/(?<endDay>[0-9]{2})(?<endHour>[0-9]{2})$");

    public static List<PartialOrCompleteTime> completePartialTimeReferenceList(final List<? extends PartialOrCompleteTime> input,
            final ZonedDateTime referenceTime) {
        Objects.requireNonNull(input, "Input list cannot be null");

        //Assumption: the start times come in chronological order, but the periods may be (partly) overlapping
        List<PartialOrCompleteTime> revisedList = new ArrayList<>(input.size());
        KeyTimePair<?> kpp = new KeyTimePair<>();
        kpp.key = referenceTime;
        for (final PartialOrCompleteTime periodOrInstant : input) {
            kpp = completePartialTimeReferenceInternal(periodOrInstant, kpp.key);
            revisedList.add(kpp.time);
        }
        return revisedList;
    }

    public static PartialOrCompleteTimePeriod completePartialTimeReference(final PartialOrCompleteTimePeriod input, final ZonedDateTime key) {
        return (PartialOrCompleteTimePeriod)completePartialTimeReferenceInternal(input, key).time;
    }

    public static PartialOrCompleteTimePeriod completePartialTimeReferenceBackwards(final PartialOrCompleteTimePeriod input, final ZonedDateTime key) {
        return (PartialOrCompleteTimePeriod)completePartialTimeReferenceInternalBackwards(input, key).time;
    }

    public static PartialOrCompleteTimeInstant completePartialTimeReference(final PartialOrCompleteTimeInstant input, final ZonedDateTime key) {
        return (PartialOrCompleteTimeInstant)completePartialTimeReferenceInternal(input, key).time;
    }

    public static PartialOrCompleteTimePeriod createValidityTimeDHDH(final String partialTimePeriod) throws IllegalArgumentException {
        Matcher m = DAY_HOUR_DAY_HOUR_PATTERN.matcher(partialTimePeriod);
        if (m.matches()) {
            return new PartialOrCompleteTimePeriod.Builder().setStartTime(
                    new PartialOrCompleteTimeInstant.Builder().setPartialTime(m.group("startDay") + m.group("startHour"))
                            .setPartialTimePattern(PartialOrCompleteTimeInstant.TimePattern.DayHour)
                            .build())
                    .setEndTime(new PartialOrCompleteTimeInstant.Builder().setPartialTime(m.group("endDay") + m.group("endHour"))
                            .setPartialTimePattern(PartialOrCompleteTimeInstant.TimePattern.DayHour)
                            .build())
                    .build();
        } else {
            throw new IllegalArgumentException("time period does not match pattern " + DAY_HOUR_DAY_HOUR_PATTERN);
        }
    }

    public static PartialOrCompleteTimePeriod createValidityTimeDHH(final String partialTimePeriod) throws IllegalArgumentException {
        Matcher m = DAY_HOUR_HOUR_PATTERN.matcher(partialTimePeriod);
        if (m.matches()) {
            return new PartialOrCompleteTimePeriod.Builder().setStartTime(
                    new PartialOrCompleteTimeInstant.Builder().setPartialTime(m.group("day") + m.group("startHour"))
                            .setPartialTimePattern(PartialOrCompleteTimeInstant.TimePattern.DayHour)
                            .build())
                    .setEndTime(new PartialOrCompleteTimeInstant.Builder().setPartialTime(m.group("day") + m.group("endHour"))
                            .setPartialTimePattern(PartialOrCompleteTimeInstant.TimePattern.DayHour)
                            .build())
                    .build();
        } else {
            throw new IllegalArgumentException("time period does not match pattern " + DAY_HOUR_HOUR_PATTERN);
        }
    }

    public static PartialOrCompleteTimePeriod createValidityTime(final String partialTimePeriod) throws IllegalArgumentException {
        try {
            return createValidityTimeDHDH(partialTimePeriod);
        } catch (IllegalArgumentException iae) {
            return createValidityTimeDHH(partialTimePeriod);
        }
    }

    private static KeyTimePair<PartialOrCompleteTime> completePartialTimeReferenceInternal(final PartialOrCompleteTime input, final ZonedDateTime key) {
        if (input != null) {
            if (input instanceof PartialOrCompleteTimePeriod) {
                PartialOrCompleteTimePeriod period = (PartialOrCompleteTimePeriod) input;
                KeyTimePair<PartialOrCompleteTime> retval = new KeyTimePair<>();
                if (period.getStartTime().isPresent()) {
                    KeyTimePair<PartialOrCompleteTime> startTimePair = completeSingularTime(period.getStartTime().get(), key);

                    PartialOrCompleteTimeInstant startTime = (PartialOrCompleteTimeInstant) startTimePair.time;
                    ZonedDateTime ref = startTimePair.key;

                    if (!startTime.getCompleteTime().isPresent()) {
                        throw new RuntimeException("Could not complete start time " + period.getStartTime().get() + " with " + key + ", this should not happen");
                    }

                    if (period.getEndTime().isPresent()) {
                        ref = ZonedDateTime.of(LocalDateTime.from(startTime.getCompleteTime().get()), ref.getZone());
                        PartialOrCompleteTimeInstant endTimeToSet = period.getEndTime().get();
                        int endHour = endTimeToSet.getHour();
                        int endDay = endTimeToSet.getDay();
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
                    KeyTimePair<PartialOrCompleteTime> endTimePair = completeSingularTime(period.getEndTime().get(), key);
                    retval.key = endTimePair.key;
                    retval.time = period.toBuilder().setEndTime((PartialOrCompleteTimeInstant)endTimePair.time).build();
                }
                return retval;
            } else if (input instanceof PartialOrCompleteTimeInstant) {
                PartialOrCompleteTimeInstant instant = (PartialOrCompleteTimeInstant) input;
                return completeSingularTime(instant, key);
            }
        }
        return null;
    }

    private static KeyTimePair<PartialOrCompleteTime> completePartialTimeReferenceInternalBackwards(final PartialOrCompleteTime input, final ZonedDateTime key) {
        if (input != null) {
            if (input instanceof PartialOrCompleteTimePeriod) {
                PartialOrCompleteTimePeriod period = (PartialOrCompleteTimePeriod) input;
                KeyTimePair<PartialOrCompleteTime> retval = new KeyTimePair<>();
                if (period.getEndTime().isPresent()) {
                    KeyTimePair<PartialOrCompleteTime> endTimePair = completeSingularTimeBackwards(period.getEndTime().get(), key);

                    PartialOrCompleteTimeInstant endTime = (PartialOrCompleteTimeInstant) endTimePair.time;
                    ZonedDateTime ref = endTimePair.key;

                    if (!endTime.getCompleteTime().isPresent()) {
                        throw new RuntimeException("Could not complete end time " + period.getEndTime().get() + " with " + key + ", this should not happen");
                    }

                    if (period.getStartTime().isPresent()) {
                        ref = ZonedDateTime.of(LocalDateTime.from(endTime.getCompleteTime().get()), ref.getZone());
                        PartialOrCompleteTimeInstant startTimeToSet = period.getStartTime().get();
                        int startHour = startTimeToSet.getHour();
                        int startDay = startTimeToSet.getDay();
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
                    KeyTimePair<PartialOrCompleteTime> startTimePair = completeSingularTime(period.getStartTime().get(), key);
                    retval.key = startTimePair.key;
                    retval.time = period.toBuilder().setStartTime((PartialOrCompleteTimeInstant)startTimePair.time).build();
                }
                return retval;
            } else if (input instanceof PartialOrCompleteTimeInstant) {
                PartialOrCompleteTimeInstant instant = (PartialOrCompleteTimeInstant) input;
                return completeSingularTimeBackwards(instant, key);
            }
        }
        return null;
    }

    private static KeyTimePair<PartialOrCompleteTime> completeSingularTime(final PartialOrCompleteTimeInstant input, final ZonedDateTime reference) {
        KeyTimePair<PartialOrCompleteTime> retval = new KeyTimePair<>();
        retval.key = ZonedDateTime.from(reference);
        int hour = input.getHour();
        int day = input.getDay();
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
        KeyTimePair<PartialOrCompleteTime> retval = new KeyTimePair<>();
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
        Optional<PartialOrCompleteTimeInstant> start = getStartTime();
        Optional<PartialOrCompleteTimeInstant> end = getEndTime();
        if (start.isPresent()) {
            if (!start.get().getCompleteTime().isPresent()) {
                return false;
            }
        }
        if (end.isPresent()) {
            if (!end.get().getCompleteTime().isPresent()) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(Object o) {
        if (o instanceof PartialOrCompleteTimePeriod) {
            PartialOrCompleteTimePeriod toMatch = (PartialOrCompleteTimePeriod) o;
            return this.getStartTime().equals(toMatch.getStartTime()) && this.getEndTime().equals(toMatch.getEndTime());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(this.getStartTime(), this.getEndTime());
    }

    public static class Builder extends PartialOrCompleteTimePeriod_Builder {
        public static Pattern TREND_TIME_GROUP = Pattern.compile("^(?<kind>FM|TL)(?<hour>[0-9]{2})(?<minute>[0-9]{2})$");

        public Builder withTrendTimeGroupToken(final String token) {
            Matcher m = TREND_TIME_GROUP.matcher(token);
            if (m.matches()) {
                switch (m.group("kind")) {
                    case "FM":
                        return setStartTime(PartialOrCompleteTimeInstant.createHourMinuteInstant(m.group("hour") + m.group("minute")));
                    case "TL":
                        return setEndTime(PartialOrCompleteTimeInstant.createHourMinuteInstant(m.group("hour") + m.group("minute")));
                }
            } else {
                throw new IllegalArgumentException("token does not match pattern " + TREND_TIME_GROUP);
            }
            throw new RuntimeException("Unexpected error, check the code");
        }
    }

    private static class KeyTimePair<T extends PartialOrCompleteTime> {
        public ZonedDateTime key;
        public T time;
    }

}
