package fi.fmi.avi.model.immutable;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.Weather;

/**
 * Created by rinne on 17/04/2018.
 */
@FreeBuilder
@JsonDeserialize(builder = WeatherImpl.Builder.class)
public abstract class WeatherImpl implements Weather, Serializable {

    public static WeatherImpl immutableCopyOf(final Weather weather) {
        checkNotNull(weather);
        if (weather instanceof WeatherImpl) {
            return (WeatherImpl) weather;
        } else {
            return Builder.from(weather).build();
        }
    }

    public static Optional<WeatherImpl> immutableCopyOf(final Optional<Weather> weather) {
        checkNotNull(weather);
        return weather.map(WeatherImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends WeatherImpl_Builder {

        public static Builder from(final Weather value) {
            return new WeatherImpl.Builder().setCode(value.getCode()).setDescription(value.getDescription());
        }

    }
}
