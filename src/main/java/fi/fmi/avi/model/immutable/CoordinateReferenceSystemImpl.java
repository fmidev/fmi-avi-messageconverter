package fi.fmi.avi.model.immutable;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.CoordinateReferenceSystem;

@FreeBuilder
@JsonDeserialize(builder = CoordinateReferenceSystemImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "name", "dimension", "axisLabels", "uomLabels" })
public abstract class CoordinateReferenceSystemImpl implements CoordinateReferenceSystem, Serializable {

    private static final long serialVersionUID = 8478876500014731383L;

    private static final CoordinateReferenceSystemImpl WGS_84 = CoordinateReferenceSystemImpl.builder()//
            .setName(AviationCodeListUser.CODELIST_VALUE_EPSG_4326)//
            .setDimension(2)//
            .addAxisLabels("Lat", "Lon")//
            .build();

    public static Builder builder() {
        return new Builder();
    }

    public static CoordinateReferenceSystemImpl immutableCopyOf(final CoordinateReferenceSystem crs) {
        if (crs instanceof CoordinateReferenceSystemImpl) {
            return (CoordinateReferenceSystemImpl) crs;
        }
        return Builder.from(requireNonNull(crs, "crs")).build();
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<CoordinateReferenceSystemImpl> immutableCopyOf(final Optional<CoordinateReferenceSystem> crs) {
        requireNonNull(crs, "crs");
        return crs.map(CoordinateReferenceSystemImpl::immutableCopyOf);
    }

    public static CoordinateReferenceSystemImpl wgs84() {
        return WGS_84;
    }

    public abstract Builder toBuilder();

    public static class Builder extends CoordinateReferenceSystemImpl_Builder {
        @Deprecated
        public Builder() {
        }

        public static Builder from(final CoordinateReferenceSystem value) {
            if (value instanceof CoordinateReferenceSystemImpl) {
                return ((CoordinateReferenceSystemImpl) value).toBuilder();
            }
            return CoordinateReferenceSystemImpl.builder()//
                    .setName(value.getName())//
                    .setDimension(value.getDimension())//
                    .addAllAxisLabels(value.getAxisLabels())//
                    .addAllUomLabels(value.getUomLabels());
        }
    }
}
