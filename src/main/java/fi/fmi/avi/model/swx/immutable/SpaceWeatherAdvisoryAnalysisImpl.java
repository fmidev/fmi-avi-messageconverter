package fi.fmi.avi.model.swx.immutable;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.swx.SpaceWeatherAdvisoryAnalysis;
import fi.fmi.avi.model.swx.SpaceWeatherRegion;

@FreeBuilder
@JsonDeserialize(builder = SpaceWeatherAdvisoryAnalysisImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "time", "analysisType", "region", "noPhenomenaExpected", "noInformationAvailable" })
public abstract class SpaceWeatherAdvisoryAnalysisImpl implements SpaceWeatherAdvisoryAnalysis, Serializable {

    private static final long serialVersionUID = -6443983160749650868L;

    public static Builder builder() {
        return new Builder();
    }

    public static SpaceWeatherAdvisoryAnalysisImpl immutableCopyOf(final SpaceWeatherAdvisoryAnalysis analysis) {
        Objects.requireNonNull(analysis);
        if (analysis instanceof SpaceWeatherAdvisoryAnalysisImpl) {
            return (SpaceWeatherAdvisoryAnalysisImpl) analysis;
        } else {
            return Builder.from(analysis).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<SpaceWeatherAdvisoryAnalysisImpl> immutableCopyOf(final Optional<SpaceWeatherAdvisoryAnalysis> analysis) {
        Objects.requireNonNull(analysis);
        return analysis.map(SpaceWeatherAdvisoryAnalysisImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends SpaceWeatherAdvisoryAnalysisImpl_Builder {
        Builder() {
            setNoPhenomenaExpected(false);
            setNoInformationAvailable(false);
        }

        public static Builder from(final SpaceWeatherAdvisoryAnalysis value) {
            if (value instanceof SpaceWeatherAdvisoryAnalysisImpl) {
                return ((SpaceWeatherAdvisoryAnalysisImpl) value).toBuilder();
            } else {
                final Builder retval = builder().setTime(value.getTime())
                        .setAnalysisType(value.getAnalysisType())
                        .setNoPhenomenaExpected(value.isNoPhenomenaExpected())
                        .setNoInformationAvailable(value.isNoInformationAvailable());

                value.getRegion()
                        .map(regions -> retval.setRegion(
                                Collections.unmodifiableList(regions.stream().map(SpaceWeatherRegionImpl::immutableCopyOf).collect(Collectors.toList()))));

                return retval;
            }
        }

        @Override
        @JsonDeserialize(contentAs = SpaceWeatherRegionImpl.class)
        public Builder setRegion(final List<SpaceWeatherRegion> region) {
            return super.setRegion(region);
        }

    }
}
