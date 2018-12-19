package fi.fmi.avi.model.sigmet;

import java.util.Optional;

import org.locationtech.jts.geom.Geometry;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.TacOrGeoGeometry;

public interface PhenomenonGeometry {
    public Optional<TacOrGeoGeometry> getGeometry();
    public Optional<PartialOrCompleteTimeInstant> getTime();
    public Optional<Boolean> getApproximateLocation();
}
