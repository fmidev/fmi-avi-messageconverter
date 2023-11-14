package fi.fmi.avi.model.sigmet;

import fi.fmi.avi.model.PhenomenonGeometry;
import fi.fmi.avi.model.SIGMETAIRMET;

import java.util.List;
import java.util.Optional;

public interface SIGMET extends SIGMETAIRMET {

    SigmetPhenomenonType getPhenomenonType();

    Optional<AeronauticalSignificantWeatherPhenomenon> getPhenomenon();

    Optional<Reference> getCancelledReference();

    Optional<List<PhenomenonGeometry>> getForecastGeometries();

    Optional<VAInfo> getVAInfo(); // If this is present this is a VASigmet
}
