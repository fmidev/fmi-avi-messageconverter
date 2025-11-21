package fi.fmi.avi.model.swx.amd82.immutable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.fmi.avi.model.swx.amd82.NextAdvisory;
import org.inferred.freebuilder.FreeBuilder;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

@FreeBuilder
@JsonDeserialize(builder = NextAdvisoryImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"time", "timeSpecifier"})
public abstract class NextAdvisoryImpl implements NextAdvisory, Serializable {

    private static final long serialVersionUID = 1697837715002765108L;

    public static Builder builder() {
        return new Builder();
    }

    public static NextAdvisoryImpl immutableCopyOf(final NextAdvisory nextAdvisory) {
        Objects.requireNonNull(nextAdvisory);
        if (nextAdvisory instanceof NextAdvisoryImpl) {
            return (NextAdvisoryImpl) nextAdvisory;
        } else {
            return Builder.from(nextAdvisory).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<NextAdvisoryImpl> immutableCopyOf(final Optional<NextAdvisory> nextAdvisory) {
        Objects.requireNonNull(nextAdvisory);
        return nextAdvisory.map(NextAdvisoryImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends NextAdvisoryImpl_Builder {
        @Deprecated
        Builder() {
        }

        public static Builder from(final NextAdvisory value) {
            if (value instanceof NextAdvisoryImpl) {
                return ((NextAdvisoryImpl) value).toBuilder();
            } else {
                return builder().setTime(value.getTime()).setTimeSpecifier(value.getTimeSpecifier());
            }
        }

        public static Builder fromAmd79(final fi.fmi.avi.model.swx.amd79.NextAdvisory value) {
            return builder().setTime(value.getTime()).setTimeSpecifier(NextAdvisory.Type.valueOf(value.getTimeSpecifier().name()));
        }
    }
}
