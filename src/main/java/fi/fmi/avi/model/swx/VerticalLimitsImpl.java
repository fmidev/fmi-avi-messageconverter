package fi.fmi.avi.model.swx;

import org.immutables.value.Value;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

@Value.Immutable
public abstract class VerticalLimitsImpl implements VerticalLimits, Serializable {

    private static final long serialVersionUID = 456150576462009061L;

    private static final VerticalLimits NONE = new Builder().build();

    public static Builder builder() {
        return new Builder();
    }

    public static VerticalLimits none() {
        return NONE;
    }

    public static VerticalLimitsImpl immutableCopyOf(final VerticalLimits verticalLimits) {
        Objects.requireNonNull(verticalLimits);
        if (verticalLimits instanceof VerticalLimitsImpl) {
            return (VerticalLimitsImpl) verticalLimits;
        } else {
            return Builder.copyOf(verticalLimits).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<VerticalLimitsImpl> immutableCopyOf(final Optional<VerticalLimits> verticalLimits) {
        Objects.requireNonNull(verticalLimits);
        return verticalLimits.map(VerticalLimitsImpl::immutableCopyOf);
    }

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    public static class Builder extends ImmutableVerticalLimitsImpl.Builder {

        Builder() {
            setVerticalReference(STANDARD_ATMOSPHERE);
        }

        public static Builder copyOf(final VerticalLimits value) {
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
