package fi.fmi.avi.data.metar.impl;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.data.NumericMeasure;
import fi.fmi.avi.data.Weather;
import fi.fmi.avi.data.impl.NumericMeasureImpl;
import fi.fmi.avi.data.impl.WeatherImpl;
import fi.fmi.avi.data.metar.HorizontalVisibility;
import fi.fmi.avi.data.metar.Metar;
import fi.fmi.avi.data.metar.ObservedClouds;
import fi.fmi.avi.data.metar.ObservedSurfaceWind;
import fi.fmi.avi.data.metar.RunwayState;
import fi.fmi.avi.data.metar.RunwayVisualRange;
import fi.fmi.avi.data.metar.SeaState;
import fi.fmi.avi.data.metar.TrendForecast;
import fi.fmi.avi.data.metar.WindShear;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class MetarImpl implements Metar {

    private boolean automatedStation;
    private MetarStatus status;
    private int dayOfMonth = -1;
    private int hour = -1;
    private int minute = -1;
    private String timeZone;
    private String aerodromeDesignator;
    private boolean ceilingAndVisibilityOk;
    private NumericMeasure airTemperature;
    private NumericMeasure dewpointTemperature;
    private NumericMeasure altimeterSettingQNH;
    private ObservedSurfaceWind surfaceWind;
    private HorizontalVisibility visibility;
    private List<RunwayVisualRange> runwayVisualRanges;
    private List<Weather> presentWeather;
    private ObservedClouds clouds;
    private List<Weather> recentWeather;
    private WindShear windShear;
    private SeaState seaState;
    private List<RunwayState> runwayStates;
    private List<TrendForecast> trends;
    private List<String> remarks;

    public MetarImpl() {
    }

    public MetarImpl(final Metar input) {
        this.automatedStation = input.isAutomatedStation();
        this.status = input.getStatus();
        this.dayOfMonth = input.getIssueDayOfMonth();
        this.hour = input.getIssueHour();
        this.minute = input.getIssueMinute();
        this.timeZone = input.getIssueTimeZone();
        this.aerodromeDesignator = input.getAerodromeDesignator();
        this.ceilingAndVisibilityOk = input.isCeilingAndVisibilityOk();
        this.airTemperature = new NumericMeasureImpl(input.getAirTemperature());
        this.dewpointTemperature = new NumericMeasureImpl(input.getDewpointTemperature());
        this.altimeterSettingQNH = new NumericMeasureImpl(input.getAltimeterSettingQNH());
        this.surfaceWind = new ObservedSurfaceWindImpl(input.getSurfaceWind());
        this.visibility = new HorizontalVisibilityImpl(input.getVisibility());
        this.runwayVisualRanges = new ArrayList<RunwayVisualRange>();
        for (RunwayVisualRange range : input.getRunwayVisualRanges()) {
            this.runwayVisualRanges.add(new RunwayVisualRangeImpl(range));
        }
        this.presentWeather = input.getPresentWeather();
        this.clouds = new ObservedCloudsImpl(input.getClouds());
        this.recentWeather = input.getRecentWeather();
        this.windShear = new WindShearImpl(input.getWindShear());
        this.seaState = new SeaStateImpl(input.getSeaState());
        this.runwayStates = new ArrayList<RunwayState>();
        for (RunwayState state : input.getRunwayStates()) {
            this.runwayStates.add(new RunwayStateImpl(state));
        }
        this.trends = new ArrayList<TrendForecast>();
        for (TrendForecast trend : input.getTrends()) {
            this.trends.add(new TrendForecastImpl(trend));
        }
        this.remarks = input.getRemarks();
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#isAutomatedStation()
     */
    @Override
    public boolean isAutomatedStation() {
        return automatedStation;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#setAutomatedStation(boolean)
     */
    @Override
    public void setAutomatedStation(final boolean automatedStation) {
        this.automatedStation = automatedStation;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#getStatus()
     */
    @Override
    public MetarStatus getStatus() {
        return status;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#setStatus(fi.fmi.avi.data.AviationCodeListUser.MetarStatus)
     */
    @Override
    public void setStatus(final MetarStatus status) {
        this.status = status;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#getIssueDayOfMonth()
     */
    @Override
    public int getIssueDayOfMonth() {
        return dayOfMonth;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#setIssueDayOfMonth(int)
     */
    @Override
    public void setIssueDayOfMonth(final int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#getIssueHour()
     */
    @Override
    public int getIssueHour() {
        return hour;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#setIssueHour(int)
     */
    @Override
    public void setIssueHour(final int hour) {
        this.hour = hour;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#getIssueMinute()
     */
    @Override
    public int getIssueMinute() {
        return minute;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#setIssueMinute(int)
     */
    @Override
    public void setIssueMinute(final int minute) {
        this.minute = minute;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#getTimeZone()
     */
    @Override
    public String getIssueTimeZone() {
        return timeZone;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#setTimeZone(java.lang.String)
     */
    @Override
    public void setIssueTimeZone(final String timeZone) {
        this.timeZone = timeZone;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#getAerodromeDesignator()
     */
    @Override
    public String getAerodromeDesignator() {
        return aerodromeDesignator;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#setAerodromeDesignator(java.lang.String)
     */
    @Override
    public void setAerodromeDesignator(final String aerodromeDesignator) {
        this.aerodromeDesignator = aerodromeDesignator;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#isCeilingAndVisibilityOk()
     */
    @Override
    public boolean isCeilingAndVisibilityOk() {
        return ceilingAndVisibilityOk;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#setCeilingAndVisibilityOk(boolean)
     */
    @Override
    public void setCeilingAndVisibilityOk(final boolean ceilingAndVisibilityOk) {
        this.ceilingAndVisibilityOk = ceilingAndVisibilityOk;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#getAirTemperature()
     */
    @Override
    public NumericMeasure getAirTemperature() {
        return airTemperature;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#setAirTemperature(fi.fmi.avi.data.NumericMeasureImpl)
     */
    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public void setAirTemperature(final NumericMeasure airTemperature) {
        this.airTemperature = airTemperature;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#getDewpointTemperature()
     */
    @Override
    public NumericMeasure getDewpointTemperature() {
        return dewpointTemperature;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#setDewpointTemperature(fi.fmi.avi.data.NumericMeasureImpl)
     */
    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public void setDewpointTemperature(final NumericMeasure dewpointTemperature) {
        this.dewpointTemperature = dewpointTemperature;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#getAltimeterSettingQNH()
     */
    @Override
    public NumericMeasure getAltimeterSettingQNH() {
        return altimeterSettingQNH;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#setAltimeterSettingQNH(fi.fmi.avi.data.NumericMeasureImpl)
     */
    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public void setAltimeterSettingQNH(final NumericMeasure altimeterSettingQNH) {
        this.altimeterSettingQNH = altimeterSettingQNH;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#getSurfaceWind()
     */
    @Override
    public ObservedSurfaceWind getSurfaceWind() {
        return surfaceWind;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#setSurfaceWind(fi.fmi.avi.data.ObservedSurfaceWindImpl)
     */
    @Override
    @JsonDeserialize(as = ObservedSurfaceWindImpl.class)
    public void setSurfaceWind(final ObservedSurfaceWind surfaceWind) {
        this.surfaceWind = surfaceWind;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#getVisibility()
     */
    @Override
    public HorizontalVisibility getVisibility() {
        return visibility;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#setVisibility(fi.fmi.avi.data.HorizontalVisibilityImpl)
     */
    @Override
    @JsonDeserialize(as = HorizontalVisibilityImpl.class)
    public void setVisibility(final HorizontalVisibility visibility) {
        this.visibility = visibility;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#getRunwayVisualRanges()
     */
    @Override
    public List<RunwayVisualRange> getRunwayVisualRanges() {
        return runwayVisualRanges;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#setRunwayVisualRanges(java.util.List)
     */
    @Override
    @JsonDeserialize(contentAs = RunwayVisualRangeImpl.class)
    public void setRunwayVisualRanges(final List<RunwayVisualRange> runwayVisualRange) {
        this.runwayVisualRanges = runwayVisualRange;
    }
    
    @Override
    public List<Weather> getPresentWeather() {
    	return this.presentWeather;
    }
    
    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#getPresentWeatherCodes()
     */
    @Override
    @JsonIgnore
    public List<String> getPresentWeatherCodes() {
    	if (this.presentWeather != null) {
    		List<String> retval = new ArrayList<>(this.presentWeather.size());
    		StringBuilder sb;
    		for (Weather w:this.presentWeather) {
    			sb = new StringBuilder();
    			if (w.getIntensity() != null) {
    				sb.append(w.getIntensity().getCode());
    			}
    			if (w.isInVicinity()) {
    				sb.append("VI");
    			}
    			sb.append(w.getKind().getCode());
    			retval.add(sb.toString());
    		}
    		return retval;
    	}
        return null;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#setPresentWeatherCodes(java.util.List)
     */
    @Override
    @JsonDeserialize(contentAs = WeatherImpl.class)
    public void setPresentWeather(final List<Weather> presentWeather) {
        this.presentWeather = presentWeather;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#getClouds()
     */
    @Override
    public ObservedClouds getClouds() {
        return clouds;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#setClouds(fi.fmi.avi.data.ObservedCloudsImpl)
     */
    @Override
    @JsonDeserialize(as = ObservedCloudsImpl.class)
    public void setClouds(final ObservedClouds clouds) {
        this.clouds = clouds;
    }

    public List<Weather> getRecentWeather() {
    	return this.recentWeather;
    }
    
    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#getRecentWeatherCodes()
     */
    @Override
    @JsonIgnore
    public List<String> getRecentWeatherCodes() {
    	if (this.recentWeather != null) {
    		List<String> retval = new ArrayList<>(this.recentWeather.size());
    		StringBuilder sb;
    		for (Weather w:this.recentWeather) {
    			sb = new StringBuilder();
    			sb.append("RE");
    			if (w.getIntensity() != null) {
    				sb.append(w.getIntensity().getCode());
    			}
    			if (w.isInVicinity()) {
    				sb.append("VI");
    			}
    			sb.append(w.getKind().getCode());
    			retval.add(sb.toString());
    		}
    		return retval;
    	}
        return null;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#setRecentWeatherCodes(java.util.List)
     */
    @Override
    @JsonDeserialize(contentAs = WeatherImpl.class)
    public void setRecentWeather(final List<Weather> recentWeather) {
        this.recentWeather = recentWeather;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#getWindShear()
     */
    @Override
    public WindShear getWindShear() {
        return windShear;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#setWindShear(fi.fmi.avi.data.WindShearImpl)
     */
    @Override
    @JsonDeserialize(as = WindShearImpl.class)
    public void setWindShear(final WindShear windShear) {
        this.windShear = windShear;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#getSeaState()
     */
    @Override
    public SeaState getSeaState() {
        return seaState;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#setSeaState(fi.fmi.avi.data.SeaStateImpl)
     */
    @Override
    @JsonDeserialize(as = SeaStateImpl.class)
    public void setSeaState(final SeaState seaState) {
        this.seaState = seaState;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#getRunwayStates()
     */
    @Override
    public List<RunwayState> getRunwayStates() {
        return runwayStates;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#setRunwayStates(java.util.List)
     */
    @Override
    @JsonDeserialize(contentAs = RunwayStateImpl.class)
    public void setRunwayStates(final List<RunwayState> runwayStates) {
        this.runwayStates = runwayStates;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#getTrends()
     */
    @Override
    public List<TrendForecast> getTrends() {
        return trends;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#setTrends(java.util.List)
     */
    @Override
    @JsonDeserialize(contentAs = TrendForecastImpl.class)
    public void setTrends(final List<TrendForecast> trends) {
        this.trends = trends;
    }

    @Override
    public List<String> getRemarks() {
        return this.remarks;
    }

    @Override
    public void setRemarks(final List<String> remarks) {
        this.remarks = remarks;
    }
}
