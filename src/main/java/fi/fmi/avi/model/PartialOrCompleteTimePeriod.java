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
        ZonedDateTime ref = ZonedDateTime.from(referenceTime);
        for (final PartialOrCompleteTimePeriod period : input) {
            revisedList.add(completePartialTimeReference(period, ref));
        }
        return revisedList;
    }

    public static PartialOrCompleteTimePeriod completePartialTimeReference(final PartialOrCompleteTimePeriod input, ZonedDateTime ref) {
        PartialOrCompleteTimePeriod retval = null;
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
                ZonedDateTime completeEndTime = ZonedDateTime.of(LocalDateTime.from(startTime.getCompleteTime().get()), ref.getZone());
                PartialOrCompleteTimeInstance partialEndTime = input.getEndTime().get();
                int endHour = partialEndTime.partialTimeHour();
                int endDay = partialEndTime.partialTimeDay();
                int endMinute = partialEndTime.partialTimeMinute();
                if (endMinute == -1) {
                    endMinute = 0;
                }

                if (endDay == -1) {
                    if (endHour <= completeEndTime.getHour()) {
                        completeEndTime = completeEndTime.plusDays(1);
                    }
                } else {
                    //We know the day
                    if (endDay < startDay) {
                        //Roll over to the next month
                        completeEndTime = completeEndTime.plusMonths(1);
                    }
                    completeEndTime = completeEndTime.withDayOfMonth(endDay);
                }
                if (partialEndTime.isMidnight24h()) {
                    completeEndTime = completeEndTime.plusDays(1).withHour(0).withMinute(0);
                } else {
                    completeEndTime = completeEndTime.withHour(endHour).withMinute(endMinute);
                }
                partialEndTime = input.getEndTime().get().toBuilder().setCompleteTime(completeEndTime).build();
                retval = input.toBuilder().setStartTime(startTime).setEndTime(partialEndTime).build();
            } else {
                retval = input.toBuilder().setStartTime(startTime).build();
            }
        }
        return retval;
    }

    public abstract PartialOrCompleteTimeInstance getStartTime();

    public abstract Optional<PartialOrCompleteTimeInstance> getEndTime();

    public abstract Builder toBuilder();

    public static class Builder extends PartialOrCompleteTimePeriod_Builder {
    }
}
