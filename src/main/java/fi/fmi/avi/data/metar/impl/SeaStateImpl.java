package fi.fmi.avi.data.metar.impl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.data.NumericMeasure;
import fi.fmi.avi.data.impl.NumericMeasureImpl;
import fi.fmi.avi.data.impl.PossiblyMissingContentImpl;
import fi.fmi.avi.data.metar.SeaState;

/**
 * 
 * @author Ilkka Rinne / Spatineo Inc for the Finnish Meteorological Institute
 * 
 */

public class SeaStateImpl extends PossiblyMissingContentImpl implements SeaState {

    private NumericMeasure seaSurfaceTemperature;
    private NumericMeasure significantWaveHeight;
    private SeaSurfaceState seaSurfaceState;

    public SeaStateImpl() {
    }

    public SeaStateImpl(final SeaState input) {
        super(input.getMissingReason());
        if (MissingReason.NOT_MISSING.equals(this.getMissingReason())) {
            this.seaSurfaceTemperature = new NumericMeasureImpl(input.getSeaSurfaceTemperature());
            this.significantWaveHeight = new NumericMeasureImpl(input.getSignificantWaveHeight());
            this.seaSurfaceState = input.getSeaSurfaceState();
        }
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.SeaState#getSeaSurfaceTemperature()
     */
    @Override
    public NumericMeasure getSeaSurfaceTemperature() {
        return seaSurfaceTemperature;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.SeaState#getSignificantWaveHeight()
     */
    @Override
    public NumericMeasure getSignificantWaveHeight() {
        return significantWaveHeight;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.SeaState#getSeaSurfaceState()
     */
    @Override
    public SeaSurfaceState getSeaSurfaceState() {
        return seaSurfaceState;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.SeaState#setSeaSurfaceTemperature(fi.fmi.avi.data.NumericMeasure)
     */
    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public void setSeaSurfaceTemperature(final NumericMeasure seaSurfaceTemperature) {
        this.seaSurfaceTemperature = seaSurfaceTemperature;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.SeaState#setSignificantWaveHeight(fi.fmi.avi.data.NumericMeasure)
     */
    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public void setSignificantWaveHeight(final NumericMeasure significantWaveHeight) {
        this.significantWaveHeight = significantWaveHeight;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.SeaState#setSeaSurfaceState(fi.fmi.avi.data.AviationCodeListUser.SeaSurfaceState)
     */
    @Override
    public void setSeaSurfaceState(final SeaSurfaceState seaSurfaceState) {
        this.seaSurfaceState = seaSurfaceState;
    }

}
