package fi.fmi.avi.model.metar;

import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.NumericMeasure;

@FreeBuilder
@JsonDeserialize(builder = HorizontalVisibility.Builder.class)
public interface HorizontalVisibility extends AviationCodeListUser {

    NumericMeasure prevailingVisibility();

    Optional<RelationalOperator> prevailingVisibilityOperator();

    Optional<NumericMeasure> minimumVisibility();

    Optional<NumericMeasure> minimumVisibilityDirection();

    Builder toBuilder();

    class Builder extends HorizontalVisibility_Builder {
    }
}
