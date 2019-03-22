package fi.fmi.avi.model.metar.immutable;

import static org.junit.Assert.assertEquals;
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
import fi.fmi.avi.model.PartialDateTime;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.metar.METAR;
import fi.fmi.avi.model.metar.TrendForecast;

public class METARTimeReferencesTest {

    @Test
    public void testIssueTimeCompletion() {
        final METAR msg = METARImpl.builder()//
                .setIssueTime(PartialOrCompleteTimeInstant.createIssueTime("311004Z"))//
                .withCompleteIssueTime(YearMonth.of(2017, Month.DECEMBER))//
                .buildPartial();

        assertEquals(Optional.of(PartialDateTime.parse("--31T10:04Z")), msg.getIssueTime().getPartialTime());
        final PartialOrCompleteTimeInstant it = msg.getIssueTime();

        final ZonedDateTime toMatch = ZonedDateTime.of(2017, 12, 31, 10, 4, 0, 0, ZoneId.of("Z"));
        assertFalse(it.isMidnight24h());
        assertEquals(Optional.of(toMatch), it.getCompleteTime());

    }

    @Test
    public void testTrendValidTimeCompletion() {
        final List<TrendForecast> changeForecasts = new ArrayList<>();
        changeForecasts.add(TrendForecastImpl.builder()//
                .setPeriodOfChange(PartialOrCompleteTimePeriod.builder()//
                        .setTrendTimeGroupToken("FM1130")//
                        .setTrendTimeGroupToken("TL1300")//
                        .build())//
                .setCeilingAndVisibilityOk(true)//
                .setChangeIndicator(AviationCodeListUser.TrendForecastChangeIndicator.TEMPORARY_FLUCTUATIONS)//
                .setNoSignificantWeather(true)//
                .build());
        changeForecasts.add(TrendForecastImpl.builder()//
                .setPeriodOfChange(PartialOrCompleteTimePeriod.builder().setTrendTimeGroupToken("TL0900").build())//
                .setCeilingAndVisibilityOk(true)//
                .setChangeIndicator(AviationCodeListUser.TrendForecastChangeIndicator.TEMPORARY_FLUCTUATIONS)//
                .setNoSignificantWeather(true)//
                .build());
        changeForecasts.add(TrendForecastImpl.builder()//
                .setInstantOfChange(PartialOrCompleteTimeInstant.builder().setTrendTimeGroupToken("AT1200").build())//
                .setCeilingAndVisibilityOk(true)//
                .setChangeIndicator(AviationCodeListUser.TrendForecastChangeIndicator.TEMPORARY_FLUCTUATIONS)//
                .setNoSignificantWeather(true)//
                .build());

        final METAR msg = METARImpl.builder()//
                .setTrends(changeForecasts)//
                .withCompleteForecastTimes(YearMonth.of(2017, Month.DECEMBER), 31, 10, ZoneId.of("Z"))//
                .buildPartial();

        assertTrue(msg.getTrends().isPresent());
        assertEquals(3, msg.getTrends().get().size());

        //Validity time of the 1st trend forecast:
        TrendForecast fct = msg.getTrends().get().get(0);
        Optional<PartialOrCompleteTimePeriod> period = fct.getPeriodOfChange();
        assertTrue(period.isPresent());
        ZonedDateTime toMatch = ZonedDateTime.of(2017, 12, 31, 11, 30, 0, 0, ZoneId.of("Z"));
        assertTrue(period.get().getStartTime().isPresent());
        assertFalse(period.get().getStartTime().get().isMidnight24h());
        assertTrue(period.get().getStartTime().get().getCompleteTime().isPresent());
        assertEquals(period.get().getStartTime().get().getCompleteTime().get(), toMatch);
        toMatch = ZonedDateTime.of(2017, 12, 31, 13, 0, 0, 0, ZoneId.of("Z"));
        assertTrue(period.get().getEndTime().isPresent());
        assertFalse(period.get().getEndTime().get().isMidnight24h());
        assertTrue(period.get().getEndTime().get().getCompleteTime().isPresent());
        assertEquals(period.get().getEndTime().get().getCompleteTime().get(), toMatch);

        //Validity time of the 2nd trend forecast:
        fct = msg.getTrends().get().get(1);
        period = fct.getPeriodOfChange();
        toMatch = ZonedDateTime.of(2018, 1, 1, 9, 0, 0, 0, ZoneId.of("Z"));
        assertTrue(period.isPresent());
        assertFalse(period.get().getStartTime().isPresent());

        assertTrue(period.get().getEndTime().isPresent());
        assertFalse(period.get().getEndTime().get().isMidnight24h());
        assertTrue(period.get().getEndTime().get().getCompleteTime().isPresent());
        assertEquals(period.get().getEndTime().get().getCompleteTime().get(), toMatch);

        //Validity time of the 3rd trend forecast:
        fct = msg.getTrends().get().get(2);
        assertFalse(fct.getPeriodOfChange().isPresent());
        final Optional<PartialOrCompleteTimeInstant> instant = fct.getInstantOfChange();
        toMatch = ZonedDateTime.of(2018, 1, 1, 12, 0, 0, 0, ZoneId.of("Z"));
        assertTrue(instant.isPresent());
        assertFalse(instant.get().isMidnight24h());
        assertTrue(instant.get().getCompleteTime().isPresent());
        assertEquals(instant.get().getCompleteTime().get(), toMatch);

    }

}
