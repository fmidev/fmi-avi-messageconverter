package fi.fmi.avi.model.sigmet;

import java.util.List;
import java.util.Optional;

import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.SIGMETAIRMET;
import fi.fmi.avi.model.UnitPropertyGroup;
import fi.fmi.avi.model.VolcanoDescription;

public interface SIGMET extends SIGMETAIRMET {
    AeronauticalSignificantWeatherPhenomenon getSigmetPhenomenon();
    Optional<SigmetReference> getCancelledReference();

    public SigmetAnalysisType getAnalysisType();
    public Optional<List<PhenomenonGeometryWithHeight>> getAnalysisGeometries();

    public Optional<NumericMeasure> getMovingSpeed();
    public Optional<NumericMeasure> getMovingDirection();

    public Optional<SigmetIntensityChange> getIntensityChange();

    public Optional<List<PhenomenonGeometry>> getForecastGeometries();

    public Optional<Boolean> getNoVaExpected(); //Only applicable to ForecastPositionAnalysis

    Optional<VAInfo> getVAInfo(); //If this is present this is a VASigmet
}
