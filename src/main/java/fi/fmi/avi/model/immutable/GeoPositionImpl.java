package fi.fmi.avi.model.immutable;

import static org.inferred.freebuilder.shaded.com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
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

    public static GeoPositionImpl immutableCopyOf(final GeoPosition geoPosition) {
        checkNotNull(geoPosition);
        if (geoPosition instanceof GeoPositionImpl) {
            return (GeoPositionImpl) geoPosition;
        } else {
            return Builder.from(geoPosition).build();
        }
    }

    public static Optional<GeoPositionImpl> immutableCopyOf(final Optional<GeoPosition> geoPosition) {
        checkNotNull(geoPosition);
        return geoPosition.map(GeoPositionImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends GeoPositionImpl_Builder {

        public static Builder from(final GeoPosition value) {
            return new GeoPositionImpl.Builder().setCoordinateReferenceSystemId(value.getCoordinateReferenceSystemId())
                    .setCoordinates(value.getCoordinates())
                    .setElevationUom(value.getElevationUom())
                    .setElevationValue(value.getElevationValue());
        }
    }

}
