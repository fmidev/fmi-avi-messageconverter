package fi.fmi.avi.model;

import static fi.fmi.avi.model.AviationCodeListUser.RelationalOperator.BELOW;
import static java.util.Objects.requireNonNull;

import java.util.Optional;

/**
 * A convenience interface containing references to shared codelists and enums.
 */
public interface AviationCodeListUser {

    String CODELIST_CLOUD_AMOUNT_REPORTED_AT_AERODROME = "http://codes.wmo.int/49-2/CloudAmountReportedAtAerodrome";
    String CODELIST_SIGNIFICANT_CONVECTIVE_CLOUD_TYPE = "http://codes.wmo.int/49-2/SigConvectiveCloudType";

    String CODELIST_VALUE_PREFIX_SIG_WEATHER = "http://codes.wmo.int/306/4678/";
    String CODELIST_VALUE_PREFIX_CLOUD_AMOUNT_REPORTED_AT_AERODROME = "http://codes.wmo.int/49-2/CloudAmountReportedAtAerodrome/";
    String CODELIST_VALUE_PREFIX_SIG_CONVECTIVE_CLOUD_TYPE = "http://codes.wmo.int/49-2/SigConvectiveCloudType/";
    String CODELIST_VALUE_PREFIX_SEA_SURFACE_STATE = "http://codes.wmo.int/bufr4/codeflag/0-22-061/";
    String CODELIST_VALUE_PREFIX_RUNWAY_DEPOSITS = "http://codes.wmo.int/bufr4/codeflag/0-20-086/";
    String CODELIST_VALUE_PREFIX_RUNWAY_CONTAMINATION = "http://codes.wmo.int/bufr4/codeflag/0-20-087/";
    String CODELIST_VALUE_PREFIX_RUNWAY_SURFACE_FRICTION_OR_BRAKING_ACTION = "http://codes.wmo.int/bufr4/codeflag/0-20-089/";

    String CODELIST_VALUE_NIL_REASON_NOTHING_OF_OPERATIONAL_SIGNIFICANCE = "http://codes.wmo.int/common/nil/nothingOfOperationalSignificance";
    String CODELIST_VALUE_NIL_REASON_NOT_OBSERVABLE = "http://codes.wmo.int/common/nil/notObservable";
    String CODELIST_VALUE_NIL_REASON_NOT_DETECTED_BY_AUTO_SYSTEM = "http://codes.wmo.int/common/nil/notDetectedByAutoSystem";
    String CODELIST_VALUE_NIL_REASON_NO_SIGNIFICANT_CHANGE = "http://codes.wmo.int/common/nil/noSignificantChange";
    String CODELIST_VALUE_NIL_REASON_MISSING = "http://codes.wmo.int/common/nil/missing";
    String CODELIST_VALUE_NIL_REASON_INAPPLICABLE = "http://codes.wmo.int/common/nil/inapplicable";
    String CODELIST_VALUE_NIL_REASON_WITHHELD = "http://codes.wmo.int/common/nil/withheld";
    String CODELIST_VALUE_NIL_REASON_UNKNOWN = "http://codes.wmo.int/common/nil/unknown";

    String MET_AERODROME_FORECAST_TYPE = "http://codes.wmo.int/49-2/observation-type/iwxxm/2.1/MeteorologicalAerodromeForecast";
    String MET_AERODROME_FORECAST_PROPERTIES = "http://codes.wmo.int/49-2/observable-property/MeteorologicalAerodromeForecast";
    String TAF_PROCEDURE_DESCRIPTION = "WMO No. 49 Volume 2 Meteorological Service for International Air Navigation APPENDIX 5 TECHNICAL SPECIFICATIONS RELATED TO FORECASTS";

    String MET_AERODROME_OBSERVATION_TYPE = "http://codes.wmo.int/49-2/observation-type/iwxxm/2.1/MeteorologicalAerodromeObservation";
    String MET_AERODROME_OBSERVATION_PROPERTIES = "http://codes.wmo.int/49-2/observable-property/MeteorologicalAerodromeObservation";
    String METAR_PROCDURE_DESCRIPTION = "WMO No. 49 Volume 2 Meteorological Service for International Air Navigation APPENDIX 3 TECHNICAL SPECIFICATIONS RELATED TO METEOROLOGICAL OBSERVATIONS AND REPORTS";

    String TREND_FORECAST_OBSERVATION_TYPE = "http://codes.wmo.int/49-2/observation-type/iwxxm/2.1/MeteorologicalAerodromeTrendForecast";
    String TREND_FORECAST_PROPERTIES = "http://codes.wmo.int/49-2/observable-property/MeteorologicalAerodromeTrendForecast";

    String CODELIST_VALUE_PREFIX_OM_SAMPLING = "http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/";
    String CODELIST_VALUE_EPSG_4326 = "http://www.opengis.net/def/crs/EPSG/0/4326";

    String CODELIST_VALUE_SIGMET_PROCESS = "WMO No. 49 Volume 2 Meteorological Service for International Air Navigation APPENDIX 6-1 TECHNICAL SPECIFICATIONS "
            + "RELATED TO SIGMET INFORMATION";
    String CODELIST_SIGMET_EVOLVING_CONDITION_COLLECTION_ANALYSIS = "http://codes.wmo.int/49-2/observation-type/iwxxm/2.1/SIGMETEvolvingConditionCollectionAnalysis";
    String CODELIST_SIGWX_PHENOMENA_ROOT = "http://codes.wmo.int/49-2/SigWxPhenomena/";
    String CODELIST_SIGMET_POSITION_COLLECTION_ANALYSIS = "http://codes.wmo.int/49-2/observable-property/SIGMETPositionCollectionAnalysis";

    String CODELIST_AIRMET_PHENOMENA_ROOT = "http://codes.wmo.int/49-2/AirWxPhenomena/";
    String CODELIST_VALUE_AIRMET_PROCESS = "WMO No. 49 Volume 2 Meteorological Service for International Air Navigation APPENDIX 6-1 TECHNICAL SPECIFICATIONS "
            + "RELATED TO AIRMET INFORMATION";
    String CODELIST_AIRMET_EVOLVING_CONDITION_COLLECTION_ANALYSIS =
            "http://codes.wmo.int/49-2/observation-type/iwxxm/2" + ".1/AIRMETEvolvingConditionCollectionAnalysis";
    String CODELIST_VALUE_WEATHERCAUSINGVISIBILITYREDUCTION = "http://codes.wmo.int/49-2/WeatherCausingVisibilityReduction";

    enum MetarStatus {
        NORMAL(0), CORRECTION(1), MISSING(2);

        public static MetarStatus fromInt(final int code) {
            switch (code) {
                case 0:
                    return NORMAL;
                case 1:
                    return CORRECTION;
                case 2:
                    return MISSING;
                default:
                    throw new IllegalArgumentException("No value for code " + code);
            }
        }

        private final int code;

        MetarStatus(final int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }

    }

    @Deprecated
    enum TAFStatus {
        NORMAL(0, AviationWeatherMessage.ReportStatus.NORMAL, false),//
        AMENDMENT(1, AviationWeatherMessage.ReportStatus.AMENDMENT, false),//
        CANCELLATION(2, AviationWeatherMessage.ReportStatus.AMENDMENT, true),//
        CORRECTION(3, AviationWeatherMessage.ReportStatus.CORRECTION, false),//
        MISSING(4, AviationWeatherMessage.ReportStatus.NORMAL, false);//

        private final int code;
        private final AviationWeatherMessage.ReportStatus reportStatus;
        private final boolean cancelMessage;

        TAFStatus(final int code, final AviationWeatherMessage.ReportStatus reportStatus, final boolean cancelMessage) {
            this.code = code;
            this.reportStatus = reportStatus;
            this.cancelMessage = cancelMessage;
        }

        public static TAFStatus fromInt(final int code) {
            switch (code) {
                case 0:
                    return NORMAL;
                case 1:
                    return AMENDMENT;
                case 2:
                    return CANCELLATION;
                case 3:
                    return CORRECTION;
                case 4:
                    return MISSING;
                default:
                    throw new IllegalArgumentException("No value for code " + code);
            }
        }

        public static TAFStatus fromReportStatus(final AviationWeatherMessage.ReportStatus reportStatus, final boolean cancelMessage,
                final boolean missingMessage) {
            requireNonNull(reportStatus, "reportStatus");
            if (cancelMessage) {
                return CANCELLATION;
            }
            if (missingMessage) {
                return MISSING;
            }
            switch (reportStatus) {
                case CORRECTION:
                    return CORRECTION;
                case AMENDMENT:
                    return AMENDMENT;
                case NORMAL:
                    return NORMAL;
                default:
                    throw new IllegalArgumentException("Unknown reportStatus: " + reportStatus);
            }
        }

        public int getCode() {
            return this.code;
        }

        public AviationWeatherMessage.ReportStatus getReportStatus() {
            return reportStatus;
        }

        public boolean isCancelMessage() {
            return cancelMessage;
        }

    }

    enum RelationalOperator {
        ABOVE(0), BELOW(1);

        public static RelationalOperator fromInt(final int code) {
            switch (code) {
                case 0:
                    return ABOVE;
                case 1:
                    return BELOW;
                default:
                    throw new IllegalArgumentException("No value for code " + code);
            }
        }

        private final int code;

        RelationalOperator(final int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }

    }

    enum VisualRangeTendency {
        UPWARD(0), NO_CHANGE(1), DOWNWARD(2), MISSING_VALUE(3);

        public static VisualRangeTendency fromInt(final int code) {
            switch (code) {
                case 0:
                    return UPWARD;
                case 1:
                    return NO_CHANGE;
                case 2:
                    return DOWNWARD;
                case 3:
                    return MISSING_VALUE;
                default:
                    throw new IllegalArgumentException("No value for code " + code);
            }
        }

        private final int code;

        VisualRangeTendency(final int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }

    }

    enum CloudAmount {
        SKC("SKC", 0),
        FEW("FEW", 1),
        SCT("SCT", 2),
        BKN("BKN", 3),
        OVC("OVC", 4),
        ISOL("ISOL", 8),
        OCNL("OCNL", 10),
        FRQ("FRQ", 12),
        LYR("LYR", 14),
        EMBD("EMBD", 16);

        public static CloudAmount fromInt(final int code) {
            switch (code) {
                case 0:
                    return SKC;
                case 1:
                    return FEW;
                case 2:
                    return SCT;
                case 3:
                    return BKN;
                case 4:
                    return OVC;
                case 8:
                    return ISOL;
                case 10:
                    return OCNL;
                case 12:
                    return FRQ;
                case 14:
                    return LYR;
                case 16:
                    return EMBD;
                default:
                    throw new IllegalArgumentException("No value for code " + code);
            }
        }

        private final String code;
        private final int bufrCode;

        CloudAmount(final String code, final int bufrCode) {
            this.code = code;
            this.bufrCode = bufrCode;
        }

        public String getCode() {
            return code;
        }

        public int getBufrCode() {
            return bufrCode;
        }
    }

    enum CloudType {
        CB("CB", 9), TCU("TCU", 32);

        public static CloudType fromInt(final int code) {
            switch (code) {
                case 9:
                    return CB;
                case 32:
                    return TCU;
                default:
                    throw new IllegalArgumentException("No value for code " + code);
            }
        }

        private final String code;
        private final int bufrCode;

        CloudType(final String code, final int bufrCode) {
            this.code = code;
            this.bufrCode = bufrCode;
        }

        public String getCode() {
            return code;
        }

        public int getBufrCode() {
            return bufrCode;
        }
    }

    enum SeaSurfaceState {
        CALM_GLASSY(0),
        CALM_RIPPLED(1),
        SMOOTH_WAVELETS(2),
        SLIGHT(3),
        MODERATE(4),
        ROUGH(5),
        VERY_ROUGH(6),
        HIGH(7),
        VERY_HIGH(8),
        PHENOMENAL(9),
        MISSING_VALUE(15);

        public static SeaSurfaceState fromInt(final int code) {
            switch (code) {
                case 0:
                    return CALM_GLASSY;
                case 1:
                    return CALM_RIPPLED;
                case 2:
                    return SMOOTH_WAVELETS;
                case 3:
                    return SLIGHT;
                case 4:
                    return MODERATE;
                case 5:
                    return ROUGH;
                case 6:
                    return VERY_ROUGH;
                case 7:
                    return HIGH;
                case 8:
                    return VERY_HIGH;
                case 9:
                    return PHENOMENAL;
                case 15:
                    return MISSING_VALUE;
                default:
                    throw new IllegalArgumentException("No value for code " + code);
            }
        }

        private final int code;

        SeaSurfaceState(final int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }
    }

    enum RunwayDeposit {
        CLEAR_AND_DRY(0), DAMP(1), WET_WITH_WATER_PATCHES(2), RIME_AND_FROST_COVERED(3), // (depth normally less than 1mm)
        DRY_SNOW(4), WET_SNOW(5), SLUSH(6), ICE(7), COMPACT_OR_ROLLED_SNOW(8), FROZEN_RUTS_OR_RIDGES(9), MISSING_OR_NOT_REPORTED(15);

        public static RunwayDeposit fromInt(final int code) {
            switch (code) {
                case 0:
                    return CLEAR_AND_DRY;
                case 1:
                    return DAMP;
                case 2:
                    return WET_WITH_WATER_PATCHES;
                case 3:
                    return RIME_AND_FROST_COVERED;
                case 4:
                    return DRY_SNOW;
                case 5:
                    return WET_SNOW;
                case 6:
                    return SLUSH;
                case 7:
                    return ICE;
                case 8:
                    return COMPACT_OR_ROLLED_SNOW;
                case 9:
                    return FROZEN_RUTS_OR_RIDGES;
                case 15:
                    return MISSING_OR_NOT_REPORTED;
                default:
                    throw new IllegalArgumentException("No value for code " + code);
            }
        }

        private final int code;

        RunwayDeposit(final int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }
    }

    enum RunwayContamination {
        PCT_COVERED_LESS_THAN_10(1), PCT_COVERED_11_25(2), PCT_COVERED_26_50(5), PCT_COVERED_51_100(9), MISSING_OR_NOT_REPORTED(15);

        public static RunwayContamination fromInt(final int code) {
            switch (code) {
                case 1:
                    return PCT_COVERED_LESS_THAN_10;
                case 2:
                    return PCT_COVERED_11_25;
                case 5:
                    return PCT_COVERED_26_50;
                case 9:
                    return PCT_COVERED_51_100;
                case 15:
                    return MISSING_OR_NOT_REPORTED;
                default:
                    throw new IllegalArgumentException("No value for code " + code);
            }
        }

        private final int code;

        RunwayContamination(final int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }
    }

    enum BrakingAction {
        POOR(91), MEDIUM_POOR(92), MEDIUM(93), MEDIUM_GOOD(94), GOOD(95);

        private final int code;

        public static BrakingAction fromInt(final int code) {
            switch (code) {
                case 91:
                    return POOR;
                case 92:
                    return MEDIUM_POOR;
                case 93:
                    return MEDIUM;
                case 94:
                    return MEDIUM_GOOD;
                case 95:
                    return GOOD;
                default:
                    return null;
            }
        }

        BrakingAction(final int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }
    }

    enum TrendForecastChangeIndicator {
        BECOMING(1), TEMPORARY_FLUCTUATIONS(2);

        public static TrendForecastChangeIndicator fromInt(final int code) {
            switch (code) {
                case 1:
                    return BECOMING;
                case 2:
                    return TEMPORARY_FLUCTUATIONS;
                default:
                    throw new IllegalArgumentException("No value for code " + code);
            }
        }

        private final int code;

        TrendForecastChangeIndicator(final int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }
    }

    enum TAFChangeIndicator {
        BECOMING(1),
        TEMPORARY_FLUCTUATIONS(2),
        FROM(3),
        PROBABILITY_30(4),
        PROBABILITY_30_TEMPORARY_FLUCTUATIONS(5),
        PROBABILITY_40(6),
        PROBABILITY_40_TEMPORARY_FLUCTUATIONS(7);

        public static TAFChangeIndicator fromInt(final int code) {
            switch (code) {
                case 1:
                    return BECOMING;
                case 2:
                    return TEMPORARY_FLUCTUATIONS;
                case 3:
                    return FROM;
                case 4:
                    return PROBABILITY_30;
                case 5:
                    return PROBABILITY_30_TEMPORARY_FLUCTUATIONS;
                case 6:
                    return PROBABILITY_40;
                case 7:
                    return PROBABILITY_40_TEMPORARY_FLUCTUATIONS;
                default:
                    throw new IllegalArgumentException("No value for code " + code);
            }
        }

        private final int code;

        TAFChangeIndicator(final int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }
    }

    enum ColorState {
        BLU(2500, 8000),
        WHT(1500, 5000),
        GRN(700, 3700),
        YLO1(500, 2500),
        YLO2(300, 1600),
        AMB(200, 800),
        RED(200, 800, BELOW),
        BLACK(-1, -1, null, true),
        BLACKBLU(2500, 8000, null, true),
        BLACKWHT(1500, 5000, null, true),
        BLACKGRN(700, 3700, null, true),
        BLACKYLO1(500, 2500, null, true),
        BLACKYLO2(300, 1600, null, true),
        BLACKAMB(200, 800, null, true),
        BLACKRED(200, 800, BELOW, true);

        private final double minCloudHeight;
        private final double minVisibility;
        private final boolean unusable;
        private final RelationalOperator operator;

        ColorState(final double cloudHeight, final double visibility) {
            this(cloudHeight, visibility, null, false);
        }

        ColorState(final double cloudHeight, final double visibility, final RelationalOperator operator) {
            this(cloudHeight, visibility, operator, false);
        }

        ColorState(final double cloudHeight, final double visibility, final RelationalOperator operator, final boolean unusable) {
            this.minCloudHeight = cloudHeight;
            this.minVisibility = visibility;
            this.operator = operator;
            this.unusable = unusable;
        }

        public double getMinCloudHeight() {
            return minCloudHeight;
        }

        public double getMinVisibility() {
            return minVisibility;
        }

        public boolean isUnusable() {
            return unusable;
        }

        public RelationalOperator getOperator() {
            return operator;
        }
    }

    enum PermissibleUsage {OPERATIONAL, NON_OPERATIONAL}

    enum PermissibleUsageReason {TEST, EXERCISE}

    //From: http://codes.wmo.int/49-2/SigWxPhenomena
    enum AeronauticalSignificantWeatherPhenomenon {
        EMBD_TS("EMBD_TS"),
        EMBD_TSGR("EMBD_TSGR"),
        FRQ_TS("FRQ_TS"),
        FRQ_TSGR("FRQ_TSGR"),
        HVY_DS("HVY_DS"),
        HVY_SS("HVY_SS"),
        OBSC_TS("OBSC_TS"),
        OBSC_TSGR("OBSC_TSGR"),
        RDOACT_CLD("RDOACT_CLD"),
        SEV_ICE("SEV_ICE"),
        SEV_ICE_FZRA("SEV_ICE_FZRA"),
        SEV_MTW("SEV_MTW"),
        SEV_TURB("SEV_TURB"),
        SQL_TS("SQL_TS"),
        SQL_TSGR("SQL_TSGR"),
        TC("TC"),
        VA("VA");

        private String text;

        AeronauticalSignificantWeatherPhenomenon(final String phen) {
            this.text = phen;
        }

        public String getText() {
            return this.text;
        }

        public AeronauticalSignificantWeatherPhenomenon fromString(String phen) {
            for (AeronauticalSignificantWeatherPhenomenon ph : AeronauticalSignificantWeatherPhenomenon.values()) {
                if (ph.getText().equals(phen)) {
                    return ph;
                }
            }
            return null;
        }

    }

    enum SigmetAirmetReportStatus {
        NORMAL(0), CANCELLATION(1);

        public static SigmetAirmetReportStatus fromInt(final int code) {
            switch (code) {
                case 0:
                    return NORMAL;
                case 1:
                    return CANCELLATION;
                default:
                    throw new IllegalArgumentException("No value for code " + code);
            }
        }

        private final int code;

        SigmetAirmetReportStatus(final int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }
    }

    enum SigmetEvolvingConditionCollectionTimeIndicatorType {
        OBSERVATION, FORECAST
    }

    //From: http://codes.wmo.int/49-2/WeatherCausingVisibilityReduction
    enum WeatherCausingVisibilityReduction {
        DZ("DZ", "Drizzle"),
        DU("DU", "Dust"),
        PO("PO", "Dust/sand whirls"),
        DS("DS", "Duststorm"),
        FG("FG", "Fog"),
        FC("FC", "Funnel cloud"),
        GR("GR", "Hail"),
        HZ("HZ", "Haze"),
        PL("PL", "Ice Pellets"),
        BR("BR", "Mist"),
        RA("RA", "Rain"),
        SA("SA", "Sand"),
        SS("SS", "Sandstorm"),
        GS("GS", "Small hail"),
        FU("FU", "Smoke"),
        SN("SN", "Snow"),
        SG("SG", "Snow grams"),
        SQ("SQ", "Squall"),
        VA("VA", "Volcanic Ash");

        private String text;
        private String description;

        public String getText() {
            return text;
        }

        public String getDescription() {
            return description;
        }

        WeatherCausingVisibilityReduction(final String s, final String description) {
            this.text = s;
            this.description = description;
        }

        public static WeatherCausingVisibilityReduction fromString(String weather) {
            for (WeatherCausingVisibilityReduction ph : WeatherCausingVisibilityReduction.values()) {
                if (ph.getText().equals(weather)) {
                    return ph;
                }
            }
            return null;
        }
    }

    enum AirmetPhenomenonParamInfo {
        NEEDS_OBSCURATION, NEEDS_WIND, NEEDS_CLOUDLEVELS
    }

    //From: http://codes.wmo.int/49-2/AirWxPhenomena
    enum AeronauticalAirmetWeatherPhenomenon {
        BKN_CLD("BKN_CLD", AirmetPhenomenonParamInfo.NEEDS_CLOUDLEVELS),
        FRQ_CB("FRQ_CB"),
        FRQ_TCU("FRQ_TCU"),
        ISOL_CB("ISOL_CB"),
        ISOL_TCU("ISOL_TCU"),
        ISOL_TS("ISOL_TS"),
        ISOL_TSGR("ISOL_TSGR"),
        MOD_ICE("MOD_ICE"),
        MOD_MTW("MOD_MTW"),
        MOD_TURB("MOD_TURB"),
        MT_OBSC("MT_OBSC"),
        OCNL_CB("OCNL_CB"),
        OCNL_TS("OCNL_TS"),
        OCNL_TSGR("OCNL_TSGR"),
        OCNL_TCU("OCNL_TCU"),
        OVC_CLD("OVC_CLD", AirmetPhenomenonParamInfo.NEEDS_CLOUDLEVELS),
        SFC_VIS("SFC_VIS", AirmetPhenomenonParamInfo.NEEDS_OBSCURATION),
        SFC_WIND("SFC_WIND", AirmetPhenomenonParamInfo.NEEDS_WIND);

        private String text;
        private Optional<AirmetPhenomenonParamInfo> info; //does parameter need extra info

        AeronauticalAirmetWeatherPhenomenon(final String phen) {
            this.text = phen;
            this.info = Optional.empty();
        }

        AeronauticalAirmetWeatherPhenomenon(final String phen, final AirmetPhenomenonParamInfo info) {
            this.text = phen;
            this.info = Optional.of(info);
        }

        public String getText() {
            return this.text;
        }

        public Optional<AirmetPhenomenonParamInfo> getInfo() {
            return this.info;
        }

        public AeronauticalAirmetWeatherPhenomenon fromString(String phen) {
            for (AeronauticalAirmetWeatherPhenomenon ph : AeronauticalAirmetWeatherPhenomenon.values()) {
                if (ph.getText().equals(phen)) {
                    return ph;
                }
            }
            return null;
        }
    }

    enum MessageType {
        TAF,
        METAR,
        SPECI,
        SIGMET,
        GAFOR,
        AIRMET,
        TROPICAL_CYCLONE_ADVISORY,
        VOLCANIC_ASH_ADVISORY,
        BULLETIN,
        GENERIC,
        LOW_WIND,
        WX_WARNING,
        SPECIAL_AIR_REPORT,
        WXREP,
        SPACE_WEATHER_ADVISORY
    }
}
