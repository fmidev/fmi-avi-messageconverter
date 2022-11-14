package fi.fmi.avi.model;

import java.util.Optional;

public interface PhenomenonGeometry {
    Optional<TacOrGeoGeometry> getGeometry();
    Optional<PartialOrCompleteTimeInstant> getTime();
    Optional<Boolean> getApproximateLocation();
    Optional <Boolean> getNoVolcanicAshExpected();
}
