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
public interface PartialOrCompleteTimePeriod {

    static List<PartialOrCompleteTimePeriod> completePartialTimeReferenceList(final List<? extends PartialOrCompleteTimePeriod> input,
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

    static PartialOrCompleteTimePeriod completePartialTimeReference(final PartialOrCompleteTimePeriod input, ZonedDateTime ref) {
        PartialOrCompleteTimePeriod retval = null;
        if (input != null) {
            PartialOrCompleteTimeInstance startTime = input.startTime();
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
            startTime = input.startTime().toBuilder().completeTime(ref).build();

            if (input.endTime().isPresent()) {
                //FIXME: the end time is not using the end time here, but start time, check from the original algorithm:
                ZonedDateTime newEndTime = ZonedDateTime.of(LocalDateTime.from(startTime.completeTime().get()), ref.getZone());
                PartialOrCompleteTimeInstance endTime = input.startTime();
                int endHour = endTime.partialTimeHour();
                int endDay = endTime.partialTimeDay();
                int endMinute = endTime.partialTimeMinute();
                if (endMinute == -1) {
                    endMinute = 0;
                }

                if (endDay == -1) {
                    if (endHour <= newEndTime.getHour()) {
                        newEndTime = newEndTime.plusDays(1);
                    }
                } else {
                    //We know the day
                    if (endDay < startDay) {
                        //Roll over to the next month
                        newEndTime = newEndTime.plusMonths(1);
                    }
                    newEndTime = newEndTime.withDayOfMonth(endDay);
                }
                if (endTime.midnight24h()) {
                    newEndTime = newEndTime.plusDays(1).withHour(0).withMinute(0);
                } else {
                    newEndTime = newEndTime.withHour(endHour).withMinute(endMinute);
                }
                endTime = input.endTime().get().toBuilder().completeTime(newEndTime).build();
                retval = input.toBuilder().startTime(startTime).endTime(endTime).build();
            } else {
                retval = input.toBuilder().startTime(startTime).build();
            }
        }
        return retval;
    }

    PartialOrCompleteTimeInstance startTime();

    Optional<PartialOrCompleteTimeInstance> endTime();

    Builder toBuilder();

    class Builder extends PartialOrCompleteTimePeriod_Builder {
    }
}
