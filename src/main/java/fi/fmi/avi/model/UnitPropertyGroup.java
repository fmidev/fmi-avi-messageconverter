package fi.fmi.avi.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "designator", "name", "type" })
public interface UnitPropertyGroup {
    String getName();

    String getType();

    String getDesignator();
}
