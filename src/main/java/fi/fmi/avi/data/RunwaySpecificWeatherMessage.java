package fi.fmi.avi.data;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface RunwaySpecificWeatherMessage extends AerodromeWeatherMessage {
	@JsonIgnore
	Set<String> getUnresolvedRunwayDirectionDesignators();

	void amendRunwayDirectionInfo(RunwayDirection fullInfo);
}
