package fi.fmi.avi.model.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.Geometry;
import fi.fmi.avi.model.PointGeometry;
import fi.fmi.avi.model.TacOrGeoGeometry;

@FreeBuilder
    @JsonDeserialize(builder = TacOrGeoGeometryImpl.Builder.class)
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public abstract class TacOrGeoGeometryImpl implements TacOrGeoGeometry, Serializable {
        public static Builder builder() {
            return new Builder();
        }
        public static  TacOrGeoGeometryImpl of(final Geometry value) {
            return new Builder().setGeoGeometry(value).build();
        }

        public static TacOrGeoGeometryImpl immutableCopyOf(final TacOrGeoGeometry tacOrGeoGeometry) {
            Objects.requireNonNull(tacOrGeoGeometry);
            if (tacOrGeoGeometry instanceof TacOrGeoGeometryImpl) {
                return (TacOrGeoGeometryImpl) tacOrGeoGeometry;
            } else {
                return Builder.from(tacOrGeoGeometry).build();
            }
        }

        public static Optional<TacOrGeoGeometryImpl> immutableCopyOf(final Optional<TacOrGeoGeometry> tacOrGeoGeometry) {
            Objects.requireNonNull(tacOrGeoGeometry);
            return tacOrGeoGeometry.map(TacOrGeoGeometryImpl::immutableCopyOf);
        }

        public abstract Builder toBuilder();

        public static class Builder extends TacOrGeoGeometryImpl_Builder {

            public static Builder from(final TacOrGeoGeometry value) {
                if (value instanceof TacOrGeoGeometryImpl) {
                    return ((TacOrGeoGeometryImpl) value).toBuilder();
                } else {
                    return new Builder();

                }
            }

        }

    }

