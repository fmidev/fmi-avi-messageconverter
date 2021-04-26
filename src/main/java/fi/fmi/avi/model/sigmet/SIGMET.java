package fi.fmi.avi.model.sigmet;

import java.util.List;
import java.util.Optional;

import fi.fmi.avi.model.PhenomenonGeometry;
import fi.fmi.avi.model.PhenomenonGeometryWithHeight;
import fi.fmi.avi.model.SIGMETAIRMET;

public interface SIGMET extends SIGMETAIRMET {
    Optional<AeronauticalSignificantWeatherPhenomenon> getSigmetPhenomenon();

    Optional<SigmetReference> getCancelledReference();

    Optional<List<PhenomenonGeometryWithHeight>> getAnalysisGeometries();

    Optional<List<PhenomenonGeometry>> getForecastGeometries();

    Optional<Boolean> getNoVaExpected(); //Only applicable to ForecastPositionAnalysis

    Optional<VAInfo> getVAInfo(); //If this is present this is a VASigmet
}
