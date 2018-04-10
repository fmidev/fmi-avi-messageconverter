package fi.fmi.avi.model.taf;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;

/**
 * Created by rinne on 30/01/15.
 */
@FreeBuilder
@JsonDeserialize(builder = TAFAirTemperatureForecast.Builder.class)
public interface TAFAirTemperatureForecast extends AviationCodeListUser {

    NumericMeasure getMaxTemperature();

    PartialOrCompleteTimeInstant getMaxTemperatureTime();

    NumericMeasure getMinTemperature();

    PartialOrCompleteTimeInstant getMinTemperatureTime();

    Builder toBuilder();

    class Builder extends TAFAirTemperatureForecast_Builder {
    }

}
