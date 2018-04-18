package fi.fmi.avi.model.metar.immutable;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.metar.HorizontalVisibility;

/**
 * Created by rinne on 13/04/2018.
 */
@FreeBuilder
@JsonDeserialize(builder = HorizontalVisibilityImpl.Builder.class)
public abstract class HorizontalVisibilityImpl implements HorizontalVisibility, Serializable {

    public static HorizontalVisibilityImpl immutableCopyOf(final HorizontalVisibility horizontalVisibility) {
        checkNotNull(horizontalVisibility);
        if (horizontalVisibility instanceof HorizontalVisibilityImpl) {
            return (HorizontalVisibilityImpl) horizontalVisibility;
        } else {
            return Builder.from(horizontalVisibility).build();
        }
    }

    public static Optional<HorizontalVisibilityImpl> immutableCopyOf(final Optional<HorizontalVisibility> horizontalVisibility) {
        checkNotNull(horizontalVisibility);
        return horizontalVisibility.map(HorizontalVisibilityImpl::immutableCopyOf);
    }

    abstract Builder toBuilder();

    public static class Builder extends HorizontalVisibilityImpl_Builder {

        public static Builder from(final HorizontalVisibility value) {
            return new HorizontalVisibilityImpl.Builder().setPrevailingVisibility(NumericMeasureImpl.immutableCopyOf(value.getPrevailingVisibility()))
                    .setPrevailingVisibilityOperator(value.getPrevailingVisibilityOperator())
                    .setMinimumVisibility(NumericMeasureImpl.immutableCopyOf(value.getMinimumVisibility()))
                    .setMinimumVisibilityDirection(NumericMeasureImpl.immutableCopyOf(value.getMinimumVisibilityDirection()));
        }
    }
}
