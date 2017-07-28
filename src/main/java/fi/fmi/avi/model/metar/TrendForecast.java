package fi.fmi.avi.model.metar;

import java.util.List;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.CloudForecast;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.TimeReferenceAmendable;
import fi.fmi.avi.model.Weather;


public interface TrendForecast extends AviationCodeListUser, TimeReferenceAmendable {

    TrendTimeGroups getTimeGroups();

    boolean isCeilingAndVisibilityOk();

    TrendForecastChangeIndicator getChangeIndicator();

    NumericMeasure getPrevailingVisibility();

    RelationalOperator getPrevailingVisibilityOperator();

    TrendForecastSurfaceWind getSurfaceWind();

    List<Weather> getForecastWeather();

    List<String> getForecastWeatherCodes();

    boolean isNoSignificantWeather();

    CloudForecast getCloud();

    ColorState getColorState();

    
    void setTimeGroups(TrendTimeGroups timeGroups);

    void setCeilingAndVisibilityOk(boolean ceilingAndVisibilityOk);

    void setChangeIndicator(TrendForecastChangeIndicator changeIndicator);

    void setPrevailingVisibility(NumericMeasure prevailingVisibility);

    void setPrevailingVisibilityOperator(RelationalOperator prevailingVisibilityOperator);

    void setSurfaceWind(TrendForecastSurfaceWind surfaceWind);

    void setForecastWeather(List<Weather> forecastWeather);

    void setNoSignificantWeather(boolean nsw);

    void setCloud(CloudForecast cloud);

    void setColorState(ColorState color);

}