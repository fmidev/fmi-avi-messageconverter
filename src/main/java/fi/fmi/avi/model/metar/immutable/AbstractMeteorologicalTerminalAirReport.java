package fi.fmi.avi.model.metar.immutable;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.metar.MeteorologicalTerminalAirReport;
import fi.fmi.avi.model.metar.TrendForecast;

/**
 * Created by rinne on 13/04/2018.
 */
public abstract class AbstractMeteorologicalTerminalAirReport implements MeteorologicalTerminalAirReport {

    protected static Optional<List<TrendForecast>> getTimeCompletedTrends(final Optional<List<TrendForecast>> oldTrends, final YearMonth issueYearMonth,
            int issueDay, int issueHour, final ZoneId tz) throws IllegalArgumentException {
        Optional<List<TrendForecast>> retval = Optional.empty();
        if (oldTrends.isPresent()) {
            final ZonedDateTime approximateIssueTime = ZonedDateTime.of(
                    LocalDateTime.of(issueYearMonth.getYear(), issueYearMonth.getMonth(), issueDay, issueHour, 0), tz);
            retval = Optional.of(new ArrayList<>());
            List<PartialOrCompleteTimePeriod> list = oldTrends.get().stream().map(fct -> fct.getValidityTime()).collect(Collectors.toList());
            list = PartialOrCompleteTimePeriod.completePartialTimeReferenceList(list, approximateIssueTime);
            for (int i = 0; i < list.size(); i++) {
                retval.get().add(TrendForecastImpl.Builder.from(oldTrends.get().get(i)).setValidityTime(list.get(i)).build());
            }
        }
        return retval;
    }
}
