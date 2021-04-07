package fi.fmi.avi.model.sigmet;

import java.util.List;
import java.util.Optional;

import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.PhenomenonGeometryWithHeight;
import fi.fmi.avi.model.SIGMETAIRMET;

public interface AIRMET extends SIGMETAIRMET {
    AeronauticalAirmetWeatherPhenomenon getAirmetPhenomenon();

    Optional<AirmetCloudLevels> getCloudLevels();

    Optional<AirmetWind> getWind();

    Optional<List<WeatherCausingVisibilityReduction>> getObscuration();

    Optional<NumericMeasure> getVisibility();

    Optional<AirmetReference> getCancelledReference();

    SigmetAnalysisType getAnalysisType();

    Optional<List<PhenomenonGeometryWithHeight>> getAnalysisGeometries();

    Optional<NumericMeasure> getMovingSpeed();

    Optional<NumericMeasure> getMovingDirection();

    Optional<SigmetIntensityChange> getIntensityChange();

}
