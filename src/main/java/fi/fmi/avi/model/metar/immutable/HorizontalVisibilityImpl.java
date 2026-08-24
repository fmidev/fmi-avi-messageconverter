package fi.fmi.avi.model.metar.immutable;


import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.metar.HorizontalVisibility;

/**
 * Created by rinne on 13/04/2018.
 */
@Value.Immutable
@JsonDeserialize(builder = HorizontalVisibilityImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"prevailingVisibility", "prevailingVisibilityOperator", "minimumVisibility", "minimumVisibilityDirection"})
public abstract class HorizontalVisibilityImpl implements HorizontalVisibility, Serializable {

    private static final long serialVersionUID = 5189785512501203996L;

    public static Builder builder() {
        return new Builder();
    }

    public static HorizontalVisibilityImpl immutableCopyOf(final HorizontalVisibility horizontalVisibility) {
        Objects.requireNonNull(horizontalVisibility);
        if (horizontalVisibility instanceof HorizontalVisibilityImpl) {
            return (HorizontalVisibilityImpl) horizontalVisibility;
        } else {
            return Builder.copyOf(horizontalVisibility).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<HorizontalVisibilityImpl> immutableCopyOf(final Optional<HorizontalVisibility> horizontalVisibility) {
        Objects.requireNonNull(horizontalVisibility);
        return horizontalVisibility.map(HorizontalVisibilityImpl::immutableCopyOf);
    }

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public abstract NumericMeasure getPrevailingVisibility();

    @Override
    @JsonDeserialize(contentAs = NumericMeasureImpl.class)
    public abstract Optional<NumericMeasure> getMinimumVisibility();

    @Override
    @JsonDeserialize(contentAs = NumericMeasureImpl.class)
    public abstract Optional<NumericMeasure> getMinimumVisibilityDirection();

    public static class Builder extends ImmutableHorizontalVisibilityImpl.Builder {

        Builder() {
        }

        public static Builder copyOf(final HorizontalVisibility value) {
            if (value instanceof HorizontalVisibilityImpl) {
                return ((HorizontalVisibilityImpl) value).toBuilder();
            } else {
                return HorizontalVisibilityImpl.builder().setPrevailingVisibility(NumericMeasureImpl.immutableCopyOf(value.getPrevailingVisibility()))
                        .setPrevailingVisibilityOperator(value.getPrevailingVisibilityOperator())
                        .setMinimumVisibility(NumericMeasureImpl.immutableCopyOf(value.getMinimumVisibility()))
                        .setMinimumVisibilityDirection(NumericMeasureImpl.immutableCopyOf(value.getMinimumVisibilityDirection()));
            }
        }



    }
}
