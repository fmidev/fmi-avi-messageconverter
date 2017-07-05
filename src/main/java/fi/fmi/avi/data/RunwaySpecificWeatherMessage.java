package fi.fmi.avi.data;

import java.util.Set;

public interface RunwaySpecificWeatherMessage extends AerodromeWeatherMessage {
	
	Set<String> getUnresolvedRunwayDirectionDesignators();
	
	void amendRunwayDirectionInfo(RunwayDirection fullInfo);
}
