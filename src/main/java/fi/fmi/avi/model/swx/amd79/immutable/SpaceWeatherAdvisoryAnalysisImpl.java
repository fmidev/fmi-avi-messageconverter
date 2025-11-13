package fi.fmi.avi.model.swx.amd79.immutable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.fmi.avi.model.swx.amd79.SpaceWeatherAdvisoryAnalysis;
import fi.fmi.avi.model.swx.amd79.SpaceWeatherRegion;
import org.inferred.freebuilder.FreeBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@FreeBuilder
@JsonDeserialize(builder = SpaceWeatherAdvisoryAnalysisImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"time", "analysisType", "regions", "nilPhenomenonReason"})
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
        }

        public static Builder from(final SpaceWeatherAdvisoryAnalysis value) {
            if (value instanceof SpaceWeatherAdvisoryAnalysisImpl) {
                return ((SpaceWeatherAdvisoryAnalysisImpl) value).toBuilder();
            } else {
                return builder()//
                        .setTime(value.getTime())//
                        .setAnalysisType(value.getAnalysisType())//
                        .addAllRegions(value.getRegions().stream().map(SpaceWeatherRegionImpl::immutableCopyOf))//
                        .setNilPhenomenonReason(value.getNilPhenomenonReason());
            }
        }

        public static Builder fromAmd82(final fi.fmi.avi.model.swx.amd82.SpaceWeatherAdvisoryAnalysis value) {
            if (value.getIntensityAndRegions().size() > 1) {
                // Combining regions correctly is not as trivial as just concatenating all regions.
                // Possibly impossible in some cases. Better to fail than return invalid result.
                throw new IllegalArgumentException("Cannot merge multiple intensity and regions");
            }
            return builder()//
                    .setTime(value.getTime())//
                    .setAnalysisType(Type.valueOf(value.getAnalysisType().name()))
                    .addAllRegions(value.getIntensityAndRegions().stream()
                            .flatMap(intensityAndRegion -> intensityAndRegion.getRegions().stream())
                            .map(region -> SpaceWeatherRegionImpl.Builder.fromAmd82(region).build())
                            .distinct())
                    .setNilPhenomenonReason(value.getNilReason().map(Builder::nilPhenomenonReasonFromAmd82));
        }

        private static NilPhenomenonReason nilPhenomenonReasonFromAmd82(final fi.fmi.avi.model.swx.amd82.SpaceWeatherAdvisoryAnalysis.NilReason value) {
            if (value == fi.fmi.avi.model.swx.amd82.SpaceWeatherAdvisoryAnalysis.NilReason.NO_SWX_EXPECTED) {
                return NilPhenomenonReason.NO_PHENOMENON_EXPECTED;
            } else {
                return NilPhenomenonReason.valueOf(value.name());
            }
        }

        @JsonDeserialize(contentAs = SpaceWeatherRegionImpl.class)
        public Builder addAllRegions(final List<SpaceWeatherRegion> region) {
            return super.addAllRegions(region);
        }

    }
}
