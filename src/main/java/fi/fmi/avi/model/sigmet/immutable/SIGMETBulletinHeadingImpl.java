package fi.fmi.avi.model.sigmet.immutable;

import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.sigmet.SIGMETBulletinHeading;

@FreeBuilder
@JsonDeserialize(builder = SIGMETBulletinHeadingImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "locationIndicator", "geographicalDesignator", "bulletinNumber", "type", "bulletinAugmentationNumber", "SIGMETType" })
public abstract class SIGMETBulletinHeadingImpl implements SIGMETBulletinHeading {

    public static SIGMETBulletinHeadingImpl immutableCopyOf(final SIGMETBulletinHeading heading) {
        Objects.requireNonNull(heading);
        if (heading instanceof SIGMETBulletinHeadingImpl) {
            return (SIGMETBulletinHeadingImpl) heading;
        } else {
            return Builder.from(heading).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<SIGMETBulletinHeadingImpl> immutableCopyOf(final Optional<SIGMETBulletinHeading> heading) {
        return heading.map(SIGMETBulletinHeadingImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends SIGMETBulletinHeadingImpl_Builder {

        public Builder() {
            setType(Type.NORMAL);
        }

        public static Builder from(final SIGMETBulletinHeading value) {
            if (value instanceof SIGMETBulletinHeadingImpl) {
                return ((SIGMETBulletinHeadingImpl) value).toBuilder();
            } else {
                return new SIGMETBulletinHeadingImpl.Builder()//
                        .setLocationIndicator(value.getLocationIndicator())//
                        .setGeographicalDesignator(value.getGeographicalDesignator())//
                        .setBulletinNumber(value.getBulletinNumber())//
                        .setType(value.getType())//
                        .setBulletinAugmentationNumber(value.getBulletinAugmentationNumber())//
                        .setSIGMETType(value.getSIGMETType());
            }

        }

        @Override
        public Builder setType(final Type type) {
            if (Type.NORMAL == type || Type.CORRECTED == type) {
                return super.setType(type);
            } else {
                throw new IllegalArgumentException("SIGMET bulletins can only be of type " + Type.NORMAL + " or " + Type.CORRECTED);
            }
        }
    }

}
