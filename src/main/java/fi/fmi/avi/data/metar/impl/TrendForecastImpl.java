package fi.fmi.avi.data.metar.impl;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.data.CloudForecast;
import fi.fmi.avi.data.NumericMeasure;
import fi.fmi.avi.data.Weather;
import fi.fmi.avi.data.impl.CloudForecastImpl;
import fi.fmi.avi.data.impl.NumericMeasureImpl;
import fi.fmi.avi.data.impl.WeatherCodeProcessor;
import fi.fmi.avi.data.impl.WeatherImpl;
import fi.fmi.avi.data.metar.TrendForecast;
import fi.fmi.avi.data.metar.TrendForecastSurfaceWind;
import fi.fmi.avi.data.metar.TrendTimeGroups;

/**
 * 
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class TrendForecastImpl extends WeatherCodeProcessor implements TrendForecast {

    private TrendTimeGroups timeGroups;
    private boolean ceilingAndVisibilityOk;
    private boolean noSignificantWeather;
    private boolean noSignificantCloud;
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
        this.timeGroups = input.getTimeGroups();
        this.ceilingAndVisibilityOk = input.isCeilingAndVisibilityOk();
        this.changeIndicator = input.getChangeIndicator();
        this.prevailingVisibility = new NumericMeasureImpl(input.getPrevailingVisibility());
        this.prevailingVisibilityOperator = input.getPrevailingVisibilityOperator();
        this.surfaceWind = new TrendForecastSurfaceWindImpl(input.getSurfaceWind());
        this.forecastWeather = new ArrayList<>(input.getForecastWeather());
        this.noSignificantWeather = input.isNoSignificantWeather();
        this.noSignificantCloud = input.isNoSignificantCloud();
        this.cloud = new CloudForecastImpl(input.getCloud());
        this.colorState = input.getColorState();
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.TrendForecast#getTimeGroups()
     */
    @Override
    public TrendTimeGroups getTimeGroups() {
        return this.timeGroups;
    }

    /* (non-Javadoc)
         * @see fi.fmi.avi.data.TrendForecast#isCeilingAndVisibilityOk()
         */
    @Override
    public boolean isCeilingAndVisibilityOk() {
        return ceilingAndVisibilityOk;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.TrendForecast#getChangeIndicator()
     */
    @Override
    public TrendForecastChangeIndicator getChangeIndicator() {
        return changeIndicator;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.TrendForecast#getPrevailingVisibility()
     */
    @Override
    public NumericMeasure getPrevailingVisibility() {
        return prevailingVisibility;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.TrendForecast#getPrevailingVisibilityOperator()
     */
    @Override
    public RelationalOperator getPrevailingVisibilityOperator() {
        return prevailingVisibilityOperator;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.TrendForecast#getSurfaceWind()
     */
    @Override
    public TrendForecastSurfaceWind getSurfaceWind() {
        return surfaceWind;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.TrendForecast#getForecastWeather()
     */
    @Override
    public List<Weather> getForecastWeather() {
        return forecastWeather;
    }

    @Override
    public List<String> getForecastWeatherCodes() {
        return getAsWeatherCodes(this.forecastWeather);
    }

    @Override
    public boolean isNoSignificantWeather() {
        return this.noSignificantWeather;
    }


    /* (non-Javadoc)
         * @see fi.fmi.avi.data.TrendForecast#getCloud()
         */
    @Override
    public CloudForecast getCloud() {
        return cloud;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.TrendForecast#setTimeGroups(fi.fmi.avi.data.TrendForecastImpl.TimeGroups)
     */

    @Override
    public boolean isNoSignificantCloud() {
        return this.noSignificantCloud;
    }
    
    @Override
    @JsonDeserialize(as = TrendTimeGroupsImpl.class)
    public void setTimeGroups(final TrendTimeGroups timeGroups) {
        this.timeGroups = timeGroups;
    }
    

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.TrendForecast#setCeilingAndVisibilityOk(boolean)
     */
    @Override
    public void setCeilingAndVisibilityOk(final boolean ceilingAndVisibilityOk) {
        this.ceilingAndVisibilityOk = ceilingAndVisibilityOk;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.TrendForecast#setChangeIndicator(fi.fmi.avi.data.AviationCodeListUser.ForecastChangeIndicator)
     */
    @Override
    public void setChangeIndicator(final TrendForecastChangeIndicator changeIndicator) {
        this.changeIndicator = changeIndicator;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.TrendForecast#setPrevailingVisibility(fi.fmi.avi.data.NumericMeasure)
     */
    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public void setPrevailingVisibility(final NumericMeasure prevailingVisibility) {
        this.prevailingVisibility = prevailingVisibility;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.TrendForecast#setPrevailingVisibilityOperator(fi.fmi.avi.data.AviationCodeListUser.RelationalOperator)
     */
    @Override
    public void setPrevailingVisibilityOperator(final RelationalOperator prevailingVisibilityOperator) {
        this.prevailingVisibilityOperator = prevailingVisibilityOperator;
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
     * @see fi.fmi.avi.data.TrendForecast#setForecastWeather(java.util.List)
     */
    @Override
    @JsonDeserialize(contentAs = WeatherImpl.class)
    public void setForecastWeather(final List<Weather> forecastWeather) {
        this.forecastWeather = forecastWeather;
    }

    @Override
    public void setNoSignificantWeather(final boolean nsw) {
        this.noSignificantWeather = nsw;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.TrendForecast#setCloud(fi.fmi.avi.data.CloudForecast)
     */
    @Override
    @JsonDeserialize(as = CloudForecastImpl.class)
    public void setCloud(final CloudForecast cloud) {
        this.cloud = cloud;
    }

    public void setNoSignificantCloud(final boolean nsc) {
        this.noSignificantCloud = nsc;
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
