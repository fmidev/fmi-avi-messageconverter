package fi.fmi.avi.model;

public interface SIGMETAIRMET extends AirTrafficServicesUnitWeatherMessage, AviationCodeListUser {
    String getSequenceNumber();

    PartialOrCompleteTimePeriod getValidityPeriod();

    Airspace getAirspace();

    @Deprecated
    default SigmetAirmetReportStatus getStatus() {
        return SigmetAirmetReportStatus.fromReportStatus(getReportStatus(), isCancelMessage());
    }

    boolean isCancelMessage();
}
