package fi.fmi.avi.model.taf.immutable;

import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.taf.TAFBulletinHeading;

@FreeBuilder
@JsonDeserialize(builder = TAFBulletinHeadingImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "locationIndicator", "geographicalDesignator", "bulletinNumber", "containingDelayedMessages", "containingAmendedMessages",
        "containingCorrectedMessages", "bulletinAugmentationNumber", "validLessThan12Hours" })
public abstract class TAFBulletinHeadingImpl implements TAFBulletinHeading {
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
            setContainingAmendedMessages(false);
            setContainingCorrectedMessages(false);
            setContainingDelayedMessages(false);
            setBulletinAugmentationNumber(OptionalInt.empty());
        }

        public static Builder from(final TAFBulletinHeading value) {
            if (value instanceof TAFBulletinHeadingImpl) {
                return ((TAFBulletinHeadingImpl) value).toBuilder();
            } else {
                return new TAFBulletinHeadingImpl.Builder().setLocationIndicator(value.getLocationIndicator())
                        .setGeographicalDesignator(value.getGeographicalDesignator())
                        .setBulletinNumber(value.getBulletinNumber())
                        .setContainingDelayedMessages(value.isContainingDelayedMessages())
                        .setContainingAmendedMessages(value.isContainingAmendedMessages())
                        .setContainingCorrectedMessages(value.isContainingCorrectedMessages())
                        .setBulletinAugmentationNumber(value.getBulletinAugmentationNumber())
                        .setValidLessThan12Hours(value.isValidLessThan12Hours());
            }
        }

        public Builder setBulletinAugmentationNumber(final int bulletinAugmentationNumber) {
            if (bulletinAugmentationNumber < 1 || bulletinAugmentationNumber > 26) {
                throw new IllegalArgumentException("Value must be between 1 and 26");
            }
            return super.setBulletinAugmentationNumber(OptionalInt.of(bulletinAugmentationNumber));
        }

        /**
         * Convenience method for setting the bulletin augmentation number as a
         * character between 'A' and 'Z' used in the GTS abbreviated heading syntax.
         *
         * @param asChar
         *
         * @return
         */
        public Builder setBulletinAugmentationNumber(final char asChar) {
            if (!Character.isAlphabetic(asChar) || asChar < 'A' || asChar > 'Z') {
                throw new IllegalArgumentException("Value must be between 'A' and 'Z'");
            }
            return super.setBulletinAugmentationNumber(OptionalInt.of(asChar - 'A' + 1));
        }
    }
}
