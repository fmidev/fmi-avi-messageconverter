package fi.fmi.avi.data.metar;

import fi.fmi.avi.data.AviationCodeListUser;
import fi.fmi.avi.data.NumericMeasure;
import fi.fmi.avi.data.PossiblyMissingContent;

public interface RunwayVisualRange extends AviationCodeListUser, PossiblyMissingContent {

    String getRunwayDirectionDesignator();

    NumericMeasure getMeanRVR();

    NumericMeasure getVaryingRVRMinimum();

    NumericMeasure getVaryingRVRMaximum();

    RelationalOperator getMeanRVROperator();

    VisualRangeTendency getPastTendency();


    void setRunwayDirectionDesignator(String runwayDirectionDesignator);

    void setMeanRVR(NumericMeasure meanRVR);

    void setVaryingRVRMinimum(NumericMeasure minimum);

    void setVaryingRVRMaximum(NumericMeasure maximum);

    void setMeanRVROperator(RelationalOperator meanRVROperator);

    void setPastTendency(VisualRangeTendency pastTendency);

}
