package fi.fmi.avi.model.metar.immutable;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.RunwayDirectionImpl;
import fi.fmi.avi.model.metar.RunwayVisualRange;

/**
 * Created by rinne on 13/04/2018.
 */
@FreeBuilder
@JsonDeserialize(builder = RunwayVisualRangeImpl.Builder.class)
public abstract class RunwayVisualRangeImpl implements RunwayVisualRange, Serializable {

    public static List<RunwayVisualRange> copyOfList(final List<RunwayVisualRange> runwayVisualRanges) {
        return null;
    }

    public static Optional<List<RunwayVisualRange>> copyOfList(final Optional<List<RunwayVisualRange>> runwayVisualRanges) {
        return null;
    }

    public static RunwayVisualRangeImpl immutableCopyOf(final RunwayVisualRange runwayVisualRange) {
        checkNotNull(runwayVisualRange);
        if (runwayVisualRange instanceof RunwayVisualRangeImpl) {
            return (RunwayVisualRangeImpl) runwayVisualRange;
        } else {
            return Builder.from(runwayVisualRange).build();
        }
    }

    public static Optional<RunwayVisualRangeImpl> immutableCopyOf(final Optional<RunwayVisualRange> runwayVisualRange) {
        checkNotNull(runwayVisualRange);
        return runwayVisualRange.map(RunwayVisualRangeImpl::immutableCopyOf);
    }

    abstract Builder toBuilder();

    public static class Builder extends RunwayVisualRangeImpl_Builder {

        public static Builder from(final RunwayVisualRange value) {
            return new RunwayVisualRangeImpl.Builder().setMeanRVR(NumericMeasureImpl.immutableCopyOf(value.getMeanRVR()))
                    .setMeanRVROperator(value.getMeanRVROperator())
                    .setPastTendency(value.getPastTendency())
                    .setRunwayDirection(RunwayDirectionImpl.immutableCopyOf(value.getRunwayDirection()))
                    .setVaryingRVRMaximum(NumericMeasureImpl.immutableCopyOf(value.getVaryingRVRMaximum()))
                    .setVaryingRVRMinimum(NumericMeasureImpl.immutableCopyOf(value.getVaryingRVRMinimum()))
                    .setVaryingRVRMaximumOperator(value.getVaryingRVRMaximumOperator())
                    .setVaryingRVRMinimumOperator(value.getVaryingRVRMinimumOperator());

        }
    }
}
