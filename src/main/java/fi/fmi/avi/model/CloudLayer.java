package fi.fmi.avi.model;

import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@FreeBuilder
@JsonDeserialize(builder = CloudLayer.Builder.class)
public interface CloudLayer extends AviationCodeListUser {

    CloudAmount amount();

    Optional<NumericMeasure> base();

    Optional<CloudType> cloudType();

    Builder toBuilder();

    class Builder extends CloudLayer_Builder {
    }
}
