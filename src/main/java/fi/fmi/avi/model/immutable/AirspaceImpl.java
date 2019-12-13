package fi.fmi.avi.model.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.Airspace;

/**
 * Created by rinne on 13/04/2018.
 */

@FreeBuilder
@JsonDeserialize(builder = AirspaceImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"designator", "name", "part", "type"})
public abstract class AirspaceImpl implements Airspace, Serializable {

    public static AirspaceImpl immutableCopyOf(final Airspace airspace) {
        Objects.requireNonNull(airspace);
        if (airspace instanceof AirspaceImpl) {
            return (AirspaceImpl) airspace;
        } else {
            return Builder.from(airspace).build();
        }
    }

    public static Optional<AirspaceImpl> immutableCopyOf(final Optional<Airspace> airspace) {
        return airspace.map(AirspaceImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends AirspaceImpl_Builder {

        public static Builder from(final Airspace value) {
            if (value instanceof AirspaceImpl) {
                return ((AirspaceImpl) value).toBuilder();
            } else {
                return new AirspaceImpl.Builder()//
                        .setType(value.getType())
                        .setDesignator(value.getDesignator())
                        .setPart(value.getPart())
                        .setName(value.getName());
            }
        }

    }
}
