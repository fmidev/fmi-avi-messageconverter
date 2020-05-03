package fi.fmi.avi.model.SpaceWeatherAdvisory.immutable;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.SpaceWeatherAdvisory.IssuingCenter;

@FreeBuilder
@JsonDeserialize(builder = IssuingCenterImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class IssuingCenterImpl implements IssuingCenter {
    public static Builder builder() {
        return new Builder();
    }

    public abstract Builder toBuilder();

    public static class Builder extends IssuingCenterImpl_Builder {
        Builder() {

        }
    }
}
