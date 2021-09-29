package fi.fmi.avi.model.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.sigmet.SigmetIntensityChange;
import fi.fmi.avi.model.PhenomenonGeometryWithHeight;
import fi.fmi.avi.model.TacOrGeoGeometry;

@FreeBuilder
@JsonDeserialize(builder = PhenomenonGeometryWithHeightImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class PhenomenonGeometryWithHeightImpl implements PhenomenonGeometryWithHeight, Serializable {
    private static final long serialVersionUID = 3780345549531133901L;

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
                return new Builder().setLowerLimit(value.getLowerLimit())
                        .setUpperLimit(value.getUpperLimit())
                        .setLowerLimitOperator(value.getLowerLimitOperator())
                        .setUpperLimitOperator(value.getUpperLimitOperator())
                        .setGeometry(TacOrGeoGeometryImpl.immutableCopyOf(value.getGeometry()))
                        .setApproximateLocation(value.getApproximateLocation())
                        .setMovingDirection(value.getMovingDirection())
                        .setMovingSpeed(value.getMovingSpeed())
                        .setIntensityChange(value.getIntensityChange())
                        .setAnalysisType(value.getAnalysisType())
                        .setTime(value.getTime());
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
        @JsonDeserialize(as = TacOrGeoGeometryImpl.class)
        public Builder setGeometry(final TacOrGeoGeometry geom) {
            return super.setGeometry(TacOrGeoGeometryImpl.immutableCopyOf(geom));
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setMovingSpeed(final NumericMeasure speed) {
            return super.setMovingSpeed(NumericMeasureImpl.immutableCopyOf(speed));
        }
        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setMovingDirection(final NumericMeasure dir) {
            return super.setMovingDirection(NumericMeasureImpl.immutableCopyOf(dir));

        }
     }
}

