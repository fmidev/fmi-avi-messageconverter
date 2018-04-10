package fi.fmi.avi.model;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.google.common.base.Preconditions;

/**
 * Created by rinne on 04/04/2018.
 */
@FreeBuilder
public abstract class PartialOrCompleteTimePeriod {

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

    private static KeyPeriodPair completePartialTimeReferenceInternal(final PartialOrCompleteTimePeriod input, final ZonedDateTime key) {
        KeyPeriodPair retval = new KeyPeriodPair();
        ZonedDateTime ref = ZonedDateTime.from(key);
        if (input != null) {
            PartialOrCompleteTimeInstance startTime = input.getStartTime();
            int startHour = startTime.partialTimeHour();
            int startDay = startTime.partialTimeDay();
            int startMinute = startTime.partialTimeMinute();
            if (startMinute == -1) {
                startMinute = 0;
            }

            if (startDay == -1) {
                if (startHour < ref.getHour()) {
                    //Roll over to the next day
                    ref = ref.plusDays(1);
                }
            } else {
                if (startDay < ref.getDayOfMonth()) {
                    //Roll over to the next month
                    ref = ref.plusMonths(1);
                }
                ref = ref.withDayOfMonth(startDay);
            }
            ref = ref.withHour(startHour).withMinute(startMinute);
            startTime = input.getStartTime().toBuilder().setCompleteTime(ref).build();

            if (input.getEndTime().isPresent()) {
                ref = ZonedDateTime.of(LocalDateTime.from(startTime.getCompleteTime().get()), ref.getZone());
                PartialOrCompleteTimeInstance endTimeToSet = input.getEndTime().get();
                int endHour = endTimeToSet.partialTimeHour();
                int endDay = endTimeToSet.partialTimeDay();
                int endMinute = endTimeToSet.partialTimeMinute();
                if (endMinute == -1) {
                    endMinute = 0;
                }

                if (endDay == -1) {
                    if (endHour <= ref.getHour()) {
                        ref = ref.plusDays(1);
                    }
                } else {
                    //We know the day
                    if (endDay < startDay) {
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
        }
        return retval;
    }

    public abstract PartialOrCompleteTimeInstance getStartTime();

    public abstract Optional<PartialOrCompleteTimeInstance> getEndTime();

    public abstract Builder toBuilder();

    public static class Builder extends PartialOrCompleteTimePeriod_Builder {
    }

    private static class KeyPeriodPair {
        public ZonedDateTime key;
        public PartialOrCompleteTimePeriod period;
    }
}
