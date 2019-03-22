package fi.fmi.avi.model.immutable;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import fi.fmi.avi.model.BulletinHeading;
import fi.fmi.avi.model.PartialDateTime;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;

@FreeBuilder
@JsonDeserialize(builder = BulletinHeadingImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "locationIndicator", "geographicalDesignator", "bulletinNumber", "type", "bulletinAugmentationNumber",
        "dataTypeDesignatorT2" })
public abstract class BulletinHeadingImpl implements BulletinHeading, Serializable {
    private static final Pattern ABBREVIATED_HEADING = Pattern.compile(
            "^(?<TT>[A-Z]{2})(?<AA>[A-Z]{2})(?<ii>[0-9]{2})" + "(?<CCCC>[A-Z]{4})(?<YY>[0-9]{2})(?<GG>[0-9]{2})(?<gg>[0-9]{2})(?<BBB>(CC|RR|AA)[A-Z])?$");

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

    @Override
    @JsonIgnore
    public abstract DataTypeDesignatorT1 getDataTypeDesignatorT1ForTAC();

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
                        .setDataTypeDesignatorT1ForTAC(value.getDataTypeDesignatorT1ForTAC()).setIssueTime(value.getIssueTime());
            }
        }

        public static Builder from(final String abbreviatedHeading) {
            Matcher m = ABBREVIATED_HEADING.matcher(abbreviatedHeading);
            if (!m.matches()) {
                throw new IllegalArgumentException(
                        "String '" + abbreviatedHeading + "' does not match the Abbreviated heading format " + "'T1T2A1A2iiCCCCYYGGgg[BBB]' as defined in "
                                + "WMO-No. 386 Manual on the Global Telecommunication System, 2015 edition (updated 2017)");
            }
            final String bbb = m.group("BBB");
            BulletinHeading.Type type = Type.NORMAL;
            Integer bulletinAugmentationNumber = null;
            if (bbb != null) {
                type = Type.fromCode(bbb.substring(0, 2));
                bulletinAugmentationNumber = bbb.charAt(2) - 'A' + 1;
            }
            DataTypeDesignatorT2 t2 = null;
            final DataTypeDesignatorT1 t1 = DataTypeDesignatorT1.fromCode(m.group("TT").charAt(0));
            if (DataTypeDesignatorT1.FORECASTS == t1) {
                t2 = ForecastsDataTypeDesignatorT2.fromCode(m.group("TT").charAt(1));
            } else if (DataTypeDesignatorT1.WARNINGS == t1) {
                t2 = WarningsDataTypeDesignatorT2.fromCode(m.group("TT").charAt(1));
            } else if (DataTypeDesignatorT1.AVIATION_INFORMATION_IN_XML == t1) {
                t2 = XMLDataTypeDesignatorT2.fromCode(m.group("TT").charAt(1));
            } else {
                throw new IllegalArgumentException("Only forecast ('F') and warning ('W') type headings currently supported, t1 is '" + t1 + "'");
            }
            String issueTime = "--" + m.group("YY") + "T" + m.group("GG") + ":" + m.group("gg");
            return new Builder()//
                    .setLocationIndicator(m.group("CCCC"))//
                    .setGeographicalDesignator(m.group("AA"))//
                    .setBulletinNumber(Integer.parseInt(m.group("ii")))//
                    .setType(type)//
                    .setBulletinAugmentationNumber(Optional.ofNullable(bulletinAugmentationNumber))//
                    .setDataTypeDesignatorT1ForTAC(t1)//
                    .setDataTypeDesignatorT2(t2).setIssueTime(PartialOrCompleteTimeInstant.of(PartialDateTime.parse(issueTime)));
        }

        @Override
        public Builder setBulletinAugmentationNumber(final int bulletinAugmentationNumber) {
            if (bulletinAugmentationNumber < 1 || bulletinAugmentationNumber > 26) {
                throw new IllegalArgumentException("Value must be between 1 and 26, value was " + bulletinAugmentationNumber);
            }
            return super.setBulletinAugmentationNumber(Integer.valueOf(bulletinAugmentationNumber));
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
            return super.setBulletinAugmentationNumber(Integer.valueOf(asChar - 'A' + 1));
        }

        @Override
        public Builder setBulletinNumber(final int bulletinNumber) {
            if (bulletinNumber < 1 || bulletinNumber > 99) {
                throw new IllegalArgumentException("Bulleting number must be between 1 and 99");
            }
            return super.setBulletinNumber(bulletinNumber);
        }

        @Override
        @JsonDeserialize(using = DataTypeDesignatorT2Deserializer.class)
        public Builder setDataTypeDesignatorT2(final DataTypeDesignatorT2 t2) {
            if (t2 instanceof ForecastsDataTypeDesignatorT2) {
                this.setDataTypeDesignatorT1ForTAC(DataTypeDesignatorT1.FORECASTS);
            } else if (t2 instanceof WarningsDataTypeDesignatorT2) {
                this.setDataTypeDesignatorT1ForTAC(DataTypeDesignatorT1.WARNINGS);
            } else if (t2 instanceof XMLDataTypeDesignatorT2) {
                this.setDataTypeDesignatorT1ForTAC(DataTypeDesignatorT1.AVIATION_INFORMATION_IN_XML);
            }
            return super.setDataTypeDesignatorT2(t2);
        }

        static class DataTypeDesignatorT2Deserializer extends StdDeserializer<DataTypeDesignatorT2> {

            DataTypeDesignatorT2Deserializer() {
                this(null);
            }

            DataTypeDesignatorT2Deserializer(final Class<?> vc) {
                super(vc);
            }

            @Override
            public DataTypeDesignatorT2 deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
                final String value = ((JsonNode) jsonParser.getCodec().readTree(jsonParser)).asText();
                for (final ForecastsDataTypeDesignatorT2 item : ForecastsDataTypeDesignatorT2.values()) {
                    if (item.name().equals(value)) {
                        return item;
                    }
                }
                for (final WarningsDataTypeDesignatorT2 item : WarningsDataTypeDesignatorT2.values()) {
                    if (item.name().equals(value)) {
                        return item;
                    }
                }
                for (final XMLDataTypeDesignatorT2 item : XMLDataTypeDesignatorT2.values()) {
                    if (item.name().equals(value)) {
                        return item;
                    }
                }
                throw new IllegalArgumentException("Invalid value '" + value + "' for DataTypeDesignatorT2");
            }
        }
    }
}
