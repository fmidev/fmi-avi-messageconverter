package fi.fmi.avi.model.bulletin;

import java.io.IOException;
import java.io.Serializable;
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

@JsonSerialize(using = DataTypeDesignatorT1.DataTypeDesignatorT1Serializer.class)
@JsonDeserialize(using = DataTypeDesignatorT1.DataTypeDesignatorT1Deserializer.class)
public class DataTypeDesignatorT1 implements DataTypeDesignator, Serializable {

    /**
     * From WMO No.386 Manual on GTS 2015 rev 2017
     */
    public static final DataTypeDesignatorT1 ANALYSES = new DataTypeDesignatorT1('A');
    public static final DataTypeDesignatorT1 ADDRESSED_MESSAGE = new DataTypeDesignatorT1('B');
    public static final DataTypeDesignatorT1 CLIMATIC_DATA = new DataTypeDesignatorT1('C');
    public static final DataTypeDesignatorT1 GRID_POINT_INFORMATION_GRID_1 = new DataTypeDesignatorT1('D');
    public static final DataTypeDesignatorT1 SATELLITE_IMAGERY = new DataTypeDesignatorT1('E');
    public static final DataTypeDesignatorT1 FORECASTS = new DataTypeDesignatorT1('F') {
        @Override
        public Optional<DataTypeDesignatorT2.ForecastsDataTypeDesignatorT2> t2FromCode(final char t2Code) {
            return DataTypeDesignatorT2.ForecastsDataTypeDesignatorT2.fromCode(t2Code);
        }
    };
    public static final DataTypeDesignatorT1 GRID_POINT_INFORMATION_GRID_2 = new DataTypeDesignatorT1('G');
    public static final DataTypeDesignatorT1 GRID_POINT_INFORMATION_GRIB = new DataTypeDesignatorT1('H');
    public static final DataTypeDesignatorT1 OBSERVATIONAL_DATA_BINARY_BUFR = new DataTypeDesignatorT1('I');
    public static final DataTypeDesignatorT1 FORECAST_INFORMATION_BINARY_BUFR = new DataTypeDesignatorT1('J');
    public static final DataTypeDesignatorT1 CREX = new DataTypeDesignatorT1('K');
    public static final DataTypeDesignatorT1 AVIATION_INFORMATION_IN_XML = new DataTypeDesignatorT1('L') {
        @Override
        public Optional<? extends DataTypeDesignatorT2> t2FromCode(final char t2Code) {
            return DataTypeDesignatorT2.XMLDataTypeDesignatorT2.fromCode(t2Code);
        }
    };
    public static final DataTypeDesignatorT1 NOTICES = new DataTypeDesignatorT1('N');
    public static final DataTypeDesignatorT1 OCEANOGRAPHIC_INFORMATION_GRIB = new DataTypeDesignatorT1('O');
    public static final DataTypeDesignatorT1 PICTORIAL_INFORMATION_BINARY = new DataTypeDesignatorT1('P');
    public static final DataTypeDesignatorT1 PICTORIAL_INFORMATION_REGIONAL_BINARY = new DataTypeDesignatorT1('Q');
    public static final DataTypeDesignatorT1 SURFACE_DATA = new DataTypeDesignatorT1('S') {
        @Override
        public Optional<DataTypeDesignatorT2.SurfaceDataTypeDesignatorT2> t2FromCode(final char t2Code) {
            return DataTypeDesignatorT2.SurfaceDataTypeDesignatorT2.fromCode(t2Code);
        }
    };
    public static final DataTypeDesignatorT1 SATELLITE_DATA = new DataTypeDesignatorT1('T');
    public static final DataTypeDesignatorT1 UPPER_AIR_DATA = new DataTypeDesignatorT1('U') {
        @Override
        public Optional<DataTypeDesignatorT2.UpperAirDataTypeDesignatorT2> t2FromCode(final char t2Code) {
            return DataTypeDesignatorT2.UpperAirDataTypeDesignatorT2.fromCode(t2Code);
        }
    };
    public static final DataTypeDesignatorT1 NATIONAL_DATA = new DataTypeDesignatorT1('V');
    public static final DataTypeDesignatorT1 WARNINGS = new DataTypeDesignatorT1('W') {
        @Override
        public Optional<DataTypeDesignatorT2.WarningsDataTypeDesignatorT2> t2FromCode(final char t2Code) {
            return DataTypeDesignatorT2.WarningsDataTypeDesignatorT2.fromCode(t2Code);
        }
    };
    public static final DataTypeDesignatorT1 CAP = new DataTypeDesignatorT1('X');
    public static final DataTypeDesignatorT1 GRIB_REGIONAL = new DataTypeDesignatorT1('Y');

    private static final Pattern EXTENSION_NAME_PATTERN = Pattern.compile("^EXTENSION_(?<code>[a-zA-Z])$");

    private static final AutoReflectionDataTypeDesignatorMapping<DataTypeDesignatorT1> MAPPING = new AutoReflectionDataTypeDesignatorMapping<>(
            DataTypeDesignatorT1.class);

    private static final long serialVersionUID = 2361752791417789348L;

    private final char code;

    private DataTypeDesignatorT1(final char code) {
        this.code = code;
    }

    public static DataTypeDesignatorT1 fromCode(final char code) {
        return MAPPING.getOptionalDesignatorByCode(code).orElseGet(() -> new DataTypeDesignatorT1(code));
    }

    public static Optional<DataTypeDesignatorT1> fromName(final String name) {
        final Optional<DataTypeDesignatorT1> designatorT1 = MAPPING.getOptionalDesignatorByName(name);
        if (designatorT1.isPresent()) {
            return designatorT1;
        }
        final Matcher m = EXTENSION_NAME_PATTERN.matcher(name);
        if (m.matches()) {
            return Optional.of(new DataTypeDesignatorT1(m.group("code").charAt(0)));
        }
        return Optional.empty();
    }

    public Optional<? extends DataTypeDesignatorT2> t2FromCode(final char t2Code) {
        return Optional.empty();
    }

    @Override
    public char code() {
        return code;
    }

    @Override
    public String name() {
        return MAPPING.getOptionalDesignatorName(this.code).orElseGet(() -> "EXTENSION_" + code());
    }

    public String toString() {
        return name();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DataTypeDesignatorT1) {
            return this.name().equals(((DataTypeDesignatorT1) obj).name());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return name().hashCode();
    }

    static class DataTypeDesignatorT1Deserializer extends StdDeserializer<DataTypeDesignatorT1> {

        DataTypeDesignatorT1Deserializer() {
            this(null);
        }

        DataTypeDesignatorT1Deserializer(final Class<?> vc) {
            super(vc);
        }

        @Override
        public fi.fmi.avi.model.bulletin.DataTypeDesignatorT1 deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext)
                throws IOException {
            final String value = ((JsonNode) jsonParser.getCodec().readTree(jsonParser)).asText();
            return DataTypeDesignatorT1.fromName(value).orElse(null);
        }
    }

    static class DataTypeDesignatorT1Serializer extends StdSerializer<DataTypeDesignatorT1> {

        DataTypeDesignatorT1Serializer() {
            this(null);
        }

        DataTypeDesignatorT1Serializer(final Class<DataTypeDesignatorT1> vc) {
            super(vc);
        }

        @Override
        public void serialize(final DataTypeDesignatorT1 designator, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider)
                throws IOException {
            jsonGenerator.writeString(designator.name());
        }
    }
}
