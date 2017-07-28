package fi.fmi.avi.model.metar.impl;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.CloudLayer;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.impl.CloudLayerImpl;
import fi.fmi.avi.model.impl.NumericMeasureImpl;
import fi.fmi.avi.model.metar.ObservedClouds;

/**
 * 
 * @author Ilkka Rinne / Spatineo Inc for the Finnish Meteorological Institute
 * 
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ObservedCloudsImpl implements ObservedClouds {

    private boolean amountAndHeightUnobservableByAutoSystem;
    private boolean isNoSignificantCloud;
    private NumericMeasure verticalVisibility;
    private List<CloudLayer> layers;

    public ObservedCloudsImpl() {
    }

    public ObservedCloudsImpl(final ObservedClouds input) {
        this.amountAndHeightUnobservableByAutoSystem = input.isAmountAndHeightUnobservableByAutoSystem();
        this.isNoSignificantCloud = input.isNoSignificantCloud();
        this.verticalVisibility = new NumericMeasureImpl(input.getVerticalVisibility());
        this.layers = new ArrayList<CloudLayer>();
        for (CloudLayer layer:input.getLayers()) {
            this.layers.add(new CloudLayerImpl(layer));
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
     * @see fi.fmi.avi.data.ObservedClouds#setVerticalVisibility(fi.fmi.avi.model.NumericMeasure)
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

	@Override
	public boolean isNoSignificantCloud() {
		return this.isNoSignificantCloud;
	}

	@Override
	public void setNoSignificantCloud(boolean nsc) {
		this.isNoSignificantCloud = nsc;
		
	}
}
