package fi.fmi.avi.model;

import static java.util.Objects.requireNonNull;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Utilities to work with {@code PartialOrCompleteTime} objects.
 */
public final class PartialOrCompleteTimes {
    private PartialOrCompleteTimes() {
        throw new AssertionError();
    }

    /**
     * Completes a sequence of {@code PartialOrCompleteTime}s enforcing completed times in ascending order, but allowing consecutive equal times.
     * The {@code input} sequence may contain instances of both {@link PartialOrCompleteTimeInstant} and {@link PartialOrCompleteTimePeriod}.
     *
     * <p>
     * Each time element in the {@code input} sequence is completed to a time being equal to or after provided {@code referenceTime}. Following times are
     * completed to time being equal to or after previous.
     * </p>
     *
     * <p>
     * Periods are completed by start time, unless start time is empty, in which case period is completed by end time. Therefore periods may overlap each other.
     * </p>
     *
     * <p>
     * This method is equivalent to {@code completeAscendingPartialTimes(input, referenceTime, PartialDateTime::toZonedDateTimeNotBefore)}.
     * </p>
     *
     * @param input
     *         sequence of times to be completed
     * @param referenceTime
     *         an instant near first time
     *
     * @return a list of completed times
     */
    public static List<PartialOrCompleteTime> completeAscendingPartialTimes(final Iterable<? extends PartialOrCompleteTime> input,
            final ZonedDateTime referenceTime) {
        return completeAscendingPartialTimes(input, referenceTime, PartialDateTime::toZonedDateTimeNotBefore);
    }

    /**
     * Completes a sequence of {@code PartialOrCompleteTime}s assuming provided times are in ascending order.
     * The {@code input} sequence may contain instances of both {@link PartialOrCompleteTimeInstant} and {@link PartialOrCompleteTimePeriod}.
     *
     * <p>
     * Each time element in the {@code input} sequence is completed applying provided {@code partialCompletion} function. The reference time for the first
     * invocation of {@code partialCompletion} function is the provided {@code referenceTime}. Consequent invocations of {@code partialCompletion} will get
     * the completed instant of previous invocation as reference time.
     * </p>
     *
     * <p>
     * Periods are completed by start time, unless start time is empty, in which case period is completed by end time. Therefore periods may overlap each other.
     * </p>
     *
     * @param input
     *         sequence of times to be completed
     * @param referenceTime
     *         an instant near first time
     * @param partialCompletion
     *         function to complete given {@code PartialDateTime} with given {@code ZonedDateTime} as reference
     *
     * @return a list of completed times
     */
    public static List<PartialOrCompleteTime> completeAscendingPartialTimes(final Iterable<? extends PartialOrCompleteTime> input,
            final ZonedDateTime referenceTime, final BiFunction<PartialDateTime, ZonedDateTime, ZonedDateTime> partialCompletion) {
        requireNonNull(input, "input");
        requireNonNull(referenceTime, "referenceTime");
        requireNonNull(partialCompletion, "partialCompletion");

        final List<PartialOrCompleteTime> result = input instanceof Collection ? new ArrayList<>(((Collection<?>) input).size()) : new ArrayList<>();
        final ZonedDateTime[] rollingReferenceTime = new ZonedDateTime[] { referenceTime }; // Used as mutable reference
        // Assumption: the start times come in (approximately) chronological order, but the periods may be (partly) overlapping
        int index = 0;
        for (final PartialOrCompleteTime partialOrCompleteTime : input) {
            if (partialOrCompleteTime == null) {
                throw new NullPointerException("null element at index " + index);
            } else if (partialOrCompleteTime instanceof PartialOrCompleteTimeInstant) {
                final PartialOrCompleteTimeInstant completed = ((PartialOrCompleteTimeInstant) partialOrCompleteTime).toBuilder()
                        .completePartial(partial -> partialCompletion.apply(partial, rollingReferenceTime[0]))
                        .build();
                result.add(completed);
                rollingReferenceTime[0] = completed.getCompleteTime().orElse(rollingReferenceTime[0]);
            } else if (partialOrCompleteTime instanceof PartialOrCompleteTimePeriod) {
                final PartialOrCompleteTimePeriod completed = ((PartialOrCompleteTimePeriod) partialOrCompleteTime).toBuilder()//
                        .completePartial(partial -> partialCompletion.apply(partial, rollingReferenceTime[0]))//
                        .build();
                result.add(completed);
                rollingReferenceTime[0] = completed.getStartTime()//
                        .map(PartialOrCompleteTimeInstant::getCompleteTime)//
                        .orElse(completed.getEndTime()//
                                .flatMap(PartialOrCompleteTimeInstant::getCompleteTime))//
                        .orElse(rollingReferenceTime[0]);
            } else {
                throw new IllegalArgumentException("Unknown PartialOrCompleteTime: " + partialOrCompleteTime.getClass() + " <" + partialOrCompleteTime + ">");
            }
            index += 1;
        }

        return result.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(result);
    }
}
