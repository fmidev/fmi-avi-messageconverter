package fi.fmi.avi.model.bulletin.immutable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.model.MessageType;
import fi.fmi.avi.model.bulletin.BulletinHeading;
import fi.fmi.avi.model.bulletin.DataTypeDesignatorT1;
import fi.fmi.avi.util.BulletinHeadingDecoder;
import fi.fmi.avi.util.BulletinHeadingEncoder;
import org.immutables.value.Value;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

@Value.Immutable
@JsonDeserialize(builder = BulletinHeadingImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"geographicalDesignator", "locationIndicator", "bulletinNumber", "bulletinAugmentationNumber", "issueTime", "type",
        "dataTypeDesignatorT1ForTAC", "dataTypeDesignatorT2"})
public abstract class BulletinHeadingImpl implements BulletinHeading, Serializable {
    private static final long serialVersionUID = -7537001968102122857L;

    public static Builder builder() {
        return new Builder();
    }

    public static BulletinHeadingImpl immutableCopyOf(final BulletinHeading heading) {
        Objects.requireNonNull(heading);
        if (heading instanceof BulletinHeadingImpl) {
            return (BulletinHeadingImpl) heading;
        } else {
            return Builder.copyOf(heading).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<BulletinHeadingImpl> immutableCopyOf(final Optional<BulletinHeading> heading) {
        return heading.map(BulletinHeadingImpl::immutableCopyOf);
    }

    /**
     * Tries to determine the intended message type from the bulletin heading.
     * <p>
     * Not detected due to unambiguous use in practice:
     * <dl>
     * <dt>UA</dt> <dd>could be either SPECIAL_AIR_REPORT or WXREP</dd>
     * <dt>UX</dt> <dd>could be LOW_WIND or some other misc upper-air data</dd>
     * <dt>WX</dt> <dd>could be WX_WRNG or some other misc warning</dd>
     * <dt>FX</dt> <dd>could be LOW_WIND or some other misc forecast</dd>
     * <dt>FV</dt> <dd>could be either VOLCANIC_ASH_ADVISORY or SIGMET</dd>
     * </dl>
     *
     * @return The message type, if one can be unambiguously determined
     */
    @Override
    public Optional<MessageType> getExpectedContainedMessageType() {
        final fi.fmi.avi.model.bulletin.DataTypeDesignatorT2 t2 = this.getDataTypeDesignatorT2();
        if (t2 != null) {
            return t2.getExpectedMessageType();
        } else {
            return Optional.empty();
        }
    }

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    /**
     * Derived from {@link #getDataTypeDesignatorT2()} unless explicitly set - see
     * {@code Builder.setDataTypeDesignatorT2(...)}'s FreeBuilder-era side effect, replaced by this
     * {@code @Value.Default} (Immutables' generated setters are final and this class's setter for
     * this property can no longer derive it as a side effect of setting T2 - see
     * docs/07-modernization-plan.md).
     */
    @Override
    @Value.Default
    public DataTypeDesignatorT1 getDataTypeDesignatorT1ForTAC() {
        return getDataTypeDesignatorT2().getT1()
                .orElseThrow(() -> new IllegalStateException(
                        "dataTypeDesignatorT1ForTAC was not set explicitly and cannot be derived from dataTypeDesignatorT2 " + getDataTypeDesignatorT2()));
    }

    public static class Builder extends ImmutableBulletinHeadingImpl.Builder {

        Builder() {
            setType(Type.NORMAL);
            setOriginalAugmentationIndicator("");
        }

        public static Builder copyOf(final BulletinHeading value) {
            if (value instanceof BulletinHeadingImpl) {
                return ((BulletinHeadingImpl) value).toBuilder();
            } else {
                return new BulletinHeadingImpl.Builder()//
                        .setLocationIndicator(value.getLocationIndicator())//
                        .setGeographicalDesignator(value.getGeographicalDesignator())//
                        .setBulletinNumber(value.getBulletinNumber())//
                        .setType(value.getType())//
                        .setAugmentationNumber(value.getAugmentationNumber())//
                        .setOriginalAugmentationIndicator(value.getOriginalAugmentationIndicator())//
                        .setDataTypeDesignatorT2(value.getDataTypeDesignatorT2())//
                        .setDataTypeDesignatorT1ForTAC(value.getDataTypeDesignatorT1ForTAC())//
                        .setIssueTime(value.getIssueTime());
            }
        }

        public static Builder copyOf(final String abbreviatedHeading) {
            return BulletinHeadingImpl.Builder.copyOf(BulletinHeadingDecoder.decode(abbreviatedHeading,
                    new ConversionHints(ConversionHints.KEY_BULLETIN_HEADING_SPACING, ConversionHints.VALUE_BULLETIN_HEADING_SPACING_NONE)));
        }

        /**
         * Convenience method for setting the bulletin augmentation number as a
         * character used in the GTS abbreviated heading syntax.
         *
         * @param asChar character between 'A' and 'Z'
         * @return the builder
         */
        public Builder setAugmentationNumber(final char asChar) {
            if (!Character.isAlphabetic(asChar) || asChar < 'A' || asChar > 'Z') {
                throw new IllegalArgumentException("Value must be between 'A' and 'Z'");
            }
            return super.setAugmentationNumber(asChar - 'A' + 1);
        }

        /**
         * Convenience method for setting the bulletin heading type, augmentation number and
         * the indicator string encoded from the given type and augmentation number.
         *
         * @param type               type
         * @param augmentationNumber augmentation number
         * @return the builder
         */
        public Builder setAugmentationIndicator(final Type type, final int augmentationNumber) {
            return setAugmentationNumber(augmentationNumber)
                    .setType(type)
                    .setOriginalAugmentationIndicator(BulletinHeadingEncoder.encodeBBBIndicator(type, augmentationNumber));
        }

        /*
         * FreeBuilder-era code validated setAugmentationNumber(int)'s and setBulletinNumber(int)'s
         * range as soon as each was set. Immutables' generated setters are final and cannot be
         * overridden (see docs/07-modernization-plan.md), so both checks moved here, into build(),
         * validating the finished value instead of eagerly on set(). The setDataTypeDesignatorT2(...)
         * override that used to derive dataTypeDesignatorT1ForTAC as a side effect is gone entirely -
         * that derivation is now a @Value.Default on getDataTypeDesignatorT1ForTAC() above, which is
         * the Immutables-native way to express "derived from another property unless explicitly set".
         */
        @Override
        public ImmutableBulletinHeadingImpl build() {
            final ImmutableBulletinHeadingImpl result = super.build();
            final int augmentationNumber = result.getAugmentationNumber().orElse(1);
            if (augmentationNumber < 1 || augmentationNumber > 26) {
                throw new IllegalArgumentException("Value must be between 1 and 26, value was " + augmentationNumber);
            }
            final int bulletinNumber = result.getBulletinNumber();
            if (bulletinNumber < 0 || bulletinNumber > 99) {
                throw new IllegalArgumentException("Bulletin number must be between 0 and 99");
            }
            return result;
        }
    }
}
