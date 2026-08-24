package fi.fmi.avi.model.immutable;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.CloudForecast;
import fi.fmi.avi.model.CloudLayer;
import fi.fmi.avi.model.NumericMeasure;

/**
 * Created by rinne on 13/04/2018.
 */
@Value.Immutable
@JsonDeserialize(builder = CloudForecastImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "verticalVisibility", "layers", "noSignificantCloud", "verticalVisibilityMissing" })
public abstract class CloudForecastImpl implements CloudForecast, Serializable {

    private static final long serialVersionUID = -8501483321135680561L;

    public static Builder builder() {
        return new Builder();
    }

    public static CloudForecastImpl immutableCopyOf(final CloudForecast cloudForecast) {
        Objects.requireNonNull(cloudForecast);
        if (cloudForecast instanceof CloudForecastImpl) {
            return (CloudForecastImpl) cloudForecast;
        } else {
            return Builder.copyOf(cloudForecast).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<CloudForecastImpl> immutableCopyOf(final Optional<CloudForecast> cloudForecast) {
        Objects.requireNonNull(cloudForecast);
        return cloudForecast.map(CloudForecastImpl::immutableCopyOf);
    }

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    @Override
    @JsonDeserialize(contentAs = NumericMeasureImpl.class)
    public abstract Optional<NumericMeasure> getVerticalVisibility();

    // NOTE: no per-property @JsonDeserialize(contentAs=...) hint here: see SIGMETImpl.getAnalysisGeometries() for why
    // (Optional<List<X>> on a non-detached builder). CloudLayer instead carries its own class-level
    // @JsonDeserialize(as=...) hint (see doc/immutables-migration.md).
    @Override
    public abstract Optional<List<CloudLayer>> getLayers();

    public static class Builder extends ImmutableCloudForecastImpl.Builder {

        Builder() {
            setVerticalVisibilityMissing(false);
            setNoSignificantCloud(false);
        }

        public static Builder copyOf(final CloudForecast value) {
            if (value instanceof CloudForecastImpl) {
                return ((CloudForecastImpl) value).toBuilder();
            } else {
                final CloudForecastImpl.Builder retval = new CloudForecastImpl.Builder()//
                        .setNoSignificantCloud(value.isNoSignificantCloud())//
                        .setVerticalVisibilityMissing(value.isVerticalVisibilityMissing())//
                        .setVerticalVisibility(NumericMeasureImpl.immutableCopyOf(value.getVerticalVisibility()));

                value.getLayers()
                        .map(layers -> retval.setLayers(
                                Collections.unmodifiableList(layers.stream().map(CloudLayerImpl::immutableCopyOf).collect(Collectors.toList()))));
                return retval;
            }
        }

    }
}
