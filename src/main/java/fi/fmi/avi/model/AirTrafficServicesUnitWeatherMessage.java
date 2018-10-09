package fi.fmi.avi.model;

public interface AirTrafficServicesUnitWeatherMessage extends AviationWeatherMessage {
    public UnitPropertyGroup getIssuingAirTrafficServicesUnit();
    public UnitPropertyGroup getMeteorologicalWatchOffice();
}
