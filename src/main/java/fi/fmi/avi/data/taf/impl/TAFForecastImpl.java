package fi.fmi.avi.data.taf.impl;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.data.CloudForecast;
import fi.fmi.avi.data.NumericMeasure;
import fi.fmi.avi.data.Weather;
import fi.fmi.avi.data.impl.AviationWeatherMessageImpl;
import fi.fmi.avi.data.impl.CloudForecastImpl;
import fi.fmi.avi.data.impl.NumericMeasureImpl;
import fi.fmi.avi.data.impl.WeatherImpl;
import fi.fmi.avi.data.taf.TAFForecast;
import fi.fmi.avi.data.taf.TAFSurfaceWind;

/**
 * Created by rinne on 30/01/15.
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class TAFForecastImpl implements TAFForecast {
	
    private boolean ceilingAndVisibilityOk;
    private boolean noSignificantWeather;
    private boolean noSignificantCloud;
    private NumericMeasure prevailingVisibility;
    private RelationalOperator prevailingVisibilityOperator;
    private TAFSurfaceWind surfaceWind;
    private List<Weather> forecastWeather;
    private CloudForecast cloud;

    public TAFForecastImpl(){
    }

    public TAFForecastImpl(final TAFForecast input) {
        this.ceilingAndVisibilityOk = input.isCeilingAndVisibilityOk();
        this.prevailingVisibility = new NumericMeasureImpl(input.getPrevailingVisibility());
        this.prevailingVisibilityOperator = input.getPrevailingVisibilityOperator();
        this.surfaceWind = new TAFSurfaceWindImpl(input.getSurfaceWind());
        this.forecastWeather = new ArrayList<>(input.getForecastWeather());
        this.noSignificantWeather = input.isNoSignificantWeather();
        this.noSignificantCloud = input.isNoSignificantCloud();
        this.cloud = new CloudForecastImpl(input.getCloud());
    }

    @Override
    public boolean isCeilingAndVisibilityOk() {
        return ceilingAndVisibilityOk;
    }

    @Override
    public void setCeilingAndVisibilityOk(final boolean ceilingAndVisibilityOk) {
        this.ceilingAndVisibilityOk = ceilingAndVisibilityOk;
    }

    @Override
    public NumericMeasure getPrevailingVisibility() {
        return prevailingVisibility;
    }

    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public void setPrevailingVisibility(final NumericMeasure prevailingVisibility) {
        this.prevailingVisibility = prevailingVisibility;
    }

    @Override
    public RelationalOperator getPrevailingVisibilityOperator() {
        return prevailingVisibilityOperator;
    }

    @Override
    public void setPrevailingVisibilityOperator(final RelationalOperator prevailingVisibilityOperator) {
        this.prevailingVisibilityOperator = prevailingVisibilityOperator;
    }

    @Override
    public TAFSurfaceWind getSurfaceWind() {
        return surfaceWind;
    }

    @Override
    @JsonDeserialize(as = TAFSurfaceWindImpl.class)
    public void setSurfaceWind(final TAFSurfaceWind surfaceWind) {
        this.surfaceWind = surfaceWind;
    }

    @Override
    public List<Weather> getForecastWeather() {
        return forecastWeather;
    }

    public List<String> getForecastWeatherCodes() {
        return AviationWeatherMessageImpl.getAsWeatherCodes(this.forecastWeather);
    }

    @Override
    @JsonDeserialize(contentAs = WeatherImpl.class)
    public void setForecastWeather(final List<Weather> forecastWeather) {
        this.forecastWeather = forecastWeather;
    }

    @Override
    public boolean isNoSignificantWeather() {
        return this.noSignificantWeather;
    }

    @Override
    public void setNoSignificantWeather(final boolean nsw) {
        this.noSignificantWeather = nsw;
    }

    @Override
    public CloudForecast getCloud() {
        return cloud;
    }

    @Override
    @JsonDeserialize(as = CloudForecastImpl.class)
    public void setCloud(final CloudForecast cloud) {
        this.cloud = cloud;
    }

    @Override
    public boolean isNoSignificantCloud() {
        return this.noSignificantCloud;
    }

    public void setNoSignificantCloud(final boolean nsc) {
        this.noSignificantCloud = nsc;
    }
}
