package fi.fmi.avi.model.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import fi.fmi.avi.model.TacGeometry;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Created by rinne on 18/04/2018.
 */
@FreeBuilder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonDeserialize(builder = TacGeometryImpl.Builder.class)
public abstract class TacGeometryImpl implements TacGeometry, Serializable {

    private static final long serialVersionUID = 1234L;

    public static Builder builder() {
        return new Builder();
    }

    public static TacGeometryImpl immutableCopyOf(final TacGeometry tacGeometry) {
        Objects.requireNonNull(tacGeometry);
        if (tacGeometry instanceof SurfaceWindImpl) {
            return (TacGeometryImpl) tacGeometry;
        } else {
            return Builder.from(tacGeometry).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<TacGeometryImpl> immutableCopyOf(final Optional<TacGeometry> tacGeometry) {
        Objects.requireNonNull(tacGeometry);
        return tacGeometry.map(TacGeometryImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    @SuppressWarnings("EmptyMethod")
    public static class Builder extends TacGeometryImpl_Builder {

        Builder() {
        }

        public static Builder from(final TacGeometry value) {
            if (value instanceof TacGeometryImpl) {
                return ((TacGeometryImpl) value).toBuilder();
            } else {
                return TacGeometryImpl.builder()//
                        .setTacContent(value.getTacContent());
            }
        }
    }
}
