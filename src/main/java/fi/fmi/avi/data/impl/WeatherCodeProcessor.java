package fi.fmi.avi.data.impl;

import java.util.ArrayList;
import java.util.List;

import fi.fmi.avi.data.Weather;

/**
 * Created by rinne on 26/04/17.
 */
public abstract class WeatherCodeProcessor {

    protected List<String> getAsWeatherCodes(final List<Weather> weatherList) {
        return getAsWeatherCodes(weatherList, null);
    }

    protected List<String> getAsWeatherCodes(final List<Weather> weatherList, final String prefix) {
        List<String> retval = null;
        if (weatherList != null) {
            retval = new ArrayList<>(weatherList.size());
            for (final Weather w : weatherList) {
                retval.add(w.getCode());
            }
        }
        return retval;
    }
}
