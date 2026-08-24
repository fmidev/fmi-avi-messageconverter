package fi.fmi.avi.model.metar.immutable;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.RunwayDirection;
import fi.fmi.avi.model.immutable.RunwayDirectionImpl;
import fi.fmi.avi.model.metar.WindShear;

/**
 * Created by rinne on 13/04/2018.
 */

@Value.Immutable
@JsonDeserialize(builder = WindShearImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "runwayDirections", "appliedToAllRunways" })
public abstract class WindShearImpl implements WindShear, Serializable {

    private static final long serialVersionUID = 3197842360756947787L;

    public static Builder builder() {
        return new Builder();
    }

    public static WindShearImpl immutableCopyOf(final WindShear windShear) {
        Objects.requireNonNull(windShear);
        if (windShear instanceof WindShearImpl) {
            return (WindShearImpl) windShear;
        } else {
            return Builder.copyOf(windShear).build();
        }
    }

    public static Optional<WindShearImpl> immutableCopyOf(final Optional<WindShear> windShear) {
        Objects.requireNonNull(windShear);
        return windShear.map(WindShearImpl::immutableCopyOf);
    }

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    // NOTE: Immutables generates a final setRunwayDirections(...) setter, which cannot be overridden to
    // carry @JsonDeserialize(contentAs = ...) (see doc/immutables-migration.md). The hint moves onto this
    // abstract getter re-declaration instead; the package's passAnnotations style (see package-info.java)
    // propagates it onto the generated builder setter for Jackson's builder-based deserialization.
    // NOTE: no per-property @JsonDeserialize(contentAs=...) hint here: see SIGMETImpl.getAnalysisGeometries() for why
    // (Optional<List<X>> on a non-detached builder). RunwayDirection instead carries its own class-level
    // @JsonDeserialize(as=...) hint (see doc/immutables-migration.md).
    @Override
    public abstract Optional<List<RunwayDirection>> getRunwayDirections();

    public static class Builder extends ImmutableWindShearImpl.Builder {

        Builder() {
            setAppliedToAllRunways(false);
        }

        public static Builder copyOf(final WindShear value) {
            if (value instanceof WindShearImpl) {
                return ((WindShearImpl) value).toBuilder();
            } else {
                final Builder retval = WindShearImpl.builder()//
                        .setAppliedToAllRunways(value.isAppliedToAllRunways());

                value.getRunwayDirections()
                        .map(directions -> retval.setRunwayDirections(
                                Collections.unmodifiableList(directions.stream().map(RunwayDirectionImpl::immutableCopyOf).collect(Collectors.toList()))));
                return retval;
            }
        }
    }
}
