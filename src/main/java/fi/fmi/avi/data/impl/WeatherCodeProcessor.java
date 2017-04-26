package fi.fmi.avi.data.impl;

import java.util.ArrayList;
import java.util.List;

import fi.fmi.avi.data.Weather;

/**
 * Created by rinne on 26/04/17.
 */
public abstract class WeatherCodeProcessor {

    protected static List<String> getAsWeatherCodes(List<Weather> weatherList) {
        return getAsWeatherCodes(weatherList, null);
    }

    protected static List<String> getAsWeatherCodes(List<Weather> weatherList, String prefix) {
        List<String> retval = null;
        if (weatherList != null) {
            retval = new ArrayList<>(weatherList.size());
            StringBuilder sb;
            for (Weather w : weatherList) {
                sb = new StringBuilder();
                if (prefix != null) {
                    sb.append(prefix);
                }
                if (w.getIntensity() != null) {
                    sb.append(w.getIntensity().getCode());
                }
                if (w.isInVicinity()) {
                    sb.append("VI");
                }
                sb.append(w.getKind().getCode());
                retval.add(sb.toString());
            }
        }
        return retval;
    }
}
