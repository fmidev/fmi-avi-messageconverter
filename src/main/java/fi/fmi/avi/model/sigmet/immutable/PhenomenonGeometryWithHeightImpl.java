package fi.fmi.avi.model.sigmet.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;
import org.locationtech.jts.geom.Geometry;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.TacOrGeoGeometry;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.TacOrGeoGeometryImpl;
import fi.fmi.avi.model.sigmet.PhenomenonGeometryWithHeight;

@FreeBuilder
    @JsonDeserialize(builder = PhenomenonGeometryWithHeightImpl.Builder.class)
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public abstract class PhenomenonGeometryWithHeightImpl implements PhenomenonGeometryWithHeight, Serializable {
        public static PhenomenonGeometryWithHeightImpl immutableCopyOf(final PhenomenonGeometryWithHeight phenomenonGeometry) {
            Objects.requireNonNull(phenomenonGeometry);
            if (phenomenonGeometry instanceof PhenomenonGeometryWithHeightImpl) {
                return (PhenomenonGeometryWithHeightImpl) phenomenonGeometry;
            } else {
                return Builder.from(phenomenonGeometry).build();
            }
        }

        public static Optional<PhenomenonGeometryWithHeightImpl> immutableCopyOf(final Optional<PhenomenonGeometryWithHeight> phenomenonGeometry) {
            Objects.requireNonNull(phenomenonGeometry);
            return phenomenonGeometry.map(PhenomenonGeometryWithHeightImpl::immutableCopyOf);
        }

        public abstract Builder toBuilder();

        public static class Builder extends PhenomenonGeometryWithHeightImpl_Builder {

            public static Builder from(final PhenomenonGeometryWithHeight value) {
                if (value instanceof PhenomenonGeometryWithHeightImpl) {
                    return ((PhenomenonGeometryWithHeightImpl) value).toBuilder();
                } else {
                    return new Builder();
                }
            }

            @Override
            @JsonDeserialize(as = NumericMeasureImpl.class)
            public Builder setUpperLimit(final NumericMeasure upperLimit) {
                return super.setUpperLimit(upperLimit);
            }

            @Override
            @JsonDeserialize(as = NumericMeasureImpl.class)
            public Builder setLowerLimit(final NumericMeasure lowerLimit) {
                return super.setLowerLimit(lowerLimit);
            }

            @Override
            @JsonDeserialize(as= TacOrGeoGeometryImpl.class)
            public Builder setGeometry(TacOrGeoGeometry geom) {
                return super.setGeometry(geom);
            }
        }
}

