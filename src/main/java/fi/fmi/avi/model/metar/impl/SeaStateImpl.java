package fi.fmi.avi.model.metar.impl;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.impl.NumericMeasureImpl;
import fi.fmi.avi.model.metar.SeaState;

/**
 * @author Ilkka Rinne / Spatineo Inc for the Finnish Meteorological Institute
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class SeaStateImpl implements SeaState, Serializable {

    private static final long serialVersionUID = 2298444419074688003L;

    private NumericMeasure seaSurfaceTemperature;
    private NumericMeasure significantWaveHeight;
    private SeaSurfaceState seaSurfaceState;

    public SeaStateImpl() {
    }

    public SeaStateImpl(final SeaState input) {
        if (input != null) {
            if (input.getSeaSurfaceTemperature() != null) {
                this.seaSurfaceTemperature = new NumericMeasureImpl(input.getSeaSurfaceTemperature());
            }
            if (input.getSignificantWaveHeight() != null) {
                this.significantWaveHeight = new NumericMeasureImpl(input.getSignificantWaveHeight());
            }
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
     * @see fi.fmi.avi.data.SeaState#setSeaSurfaceTemperature(fi.fmi.avi.model.NumericMeasure)
     */
    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public void setSeaSurfaceTemperature(final NumericMeasure seaSurfaceTemperature) {
        this.seaSurfaceTemperature = seaSurfaceTemperature;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.SeaState#getSignificantWaveHeight()
     */
    @Override
    public NumericMeasure getSignificantWaveHeight() {
        return significantWaveHeight;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.SeaState#setSignificantWaveHeight(fi.fmi.avi.model.NumericMeasure)
     */
    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public void setSignificantWaveHeight(final NumericMeasure significantWaveHeight) {
        this.significantWaveHeight = significantWaveHeight;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.SeaState#getSeaSurfaceState()
     */
    @Override
    public SeaSurfaceState getSeaSurfaceState() {
        return seaSurfaceState;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.SeaState#setSeaSurfaceState(fi.fmi.avi.model.AviationCodeListUser.SeaSurfaceState)
     */
    @Override
    public void setSeaSurfaceState(final SeaSurfaceState seaSurfaceState) {
        this.seaSurfaceState = seaSurfaceState;
    }

}
