package fi.fmi.avi.model.swx.amd82.immutable;

import fi.fmi.avi.model.PartialDateTime;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.swx.VerticalLimitsImpl;
import fi.fmi.avi.model.swx.amd82.Intensity;
import fi.fmi.avi.model.swx.amd82.SpaceWeatherAdvisoryAnalysis;
import fi.fmi.avi.model.swx.amd82.SpaceWeatherRegion;
import org.inferred.freebuilder.FreeBuilder;

import javax.annotation.Nullable;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.BaseStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

    @FreeBuilder
    public static abstract class AnalysisBuilderSpec {
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
                                .setNullableNilReason(getNullableElement(analysisIndex, getNilReasons()))
                                .addAllIntensityAndRegions(generateIntensityAndRegions(analysisIndex, analysisType, analysisTime.toInstant()))
                                .build();
                    });
        }

        private Stream<SpaceWeatherIntensityAndRegionImpl> generateIntensityAndRegions(final int analysisIndex, final SpaceWeatherAdvisoryAnalysis.Type analysisType, final Instant analysisTime) {
            final int totalRegionsPerAnalysis = getRegionsPerIntensityCount() * getIntensityAndRegionCount();
            final List<Intensity> intensities = analysisType == SpaceWeatherAdvisoryAnalysis.Type.OBSERVATION
                    ? getObservationIntensities() : getForecastIntensities();
            return IntStream.range(0, getIntensityAndRegionCount())
                    .mapToObj(intensityAndRegionIndex -> SpaceWeatherIntensityAndRegionImpl.builder()
                            .setIntensity(getNullableElement(analysisIndex, intensities))
                            .addAllRegions(generateRegions(analysisIndex, intensityAndRegionIndex, totalRegionsPerAnalysis, analysisTime))
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

        public static class Builder extends SWXAmd82Tests_AnalysisBuilderSpec_Builder {
            Builder() {
                setIntensityAndRegionCount(INTENSITY_AND_REGION_COUNT_DEFAULT);
                setRegionsPerIntensityCount(REGIONS_PER_INTENSITY_COUNT_DEFAULT);
            }

            @Override
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
                return super.build();
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

            public Builder addAllIntensities(final Spliterator<? extends Intensity> elements) {
                return addAllObservationIntensities(elements)
                        .addAllForecastIntensities(elements);
            }

            public Builder addAllIntensities(final BaseStream<? extends Intensity, ?> elements) {
                return addAllObservationIntensities(elements)
                        .addAllForecastIntensities(elements);
            }

            public Builder addAllIntensities(final Iterable<? extends Intensity> elements) {
                return addAllObservationIntensities(elements)
                        .addAllForecastIntensities(elements);
            }

            public Builder clearIntensities() {
                return clearObservationIntensities()
                        .clearForecastIntensities();
            }
        }
    }
}
