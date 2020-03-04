package fi.fmi.avi.model.SWX;

import java.io.Serializable;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@FreeBuilder
@JsonDeserialize(builder = AdvisoryNumberImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class AdvisoryNumberImpl implements AdvisoryNumber, Serializable {
    public static AdvisoryNumberImpl.Builder builder() {
        return new AdvisoryNumberImpl.Builder();
    }

    public abstract AdvisoryNumberImpl.Builder toBuilder();

    public static class Builder extends AdvisoryNumberImpl_Builder {

    }
}
