package fi.fmi.avi.model.metar;

import java.util.Optional;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.NumericMeasure;

public interface HorizontalVisibility extends AviationCodeListUser {

    NumericMeasure getPrevailingVisibility();

    Optional<RelationalOperator> getPrevailingVisibilityOperator();

    Optional<NumericMeasure> getMinimumVisibility();

    Optional<NumericMeasure> getMinimumVisibilityDirection();

}
