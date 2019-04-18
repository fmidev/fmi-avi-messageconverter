package fi.fmi.avi.model.sigmet;

import java.util.List;
import java.util.Optional;

import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.SIGMETAIRMET;

public interface AIRMET extends SIGMETAIRMET {
    AeronauticalAirmetWeatherPhenomenon getAirmetPhenomenon();
    Optional<AirmetCloudLevels>getCloudLevels();
    Optional<AirmetWind>getWind();
    Optional<List<WeatherCausingVisibilityReduction>>getObscuration();
    Optional<NumericMeasure>getVisibility();

    Optional<AirmetReference> getCancelledReference();

    public SigmetAnalysisType getAnalysisType();
    public Optional<List<PhenomenonGeometryWithHeight>> getAnalysisGeometries();

    public Optional<NumericMeasure> getMovingSpeed();
    public Optional<NumericMeasure> getMovingDirection();

    public Optional<SigmetIntensityChange> getIntensityChange();

}
