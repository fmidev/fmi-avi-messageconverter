package fi.fmi.avi.model.swx.amd79.immutable;

import fi.fmi.avi.model.PartialDateTime;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.swx.amd79.SpaceWeatherAdvisoryAnalysis;
import fi.fmi.avi.model.swx.amd79.SpaceWeatherRegion;
import org.inferred.freebuilder.FreeBuilder;

import javax.annotation.Nullable;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class SWXAmd79Tests {
    public static final int REGIONS_COUNT_DEFAULT = 2;
    public static final List<SpaceWeatherRegion.SpaceWeatherLocation> LATITUDE_BANDS = Collections.unmodifiableList(Arrays.asList(
            SpaceWeatherRegion.SpaceWeatherLocation.HIGH_NORTHERN_HEMISPHERE,
            SpaceWeatherRegion.SpaceWeatherLocation.MIDDLE_NORTHERN_HEMISPHERE,
            SpaceWeatherRegion.SpaceWeatherLocation.EQUATORIAL_LATITUDES_NORTHERN_HEMISPHERE,
            SpaceWeatherRegion.SpaceWeatherLocation.EQUATORIAL_LATITUDES_SOUTHERN_HEMISPHERE,
            SpaceWeatherRegion.SpaceWeatherLocation.MIDDLE_LATITUDES_SOUTHERN_HEMISPHERE,
            SpaceWeatherRegion.SpaceWeatherLocation.HIGH_LATITUDES_SOUTHERN_HEMISPHERE
    ));

    private SWXAmd79Tests() {
        throw new AssertionError();
    }

    public static AnalysisBuilderSpec.Builder analysisBuilder(final ZonedDateTime analysisTime) {
        return new AnalysisBuilderSpec.Builder()
                .setBaseTime(analysisTime);
    }

    public static PartialOrCompleteTimeInstant dayHourMinuteZoneAndCompleteTime(final ZonedDateTime completeTime) {
        return PartialOrCompleteTimeInstant.of(PartialDateTime.ofDayHourMinuteZone(completeTime, false), completeTime);
    }

    public static Stream<SpaceWeatherAdvisoryAnalysis> generateAnalyses(final ZonedDateTime baseTime) {
        return analysisBuilder(baseTime).generateAnalyses();
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

        public abstract List<SpaceWeatherAdvisoryAnalysis.NilPhenomenonReason> getNilPhenomenonReasons();

        public abstract List<SpaceWeatherRegion.SpaceWeatherLocation> getLocationIndicators();

        public abstract int getRegionsCount();

        public Stream<SpaceWeatherAdvisoryAnalysis> generateAnalyses() {
            return IntStream.range(0, 5)
                    .mapToObj(analysisIndex -> {
                        final ZonedDateTime analysisTime = getBaseTime().plusHours(analysisIndex);
                        return SpaceWeatherAdvisoryAnalysisImpl.builder()
                                .setTime(dayHourMinuteZoneAndCompleteTime(analysisTime))
                                .setAnalysisType(analysisIndex == 0
                                        ? SpaceWeatherAdvisoryAnalysis.Type.OBSERVATION
                                        : SpaceWeatherAdvisoryAnalysis.Type.FORECAST)
                                .setNullableNilPhenomenonReason(getNullableElement(analysisIndex, getNilPhenomenonReasons()))
                                .addAllRegions(generateRegions(analysisIndex, analysisTime.toInstant()))
                                .build();
                    });
        }

        private Stream<SpaceWeatherRegionImpl> generateRegions(final int analysisIndex, final Instant analysisTime) {
            return IntStream.range(0, getRegionsCount())
                    .mapToObj(subsetIndex ->
                            SpaceWeatherRegionImpl.fromLocationIndicator(
                                    getNullableElement(subsetIndex, getRegionsCount(), analysisIndex, getLocationIndicators()),
                                    analysisTime, -180.0, 180.0, null)
                    );
        }

        public static class Builder extends SWXAmd79Tests_AnalysisBuilderSpec_Builder {
            Builder() {
                setRegionsCount(REGIONS_COUNT_DEFAULT);
            }

            @Override
            public AnalysisBuilderSpec build() {
                if (getRegionsCount() > 0 && !getNilPhenomenonReasons().isEmpty()) {
                    throw new IllegalStateException("Nil phenomenon reasons are not supported when regionsCount > 0");
                }
                if (getLocationIndicators().isEmpty()) {
                    addAllLocationIndicators(LATITUDE_BANDS);
                }
                return super.build();
            }

            public Stream<SpaceWeatherAdvisoryAnalysis> generateAnalyses() {
                return build().generateAnalyses();
            }
        }
    }
}
