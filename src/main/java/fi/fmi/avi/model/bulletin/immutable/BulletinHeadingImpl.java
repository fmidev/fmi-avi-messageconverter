package fi.fmi.avi.model.bulletin.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.model.MessageType;
import fi.fmi.avi.model.bulletin.BulletinHeading;
import fi.fmi.avi.model.bulletin.DataTypeDesignatorT1;
import fi.fmi.avi.util.BulletinHeadingDecoder;

@FreeBuilder
@JsonDeserialize(builder = BulletinHeadingImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "geographicalDesignator", "locationIndicator", "bulletinNumber", "bulletinAugmentationNumber", "issueTime", "type",
        "dataTypeDesignatorT1ForTAC", "dataTypeDesignatorT2" })
public abstract class BulletinHeadingImpl implements BulletinHeading, Serializable {
    private static final Pattern ABBREVIATED_HEADING = Pattern.compile(
            "^(?<TT>[A-Z]{2})(?<AA>[A-Z]{2})(?<ii>[0-9]{2})" + "(?<CCCC>[A-Z]{4})(?<YY>[0-9]{2})(?<GG>[0-9]{2})(?<gg>[0-9]{2})(?<BBB>(CC|RR|AA)[A-Z])?$");
    private static final long serialVersionUID = -7537001968102122857L;

    public static Builder builder() {
        return new Builder();
    }

    public static BulletinHeadingImpl immutableCopyOf(final BulletinHeading heading) {
        Objects.requireNonNull(heading);
        if (heading instanceof BulletinHeadingImpl) {
            return (BulletinHeadingImpl) heading;
        } else {
            return Builder.from(heading).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<BulletinHeadingImpl> immutableCopyOf(final Optional<BulletinHeading> heading) {
        return heading.map(BulletinHeadingImpl::immutableCopyOf);
    }

    /**
     * Tries to determine the intended message type from the bulletin heading.
     *
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

    public abstract Builder toBuilder();

    public static class Builder extends BulletinHeadingImpl_Builder {

        @Deprecated
        public Builder() {
            setType(Type.NORMAL);
        }

        public static Builder from(final BulletinHeading value) {
            if (value instanceof BulletinHeadingImpl) {
                return ((BulletinHeadingImpl) value).toBuilder();
            } else {
                return new BulletinHeadingImpl.Builder()//
                        .setLocationIndicator(value.getLocationIndicator())//
                        .setGeographicalDesignator(value.getGeographicalDesignator())//
                        .setBulletinNumber(value.getBulletinNumber())//
                        .setType(value.getType())//
                        .setBulletinAugmentationNumber(value.getBulletinAugmentationNumber())//
                        .setDataTypeDesignatorT2(value.getDataTypeDesignatorT2())//
                        .setDataTypeDesignatorT1ForTAC(value.getDataTypeDesignatorT1ForTAC())//
                        .setIssueTime(value.getIssueTime());
            }
        }

        public static Builder from(final String abbreviatedHeading) {
            return BulletinHeadingImpl.Builder.from(BulletinHeadingDecoder.decode(abbreviatedHeading,
                    new ConversionHints(ConversionHints.KEY_BULLETIN_HEADING_SPACING, ConversionHints.VALUE_BULLETIN_HEADING_SPACING_NONE)));
        }

        @Override
        public Builder setBulletinAugmentationNumber(final int bulletinAugmentationNumber) {
            if (bulletinAugmentationNumber < 1 || bulletinAugmentationNumber > 26) {
                throw new IllegalArgumentException("Value must be between 1 and 26, value was " + bulletinAugmentationNumber);
            }
            return super.setBulletinAugmentationNumber(bulletinAugmentationNumber);
        }

        /**
         * Convenience method for setting the bulletin augmentation number as a
         * character used in the GTS abbreviated heading syntax.
         *
         * @param asChar
         *         character between 'A' and 'Z'
         *
         * @return the builder
         */
        public Builder setBulletinAugmentationNumber(final char asChar) {
            if (!Character.isAlphabetic(asChar) || asChar < 'A' || asChar > 'Z') {
                throw new IllegalArgumentException("Value must be between 'A' and 'Z'");
            }
            return super.setBulletinAugmentationNumber(asChar - 'A' + 1);
        }

        @Override
        public Builder setBulletinNumber(final int bulletinNumber) {
            if (bulletinNumber < 0 || bulletinNumber > 99) {
                throw new IllegalArgumentException("Bulletin number must be between 0 and 99");
            }
            return super.setBulletinNumber(bulletinNumber);
        }

        @Override
        public Builder setDataTypeDesignatorT2(final fi.fmi.avi.model.bulletin.DataTypeDesignatorT2 t2) {
            final Optional<DataTypeDesignatorT1> t1 = t2.getT1();
            t1.ifPresent(this::setDataTypeDesignatorT1ForTAC);
            return super.setDataTypeDesignatorT2(t2);
        }

        @Override
        public Builder setDataTypeDesignatorT1ForTAC(final DataTypeDesignatorT1 t1) {
            return super.setDataTypeDesignatorT1ForTAC(t1);
        }

    }
}
