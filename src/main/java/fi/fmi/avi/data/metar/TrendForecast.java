package fi.fmi.avi.data.metar;

import java.util.List;

import fi.fmi.avi.data.AviationCodeListUser;
import fi.fmi.avi.data.CloudForecast;
import fi.fmi.avi.data.NumericMeasure;
import fi.fmi.avi.data.Weather;


public interface TrendForecast extends AviationCodeListUser {

    TrendTimeGroups getTimeGroups();

    boolean isCeilingAndVisibilityOk();

    TrendForecastChangeIndicator getChangeIndicator();

    NumericMeasure getPrevailingVisibility();

    RelationalOperator getPrevailingVisibilityOperator();

    TrendForecastSurfaceWind getSurfaceWind();

    List<Weather> getForecastWeather();

    List<String> getForecastWeatherCodes();


    CloudForecast getCloud();

    void setTimeGroups(TrendTimeGroups timeGroups);

    void setCeilingAndVisibilityOk(boolean ceilingAndVisibilityOk);

    void setChangeIndicator(TrendForecastChangeIndicator changeIndicator);

    void setPrevailingVisibility(NumericMeasure prevailingVisibility);

    void setPrevailingVisibilityOperator(RelationalOperator prevailingVisibilityOperator);

    void setSurfaceWind(TrendForecastSurfaceWind surfaceWind);

    void setForecastWeather(List<Weather> forecastWeather);

    void setCloud(CloudForecast cloud);

}
