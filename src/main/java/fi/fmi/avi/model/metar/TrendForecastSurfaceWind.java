package fi.fmi.avi.model.metar;

import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.NumericMeasure;

@FreeBuilder
@JsonDeserialize(builder = TrendForecastSurfaceWind.Builder.class)
public interface TrendForecastSurfaceWind extends AviationCodeListUser {

    NumericMeasure meanWindDirection();

    NumericMeasure meanWindSpeed();

    Optional<NumericMeasure> windGust();

    Builder toBuilder();

    class Builder extends TrendForecastSurfaceWind_Builder {
    }

}
