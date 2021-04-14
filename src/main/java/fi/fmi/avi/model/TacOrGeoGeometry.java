package fi.fmi.avi.model;

import java.util.Optional;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.immutable.TacOrGeoGeometryImpl;

@JsonDeserialize(builder = TacOrGeoGeometryImpl.Builder.class)
public interface TacOrGeoGeometry {
    Optional<TacGeometry> getTacGeometry();
    Optional<Geometry> getGeoGeometry();

}
