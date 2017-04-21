package fi.fmi.avi.data.impl;

import com.fasterxml.jackson.annotation.JsonInclude;

import fi.fmi.avi.data.AviationCodeListUser.WeatherCodeIntensity;
import fi.fmi.avi.data.AviationCodeListUser.WeatherCodeKind;
import fi.fmi.avi.data.Weather;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class WeatherImpl implements Weather {

	private WeatherCodeKind kind;
	private WeatherCodeIntensity intensity;
	private boolean inVicinity;

	public WeatherImpl() {
	}

	public WeatherImpl(final Weather weather) {
		this.kind = weather.getKind();
		this.intensity = weather.getIntensity();
		this.inVicinity = weather.isInVicinity();
	}

	@Override
	public WeatherCodeKind getKind() {
		return this.kind;
	}

	@Override
	public WeatherCodeIntensity getIntensity() {
		return this.intensity;
	}

	@Override
	public boolean isInVicinity() {
		return this.inVicinity;
	}

	@Override
	public void setKind(final WeatherCodeKind kind) {
		this.kind = kind;

	}

	@Override
	public void setIntensity(final WeatherCodeIntensity intensity) {
		this.intensity = intensity;
	}

	@Override
	public void setInVicinity(final boolean inVicinity) {
		this.inVicinity = inVicinity;
	}

}
