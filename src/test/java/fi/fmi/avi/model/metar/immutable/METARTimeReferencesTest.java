package fi.fmi.avi.model.metar.immutable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Month;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.metar.METAR;
import fi.fmi.avi.model.metar.TrendForecast;

public class METARTimeReferencesTest {

    @Test
    public void testIssueTimeCompletion() {
        METAR msg = new METARImpl.Builder()//
                .setIssueTime(PartialOrCompleteTimeInstant.createIssueTime("311004Z"))//
                .withCompleteIssueTime(YearMonth.of(2017, Month.DECEMBER))//
                .buildPartial();

        assertTrue(msg.getIssueTime().getPartialTime().isPresent());
        assertTrue(msg.getIssueTime().getPartialTime().get().equals("311004Z"));
        PartialOrCompleteTimeInstant it = msg.getIssueTime();

        ZonedDateTime toMatch = ZonedDateTime.of(2017, 12, 31, 10, 4, 0, 0, ZoneId.of("Z"));
        assertFalse(it.isMidnight24h());
        assertTrue(it.getCompleteTime().isPresent());
        assertTrue(it.getCompleteTime().get().equals(toMatch));
        assertTrue(it.getCompleteTimeAsISOString().isPresent());
        assertTrue(it.getCompleteTimeAsISOString().get().equals("2017-12-31T10:04:00Z"));

    }

    @Test
    public void testTrendValidTimeCompletion() {
        List<TrendForecast> changeForecasts = new ArrayList<>();
        changeForecasts.add(new TrendForecastImpl.Builder().setPeriodOfChange(new PartialOrCompleteTimePeriod.Builder()//
                .withTrendTimeGroupToken("FM1130")//
                .withTrendTimeGroupToken("TL1300")//
                .build())//
                .setCeilingAndVisibilityOk(true)
                .setChangeIndicator(AviationCodeListUser.TrendForecastChangeIndicator.TEMPORARY_FLUCTUATIONS)
                .setNoSignificantWeather(true)
                .build());
        changeForecasts.add(
                new TrendForecastImpl.Builder().setPeriodOfChange(new PartialOrCompleteTimePeriod.Builder().withTrendTimeGroupToken("TL0900").build())
                .setCeilingAndVisibilityOk(true)
                .setChangeIndicator(AviationCodeListUser.TrendForecastChangeIndicator.TEMPORARY_FLUCTUATIONS)
                .setNoSignificantWeather(true)
                .build());
        changeForecasts.add(
                new TrendForecastImpl.Builder().setInstantOfChange(new PartialOrCompleteTimeInstant.Builder().withTrendTimeGroupToken("AT1200").build())
                .setCeilingAndVisibilityOk(true)
                .setChangeIndicator(AviationCodeListUser.TrendForecastChangeIndicator.TEMPORARY_FLUCTUATIONS)
                .setNoSignificantWeather(true)
                .build());

        METAR msg = new METARImpl.Builder()//
                .setTrends(changeForecasts)//
                .withCompleteForecastTimes(YearMonth.of(2017, Month.DECEMBER), 31, 10, ZoneId.of("Z"))//
                .buildPartial();

        assertTrue(msg.getTrends().isPresent());
        assertTrue(msg.getTrends().get().size() == 3);

        //Validity time of the 1st trend forecast:
        TrendForecast fct = msg.getTrends().get().get(0);
        Optional<PartialOrCompleteTimePeriod> period = fct.getPeriodOfChange();
        assertTrue(period.isPresent());
        ZonedDateTime toMatch = ZonedDateTime.of(2017, 12, 31, 11, 30, 0, 0, ZoneId.of("Z"));
        assertTrue(period.get().getStartTime().isPresent());
        assertFalse(period.get().getStartTime().get().isMidnight24h());
        assertTrue(period.get().getStartTime().get().getCompleteTime().isPresent());
        assertTrue(period.get().getStartTime().get().getCompleteTime().get().equals(toMatch));
        assertTrue(period.get().getStartTime().get().getCompleteTimeAsISOString().isPresent());
        assertTrue(period.get().getStartTime().get().getCompleteTimeAsISOString().get().equals("2017-12-31T11:30:00Z"));
        toMatch = ZonedDateTime.of(2017, 12, 31, 13, 00, 0, 0, ZoneId.of("Z"));
        assertTrue(period.get().getEndTime().isPresent());
        assertFalse(period.get().getEndTime().get().isMidnight24h());
        assertTrue(period.get().getEndTime().get().getCompleteTime().isPresent());
        assertTrue(period.get().getEndTime().get().getCompleteTime().get().equals(toMatch));
        assertTrue(period.get().getEndTime().get().getCompleteTimeAsISOString().isPresent());
        assertTrue(period.get().getEndTime().get().getCompleteTimeAsISOString().get().equals("2017-12-31T13:00:00Z"));

        //Validity time of the 2nd trend forecast:
        fct = msg.getTrends().get().get(1);
        period = fct.getPeriodOfChange();
        toMatch = ZonedDateTime.of(2018, 1, 1, 9, 0, 0, 0, ZoneId.of("Z"));
        assertTrue(period.isPresent());
        assertFalse(period.get().getStartTime().isPresent());

        assertTrue(period.get().getEndTime().isPresent());
        assertFalse(period.get().getEndTime().get().isMidnight24h());
        assertTrue(period.get().getEndTime().get().getCompleteTime().isPresent());
        assertTrue(period.get().getEndTime().get().getCompleteTime().get().equals(toMatch));
        assertTrue(period.get().getEndTime().get().getCompleteTimeAsISOString().isPresent());
        assertTrue(period.get().getEndTime().get().getCompleteTimeAsISOString().get().equals("2018-01-01T09:00:00Z"));

        //Validity time of the 3rd trend forecast:
        fct = msg.getTrends().get().get(2);
        assertFalse(fct.getPeriodOfChange().isPresent());
        Optional<PartialOrCompleteTimeInstant> instant = fct.getInstantOfChange();
        toMatch = ZonedDateTime.of(2018, 1, 1, 12, 0, 0, 0, ZoneId.of("Z"));
        assertTrue(instant.isPresent());
        assertFalse(instant.get().isMidnight24h());
        assertTrue(instant.get().getCompleteTime().isPresent());
        assertTrue(instant.get().getCompleteTime().get().equals(toMatch));
        assertTrue(instant.get().getCompleteTimeAsISOString().isPresent());
        assertTrue(instant.get().getCompleteTimeAsISOString().get().equals("2018-01-01T12:00:00Z"));


    }

}
