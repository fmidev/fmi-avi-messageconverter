package fi.fmi.avi.model;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import fi.fmi.avi.model.immutable.CircleByCenterPointImpl;
import fi.fmi.avi.model.immutable.MultiPolygonGeometryImpl;
import fi.fmi.avi.model.immutable.PointGeometryImpl;
import fi.fmi.avi.model.immutable.PolygonGeometryImpl;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ //
        @JsonSubTypes.Type(value = PointGeometryImpl.class, name = "Point"), //
        @JsonSubTypes.Type(value = PolygonGeometryImpl.class, name = "Polygon"), //
        @JsonSubTypes.Type(value = CircleByCenterPointImpl.class, name = "CircleByCenterPoint"), //
        @JsonSubTypes.Type(value = MultiPolygonGeometryImpl.class, name = "MultiPolygon") //
})
@JsonPropertyOrder({ "crs" })
public interface Geometry {

    Optional<CoordinateReferenceSystem> getCrs();

}
