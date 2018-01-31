package fi.fmi.avi.model.metar;

import java.time.ZoneId;
import java.util.List;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.RunwaySpecificWeatherMessage;
import fi.fmi.avi.model.Weather;

public interface METAR extends RunwaySpecificWeatherMessage, AviationCodeListUser {

    boolean isAutomatedStation();

    boolean isDelayed();

    MetarStatus getStatus();

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

    ColorState getColorState();



    void setAutomatedStation(boolean automatedStation);

    void setDelayed(boolean delayed);

    void setStatus(MetarStatus status);

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

    void setColorState(ColorState color);

    /**
     * Completes the partial trend start and end times by providing the missing year and month information.
     * If no trend information if given, this method has no effect.
     *
     * @param issueYear the (expected or known) year of the message issue time.
     * @param issueMonth the (expected or known) month (1-12) of message issue time.
     * @param issueDay the (expected or known) day-of-month (1-31) of the message issue time.
     * @param issueHour the (expected or known) hour-of-day (0-23) of the message issue time.
     * @param tz timezone
     *
     * @throws IllegalArgumentException when the time references cannot be completed by combining the existing partial times and the provided additional
     * information.
     */
    void completeTrendTimeReferences(int issueYear, int issueMonth, int issueDay, int issueHour, ZoneId tz);

    /**
     * Resets the fully-qualified trend time references.
     *
     * If partial time values have been set previously,and those have been completed
     * using {@link #completeTrendTimeReferences(int, int, int, int, ZoneId)}, the complete
     * time references must no longer be available after this call. Also the methods
     * returning partial time values must return the times based on the originally
     * provided partial time components.
     */
    void uncompleteTrendTimeReferences();

    /**
     * Indicates whether there are partial trend time references in the message.
     *
     * @return true, if the all trend time references are complete or there are no trends, false otherwise.
     */
    boolean areTrendTimeReferencesComplete();


}
