package fi.fmi.avi.model.swx.amd82;

import java.util.List;

public interface SpaceWeatherIntensityAndRegion {
    Intensity getIntensity();

    List<SpaceWeatherRegion> getRegions();
}
