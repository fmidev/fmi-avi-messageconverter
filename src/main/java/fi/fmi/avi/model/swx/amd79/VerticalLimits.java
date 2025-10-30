package fi.fmi.avi.model.swx.amd79;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.NumericMeasure;

import java.util.Optional;

public interface VerticalLimits {

    Optional<NumericMeasure> getLowerLimit();

    Optional<NumericMeasure> getUpperLimit();

    Optional<AviationCodeListUser.RelationalOperator> getOperator();

    String getVerticalReference();

}
