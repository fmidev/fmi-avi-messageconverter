package fi.fmi.avi.model.taf;

import java.util.List;
import java.util.Optional;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.CloudForecast;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.Weather;

/**
 * Created by rinne on 02/05/2018.
 */
public interface TAFForecast extends AviationCodeListUser {

    boolean isCeilingAndVisibilityOk();

    Optional<NumericMeasure> getPrevailingVisibility();

    Optional<AviationCodeListUser.RelationalOperator> getPrevailingVisibilityOperator();

    Optional<TAFSurfaceWind> getSurfaceWind();

    Optional<List<Weather>> getForecastWeather();

    boolean isNoSignificantWeather();

    Optional<CloudForecast> getCloud();
}
