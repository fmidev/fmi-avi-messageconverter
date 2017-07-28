package fi.fmi.avi.model.metar.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.impl.NumericMeasureImpl;
import fi.fmi.avi.model.metar.TrendForecastSurfaceWind;

/**
 *
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class TrendForecastSurfaceWindImpl implements TrendForecastSurfaceWind {

    private NumericMeasure meanWindDirection;
    private NumericMeasure meanWindSpeed;
    private NumericMeasure windGust;

    public TrendForecastSurfaceWindImpl() {
    }

    public TrendForecastSurfaceWindImpl(final TrendForecastSurfaceWind input) {
        this.meanWindDirection = new NumericMeasureImpl(input.getMeanWindDirection());
        this.meanWindSpeed = new NumericMeasureImpl(input.getMeanWindSpeed());
        this.windGust = new NumericMeasureImpl(input.getWindGust());
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.TrendForecastSurfaceWind#getMeanWindDirection()
     */
    @Override
    public NumericMeasure getMeanWindDirection() {
        return meanWindDirection;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.TrendForecastSurfaceWind#getMeanWindSpeed()
     */
    @Override
    public NumericMeasure getMeanWindSpeed() {
        return meanWindSpeed;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.TrendForecastSurfaceWind#getWindGust()
     */
    @Override
    public NumericMeasure getWindGust() {
        return windGust;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.TrendForecastSurfaceWind#setMeanWindDirection(fi.fmi.avi.model.NumericMeasure)
     */
    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public void setMeanWindDirection(final NumericMeasure meanWindDirection) {
        this.meanWindDirection = meanWindDirection;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.TrendForecastSurfaceWind#setMeanWindSpeed(fi.fmi.avi.model.NumericMeasure)
     */
    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public void setMeanWindSpeed(final NumericMeasure meanWindSpeed) {
        this.meanWindSpeed = meanWindSpeed;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.TrendForecastSurfaceWind#setWindGust(fi.fmi.avi.model.NumericMeasure)
     */
    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public void setWindGust(final NumericMeasure windGust) {
        this.windGust = windGust;
    }

}
