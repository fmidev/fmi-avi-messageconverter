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
@JsonPropertyOrder({ "locationIndicator", "geographicalDesignator", "bulletinNumber", "type", "bulletinAugmentationNumber", "dataTypeDesignatorT2" })
public abstract class SIGMETBulletinHeadingImpl implements SIGMETBulletinHeading {

    public static Builder builder() {
        return new Builder();
    }

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

        @Deprecated
        public Builder() {
            setType(Type.NORMAL);
        }

        public static Builder from(final SIGMETBulletinHeading value) {
            if (value instanceof SIGMETBulletinHeadingImpl) {
                return ((SIGMETBulletinHeadingImpl) value).toBuilder();
            } else {
                return SIGMETBulletinHeadingImpl.builder()//
                        .setLocationIndicator(value.getLocationIndicator())//
                        .setGeographicalDesignator(value.getGeographicalDesignator())//
                        .setBulletinNumber(value.getBulletinNumber())//
                        .setType(value.getType())//
                        .setBulletinAugmentationNumber(value.getBulletinAugmentationNumber())//
                        .setDataTypeDesignatorT2(value.getDataTypeDesignatorT2());
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

        @Override
        public Builder setDataTypeDesignatorT2(final WarningsDataTypeDesignatorT2 t2) {
            if (t2 != WarningsDataTypeDesignatorT2.SIGMET && t2 != WarningsDataTypeDesignatorT2.TROPICAL_CYCLONE_SIGMET && t2 != WarningsDataTypeDesignatorT2.VOLCANIC_ASH_CLOUDS_SIGMET) {
                throw new IllegalArgumentException("Value for t2 for SIGMETBulletin must be " + WarningsDataTypeDesignatorT2.SIGMET + ", " + WarningsDataTypeDesignatorT2.TROPICAL_CYCLONE_SIGMET + ", or " + WarningsDataTypeDesignatorT2.VOLCANIC_ASH_CLOUDS_SIGMET);
            }
            return super.setDataTypeDesignatorT2(t2);
        }

        @Deprecated
        public Builder setSIGMETType(final SIGMETType type) {
            switch (type) {
                case SEVERE_WEATHER: return this.setDataTypeDesignatorT2(WarningsDataTypeDesignatorT2.SIGMET);
                case VOLCANIC_ASH: return this.setDataTypeDesignatorT2(WarningsDataTypeDesignatorT2.VOLCANIC_ASH_CLOUDS_SIGMET);
                case TROPICAL_CYCLONE: return this.setDataTypeDesignatorT2(WarningsDataTypeDesignatorT2.TROPICAL_CYCLONE_SIGMET);
                default: throw new IllegalArgumentException("Unknown type");
            }
        }
    }

}
