package fi.fmi.avi.model;

import java.util.Optional;

public interface PhenomenonGeometryWithHeight extends PhenomenonGeometry{
    Optional<NumericMeasure> getLowerLimit();
    Optional<AviationCodeListUser.RelationalOperator> getLowerLimitOperator();

    Optional<NumericMeasure> getUpperLimit();
    Optional<AviationCodeListUser.RelationalOperator> getUpperLimitOperator();

}
