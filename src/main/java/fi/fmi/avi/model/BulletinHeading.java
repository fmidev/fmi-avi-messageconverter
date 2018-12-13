package fi.fmi.avi.model;

import java.util.Optional;

public interface BulletinHeading {

    /**
     * Type of the content (AAx, RRx, CCx) part of the abbreviated heading
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
    default DataTypeDesignatorT1 getDataTypeDesignatorT1ForXML() {
        return DataTypeDesignatorT1.AVIATION_INFORMATION_IN_XML;
    }

    DataTypeDesignatorT2 getDataTypeDesignatorT2();

    /**
     *
     * @return
     */
    default String getDataTypeDesignatorsForTAC() {
        return "" + getDataTypeDesignatorT1ForTAC().getCode() + getDataTypeDesignatorT2().getCode();
    }

    /**
     *
     * @return
     */
    default String getDataTypeDesignatorsForXML() {
        return "" + getDataTypeDesignatorT1ForXML().getCode() + getDataTypeDesignatorT2().getCode();
    }

    /**
     *
     * @return
     */
    default String getDataDesignatorsForTAC() {
        return "" + getDataTypeDesignatorsForTAC() + getGeographicalDesignator() + String.format("%02d", getBulletinNumber());
    }

    /**
     *
     * @return
     */
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

    enum Type {
        NORMAL(""), DELAYED("RR"), AMENDED("AA"), CORRECTED("CC");
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

        private final char code;

        DataTypeDesignatorT1(final char code) {
            this.code = code;
        }

        public char getCode() {
            return code;
        }
    }

    enum ForecastsDataTypeDesignatorT2 implements DataTypeDesignatorT2 {

        AVIATION_AREA_OR_GAMET_OR_ADVISORIES('A'),//
        UPPER_AIR_WINDS_AND_TEMPERATURES('B'),//
        AERODROME_VT_SHORT('C'),//
        RADIOLOGICAL_TRAJECTORY_DOSE('D'),//
        EXTENDED('E'),//
        SHIPPING('F'),//
        HYDROLOGICAL('G'),//
        UPPER_AIR_THICKNESS('H'),//
        ICEBERG('I'),//
        RADIO_WARNING_SERVICE('J'),//
        TROPICAL_CYCLONE_ADVISORIES('K'),//
        LOCAL_OR_AREA('L'),//
        TEMPERATURE_EXTREMES('M'),//
        GUIDANCE('O'),//
        PUBLIC('P'),//
        OTHER_SHIPPING('Q'),//
        AVIATION_ROUTE('R'),//
        SURFACE('S'),//
        AERODROME_VT_LONG('T'),//
        UPPER_AIR('U'),//
        VOLCANIC_ASH_ADVISORIES('V'),//
        WINTER_SPORTS('W'),//
        MISCELLANEOUS('X'),
        SHIPPING_AREA('Z');

        private final char code;

        ForecastsDataTypeDesignatorT2(final char code) {
            this.code = code;
        }

        public char getCode() {
            return code;
        }
    }

    enum WarningsDataTypeDesignatorT2 implements DataTypeDesignatorT2 {

        AIRMET('A'),//
        TROPICAL_CYCLONE_SIGMET('C'),//
        TSUNAMI('E'),//
        TORNADO('F'),//
        HYDROLOGICAL_OR_RIVER_FLOOD('G'),//
        MARINE_OR_COASTAL_FLOOD('H'),//
        OTHER('O'),//
        HUMANITARIAN_ACTIVITIES('R'),//
        SIGMET('S'),//
        TROPICAL_CYCLONE_TYPHOON_OR_HURRICANE('T'),//
        SEVERE_THUNDERSTORM('U'),//
        VOLCANIC_ASH_CLOUDS_SIGMET('V'),
        WARNINGS_AND_WEATHER_SUMMARY('W');

        private final char code;

        WarningsDataTypeDesignatorT2(final char code) {
            this.code = code;
        }

        public char getCode() {
            return code;
        }
    }

    interface DataTypeDesignatorT2 {
        char getCode();
    }
}
