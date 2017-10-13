package fi.fmi.avi.model;

public interface AerodromeWeatherMessage extends AviationWeatherMessage, AerodromeUpdateListener {

	Aerodrome getAerodrome();

	void setAerodrome(final Aerodrome aerodrome);
	
	boolean isAerodromeInfoResolved();
	
	void amendAerodromeInfo(Aerodrome fullInfo) throws IllegalArgumentException;
	

}
