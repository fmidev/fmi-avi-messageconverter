package fi.fmi.avi.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"designator", "name", "type"})
public interface UnitPropertyGroup {
    public String getName();
    public String getType();
    public String getDesignator();
}
