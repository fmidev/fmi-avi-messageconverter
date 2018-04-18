package fi.fmi.avi.model.metar;

import java.util.List;
import java.util.Optional;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.CloudForecast;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.Weather;

public interface TrendForecast extends AviationCodeListUser {

    PartialOrCompleteTimePeriod getValidityTime();

    boolean isCeilingAndVisibilityOk();

    TrendForecastChangeIndicator getChangeIndicator();

    Optional<NumericMeasure> getPrevailingVisibility();

    Optional<RelationalOperator> getPrevailingVisibilityOperator();

    Optional<TrendForecastSurfaceWind> getSurfaceWind();

    Optional<List<Weather>> getForecastWeather();

    boolean isNoSignificantWeather();

    Optional<CloudForecast> getCloud();

    Optional<ColorState> getColorState();

}
