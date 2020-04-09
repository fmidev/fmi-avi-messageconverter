package fi.fmi.avi.model.SpaceWeatherAdvisory.immutable;

import java.io.Serializable;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.SpaceWeatherAdvisory.AdvisoryNumber;

@FreeBuilder
@JsonDeserialize(builder = AdvisoryNumberImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class AdvisoryNumberImpl implements AdvisoryNumber, Serializable {
    public static AdvisoryNumberImpl.Builder builder() {
        return new AdvisoryNumberImpl.Builder();
    }

    public abstract Builder toBuilder();

    public static class Builder extends AdvisoryNumberImpl_Builder {
        Builder() {
        }
    }
}
