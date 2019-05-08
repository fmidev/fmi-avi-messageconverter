package fi.fmi.avi.model.immutable;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.BulletinHeading;
import fi.fmi.avi.util.BulletinHeadingDecoder;

@FreeBuilder
@JsonDeserialize(builder = BulletinHeadingImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "geographicalDesignator", "locationIndicator", "bulletinNumber",  "bulletinAugmentationNumber",
        "issueTime", "type", "dataTypeDesignatorT1ForTAC", "dataTypeDesignatorT2" })
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
     *
     * Tries to determine the intended message type from the bulletin heading.
     *
     * Not detected due to unambiguous use in practice:
     *    * UA: could be either SPECIAL_AIR_REPORT or WXREP
     *    * UX: could be LOW_WIND or some other misc upper-air data
     *    * WX: could be WX_WRNG or some other misc warning
     *    * FX: could be LOW_WIND or some other misc forecast
     *    * FV: could be either VOLCANIC_ASH_ADVISORY or SIGMET
     *
     * @return The message type, if one can be unambiguously determined
     */
    @Override
    public Optional<AviationCodeListUser.MessageType> getExpectedContainedMessageType() {
        AviationCodeListUser.MessageType retval = null;
        DataTypeDesignatorT1 t1 = this.getDataTypeDesignatorT1ForTAC();
        DataTypeDesignatorT2 t2 = this.getDataTypeDesignatorT2();
        if (DataTypeDesignatorT1.FORECASTS == t1) {
            switch ((ForecastsDataTypeDesignatorT2)t2) {
                case FCT_AERODROME_VT_LONG:
                case FCT_AERODROME_VT_SHORT:
                    retval = AviationCodeListUser.MessageType.TAF;
                    break;
                case FCT_SPACE_WEATHER:
                    retval = AviationCodeListUser.MessageType.SPACE_WEATHER_ADVISORY;
                    break;
                    /*
                case FCT_VOLCANIC_ASH_ADVISORIES:
                    retval = AviationCodeListUser.MessageType.VOLCANIC_ASH_ADVISORY;
                    break;
                    */
                case FCT_TROPICAL_CYCLONE_ADVISORIES:
                    retval = AviationCodeListUser.MessageType.TROPICAL_CYCLONE_ADVISORY;
                    break;
                case FCT_UPPER_AIR_WINDS_AND_TEMPERATURES:
                    retval = AviationCodeListUser.MessageType.GAFOR;
            }
        } else if (DataTypeDesignatorT1.WARNINGS == t1) {
            switch ((WarningsDataTypeDesignatorT2)t2) {
                case WRN_SIGMET:
                case WRN_TROPICAL_CYCLONE_SIGMET:
                case WRN_VOLCANIC_ASH_CLOUDS_SIGMET:
                    retval = AviationCodeListUser.MessageType.SIGMET;
                    break;
                case WRN_AIRMET:
                    retval = AviationCodeListUser.MessageType.AIRMET;
                    break;
            }
        } else if (DataTypeDesignatorT1.AVIATION_INFORMATION_IN_XML == t1) {
            switch ((XMLDataTypeDesignatorT2)t2) {
                case XML_METAR:
                    retval = AviationCodeListUser.MessageType.METAR;
                    break;
                case XML_SPECI:
                    retval = AviationCodeListUser.MessageType.SPECI;
                    break;
                case XML_AIRMET:
                    retval = AviationCodeListUser.MessageType.AIRMET;
                    break;
                case XML_SIGMET:
                case XML_VOLCANIC_ASH_SIGMET:
                case XML_TROPICAL_CYCLONE_SIGMET:
                    retval = AviationCodeListUser.MessageType.SIGMET;
                    break;
                case XML_AERODROME_VT_LONG:
                case XML_AERODROME_VT_SHORT:
                    retval = AviationCodeListUser.MessageType.TAF;
                    break;
                case XML_VOLCANIC_ASH_ADVISORY:
                    retval = AviationCodeListUser.MessageType.VOLCANIC_ASH_ADVISORY;
                    break;
                case XML_TROPICAL_CYCLONE_ADVISORIES:
                    retval = AviationCodeListUser.MessageType.TROPICAL_CYCLONE_ADVISORY;
                    break;
            }
        } /*
        else if (DataTypeDesignatorT1.UPPER_AIR_DATA == t1) {
            switch ((UpperAirDataTypeDesignatorT2)t2) {
                case UA_AIRCRAFT_REPORT_CODAR_AIREP:
                    retval = AviationCodeListUser.MessageType.SPECIAL_AIR_REPORT;
                    retval = AviationCodeListUser.MessageType.WXREP;
            }
        } */else if (DataTypeDesignatorT1.SURFACE_DATA == t1) {
            switch ((SurfaceDataTypeDesignatorT2)t2) {
                case SD_AVIATION_ROUTINE_REPORTS:
                    retval = AviationCodeListUser.MessageType.METAR;
                    break;
                case SD_SPECIAL_AVIATION_WEATHER_REPORTS:
                    retval = AviationCodeListUser.MessageType.SPECI;

            }
        }
        return Optional.ofNullable(retval);
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
                        .setDataTypeDesignatorT1ForTAC(value.getDataTypeDesignatorT1ForTAC()).setIssueTime(value.getIssueTime());
            }
        }

        public static Builder from(final String abbreviatedHeading) {
            return BulletinHeadingImpl.Builder.from(BulletinHeadingDecoder.decode(abbreviatedHeading, new ConversionHints(ConversionHints
                    .KEY_BULLETIN_HEADING_SPACING, ConversionHints.VALUE_BULLETIN_HEADING_SPACING_NONE)));
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
            } else if (t2 instanceof UpperAirDataTypeDesignatorT2) {
                this.setDataTypeDesignatorT1ForTAC(DataTypeDesignatorT1.UPPER_AIR_DATA);
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
                for (final UpperAirDataTypeDesignatorT2 item : UpperAirDataTypeDesignatorT2.values()) {
                    if (item.name().equals(value)) {
                        return item;
                    }
                }
                throw new IllegalArgumentException("Invalid value '" + value + "' for DataTypeDesignatorT2");
            }
        }
    }
}
