package fi.fmi.avi.model;

public interface SIGMETAIRMET  extends AirTrafficServicesUnitWeatherMessage, AviationCodeListUser {
        String getSequenceNumber();
        PartialOrCompleteTimePeriod getValidityPeriod();

        Airspace getAirspace();

        SigmetAirmetReportStatus getStatus();
}
