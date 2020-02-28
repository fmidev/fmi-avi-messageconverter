package fi.fmi.avi.model.SWX.immutable;

import org.inferred.freebuilder.FreeBuilder;

import fi.fmi.avi.model.SWX.SWXGeometry;

@FreeBuilder
public abstract class SWXGeometryImpl implements SWXGeometry {
    public static SWXGeometryImpl.Builder builder() {
        return new SWXGeometryImpl.Builder();
    }

    public static class Builder extends SWXGeometryImpl_Builder {

    }
}
