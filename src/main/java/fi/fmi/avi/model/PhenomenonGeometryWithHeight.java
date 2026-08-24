package fi.fmi.avi.model;

import fi.fmi.avi.model.sigmet.SigmetAnalysisType;
import fi.fmi.avi.model.sigmet.SigmetIntensityChange;

import java.util.Optional;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.immutable.PhenomenonGeometryWithHeightImpl;

/**
 * See {@link PhenomenonGeometry}'s class-level {@link JsonDeserialize} javadoc for why this interface needs its own
 * (more specific) class-level hint too, rather than relying solely on a property-level hint.
 */
@JsonDeserialize(as = PhenomenonGeometryWithHeightImpl.class)
public interface PhenomenonGeometryWithHeight extends PhenomenonGeometry {
    Optional<NumericMeasure> getLowerLimit();

    Optional<AviationCodeListUser.RelationalOperator> getLowerLimitOperator();

    Optional<NumericMeasure> getUpperLimit();

    Optional<AviationCodeListUser.RelationalOperator> getUpperLimitOperator();

    Optional<NumericMeasure> getMovingSpeed();

    Optional<NumericMeasure> getMovingDirection();

    Optional<SigmetIntensityChange> getIntensityChange();

    Optional<SigmetAnalysisType> getAnalysisType();

}
