package fi.fmi.avi.model.swx.amd82.immutable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.fmi.avi.model.swx.amd82.Intensity;
import fi.fmi.avi.model.swx.amd82.SpaceWeatherAdvisoryAnalysis;
import fi.fmi.avi.model.swx.amd82.SpaceWeatherIntensityAndRegion;
import org.immutables.value.Value;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Value.Immutable
@JsonDeserialize(builder = SpaceWeatherAdvisoryAnalysisImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"time", "analysisType", "intensityAndRegions", "nilReason"})
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
            return Builder.copyOf(analysis).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<SpaceWeatherAdvisoryAnalysisImpl> immutableCopyOf(final Optional<SpaceWeatherAdvisoryAnalysis> analysis) {
        Objects.requireNonNull(analysis);
        return analysis.map(SpaceWeatherAdvisoryAnalysisImpl::immutableCopyOf);
    }

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    public static class Builder extends ImmutableSpaceWeatherAdvisoryAnalysisImpl.Builder {
        Builder() {
        }

        public static Builder copyOf(final SpaceWeatherAdvisoryAnalysis value) {
            if (value instanceof SpaceWeatherAdvisoryAnalysisImpl) {
                return ((SpaceWeatherAdvisoryAnalysisImpl) value).toBuilder();
            } else {
                return builder()//
                        .setTime(value.getTime())//
                        .setAnalysisType(value.getAnalysisType())//
                        .addAllIntensityAndRegions(value.getIntensityAndRegions().stream()
                                .map(SpaceWeatherIntensityAndRegionImpl::immutableCopyOf)
                                .collect(java.util.stream.Collectors.toList()))//
                        .setNilReason(value.getNilReason());
            }
        }

        public static Builder fromAmd79(final Intensity intensity, final fi.fmi.avi.model.swx.amd79.SpaceWeatherAdvisoryAnalysis value) {
            return builder()//
                    .setTime(value.getTime())//
                    .setAnalysisType(Type.valueOf(value.getAnalysisType().name()))
                    .addIntensityAndRegions(SpaceWeatherIntensityAndRegionImpl.Builder.fromAmd79(intensity, value.getRegions()).build())
                    .setNilReason(value.getNilPhenomenonReason().map(Builder::nilReasonFromAmd79));
        }

        private static NilReason nilReasonFromAmd79(final fi.fmi.avi.model.swx.amd79.SpaceWeatherAdvisoryAnalysis.NilPhenomenonReason value) {
            if (value == fi.fmi.avi.model.swx.amd79.SpaceWeatherAdvisoryAnalysis.NilPhenomenonReason.NO_PHENOMENON_EXPECTED) {
                return NilReason.NO_SWX_EXPECTED;
            } else {
                return NilReason.valueOf(value.name());
            }
        }

        @JsonDeserialize(contentAs = SpaceWeatherIntensityAndRegionImpl.class)
        public Builder addAllIntensityAndRegions(final List<SpaceWeatherIntensityAndRegion> intensityAndRegions) {
            return super.addAllIntensityAndRegions(intensityAndRegions);
        }
    }
}
