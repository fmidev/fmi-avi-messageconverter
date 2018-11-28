package fi.fmi.avi.model.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.NumericMeasure;

/**
 * Created by rinne on 17/04/2018.
 */

@FreeBuilder
@JsonDeserialize(builder = NumericMeasureImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"value", "uom"})
public abstract class NumericMeasureImpl implements NumericMeasure, Serializable {

    private static final long serialVersionUID = 8955711992731295488L;

    public static NumericMeasureImpl of(final Integer value, final String uom) {
        return new Builder().setValue(value.doubleValue()).setUom(uom).build();
    }

    public static NumericMeasureImpl of(final Double value, final String uom) {
        return new Builder().setValue(value).setUom(uom).build();
    }

    public static NumericMeasureImpl immutableCopyOf(final NumericMeasure numericMeasure) {
        Objects.requireNonNull(numericMeasure);
        if (numericMeasure instanceof NumericMeasureImpl) {
            return (NumericMeasureImpl) numericMeasure;
        } else {
            return Builder.from(numericMeasure).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<NumericMeasureImpl> immutableCopyOf(final Optional<NumericMeasure> numericMeasure) {
        return numericMeasure.map(NumericMeasureImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends NumericMeasureImpl_Builder {

        public static Builder from(final NumericMeasure value) {
            if (value instanceof NumericMeasureImpl) {
                return ((NumericMeasureImpl) value).toBuilder();
            } else {
                return new NumericMeasureImpl.Builder()//
                        .setUom(value.getUom())//
                        .setValue(value.getValue());
            }
        }
    }
}
