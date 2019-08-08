package fi.fmi.avi.model.bulletin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import fi.fmi.avi.model.MessageType;

@JsonSerialize(using = DataTypeDesignatorT2.DataTypeDesignatorT2Serializer.class)
@JsonDeserialize(using = DataTypeDesignatorT2.DataTypeDesignatorT2Deserializer.class)
public class DataTypeDesignatorT2 implements DataTypeDesignator {
    protected static final Map<DataTypeDesignatorT2, MessageType> t2ToContainedMessageType = new HashMap<>();

    private static final Pattern EXTENSION_NAME_PATTERN = Pattern.compile("^EXTENSION_(?<code>[a-zA-Z])$");

    private final char code;

    protected DataTypeDesignatorT2(final char code) {
        this.code = code;
    }

    public static Optional<? extends DataTypeDesignatorT2> fromName(final String name) {
        final Matcher m = EXTENSION_NAME_PATTERN.matcher(name);
        if (m.matches()) {
            return Optional.of(new DataTypeDesignatorT2(m.group("code").charAt(0)));
        }
        Optional<? extends DataTypeDesignatorT2> designator;
        designator = DataTypeDesignatorT2.ForecastsDataTypeDesignatorT2.fromName(name);
        if (!designator.isPresent()) {
            designator = DataTypeDesignatorT2.WarningsDataTypeDesignatorT2.fromName(name);
        }
        if (!designator.isPresent()) {
            designator = DataTypeDesignatorT2.XMLDataTypeDesignatorT2.fromName(name);
        }
        if (!designator.isPresent()) {
            designator = DataTypeDesignatorT2.UpperAirDataTypeDesignatorT2.fromName(name);
        }
        if (!designator.isPresent()) {
            designator = DataTypeDesignatorT2.SurfaceDataTypeDesignatorT2.fromName(name);
        }
        return designator;
    }

    public static DataTypeDesignatorT2 fromExtensionCode(final char code) {
        return new DataTypeDesignatorT2(code);
    }

    public Optional<DataTypeDesignatorT1> getT1() {
        return Optional.empty();
    }

    @Override
    public char code() {
        return code;
    }

    @Override
    public String name() {
        return "EXTENSION_" + code();
    }

    public String toString() {
        return name();
    }

    public Optional<MessageType> getExpectedMessageType() {
        return Optional.ofNullable(t2ToContainedMessageType.get(this));
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DataTypeDesignatorT2) {
            return this.name().equals(((DataTypeDesignatorT2) obj).name());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return name().hashCode();
    }

    public static class UpperAirDataTypeDesignatorT2 extends DataTypeDesignatorT2 {
        public static final UpperAirDataTypeDesignatorT2 UA_AIRCRAFT_REPORT_CODAR_AIREP = new UpperAirDataTypeDesignatorT2('A');
        public static final UpperAirDataTypeDesignatorT2 UA_AIRCRAFT_REPORT_AMDAR = new UpperAirDataTypeDesignatorT2('D');
        public static final UpperAirDataTypeDesignatorT2 UA_PRESSURE_TEMPERATURE_HUMIDITY_WIND_PART_D = new UpperAirDataTypeDesignatorT2('E');
        public static final UpperAirDataTypeDesignatorT2 UA_PRESSURE_TEMPERATURE_HUMIDITY_WIND_PARTS_C_D = new UpperAirDataTypeDesignatorT2('F');
        public static final UpperAirDataTypeDesignatorT2 UA_WIND_PART_B = new UpperAirDataTypeDesignatorT2('G');
        public static final UpperAirDataTypeDesignatorT2 UA_WIND_PART_C = new UpperAirDataTypeDesignatorT2('H');
        public static final UpperAirDataTypeDesignatorT2 UA_WIND_PARTS_A_B = new UpperAirDataTypeDesignatorT2('I');
        public static final UpperAirDataTypeDesignatorT2 UA_PRESSURE_TEMPERATURE_HUMIDITY_WIND_PART_B = new UpperAirDataTypeDesignatorT2('K');
        public static final UpperAirDataTypeDesignatorT2 UA_PRESSURE_TEMPERATURE_HUMIDITY_WIND_PART_C = new UpperAirDataTypeDesignatorT2('L');
        public static final UpperAirDataTypeDesignatorT2 UA_ROCKETSONDE_REPORT = new UpperAirDataTypeDesignatorT2('N');
        public static final UpperAirDataTypeDesignatorT2 UA_WIND_PART_A = new UpperAirDataTypeDesignatorT2('P');
        public static final UpperAirDataTypeDesignatorT2 UA_WIND_PART_D = new UpperAirDataTypeDesignatorT2('Q');
        public static final UpperAirDataTypeDesignatorT2 UA_AIRCRAFT_REPORT_RECCO = new UpperAirDataTypeDesignatorT2('R');
        public static final UpperAirDataTypeDesignatorT2 UA_PRESSURE_TEMPERATURE_HUMIDITY_WIND_PART_A = new UpperAirDataTypeDesignatorT2('S');
        public static final UpperAirDataTypeDesignatorT2 UA_AIRCRAFT_REPORT_CODAR = new UpperAirDataTypeDesignatorT2('T');
        public static final UpperAirDataTypeDesignatorT2 UA_MISCELLANEOUS = new UpperAirDataTypeDesignatorT2('X');
        public static final UpperAirDataTypeDesignatorT2 UA_WIND_PARTS_C_D = new UpperAirDataTypeDesignatorT2('Y');
        public static final UpperAirDataTypeDesignatorT2 UA_PRESSURE_TEMPERATURE_HUMIDITY_WIND_PARTS_A_B_C_D = new UpperAirDataTypeDesignatorT2('Z');

        private static final AutoReflectionDataTypeDesignatorMapping<UpperAirDataTypeDesignatorT2> MAPPING = new AutoReflectionDataTypeDesignatorMapping<>(
                UpperAirDataTypeDesignatorT2.class);

        UpperAirDataTypeDesignatorT2(final char code) {
            super(code);
        }

        public static Optional<UpperAirDataTypeDesignatorT2> fromCode(final char code) {
            return MAPPING.getOptionalDesignatorByCode(code);
        }

        public static Optional<UpperAirDataTypeDesignatorT2> fromName(final String name) {
            return MAPPING.getOptionalDesignatorByName(name);
        }

        @Override
        public String name() {
            return MAPPING.getDesignatorName(this.code());
        }
    }

    public static class XMLDataTypeDesignatorT2 extends DataTypeDesignatorT2 {
        public static final XMLDataTypeDesignatorT2 XML_METAR = new XMLDataTypeDesignatorT2('A');
        public static final XMLDataTypeDesignatorT2 XML_AERODROME_VT_SHORT = new XMLDataTypeDesignatorT2('C');
        public static final XMLDataTypeDesignatorT2 XML_TROPICAL_CYCLONE_ADVISORIES = new XMLDataTypeDesignatorT2('K');
        public static final XMLDataTypeDesignatorT2 XML_SPECI = new XMLDataTypeDesignatorT2('P');
        public static final XMLDataTypeDesignatorT2 XML_SIGMET = new XMLDataTypeDesignatorT2('S');
        public static final XMLDataTypeDesignatorT2 XML_AERODROME_VT_LONG = new XMLDataTypeDesignatorT2('T');
        public static final XMLDataTypeDesignatorT2 XML_VOLCANIC_ASH_ADVISORY = new XMLDataTypeDesignatorT2('U');
        public static final XMLDataTypeDesignatorT2 XML_VOLCANIC_ASH_SIGMET = new XMLDataTypeDesignatorT2('V');
        public static final XMLDataTypeDesignatorT2 XML_AIRMET = new XMLDataTypeDesignatorT2('W');
        public static final XMLDataTypeDesignatorT2 XML_TROPICAL_CYCLONE_SIGMET = new XMLDataTypeDesignatorT2('Y');

        private static final AutoReflectionDataTypeDesignatorMapping<XMLDataTypeDesignatorT2> MAPPING = new AutoReflectionDataTypeDesignatorMapping<>(
                XMLDataTypeDesignatorT2.class);

        static {
            t2ToContainedMessageType.put(XML_METAR, MessageType.METAR);
            t2ToContainedMessageType.put(XML_SPECI, MessageType.SPECI);
            t2ToContainedMessageType.put(XML_AIRMET, MessageType.AIRMET);
            t2ToContainedMessageType.put(XML_SIGMET, MessageType.SIGMET);
            t2ToContainedMessageType.put(XML_VOLCANIC_ASH_SIGMET, MessageType.SIGMET);
            t2ToContainedMessageType.put(XML_TROPICAL_CYCLONE_SIGMET, MessageType.SIGMET);
            t2ToContainedMessageType.put(XML_AERODROME_VT_SHORT, MessageType.TAF);
            t2ToContainedMessageType.put(XML_AERODROME_VT_LONG, MessageType.TAF);
            t2ToContainedMessageType.put(XML_VOLCANIC_ASH_ADVISORY, MessageType.VOLCANIC_ASH_ADVISORY);
            t2ToContainedMessageType.put(XML_TROPICAL_CYCLONE_ADVISORIES, MessageType.VOLCANIC_ASH_ADVISORY);
        }

        XMLDataTypeDesignatorT2(final char code) {
            super(code);
        }

        public static Optional<XMLDataTypeDesignatorT2> fromCode(final char code) {
            return MAPPING.getOptionalDesignatorByCode(code);
        }

        public static Optional<XMLDataTypeDesignatorT2> fromName(final String name) {
            return MAPPING.getOptionalDesignatorByName(name);
        }

        @Override
        public Optional<DataTypeDesignatorT1> getT1() {
            return Optional.of(DataTypeDesignatorT1.AVIATION_INFORMATION_IN_XML);
        }

        @Override
        public String name() {
            return MAPPING.getDesignatorName(this.code());
        }
    }

    public static class WarningsDataTypeDesignatorT2 extends DataTypeDesignatorT2 {
        public static final WarningsDataTypeDesignatorT2 WRN_AIRMET = new WarningsDataTypeDesignatorT2('A');
        public static final WarningsDataTypeDesignatorT2 WRN_TROPICAL_CYCLONE_SIGMET = new WarningsDataTypeDesignatorT2('C');
        public static final WarningsDataTypeDesignatorT2 WRN_TSUNAMI = new WarningsDataTypeDesignatorT2('E');
        public static final WarningsDataTypeDesignatorT2 WRN_TORNADO = new WarningsDataTypeDesignatorT2('F');
        public static final WarningsDataTypeDesignatorT2 WRN_HYDROLOGICAL_OR_RIVER_FLOOD = new WarningsDataTypeDesignatorT2('G');
        public static final WarningsDataTypeDesignatorT2 WRN_MARINE_OR_COASTAL_FLOOD = new WarningsDataTypeDesignatorT2('H');
        public static final WarningsDataTypeDesignatorT2 WRN_OTHER = new WarningsDataTypeDesignatorT2('O');
        public static final WarningsDataTypeDesignatorT2 WRN_HUMANITARIAN_ACTIVITIES = new WarningsDataTypeDesignatorT2('R');
        public static final WarningsDataTypeDesignatorT2 WRN_SIGMET = new WarningsDataTypeDesignatorT2('S');
        public static final WarningsDataTypeDesignatorT2 WRN_TROPICAL_CYCLONE_TYPHOON_OR_HURRICANE = new WarningsDataTypeDesignatorT2('T');
        public static final WarningsDataTypeDesignatorT2 WRN_SEVERE_THUNDERSTORM = new WarningsDataTypeDesignatorT2('U');
        public static final WarningsDataTypeDesignatorT2 WRN_VOLCANIC_ASH_CLOUDS_SIGMET = new WarningsDataTypeDesignatorT2('V');
        public static final WarningsDataTypeDesignatorT2 WRN_WARNINGS_AND_WEATHER_SUMMARY = new WarningsDataTypeDesignatorT2('W');

        private static final AutoReflectionDataTypeDesignatorMapping<WarningsDataTypeDesignatorT2> MAPPING = new AutoReflectionDataTypeDesignatorMapping<>(
                WarningsDataTypeDesignatorT2.class);

        static {
            t2ToContainedMessageType.put(WRN_SIGMET, MessageType.SIGMET);
            t2ToContainedMessageType.put(WRN_TROPICAL_CYCLONE_SIGMET, MessageType.SIGMET);
            t2ToContainedMessageType.put(WRN_VOLCANIC_ASH_CLOUDS_SIGMET, MessageType.SIGMET);
            t2ToContainedMessageType.put(WRN_AIRMET, MessageType.AIRMET);
        }

        WarningsDataTypeDesignatorT2(final char code) {
            super(code);
        }

        public static Optional<WarningsDataTypeDesignatorT2> fromCode(final char code) {
            return MAPPING.getOptionalDesignatorByCode(code);
        }

        public static Optional<WarningsDataTypeDesignatorT2> fromName(final String name) {
            return MAPPING.getOptionalDesignatorByName(name);
        }

        @Override
        public Optional<DataTypeDesignatorT1> getT1() {
            return Optional.of(DataTypeDesignatorT1.WARNINGS);
        }

        @Override
        public String name() {
            return MAPPING.getDesignatorName(this.code());
        }
    }

    public static class ForecastsDataTypeDesignatorT2 extends DataTypeDesignatorT2 {
        public static final ForecastsDataTypeDesignatorT2 FCT_AVIATION_AREA_OR_GAMET_OR_ADVISORIES = new ForecastsDataTypeDesignatorT2('A');
        public static final ForecastsDataTypeDesignatorT2 FCT_UPPER_AIR_WINDS_AND_TEMPERATURES = new ForecastsDataTypeDesignatorT2('B');
        public static final ForecastsDataTypeDesignatorT2 FCT_AERODROME_VT_SHORT = new ForecastsDataTypeDesignatorT2('C');
        public static final ForecastsDataTypeDesignatorT2 FCT_RADIOLOGICAL_TRAJECTORY_DOSE = new ForecastsDataTypeDesignatorT2('D');
        public static final ForecastsDataTypeDesignatorT2 FCT_EXTENDED = new ForecastsDataTypeDesignatorT2('E');
        public static final ForecastsDataTypeDesignatorT2 FCT_SHIPPING = new ForecastsDataTypeDesignatorT2('F');
        public static final ForecastsDataTypeDesignatorT2 FCT_HYDROLOGICAL = new ForecastsDataTypeDesignatorT2('G');
        public static final ForecastsDataTypeDesignatorT2 FCT_UPPER_AIR_THICKNESS = new ForecastsDataTypeDesignatorT2('H');
        public static final ForecastsDataTypeDesignatorT2 FCT_ICEBERG = new ForecastsDataTypeDesignatorT2('I');
        public static final ForecastsDataTypeDesignatorT2 FCT_RADIO_WARNING_SERVICE = new ForecastsDataTypeDesignatorT2('J');
        public static final ForecastsDataTypeDesignatorT2 FCT_TROPICAL_CYCLONE_ADVISORIES = new ForecastsDataTypeDesignatorT2('K');
        public static final ForecastsDataTypeDesignatorT2 FCT_LOCAL_OR_AREA = new ForecastsDataTypeDesignatorT2('L');
        public static final ForecastsDataTypeDesignatorT2 FCT_TEMPERATURE_EXTREMES = new ForecastsDataTypeDesignatorT2('M');
        public static final ForecastsDataTypeDesignatorT2 FCT_SPACE_WEATHER = new ForecastsDataTypeDesignatorT2('N');
        public static final ForecastsDataTypeDesignatorT2 FCT_GUIDANCE = new ForecastsDataTypeDesignatorT2('O');
        public static final ForecastsDataTypeDesignatorT2 FCT_PUBLIC = new ForecastsDataTypeDesignatorT2('P');
        public static final ForecastsDataTypeDesignatorT2 FCT_OTHER_SHIPPING = new ForecastsDataTypeDesignatorT2('Q');
        public static final ForecastsDataTypeDesignatorT2 FCT_AVIATION_ROUTE = new ForecastsDataTypeDesignatorT2('R');
        public static final ForecastsDataTypeDesignatorT2 FCT_SURFACE = new ForecastsDataTypeDesignatorT2('S');
        public static final ForecastsDataTypeDesignatorT2 FCT_AERODROME_VT_LONG = new ForecastsDataTypeDesignatorT2('T');
        public static final ForecastsDataTypeDesignatorT2 FCT_UPPER_AIR = new ForecastsDataTypeDesignatorT2('U');
        public static final ForecastsDataTypeDesignatorT2 FCT_VOLCANIC_ASH_ADVISORIES = new ForecastsDataTypeDesignatorT2('V');
        public static final ForecastsDataTypeDesignatorT2 FCT_WINTER_SPORTS = new ForecastsDataTypeDesignatorT2('W');
        public static final ForecastsDataTypeDesignatorT2 FCT_MISCELLANEOUS = new ForecastsDataTypeDesignatorT2('X');
        public static final ForecastsDataTypeDesignatorT2 FCT_SHIPPING_AREA = new ForecastsDataTypeDesignatorT2('Z');

        private static final AutoReflectionDataTypeDesignatorMapping<ForecastsDataTypeDesignatorT2> MAPPING = new AutoReflectionDataTypeDesignatorMapping<>(
                ForecastsDataTypeDesignatorT2.class);

        static {
            t2ToContainedMessageType.put(FCT_AERODROME_VT_LONG, MessageType.TAF);
            t2ToContainedMessageType.put(FCT_AERODROME_VT_SHORT, MessageType.TAF);
            t2ToContainedMessageType.put(FCT_SPACE_WEATHER, MessageType.SPACE_WEATHER_ADVISORY);
            t2ToContainedMessageType.put(FCT_TROPICAL_CYCLONE_ADVISORIES, MessageType.TROPICAL_CYCLONE_ADVISORY);
            t2ToContainedMessageType.put(FCT_UPPER_AIR_WINDS_AND_TEMPERATURES, new MessageType("GAFOR"));
        }

        ForecastsDataTypeDesignatorT2(final char code) {
            super(code);
        }

        public static Optional<ForecastsDataTypeDesignatorT2> fromCode(final char code) {
            return MAPPING.getOptionalDesignatorByCode(code);
        }

        public static Optional<ForecastsDataTypeDesignatorT2> fromName(final String name) {
            return MAPPING.getOptionalDesignatorByName(name);
        }

        @Override
        public Optional<DataTypeDesignatorT1> getT1() {
            return Optional.of(DataTypeDesignatorT1.FORECASTS);
        }

        @Override
        public String name() {
            return MAPPING.getDesignatorName(this.code());
        }
    }

    public static class SurfaceDataTypeDesignatorT2 extends DataTypeDesignatorT2 {
        public static final SurfaceDataTypeDesignatorT2 SD_AVIATION_ROUTINE_REPORTS = new SurfaceDataTypeDesignatorT2('A');
        public static final SurfaceDataTypeDesignatorT2 SD_RADAR_REPORTS_A = new SurfaceDataTypeDesignatorT2('B');
        public static final SurfaceDataTypeDesignatorT2 SD_RADAR_REPORTS_B = new SurfaceDataTypeDesignatorT2('C');
        public static final SurfaceDataTypeDesignatorT2 SD_RADAR_REPORTS_AB = new SurfaceDataTypeDesignatorT2('D');
        public static final SurfaceDataTypeDesignatorT2 SD_SEISMIC_DATA = new SurfaceDataTypeDesignatorT2('E');
        public static final SurfaceDataTypeDesignatorT2 SD_ATMOSPHERICS_REPORTS = new SurfaceDataTypeDesignatorT2('F');
        public static final SurfaceDataTypeDesignatorT2 SD_RADIOLOGICAL_DATA_REPORT = new SurfaceDataTypeDesignatorT2('G');
        public static final SurfaceDataTypeDesignatorT2 SD_SDP_STATIONS_REPORTS = new SurfaceDataTypeDesignatorT2('H');
        public static final SurfaceDataTypeDesignatorT2 SD_INTERMEDIATE_SYNOPTIC_HOUR = new SurfaceDataTypeDesignatorT2('I');
        public static final SurfaceDataTypeDesignatorT2 SD_MAIN_SYNOPTIC_HOUR = new SurfaceDataTypeDesignatorT2('M');
        public static final SurfaceDataTypeDesignatorT2 SD_NON_STANDARD_SYNOPTIC_HOUR = new SurfaceDataTypeDesignatorT2('N');
        public static final SurfaceDataTypeDesignatorT2 SD_OCEANOGRAPHIC_DATA = new SurfaceDataTypeDesignatorT2('O');
        public static final SurfaceDataTypeDesignatorT2 SD_SPECIAL_AVIATION_WEATHER_REPORTS = new SurfaceDataTypeDesignatorT2('P');
        public static final SurfaceDataTypeDesignatorT2 SD_HYDROLOGICAL_RIVER_REPORTS = new SurfaceDataTypeDesignatorT2('R');
        public static final SurfaceDataTypeDesignatorT2 SD_DRIFTING_BUOY_REPORTS = new SurfaceDataTypeDesignatorT2('S');
        public static final SurfaceDataTypeDesignatorT2 SD_SEA_ICE_REPORTS = new SurfaceDataTypeDesignatorT2('T');
        public static final SurfaceDataTypeDesignatorT2 SD_SNOW_DEPTH_REPORTS = new SurfaceDataTypeDesignatorT2('U');
        public static final SurfaceDataTypeDesignatorT2 SD_LAKE_ICE_REPORTS = new SurfaceDataTypeDesignatorT2('V');
        public static final SurfaceDataTypeDesignatorT2 SD_WAVE_INFORMATION = new SurfaceDataTypeDesignatorT2('W');
        public static final SurfaceDataTypeDesignatorT2 SD_MISCELLANEOUS = new SurfaceDataTypeDesignatorT2('X');
        public static final SurfaceDataTypeDesignatorT2 SD_SEISMIC_WAVEFORM_DATA = new SurfaceDataTypeDesignatorT2('Y');
        public static final SurfaceDataTypeDesignatorT2 SD_SEA_LEVEL_DEEP_OCEAN_TSUNAMI_DATA = new SurfaceDataTypeDesignatorT2('Z');

        private static final AutoReflectionDataTypeDesignatorMapping<SurfaceDataTypeDesignatorT2> MAPPING = new AutoReflectionDataTypeDesignatorMapping<>(
                SurfaceDataTypeDesignatorT2.class);

        static {
            t2ToContainedMessageType.put(SD_AVIATION_ROUTINE_REPORTS, MessageType.METAR);
            t2ToContainedMessageType.put(SD_SPECIAL_AVIATION_WEATHER_REPORTS, MessageType.SPECI);
        }

        SurfaceDataTypeDesignatorT2(final char code) {
            super(code);
        }

        public static Optional<SurfaceDataTypeDesignatorT2> fromCode(final char code) {
            return MAPPING.getOptionalDesignatorByCode(code);
        }

        public static Optional<SurfaceDataTypeDesignatorT2> fromName(final String name) {
            return MAPPING.getOptionalDesignatorByName(name);
        }

        @Override
        public Optional<DataTypeDesignatorT1> getT1() {
            return Optional.of(DataTypeDesignatorT1.SURFACE_DATA);
        }

        @Override
        public String name() {
            return MAPPING.getDesignatorName(this.code());
        }

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
            return DataTypeDesignatorT2.fromName(value).orElse(null);
        }
    }

    static class DataTypeDesignatorT2Serializer extends StdSerializer<DataTypeDesignatorT2> {

        DataTypeDesignatorT2Serializer() {
            this(null);
        }

        DataTypeDesignatorT2Serializer(final Class<DataTypeDesignatorT2> vc) {
            super(vc);
        }

        @Override
        public void serialize(final DataTypeDesignatorT2 designator, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider)
                throws IOException {
            jsonGenerator.writeString(designator.name());
        }
    }
}
