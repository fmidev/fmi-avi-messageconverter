package fi.fmi.avi.model;

public interface SIGMETAIRMET  extends AirTrafficServicesUnitWeatherMessage, AviationCodeListUser {
        String getSequenceNumber();
        PartialOrCompleteTimePeriod getValidityPeriod();

        SigmetAirmetReportStatus getStatus();
}
