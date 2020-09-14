package fi.fmi.avi.model.swx;

public interface SpaceWeatherPhenomenon {
    enum Type {
        GNSS_BASED_NAVIGATION_AND_SURVEILLANCE("GNSS"),//
        HF_COMMUNICATIONS("HF COM"),//
        RADIATION_AT_FLIGHT_LEVELS("RADIATION"),//
        COMMUNICATIONS_VIA_SATELLITE("SATCOM");

        private final String code;

        Type(final String code) {
            this.code = code;
        }

        public String getCode() {
            return this.code;
        }

        public static Type fromString(final String code) {
            for (final Type t : Type.values()) {
                if (t.getCode().equals(code)) {
                    return t;
                }
            }
            return null;
        }
    }

    enum Severity {
        MODERATE("MOD"), SEVERE("SEV");

        private final String code;

        Severity(final String code) {
            this.code = code;
        }

        public String getCode() {
            return this.code;
        }

        public static Severity fromString(final String code) {
            for (final Severity t : Severity.values()) {
                if (t.getCode().equals(code)) {
                    return t;
                }
            }
            return null;
        }

    }

    Type getType();

    Severity getSeverity();

    String asCombinedCode();

    String asWMOCodeListValue();
}
