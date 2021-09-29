package fi.fmi.avi.model.sigmet;

import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.UnitPropertyGroup;

public interface AirmetReference<T> {
    UnitPropertyGroup getIssuingAirTrafficServicesUnit();

    UnitPropertyGroup getMeteorologicalWatchOffice();

    String getSequenceNumber();

    PartialOrCompleteTimePeriod getValidityPeriod();
}
