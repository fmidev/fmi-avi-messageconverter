package fi.fmi.avi.data.metar;

import fi.fmi.avi.data.AviationCodeListUser;
import fi.fmi.avi.data.NumericMeasure;
import fi.fmi.avi.data.RunwayDirection;

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
