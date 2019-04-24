package fi.fmi.avi.model;

import java.time.YearMonth;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface BulletinHeading {

    /**
     * Type of the content (AAx, RRx, CCx) part of the abbreviated heading.
     *
     * @return the type of the content
     */
    Type getType();

    /**
     *
     * @return
     */
    DataTypeDesignatorT1 getDataTypeDesignatorT1ForTAC();

    /**
     *
     * @return
     */
    @JsonIgnore
    default DataTypeDesignatorT1 getDataTypeDesignatorT1ForXML() {
        return DataTypeDesignatorT1.AVIATION_INFORMATION_IN_XML;
    }

    DataTypeDesignatorT2 getDataTypeDesignatorT2();

    /**
     *
     * @return
     */
    @JsonIgnore
    default String getDataTypeDesignatorsForTAC() {
        return "" + getDataTypeDesignatorT1ForTAC().getCode() + getDataTypeDesignatorT2().getCode();
    }

    /**
     *
     * @return
     */
    @JsonIgnore
    default String getDataTypeDesignatorsForXML() {
        return "" + getDataTypeDesignatorT1ForXML().getCode() + getDataTypeDesignatorT2().getCode();
    }

    /**
     *
     * @return
     */
    @JsonIgnore
    default String getDataDesignatorsForTAC() {
        return "" + getDataTypeDesignatorsForTAC() + getGeographicalDesignator() + String.format("%02d", getBulletinNumber());
    }

    /**
     *
     * @return
     */
    @JsonIgnore
    default String getDataDesignatorsForXML() {
        return "" + getDataTypeDesignatorsForXML() + getGeographicalDesignator() + String.format("%02d", getBulletinNumber());
    }

    /**
     * This corresponds to the 'CCCC' part of the abbreviated heading
     *
     * @return the indicator
     */
    String getLocationIndicator();

    /**
     * This corresponds to the 'A<sub>1</sub>A<sub>2</sub>' part of the abbreviated heading
     *
     * @return the designator
     */
    String getGeographicalDesignator();

    /**
     * This corresponds to the 'ii' number of the abbreviated heading
     *
     * @return the number
     */
    int getBulletinNumber();

    /**
     * Corresponds to the A-Z letter 'x' of the RRx, AAx or CCx part of the abbreviated heading
     *
     * @return the augmentation number
     */
    Optional<Integer> getBulletinAugmentationNumber();

    /**
     * Returns the issue time of the bulletin.
     * The returned {@link PartialOrCompleteTimeInstant} may or may not contain
     * a completely resolved date time depending on which information it was
     * created with.
     *
     * @return the issue time
     *
     * @see PartialOrCompleteTimeInstant.Builder#completePartialAt(YearMonth)
     */
    PartialOrCompleteTimeInstant getIssueTime();

    enum Type {
        NORMAL(""), DELAYED("RR"), AMENDED("AA"), CORRECTED("CC");

        public static Type fromCode(final String code) {
            for (Type t : Type.values()) {
                if (t.getPrefix().equals(code)) {
                    return t;
                }
            }
            throw new IllegalArgumentException("Unknown prefix '" + code + "'");
        }

        private String prefix;

        Type(final String prefix) {
            this.prefix = prefix;
        }

        public String getPrefix() {
            return this.prefix;
        }
    }

    /**
     * WMO No.386 Manual on GTS 2015 rev 2017
     */
    enum DataTypeDesignatorT1 {
        ANALYSES('A'),//
        ADDRESSED_MESSAGE('B'),//
        CLIMATIC_DATA('C'),//
        GRID_POINT_INFORMATION_GRID_1('D'),//
        SATELLITE_IMAGERY('E'),//
        FORECASTS('F'),//
        GRID_POINT_INFORMATION_GRID_2('G'),//
        GRID_POINT_INFORMATION_GRIB('H'),//
        OBSERVATIONAL_DATA_BINARY_BUFR('I'),//
        FORECAST_INFORMATION_BINARY_BUFR('J'),//
        CREX('K'),//
        AVIATION_INFORMATION_IN_XML('L'),//
        NOTICES('N'),//
        OCEANOGRAPHIC_INFORMATION_GRIB('O'),//
        PICTORIAL_INFORMATION_BINARY('P'),//
        PICTORIAL_INFORMATION_REGIONAL_BINARY('Q'),//
        SURFACE_DATA('S'),//
        SATELLITE_DATA('T'),//
        UPPER_AIR_DATA('U'),//
        NATIONAL_DATA('V'),//
        WARNINGS('W');

        public static DataTypeDesignatorT1 fromCode(final char code) {
            for (DataTypeDesignatorT1 t : DataTypeDesignatorT1.values()) {
                if (t.getCode() == code) {
                    return t;
                }
            }
            throw new IllegalArgumentException("Unknown code '" + code + "'");
        }

        private final char code;

        DataTypeDesignatorT1(final char code) {
            this.code = code;
        }

        public char getCode() {
            return code;
        }
    }

    enum ForecastsDataTypeDesignatorT2 implements DataTypeDesignatorT2 {

        FCT_AVIATION_AREA_OR_GAMET_OR_ADVISORIES('A'),//
        FCT_UPPER_AIR_WINDS_AND_TEMPERATURES('B'),//
        FCT_AERODROME_VT_SHORT('C'),//
        FCT_RADIOLOGICAL_TRAJECTORY_DOSE('D'),//
        FCT_EXTENDED('E'),//
        FCT_SHIPPING('F'),//
        FCT_HYDROLOGICAL('G'),//
        FCT_UPPER_AIR_THICKNESS('H'),//
        FCT_ICEBERG('I'),//
        FCT_RADIO_WARNING_SERVICE('J'),//
        FCT_TROPICAL_CYCLONE_ADVISORIES('K'),//
        FCT_LOCAL_OR_AREA('L'),//
        FCT_TEMPERATURE_EXTREMES('M'),//
        FCT_SPACE_WEATHER('N'),//
        FCT_GUIDANCE('O'),//
        FCT_PUBLIC('P'),//
        FCT_OTHER_SHIPPING('Q'),//
        FCT_AVIATION_ROUTE('R'),//
        FCT_SURFACE('S'),//
        FCT_AERODROME_VT_LONG('T'),//
        FCT_UPPER_AIR('U'),//
        FCT_VOLCANIC_ASH_ADVISORIES('V'),//
        FCT_WINTER_SPORTS('W'),//
        FCT_MISCELLANEOUS('X'),//
        FCT_SHIPPING_AREA('Z');

        public static ForecastsDataTypeDesignatorT2 fromCode(final char code) {
            for (ForecastsDataTypeDesignatorT2 t : ForecastsDataTypeDesignatorT2.values()) {
                if (t.getCode() == code) {
                    return t;
                }
            }
            throw new IllegalArgumentException("Unknown code '" + code + "'");
        }

        private final char code;

        ForecastsDataTypeDesignatorT2(final char code) {
            this.code = code;
        }

        @Override
        public char getCode() {
            return code;
        }
    }

    enum WarningsDataTypeDesignatorT2 implements DataTypeDesignatorT2 {

        WRN_AIRMET('A'),//
        WRN_TROPICAL_CYCLONE_SIGMET('C'),//
        WRN_TSUNAMI('E'),//
        WRN_TORNADO('F'),//
        WRN_HYDROLOGICAL_OR_RIVER_FLOOD('G'),//
        WRN_MARINE_OR_COASTAL_FLOOD('H'),//
        WRN_OTHER('O'),//
        WRN_HUMANITARIAN_ACTIVITIES('R'),//
        WRN_SIGMET('S'),//
        WRN_TROPICAL_CYCLONE_TYPHOON_OR_HURRICANE('T'),//
        WRN_SEVERE_THUNDERSTORM('U'),//
        WRN_VOLCANIC_ASH_CLOUDS_SIGMET('V'),//
        WRN_WARNINGS_AND_WEATHER_SUMMARY('W');

        public static WarningsDataTypeDesignatorT2 fromCode(final char code) {
            for (WarningsDataTypeDesignatorT2 t : WarningsDataTypeDesignatorT2.values()) {
                if (t.getCode() == code) {
                    return t;
                }
            }
            throw new IllegalArgumentException("Unknown code '" + code + "'");
        }

        private final char code;

        WarningsDataTypeDesignatorT2(final char code) {
            this.code = code;
        }

        @Override
        public char getCode() {
            return code;
        }
    }

    enum XMLDataTypeDesignatorT2 implements DataTypeDesignatorT2 {

        XML_METAR('A'),//
        XML_AERODROME_VT_SHORT('C'),//
        XML_TROPICAL_CYCLONE_ADVISORIES('K'),//
        XML_SPECI('P'),//
        XML_SIGMET('S'),//
        XML_AERODROME_VT_LONG('T'),//
        XML_VOLCANIC_ASH_ADVISORY('U'),//
        XML_VOLCANIC_ASH_SIGMET('V'),//
        XML_AIRMET('W'),//
        XML_TROPICAL_CYCLONE_SIGMET('Y');

        public static XMLDataTypeDesignatorT2 fromCode(final char code) {
            for (XMLDataTypeDesignatorT2 t : XMLDataTypeDesignatorT2.values()) {
                if (t.getCode() == code) {
                    return t;
                }
            }
            throw new IllegalArgumentException("Unknown code '" + code + "'");
        }

        private final char code;

        XMLDataTypeDesignatorT2(final char code) {
            this.code = code;
        }

        @Override
        public char getCode() {
            return code;
        }
    }

    enum UpperAirDataTypeDesignatorT2 implements DataTypeDesignatorT2 {
        UA_AIRCRAFT_REPORT_CODAR_AIREP('A'),//
        UA_AIRCRAFT_REPORT_AMDAR('D'),//
        UA_PRESSURE_TEMPERATURE_HUMIDITY_WIND_PART_D('E'),//
        UA_PRESSURE_TEMPERATURE_HUMIDITY_WIND_PARTS_C_D('F'),//
        UA_WIND_PART_B('G'),//
        UA_WIND_PART_C('H'),//
        UA_WIND_PARTS_A_B('I'),//
        UA_PRESSURE_TEMPERATURE_HUMIDITY_WIND_PART_B('K'),//
        UA_PRESSURE_TEMPERATURE_HUMIDITY_WIND_PART_C('L'),//
        UA_ROCKETSONDE_REPORT('N'),//
        UA_WIND_PART_A('P'),//
        UA_WIND_PART_D('Q'),//
        UA_AIRCRAFT_REPORT_RECCO('R'),//
        UA_PRESSURE_TEMPERATURE_HUMIDITY_WIND_PART_A('S'),//
        UA_AIRCRAFT_REPORT_CODAR('T'),//
        UA_MISCELLANEOUS('X'),//
        UA_WIND_PARTS_C_D('Y'),//
        UA_PRESSURE_TEMPERATURE_HUMIDITY_WIND_PARTS_A_B_C_D('Z');

        public static UpperAirDataTypeDesignatorT2 fromCode(final char code) {
            for (UpperAirDataTypeDesignatorT2 t : UpperAirDataTypeDesignatorT2.values()) {
                if (t.getCode() == code) {
                    return t;
                }
            }
            throw new IllegalArgumentException("Unknown code '" + code + "'");
        }

        private final char code;

        UpperAirDataTypeDesignatorT2(final char code) {
            this.code = code;
        }

        @Override
        public char getCode() {
            return code;
        }
    }

    enum SurfaceDataTypeDesignatorT2 implements DataTypeDesignatorT2 {
        SD_AVIATION_ROUTINE_REPORTS('A'),//
        SD_RADAR_REPORTS_A('B'),//
        SD_RADAR_REPORTS_B('C'),//
        SD_RADAR_REPORTS_AB('D'),//
        SD_SEISMIC_DATA('E'),//
        SD_ATMOSPHERICS_REPORTS('F'),//
        SD_RADIOLOGICAL_DATA_REPORT('G'),//
        SD_SDP_STATIONS_REPORTS('H'),//
        SD_INTERMEDIATE_SYNOPTIC_HOUR('I'),//
        SD_MAIN_SYNOPTIC_HOUR('M'),//
        SD_NON_STANDARD_SYNOPTIC_HOUR('N'),//
        SD_OCEANOGRAPHIC_DATA('O'),//
        SD_SPECIAL_AVIATION_WEATHER_REPORTS('P'),//
        SD_HYDROLOGICAL_RIVER_REPORTS('R'),//
        SD_DRIFTING_BUOY_REPORTS('S'),//
        SD_SEA_ICE_REPORTS('T'),//
        SD_SNOW_DEPTH_REPORTS('U'),//
        SD_LAKE_ICE_REPORTS('V'),//
        SD_WAVE_INFORMATION('W'),//
        SD_MISCELLANEOUS('X'),//
        SD_SEISMIC_WAVEFORM_DATA('Y'),//
        SD_SEA_LEVEÖ_DEEP_OCEAN_TSUNAMI_DATA('Z');

        public static SurfaceDataTypeDesignatorT2  fromCode(final char code) {
            for (SurfaceDataTypeDesignatorT2  t : SurfaceDataTypeDesignatorT2 .values()) {
                if (t.getCode() == code) {
                    return t;
                }
            }
            throw new IllegalArgumentException("Unknown code '" + code + "'");
        }

        private final char code;

        SurfaceDataTypeDesignatorT2 (final char code) {
            this.code = code;
        }

        @Override
        public char getCode() {
            return code;
        }
    }

    interface DataTypeDesignatorT2 {
        char getCode();
    }
}
