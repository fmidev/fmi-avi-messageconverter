package fi.fmi.avi.model.immutable;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.CoordinateReferenceSystem;
import fi.fmi.avi.model.PointGeometry;

@Value.Immutable
@JsonDeserialize(builder = PointGeometryImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class PointGeometryImpl implements PointGeometry, Serializable {

    private static final long serialVersionUID = -5666437406419795118L;

    public static Builder builder() {
        return new Builder();
    }

    public static PointGeometryImpl immutableCopyOf(final PointGeometry pointGeometry) {
        Objects.requireNonNull(pointGeometry);
        if (pointGeometry instanceof PointGeometryImpl) {
            return (PointGeometryImpl) pointGeometry;
        } else {
            return Builder.copyOf(pointGeometry).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<PointGeometryImpl> immutableCopyOf(final Optional<PointGeometry> pointGeometry) {
        Objects.requireNonNull(pointGeometry);
        return pointGeometry.map(PointGeometryImpl::immutableCopyOf);
    }

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    @Override
    @JsonDeserialize(contentAs = CoordinateReferenceSystemImpl.class)
    public abstract Optional<CoordinateReferenceSystem> getCrs();

    public static class Builder extends ImmutablePointGeometryImpl.Builder {

        Builder() {
        }

        public static Builder copyOf(final PointGeometry value) {
            if (value instanceof PointGeometryImpl) {
                return ((PointGeometryImpl) value).toBuilder();
            } else {
                return PointGeometryImpl.builder()//
                        .setCrs(value.getCrs())//
                        .addAllCoordinates(value.getCoordinates());
            }
        }

        // A hand-written setCoordinates(List<Double>) used to live here; Immutables' own generated
        // setCoordinates(Iterable<Double>) already replaces the whole collection, so the wrapper
        // was redundant once migrated - see doc/immutables-migration.md.
    }
}
