package fi.fmi.avi.model.impl;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.CloudForecast;
import fi.fmi.avi.model.CloudLayer;
import fi.fmi.avi.model.NumericMeasure;

/**
 *
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class CloudForecastImpl implements CloudForecast {

	private boolean isNoSignificantCloud;
    private NumericMeasure verticalVisibility; // only if no layers
    private List<CloudLayer> layers;

    public CloudForecastImpl() {
    }

    public CloudForecastImpl(final CloudForecast input) {
        this.isNoSignificantCloud = input.isNoSignificantCloud();
        this.verticalVisibility = new NumericMeasureImpl(input.getVerticalVisibility());
        this.layers = new ArrayList<CloudLayer>();
        for (CloudLayer layer: input.getLayers()) {
            this.layers.add(new CloudLayerImpl(layer));
        }
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.model.metar.CloudForecast#getVerticalVisibility()
     */
    @Override
    public NumericMeasure getVerticalVisibility() {
        return verticalVisibility;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.model.metar.CloudForecast#setVerticalVisibility(fi.fmi.avi.model.metar.NumericMeasureImpl)
     */
    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public void setVerticalVisibility(final NumericMeasure verticalVisibility) {
        this.verticalVisibility = verticalVisibility;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.model.metar.CloudForecast#getLayers()
     */
    @Override
    public List<CloudLayer> getLayers() {
        return layers;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.model.metar.CloudForecast#setLayers(java.util.List)
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
