package fi.fmi.avi.data.taf;

import java.util.List;

import fi.fmi.avi.data.AviationCodeListUser;
import fi.fmi.avi.data.CloudForecast;
import fi.fmi.avi.data.NumericMeasure;
import fi.fmi.avi.data.PossiblyMissingContent;

/**
 * Created by rinne on 30/01/15.
 */
public interface TAFForecast extends AviationCodeListUser, PossiblyMissingContent {

    boolean isCeilingAndVisibilityOk();

    NumericMeasure getPrevailingVisibility();

    RelationalOperator getPrevailingVisibilityOperator();

    TAFSurfaceWind getSurfaceWind();

    List<String> getForecastWeather();

    CloudForecast getCloud();


    void setCeilingAndVisibilityOk(boolean ceilingAndVisibilityOk);

    void setPrevailingVisibility(NumericMeasure prevailingVisibility);

    void setPrevailingVisibilityOperator(RelationalOperator prevailingVisibilityOperator);

    void setSurfaceWind(TAFSurfaceWind surfaceWind);

    void setForecastWeather(List<String> forecastWeather);

    void setCloud(CloudForecast cloud);
}
