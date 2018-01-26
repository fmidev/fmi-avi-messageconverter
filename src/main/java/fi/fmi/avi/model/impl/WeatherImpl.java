package fi.fmi.avi.model.impl;

import com.fasterxml.jackson.annotation.JsonInclude;

import fi.fmi.avi.model.Weather;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class WeatherImpl implements Weather {

    private String code;
    private String description;

    public WeatherImpl() {
    }

    public WeatherImpl(final Weather weather) {
        if (weather != null) {
            this.code = weather.getCode();
            this.description = weather.getDescription();
        }
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public void setCode(final String code) {
        this.code = code;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

}
