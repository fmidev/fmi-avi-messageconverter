package fi.fmi.avi.model.sigmet;

import fi.fmi.avi.model.AviationCodeListUser.AeronauticalSignificantWeatherPhenomenon;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.UnitPropertyGroup;

public interface SigmetReference {
    public UnitPropertyGroup getIssuingAirTrafficServicesUnit();
    public UnitPropertyGroup getMeteorologicalWatchOffice();
    public AeronauticalSignificantWeatherPhenomenon getPhenomenon();
    public String getSequenceNumber();
    public PartialOrCompleteTimePeriod getValidityPeriod();
}
