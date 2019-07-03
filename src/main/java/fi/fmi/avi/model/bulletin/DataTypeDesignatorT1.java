package fi.fmi.avi.model.bulletin;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataTypeDesignatorT1 implements DataTypeDesignator {

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
        public Optional<DataTypeDesignatorT2> t2FromCode(final char t2Code) {
            return Optional.of(DataTypeDesignatorT2.ForecastsDataTypeDesignatorT2.fromCode(t2Code));
        }
    };
    public static final DataTypeDesignatorT1 GRID_POINT_INFORMATION_GRID_2 = new DataTypeDesignatorT1('G');
    public static final DataTypeDesignatorT1 GRID_POINT_INFORMATION_GRIB = new DataTypeDesignatorT1('H');
    public static final DataTypeDesignatorT1 OBSERVATIONAL_DATA_BINARY_BUFR = new DataTypeDesignatorT1('I');
    public static final DataTypeDesignatorT1 FORECAST_INFORMATION_BINARY_BUFR = new DataTypeDesignatorT1('J');
    public static final DataTypeDesignatorT1 CREX = new DataTypeDesignatorT1('K');
    public static final DataTypeDesignatorT1 AVIATION_INFORMATION_IN_XML = new DataTypeDesignatorT1('L');
    public static final DataTypeDesignatorT1 NOTICES = new DataTypeDesignatorT1('N');
    public static final DataTypeDesignatorT1 OCEANOGRAPHIC_INFORMATION_GRIB = new DataTypeDesignatorT1('O');
    public static final DataTypeDesignatorT1 PICTORIAL_INFORMATION_BINARY = new DataTypeDesignatorT1('P');
    public static final DataTypeDesignatorT1 PICTORIAL_INFORMATION_REGIONAL_BINARY = new DataTypeDesignatorT1('Q');
    public static final DataTypeDesignatorT1 SURFACE_DATA = new DataTypeDesignatorT1('S') {
        @Override
        public Optional<DataTypeDesignatorT2> t2FromCode(final char t2Code) {
            return Optional.of(DataTypeDesignatorT2.SurfaceDataTypeDesignatorT2.fromCode(t2Code));
        }
    };
    public static final DataTypeDesignatorT1 SATELLITE_DATA = new DataTypeDesignatorT1('T');
    public static final DataTypeDesignatorT1 UPPER_AIR_DATA = new DataTypeDesignatorT1('U'){
        @Override
        public Optional<DataTypeDesignatorT2> t2FromCode(final char t2Code) {
            return Optional.of(DataTypeDesignatorT2.UpperAirDataTypeDesignatorT2.fromCode(t2Code));
        }
    };
    public static final DataTypeDesignatorT1 NATIONAL_DATA = new DataTypeDesignatorT1('V');
    public static final DataTypeDesignatorT1 WARNINGS = new DataTypeDesignatorT1('W') {
        @Override
        public Optional<DataTypeDesignatorT2> t2FromCode(final char t2Code) {
            return Optional.of(DataTypeDesignatorT2.WarningsDataTypeDesignatorT2.fromCode(t2Code));
        }
    };
    public static final DataTypeDesignatorT1 CAP = new DataTypeDesignatorT1('X');
    public static final DataTypeDesignatorT1 GRIB_REGIONAL = new DataTypeDesignatorT1('Y');

    private static final AutoReflectionDataTypeDesignatorMapping<DataTypeDesignatorT1> mapping = new AutoReflectionDataTypeDesignatorMapping<>(DataTypeDesignatorT1.class);

    public static DataTypeDesignatorT1 fromCode(final char code) {
        DataTypeDesignatorT1 designatorT1 = mapping.getDesignatorByCode(code);
        if (designatorT1 == null) {
            designatorT1 = new DataTypeDesignatorT1(code);
        }
        return designatorT1;
    }

    public static DataTypeDesignatorT1 fromName(final String name) {
        DataTypeDesignatorT1 designatorT1 = mapping.getDesignatorByName(name);
        if (designatorT1 == null) {
            final Matcher m = Pattern.compile("^EXTENSION_(?<code>[a-zA-Z])$").matcher(name);
            if (m.matches()) {
                designatorT1 = new DataTypeDesignatorT1(m.group("code").charAt(0));
            }
        }
        return designatorT1;
    }

    private final char code;

    public DataTypeDesignatorT1(final char code) {
        this.code = code;
    }

    public Optional<DataTypeDesignatorT2> t2FromCode(final char t2Code) {
        return Optional.empty();
    }

    @Override
    public char code() {
        return code;
    }

    @Override
    public String name() {
        final String name = mapping.getDesignatorName(this.code);
        if (name == null) {
            return "EXTENSION_" + code();
        } else {
            return name;
        }
    }

    public String toString() {
        return name();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DataTypeDesignatorT1) {
            return this.name().equals(((DataTypeDesignatorT1)o).name());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return name().hashCode();
    }
}
