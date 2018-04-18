package fi.fmi.avi.model.metar;

import java.util.Optional;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.RunwayDirection;

public interface RunwayVisualRange extends AviationCodeListUser {

    RunwayDirection getRunwayDirection();

    NumericMeasure getMeanRVR();

    Optional<NumericMeasure> getVaryingRVRMinimum();

    Optional<NumericMeasure> getVaryingRVRMaximum();

    Optional<RelationalOperator> getMeanRVROperator();

    Optional<RelationalOperator> getVaryingRVRMinimumOperator();

    Optional<RelationalOperator> getVaryingRVRMaximumOperator();

    Optional<VisualRangeTendency> getPastTendency();

}
