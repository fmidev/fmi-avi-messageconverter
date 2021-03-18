package fi.fmi.avi.model.metar;

import static fi.fmi.avi.model.BuilderHelper.toImmutableList;
import static java.util.Objects.requireNonNull;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fi.fmi.avi.model.Aerodrome;
import fi.fmi.avi.model.AerodromeWeatherMessageBuilderHelper;
import fi.fmi.avi.model.AviationWeatherMessageBuilderHelper;
import fi.fmi.avi.model.PartialOrCompleteTime;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.PartialOrCompleteTimes;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.RunwayDirectionImpl;
import fi.fmi.avi.model.immutable.WeatherImpl;
import fi.fmi.avi.model.metar.immutable.HorizontalVisibilityImpl;
import fi.fmi.avi.model.metar.immutable.ObservedCloudsImpl;
import fi.fmi.avi.model.metar.immutable.ObservedSurfaceWindImpl;
import fi.fmi.avi.model.metar.immutable.RunwayStateImpl;
import fi.fmi.avi.model.metar.immutable.RunwayVisualRangeImpl;
import fi.fmi.avi.model.metar.immutable.SeaStateImpl;
import fi.fmi.avi.model.metar.immutable.TrendForecastImpl;
import fi.fmi.avi.model.metar.immutable.WindShearImpl;

/**
 * Helper methods for implementations of {@link MeteorologicalTerminalAirReportBuilder}.
 */
public final class MeteorologicalTerminalAirReportBuilderHelper {
    public MeteorologicalTerminalAirReportBuilderHelper() {
        throw new UnsupportedOperationException();
    }

    public static void copyFrom(final MeteorologicalTerminalAirReportBuilder<?, ?> builder, final MeteorologicalTerminalAirReport value) {
        requireNonNull(builder, "builder");
        requireNonNull(value, "value");

        AviationWeatherMessageBuilderHelper.copyFrom(builder, value,  //
                MeteorologicalTerminalAirReportBuilder::setRemarks, //
                MeteorologicalTerminalAirReportBuilder::setPermissibleUsage, //
                MeteorologicalTerminalAirReportBuilder::setPermissibleUsageReason, //
                MeteorologicalTerminalAirReportBuilder::setPermissibleUsageSupplementary, //
                MeteorologicalTerminalAirReportBuilder::setTranslated, //
                MeteorologicalTerminalAirReportBuilder::setTranslatedBulletinID, //
                MeteorologicalTerminalAirReportBuilder::setTranslatedBulletinReceptionTime, //
                MeteorologicalTerminalAirReportBuilder::setTranslationCentreDesignator, //
                MeteorologicalTerminalAirReportBuilder::setTranslationCentreName, //
                MeteorologicalTerminalAirReportBuilder::setTranslationTime, //
                MeteorologicalTerminalAirReportBuilder::setTranslatedTAC, //
                MeteorologicalTerminalAirReportBuilder::setIssueTime, //
                MeteorologicalTerminalAirReportBuilder::setReportStatus);
        AerodromeWeatherMessageBuilderHelper.copyFrom(builder, value,  //
                MeteorologicalTerminalAirReportBuilder::setAerodrome);
        builder//
                .setAutomatedStation(value.isAutomatedStation())//
                .setMissingMessage(value.isMissingMessage())//
                .setCeilingAndVisibilityOk(value.isCeilingAndVisibilityOk())//
                .setAirTemperature(NumericMeasureImpl.immutableCopyOf(value.getAirTemperature()))//
                .setDewpointTemperature(NumericMeasureImpl.immutableCopyOf(value.getDewpointTemperature()))//
                .setAltimeterSettingQNH(NumericMeasureImpl.immutableCopyOf(value.getAltimeterSettingQNH()))//
                .setSurfaceWind(ObservedSurfaceWindImpl.immutableCopyOf(value.getSurfaceWind()))//
                .setVisibility(HorizontalVisibilityImpl.immutableCopyOf(value.getVisibility()))//
                .setRunwayVisualRanges(value.getRunwayVisualRanges().map(list -> toImmutableList(list, RunwayVisualRangeImpl::immutableCopyOf)))//
                .setPresentWeather(value.getPresentWeather().map(list -> toImmutableList(list, WeatherImpl::immutableCopyOf)))//
                .setClouds(ObservedCloudsImpl.immutableCopyOf(value.getClouds()))//
                .setRecentWeather(value.getRecentWeather().map(list -> toImmutableList(list, WeatherImpl::immutableCopyOf)))//
                .setWindShear(WindShearImpl.immutableCopyOf(value.getWindShear()))//
                .setSeaState(SeaStateImpl.immutableCopyOf(value.getSeaState()))//
                .setRunwayStates(value.getRunwayStates().map(list -> toImmutableList(list, RunwayStateImpl::immutableCopyOf)))//
                .setSnowClosure(value.isSnowClosure())//
                .setNoSignificantChanges(value.isNoSignificantChanges())//
                .setTrends(value.getTrends().map(list -> toImmutableList(list, TrendForecastImpl::immutableCopyOf)))//
                .setColorState(value.getColorState());
    }

    public static void afterSetAerodrome(final MeteorologicalTerminalAirReportBuilder<?, ?> builder, final Aerodrome aerodrome) {
        if (builder.getRunwayStates().isPresent()) {
            final List<RunwayState> oldStates = builder.getRunwayStates().get();
            final List<RunwayState> newStates = new ArrayList<>(oldStates.size());
            for (final RunwayState state : oldStates) {
                if (state.getRunwayDirection().isPresent()) {
                    if (state.getRunwayDirection().get().getAssociatedAirportHeliport().isPresent()) {
                        final RunwayStateImpl.Builder runWayBuilder = RunwayStateImpl.immutableCopyOf(state).toBuilder();
                        runWayBuilder.setRunwayDirection(RunwayDirectionImpl.immutableCopyOf(state.getRunwayDirection().get()).toBuilder()//
                                .setAssociatedAirportHeliport(aerodrome).build());

                        newStates.add(runWayBuilder.build());
                    }
                }
            }
            builder.setRunwayStates(newStates);
        }
        if (builder.getRunwayVisualRanges().isPresent()) {
            final List<RunwayVisualRange> oldRanges = builder.getRunwayVisualRanges().get();
            final List<RunwayVisualRange> newRanges = new ArrayList<>(oldRanges.size());
            for (final RunwayVisualRange range : oldRanges) {
                if (range.getRunwayDirection().getAssociatedAirportHeliport().isPresent()) {
                    final RunwayVisualRangeImpl.Builder runwayVisualBuilder = RunwayVisualRangeImpl.immutableCopyOf(range).toBuilder();
                    runwayVisualBuilder.setRunwayDirection(RunwayDirectionImpl.immutableCopyOf(runwayVisualBuilder.getRunwayDirection()).toBuilder()//
                            .setAssociatedAirportHeliport(aerodrome)//
                            .build());
                    newRanges.add(runwayVisualBuilder.build());
                }
            }
            builder.setRunwayVisualRanges(newRanges);
        }
    }

    public static List<TrendForecast> completeTrendTimes(final List<TrendForecast> trendForecasts, final ZonedDateTime reference) {
        requireNonNull(trendForecasts, "trendForecasts");
        requireNonNull(reference, "reference");
        if (trendForecasts.isEmpty()) {
            return Collections.emptyList();
        }
        final List<TrendForecast> builder = new ArrayList<>(trendForecasts.size());
        List<PartialOrCompleteTime> times = new ArrayList<>(trendForecasts.size());
        for (final TrendForecast forecast : trendForecasts) {
            if (forecast.getPeriodOfChange().isPresent()) {
                times.add(forecast.getPeriodOfChange().get());
            } else if (forecast.getInstantOfChange().isPresent()) {
                times.add(forecast.getInstantOfChange().get());
            } else {
                times.add(null);
            }
        }
        times = PartialOrCompleteTimes.completeAscendingPartialTimes(times, reference);
        for (int i = 0; i < times.size(); i++) {
            final PartialOrCompleteTime time = times.get(i);
            if (time instanceof PartialOrCompleteTimePeriod) {
                builder.add(TrendForecastImpl.Builder.from(trendForecasts.get(i)).setPeriodOfChange((PartialOrCompleteTimePeriod) time).build());
            } else if (time instanceof PartialOrCompleteTimeInstant) {
                builder.add(TrendForecastImpl.Builder.from(trendForecasts.get(i)).setInstantOfChange((PartialOrCompleteTimeInstant) time).build());
            }
        }
        return Collections.unmodifiableList(builder);
    }

}
