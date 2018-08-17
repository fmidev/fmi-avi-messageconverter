package fi.fmi.avi.model.metar.immutable;


import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.CloudLayer;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.immutable.CloudLayerImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.metar.ObservedClouds;

/**
 * Created by rinne on 13/04/2018.
 */

@FreeBuilder
@JsonDeserialize(builder = ObservedCloudsImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"layers", "verticalVisibility", "noSignificantCloud", "amountAndHeightUnobservableByAutoSystem"})
public abstract class ObservedCloudsImpl implements ObservedClouds, Serializable {

    public static ObservedCloudsImpl immutableCopyOf(final ObservedClouds observedClouds) {
        Objects.requireNonNull(observedClouds);
        if (observedClouds instanceof ObservedCloudsImpl) {
            return (ObservedCloudsImpl) observedClouds;
        } else {
            return Builder.from(observedClouds).build();
        }
    }

    public static Optional<ObservedCloudsImpl> immutableCopyOf(final Optional<ObservedClouds> observedClouds) {
        Objects.requireNonNull(observedClouds);
        return observedClouds.map(ObservedCloudsImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends ObservedCloudsImpl_Builder {

        public Builder() {
            setAmountUnobservableByAutoSystem(false);
            setAmountNotDetectedCloudsDetectedByAutoSystem(false);
            setHeightUnobservableByAutoSystem(false);
            setHeightNotDetectedCloudsDetectedByAutoSystem(false);
            setCloudTypeUnobservableByAutoSystem(false);
            setNoCloudsDetectedByAutoSystem(false);
            setNoSignificantCloud(false);
            setVerticalVisibilityUnobservableByAutoSystem(false);
        }

        public static Builder from(final ObservedClouds value) {
            if (value instanceof ObservedCloudsImpl) {
                return ((ObservedCloudsImpl) value).toBuilder();
            } else {
                ObservedCloudsImpl.Builder retval = new ObservedCloudsImpl.Builder()//
                        .setAmountUnobservableByAutoSystem(value.isAmountUnobservableByAutoSystem())
                        .setAmountNotDetectedCloudsDetectedByAutoSystem(value.isAmountNotDetectedCloudsDetectedByAutoSystem())
                        .setHeightUnobservableByAutoSystem(value.isHeightUnobservableByAutoSystem())
                        .setHeightNotDetectedCloudsDetectedByAutoSystem(value.isHeightNotDetectedCloudsDetectedByAutoSystem())
                        .setCloudTypeUnobservableByAutoSystem(value.isCloudTypeUnobservableByAutoSystem())
                        .setNoCloudsDetectedByAutoSystem(value.isNoCloudsDetectedByAutoSystem())
                        .setNoSignificantCloud(value.isNoSignificantCloud())
                        .setVerticalVisibility(NumericMeasureImpl.immutableCopyOf(value.getVerticalVisibility()));

                retval.getLayers()
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
