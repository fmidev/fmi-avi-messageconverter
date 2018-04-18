package fi.fmi.avi.model.immutable;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.NumericMeasure;

/**
 * Created by rinne on 17/04/2018.
 */

@FreeBuilder
@JsonDeserialize(builder = NumericMeasureImpl.Builder.class)
public abstract class NumericMeasureImpl implements NumericMeasure, Serializable {

    public static NumericMeasureImpl immutableCopyOf(final NumericMeasure numericMeasure) {
        checkNotNull(numericMeasure);
        if (numericMeasure instanceof NumericMeasureImpl) {
            return (NumericMeasureImpl) numericMeasure;
        } else {
            return Builder.from(numericMeasure).build();
        }
    }

    public static Optional<NumericMeasureImpl> immutableCopyOf(final Optional<NumericMeasure> numericMeasure) {
        return numericMeasure.map(NumericMeasureImpl::immutableCopyOf);
    }

    abstract Builder toBuilder();

    public static class Builder extends NumericMeasureImpl_Builder {

        public static Builder from(final NumericMeasure value) {
            return new NumericMeasureImpl.Builder().setUom(value.getUom()).setValue(value.getValue());
        }
    }
}
