package fi.fmi.avi.model.metar;

import java.util.List;
import java.util.Optional;

import fi.fmi.avi.model.AerodromeWeatherMessage;
import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.Weather;

/**
 * Created by rinne on 06/04/2018.
 */

public interface MeteorologicalTerminalAirReport extends AerodromeWeatherMessage, AviationCodeListUser {

    boolean isAutomatedStation();

    MetarStatus getStatus();

    boolean isCeilingAndVisibilityOk();

    Optional<NumericMeasure> getAirTemperature();

    Optional<NumericMeasure> getDewpointTemperature();

    Optional<NumericMeasure> getAltimeterSettingQNH();

    Optional<ObservedSurfaceWind> getSurfaceWind();

    Optional<HorizontalVisibility> getVisibility();

    Optional<List<RunwayVisualRange>> getRunwayVisualRanges();

    Optional<List<Weather>> getPresentWeather();

    Optional<ObservedClouds> getClouds();

    Optional<List<Weather>> getRecentWeather();

    Optional<WindShear> getWindShear();

    Optional<SeaState> getSeaState();

    Optional<List<RunwayState>> getRunwayStates();

    boolean isSnowClosure();

    boolean isNoSignificantChanges();

    Optional<List<TrendForecast>> getTrends();

    Optional<ColorState> getColorState();

}
