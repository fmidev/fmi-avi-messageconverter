package fi.fmi.avi.model.swx;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpaceWeatherPhenomenon {

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

    public static SpaceWeatherPhenomenon fromWMOCodeListValue(final String value) {
        final Matcher m = WMO_CODELIST_PATTERN.matcher(value);
        if (m.matches()) {
            final String code = m.group("value");
            return fromCombinedCode(code, '_');
        }
        throw new IllegalArgumentException("Value '" + value + "' is not valid WMO 49-2 SpaceWxPhenomena value");
    }

    public static SpaceWeatherPhenomenon fromCombinedCode(final String code) {
        return fromCombinedCode(code, ' ');
    }

    public static SpaceWeatherPhenomenon fromCombinedCode(final String code, final char separator) {
        if (code.lastIndexOf(separator) > -1) {
            final String sevStr = code.substring(code.lastIndexOf(separator) + 1);
            final Severity severity = Severity.fromString(sevStr);
            if (severity == null) {
                throw new IllegalArgumentException("Value '" + sevStr + "' is not valid SpaceWeatherPhenomenon.Severity value");
            }
            final String typeStr = code.substring(0, code.lastIndexOf(separator)).replace(separator, ' ');
            final Type type = Type.fromString(typeStr);
            if (type == null) {
                throw new IllegalArgumentException("Value '" + typeStr + "' is not valid SpaceWeatherPhenomenon.Type value");
            }
            return new SpaceWeatherPhenomenon(type, severity);
        }
        throw new IllegalArgumentException("Value '" + code + "' is not a combination of severity and type separated by a '" + separator + "'");
    }

    private static final String CODELIST_BASE = "http://codes.wmo.int/49-2/SpaceWxPhenomena/";
    private static final Pattern WMO_CODELIST_PATTERN = Pattern.compile("^(?<protocol>[a-z]*)://codes\\.wmo\\.int/49-2/SpaceWxPhenomena/(?<value>[A-Z_0-9]*)$");

    private Type type;
    private Severity severity;

    public SpaceWeatherPhenomenon() {
    }

    public SpaceWeatherPhenomenon(final String combinedCode) {
        final SpaceWeatherPhenomenon s = SpaceWeatherPhenomenon.fromCombinedCode(combinedCode);
        this.type = s.type;
        this.severity = s.severity;
    }

    public SpaceWeatherPhenomenon(final Type type, final Severity severity) {
        this.type = type;
        this.severity = severity;
    }

    public Type getType() {
        return type;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public void setSeverity(final Severity severity) {
        this.severity = severity;
    }

    public String asWMOCodeListValue() {
        return CODELIST_BASE + this.asCombinedCode('_');
    }

    public String asCombinedCode() {
        return this.type.getCode() + ' ' + this.severity.getCode();
    }

    public String asCombinedCode(final char separator) {
        return this.type.getCode().replace(' ', separator) + separator + this.severity.getCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SpaceWeatherPhenomenon that = (SpaceWeatherPhenomenon) o;
        return type == that.type && severity == that.severity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, severity);
    }

}
