package fi.fmi.avi.model.sigmet;

import fi.fmi.avi.model.AviationCodeListUser.AeronauticalSignificantWeatherPhenomenon;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.UnitPropertyGroup;

public interface SigmetReference<T> {
    UnitPropertyGroup getIssuingAirTrafficServicesUnit();

    UnitPropertyGroup getMeteorologicalWatchOffice();

    AeronauticalSignificantWeatherPhenomenon getPhenomenon();

    String getSequenceNumber();

    PartialOrCompleteTimePeriod getValidityPeriod();
}
