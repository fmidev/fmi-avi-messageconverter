package fi.fmi.avi.model;

import java.util.Optional;

import fi.fmi.avi.model.sigmet.SigmetAnalysisType;

public interface PhenomenonGeometry {
    Optional<TacOrGeoGeometry> getGeometry();
    Optional<PartialOrCompleteTimeInstant> getTime();
    Optional<Boolean> getApproximateLocation();


}
