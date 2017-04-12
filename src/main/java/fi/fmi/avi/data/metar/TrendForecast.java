package fi.fmi.avi.data.metar;

import java.util.List;

import fi.fmi.avi.data.AviationCodeListUser;
import fi.fmi.avi.data.CloudForecast;
import fi.fmi.avi.data.NumericMeasure;

public interface TrendForecast extends AviationCodeListUser {

    List<String> getTimeGroups();

    TrendTimeGroups getParsedTimeGroups();

    boolean isCeilingAndVisibilityOk();

    TrendForecastChangeIndicator getChangeIndicator();

    NumericMeasure getPrevailingVisibility();

    RelationalOperator getPrevailingVisibilityOperator();

    TrendForecastSurfaceWind getSurfaceWind();

    List<String> getForecastWeather();

    CloudForecast getCloud();

    void setTimeGroups(List<String> timeGroups);

    void setCeilingAndVisibilityOk(boolean ceilingAndVisibilityOk);

    void setChangeIndicator(TrendForecastChangeIndicator changeIndicator);

    void setPrevailingVisibility(NumericMeasure prevailingVisibility);

    void setPrevailingVisibilityOperator(RelationalOperator prevailingVisibilityOperator);

    void setSurfaceWind(TrendForecastSurfaceWind surfaceWind);

    void setForecastWeather(List<String> forecastWeather);

    void setCloud(CloudForecast cloud);

}
