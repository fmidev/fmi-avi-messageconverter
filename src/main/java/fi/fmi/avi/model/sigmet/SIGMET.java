package fi.fmi.avi.model.sigmet;

import fi.fmi.avi.model.AirTrafficServicesUnitWeatherMessage;
import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;

import java.util.List;
import java.util.Optional;

public interface SIGMET extends AirTrafficServicesUnitWeatherMessage, AviationCodeListUser {
    public String getSequenceNumber();
    public PartialOrCompleteTimePeriod getValidityPeriod();
    public AeronauticalSignificantWeatherPhenomenon getSigmetPhenomenon();
    public Optional<SigmetReference> getCancelledReference();

    public Optional<List<SigmetAnalysis>> getAnalysis();

    public Optional<String> getVolcanicAshMovedToFIR();

    public SigmetReportStatus getStatus();
}
