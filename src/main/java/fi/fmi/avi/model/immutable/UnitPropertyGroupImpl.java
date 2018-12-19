package fi.fmi.avi.model.immutable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.fmi.avi.model.UnitPropertyGroup;
import org.inferred.freebuilder.FreeBuilder;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

@FreeBuilder
@JsonDeserialize(builder = UnitPropertyGroupImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"name", "type", "designator"})
public abstract class UnitPropertyGroupImpl implements UnitPropertyGroup, Serializable {
    public static UnitPropertyGroupImpl immutableCopyOf(final UnitPropertyGroup unitPropertyGroup) {
        Objects.requireNonNull(unitPropertyGroup);
        if (unitPropertyGroup instanceof UnitPropertyGroupImpl) {
            return (UnitPropertyGroupImpl) unitPropertyGroup;
        } else {
            return Builder.from(unitPropertyGroup).build();
        }
    }

    public static Optional<UnitPropertyGroupImpl> immutableCopyOf(final Optional<UnitPropertyGroup> unitPropertyGroup) {
        return unitPropertyGroup.map(UnitPropertyGroupImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends UnitPropertyGroupImpl_Builder {

        public static Builder from(final UnitPropertyGroup value) {
            if (value instanceof UnitPropertyGroupImpl) {
                return ((UnitPropertyGroupImpl) value).toBuilder();
            } else {
                return new UnitPropertyGroupImpl.Builder()//
                        .setName(value.getName())//
                        .setDesignator(value.getDesignator())
                        .setType(value.getType());
            }
        }

        @JsonIgnore
        public Builder setPropertyGroup(String name, String designator, String type) {
            return super.setName(name).setDesignator(designator).setType(type);
        }
    }
}
