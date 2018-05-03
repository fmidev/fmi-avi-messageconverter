package fi.fmi.avi.model.immutable;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.CloudForecast;
import fi.fmi.avi.model.CloudLayer;
import fi.fmi.avi.model.NumericMeasure;

/**
 * Created by rinne on 13/04/2018.
 */
@FreeBuilder
@JsonDeserialize(builder = CloudForecastImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class CloudForecastImpl implements CloudForecast, Serializable {

    public static CloudForecastImpl immutableCopyOf(final CloudForecast cloudForecast) {
        checkNotNull(cloudForecast);
        if (cloudForecast instanceof CloudForecastImpl) {
            return (CloudForecastImpl) cloudForecast;
        } else {
            return Builder.from(cloudForecast).build();
        }
    }

    public static Optional<CloudForecastImpl> immutableCopyOf(final Optional<CloudForecast> cloudForecast) {
        checkNotNull(cloudForecast);
        return cloudForecast.map(CloudForecastImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends CloudForecastImpl_Builder {

        public Builder() {
            setNoSignificantCloud(false);
        }
        public static Builder from(final CloudForecast value) {
            CloudForecastImpl.Builder retval = new CloudForecastImpl.Builder().setNoSignificantCloud(value.isNoSignificantCloud())
                    .setVerticalVisibility(NumericMeasureImpl.immutableCopyOf(value.getVerticalVisibility()));

            value.getLayers()
                    .map(layers -> retval.setLayers(
                            Collections.unmodifiableList(layers.stream().map(CloudLayerImpl::immutableCopyOf).collect(Collectors.toList()))));
            return retval;
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
