package fi.fmi.avi.model.swx.amd79.immutable;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.swx.amd79.VerticalLimits;
import org.inferred.freebuilder.FreeBuilder;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

@FreeBuilder
public abstract class VerticalLimitsImpl implements VerticalLimits, Serializable {

    /**
     * The vertical distance is measured with an altimeter set to the standard atmosphere.
     * See
     * <a href="http://aixm.aero/sites/aixm.aero/files/imce/AIXM511HTML/AIXM/DataType_CodeVerticalReferenceType.html">AIXM 5.1.1 CodeVerticalReferenceType</a>.
     */
    public static final String STANDARD_ATMOSPHERE = "STD";
    private static final long serialVersionUID = 456150576462009061L;

    public static Builder builder() {
        return new Builder();
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

        public static VerticalLimitsImpl none() {
            return builder().build();
        }

        public boolean isAbove() {
            return getOperator().filter(op -> op == AviationCodeListUser.RelationalOperator.ABOVE).isPresent();
        }

        public boolean isBelow() {
            return getOperator().filter(op -> op == AviationCodeListUser.RelationalOperator.BELOW).isPresent();
        }
    }
}
