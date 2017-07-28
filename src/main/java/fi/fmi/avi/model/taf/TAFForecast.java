package fi.fmi.avi.model.taf;

import java.util.List;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.CloudForecast;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.Weather;

/**
 * Created by rinne on 30/01/15.
 */
public interface TAFForecast extends AviationCodeListUser {

    boolean isCeilingAndVisibilityOk();

    NumericMeasure getPrevailingVisibility();

    RelationalOperator getPrevailingVisibilityOperator();

    TAFSurfaceWind getSurfaceWind();

    List<Weather> getForecastWeather();

    boolean isNoSignificantWeather();

    List<String> getForecastWeatherCodes();

    CloudForecast getCloud();


    void setCeilingAndVisibilityOk(boolean ceilingAndVisibilityOk);

    void setPrevailingVisibility(NumericMeasure prevailingVisibility);

    void setPrevailingVisibilityOperator(RelationalOperator prevailingVisibilityOperator);

    void setSurfaceWind(TAFSurfaceWind surfaceWind);

    void setForecastWeather(List<Weather> forecastWeather);

    void setNoSignificantWeather(boolean nsw);

    void setCloud(CloudForecast cloud);
}
