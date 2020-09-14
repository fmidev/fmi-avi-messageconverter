package fi.fmi.avi.model;

import java.util.Optional;

public interface PhenomenonGeometryWithHeight extends PhenomenonGeometry{
    public Optional<NumericMeasure> getLowerLimit();
    public Optional<AviationCodeListUser.RelationalOperator> getLowerLimitOperator();

    public Optional<NumericMeasure> getUpperLimit();
    public Optional<AviationCodeListUser.RelationalOperator> getUpperLimitOperator();

}
