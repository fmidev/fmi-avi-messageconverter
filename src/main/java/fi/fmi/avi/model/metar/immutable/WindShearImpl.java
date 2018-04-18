package fi.fmi.avi.model.metar.immutable;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.immutable.RunwayDirectionImpl;
import fi.fmi.avi.model.metar.WindShear;

/**
 * Created by rinne on 13/04/2018.
 */

@FreeBuilder
@JsonDeserialize(builder = WindShearImpl.Builder.class)
public abstract class WindShearImpl implements WindShear, Serializable {

    public static WindShearImpl immutableCopyOf(final WindShear windShear) {
        checkNotNull(windShear);
        if (windShear instanceof WindShearImpl) {
            return (WindShearImpl) windShear;
        } else {
            return Builder.from(windShear).build();
        }
    }

    public static Optional<WindShearImpl> immutableCopyOf(final Optional<WindShear> windShear) {
        checkNotNull(windShear);
        return windShear.map(WindShearImpl::immutableCopyOf);
    }

    abstract Builder toBuilder();

    public static class Builder extends WindShearImpl_Builder {

        public static Builder from(final WindShear value) {
            Builder retval = new WindShearImpl.Builder().setAppliedToAllRunways(value.isAppliedToAllRunways());

            value.getRunwayDirections()
                    .map(directions -> retval.setRunwayDirections(
                            Collections.unmodifiableList(directions.stream().map(RunwayDirectionImpl::immutableCopyOf).collect(Collectors.toList()))));
            return retval;
        }
    }
}
