package fi.fmi.avi.model.sigmet;

import java.util.List;
import java.util.Optional;

import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.PhenomenonGeometryWithHeight;
import fi.fmi.avi.model.SIGMETAIRMET;

public interface AIRMET extends SIGMETAIRMET {
    Optional<AeronauticalAirmetWeatherPhenomenon> getPhenomenon();

    Optional<AirmetCloudLevels> getCloudLevels();

    Optional<AirmetWind> getWind();

    Optional<List<WeatherCausingVisibilityReduction>> getObscuration();

    Optional<NumericMeasure> getVisibility();

    Optional<Reference> getCancelledReference();

    Optional<List<PhenomenonGeometryWithHeight>> getAnalysisGeometries();
}
