package fi.fmi.avi.model.metar.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.CloudLayer;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.metar.ObservedCloudLayer;

@Value.Immutable
@JsonDeserialize(builder = ObservedCloudLayerImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "amount", "base", "cloudType", "amountNotDetectedByAutoSystem", "amountUnobservableByAutoSystem", "heightNotDetectedByAutoSystem",
        "heightUnobservableByAutoSystem", "cloudTypeUnobservableByAutoSystem" })
public abstract class ObservedCloudLayerImpl implements fi.fmi.avi.model.metar.ObservedCloudLayer, Serializable {

    private static final long serialVersionUID = 7312850983219439091L;

    public static Builder builder() {
        return new Builder();
    }

    public static ObservedCloudLayerImpl immutableCopyOf(final CloudLayer layer) {
        Objects.requireNonNull(layer);
        if (layer instanceof ObservedCloudLayerImpl) {
            return (ObservedCloudLayerImpl) layer;
        } else if (layer instanceof ObservedCloudLayer) {
            return ObservedCloudLayerImpl.Builder.copyOf((ObservedCloudLayer) layer).build();
        } else {
            return ObservedCloudLayerImpl.Builder.copyOf(layer).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<ObservedCloudLayerImpl> immutableCopyOf(final Optional<CloudLayer> layer) {
        Objects.requireNonNull(layer);
        return layer.map(ObservedCloudLayerImpl::immutableCopyOf);
    }

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    @Override
    @JsonDeserialize(contentAs = NumericMeasureImpl.class)
    public abstract Optional<NumericMeasure> getBase();

    public static class Builder extends ImmutableObservedCloudLayerImpl.Builder {

        Builder() {
            setAmountNotDetectedByAutoSystem(false);
            setAmountUnobservableByAutoSystem(false);
            setHeightNotDetectedByAutoSystem(false);
            setHeightUnobservableByAutoSystem(false);
            setCloudTypeUnobservableByAutoSystem(false);
        }

        public static Builder copyOf(final ObservedCloudLayer value) {
            if (value instanceof ObservedCloudLayerImpl) {
                return ((ObservedCloudLayerImpl) value).toBuilder();
            } else {
                return copyOf((CloudLayer) value)//
                        .setAmountNotDetectedByAutoSystem(value.isAmountNotDetectedByAutoSystem())//
                        .setAmountUnobservableByAutoSystem(value.isAmountUnobservableByAutoSystem())//
                        .setHeightNotDetectedByAutoSystem(value.isHeightNotDetectedByAutoSystem())//
                        .setHeightUnobservableByAutoSystem(value.isHeightUnobservableByAutoSystem())//
                        .setCloudTypeUnobservableByAutoSystem(value.isCloudTypeUnobservableByAutoSystem());
            }
        }

        public static Builder copyOf(final CloudLayer value) {
            if (value instanceof ObservedCloudLayerImpl) {
                return ((ObservedCloudLayerImpl) value).toBuilder();
            } else {
                return ObservedCloudLayerImpl.builder().setAmount(value.getAmount())//
                        .setCloudType(value.getCloudType())//
                        .setBase(NumericMeasureImpl.immutableCopyOf(value.getBase()));
            }
        }
    }
}
