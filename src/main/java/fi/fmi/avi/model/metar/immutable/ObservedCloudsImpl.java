package fi.fmi.avi.model.metar.immutable;


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

import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.metar.ObservedCloudLayer;
import fi.fmi.avi.model.metar.ObservedClouds;

/**
 * Created by rinne on 13/04/2018.
 */

@Value.Immutable
@JsonDeserialize(builder = ObservedCloudsImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "layers", "verticalVisibility", "noSignificantCloud", "noCloudsDetectedByAutoSystem", "verticalVisibilityUnobservableByAutoSystem" })
public abstract class ObservedCloudsImpl implements ObservedClouds, Serializable {

    private static final long serialVersionUID = -6578295705372073484L;

    public static Builder builder() {
        return new Builder();
    }

    public static ObservedCloudsImpl immutableCopyOf(final ObservedClouds observedClouds) {
        Objects.requireNonNull(observedClouds);
        if (observedClouds instanceof ObservedCloudsImpl) {
            return (ObservedCloudsImpl) observedClouds;
        } else {
            return Builder.copyOf(observedClouds).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<ObservedCloudsImpl> immutableCopyOf(final Optional<ObservedClouds> observedClouds) {
        Objects.requireNonNull(observedClouds);
        return observedClouds.map(ObservedCloudsImpl::immutableCopyOf);
    }

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    @Override
    @JsonDeserialize(contentAs = NumericMeasureImpl.class)
    public abstract Optional<NumericMeasure> getVerticalVisibility();

    // NOTE: no per-property @JsonDeserialize(contentAs=...) hint here: see SIGMETImpl.getAnalysisGeometries() for why
    // (Optional<List<X>> on a non-detached builder). ObservedCloudLayer instead carries its own class-level
    // @JsonDeserialize(as=...) hint (see doc/immutables-migration.md).
    @Override
    public abstract Optional<List<ObservedCloudLayer>> getLayers();

    public static class Builder extends ImmutableObservedCloudsImpl.Builder {

        Builder() {
            setNoCloudsDetectedByAutoSystem(false);
            setNoSignificantCloud(false);
            setVerticalVisibilityUnobservableByAutoSystem(false);
        }

        public static Builder copyOf(final ObservedClouds value) {
            if (value instanceof ObservedCloudsImpl) {
                return ((ObservedCloudsImpl) value).toBuilder();
            } else {
                final ObservedCloudsImpl.Builder retval = ObservedCloudsImpl.builder()//
                        .setNoCloudsDetectedByAutoSystem(value.isNoCloudsDetectedByAutoSystem())
                        .setNoSignificantCloud(value.isNoSignificantCloud())
                        .setVerticalVisibility(NumericMeasureImpl.immutableCopyOf(value.getVerticalVisibility()));

                value.getLayers()
                        .map(layers -> retval.setLayers(
                                Collections.unmodifiableList(layers.stream().map(ObservedCloudLayerImpl::immutableCopyOf).collect(Collectors.toList()))));
                return retval;
            }
        }
    }
}
