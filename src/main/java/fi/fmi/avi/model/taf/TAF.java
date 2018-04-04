package fi.fmi.avi.model.taf;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.AerodromeWeatherMessage;
import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;

/**
 * Created by rinne on 30/01/15.
 */
@FreeBuilder
@JsonDeserialize(builder = TAF.Builder.class)
public interface TAF extends AerodromeWeatherMessage, AviationCodeListUser {

    TAFStatus status();

    Optional<PartialOrCompleteTimePeriod> validityTime();

    Optional<TAFBaseForecast> baseForecast();

    Optional<List<TAFChangeForecast>> changeForecasts();

    Optional<TAFReference> referredReport();

    Builder toBuilder();

    class Builder extends TAF_Builder {
        public Builder() {
            translated(false);
            status(TAFStatus.NORMAL);
        }

        public TAF.Builder withCompleteForecastTimes(final YearMonth issueYearMonth, int issueDay, int issueHour, final ZoneId tz)
                throws IllegalArgumentException {
            final ZonedDateTime approximateIssueTime = ZonedDateTime.of(
                    LocalDateTime.of(issueYearMonth.getYear(), issueYearMonth.getMonth(), issueDay, issueHour, 0), tz);
            TAF.Builder retval = this;
            if (validityTime().isPresent()) {
                retval = retval.mapValidityTime(vTime -> PartialOrCompleteTimePeriod.completePartialTimeReference(vTime, approximateIssueTime));
            }

            if (baseForecast().isPresent() && baseForecast().get().temperatures().isPresent()) {
                List<TAFAirTemperatureForecast> newTemps = new ArrayList<>();
                for (final TAFAirTemperatureForecast airTemp : baseForecast().get().temperatures().get()) {
                    newTemps.add(airTemp.toBuilder()
                            .mutateMinTemperatureTime(time -> time.completedWithYearMonthDay(issueYearMonth, issueDay).build())
                            .mutateMaxTemperatureTime(time -> time.completedWithYearMonthDay(issueYearMonth, issueDay).build())
                            .build());
                }
                retval = retval.mapBaseForecast(fct -> fct.toBuilder().temperatures(newTemps).build());

            }
            if (changeForecasts().isPresent() && !changeForecasts().get().isEmpty()) {
                List<TAFChangeForecast> oldFcts = changeForecasts().get();
                List<PartialOrCompleteTimePeriod> list = oldFcts.stream().map(fct -> fct.validityTime()).collect(Collectors.toList());
                list = PartialOrCompleteTimePeriod.completePartialTimeReferenceList(list, approximateIssueTime);
                List<TAFChangeForecast> newFcts = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    newFcts.add(oldFcts.get(i).toBuilder().validityTime(list.get(i)).build());
                }
                retval = retval.changeForecasts(newFcts);
            }
            return retval;
        }

        public TAF.Builder withCompleteIssueTime(final YearMonth yearMonth) throws IllegalArgumentException {
            return mutateIssueTime((input) -> input.completedWithYearMonth(yearMonth));
        }
    }

}
