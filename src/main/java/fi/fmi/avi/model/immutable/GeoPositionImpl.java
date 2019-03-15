package fi.fmi.avi.model.immutable;


import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.GeoPosition;

/**
 * Created by rinne on 17/04/2018.
 */
@FreeBuilder
@JsonDeserialize(builder = GeoPositionImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"coordinateReferenceSystemId", "coordinates", "elevationValue", "elevationUom"})
public abstract class GeoPositionImpl implements GeoPosition, Serializable {

    private static final long serialVersionUID = -69857237712526561L;

    public static Builder builder() {
        return new Builder();
    }

    public static GeoPositionImpl immutableCopyOf(final GeoPosition geoPosition) {
        Objects.requireNonNull(geoPosition);
        if (geoPosition instanceof GeoPositionImpl) {
            return (GeoPositionImpl) geoPosition;
        } else {
            return Builder.from(geoPosition).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<GeoPositionImpl> immutableCopyOf(final Optional<GeoPosition> geoPosition) {
        Objects.requireNonNull(geoPosition);
        return geoPosition.map(GeoPositionImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends GeoPositionImpl_Builder {

        @Deprecated
        public Builder() {
        }

        public static Builder from(final GeoPosition value) {
            if (value instanceof GeoPositionImpl) {
                return ((GeoPositionImpl) value).toBuilder();
            } else {
                return new GeoPositionImpl.Builder()//
                        .setCoordinateReferenceSystemId(value.getCoordinateReferenceSystemId())//
                        .addAllCoordinates(value.getCoordinates())
                        .setElevationUom(value.getElevationUom())
                        .setElevationValue(value.getElevationValue());
            }
        }
    }

}
