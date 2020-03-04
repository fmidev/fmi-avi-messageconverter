package fi.fmi.avi.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import fi.fmi.avi.model.SWX.immutable.SWXGeometryImpl;
import fi.fmi.avi.model.immutable.PointGeometryImpl;
import fi.fmi.avi.model.immutable.PolygonsGeometryImpl;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PointGeometryImpl.class, name = "Point"),
        @JsonSubTypes.Type(value = PolygonsGeometryImpl.class, name = "Polygon"),
        @JsonSubTypes.Type(value = SWXGeometryImpl.class, name = "SWX")
})

public interface Geometry {
}
