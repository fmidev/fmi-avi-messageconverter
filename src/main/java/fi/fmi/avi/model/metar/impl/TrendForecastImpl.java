package fi.fmi.avi.model.metar.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.CloudForecast;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.Weather;
import fi.fmi.avi.model.impl.AviationWeatherMessageImpl;
import fi.fmi.avi.model.impl.CloudForecastImpl;
import fi.fmi.avi.model.impl.NumericMeasureImpl;
import fi.fmi.avi.model.impl.WeatherImpl;
import fi.fmi.avi.model.metar.TrendForecast;
import fi.fmi.avi.model.metar.TrendForecastSurfaceWind;
import fi.fmi.avi.model.metar.TrendTimeGroups;

/**
 *
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class TrendForecastImpl implements TrendForecast, Serializable {

    private static final long serialVersionUID = 6810184324056939712L;

    private TrendTimeGroups timeGroups;
    private boolean ceilingAndVisibilityOk;
    private boolean noSignificantWeather;
    private TrendForecastChangeIndicator changeIndicator;
    private NumericMeasure prevailingVisibility;
    private RelationalOperator prevailingVisibilityOperator;
    private TrendForecastSurfaceWind surfaceWind;
    private List<Weather> forecastWeather;
    private CloudForecast cloud;
    private ColorState colorState;

    public TrendForecastImpl() {
    }

    public TrendForecastImpl(final TrendForecast input) {
        if (input != null) {
            this.timeGroups = input.getTimeGroups();
            this.ceilingAndVisibilityOk = input.isCeilingAndVisibilityOk();
            this.changeIndicator = input.getChangeIndicator();
            if (input.getPrevailingVisibility() != null) {
                this.prevailingVisibility = new NumericMeasureImpl(input.getPrevailingVisibility());
            }
            this.prevailingVisibilityOperator = input.getPrevailingVisibilityOperator();
            if (input.getSurfaceWind() != null) {
                this.surfaceWind = new TrendForecastSurfaceWindImpl(input.getSurfaceWind());
            }
            if (input.getForecastWeather() != null) {
                this.forecastWeather = new ArrayList<>();
                for (final Weather w : input.getForecastWeather()) {
                    this.forecastWeather.add(new WeatherImpl(w));
                }
            }
            this.noSignificantWeather = input.isNoSignificantWeather();
            if (input.getCloud() != null) {
                this.cloud = new CloudForecastImpl(input.getCloud());
            }
            this.colorState = input.getColorState();
        }
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.TrendForecast#getTimeGroups()
     */
    @Override
    public TrendTimeGroups getTimeGroups() {
        return this.timeGroups;
    }

    @Override
    @JsonDeserialize(as = TrendTimeGroupsImpl.class)
    public void setTimeGroups(final TrendTimeGroups timeGroups) {
        this.timeGroups = timeGroups;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.TrendForecast#isCeilingAndVisibilityOk()
     */
    @Override
    public boolean isCeilingAndVisibilityOk() {
        return ceilingAndVisibilityOk;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.TrendForecast#setCeilingAndVisibilityOk(boolean)
     */
    @Override
    public void setCeilingAndVisibilityOk(final boolean ceilingAndVisibilityOk) {
        this.ceilingAndVisibilityOk = ceilingAndVisibilityOk;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.TrendForecast#getChangeIndicator()
     */
    @Override
    public TrendForecastChangeIndicator getChangeIndicator() {
        return changeIndicator;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.TrendForecast#setChangeIndicator(fi.fmi.avi.model.AviationCodeListUser.ForecastChangeIndicator)
     */
    @Override
    public void setChangeIndicator(final TrendForecastChangeIndicator changeIndicator) {
        this.changeIndicator = changeIndicator;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.TrendForecast#getPrevailingVisibility()
     */
    @Override
    public NumericMeasure getPrevailingVisibility() {
        return prevailingVisibility;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.TrendForecast#setPrevailingVisibility(fi.fmi.avi.model.NumericMeasure)
     */
    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public void setPrevailingVisibility(final NumericMeasure prevailingVisibility) {
        this.prevailingVisibility = prevailingVisibility;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.TrendForecast#getPrevailingVisibilityOperator()
     */
    @Override
    public RelationalOperator getPrevailingVisibilityOperator() {
        return prevailingVisibilityOperator;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.TrendForecast#setPrevailingVisibilityOperator(fi.fmi.avi.model.AviationCodeListUser.RelationalOperator)
     */
    @Override
    public void setPrevailingVisibilityOperator(final RelationalOperator prevailingVisibilityOperator) {
        this.prevailingVisibilityOperator = prevailingVisibilityOperator;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.TrendForecast#setTimeGroups(fi.fmi.avi.data.TrendForecastImpl.TimeGroups)
     */

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.TrendForecast#getSurfaceWind()
     */
    @Override
    public TrendForecastSurfaceWind getSurfaceWind() {
        return surfaceWind;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.TrendForecast#setSurfaceWind(fi.fmi.avi.data.TrendForecastSurfaceWindImpl)
     */
    @Override
    @JsonDeserialize(as = TrendForecastSurfaceWindImpl.class)
    public void setSurfaceWind(final TrendForecastSurfaceWind surfaceWind) {
        this.surfaceWind = surfaceWind;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.TrendForecast#getForecastWeather()
     */
    @Override
    public List<Weather> getForecastWeather() {
        return forecastWeather;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.TrendForecast#setForecastWeather(java.util.List)
     */
    @Override
    @JsonDeserialize(contentAs = WeatherImpl.class)
    public void setForecastWeather(final List<Weather> forecastWeather) {
        this.forecastWeather = forecastWeather;
    }

    @Override
    public List<String> getForecastWeatherCodes() {
        return AviationWeatherMessageImpl.getAsWeatherCodes(this.forecastWeather);
    }

    @Override
    public boolean isNoSignificantWeather() {
        return this.noSignificantWeather;
    }

    @Override
    public void setNoSignificantWeather(final boolean nsw) {
        this.noSignificantWeather = nsw;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.TrendForecast#getCloud()
     */
    @Override
    public CloudForecast getCloud() {
        return cloud;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.TrendForecast#setCloud(fi.fmi.avi.model.CloudForecast)
     */
    @Override
    @JsonDeserialize(as = CloudForecastImpl.class)
    public void setCloud(final CloudForecast cloud) {
        this.cloud = cloud;
    }

    @Override
    public ColorState getColorState() {
        return this.colorState;
    }

    @Override
    public void setColorState(final ColorState colorState) {
        this.colorState = colorState;
    }

}
