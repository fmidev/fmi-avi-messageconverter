package fi.fmi.avi.model;

import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@FreeBuilder
@JsonDeserialize(builder = CloudLayer.Builder.class)
public interface CloudLayer extends AviationCodeListUser {

    CloudAmount getAmount();

    Optional<NumericMeasure> getBase();

    Optional<CloudType> getCloudType();

    Builder toBuilder();

    class Builder extends CloudLayer_Builder {
    }
}
