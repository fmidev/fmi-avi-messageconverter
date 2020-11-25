package fi.fmi.avi.model;

import java.util.List;
import java.util.Optional;

public interface CoordinateReferenceSystem {
    String getName();

    Optional<Integer> getDimension();

    List<String> getAxisLabels();

    List<String> getUomLabels();

    default CoordinateReferenceSystem checkSanity() {
        if (getName().isEmpty()) {
            throw new IllegalStateException("name must not be empty");
        }
        final Integer dimension = getDimension().orElse(null);
        if (dimension != null && dimension <= 0) {
            throw new IllegalStateException("dimension must be positive. Was: " + dimension);
        }
        final List<String> axisLabels = getAxisLabels();
        final List<String> uomLabels = getUomLabels();
        if (dimension == null) {
            if (!axisLabels.isEmpty() && !uomLabels.isEmpty() && axisLabels.size() != uomLabels.size()) {
                throw new IllegalStateException("axisLabels and uomLabels have different size; axisLabels: " + axisLabels + "; uomLabels: " + uomLabels);
            }
        } else {
            if (!axisLabels.isEmpty() && axisLabels.size() != dimension) {
                throw new IllegalStateException("axisLabels size differs from dimension <" + dimension + ">: " + axisLabels);
            }
            if (!uomLabels.isEmpty() && uomLabels.size() != dimension) {
                throw new IllegalStateException("uomLabels size differs from dimension <" + dimension + ">: " + uomLabels);
            }
        }
        return this;
    }
}
