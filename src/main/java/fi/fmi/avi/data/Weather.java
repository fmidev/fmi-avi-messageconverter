package fi.fmi.avi.data;

import fi.fmi.avi.data.AviationCodeListUser.WeatherCodeIntensity;
import fi.fmi.avi.data.AviationCodeListUser.WeatherCodeKind;

public interface Weather {
	WeatherCodeKind getKind();
	WeatherCodeIntensity getIntensity();
	boolean isInVicinity();
	
	void setKind(final WeatherCodeKind kind);
	void setIntensity(final WeatherCodeIntensity intensity);
	void setInVicinity(final boolean inVicinity);
	
}
