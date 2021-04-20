package fi.fmi.avi.model;

import java.util.Optional;

import fi.fmi.avi.model.sigmet.SigmetAnalysisType;
import fi.fmi.avi.model.sigmet.SigmetIntensityChange;

public interface PhenomenonGeometryWithHeight extends PhenomenonGeometry{
    Optional<NumericMeasure> getLowerLimit();
    Optional<AviationCodeListUser.RelationalOperator> getLowerLimitOperator();

    Optional<NumericMeasure> getUpperLimit();
    Optional<AviationCodeListUser.RelationalOperator> getUpperLimitOperator();

    Optional<NumericMeasure> getMovingSpeed();

    Optional<NumericMeasure> getMovingDirection();

    Optional<SigmetIntensityChange> getIntensityChange();
    SigmetAnalysisType getAnalysisType();

}
