package fi.fmi.avi.model.impl;

import java.io.Serializable;
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
public class CloudForecastImpl implements CloudForecast, Cloneable, Serializable {

    private static final long serialVersionUID = 3260177137474861151L;

    private boolean isNoSignificantCloud;
    private NumericMeasure verticalVisibility; // only if no layers
    private List<CloudLayer> layers;

    public CloudForecastImpl() {
    }

    public CloudForecastImpl(final CloudForecast input) {
        if (input != null) {
            this.isNoSignificantCloud = input.isNoSignificantCloud();
            if (input.getVerticalVisibility() != null) {
                this.verticalVisibility = new NumericMeasureImpl(input.getVerticalVisibility());
            }
            if (input.getLayers() != null) {
                this.layers = new ArrayList<>();
                for (final CloudLayer layer : input.getLayers()) {
                    this.layers.add(new CloudLayerImpl(layer));
                }
            }
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
    public void setNoSignificantCloud(final boolean nsc) {
        this.isNoSignificantCloud = nsc;

    }
}
