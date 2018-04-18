package fi.fmi.avi.model.metar.immutable;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.immutable.CloudLayerImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.metar.ObservedClouds;

/**
 * Created by rinne on 13/04/2018.
 */

@FreeBuilder
@JsonDeserialize(builder = ObservedCloudsImpl.Builder.class)
public abstract class ObservedCloudsImpl implements ObservedClouds, Serializable {

    public static ObservedCloudsImpl immutableCopyOf(final ObservedClouds observedClouds) {
        checkNotNull(observedClouds);
        if (observedClouds instanceof ObservedCloudsImpl) {
            return (ObservedCloudsImpl) observedClouds;
        } else {
            return Builder.from(observedClouds).build();
        }
    }

    public static Optional<ObservedCloudsImpl> immutableCopyOf(final Optional<ObservedClouds> observedClouds) {
        checkNotNull(observedClouds);
        return observedClouds.map(ObservedCloudsImpl::immutableCopyOf);
    }

    abstract Builder toBuilder();

    public static class Builder extends ObservedCloudsImpl_Builder {

        public static Builder from(final ObservedClouds value) {
            ObservedCloudsImpl.Builder retval = new ObservedCloudsImpl.Builder().setAmountAndHeightUnobservableByAutoSystem(
                    value.isAmountAndHeightUnobservableByAutoSystem())
                    .setNoSignificantCloud(value.isNoSignificantCloud())
                    .setVerticalVisibility(NumericMeasureImpl.immutableCopyOf(value.getVerticalVisibility()));

            retval.getLayers()
                    .map(layers -> retval.setLayers(
                            Collections.unmodifiableList(layers.stream().map(CloudLayerImpl::immutableCopyOf).collect(Collectors.toList()))));
            return retval;
        }
    }
}
