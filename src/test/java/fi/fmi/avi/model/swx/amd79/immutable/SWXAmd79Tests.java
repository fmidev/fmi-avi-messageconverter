package fi.fmi.avi.model.swx.amd79.immutable;

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
import fi.fmi.avi.model.swx.amd79.SpaceWeatherAdvisoryAnalysis;
import fi.fmi.avi.model.swx.amd79.SpaceWeatherRegion;

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

    /**
     * Test-only fixture value type, migrated off {@code @FreeBuilder} onto Immutables. {@link Builder} is a
     * hand-written "detached builder" (see docs/07-modernization-plan.md) because {@link Builder#build()} needs to
     * perform cross-field validation and defaulting that requires reading back already-set builder state
     * (region count, nil phenomenon reasons, location indicators) — reads which a plain Immutables-generated
     * builder (extending {@code ImmutableAnalysisBuilderSpec.Builder}) does not expose.
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
                                .setNilPhenomenonReason(Optional.ofNullable(getNullableElement(analysisIndex, getNilPhenomenonReasons())))
                                .addAllRegions(generateRegions(analysisIndex, analysisTime.toInstant()).collect(Collectors.toList()))
                                .build();
                    });
        }

        private Stream<SpaceWeatherRegionImpl> generateRegions(final int analysisIndex, final Instant analysisTime) {
            return IntStream.range(0, getRegionsCount())
                    .mapToObj(subsetIndex ->
                            SpaceWeatherRegionImpl.fromLocationIndicator(
                                    getNullableElement(subsetIndex, getRegionsCount(), analysisIndex, getLocationIndicators()),
                                    VerticalLimitsImpl.none(), analysisTime, -180.0, 180.0)
                    );
        }

        public static class Builder {
            private ZonedDateTime baseTime;
            private final List<SpaceWeatherAdvisoryAnalysis.NilPhenomenonReason> nilPhenomenonReasons = new ArrayList<>();
            private final List<SpaceWeatherRegion.SpaceWeatherLocation> locationIndicators = new ArrayList<>();
            private int regionsCount = REGIONS_COUNT_DEFAULT;
            private boolean regionsCountFromLocationIndicators;

            public Builder setBaseTime(final ZonedDateTime baseTime) {
                this.baseTime = requireNonNull(baseTime, "baseTime");
                return this;
            }

            public List<SpaceWeatherAdvisoryAnalysis.NilPhenomenonReason> getNilPhenomenonReasons() {
                return nilPhenomenonReasons;
            }

            public Builder addNilPhenomenonReasons(final SpaceWeatherAdvisoryAnalysis.NilPhenomenonReason... reasons) {
                Collections.addAll(nilPhenomenonReasons, reasons);
                return this;
            }

            public Builder addAllNilPhenomenonReasons(final Iterable<? extends SpaceWeatherAdvisoryAnalysis.NilPhenomenonReason> reasons) {
                for (final SpaceWeatherAdvisoryAnalysis.NilPhenomenonReason reason : reasons) {
                    nilPhenomenonReasons.add(reason);
                }
                return this;
            }

            public List<SpaceWeatherRegion.SpaceWeatherLocation> getLocationIndicators() {
                return locationIndicators;
            }

            public Builder addLocationIndicators(final SpaceWeatherRegion.SpaceWeatherLocation... indicators) {
                Collections.addAll(locationIndicators, indicators);
                return this;
            }

            public Builder addAllLocationIndicators(final Iterable<? extends SpaceWeatherRegion.SpaceWeatherLocation> indicators) {
                for (final SpaceWeatherRegion.SpaceWeatherLocation indicator : indicators) {
                    locationIndicators.add(indicator);
                }
                return this;
            }

            public int getRegionsCount() {
                return regionsCountFromLocationIndicators ? locationIndicators.size() : regionsCount;
            }

            public Builder setRegionsCount(final int regionsCount) {
                this.regionsCountFromLocationIndicators = false;
                this.regionsCount = regionsCount;
                return this;
            }

            public Builder setRegionsCountFromLocationIndicators() {
                this.regionsCountFromLocationIndicators = true;
                return this;
            }

            public AnalysisBuilderSpec build() {
                if (getRegionsCount() > 0 && !getNilPhenomenonReasons().isEmpty()) {
                    throw new IllegalStateException("Nil phenomenon reasons are not supported when regionsCount > 0");
                }
                if (getLocationIndicators().isEmpty()) {
                    addAllLocationIndicators(LATITUDE_BANDS);
                }
                return ImmutableAnalysisBuilderSpec.internalBuilder()//
                        .setBaseTime(requireNonNull(baseTime, "baseTime"))//
                        .addAllNilPhenomenonReasons(nilPhenomenonReasons)//
                        .addAllLocationIndicators(locationIndicators)//
                        .setRegionsCount(getRegionsCount())//
                        .build();
            }

            public Stream<SpaceWeatherAdvisoryAnalysis> generateAnalyses() {
                return build().generateAnalyses();
            }
        }
    }
}
