package fi.fmi.avi.model.taf;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;

/**
 * Created by rinne on 30/01/15.
 */

public interface TAFAirTemperatureForecast extends AviationCodeListUser {

    NumericMeasure getMaxTemperature();

    PartialOrCompleteTimeInstant getMaxTemperatureTime();

    NumericMeasure getMinTemperature();

    PartialOrCompleteTimeInstant getMinTemperatureTime();



}
