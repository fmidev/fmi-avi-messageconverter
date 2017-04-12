package fi.fmi.avi.data.taf.impl;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.data.CloudForecast;
import fi.fmi.avi.data.NumericMeasure;
import fi.fmi.avi.data.impl.CloudForecastImpl;
import fi.fmi.avi.data.impl.NumericMeasureImpl;
import fi.fmi.avi.data.taf.TAFForecast;
import fi.fmi.avi.data.taf.TAFSurfaceWind;

/**
 * Created by rinne on 30/01/15.
 */
public abstract class TAFForecastImpl implements TAFForecast {

    private boolean ceilingAndVisibilityOk;
    private NumericMeasure prevailingVisibility;
    private RelationalOperator prevailingVisibilityOperator;
    private TAFSurfaceWind surfaceWind;
    private List<String> forecastWeather;
    private CloudForecast cloud;


    public TAFForecastImpl(){
    }

    public TAFForecastImpl(final TAFForecast input) {
        this.ceilingAndVisibilityOk = input.isCeilingAndVisibilityOk();
        this.prevailingVisibility = new NumericMeasureImpl(input.getPrevailingVisibility());
        this.prevailingVisibilityOperator = input.getPrevailingVisibilityOperator();
        this.surfaceWind = new TAFSurfaceWindImpl(input.getSurfaceWind());
        this.forecastWeather = new ArrayList<String>(input.getForecastWeather());
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
    public List<String> getForecastWeather() {
        return forecastWeather;
    }

    @Override
    public void setForecastWeather(final List<String> forecastWeather) {
        this.forecastWeather = forecastWeather;
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
}
