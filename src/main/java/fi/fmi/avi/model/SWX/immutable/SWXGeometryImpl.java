package fi.fmi.avi.model.SWX.immutable;

import java.io.Serializable;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.SWX.SWXGeometry;

@FreeBuilder
@JsonDeserialize(builder = SWXGeometryImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonTypeName("SWX")
public abstract class SWXGeometryImpl implements SWXGeometry, Serializable {
    public static Builder builder() {
        return new SWXGeometryImpl.Builder();
    }

    public static class Builder extends SWXGeometryImpl_Builder {

    }
}it a
