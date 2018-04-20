package fi.fmi.avi.model;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;

/**
 * Created by rinne on 04/04/2018.
 */
@FreeBuilder
@JsonDeserialize(builder = PartialOrCompleteTimePeriod.Builder.class)
public abstract class PartialOrCompleteTimePeriod {

    public static Pattern DAY_HOUR_HOUR_PATTERN = Pattern.compile("^(?<day>[0-9]{2})(?<startHour>[0-9]{2})(?<endHour>[0-9]{2})$");
    public static Pattern DAY_HOUR_DAY_HOUR_PATTERN = Pattern.compile("^(?<startDay>[0-9]{2})(?<startHour>[0-9]{2})/(?<endDay>[0-9]{2})(?<endHour>[0-9]{2})$");

    public static List<PartialOrCompleteTimePeriod> completePartialTimeReferenceList(final List<? extends PartialOrCompleteTimePeriod> input,
            final ZonedDateTime referenceTime) {
        Preconditions.checkNotNull(input, "Input list cannot be null");

        //Assumption: the start times come in chronological order, but the periods may be (partly) overlapping
        List<PartialOrCompleteTimePeriod> revisedList = new ArrayList<>(input.size());
        KeyPeriodPair kpp = new KeyPeriodPair();
        kpp.key = referenceTime;
        for (final PartialOrCompleteTimePeriod period : input) {
            kpp = completePartialTimeReferenceInternal(period, kpp.key);
            revisedList.add(kpp.period);
        }
        return revisedList;
    }

    public static PartialOrCompleteTimePeriod completePartialTimeReference(final PartialOrCompleteTimePeriod input, final ZonedDateTime key) {
        return completePartialTimeReferenceInternal(input, key).period;
    }

    public static PartialOrCompleteTimePeriod createValidityTimeDHDH(final String partialTimePeriod) throws IllegalArgumentException {
        Matcher m = DAY_HOUR_DAY_HOUR_PATTERN.matcher(partialTimePeriod);
        if (m.matches()) {
            return new PartialOrCompleteTimePeriod.Builder().setStartTime(
                    new PartialOrCompleteTimeInstant.Builder().setPartialTime(m.group("startDay") + m.group("startHour"))
                            .setPartialTimePattern(PartialOrCompleteTimeInstant.DAY_HOUR_PATTERN)
                            .build())
                    .setEndTime(new PartialOrCompleteTimeInstant.Builder().setPartialTime(m.group("endDay") + m.group("endHour"))
                            .setPartialTimePattern(PartialOrCompleteTimeInstant.DAY_HOUR_PATTERN)
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
                            .setPartialTimePattern(PartialOrCompleteTimeInstant.DAY_HOUR_PATTERN)
                            .build())
                    .setEndTime(new PartialOrCompleteTimeInstant.Builder().setPartialTime(m.group("day") + m.group("endHour"))
                            .setPartialTimePattern(PartialOrCompleteTimeInstant.DAY_HOUR_PATTERN)
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

    private static KeyPeriodPair completePartialTimeReferenceInternal(final PartialOrCompleteTimePeriod input, final ZonedDateTime key) {
        KeyPeriodPair retval = new KeyPeriodPair();
        if (input != null) {
            if (input.getStartTime().isPresent()) {
                KeyInstantPair startTimePair = completeSingularTime(input.getStartTime().get(), key);
                PartialOrCompleteTimeInstant startTime = startTimePair.instant;
                ZonedDateTime ref = startTimePair.key;

                if (input.getEndTime().isPresent()) {
                    ref = ZonedDateTime.of(LocalDateTime.from(startTime.getCompleteTime().get()), ref.getZone());
                    PartialOrCompleteTimeInstant endTimeToSet = input.getEndTime().get();
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
                    endTimeToSet = input.getEndTime().get().toBuilder().setCompleteTime(ref).build();
                    retval.key = ref;
                    retval.period = input.toBuilder().setStartTime(startTime).setEndTime(endTimeToSet).build();
                } else {
                    retval.key = ref;
                    retval.period = input.toBuilder().setStartTime(startTime).build();
                }
            } else if (input.getEndTime().isPresent()) {
                //Only the end time is present, this can happen at least in Trends with only the "TL" time given.
                //Handle as only the start time was given:
                KeyInstantPair endTimePair = completeSingularTime(input.getEndTime().get(), key);
                retval.key = endTimePair.key;
                retval.period = input.toBuilder().setEndTime(endTimePair.instant).build();
            }
        }
        return retval;
    }

    private static KeyInstantPair completeSingularTime(final PartialOrCompleteTimeInstant input, final ZonedDateTime reference) {
        KeyInstantPair retval = new KeyInstantPair();
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
        retval.instant = input.toBuilder().setCompleteTime(retval.key).build();
        return retval;
    }

    public abstract Optional<PartialOrCompleteTimeInstant> getStartTime();

    public abstract Optional<PartialOrCompleteTimeInstant> getEndTime();

    public abstract Builder toBuilder();

    public static class Builder extends PartialOrCompleteTimePeriod_Builder {
        public static Pattern TREND_TIME_GROUP = Pattern.compile("^(?<kind>FM|TL|AT)(?<hour>[0-9]{2})(?<minute>[0-9]{2})$");

        public Builder withTrendTimeGroupToken(final String token) {
            Matcher m = TREND_TIME_GROUP.matcher(token);
            if (m.matches()) {
                switch (m.group("kind")) {
                    case "FM":
                        return setStartTime(PartialOrCompleteTimeInstant.createHourMinuteInstant(m.group("hour") + m.group("minute")));
                    case "TL":
                        return setEndTime(PartialOrCompleteTimeInstant.createHourMinuteInstant(m.group("hour") + m.group("minute")));
                    case "AT":
                        return setStartTime(PartialOrCompleteTimeInstant.createHourMinuteInstant(m.group("hour") + m.group("minute")));
                }
            } else {
                throw new IllegalArgumentException("token does not match pattern " + TREND_TIME_GROUP);
            }
            throw new RuntimeException("Unexpected error, check the code");
        }
    }

    private static class KeyPeriodPair {
        public ZonedDateTime key;
        public PartialOrCompleteTimePeriod period;
    }

    private static class KeyInstantPair {
        public ZonedDateTime key;
        public PartialOrCompleteTimeInstant instant;
    }
}
