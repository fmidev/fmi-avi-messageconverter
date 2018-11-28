package fi.fmi.avi.model.sigmet;

import fi.fmi.avi.model.AirTrafficServicesUnitWeatherMessage;
import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.SIGMETAIRMET;

import java.util.List;
import java.util.Optional;

public interface SIGMET extends SIGMETAIRMET {
    AeronauticalSignificantWeatherPhenomenon getSigmetPhenomenon();
    Optional<SigmetReference> getCancelledReference();

    Optional<List<SigmetAnalysis>> getAnalysis();
}
