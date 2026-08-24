package fi.fmi.avi.model.immutable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.fmi.avi.model.CircleByCenterPoint;
import fi.fmi.avi.model.CoordinateReferenceSystem;
import fi.fmi.avi.model.NumericMeasure;
import org.immutables.value.Value;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Value.Immutable
@JsonDeserialize(builder = CircleByCenterPointImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class CircleByCenterPointImpl implements CircleByCenterPoint, Serializable {

    private static final long serialVersionUID = 2380951223773931004L;

    public static Builder builder() {
        return new Builder();
    }

    public static CircleByCenterPointImpl immutableCopyOf(final CircleByCenterPoint geom) {
        Objects.requireNonNull(geom);
        if (geom instanceof CircleByCenterPointImpl) {
            return (CircleByCenterPointImpl) geom;
        } else {
            return CircleByCenterPointImpl.Builder.copyOf(geom).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<CircleByCenterPointImpl> immutableCopyOf(final Optional<CircleByCenterPoint> geom) {
        Objects.requireNonNull(geom);
        return geom.map(CircleByCenterPointImpl::immutableCopyOf);
    }

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    @Override
    @JsonDeserialize(contentAs = CoordinateReferenceSystemImpl.class)
    public abstract Optional<CoordinateReferenceSystem> getCrs();

    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public abstract NumericMeasure getRadius();

    public static class Builder extends ImmutableCircleByCenterPointImpl.Builder {
        Builder() {
        }

        public static Builder copyOf(final CircleByCenterPoint value) {
            if (value instanceof CircleByCenterPointImpl) {
                return ((CircleByCenterPointImpl) value).toBuilder();
            } else {
                return CircleByCenterPointImpl.builder()//
                        .setCrs(value.getCrs())//
                        .addAllCenterPointCoordinates(value.getCenterPointCoordinates());
            }
        }

        // A hand-written setCenterPointCoordinates(List<Double>) used to live here; Immutables'
        // own generated setCenterPointCoordinates(Iterable<Double>) already replaces the whole
        // collection, so the wrapper was redundant once migrated - see doc/immutables-migration.md.
    }

}
