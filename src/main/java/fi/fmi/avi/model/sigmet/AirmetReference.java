package fi.fmi.avi.model.sigmet;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.AviationCodeListUser.AeronauticalSignificantWeatherPhenomenon;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.UnitPropertyGroup;

public interface AirmetReference<T> {
    public UnitPropertyGroup getIssuingAirTrafficServicesUnit();
    public UnitPropertyGroup getMeteorologicalWatchOffice();
    public AviationCodeListUser.AeronauticalAirmetWeatherPhenomenon getPhenomenon();
    public String getSequenceNumber();
    public PartialOrCompleteTimePeriod getValidityPeriod();
}
