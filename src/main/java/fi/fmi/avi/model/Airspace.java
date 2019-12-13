package fi.fmi.avi.model;

import java.util.Optional;

public interface Airspace {
    public enum AirspaceType {
        FIR,
        UIR,
        FIR_UIR,
        CTA
    }
    AirspaceType getType();
    Optional<Integer> getPart();
    String getName();
    String getDesignator();
}
