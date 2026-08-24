package fi.fmi.avi.model.metar.immutable;


import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.RunwayDirection;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.RunwayDirectionImpl;
import fi.fmi.avi.model.metar.RunwayVisualRange;

/**
 * Created by rinne on 13/04/2018.
 */
@Value.Immutable
@JsonDeserialize(builder = RunwayVisualRangeImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"runwayDirection", "meanRVR", "meanRVROperator", "varyingRVRMinimum", "varyingRVRMinimumOperator",
        "varyingRVRMaximum", "varyingRVRMaximumOperator", "pastTendency"})
public abstract class RunwayVisualRangeImpl implements RunwayVisualRange, Serializable {

    private static final long serialVersionUID = 6512555668623334989L;

    public static Builder builder() {
        return new Builder();
    }

    public static List<RunwayVisualRange> copyOfList(final List<RunwayVisualRange> runwayVisualRanges) {
        return null;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<List<RunwayVisualRange>> copyOfList(final Optional<List<RunwayVisualRange>> runwayVisualRanges) {
        return Optional.empty();
    }

    public static RunwayVisualRangeImpl immutableCopyOf(final RunwayVisualRange runwayVisualRange) {
        Objects.requireNonNull(runwayVisualRange);
        if (runwayVisualRange instanceof RunwayVisualRangeImpl) {
            return (RunwayVisualRangeImpl) runwayVisualRange;
        } else {
            return Builder.copyOf(runwayVisualRange).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<RunwayVisualRangeImpl> immutableCopyOf(final Optional<RunwayVisualRange> runwayVisualRange) {
        Objects.requireNonNull(runwayVisualRange);
        return runwayVisualRange.map(RunwayVisualRangeImpl::immutableCopyOf);
    }

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    @Override
    @JsonDeserialize(as = RunwayDirectionImpl.class)
    public abstract RunwayDirection getRunwayDirection();

    @Override
    @JsonDeserialize(contentAs = NumericMeasureImpl.class)
    public abstract Optional<NumericMeasure> getMeanRVR();

    @Override
    @JsonDeserialize(contentAs = NumericMeasureImpl.class)
    public abstract Optional<NumericMeasure> getVaryingRVRMinimum();

    @Override
    @JsonDeserialize(contentAs = NumericMeasureImpl.class)
    public abstract Optional<NumericMeasure> getVaryingRVRMaximum();

    public static class Builder extends ImmutableRunwayVisualRangeImpl.Builder {

        Builder() {
        }

        public static Builder copyOf(final RunwayVisualRange value) {
            if (value instanceof RunwayVisualRangeImpl) {
                return ((RunwayVisualRangeImpl) value).toBuilder();
            } else {
                return RunwayVisualRangeImpl.builder()//
                        .setMeanRVR(NumericMeasureImpl.immutableCopyOf(value.getMeanRVR()))
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
}
