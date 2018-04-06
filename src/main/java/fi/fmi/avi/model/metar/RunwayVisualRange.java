package fi.fmi.avi.model.metar;

import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.RunwayDirection;

@FreeBuilder
@JsonDeserialize(builder = RunwayVisualRange.Builder.class)
public interface RunwayVisualRange extends AviationCodeListUser {

    RunwayDirection getRunwayDirection();

    NumericMeasure getMeanRVR();

    Optional<NumericMeasure> getVaryingRVRMinimum();

    Optional<NumericMeasure> getVaryingRVRMaximum();

    Optional<RelationalOperator> getMeanRVROperator();

    Optional<RelationalOperator> getVaryingRVRMinimumOperator();

    Optional<RelationalOperator> getVaryingRVRMaximumOperator();

    Optional<VisualRangeTendency> getPastTendency();

    Builder toBuilder();

    class Builder extends RunwayVisualRange_Builder {
    }

}
