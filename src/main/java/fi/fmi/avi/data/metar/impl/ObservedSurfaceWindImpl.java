package fi.fmi.avi.data.metar.impl;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.data.NumericMeasure;
import fi.fmi.avi.data.impl.NumericMeasureImpl;
import fi.fmi.avi.data.impl.PossiblyMissingContentImpl;
import fi.fmi.avi.data.metar.ObservedSurfaceWind;

/**
 * 
 * @author Ilkka Rinne / Spatineo Inc for the Finnish Meteorological Institute
 * 
 */

public class ObservedSurfaceWindImpl extends PossiblyMissingContentImpl implements ObservedSurfaceWind {

    private boolean variableDirection;
    private NumericMeasure meanWindDirection; // 0-359 degrees
    private NumericMeasure meanWindSpeed;
    private NumericMeasure windGust;
    private NumericMeasure extremeClockwiseWindDirection;
    private NumericMeasure extremeCounterClockwiseWindDirection;

    public ObservedSurfaceWindImpl() {
    }

    public ObservedSurfaceWindImpl(final ObservedSurfaceWind input) {
        super(input.getMissingReason());
        if (MissingReason.NOT_MISSING.equals(this.getMissingReason())) {
            this.variableDirection = input.isVariableDirection();
            this.meanWindDirection = new NumericMeasureImpl(input.getMeanWindDirection());
            this.meanWindSpeed = new NumericMeasureImpl(input.getMeanWindSpeed());
            this.windGust = new NumericMeasureImpl(input.getWindGust());
            this.extremeClockwiseWindDirection = new NumericMeasureImpl(input.getExtremeClockwiseWindDirection());
            this.extremeCounterClockwiseWindDirection = new NumericMeasureImpl(input.getExtremeCounterClockwiseWindDirection());
        }
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.ObservedSurfaceWind#isVariableDirection()
     */
    @Override
    public boolean isVariableDirection() {
        return variableDirection;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.ObservedSurfaceWind#getMeanWindDirection()
     */
    @Override
    public NumericMeasure getMeanWindDirection() {
        return meanWindDirection;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.ObservedSurfaceWind#getMeanWindSpeed()
     */
    @Override
    public NumericMeasure getMeanWindSpeed() {
        return meanWindSpeed;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.ObservedSurfaceWind#getWindGust()
     */
    @Override
    public NumericMeasure getWindGust() {
        return windGust;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.ObservedSurfaceWind#getExtremeClockwiseWindDirection()
     */
    @Override
    public NumericMeasure getExtremeClockwiseWindDirection() {
        return extremeClockwiseWindDirection;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.ObservedSurfaceWind#getExtremeCounterClockwiseWindDirection()
     */
    @Override
    public NumericMeasure getExtremeCounterClockwiseWindDirection() {
        return extremeCounterClockwiseWindDirection;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.ObservedSurfaceWind#setVariableDirection(boolean)
     */
    @Override
    public void setVariableDirection(final boolean variableDirection) {
        this.variableDirection = variableDirection;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.ObservedSurfaceWind#setMeanWindDirection(fi.fmi.avi.data.NumericMeasure)
     */
    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public void setMeanWindDirection(final NumericMeasure meanWindDirection) {
        this.meanWindDirection = meanWindDirection;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.ObservedSurfaceWind#setMeanWindSpeed(fi.fmi.avi.data.NumericMeasure)
     */
    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public void setMeanWindSpeed(final NumericMeasure meanWindSpeed) {
        this.meanWindSpeed = meanWindSpeed;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.ObservedSurfaceWind#setWindGust(fi.fmi.avi.data.NumericMeasure)
     */
    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public void setWindGust(final NumericMeasure windGust) {
        this.windGust = windGust;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.ObservedSurfaceWind#setExtremeClockwiseWindDirection(fi.fmi.avi.data.NumericMeasure)
     */
    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public void setExtremeClockwiseWindDirection(final NumericMeasure extremeClockwiseWindDirection) {
        this.extremeClockwiseWindDirection = extremeClockwiseWindDirection;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.ObservedSurfaceWind#setExtremeCounterClockwiseWindDirection(fi.fmi.avi.data.NumericMeasure)
     */
    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public void setExtremeCounterClockwiseWindDirection(final NumericMeasure extremeCounterClockwiseWindDirection) {
        this.extremeCounterClockwiseWindDirection = extremeCounterClockwiseWindDirection;
    }
}
