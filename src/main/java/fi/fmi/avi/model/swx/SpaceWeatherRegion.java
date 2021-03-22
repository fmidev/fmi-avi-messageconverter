package fi.fmi.avi.model.swx;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface SpaceWeatherRegion {

    Optional<AirspaceVolume> getAirSpaceVolume();

    Optional<SpaceWeatherLocation> getLocationIndicator();

    Optional<Double> getLongitudeLimitMinimum();

    Optional<Double> getLongitudeLimitMaximum();

    enum SpaceWeatherLocation {
        HIGH_NORTHERN_HEMISPHERE("HNH", 90d, 60d),
        MIDDLE_NORTHERN_HEMISPHERE("MNH", 60d, 30d),
        EQUATORIAL_LATITUDES_NORTHERN_HEMISPHERE("EQN", 30d, 0d),
        EQUATORIAL_LATITUDES_SOUTHERN_HEMISPHERE("EQS", 0d, -30d),
        MIDDLE_LATITUDES_SOUTHERN_HEMISPHERE("MSH", -30d, -60d),
        HIGH_LATITUDES_SOUTHERN_HEMISPHERE("HSH", -60d, -90d),
        DAYLIGHT_SIDE("DAYLIGHT SIDE", Double.NaN, Double.NaN);

        private static final String CODELIST_BASE = "http://codes.wmo.int/49-2/SpaceWxLocation/";
        private static final Pattern WMO_CODELIST_PATTERN = Pattern.compile(
                "^(?<protocol>[a-z]*)://codes\\.wmo\\.int/49-2/SpaceWxLocation/" + "(?<value>[A-Z_0-9]*)$");

        private final String code;
        private final double latitudeBandMinCoordinate;
        private final double latitudeBandMaxCoordinate;

        SpaceWeatherLocation(final String code, final double minLat, final double maxLat) {
            this.code = code;
            this.latitudeBandMinCoordinate = minLat;
            this.latitudeBandMaxCoordinate = maxLat;
        }

        public static SpaceWeatherLocation fromWMOCodeListValue(final String value) {
            final Matcher m = WMO_CODELIST_PATTERN.matcher(value);
            if (m.matches()) {
                final String tacCode = m.group("value").replaceAll("_", " ");
                return fromTacCode(tacCode);
            }
            throw new IllegalArgumentException("Value '" + value + "' is not valid WMO 49-2 SpaceWxLocation value");
        }

        public static SpaceWeatherLocation fromTacCode(final String code) {
            for (final SpaceWeatherLocation loc : SpaceWeatherLocation.values()) {
                if (loc.getCode().equals(code)) {
                    return loc;
                }
            }
            throw new IllegalArgumentException("Value '" + code + "' is not valid SpaceWeatherLocation code value");
        }

        public String getCode() {
            return this.code;
        }

        public Optional<Double> getLatitudeBandMinCoordinate() {
            return Double.isNaN(this.latitudeBandMinCoordinate) ? Optional.empty() : Optional.of(this.latitudeBandMinCoordinate);
        }

        public Optional<Double> getLatitudeBandMaxCoordinate() {
            return Double.isNaN(this.latitudeBandMaxCoordinate) ? Optional.empty() : Optional.of(this.latitudeBandMaxCoordinate);
        }

        public String asWMOCodeListValue() {
            return CODELIST_BASE + getCode().replaceAll("\\s+", "_");
        }
    }
}
