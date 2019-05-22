package fi.fmi.avi.model;

import java.util.Optional;

public interface TacOrGeoGeometry {
    Optional<TacGeometry> getTacGeometry();
    Optional<Geometry> getGeoGeometry();

}
