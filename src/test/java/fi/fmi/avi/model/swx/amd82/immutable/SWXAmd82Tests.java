package fi.fmi.avi.model.swx.amd82.immutable;

import static java.util.Objects.requireNonNull;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import fi.fmi.avi.model.PartialDateTime;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.swx.VerticalLimitsImpl;
import fi.fmi.avi.model.swx.amd82.Intensity;
import fi.fmi.avi.model.swx.amd82.SpaceWeatherAdvisoryAnalysis;
import fi.fmi.avi.model.swx.amd82.SpaceWeatherRegion;

public final class SWXAmd82Tests {
    public static final int INTENSITY_AND_REGION_COUNT_DEFAULT = 1;
    public static final int REGIONS_PER_INTENSITY_COUNT_DEFAULT = 2;
    public static final List<SpaceWeatherRegion.SpaceWeatherLocation> LATITUDE_BANDS = Collections.unmodifiableList(Arrays.asList(
            SpaceWeatherRegion.SpaceWeatherLocation.HIGH_NORTHERN_HEMISPHERE,
            SpaceWeatherRegion.SpaceWeatherLocation.MIDDLE_NORTHERN_HEMISPHERE,
            SpaceWeatherRegion.SpaceWeatherLocation.EQUATORIAL_LATITUDES_NORTHERN_HEMISPHERE,
            SpaceWeatherRegion.SpaceWeatherLocation.EQUATORIAL_LATITUDES_SOUTHERN_HEMISPHERE,
            SpaceWeatherRegion.SpaceWeatherLocation.MIDDLE_LATITUDES_SOUTHERN_HEMISPHERE,
            SpaceWeatherRegion.SpaceWeatherLocation.HIGH_LATITUDES_SOUTHERN_HEMISPHERE
    ));

    private SWXAmd82Tests() {
        throw new AssertionError();
    }

    public static AnalysisBuilderSpec.Builder analysisBuilder(final ZonedDateTime analysisTime) {
        return new AnalysisBuilderSpec.Builder()
                .setBaseTime(analysisTime);
    }

    public static PartialOrCompleteTimeInstant dayHourMinuteZoneAndCompleteTime(final ZonedDateTime completeTime) {
        return PartialOrCompleteTimeInstant.of(PartialDateTime.ofDayHourMinuteZone(completeTime, false), completeTime);
    }

    /**
     * Test-only fixture value type, migrated off {@code @FreeBuilder} onto Immutables. {@link Builder} is a
     * hand-written "detached builder" (see docs/07-modernization-plan.md) because {@link Builder#build()} needs to
     * perform cross-field validation and defaulting that requires reading back already-set builder state, which a
     * plain Immutables-generated builder (extending {@code ImmutableAnalysisBuilderSpec.Builder}) does not expose.
     */
    @Value.Immutable
    @Value.Style(init = "set*", typeInnerBuilder = "InternalImmutableBuilder", builder = "internalBuilder")
    public abstract static class AnalysisBuilderSpec {
        @Nullable
        private static <E extends Enum<E>> E getNullableElement(final int round, final List<E> elements) {
            return getNullableElement(0, 1, round, elements);
        }

        @Nullable
        private static <E extends Enum<E>> E getNullableElement(final int subsetIndex, final int subsetSize, final int round, final List<E> elements) {
            if (elements.isEmpty()) {
                return null;
            }
            return elements.get((round * subsetSize + subsetIndex) % elements.size());
        }

        public abstract ZonedDateTime getBaseTime();

        public abstract List<SpaceWeatherAdvisoryAnalysis.NilReason> getNilReasons();

        public abstract List<Intensity> getObservationIntensities();

        public abstract List<Intensity> getForecastIntensities();

        public abstract List<SpaceWeatherRegion.SpaceWeatherLocation> getLocationIndicators();

        public abstract int getIntensityAndRegionCount();

        public abstract int getRegionsPerIntensityCount();

        public Stream<SpaceWeatherAdvisoryAnalysisImpl> generateAnalyses() {
            return IntStream.range(0, 5)
                    .mapToObj(analysisIndex -> {
                        final ZonedDateTime analysisTime = getBaseTime().plusHours(analysisIndex);
                        final SpaceWeatherAdvisoryAnalysis.Type analysisType = analysisIndex == 0
                                ? SpaceWeatherAdvisoryAnalysis.Type.OBSERVATION
                                : SpaceWeatherAdvisoryAnalysis.Type.FORECAST;
                        return SpaceWeatherAdvisoryAnalysisImpl.builder()
                                .setTime(dayHourMinuteZoneAndCompleteTime(analysisTime))
                                .setAnalysisType(analysisType)
                                .setNilReason(Optional.ofNullable(getNullableElement(analysisIndex, getNilReasons())))
                                .addAllIntensityAndRegions(generateIntensityAndRegions(analysisIndex, analysisType, analysisTime.toInstant())
                                        .collect(Collectors.toList()))
                                .build();
                    });
        }

        private Stream<SpaceWeatherIntensityAndRegionImpl> generateIntensityAndRegions(final int analysisIndex, final SpaceWeatherAdvisoryAnalysis.Type analysisType, final Instant analysisTime) {
            final int totalRegionsPerAnalysis = getRegionsPerIntensityCount() * getIntensityAndRegionCount();
            final List<Intensity> intensities = analysisType == SpaceWeatherAdvisoryAnalysis.Type.OBSERVATION
                    ? getObservationIntensities() : getForecastIntensities();
            return IntStream.range(0, getIntensityAndRegionCount())
                    .mapToObj(intensityAndRegionIndex -> SpaceWeatherIntensityAndRegionImpl.builder()
                            .setIntensity(getNullableElement(intensityAndRegionIndex, intensities))
                            .addAllRegions(generateRegions(analysisIndex, intensityAndRegionIndex, totalRegionsPerAnalysis, analysisTime)
                                    .collect(Collectors.toList()))
                            .build()
                    );
        }

        private Stream<SpaceWeatherRegionImpl> generateRegions(final int analysisIndex, final int intensityAndRegionIndex, final int totalRegionsPerAnalysis, final Instant analysisTime) {
            return IntStream.range(
                            intensityAndRegionIndex * getRegionsPerIntensityCount(),
                            (intensityAndRegionIndex + 1) * getRegionsPerIntensityCount()
                    )
                    .mapToObj(subsetIndex -> SpaceWeatherRegionImpl.fromLocationIndicator(
                            getNullableElement(subsetIndex, totalRegionsPerAnalysis, analysisIndex, getLocationIndicators()),
                            VerticalLimitsImpl.none(), analysisTime, -180.0, 180.0)
                    );
        }

        public static class Builder {
            private ZonedDateTime baseTime;
            private final List<SpaceWeatherAdvisoryAnalysis.NilReason> nilReasons = new ArrayList<>();
            private final List<Intensity> observationIntensities = new ArrayList<>();
            private final List<Intensity> forecastIntensities = new ArrayList<>();
            private final List<SpaceWeatherRegion.SpaceWeatherLocation> locationIndicators = new ArrayList<>();
            private int intensityAndRegionCount = INTENSITY_AND_REGION_COUNT_DEFAULT;
            private int regionsPerIntensityCount = REGIONS_PER_INTENSITY_COUNT_DEFAULT;
            private boolean regionsPerIntensityFromLocationIndicators;

            public Builder setBaseTime(final ZonedDateTime baseTime) {
                this.baseTime = requireNonNull(baseTime, "baseTime");
                return this;
            }

            public List<SpaceWeatherAdvisoryAnalysis.NilReason> getNilReasons() {
                return nilReasons;
            }

            public Builder addNilReasons(final SpaceWeatherAdvisoryAnalysis.NilReason... elements) {
                Collections.addAll(nilReasons, elements);
                return this;
            }

            public Builder addAllNilReasons(final Iterable<? extends SpaceWeatherAdvisoryAnalysis.NilReason> elements) {
                for (final SpaceWeatherAdvisoryAnalysis.NilReason element : elements) {
                    nilReasons.add(element);
                }
                return this;
            }

            public List<Intensity> getObservationIntensities() {
                return observationIntensities;
            }

            public Builder addObservationIntensities(final Intensity element) {
                observationIntensities.add(element);
                return this;
            }

            public Builder addObservationIntensities(final Intensity... elements) {
                Collections.addAll(observationIntensities, elements);
                return this;
            }

            public Builder addAllObservationIntensities(final Iterable<? extends Intensity> elements) {
                for (final Intensity element : elements) {
                    observationIntensities.add(element);
                }
                return this;
            }

            public Builder clearObservationIntensities() {
                observationIntensities.clear();
                return this;
            }

            public List<Intensity> getForecastIntensities() {
                return forecastIntensities;
            }

            public Builder addForecastIntensities(final Intensity element) {
                forecastIntensities.add(element);
                return this;
            }

            public Builder addForecastIntensities(final Intensity... elements) {
                Collections.addAll(forecastIntensities, elements);
                return this;
            }

            public Builder addAllForecastIntensities(final Iterable<? extends Intensity> elements) {
                for (final Intensity element : elements) {
                    forecastIntensities.add(element);
                }
                return this;
            }

            public Builder clearForecastIntensities() {
                forecastIntensities.clear();
                return this;
            }

            public List<SpaceWeatherRegion.SpaceWeatherLocation> getLocationIndicators() {
                return locationIndicators;
            }

            public Builder addLocationIndicators(final SpaceWeatherRegion.SpaceWeatherLocation... elements) {
                Collections.addAll(locationIndicators, elements);
                return this;
            }

            public Builder addAllLocationIndicators(final Iterable<? extends SpaceWeatherRegion.SpaceWeatherLocation> elements) {
                for (final SpaceWeatherRegion.SpaceWeatherLocation element : elements) {
                    locationIndicators.add(element);
                }
                return this;
            }

            public int getIntensityAndRegionCount() {
                return intensityAndRegionCount;
            }

            public Builder setIntensityAndRegionCount(final int intensityAndRegionCount) {
                this.intensityAndRegionCount = intensityAndRegionCount;
                return this;
            }

            public int getRegionsPerIntensityCount() {
                return regionsPerIntensityFromLocationIndicators ? locationIndicators.size() : regionsPerIntensityCount;
            }

            public Builder setRegionsPerIntensityCount(final int regionsPerIntensityCount) {
                this.regionsPerIntensityFromLocationIndicators = false;
                this.regionsPerIntensityCount = regionsPerIntensityCount;
                return this;
            }

            public Builder setRegionsPerIntensityFromLocationIndicators() {
                this.regionsPerIntensityFromLocationIndicators = true;
                return this;
            }

            public AnalysisBuilderSpec build() {
                if (getIntensityAndRegionCount() > 0 && !getNilReasons().isEmpty()) {
                    throw new IllegalStateException("Nil reasons are not supported when intensityAndRegionCount > 0");
                }
                if (getObservationIntensities().isEmpty()) {
                    addObservationIntensities(Intensity.MODERATE);
                }
                if (getForecastIntensities().isEmpty()) {
                    addForecastIntensities(Intensity.MODERATE);
                }
                if (getLocationIndicators().isEmpty()) {
                    addAllLocationIndicators(LATITUDE_BANDS);
                }
                return ImmutableAnalysisBuilderSpec.internalBuilder()//
                        .setBaseTime(requireNonNull(baseTime, "baseTime"))//
                        .addAllNilReasons(nilReasons)//
                        .addAllObservationIntensities(observationIntensities)//
                        .addAllForecastIntensities(forecastIntensities)//
                        .addAllLocationIndicators(locationIndicators)//
                        .setIntensityAndRegionCount(getIntensityAndRegionCount())//
                        .setRegionsPerIntensityCount(getRegionsPerIntensityCount())//
                        .build();
            }

            public Stream<SpaceWeatherAdvisoryAnalysisImpl> generateAnalyses() {
                return build().generateAnalyses();
            }

            public Builder addIntensities(final Intensity element) {
                return addObservationIntensities(element)
                        .addForecastIntensities(element);
            }

            public Builder addIntensities(final Intensity... elements) {
                return addObservationIntensities(elements)
                        .addForecastIntensities(elements);
            }
        }
    }
}
