package fi.fmi.avi.model;

public interface AerodromeWeatherMessage extends AviationWeatherMessage {

    Aerodrome getAerodrome();

    boolean allAerodromeReferencesContainPositionAndElevation();
}
