package fi.fmi.avi.model;

import java.util.List;
import java.util.Optional;

public interface SIGMETAIRMET extends AirTrafficServicesUnitWeatherMessage, AviationCodeListUser {
    String getSequenceNumber();

    PartialOrCompleteTimePeriod getValidityPeriod();

    Airspace getAirspace();

    Optional<List<PhenomenonGeometryWithHeight>> getAnalysisGeometries();

    @Deprecated
    default SigmetAirmetReportStatus getStatus() {
        return SigmetAirmetReportStatus.fromReportStatus(getReportStatus(), isCancelMessage());
    }

    boolean isCancelMessage();
}
