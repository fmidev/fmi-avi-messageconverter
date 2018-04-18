package fi.fmi.avi.model.metar.immutable;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.immutable.AerodromeImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.WeatherImpl;
import fi.fmi.avi.model.metar.SPECI;

@FreeBuilder
@JsonDeserialize(builder = SPECIImpl.Builder.class)
public abstract class SPECIImpl extends AbstractMeteorologicalTerminalAirReport implements SPECI, Serializable {

    public static SPECIImpl immutableCopyOf(final SPECI speci) {
        checkNotNull(speci);
        if (speci instanceof SPECIImpl) {
            return (SPECIImpl) speci;
        } else {
            return SPECIImpl.Builder.from(speci).build();
        }
    }

    public static Optional<SPECIImpl> immutableCopyOf(final Optional<SPECI> speci) {
        checkNotNull(speci);
        return speci.map(SPECIImpl::immutableCopyOf);
    }

    abstract Builder toBuilder();

    public static class Builder extends SPECIImpl_Builder {

        // It's very unfortunate that the code needs to be copied here from
        // the METARImpl.Builder. Even if METARImpl and SPECIImpl
        // both extend AbstractMeteorologicalTerminalReport, their generated
        // Builder classes have no common ancestor.

        public static SPECIImpl.Builder from(final SPECI value) {
            //From AviationWeatherMessage:
            SPECIImpl.Builder retval = new SPECIImpl.Builder().setIssueTime(value.getIssueTime())
                    .setPermissibleUsage(value.getPermissibleUsage())
                    .setPermissibleUsageReason(value.getPermissibleUsageReason())
                    .setPermissibleUsageSupplementary(value.getPermissibleUsageSupplementary())
                    .setTranslated(value.isTranslated())
                    .setTranslatedBulletinID(value.getTranslatedBulletinID())
                    .setTranslatedBulletinReceptionTime(value.getTranslatedBulletinReceptionTime())
                    .setTranslationCentreDesignator(value.getTranslationCentreDesignator())
                    .setTranslationCentreName(value.getTranslationCentreName())
                    .setTranslationTime(value.getTranslationTime())
                    .setTranslatedTAC(value.getTranslatedTAC());

            value.getRemarks().map(remarks -> retval.setRemarks(Collections.unmodifiableList(remarks)));

            //From AerodromeWeatherMessage:
            retval.setAerodrome(AerodromeImpl.immutableCopyOf(value.getAerodrome()));

            //From MeteorologicalTerminalAirReport:
            retval.setAutomatedStation(value.isAutomatedStation())
                    .setStatus(value.getStatus())
                    .setCeilingAndVisibilityOk(value.isCeilingAndVisibilityOk())
                    .setAirTemperature(NumericMeasureImpl.immutableCopyOf(value.getAirTemperature()))
                    .setDewpointTemperature(NumericMeasureImpl.immutableCopyOf(value.getDewpointTemperature()))
                    .setAltimeterSettingQNH(NumericMeasureImpl.immutableCopyOf(value.getAltimeterSettingQNH()))
                    .setSurfaceWind(ObservedSurfaceWindImpl.immutableCopyOf(value.getSurfaceWind()))
                    .setVisibility(HorizontalVisibilityImpl.immutableCopyOf(value.getVisibility()))
                    .setClouds(ObservedCloudsImpl.immutableCopyOf(value.getClouds()))
                    .setWindShear(WindShearImpl.immutableCopyOf(value.getWindShear()))
                    .setSeaState(SeaStateImpl.immutableCopyOf(value.getSeaState()))
                    .setColorState(value.getColorState());

            value.getRunwayVisualRanges()
                    .map(ranges -> retval.setRunwayVisualRanges(
                            Collections.unmodifiableList(ranges.stream().map(RunwayVisualRangeImpl::immutableCopyOf).collect(Collectors.toList()))));

            value.getPresentWeather()
                    .map(weather -> retval.setPresentWeather(
                            Collections.unmodifiableList(weather.stream().map(WeatherImpl::immutableCopyOf).collect(Collectors.toList()))));

            value.getRecentWeather()
                    .map(weather -> retval.setRecentWeather(
                            Collections.unmodifiableList(weather.stream().map(WeatherImpl::immutableCopyOf).collect(Collectors.toList()))

                    ));

            value.getRunwayStates()
                    .map(states -> retval.setRunwayStates(
                            Collections.unmodifiableList(states.stream().map(RunwayStateImpl::immutableCopyOf).collect(Collectors.toList()))));

            value.getTrends()
                    .map(trends -> retval.setTrends(
                            Collections.unmodifiableList(trends.stream().map(TrendForecastImpl::immutableCopyOf).collect(Collectors.toList()))));

            return retval;
        }

        public Builder withCompleteForecastTimes(final YearMonth issueYearMonth, int issueDay, int issueHour, final ZoneId tz) throws IllegalArgumentException {
            return setTrends(getTimeCompletedTrends(getTrends(), issueYearMonth, issueDay, issueHour, tz));
        }

        public Builder withCompleteIssueTime(final YearMonth yearMonth) throws IllegalArgumentException {
            return mutateIssueTime((input) -> input.completedWithIssueYearMonth(yearMonth));
        }
    }

}
