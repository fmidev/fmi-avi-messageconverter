package fi.fmi.avi.model.immutable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.fmi.avi.model.CloudForecast;
import fi.fmi.avi.model.CloudLayer;
import fi.fmi.avi.model.NumericMeasure;
import org.inferred.freebuilder.FreeBuilder;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by rinne on 13/04/2018.
 */
@FreeBuilder
@JsonDeserialize(builder = CloudForecastImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"verticalVisibility", "layers", "noSignificantCloud", "verticalVisibilityMissing"})
public abstract class CloudForecastImpl implements CloudForecast, Serializable {

    public static CloudForecastImpl immutableCopyOf(final CloudForecast cloudForecast) {
        Objects.requireNonNull(cloudForecast);
        if (cloudForecast instanceof CloudForecastImpl) {
            return (CloudForecastImpl) cloudForecast;
        } else {
            return Builder.from(cloudForecast).build();
        }
    }

    public static Optional<CloudForecastImpl> immutableCopyOf(final Optional<CloudForecast> cloudForecast) {
        Objects.requireNonNull(cloudForecast);
        return cloudForecast.map(CloudForecastImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends CloudForecastImpl_Builder {

        public Builder() {
            setVerticalVisibilityMissing(false);
            setNoSignificantCloud(false);
        }
        public static Builder from(final CloudForecast value) {
            if (value instanceof CloudForecastImpl) {
                return ((CloudForecastImpl) value).toBuilder();
            } else {
                CloudForecastImpl.Builder retval = new CloudForecastImpl.Builder()//
                        .setNoSignificantCloud(value.isNoSignificantCloud())//
                        .setVerticalVisibilityMissing(value.isVerticalVisibilityMissing())//
                        .setVerticalVisibility(NumericMeasureImpl.immutableCopyOf(value.getVerticalVisibility()));

                value.getLayers()
                        .map(layers -> retval.setLayers(
                                Collections.unmodifiableList(layers.stream().map(CloudLayerImpl::immutableCopyOf).collect(Collectors.toList()))));
                return retval;
            }
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setVerticalVisibility(final NumericMeasure verticalVisibility) {
            return super.setVerticalVisibility(verticalVisibility);
        }

        @Override
        @JsonDeserialize(contentAs = CloudLayerImpl.class)
        public Builder setLayers(final List<CloudLayer> layers) {
            return super.setLayers(layers);
        }
    }
}
