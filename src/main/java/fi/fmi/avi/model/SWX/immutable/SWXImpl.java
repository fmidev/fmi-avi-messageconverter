package fi.fmi.avi.model.SWX.immutable;

import java.io.Serializable;

import org.inferred.freebuilder.FreeBuilder;

import fi.fmi.avi.model.SWX.SWX;

@FreeBuilder
public abstract class SWXImpl implements SWX, Serializable {

    public static SWXImpl.Builder builder() {
        return new SWXImpl.Builder();
    }

    public abstract SWXImpl.Builder toBuilder();

    @Override
    public boolean areAllTimeReferencesComplete() {
        return true;
    }

    public static class Builder extends SWXImpl_Builder {

    }

}
