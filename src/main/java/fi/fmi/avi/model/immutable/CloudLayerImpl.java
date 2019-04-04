package fi.fmi.avi.model.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.CloudLayer;
import fi.fmi.avi.model.NumericMeasure;

/**
 * Created by rinne on 17/04/2018.
 */

@FreeBuilder
@JsonDeserialize(builder = CloudLayerImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"amount", "base", "cloudType"})
public abstract class CloudLayerImpl implements CloudLayer, Serializable {

    private static final long serialVersionUID = 7576387664491299068L;

    public static Builder builder() {
        return new Builder();
    }

    public static CloudLayerImpl immutableCopyOf(final CloudLayer cloudLayer) {
        Objects.requireNonNull(cloudLayer);
        if (cloudLayer instanceof CloudLayerImpl) {
            return (CloudLayerImpl) cloudLayer;
        } else {
            return Builder.from(cloudLayer).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<CloudLayerImpl> immutableCopyOf(final Optional<CloudLayer> cloudLayer) {
        Objects.requireNonNull(cloudLayer);
        return cloudLayer.map(CloudLayerImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends CloudLayerImpl_Builder {

        @Deprecated
        public Builder() {
        }

        public static Builder from(final CloudLayer value) {
            if (value instanceof CloudLayerImpl) {
                return ((CloudLayerImpl) value).toBuilder();
            } else {
                return CloudLayerImpl.builder()//
                        .setAmount(value.getAmount())//
                        .setCloudType(value.getCloudType())//
                        .setBase(NumericMeasureImpl.immutableCopyOf(value.getBase()));
            }
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setBase(final NumericMeasure base) {
            return super.setBase(base);
        }
    }
}
