package fi.fmi.avi.model.sigmet;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import fi.fmi.avi.model.NumericMeasure;

@JsonPropertyOrder({"cloudbottom", "cloudtop"})
public interface AirmetCloudLevels {
    public NumericMeasure getCloudBottom();
    public NumericMeasure getCloudTop();
}
