package fi.fmi.avi.model.sigmet.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.TacOrGeoGeometry;
import fi.fmi.avi.model.immutable.TacOrGeoGeometryImpl;
import fi.fmi.avi.model.sigmet.PhenomenonGeometry;

@FreeBuilder
    @JsonDeserialize(builder = PhenomenonGeometryImpl.Builder.class)
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public abstract class PhenomenonGeometryImpl implements PhenomenonGeometry, Serializable {
        public static PhenomenonGeometryImpl immutableCopyOf(final PhenomenonGeometry phenomenonGeometry) {
            Objects.requireNonNull(phenomenonGeometry);
            if (phenomenonGeometry instanceof PhenomenonGeometryImpl) {
                return (PhenomenonGeometryImpl) phenomenonGeometry;
            } else {
                return Builder.from(phenomenonGeometry).build();
            }
        }

        public static Optional<PhenomenonGeometryImpl> immutableCopyOf(final Optional<PhenomenonGeometry> phenomenonGeometry) {
            Objects.requireNonNull(phenomenonGeometry);
            return phenomenonGeometry.map(PhenomenonGeometryImpl::immutableCopyOf);
        }

        public abstract Builder toBuilder();

        public static class Builder extends PhenomenonGeometryImpl_Builder {

            public static Builder from(final PhenomenonGeometry value) {
                if (value instanceof PhenomenonGeometryImpl) {
                    return ((PhenomenonGeometryImpl) value).toBuilder();
                } else {
                    return new Builder()
                            .setGeometry(value.getGeometry())
                            .setTime(value.getTime())
                            .setApproximateLocation(value.getApproximateLocation());

                }
            }
            @Override
            @JsonDeserialize(as= TacOrGeoGeometryImpl.class)
            public Builder setGeometry(TacOrGeoGeometry geom) {
                return super.setGeometry(TacOrGeoGeometryImpl.immutableCopyOf(geom));
            }
        }
    }

