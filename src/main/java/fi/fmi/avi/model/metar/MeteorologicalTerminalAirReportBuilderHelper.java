package fi.fmi.avi.model.metar;

import static fi.fmi.avi.model.taf.TAFForecastBuilderHelper.toImmutableList;
import static java.util.Objects.requireNonNull;

import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.immutable.AerodromeImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.WeatherImpl;
import fi.fmi.avi.model.metar.immutable.ObservedCloudsImpl;
import fi.fmi.avi.model.metar.immutable.ObservedSurfaceWindImpl;
import fi.fmi.avi.model.metar.immutable.RunwayStateImpl;
import fi.fmi.avi.model.metar.immutable.RunwayVisualRangeImpl;
import fi.fmi.avi.model.metar.immutable.SeaStateImpl;
import fi.fmi.avi.model.metar.immutable.TrendForecastImpl;

/**
 * Helper methods for implementations of {@link MeteorologicalTerminalAirReport.Builder}.
 */
public final class MeteorologicalTerminalAirReportBuilderHelper {
    public MeteorologicalTerminalAirReportBuilderHelper() {
        throw new UnsupportedOperationException();
    }

    public static void copyFrom(final MeteorologicalTerminalAirReport.Builder<?, ?> builder, final MeteorologicalTerminalAirReport value) {
        requireNonNull(builder, "builder");
        requireNonNull(value, "value");

        //From AviationWeatherMessage:
        builder.setPermissibleUsage(value.getPermissibleUsage());
        builder.setPermissibleUsageReason(value.getPermissibleUsageReason());
        builder.setPermissibleUsageSupplementary(value.getPermissibleUsageSupplementary());
        builder.setTranslated(value.isTranslated());
        builder.setTranslatedBulletinID(value.getTranslatedBulletinID());
        builder.setTranslatedBulletinReceptionTime(value.getTranslatedBulletinReceptionTime());
        builder.setTranslationCentreDesignator(value.getTranslationCentreDesignator());
        builder.setTranslationCentreName(value.getTranslationCentreName());
        builder.setTranslationTime(value.getTranslationTime());
        builder.setTranslatedTAC(value.getTranslatedTAC());
        builder.setRemarks(value.getRemarks());

        //From AerodromeWeatherMessage:
        builder.setIssueTime(PartialOrCompleteTimeInstant.Builder.from((value.getIssueTime())));
        builder.setAerodrome(AerodromeImpl.immutableCopyOf(value.getAerodrome()));

        //From MeteorologicalTerminalAirReport:
        builder.setAutomatedStation(value.isAutomatedStation());
        builder.setSnowClosure(value.isSnowClosure());
        builder.setCeilingAndVisibilityOk(value.isCeilingAndVisibilityOk());
        builder.setNoSignificantChanges(value.isNoSignificantChanges());
        builder.setAirTemperature(NumericMeasureImpl.immutableCopyOf(value.getAirTemperature()));
        builder.setAltimeterSettingQNH(NumericMeasureImpl.immutableCopyOf(value.getAltimeterSettingQNH()));
        builder.setClouds(ObservedCloudsImpl.immutableCopyOf(value.getClouds()));
        builder.setColorState(value.getColorState());
        builder.setDewpointTemperature(NumericMeasureImpl.immutableCopyOf(value.getDewpointTemperature()));
        builder.setPresentWeather(value.getPresentWeather().map(list -> toImmutableList(list, WeatherImpl::immutableCopyOf)));
        builder.setRecentWeather(value.getRecentWeather().map(list -> toImmutableList(list, WeatherImpl::immutableCopyOf)));
        builder.setRunwayStates(value.getRunwayStates().map(list -> toImmutableList(list, RunwayStateImpl::immutableCopyOf)));
        builder.setRunwayVisualRanges(value.getRunwayVisualRanges().map(list -> toImmutableList(list, RunwayVisualRangeImpl::immutableCopyOf)));
        builder.setSeaState(SeaStateImpl.immutableCopyOf(value.getSeaState()));
        builder.setSurfaceWind(ObservedSurfaceWindImpl.immutableCopyOf(value.getSurfaceWind()));
        builder.setStatus(value.getStatus());
        builder.setTrends(value.getTrends().map(list -> toImmutableList(list, TrendForecastImpl::immutableCopyOf)));
    }

}
