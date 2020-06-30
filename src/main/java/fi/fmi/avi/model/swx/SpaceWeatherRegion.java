package fi.fmi.avi.model.swx;

import fi.fmi.avi.model.AviationWeatherMessageOrCollection;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface SpaceWeatherRegion extends AviationWeatherMessageOrCollection {

    Optional<AirspaceVolume> getAirSpaceVolume();

    Optional<SpaceWeatherLocation> getLocationIndicator();

    enum SpaceWeatherLocation {
        HIGH_NORTHERN_HEMISPHERE("HNH", -90d, -60d),
        MIDDLE_NORTHERN_HEMISPHERE("MNH", -60d, -30d),
        EQUATORIAL_LATITUDES_NORTHERN_HEMISPHERE("EQN", -30d, 0d),
        EQUATORIAL_LATITUDES_SOUTHERN_HEMISPHERE("EQS", 0d, 30d),
        MIDDLE_LATITUDES_SOUTHERN_HEMISPHERE("MSH", 30d, 60d),
        HIGH_LATITUDES_SOUTHERN_HEMISPHERE("HSH", 60d, 90d),
        DAYLIGHT_SIDE("DAYLIGHT_SIDE", null, null);

        private static final String CODELIST_BASE = "http://codes.wmo.int/49-2/SpaceWxLocation/";
        private static final Pattern WMO_CODELIST_PATTERN = Pattern.compile(
                "^(?<protocol>[a-z]*)://codes\\.wmo\\.int/49-2/SpaceWxLocation/" + "(?<value>[A-Z_0-9]*)$");

        private final String code;
        private final Double latitudeBandMinCoordinate;
        private final Double latitudeBandMaxCoordinate;

        SpaceWeatherLocation(final String code, final Double minLat, final Double maxLat) {
            this.code = code;
            this.latitudeBandMinCoordinate = minLat;
            this.latitudeBandMaxCoordinate = maxLat;
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

        public Double getLatitudeBandMinCoordinate() {
            return this.latitudeBandMinCoordinate;
        }

        public Double getLatitudeBandMaxCoordinate() {
            return this.latitudeBandMaxCoordinate;
        }

        public String asWMOCodeListValue() {
            return CODELIST_BASE + this.getCode();
        }
    }
}
