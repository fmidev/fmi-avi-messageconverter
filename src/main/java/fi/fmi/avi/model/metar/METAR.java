package fi.fmi.avi.model.metar;

import java.util.List;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.AerodromeWeatherMessage;
import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.Weather;

@FreeBuilder
@JsonDeserialize(builder = METAR.Builder.class)
public interface METAR extends AerodromeWeatherMessage, AviationCodeListUser {

    boolean special();

    boolean automatedStation();

    boolean delayed();

    MetarStatus status();

    boolean ceilingAndVisibilityOk();

    Optional<NumericMeasure> airTemperature();

    Optional<NumericMeasure> dewpointTemperature();

    Optional<NumericMeasure> altimeterSettingQNH();

    Optional<ObservedSurfaceWind> surfaceWind();

    Optional<HorizontalVisibility> visibility();

    Optional<List<RunwayVisualRange>> runwayVisualRanges();

    Optional<List<Weather>> presentWeather();

    Optional<List<String>> presentWeatherCodes();

    Optional<ObservedClouds> clouds();

    Optional<List<Weather>> recentWeather();

    Optional<List<String>> recentWeatherCodes();

    Optional<WindShear> windShear();

    Optional<SeaState> seaState();

    Optional<List<RunwayState>> runwayStates();

    Optional<List<TrendForecast>> trends();

    Optional<ColorState> colorState();

    Builder toBuilder();

    class Builder extends METAR_Builder {

    }


}
