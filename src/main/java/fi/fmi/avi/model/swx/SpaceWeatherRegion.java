package fi.fmi.avi.model.swx;

import java.util.Optional;

public interface SpaceWeatherRegion {

    Optional<AirspaceVolume> getAirSpaceVolume();

    Optional<String> getLocationIndicator();

    Optional<String> getTac();
}
