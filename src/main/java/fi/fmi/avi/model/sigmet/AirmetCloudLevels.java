package fi.fmi.avi.model.sigmet;

import java.util.Optional;

import fi.fmi.avi.model.NumericMeasure;

//@JsonPropertyOrder({"cloudbase", "cloudtop", "topabove"})
public interface AirmetCloudLevels {
    NumericMeasure getCloudBase();

    NumericMeasure getCloudTop();

    Optional<Boolean> getTopAbove();
}
