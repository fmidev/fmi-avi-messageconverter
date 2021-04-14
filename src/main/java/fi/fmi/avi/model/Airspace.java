package fi.fmi.avi.model;

import java.util.Optional;

public interface Airspace {
    AirspaceType getType();

    Optional<Integer> getPart();

    String getName();

    String getDesignator();

    enum AirspaceType {
        FIR, UIR, FIR_UIR, CTA
    }
}
