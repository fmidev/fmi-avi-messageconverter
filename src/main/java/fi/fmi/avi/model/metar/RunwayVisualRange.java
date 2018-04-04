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

    RunwayDirection runwayDirection();

    NumericMeasure meanRVR();

    Optional<NumericMeasure> varyingRVRMinimum();

    Optional<NumericMeasure> varyingRVRMaximum();

    Optional<RelationalOperator> meanRVROperator();

    Optional<RelationalOperator> varyingRVRMinimumOperator();

    Optional<RelationalOperator> varyingRVRMaximumOperator();

    Optional<VisualRangeTendency> pastTendency();

    Builder toBuilder();

    class Builder extends RunwayVisualRange_Builder {
    }

}
