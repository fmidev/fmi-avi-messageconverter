package fi.fmi.avi.model;

import java.util.Optional;

import org.locationtech.jts.geom.Geometry;

public interface TacOrGeoGeometry {
    Optional<TacGeometry> getTacGeometry();
    Optional<Geometry> getGeoGeometry();

}
