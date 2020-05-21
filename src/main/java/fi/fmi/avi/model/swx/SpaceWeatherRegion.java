package fi.fmi.avi.model.swx;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface SpaceWeatherRegion {

    Optional<AirspaceVolume> getAirSpaceVolume();

    Optional<SpaceWeatherLocation> getLocationIndicator();

    Optional<String> getTac();

    enum SpaceWeatherLocation {

        EQUATORIAL_LATITUDES_NORTHERN_HEMISPHERE("EQN"),
        MIDDLE_NORTHERN_HEMISPHERE("MNH"),
        DAYLIGHT_SIDE("DAYLIGHT_SIDE"),
        EQUATORIAL_LATITUDES_SOUTHERN_HEMISPHERE("EQS"),
        MIDDLE_LATITUDES_SOUTHERN_HEMISPHERE("MSH"),
        HIGH_LATITUDES_SOUTHERN_HEMISPHERE("HSH"),
        HIGH_NORTHERN_HEMISPHERE("HNH");

        private static final String CODELIST_BASE = "http://codes.wmo.int/49-2/SpaceWxLocation/";
        private static final Pattern WMO_CODELIST_PATTERN = Pattern.compile(
                "^(?<protocol>[a-z]*)://codes\\.wmo\\.int/49-2/SpaceWxLocation/" + "(?<value>[A-Z_0-9]*)$");

        private final String code;

        SpaceWeatherLocation(final String code) {
            this.code = code;
        }

        public static SpaceWeatherLocation fromWMOCodeListValue(final String value) {
            final Matcher m = WMO_CODELIST_PATTERN.matcher(value);
            if (m.matches()) {
                return fromCode(m.group("value"));
            }
            throw new IllegalArgumentException("Value '" + value + "' is not valid WMO 49-2 SpaceWxLocation value");
        }

        public static SpaceWeatherLocation fromCode(String code) {
            for (SpaceWeatherLocation loc : SpaceWeatherLocation.values()) {
                if (loc.getCode().equals(code)) {
                    return loc;
                }
            }
            return null;
        }

        public String getCode() {
            return this.code;
        }

        public String asWMOCodeListValue() {
            return CODELIST_BASE + this.getCode();
        }
    }
}
