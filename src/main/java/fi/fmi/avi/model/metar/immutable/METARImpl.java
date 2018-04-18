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
import fi.fmi.avi.model.metar.METAR;

@FreeBuilder
@JsonDeserialize(builder = METARImpl.Builder.class)
public abstract class METARImpl extends AbstractMeteorologicalTerminalAirReport implements METAR, Serializable {

    public static METARImpl immutableCopyOf(final METAR metar) {
        checkNotNull(metar);
        if (metar instanceof METARImpl) {
            return (METARImpl) metar;
        } else {
            return Builder.from(metar).build();
        }
    }

    public static Optional<METARImpl> immutableCopyOf(final Optional<METAR> metar) {
        checkNotNull(metar);
        return metar.map(METARImpl::immutableCopyOf);
    }

    abstract Builder toBuilder();

    public static class Builder extends METARImpl_Builder {

        public static Builder from(final METAR value) {
            //From AviationWeatherMessage:
            METARImpl.Builder retval = new METARImpl.Builder().setIssueTime(value.getIssueTime())
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

            //From METAR:
            retval.setRoutineDelayed(value.isRoutineDelayed());

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
