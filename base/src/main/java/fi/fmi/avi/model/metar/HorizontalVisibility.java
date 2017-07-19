package fi.fmi.avi.model.metar;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.NumericMeasure;

public interface HorizontalVisibility extends AviationCodeListUser {


    NumericMeasure getPrevailingVisibility();

    RelationalOperator getPrevailingVisibilityOperator();

    NumericMeasure getMinimumVisibility();

    NumericMeasure getMinimumVisibilityDirection();


    void setPrevailingVisibility(NumericMeasure prevailingVisibility);

    void setPrevailingVisibilityOperator(RelationalOperator prevailingVisibilityOperator);

    void setMinimumVisibility(NumericMeasure minimumVisibility);

    void setMinimumVisibilityDirection(NumericMeasure minimumVisibilityDirection);

}
