package fi.fmi.avi.model.sigmet;

import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.SIGMETAIRMET;

import java.util.List;
import java.util.Optional;

public interface AIRMET extends SIGMETAIRMET {
    Optional<AeronauticalAirmetWeatherPhenomenon> getPhenomenon();

    Optional<AirmetCloudLevels> getCloudLevels();

    Optional<AirmetWind> getWind();

    Optional<List<WeatherCausingVisibilityReduction>> getObscuration();

    Optional<NumericMeasure> getVisibility();

    Optional<Reference> getCancelledReference();

}
