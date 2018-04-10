package fi.fmi.avi.model.metar;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.PartialOrCompleteTimePeriod;

@FreeBuilder
@JsonDeserialize(builder = METAR.Builder.class)
public interface METAR extends MeteorologicalTerminalAirReport {

    boolean isRoutineDelayed();

    Builder toBuilder();

    class Builder extends METAR_Builder {

        public Builder withCompleteForecastTimes(final YearMonth issueYearMonth, int issueDay, int issueHour, final ZoneId tz) throws IllegalArgumentException {
            final ZonedDateTime approximateIssueTime = ZonedDateTime.of(
                    LocalDateTime.of(issueYearMonth.getYear(), issueYearMonth.getMonth(), issueDay, issueHour, 0), tz);
            Builder retval = this;
            if (getTrends().isPresent() && !getTrends().get().isEmpty()) {
                List<TrendForecast> oldTrends = getTrends().get();
                List<PartialOrCompleteTimePeriod> list = oldTrends.stream().map(fct -> fct.getValidityTime()).collect(Collectors.toList());
                list = PartialOrCompleteTimePeriod.completePartialTimeReferenceList(list, approximateIssueTime);
                List<TrendForecast> newTrends = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    newTrends.add(oldTrends.get(i).toBuilder().setValidityTime(list.get(i)).build());
                }
                retval = retval.setTrends(newTrends);
            }
            return retval;
        }

        public Builder withCompleteIssueTime(final YearMonth yearMonth) throws IllegalArgumentException {
            return mutateIssueTime((input) -> input.completedWithIssueYearMonth(yearMonth));
        }
    }


}
