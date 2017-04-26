package fi.fmi.avi.data.metar;

import java.util.List;

import fi.fmi.avi.data.AviationCodeListUser;
import fi.fmi.avi.data.AviationWeatherMessage;
import fi.fmi.avi.data.NumericMeasure;
import fi.fmi.avi.data.Weather;

public interface Metar extends AviationWeatherMessage, AviationCodeListUser {

    boolean isAutomatedStation();

    MetarStatus getStatus();

    String getAerodromeDesignator();

    boolean isCeilingAndVisibilityOk();

    NumericMeasure getAirTemperature();

    NumericMeasure getDewpointTemperature();

    NumericMeasure getAltimeterSettingQNH();

    ObservedSurfaceWind getSurfaceWind();

    HorizontalVisibility getVisibility();

    List<RunwayVisualRange> getRunwayVisualRanges();

    List<Weather> getPresentWeather();

    List<String> getPresentWeatherCodes();
    
    ObservedClouds getClouds();

    List<Weather> getRecentWeather();
    
    List<String> getRecentWeatherCodes();

    WindShear getWindShear();

    SeaState getSeaState();

    List<RunwayState> getRunwayStates();

    List<TrendForecast> getTrends();


    void setAutomatedStation(boolean automatedStation);

    void setStatus(MetarStatus status);

    void setAerodromeDesignator(String aerodromeDesignator);

    void setCeilingAndVisibilityOk(boolean ceilingAndVisibilityOk);

    void setAirTemperature(NumericMeasure airTemperature);

    void setDewpointTemperature(NumericMeasure dewpointTemperature);

    void setAltimeterSettingQNH(NumericMeasure altimeterSettingQNH);

    void setSurfaceWind(ObservedSurfaceWind surfaceWind);

    void setVisibility(HorizontalVisibility visibility);

    void setRunwayVisualRanges(List<RunwayVisualRange> runwayVisualRange);

    void setPresentWeather(List<Weather> presentWeather);

    void setClouds(ObservedClouds clouds);

    void setRecentWeather(List<Weather> recentWeather);

    void setWindShear(WindShear windShear);

    void setSeaState(SeaState seaState);

    void setRunwayStates(List<RunwayState> runwayStates);

    void setTrends(List<TrendForecast> trends);


}
