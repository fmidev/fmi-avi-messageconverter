package fi.fmi.avi.model.sigmet;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import fi.fmi.avi.model.NumericMeasure;

@JsonPropertyOrder({"speed", "direction"})
public interface AirmetWind {
    public NumericMeasure getSpeed();
    public NumericMeasure getDirection();
}
