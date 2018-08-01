package fi.fmi.avi.model.metar.immutable;

import static org.inferred.freebuilder.shaded.com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.RunwayDirection;
import fi.fmi.avi.model.immutable.RunwayDirectionImpl;
import fi.fmi.avi.model.metar.WindShear;

/**
 * Created by rinne on 13/04/2018.
 */

@FreeBuilder
@JsonDeserialize(builder = WindShearImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"runwayDirections", "appliedToAllRunways"})
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

    public abstract Builder toBuilder();

    public static class Builder extends WindShearImpl_Builder {

        public Builder() {
            setAppliedToAllRunways(false);
        }
        public static Builder from(final WindShear value) {
            Builder retval = new WindShearImpl.Builder().setAppliedToAllRunways(value.isAppliedToAllRunways());

            value.getRunwayDirections()
                    .map(directions -> retval.setRunwayDirections(
                            Collections.unmodifiableList(directions.stream().map(RunwayDirectionImpl::immutableCopyOf).collect(Collectors.toList()))));
            return retval;
        }

        @Override
        @JsonDeserialize(contentAs = RunwayDirectionImpl.class)
        public Builder setRunwayDirections(final List<RunwayDirection> runwayDirections) {
            return super.setRunwayDirections(runwayDirections);
        }
    }
}
