package fi.fmi.avi.data.metar.impl;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.data.CloudLayer;
import fi.fmi.avi.data.NumericMeasure;
import fi.fmi.avi.data.impl.CloudLayerImpl;
import fi.fmi.avi.data.impl.NumericMeasureImpl;
import fi.fmi.avi.data.impl.PossiblyMissingContentImpl;
import fi.fmi.avi.data.metar.ObservedClouds;

/**
 * 
 * @author Ilkka Rinne / Spatineo Inc for the Finnish Meteorological Institute
 * 
 */

public class ObservedCloudsImpl extends PossiblyMissingContentImpl implements ObservedClouds {

    private boolean amountAndHeightUnobservableByAutoSystem;
    private NumericMeasure verticalVisibility;
    private List<CloudLayer> layers;

    public ObservedCloudsImpl() {
    }

    public ObservedCloudsImpl(final ObservedClouds input) {
        super(input.getMissingReason());
        if (MissingReason.NOT_MISSING.equals(this.getMissingReason())) {
            this.amountAndHeightUnobservableByAutoSystem = input.isAmountAndHeightUnobservableByAutoSystem();
            this.verticalVisibility = new NumericMeasureImpl(input.getVerticalVisibility());
            this.layers = new ArrayList<CloudLayer>();
            for (CloudLayer layer:input.getLayers()) {
                this.layers.add(new CloudLayerImpl(layer));
            }
        }
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.ObservedClouds#isAmountAndHeightUnobservableByAutoSystem()
     */
    @Override
    public boolean isAmountAndHeightUnobservableByAutoSystem() {
        return amountAndHeightUnobservableByAutoSystem;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.ObservedClouds#getVerticalVisibility()
     */
    @Override
    public NumericMeasure getVerticalVisibility() {
        return verticalVisibility;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.ObservedClouds#getLayers()
     */
    @Override
    public List<CloudLayer> getLayers() {
        return layers;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.ObservedClouds#setAmountAndHeightUnobservableByAutoSystem(boolean)
     */
    @Override
    public void setAmountAndHeightUnobservableByAutoSystem(final boolean amountAndHeightUnobservableByAutoSystem) {
        this.amountAndHeightUnobservableByAutoSystem = amountAndHeightUnobservableByAutoSystem;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.ObservedClouds#setVerticalVisibility(fi.fmi.avi.data.NumericMeasure)
     */
    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public void setVerticalVisibility(final NumericMeasure verticalVisibility) {
        this.verticalVisibility = verticalVisibility;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.ObservedClouds#setLayers(java.util.List)
     */
    @Override
    @JsonDeserialize(contentAs = CloudLayerImpl.class)
    public void setLayers(final List<CloudLayer> layers) {
        this.layers = layers;
    }
}
