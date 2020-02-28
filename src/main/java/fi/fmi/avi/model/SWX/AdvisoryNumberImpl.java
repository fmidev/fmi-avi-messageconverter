package fi.fmi.avi.model.SWX;

import java.io.Serializable;

import org.inferred.freebuilder.FreeBuilder;

@FreeBuilder
public abstract class AdvisoryNumberImpl implements AdvisoryNumber, Serializable {
    public static AdvisoryNumberImpl.Builder builder() {
        return new AdvisoryNumberImpl.Builder();
    }

    public abstract AdvisoryNumberImpl.Builder toBuilder();

    public static class Builder extends AdvisoryNumberImpl_Builder {

    }
}
