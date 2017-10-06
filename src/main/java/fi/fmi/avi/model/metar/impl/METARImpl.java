package fi.fmi.avi.model.metar.impl;

import java.time.ZonedDateTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import fi.fmi.avi.model.Aerodrome;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.RunwayDirection;
import fi.fmi.avi.model.Weather;
import fi.fmi.avi.model.impl.AerodromeWeatherMessageImpl;
import fi.fmi.avi.model.impl.NumericMeasureImpl;
import fi.fmi.avi.model.impl.WeatherImpl;
import fi.fmi.avi.model.metar.HorizontalVisibility;
import fi.fmi.avi.model.metar.METAR;
import fi.fmi.avi.model.metar.ObservedClouds;
import fi.fmi.avi.model.metar.ObservedSurfaceWind;
import fi.fmi.avi.model.metar.RunwayState;
import fi.fmi.avi.model.metar.RunwayVisualRange;
import fi.fmi.avi.model.metar.SeaState;
import fi.fmi.avi.model.metar.TrendForecast;
import fi.fmi.avi.model.metar.WindShear;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class METARImpl extends AerodromeWeatherMessageImpl implements METAR {

    private boolean automatedStation;
    private MetarStatus status;
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
    private ColorState colorState;

    public METARImpl() {
    }

    public METARImpl(final METAR input) {
        super(input);
        this.runwayVisualRanges = new ArrayList<RunwayVisualRange>();
        this.runwayStates = new ArrayList<RunwayState>();
        this.trends = new ArrayList<TrendForecast>();
        if (input != null) {
            this.automatedStation = input.isAutomatedStation();
            this.status = input.getStatus();
            this.ceilingAndVisibilityOk = input.isCeilingAndVisibilityOk();
            this.airTemperature = new NumericMeasureImpl(input.getAirTemperature());
            this.dewpointTemperature = new NumericMeasureImpl(input.getDewpointTemperature());
            this.altimeterSettingQNH = new NumericMeasureImpl(input.getAltimeterSettingQNH());
            this.surfaceWind = new ObservedSurfaceWindImpl(input.getSurfaceWind());
            this.visibility = new HorizontalVisibilityImpl(input.getVisibility());

            for (RunwayVisualRange range : input.getRunwayVisualRanges()) {
                this.runwayVisualRanges.add(new RunwayVisualRangeImpl(range));
            }
            this.presentWeather = input.getPresentWeather();
            this.clouds = new ObservedCloudsImpl(input.getClouds());
            this.recentWeather = input.getRecentWeather();
            this.windShear = new WindShearImpl(input.getWindShear());
            this.seaState = new SeaStateImpl(input.getSeaState());
            for (RunwayState state : input.getRunwayStates()) {
                this.runwayStates.add(new RunwayStateImpl(state));
            }
            for (TrendForecast trend : input.getTrends()) {
                this.trends.add(new TrendForecastImpl(trend));
            }
            this.colorState = input.getColorState();
        }
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.METAR#isAutomatedStation()
     */
    @Override
    public boolean isAutomatedStation() {
        return automatedStation;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.METAR#setAutomatedStation(boolean)
     */
    @Override
    public void setAutomatedStation(final boolean automatedStation) {
        this.automatedStation = automatedStation;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.METAR#getStatus()
     */
    @Override
    public MetarStatus getStatus() {
        return status;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.METAR#setStatus(fi.fmi.avi.model.AviationCodeListUser.MetarStatus)
     */
    @Override
    public void setStatus(final MetarStatus status) {
        this.status = status;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.METAR#isCeilingAndVisibilityOk()
     */
    @Override
    public boolean isCeilingAndVisibilityOk() {
        return ceilingAndVisibilityOk;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.METAR#setCeilingAndVisibilityOk(boolean)
     */
    @Override
    public void setCeilingAndVisibilityOk(final boolean ceilingAndVisibilityOk) {
        this.ceilingAndVisibilityOk = ceilingAndVisibilityOk;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.METAR#getAirTemperature()
     */
    @Override
    public NumericMeasure getAirTemperature() {
        return airTemperature;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.METAR#setAirTemperature(fi.fmi.avi.data.NumericMeasureImpl)
     */
    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public void setAirTemperature(final NumericMeasure airTemperature) {
        this.airTemperature = airTemperature;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.METAR#getDewpointTemperature()
     */
    @Override
    public NumericMeasure getDewpointTemperature() {
        return dewpointTemperature;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.METAR#setDewpointTemperature(fi.fmi.avi.data.NumericMeasureImpl)
     */
    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public void setDewpointTemperature(final NumericMeasure dewpointTemperature) {
        this.dewpointTemperature = dewpointTemperature;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.METAR#getAltimeterSettingQNH()
     */
    @Override
    public NumericMeasure getAltimeterSettingQNH() {
        return altimeterSettingQNH;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.METAR#setAltimeterSettingQNH(fi.fmi.avi.data.NumericMeasureImpl)
     */
    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public void setAltimeterSettingQNH(final NumericMeasure altimeterSettingQNH) {
        this.altimeterSettingQNH = altimeterSettingQNH;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.METAR#getSurfaceWind()
     */
    @Override
    public ObservedSurfaceWind getSurfaceWind() {
        return surfaceWind;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.METAR#setSurfaceWind(fi.fmi.avi.data.ObservedSurfaceWindImpl)
     */
    @Override
    @JsonDeserialize(as = ObservedSurfaceWindImpl.class)
    public void setSurfaceWind(final ObservedSurfaceWind surfaceWind) {
        this.surfaceWind = surfaceWind;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.METAR#getVisibility()
     */
    @Override
    public HorizontalVisibility getVisibility() {
        return visibility;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.METAR#setVisibility(fi.fmi.avi.data.HorizontalVisibilityImpl)
     */
    @Override
    @JsonDeserialize(as = HorizontalVisibilityImpl.class)
    public void setVisibility(final HorizontalVisibility visibility) {
        this.visibility = visibility;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.METAR#getRunwayVisualRanges()
     */
    @Override
    public List<RunwayVisualRange> getRunwayVisualRanges() {
        return runwayVisualRanges;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.METAR#setRunwayVisualRanges(java.util.List)
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
     * @see fi.fmi.avi.data.METAR#getPresentWeatherCodes()
     */
    @Override
    @JsonIgnore
    public List<String> getPresentWeatherCodes() {
        return getAsWeatherCodes(this.presentWeather);
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.METAR#setPresentWeatherCodes(java.util.List)
     */
    @Override
    @JsonDeserialize(contentAs = WeatherImpl.class)
    public void setPresentWeather(final List<Weather> presentWeather) {
        this.presentWeather = presentWeather;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.METAR#getClouds()
     */
    @Override
    public ObservedClouds getClouds() {
        return clouds;
    }
    
    /* (non-Javadoc)
         * @see fi.fmi.avi.data.METAR#setClouds(fi.fmi.avi.data.ObservedCloudsImpl)
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
     * @see fi.fmi.avi.data.METAR#getRecentWeatherCodes()
     */
    @Override
    @JsonIgnore
    public List<String> getRecentWeatherCodes() {
        return getAsWeatherCodes(this.recentWeather, "RE");
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.METAR#setRecentWeatherCodes(java.util.List)
     */
    @Override
    @JsonDeserialize(contentAs = WeatherImpl.class)
    public void setRecentWeather(final List<Weather> recentWeather) {
        this.recentWeather = recentWeather;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.METAR#getWindShear()
     */
    @Override
    public WindShear getWindShear() {
        return windShear;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.METAR#setWindShear(fi.fmi.avi.data.WindShearImpl)
     */
    @Override
    @JsonDeserialize(as = WindShearImpl.class)
    public void setWindShear(final WindShear windShear) {
        this.windShear = windShear;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.METAR#getSeaState()
     */
    @Override
    public SeaState getSeaState() {
        return seaState;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.METAR#setSeaState(fi.fmi.avi.data.SeaStateImpl)
     */
    @Override
    @JsonDeserialize(as = SeaStateImpl.class)
    public void setSeaState(final SeaState seaState) {
        this.seaState = seaState;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.METAR#getRunwayStates()
     */
    @Override
    public List<RunwayState> getRunwayStates() {
        return runwayStates;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.METAR#setRunwayStates(java.util.List)
     */
    @Override
    @JsonDeserialize(contentAs = RunwayStateImpl.class)
    public void setRunwayStates(final List<RunwayState> runwayStates) {
        this.runwayStates = runwayStates;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.METAR#getTrends()
     */
    @Override
    public List<TrendForecast> getTrends() {
        return trends;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.METAR#setTrends(java.util.List)
     */
    @Override
    @JsonDeserialize(contentAs = TrendForecastImpl.class)
    public void setTrends(final List<TrendForecast> trends) {
        this.trends = trends;
    }


    @Override
    public ColorState getColorState() {
        return this.colorState;
    }

    @Override
    public void setColorState(final ColorState colorState) {
        this.colorState = colorState;
    }

	@Override
	@JsonIgnore
	public Set<String> getUnresolvedRunwayDirectionDesignators() {
		Set<String> retval = new HashSet<>();
		if (this.runwayStates != null) {
			for (RunwayState rws:this.runwayStates) {
				if (rws.getRunwayDirection() != null && !rws.getRunwayDirection().isResolved()) {
					retval.add(rws.getRunwayDirection().getDesignator());
				}
			}
		}
		if (this.runwayVisualRanges != null) {
			for (RunwayVisualRange rvr:this.runwayVisualRanges) {
				if (rvr.getRunwayDirection() != null && !rvr.getRunwayDirection().isResolved()) {
					retval.add(rvr.getRunwayDirection().getDesignator());
				}
			}
		}
		if (this.windShear != null) {
			for (RunwayDirection rwd:this.windShear.getRunwayDirections()) {
				if (!rwd.isResolved()){
					retval.add(rwd.getDesignator());
				}
			}
		}
		return retval;
	}

	@Override
	public void amendRunwayDirectionInfo(RunwayDirection fullInfo) {
		if (this.getAerodrome() == null) {
			throw new IllegalStateException("Set target aerodrome before amending runway direction info");
		}
		if (fullInfo.getAssociatedAirportHeliport() != null && this.getAerodrome().getDesignator().equals(fullInfo.getAssociatedAirportHeliport().getDesignator())) {
			if (this.runwayStates != null) {
				for (RunwayState rws:this.runwayStates) {
					if (rws.getRunwayDirection() != null && !rws.getRunwayDirection().getDesignator().equals(fullInfo.getDesignator())) {
						rws.setRunwayDirection(fullInfo);
					}
				}
			}
			if (this.runwayVisualRanges != null) {
				for (RunwayVisualRange rvr:this.runwayVisualRanges) {
					if (rvr.getRunwayDirection() != null && !rvr.getRunwayDirection().getDesignator().equals(fullInfo.getDesignator())) {
						rvr.setRunwayDirection(fullInfo);
					}
				}
			}
			if (this.windShear != null) {
				List<RunwayDirection> amendedList = new ArrayList<>();
				for (RunwayDirection rwd:this.windShear.getRunwayDirections()) {
					if (rwd.getDesignator().equals(fullInfo.getDesignator())){
						amendedList.add(fullInfo);
					} else {
						amendedList.add(rwd);
					}
				}
				this.windShear.setRunwayDirections(amendedList);
			}
		}
	}
	
	@Override
	public void amendTimeReferences(final ZonedDateTime referenceTime) {
		super.amendTimeReferences(referenceTime);
		if (this.trends != null) {
			for (TrendForecast fct:this.trends) {
				if (!fct.areTimeReferencesResolved()) {
					fct.amendTimeReferences(this.getIssueTime());
				}
			}
		}
	}

	@Override
	public boolean areTimeReferencesResolved() {
		boolean retval = super.areTimeReferencesResolved();
		if (retval && this.trends != null) {
			for (TrendForecast fct:this.trends) {
				if (!fct.areTimeReferencesResolved()) {
					retval = false;
					break;
				}
			}
		}
		return retval;
	}
	
	
	protected void syncAerodromeInfo(final Aerodrome fullInfo) {
		if (this.runwayStates != null) {
			for (RunwayState rws:this.runwayStates) {
				if (rws.getRunwayDirection() != null) {
					rws.getRunwayDirection().setAssociatedAirportHeliport(fullInfo);
				}
			}
		}
		if (this.runwayVisualRanges != null) {
			for (RunwayVisualRange rvr:this.runwayVisualRanges) {
				if (rvr.getRunwayDirection() != null) {
					rvr.getRunwayDirection().setAssociatedAirportHeliport(fullInfo);
				}
			}
		}
		if (this.windShear != null) {
			for (RunwayDirection rwd:this.windShear.getRunwayDirections()) {
				rwd.setAssociatedAirportHeliport(fullInfo);
			}
		}
	}	
	
}
