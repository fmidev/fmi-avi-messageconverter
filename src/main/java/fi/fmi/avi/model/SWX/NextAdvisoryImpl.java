package fi.fmi.avi.model.SWX;

import java.io.Serializable;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@FreeBuilder
@JsonDeserialize(builder = NextAdvisoryImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class NextAdvisoryImpl implements NextAdvisory, Serializable {

    public static Builder builder() {
        return new NextAdvisoryImpl.Builder();
    }

    public static class Builder extends NextAdvisoryImpl_Builder {

    }
}
