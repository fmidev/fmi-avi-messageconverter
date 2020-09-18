package fi.fmi.avi.model.immutable;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.ElevatedPoint;

/**
 * Created by rinne on 17/04/2018.
 */
@FreeBuilder
@JsonDeserialize(builder = ElevatedPointImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class ElevatedPointImpl implements ElevatedPoint, Serializable {

    private static final long serialVersionUID = -69857237712526561L;

    public static Builder builder() {
        return new Builder();
    }

    public static ElevatedPointImpl immutableCopyOf(final ElevatedPoint geoPosition) {
        Objects.requireNonNull(geoPosition);
        if (geoPosition instanceof ElevatedPointImpl) {
            return (ElevatedPointImpl) geoPosition;
        } else {
            return Builder.from(geoPosition).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<ElevatedPointImpl> immutableCopyOf(final Optional<ElevatedPoint> geoPosition) {
        Objects.requireNonNull(geoPosition);
        return geoPosition.map(ElevatedPointImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends ElevatedPointImpl_Builder {

        @Deprecated
        public Builder() {
        }

        public static Builder from(final ElevatedPoint value) {
            if (value instanceof ElevatedPointImpl) {
                return ((ElevatedPointImpl) value).toBuilder();
            } else {
                return ElevatedPointImpl.builder()//
                        .setSrsName(value.getSrsName())//
                        .setSrsDimension(value.getSrsDimension())//
                        .setAxisLabels(value.getAxisLabels())//
                        .addAllCoordinates(value.getCoordinates()).setElevationUom(value.getElevationUom()).setElevationValue(value.getElevationValue());
            }
        }

        @Override
        @JsonAlias("coordinateReferenceSystemId")
        public Optional<String> getSrsName() {
            return super.getSrsName();
        }

        public Builder setCoordinates(final List<Double> coordinates) {
            return this.clearCoordinates().addAllCoordinates(coordinates);
        }
    }

}
