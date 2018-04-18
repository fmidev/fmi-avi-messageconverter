package fi.fmi.avi.model.taf;

import java.util.List;
import java.util.Optional;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.CloudForecast;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.Weather;

/**
 * Created by rinne on 30/01/15.
 */

public interface TAFChangeForecast extends AviationCodeListUser {

    TAFChangeIndicator getChangeIndicator();

    PartialOrCompleteTimePeriod getValidityTime();

    boolean isCeilingAndVisibilityOk();

    Optional<NumericMeasure> getPrevailingVisibility();

    Optional<RelationalOperator> getPrevailingVisibilityOperator();

    Optional<TAFSurfaceWind> getSurfaceWind();

    Optional<List<Weather>> getForecastWeather();

    boolean isNoSignificantWeather();

    Optional<CloudForecast> getCloud();

}
