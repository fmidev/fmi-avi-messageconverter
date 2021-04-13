package fi.fmi.avi.model.sigmet;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.UnitPropertyGroup;

public interface AirmetReference<T> {
    UnitPropertyGroup getIssuingAirTrafficServicesUnit();

    UnitPropertyGroup getMeteorologicalWatchOffice();

    AviationCodeListUser.AeronauticalAirmetWeatherPhenomenon getPhenomenon();

    String getSequenceNumber();

    PartialOrCompleteTimePeriod getValidityPeriod();
}
