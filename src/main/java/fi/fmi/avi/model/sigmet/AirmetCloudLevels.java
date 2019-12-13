package fi.fmi.avi.model.sigmet;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import fi.fmi.avi.model.NumericMeasure;

//@JsonPropertyOrder({"cloudbase", "cloudtop", "topabove"})
public interface AirmetCloudLevels {
    public NumericMeasure getCloudBase();
    public NumericMeasure getCloudTop();
    public Optional<Boolean> getTopAbove();
}
