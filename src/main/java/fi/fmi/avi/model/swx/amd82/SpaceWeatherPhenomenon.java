package fi.fmi.avi.model.swx.amd82;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

public enum SpaceWeatherPhenomenon {
    GNSS_MOD(Type.GNSS_BASED_NAVIGATION_AND_SURVEILLANCE, Severity.MODERATE), //
    GNSS_SEV(Type.GNSS_BASED_NAVIGATION_AND_SURVEILLANCE, Severity.SEVERE), //
    HF_COM_MOD(Type.HF_COMMUNICATIONS, Severity.MODERATE), //
    HF_COM_SEV(Type.HF_COMMUNICATIONS, Severity.SEVERE), //
    RADIATION_MOD(Type.RADIATION_AT_FLIGHT_LEVELS, Severity.MODERATE), //
    RADIATION_SEV(Type.RADIATION_AT_FLIGHT_LEVELS, Severity.SEVERE), //
    SATCOM_MOD(Type.COMMUNICATIONS_VIA_SATELLITE, Severity.MODERATE), //
    SATCOM_SEV(Type.COMMUNICATIONS_VIA_SATELLITE, Severity.SEVERE);

    private static final String CODELIST_BASE = "http://codes.wmo.int/49-2/SpaceWxPhenomena/";
    private static final Pattern WMO_CODELIST_PATTERN = Pattern.compile("^(?<protocol>[a-z]+)://codes\\.wmo\\.int/49-2/SpaceWxPhenomena/(?<value>[A-Z_0-9]+)$");

    private final Type type;
    private final Severity severity;

    SpaceWeatherPhenomenon(final Type type, final Severity severity) {
        this.type = requireNonNull(type, "type");
        this.severity = requireNonNull(severity, "severity");
    }

    public static SpaceWeatherPhenomenon from(final Type type, final Severity severity) {
        requireNonNull(type, "type");
        requireNonNull(severity, "severity");
        for (final SpaceWeatherPhenomenon value : values()) {
            if (value.getType().equals(type) && value.getSeverity().equals(severity)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Illegal combination of type and severity: " + type + ", " + severity);
    }

    public static SpaceWeatherPhenomenon fromCombinedCode(final String code) {
        return fromCombinedCode(code, ' ');
    }

    private static SpaceWeatherPhenomenon fromCombinedCode(final String code, final char wordSeparator) {
        if (wordSeparator != '_' && code.indexOf('_') >= 0) {
            throw new IllegalArgumentException("Illegal code: " + code);
        }
        return valueOf(code.replace(wordSeparator, '_'));
    }

    public static SpaceWeatherPhenomenon fromWMOCodeListValue(final String value) {
        final Matcher m = WMO_CODELIST_PATTERN.matcher(value);
        if (m.matches()) {
            final String code = m.group("value");
            return fromCombinedCode(code, '_');
        }
        throw new IllegalArgumentException("Value '" + value + "' is not valid WMO 49-2 SpaceWxPhenomena value");
    }

    public Type getType() {
        return type;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String asCombinedCode() {
        return type.getCode() + ' ' + severity.getCode();
    }

    public String asWMOCodeListValue() {
        return CODELIST_BASE + (type.getCode().replace(' ', '_') + '_' + severity.getCode());
    }

    public enum Type {
        GNSS_BASED_NAVIGATION_AND_SURVEILLANCE("GNSS"), //
        HF_COMMUNICATIONS("HF COM"), //
        RADIATION_AT_FLIGHT_LEVELS("RADIATION"), //
        COMMUNICATIONS_VIA_SATELLITE("SATCOM");

        private final String code;

        Type(final String code) {
            this.code = code;
        }

        public static Type fromString(final String code) {
            for (final Type t : Type.values()) {
                if (t.getCode().equals(code)) {
                    return t;
                }
            }
            throw new IllegalArgumentException("Type code \" " + code + "\" could not be resolved");
        }

        public String getCode() {
            return this.code;
        }
    }

    public enum Severity {
        MODERATE("MOD"), SEVERE("SEV");

        private final String code;

        Severity(final String code) {
            this.code = code;
        }

        public static Severity fromString(final String code) {
            for (final Severity t : Severity.values()) {
                if (t.getCode().equals(code)) {
                    return t;
                }
            }
            throw new IllegalArgumentException("Severity code \" " + code + "\" could not be resolved");
        }

        public String getCode() {
            return this.code;
        }
    }
}
