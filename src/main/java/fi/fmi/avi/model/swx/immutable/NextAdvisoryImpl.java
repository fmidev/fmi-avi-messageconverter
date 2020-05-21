package fi.fmi.avi.model.swx.immutable;

import java.io.Serializable;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.swx.NextAdvisory;

@FreeBuilder
@JsonDeserialize(builder = NextAdvisoryImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "time", "timeSpecifier" })
public abstract class NextAdvisoryImpl implements NextAdvisory, Serializable {

    public static Builder builder() {
        return new NextAdvisoryImpl.Builder();
    }

    public abstract Builder toBuilder();

    public static class Builder extends NextAdvisoryImpl_Builder {
        Builder() {
        }
    }
}
