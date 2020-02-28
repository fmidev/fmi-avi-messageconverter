package fi.fmi.avi.model.SWX;

import java.io.Serializable;

import org.inferred.freebuilder.FreeBuilder;

@FreeBuilder
public abstract class NextAdvisoryImpl implements NextAdvisory, Serializable {

    public static NextAdvisoryImpl.Builder builder() {
        return new NextAdvisoryImpl.Builder();
    }

    public static class Builder extends NextAdvisoryImpl_Builder {

    }
}
