package fi.fmi.avi.model.metar.immutable;

import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.CloudLayer;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.metar.ObservedCloudLayer;

@FreeBuilder
@JsonDeserialize(builder = ObservedCloudLayerImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "amount", "base", "cloudType", "amountNotDetectedByAutoSystem", "amountUnobservableByAutoSystem", "heightNotDetectedByAutoSystem",
        "heightUnobservableByAutoSystem", "cloudTypeUnobservableByAutoSystem" })
public abstract class ObservedCloudLayerImpl implements fi.fmi.avi.model.metar.ObservedCloudLayer {

    public static ObservedCloudLayerImpl immutableCopyOf(final CloudLayer layer) {
        Objects.requireNonNull(layer);
        if (layer instanceof ObservedCloudLayerImpl) {
            return (ObservedCloudLayerImpl) layer;
        } else if (layer instanceof ObservedCloudLayer) {
            return ObservedCloudLayerImpl.Builder.from((ObservedCloudLayer) layer).build();
        } else {
            return ObservedCloudLayerImpl.Builder.from(layer).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<ObservedCloudLayerImpl> immutableCopyOf(final Optional<CloudLayer> layer) {
        Objects.requireNonNull(layer);
        return layer.map(ObservedCloudLayerImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends ObservedCloudLayerImpl_Builder {

        public Builder() {
            setAmountNotDetectedByAutoSystem(false);
            setAmountUnobservableByAutoSystem(false);
            setHeightNotDetectedByAutoSystem(false);
            setHeightUnobservableByAutoSystem(false);
            setCloudTypeUnobservableByAutoSystem(false);
        }

        public static Builder from(final ObservedCloudLayer value) {
            if (value instanceof ObservedCloudLayerImpl) {
                return ((ObservedCloudLayerImpl) value).toBuilder();
            } else {
                return from((CloudLayer) value)//
                        .setAmountNotDetectedByAutoSystem(value.isAmountNotDetectedByAutoSystem())//
                        .setAmountUnobservableByAutoSystem(value.isAmountUnobservableByAutoSystem())//
                        .setHeightNotDetectedByAutoSystem(value.isHeightNotDetectedByAutoSystem())//
                        .setHeightUnobservableByAutoSystem(value.isHeightUnobservableByAutoSystem())//
                        .setCloudTypeUnobservableByAutoSystem(value.isCloudTypeUnobservableByAutoSystem());
            }
        }

        public static Builder from(final CloudLayer value) {
            if (value instanceof ObservedCloudLayerImpl) {
                return ((ObservedCloudLayerImpl) value).toBuilder();
            } else {
                return new ObservedCloudLayerImpl.Builder().setAmount(value.getAmount())//
                        .setCloudType(value.getCloudType())//
                        .setBase(NumericMeasureImpl.immutableCopyOf(value.getBase()));
            }
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public ObservedCloudLayerImpl.Builder setBase(final NumericMeasure base) {
            return super.setBase(base);
        }
    }
}
