package fi.fmi.avi.data;

public interface AerodromeWeatherMessage extends AviationWeatherMessage {

	Aerodrome getAerodrome();

	void setAerodrome(final Aerodrome aerodrome);
	
	boolean isAerodromeInfoResolved();
	
	void amendAerodromeInfo(Aerodrome fullInfo);
	

}
