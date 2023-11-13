package fi.fmi.avi.model;

import fi.fmi.avi.model.sigmet.SigmetAnalysisType;
import fi.fmi.avi.model.sigmet.SigmetIntensityChange;

import java.util.Optional;

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
