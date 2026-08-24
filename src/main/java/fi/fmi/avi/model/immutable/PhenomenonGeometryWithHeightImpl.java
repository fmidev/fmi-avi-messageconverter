package fi.fmi.avi.model.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.PhenomenonGeometryWithHeight;
import fi.fmi.avi.model.TacOrGeoGeometry;

@Value.Immutable
@JsonDeserialize(builder = PhenomenonGeometryWithHeightImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"time", "lowerLimit", "upperLimit", "analysisType", "intensityChange",
                    "approximateLocation", "geometry"})
public abstract class PhenomenonGeometryWithHeightImpl implements PhenomenonGeometryWithHeight, Serializable {
    private static final long serialVersionUID = 3780345549531133901L;

    public static Builder builder() {
        return new Builder();
    }

    public static PhenomenonGeometryWithHeightImpl immutableCopyOf(
            final PhenomenonGeometryWithHeight phenomenonGeometry) {
        Objects.requireNonNull(phenomenonGeometry);
        if (phenomenonGeometry instanceof PhenomenonGeometryWithHeightImpl) {
            return (PhenomenonGeometryWithHeightImpl) phenomenonGeometry;
        } else {
            return Builder.copyOf(phenomenonGeometry).build();
        }
    }

    public static Optional<PhenomenonGeometryWithHeightImpl> immutableCopyOf(
            final Optional<PhenomenonGeometryWithHeight> phenomenonGeometry) {
        Objects.requireNonNull(phenomenonGeometry);
        return phenomenonGeometry.map(PhenomenonGeometryWithHeightImpl::immutableCopyOf);
    }

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    @Override
    @JsonDeserialize(contentAs = NumericMeasureImpl.class)
    public abstract Optional<NumericMeasure> getUpperLimit();

    @Override
    @JsonDeserialize(contentAs = NumericMeasureImpl.class)
    public abstract Optional<NumericMeasure> getLowerLimit();

    @Override
    @JsonDeserialize(contentAs = TacOrGeoGeometryImpl.class)
    public abstract Optional<TacOrGeoGeometry> getGeometry();

    @Override
    @JsonDeserialize(contentAs = NumericMeasureImpl.class)
    public abstract Optional<NumericMeasure> getMovingSpeed();

    @Override
    @JsonDeserialize(contentAs = NumericMeasureImpl.class)
    public abstract Optional<NumericMeasure> getMovingDirection();

    public static class Builder extends ImmutablePhenomenonGeometryWithHeightImpl.Builder {

        public static Builder copyOf(final PhenomenonGeometryWithHeight value) {
            if (value instanceof PhenomenonGeometryWithHeightImpl) {
                return ((PhenomenonGeometryWithHeightImpl) value).toBuilder();
            } else {
                return new Builder()
                        .setGeometry(TacOrGeoGeometryImpl.immutableCopyOf(value.getGeometry()))
                        .setTime(value.getTime())
                        .setApproximateLocation(value.getApproximateLocation())
                        .setLowerLimit(value.getLowerLimit())
                        .setLowerLimitOperator(value.getLowerLimitOperator())
                        .setUpperLimit(value.getUpperLimit())
                        .setUpperLimitOperator(value.getUpperLimitOperator())
                        .setMovingSpeed(value.getMovingSpeed())
                        .setMovingDirection(value.getMovingDirection())
                        .setIntensityChange(value.getIntensityChange())
                        .setAnalysisType(value.getAnalysisType());
            }
        }





    }
}
