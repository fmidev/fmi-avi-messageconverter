package fi.fmi.avi.data.metar;

import fi.fmi.avi.data.AviationCodeListUser;
import fi.fmi.avi.data.NumericMeasure;

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
