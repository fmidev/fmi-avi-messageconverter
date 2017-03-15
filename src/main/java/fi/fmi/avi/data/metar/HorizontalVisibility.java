package fi.fmi.avi.data.metar;

import fi.fmi.avi.data.AviationCodeListUser;
import fi.fmi.avi.data.NumericMeasure;
import fi.fmi.avi.data.AviationCodeListUser.MissingReason;
import fi.fmi.avi.data.AviationCodeListUser.RelationalOperator;
import fi.fmi.avi.data.PossiblyMissingContent;

public interface HorizontalVisibility extends AviationCodeListUser, PossiblyMissingContent {


    NumericMeasure getPrevailingVisibility();

    RelationalOperator getPrevailingVisibilityOperator();

    NumericMeasure getMinimumVisibility();

    NumericMeasure getMinimumVisibilityDirection();


    void setPrevailingVisibility(NumericMeasure prevailingVisibility);

    void setPrevailingVisibilityOperator(RelationalOperator prevailingVisibilityOperator);

    void setMinimumVisibility(NumericMeasure minimumVisibility);

    void setMinimumVisibilityDirection(NumericMeasure minimumVisibilityDirection);

}
