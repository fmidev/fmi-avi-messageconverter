package fi.fmi.avi.model.swx.amd79.immutable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.fmi.avi.model.swx.amd79.SpaceWeatherAdvisoryAnalysis;
import fi.fmi.avi.model.swx.amd79.SpaceWeatherRegion;
import org.inferred.freebuilder.FreeBuilder;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.*;
import java.util.stream.IntStream;

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
        private static final Comparator<fi.fmi.avi.model.swx.amd82.SpaceWeatherIntensityAndRegion> AMD82_INTENSITY_AND_REGION_COMPARATOR =
                Comparator.comparing(fi.fmi.avi.model.swx.amd82.SpaceWeatherIntensityAndRegion::getIntensity,
                                fi.fmi.avi.model.swx.amd82.Intensity.comparator())
                        .reversed();

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

        /**
         * Return a builder converted from
         * {@link fi.fmi.avi.model.swx.amd82.SpaceWeatherAdvisoryAnalysis Amd82 SpaceWeatherAdvisoryAnalysis}.
         * In lenient mode, the method tries to convert some data, though it may be incomplete. In normal mode
         * ({@code lenient == false}) the method will simply fail if data cannot be converted. For example,
         * multiple {@link fi.fmi.avi.model.swx.amd82.SpaceWeatherIntensityAndRegion SpaceWeatherIntensityAndRegion}s
         * cannot be converted into single list of regions.
         *
         * <p>
         * Current lenient implementation uses the first most severe regions that do not contain
         * {@link fi.fmi.avi.model.swx.amd82.SpaceWeatherRegion.SpaceWeatherLocation#NIGHTSIDE NIGHTSIDE} and has
         * {@link fi.fmi.avi.model.swx.amd82.SpaceWeatherRegion.SpaceWeatherLocation#DAYSIDE DAYSIDE} only as the first
         * region if at all. The lenient behavior may be changed in the future.
         * </p>
         *
         * @param value   analysis to convert
         * @param lenient whether to run in lenient ({@code  true}) or normal ({@code false}) mode
         * @return new builder with values from provided {@code value}
         * @throws IllegalArgumentException if data cannot be converted
         */
        public static Builder fromAmd82(
                final fi.fmi.avi.model.swx.amd82.SpaceWeatherAdvisoryAnalysis value, final boolean lenient) {
            final List<fi.fmi.avi.model.swx.amd82.SpaceWeatherRegion> firstConvertableMostSevereAmd82Regions =
                    value.getIntensityAndRegions().stream()
                            .sorted(AMD82_INTENSITY_AND_REGION_COMPARATOR) // stable on ordered stream
                            .map(fi.fmi.avi.model.swx.amd82.SpaceWeatherIntensityAndRegion::getRegions)
                            .filter(Builder::isConvertableAmd82Regions)
                            .findFirst()
                            .orElse(Collections.emptyList());

            if (!lenient && value.getIntensityAndRegions().size() > 1) {
                // Combining regions correctly is not trivial, but impossible in some cases.
                // Better to fail than return invalid result, unless lenient processing is requested.
                throw new IllegalArgumentException("Cannot convert multiple intensity and regions");
            } else if (firstConvertableMostSevereAmd82Regions.isEmpty() && !value.getIntensityAndRegions().isEmpty()) {
                throw new IllegalArgumentException("Unable to convert regions: " + value.getIntensityAndRegions());
            }
            return builder()//
                    .setTime(value.getTime())//
                    .setAnalysisType(Type.valueOf(value.getAnalysisType().name()))
                    .addAllRegions(firstConvertableMostSevereAmd82Regions.stream()
                            .map(region -> SpaceWeatherRegionImpl.Builder.fromAmd82(region).build()))
                    .setNilPhenomenonReason(value.getNilReason().map(Builder::nilPhenomenonReasonFromAmd82));
        }

        private static boolean isConvertableAmd82Regions(final List<fi.fmi.avi.model.swx.amd82.SpaceWeatherRegion> amd82Regions) {
            return !amd82Regions.isEmpty() &&
                    IntStream.range(0, amd82Regions.size())
                            .noneMatch(i -> {
                                @Nullable final fi.fmi.avi.model.swx.amd82.SpaceWeatherRegion.SpaceWeatherLocation locationIndicator =
                                        amd82Regions.get(i).getLocationIndicator().orElse(null);
                                // Allow DAYSIDE (DAYLIGHT SIDE) as first region, otherwise disallow DAYSIDE and NIGHTSIDE
                                return locationIndicator == fi.fmi.avi.model.swx.amd82.SpaceWeatherRegion.SpaceWeatherLocation.NIGHTSIDE ||
                                        i > 0 && locationIndicator == fi.fmi.avi.model.swx.amd82.SpaceWeatherRegion.SpaceWeatherLocation.DAYSIDE;
                            });
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
