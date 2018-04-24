package fi.fmi.avi.model.metar.immutable;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.PartialOrCompleteTime;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.immutable.AerodromeImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.WeatherImpl;
import fi.fmi.avi.model.metar.METAR;
import fi.fmi.avi.model.metar.MeteorologicalTerminalAirReport;
import fi.fmi.avi.model.metar.TrendForecast;

@FreeBuilder
@JsonDeserialize(builder = METARImpl.Builder.class)
public abstract class METARImpl implements METAR, Serializable, MeteorologicalTerminalAirReport {

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

    protected static Optional<List<TrendForecast>> getTimeCompletedTrends(final List<TrendForecast> oldTrends, final YearMonth issueYearMonth, int issueDay,
            int issueHour, final ZoneId tz) throws IllegalArgumentException {
        Optional<List<TrendForecast>> retval = Optional.empty();
        if (oldTrends != null) {
            final ZonedDateTime approximateIssueTime = ZonedDateTime.of(
                    LocalDateTime.of(issueYearMonth.getYear(), issueYearMonth.getMonth(), issueDay, issueHour, 0), tz);
            retval = Optional.of(new ArrayList<>());
            List<PartialOrCompleteTime> list = new ArrayList<>();
            for (TrendForecast fct : oldTrends) {
                if (fct.getPeriodOfChange().isPresent()) {
                    list.add(fct.getPeriodOfChange().get());
                } else if (fct.getInstantOfChange().isPresent()) {
                    list.add(fct.getInstantOfChange().get());
                }
            }
            list = PartialOrCompleteTimePeriod.completePartialTimeReferenceList(list, approximateIssueTime);
            for (int i = 0; i < list.size(); i++) {
                PartialOrCompleteTime time = list.get(i);
                if (time instanceof PartialOrCompleteTimePeriod) {
                    retval.get().add(TrendForecastImpl.Builder.from(oldTrends.get(i)).setPeriodOfChange((PartialOrCompleteTimePeriod) time).build());
                } else if (time instanceof PartialOrCompleteTimeInstant) {
                    retval.get().add(TrendForecastImpl.Builder.from(oldTrends.get(i)).setInstantOfChange((PartialOrCompleteTimeInstant) time).build());
                }
            }
        }
        return retval;
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
            if (getTrends().isPresent()) {
                return setTrends(getTimeCompletedTrends(getTrends().get(), issueYearMonth, issueDay, issueHour, tz));
            } else {
                return this;
            }
        }

        public Builder withCompleteIssueTime(final YearMonth yearMonth) throws IllegalArgumentException {
            return mutateIssueTime((input) -> input.completedWithIssueYearMonth(yearMonth));
        }
    }

}
