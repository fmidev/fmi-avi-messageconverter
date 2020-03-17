package fi.fmi.avi.model.SpaceWeatherAdvisory;

import java.util.Optional;

import fi.fmi.avi.model.PhenomenonGeometryWithHeight;

public interface SpaceWeatherRegion {

    Optional<String> getLocationIndicator();

    Optional<PhenomenonGeometryWithHeight> getGeographiclocation();
}
