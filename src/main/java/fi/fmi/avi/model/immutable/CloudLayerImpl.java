package fi.fmi.avi.model.immutable;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.CloudLayer;
import fi.fmi.avi.model.NumericMeasure;

/**
 * Created by rinne on 17/04/2018.
 */

@FreeBuilder
@JsonDeserialize(builder = CloudLayerImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class CloudLayerImpl implements CloudLayer, Serializable {

    public static CloudLayerImpl immutableCopyOf(final CloudLayer cloudLayer) {
        checkNotNull(cloudLayer);
        if (cloudLayer instanceof CloudLayerImpl) {
            return (CloudLayerImpl) cloudLayer;
        } else {
            return Builder.from(cloudLayer).build();
        }
    }

    public static Optional<CloudLayerImpl> immutableCopyOf(final Optional<CloudLayer> cloudLayer) {
        checkNotNull(cloudLayer);
        return cloudLayer.map(CloudLayerImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends CloudLayerImpl_Builder {

        public static Builder from(final CloudLayer value) {
            return new CloudLayerImpl.Builder().setAmount(value.getAmount())
                    .setCloudType(value.getCloudType())
                    .setBase(NumericMeasureImpl.immutableCopyOf(value.getBase()));
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setBase(final NumericMeasure base) {
            return super.setBase(base);
        }
    }
}
