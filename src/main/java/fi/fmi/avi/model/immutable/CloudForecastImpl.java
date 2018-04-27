package fi.fmi.avi.model.immutable;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.CloudForecast;

/**
 * Created by rinne on 13/04/2018.
 */
@FreeBuilder
@JsonDeserialize(builder = CloudForecastImpl.Builder.class)
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

        public static Builder from(final CloudForecast value) {
            CloudForecastImpl.Builder retval = new CloudForecastImpl.Builder().setNoSignificantCloud(value.isNoSignificantCloud())
                    .setVerticalVisibility(NumericMeasureImpl.immutableCopyOf(value.getVerticalVisibility()));

            value.getLayers()
                    .map(layers -> retval.setLayers(
                            Collections.unmodifiableList(layers.stream().map(CloudLayerImpl::immutableCopyOf).collect(Collectors.toList()))));
            return retval;
        }
    }
}
