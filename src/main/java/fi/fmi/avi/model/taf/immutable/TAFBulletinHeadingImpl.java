package fi.fmi.avi.model.taf.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.taf.TAFBulletinHeading;

@FreeBuilder
@JsonDeserialize(builder = TAFBulletinHeadingImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "locationIndicator", "geographicalDesignator", "bulletinNumber", "type", "bulletinAugmentationNumber", "dataTypeDesignatorT2" })
public abstract class TAFBulletinHeadingImpl implements TAFBulletinHeading, Serializable {

    public static TAFBulletinHeadingImpl immutableCopyOf(final TAFBulletinHeading heading) {
        Objects.requireNonNull(heading);
        if (heading instanceof TAFBulletinHeadingImpl) {
            return (TAFBulletinHeadingImpl) heading;
        } else {
            return Builder.from(heading).build();
        }
    }

    public static Optional<TAFBulletinHeadingImpl> immutableCopyOf(final Optional<TAFBulletinHeading> heading) {
        return heading.map(TAFBulletinHeadingImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends TAFBulletinHeadingImpl_Builder {

        public Builder() {
            setType(Type.NORMAL);
        }

        public static Builder from(final TAFBulletinHeading value) {
            if (value instanceof TAFBulletinHeadingImpl) {
                return ((TAFBulletinHeadingImpl) value).toBuilder();
            } else {
                return new TAFBulletinHeadingImpl.Builder()//
                        .setLocationIndicator(value.getLocationIndicator())//
                        .setGeographicalDesignator(value.getGeographicalDesignator())//
                        .setBulletinNumber(value.getBulletinNumber())//
                        .setType(value.getType())//
                        .setBulletinAugmentationNumber(value.getBulletinAugmentationNumber())//
                        .setDataTypeDesignatorT2(value.getDataTypeDesignatorT2());
            }
        }

        @Override
        public Builder setBulletinAugmentationNumber(final int bulletinAugmentationNumber) {
            if (bulletinAugmentationNumber < 1 || bulletinAugmentationNumber > 26) {
                throw new IllegalArgumentException("Value must be between 1 and 26");
            }
            return super.setBulletinAugmentationNumber(Integer.valueOf(bulletinAugmentationNumber));
        }

        /**
         * Convenience method for setting the bulletin augmentation number as a
         * character used in the GTS abbreviated heading syntax.
         *
         * @param asChar character between 'A' and 'Z'
         *
         * @return the builder
         */
        public Builder setBulletinAugmentationNumber(final char asChar) {
            if (!Character.isAlphabetic(asChar) || asChar < 'A' || asChar > 'Z') {
                throw new IllegalArgumentException("Value must be between 'A' and 'Z'");
            }
            return super.setBulletinAugmentationNumber(Integer.valueOf(asChar - 'A' + 1));
        }

        @Override
        public Builder setDataTypeDesignatorT2(final ForecastsDataTypeDesignatorT2 t2) {
            if (t2 != ForecastsDataTypeDesignatorT2.AERODROME_VT_SHORT && t2 != ForecastsDataTypeDesignatorT2.AERODROME_VT_LONG) {
                throw new IllegalArgumentException("Value for T2 for TAFBulletin must be either " + ForecastsDataTypeDesignatorT2.AERODROME_VT_SHORT + " or " +ForecastsDataTypeDesignatorT2.AERODROME_VT_LONG);
            }
            return super.setDataTypeDesignatorT2(t2);
        }

        @Override
        public Builder setGeographicalDesignator(final String designator) {
            if (designator != null && designator.length() != 2) {
                throw new IllegalArgumentException("Geographical designator must be a string with length 2");
            }
            return super.setGeographicalDesignator(designator);
        }

        @Deprecated
        public Builder setValidLessThan12Hours(final boolean isShort) {
            if (isShort) {
                return this.setDataTypeDesignatorT2(ForecastsDataTypeDesignatorT2.AERODROME_VT_SHORT);
            } else {
                return this.setDataTypeDesignatorT2(ForecastsDataTypeDesignatorT2.AERODROME_VT_LONG);
            }
        }
    }

}
