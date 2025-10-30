package fi.fmi.avi.model.swx;

import org.inferred.freebuilder.FreeBuilder;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

@FreeBuilder
public abstract class VerticalLimitsImpl implements VerticalLimits, Serializable {

    private static final long serialVersionUID = 456150576462009061L;

    public static Builder builder() {
        return new Builder();
    }

    public static VerticalLimits none() {
        return builder().build();
    }

    public static VerticalLimitsImpl immutableCopyOf(final VerticalLimits verticalLimits) {
        Objects.requireNonNull(verticalLimits);
        if (verticalLimits instanceof VerticalLimitsImpl) {
            return (VerticalLimitsImpl) verticalLimits;
        } else {
            return Builder.from(verticalLimits).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<VerticalLimitsImpl> immutableCopyOf(final Optional<VerticalLimits> verticalLimits) {
        Objects.requireNonNull(verticalLimits);
        return verticalLimits.map(VerticalLimitsImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends VerticalLimitsImpl_Builder {

        public Builder() {
            setVerticalReference(STANDARD_ATMOSPHERE);
        }

        public static Builder from(final VerticalLimits value) {
            if (value instanceof VerticalLimitsImpl) {
                return ((VerticalLimitsImpl) value).toBuilder();
            } else {
                return builder()
                        .setLowerLimit(value.getLowerLimit())
                        .setUpperLimit(value.getUpperLimit())
                        .setOperator(value.getOperator())
                        .setVerticalReference(value.getVerticalReference());
            }
        }

    }
}
