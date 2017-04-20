package fi.fmi.avi.data;

public interface AviationCodeListUser {

    String CODELIST_AERODROME_PRESENT_OR_FORECAST_WEATHER = "http://codes.wmo.int/49-2/AerodromePresentOrForecastWeather";
    String CODELIST_AERODROME_RECENT_WEATHER = "http://codes.wmo.int/49-2/AerodromeRecentWeather";
    String CODELIST_CLOUD_AMOUNT_REPORTED_AT_AERODROME = "http://codes.wmo.int/49-2/CloudAmountReportedAtAerodrome";
    String CODELIST_SIGNIFICANT_CONVECTIVE_CLOUD_TYPE = "http://codes.wmo.int/49-2/SigConvectiveCloudType";
    String CODELIST_SEA_SURFACE_STATE = "http://codes.wmo.int/bufr4/codeflag/0-22-061";
    String CODELIST_RUNWAY_DEPOSITS = "http://codes.wmo.int/bufr4/codeflag/0-20-086";
    String CODELIST_RUNWAY_CONTAMINATION = "http://codes.wmo.int/bufr4/codeflag/0-20-087";

    String CODELIST_VALUE_PREFIX_SIG_WEATHER = "http://codes.wmo.int/306/4678/";
    String CODELIST_VALUE_PREFIX_CLOUD_AMOUNT_REPORTED_AT_AERODROME = "http://codes.wmo.int/bufr4/codeflag/0-20-008/";
    String CODELIST_VALUE_PREFIX_SIG_CONVECTIVE_CLOUD_TYPE = "http://codes.wmo.int/bufr4/codeflag/0-20-012/";
    String CODELIST_VALUE_PREFIX_SEA_SURFACE_STATE = "http://codes.wmo.int/bufr4/codeflag/0-22-061/";
    String CODELIST_VALUE_PREFIX_RUNWAY_DEPOSITS = "http://codes.wmo.int/bufr4/codeflag/0-20-086/";
    String CODELIST_VALUE_PREFIX_RUNWAY_CONTAMINATION = "http://codes.wmo.int/bufr4/codeflag/0-20-087/";

    public enum MetarStatus {
        NORMAL(0), CORRECTION(1), MISSING(2);

        public static MetarStatus fromInt(final int code) {
            switch (code) {
                case 0: return NORMAL;
                case 1: return CORRECTION;
                case 2: return MISSING;
                default: throw new IllegalArgumentException("No value for code " + code);
            }
        }

        private int code;

        MetarStatus(final int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }

    }

    public enum TAFStatus {
        NORMAL(0), AMENDMENT(1), CANCELLATION(2), CORRECTION(3), MISSING(4);

        public static TAFStatus fromInt(final int code) {
            switch (code) {
                case 0: return NORMAL;
                case 1: return AMENDMENT;
                case 2: return CANCELLATION;
                case 3: return CORRECTION;
                case 4: return MISSING;
                default: throw new IllegalArgumentException("No value for code " + code);
            }
        }

        private int code;

        TAFStatus(final int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }

    }

    public enum MissingReason {
        NOT_MISSING(-1), INAPPLICABLE(0), MISSING(1), UNKNOWN(2), WITHHELD(3);

        public static MissingReason fromInt(final int code) {
            switch (code) {
                case -1: return NOT_MISSING;
                case 0: return INAPPLICABLE;
                case 1: return MISSING;
                case 2: return UNKNOWN;
                case 3: return WITHHELD;
                default: throw new IllegalArgumentException("No value for code " + code);
            }
        }

        private int code;

        MissingReason(final int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }

    }

    public enum RelationalOperator {
        NOT_GIVEN(-1), ABOVE(0), BELOW(1);

        public static RelationalOperator fromInt(final int code) {
            switch (code) {
                case -1: return NOT_GIVEN;
                case 0: return ABOVE;
                case 1: return BELOW;
                default: throw new IllegalArgumentException("No value for code " + code);
            }
        }

        private int code;

        RelationalOperator(final int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }

    }

    public enum VisualRangeTendency {
        NOT_GIVEN(-1), UPWARD(0), NO_CHANGE(1), DOWNWARD(2);

        public static VisualRangeTendency fromInt(final int code) {
            switch(code) {
                case -1: return NOT_GIVEN;
                case 0: return UPWARD;
                case 1: return NO_CHANGE;
                case 2: return DOWNWARD;
                default: throw new IllegalArgumentException("No value for code " + code);
            }
        }

        private int code;

        VisualRangeTendency(final int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }

    }

    public enum CloudAmount {
        SKC(0), FEW(1), SCT(2), BKN(3), OVC(4), ISOL(8), OCNL(10), FRQ(12), LYR(14), EMBD(16);

        public static CloudAmount fromInt(final int code) {
            switch(code) {
                case 0: return SKC;
                case 1: return FEW;
                case 2: return SCT;
                case 3: return BKN;
                case 4: return OVC;
                case 8: return ISOL;
                case 10: return OCNL;
                case 12: return FRQ;
                case 14: return LYR;
                case 16: return EMBD;
                default: throw new IllegalArgumentException("No value for code " + code);
            }
        }

        private int code;

        CloudAmount(final int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }
    }

    public enum CloudType {
        NOT_GIVEN(-1), CB(9), TCU(32);

        public static CloudType fromInt(final int code) {
           switch(code) {
               case -1: return NOT_GIVEN;
               case 9: return CB;
               case 32: return TCU;
               default: throw new IllegalArgumentException("No value for code " + code);
           }
        }

        private int code;

        CloudType(final int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }
    }

    public enum SeaSurfaceState {
        CALM_GLASSY(0), CALM_RIPPLED(1), SMOOTH_WAVELETS(2), SLIGHT(3), MODERATE(4),
        ROUGH(5), VERY_ROUGH(6), HIGH(7), VERY_HIGH(8), PHENOMENAL(9), MISSING_VALUE(15);

               public static SeaSurfaceState fromInt(final int code) {
           switch(code) {
               case 0: return CALM_GLASSY;
               case 1: return CALM_RIPPLED;
               case 2: return SMOOTH_WAVELETS;
               case 3: return SLIGHT;
               case 4: return MODERATE;
               case 5: return ROUGH;
               case 6: return VERY_ROUGH;
               case 7: return HIGH;
               case 8: return VERY_HIGH;
               case 9: return PHENOMENAL;
               case 15: return MISSING_VALUE;
               default: throw new IllegalArgumentException("No value for code " + code);
           }
        }

        private int code;

        SeaSurfaceState(final int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }
    }

    public enum RunwayDeposit {
        NOT_AVAILABLE(-1), CLEAR_AND_DRY(0), DAMP(1), WET_WITH_WATER_PATCHES(2), RIME_AND_FROST_COVERED(3), // (depth normally less than 1mm)
        DRY_SNOW(4), WET_SNOW(5), SLUSH(6), ICE(7), COMPACT_OR_ROLLED_SNOW(8), FROZEN_RUTS_OR_RIDGES(9),
        MISSING_OR_NOT_REPORTED(15);


        public static RunwayDeposit fromInt(final int code) {
           switch (code) {
               case -1: return NOT_AVAILABLE;
               case 0: return CLEAR_AND_DRY;
               case 1: return DAMP;
               case 2: return WET_WITH_WATER_PATCHES;
               case 3: return RIME_AND_FROST_COVERED;
               case 4: return DRY_SNOW;
               case 5: return WET_SNOW;
               case 6: return SLUSH;
               case 7: return ICE;
               case 8: return COMPACT_OR_ROLLED_SNOW;
               case 9: return FROZEN_RUTS_OR_RIDGES;
               case 15: return MISSING_OR_NOT_REPORTED;
               default: throw new IllegalArgumentException("No value for code " + code);
           }
        }

        private int code;

        RunwayDeposit(final int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }
    }

    public enum RunwayContamination {
        NOT_AVAILABLE(-1), PCT_COVERED_LESS_THAN_10(1), PCT_COVERED_11_25(2), PCT_COVERED_26_50(5),
        PCT_COVERED_51_100(9), MISSING_OR_NOT_REPORTED(15);


        public static RunwayContamination fromInt(final int code) {
          switch (code) {
              case -1: return NOT_AVAILABLE;
              case 1: return PCT_COVERED_LESS_THAN_10;
              case 2: return PCT_COVERED_11_25;
              case 5:
                  return PCT_COVERED_26_50;
              case 9: return PCT_COVERED_51_100;
              case 15: return MISSING_OR_NOT_REPORTED;
              default: throw new IllegalArgumentException("No value for code " + code);
          }
        }

        private int code;

        RunwayContamination(final int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }
    }
    
    public enum BreakingAction {
    	POOR(91), MEDIUM_POOR(92), MEDIUM(93), MEDIUM_GOOD(94), GOOD(95);
    	
    	private int code;
    	
    	public static BreakingAction fromInt(final int code) {
    		switch(code) {
    		case 91: return POOR;
    		case 92: return MEDIUM_POOR;
    		case 93: return BreakingAction.MEDIUM;
    		case 94: return MEDIUM_GOOD;
    		case 95: return GOOD;
    		default: return null;
    		}
    	}
    	
    	BreakingAction(final int code) {
    		this.code = code;
    	}
    	
    	public int getCode() {
    		return this.code;
    	}
    }

    public enum TrendForecastChangeIndicator {
        NO_SIGNIFICANT_CHANGES(0), BECOMING(1), TEMPORARY_FLUCTUATIONS(2);

        public static TrendForecastChangeIndicator fromInt(final int code) {
            switch (code) {
                case 0: return NO_SIGNIFICANT_CHANGES;
                case 1: return BECOMING;
                case 2: return TEMPORARY_FLUCTUATIONS;
                default: throw new IllegalArgumentException("No value for code " + code);
            }
        }

        private int code;

        TrendForecastChangeIndicator(final int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }
    }

    public enum TAFChangeIndicator {
        BECOMING(1),
        TEMPORARY_FLUCTUATIONS(2),
        FROM(3),
        PROBABILITY_30(4),
        PROBABILITY_30_TEMPORARY_FLUCTUATIONS(5),
        PROBABILITY_40(6),
        PROBABILITY_40_TEMPORARY_FLUCTUATIONS(7);

        public static TAFChangeIndicator fromInt(final int code) {
            switch (code) {
                case 1: return BECOMING;
                case 2: return TEMPORARY_FLUCTUATIONS;
                case 3: return FROM;
                case 4: return PROBABILITY_30;
                case 5: return PROBABILITY_30_TEMPORARY_FLUCTUATIONS;
                case 6: return PROBABILITY_40;
                case 7: return PROBABILITY_40_TEMPORARY_FLUCTUATIONS;
                default: throw new IllegalArgumentException("No value for code " + code);
            }
        }

        private int code;

        TAFChangeIndicator(final int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }
    }
    
    public enum WeatherCodeKind {
        SHALLOW("MI"),
        PATCHES("BC"),
        PARTIAL("PR"),
        DRIFTING("DR"),
        BLOWING("BL"),
        SHOWER("SH"),
        THUNDERSTORM("TS"),
        FREEZING("FZ"),
        DRIZZLE("DZ"),
        RAIN("RA"),
        SNOW("SN"),
        SNOW_GRAINS("SG"),
        ICE_CRYSTALS("IC"),
        PELLETS("PL"),
        HAIL("GR"),
        SNOW_PELLETS("GS"),
        UNKNOWN_PRECIPITATION("UP"),
        MIST("BR"),
        FOG("FG"),
        SMOKE("FU"),
        VOLCANIC_ASH("VA"),
        DUST("DU"),
        SAND("SA"),
        HAZE("HZ"),
        DUST_DEVILS("PO"),
        SQUALLS("SQ"),
        TORNADO_WATER_SPOUT("+FC"),
        FUNNEL_CLOUD("FC"),
        SANDSTORM("SS"),
        DUSTSTORM("DS");
    	
    	private String code;
    	
    	WeatherCodeKind(final String code) {
    		this.code = code;
    	}
    	
    	public String getCode() {
    		return this.code;
    	}
    	
    	public static WeatherCodeKind forCode(final String code) {
            for (WeatherCodeKind w : values()) {
                if (w.code.equals(code)) {
                    return w;
                }
            }
            return null;
        }
    }
    
    public enum WeatherCodeIntensity {
    	HIGH('+'), LOW('-');
    	private char code;
    	
    	WeatherCodeIntensity(final char code) {
    		this.code = code;
    	}
    	public char getCode() {
    		return this.code;
    	}
    }
}
