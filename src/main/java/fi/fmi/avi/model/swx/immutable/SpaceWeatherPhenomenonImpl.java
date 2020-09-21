package fi.fmi.avi.model.swx.immutable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.fmi.avi.model.swx.SpaceWeatherPhenomenon;
import org.inferred.freebuilder.FreeBuilder;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@FreeBuilder
@JsonDeserialize(builder = SpaceWeatherPhenomenonImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "type", "severity" })
public abstract class SpaceWeatherPhenomenonImpl implements SpaceWeatherPhenomenon, Serializable {
    private static final String CODELIST_BASE = "http://codes.wmo.int/49-2/SpaceWxPhenomena/";

    public static Builder builder() {
        return new Builder();
    }

    public static SpaceWeatherPhenomenonImpl immutableCopyOf(final SpaceWeatherPhenomenon spaceWeatherPhenomenon) {
        Objects.requireNonNull(spaceWeatherPhenomenon);
        if (spaceWeatherPhenomenon instanceof SpaceWeatherPhenomenonImpl) {
            return (SpaceWeatherPhenomenonImpl) spaceWeatherPhenomenon;
        } else {
            return Builder.from(spaceWeatherPhenomenon).build();
        }
    }

    public String asCombinedCode(final char separator) {
        return getType().getCode().replace(' ', separator) + separator + getSeverity().getCode();
    }

    public String asWMOCodeListValue() {
        return CODELIST_BASE + asCombinedCode('_');
    }

    public String asCombinedCode() {
        return getType().getCode() + ' ' + getSeverity().getCode();
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
        return getType() == that.getType() && getSeverity() == that.getSeverity();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getSeverity());
    }

    public abstract SpaceWeatherPhenomenonImpl.Builder toBuilder();

    public static class Builder extends SpaceWeatherPhenomenonImpl_Builder {

        public static Builder from(final SpaceWeatherPhenomenon value) {
            if (value instanceof SpaceWeatherPhenomenonImpl) {
                return ((SpaceWeatherPhenomenonImpl) value).toBuilder();
            } else {
                return builder().setType(value.getType()).setSeverity(value.getSeverity());
            }
        }

        @Override
        @JsonDeserialize(as = Severity.class)
        public Builder setSeverity(final Severity severity) {
            return super.setSeverity(severity);
        }

        @Override
        @JsonDeserialize(as = Type.class)
        public Builder setType(final Type type) {
            return super.setType(type);
        }

        private static final Pattern WMO_CODELIST_PATTERN =
                Pattern.compile("^(?<protocol>[a-z]*)://codes\\.wmo\\.int/49-2/SpaceWxPhenomena/(?<value>[A-Z_0-9]*)$");

        public static Builder fromCombinedCode(final String code) {
            return fromCombinedCode(code, ' ');
        }

        public static Builder fromCombinedCode(final String code, final char separator) {
            if (code.lastIndexOf(separator) > -1) {
                final String sevStr = code.substring(code.lastIndexOf(separator) + 1);
                final SpaceWeatherPhenomenon.Severity severity = SpaceWeatherPhenomenon.Severity.fromString(sevStr);
                if (severity == null) {
                    throw new IllegalArgumentException("Value '" + sevStr + "' is not valid SpaceWeatherPhenomenon.Severity value");
                }
                final String typeStr = code.substring(0, code.lastIndexOf(separator)).replace(separator, ' ');
                final SpaceWeatherPhenomenon.Type type = SpaceWeatherPhenomenon.Type.fromString(typeStr);
                if (type == null) {
                    throw new IllegalArgumentException("Value '" + typeStr + "' is not valid SpaceWeatherPhenomenon.Type value");
                }
                return new Builder().setSeverity(severity).setType(type);
            }
            throw new IllegalArgumentException("Value '" + code + "' is not a combination of severity and type separated by a '" + separator + "'");
        }

        public static Builder fromWMOCodeListValue(final String value) {
            final Matcher m = WMO_CODELIST_PATTERN.matcher(value);
            if (m.matches()) {
                final String code = m.group("value");
                return fromCombinedCode(code, '_');
            }
            throw new IllegalArgumentException("Value '" + value + "' is not valid WMO 49-2 SpaceWxPhenomena value");
        }
    }

}
