package fi.fmi.avi.model.sigmet;

import java.util.Optional;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.NumericMeasure;

public interface PhenomenonGeometryWithHeight extends PhenomenonGeometry{
    public Optional<NumericMeasure> getLowerLimit();
    public Optional<AviationCodeListUser.RelationalOperator> getLowerLimitOperator();

    public Optional<NumericMeasure> getUpperLimit();
    public Optional<AviationCodeListUser.RelationalOperator> getUpperLimitOperator();

}
