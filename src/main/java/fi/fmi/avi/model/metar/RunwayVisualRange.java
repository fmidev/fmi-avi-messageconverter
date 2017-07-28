package fi.fmi.avi.model.metar;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.RunwayDirection;

public interface RunwayVisualRange extends AviationCodeListUser {

    RunwayDirection getRunwayDirection();

    NumericMeasure getMeanRVR();

    NumericMeasure getVaryingRVRMinimum();

    NumericMeasure getVaryingRVRMaximum();

    RelationalOperator getMeanRVROperator();
    
    RelationalOperator getVaryingRVRMinimumOperator();
    
    RelationalOperator getVaryingRVRMaximumOperator();

    VisualRangeTendency getPastTendency();


    void setRunwayDirection(RunwayDirection runwayDirection);

    void setMeanRVR(NumericMeasure meanRVR);

    void setVaryingRVRMinimum(NumericMeasure minimum);

    void setVaryingRVRMaximum(NumericMeasure maximum);

    void setMeanRVROperator(RelationalOperator meanRVROperator);
    
    void setVaryingRVRMinimumOperator(RelationalOperator minRVROperator);
    
    void setVaryingRVRMaximumOperator(RelationalOperator maxRVROperator);

    void setPastTendency(VisualRangeTendency pastTendency);

}
