package fi.fmi.avi.model.taf.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Month;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.TAFAirTemperatureForecast;
import fi.fmi.avi.model.taf.TAFBaseForecast;
import fi.fmi.avi.model.taf.TAFChangeForecast;

public class TAFTimeReferencesTest {

    @Test
    public void testIssueTimeCompletion() {
        TAF msg = new TAF.Builder().setIssueTime(PartialOrCompleteTimeInstant.createIssueTime("201004Z"))
                .withCompleteIssueTime(YearMonth.of(2017, Month.DECEMBER))
                .buildPartial();

        assertTrue(msg.getIssueTime().getPartialTime().equals("201004Z"));
        PartialOrCompleteTimeInstant it = msg.getIssueTime();

        ZonedDateTime toMatch = ZonedDateTime.of(2017, 12, 20, 10, 4, 0, 0, ZoneId.of("Z"));
        assertFalse(it.isMidnight24h());
        assertTrue(it.getCompleteTime().isPresent());
        assertTrue(it.getCompleteTime().get().equals(toMatch));
        assertTrue(it.getCompleteTimeAsISOString().isPresent());
        assertTrue(it.getCompleteTimeAsISOString().get().equals("2017-12-20T10:04:00Z"));

    }

    @Test
    public void testCompleteMessageValidTime() {
        TAF msg = new TAF.Builder().setValidityTime(PartialOrCompleteTimePeriod.createValidityTimeDHDH("3118/0118"))
                .withCompleteForecastTimes(YearMonth.of(2017, Month.DECEMBER), 31, 18, ZoneId.of("Z"))
                .buildPartial();

        ZonedDateTime toMatch = ZonedDateTime.of(2017, 12, 31, 18, 0, 0, 0, ZoneId.of("Z"));

        //Validity time of the entire TAF:
        assertTrue(msg.getValidityTime().isPresent());
        PartialOrCompleteTimePeriod validityTime = msg.getValidityTime().get();

        assertTrue(validityTime.getStartTime().isPresent());
        assertFalse(validityTime.getStartTime().get().isMidnight24h());
        assertTrue(validityTime.getStartTime().get().getCompleteTime().isPresent());
        assertTrue(validityTime.getStartTime().get().getCompleteTime().get().equals(toMatch));
        assertTrue(validityTime.getStartTime().get().getCompleteTimeAsISOString().isPresent());
        assertTrue(validityTime.getStartTime().get().getCompleteTimeAsISOString().get().equals("2017-12-31T18:00:00Z"));

        toMatch = ZonedDateTime.of(2018, 1, 1, 18, 0, 0, 0, ZoneId.of("Z"));
        assertTrue(validityTime.getEndTime().isPresent());
        assertFalse(validityTime.getEndTime().get().isMidnight24h());
        assertTrue(validityTime.getEndTime().get().getCompleteTime().isPresent());
        assertTrue(validityTime.getEndTime().get().getCompleteTime().get().equals(toMatch));
        assertTrue(validityTime.getEndTime().get().getCompleteTimeAsISOString().isPresent());
        assertTrue(validityTime.getEndTime().get().getCompleteTimeAsISOString().get().equals("2018-01-01T18:00:00Z"));
    }

    @Test
    public void testCompleteChangeFctValidTimes() {
        List<TAFChangeForecast> changeForecasts = new ArrayList<>();
        changeForecasts.add(new TAFChangeForecast.Builder().setValidityTime(PartialOrCompleteTimePeriod.createValidityTimeDHDH("3119/3124")).buildPartial());
        changeForecasts.add(new TAFChangeForecast.Builder().setValidityTime(PartialOrCompleteTimePeriod.createValidityTimeDHDH("0100/0106")).buildPartial());
        changeForecasts.add(new TAFChangeForecast.Builder().setValidityTime(PartialOrCompleteTimePeriod.createValidityTimeDHDH("0102/0112")).buildPartial());

        TAF msg = new TAF.Builder().setChangeForecasts(changeForecasts)
                .withCompleteForecastTimes(YearMonth.of(2017, Month.DECEMBER), 31, 18, ZoneId.of("Z"))
                .buildPartial();

        assertTrue(msg.getChangeForecasts().isPresent());
        assertTrue(msg.getChangeForecasts().get().size() == 3);

        //Validity time of the 1st change forecast:
        TAFChangeForecast fct = msg.getChangeForecasts().get().get(0);
        PartialOrCompleteTimePeriod validityTime = fct.getValidityTime();
        ZonedDateTime toMatch = ZonedDateTime.of(2017, 12, 31, 19, 0, 0, 0, ZoneId.of("Z"));
        assertTrue(validityTime.getStartTime().isPresent());
        assertFalse(validityTime.getStartTime().get().isMidnight24h());
        assertTrue(validityTime.getStartTime().get().getCompleteTime().isPresent());
        assertTrue(validityTime.getStartTime().get().getCompleteTime().get().equals(toMatch));
        assertTrue(validityTime.getStartTime().get().getCompleteTimeAsISOString().isPresent());
        assertTrue(validityTime.getStartTime().get().getCompleteTimeAsISOString().get().equals("2017-12-31T19:00:00Z"));
        toMatch = ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("Z"));
        assertTrue(validityTime.getEndTime().isPresent());
        assertTrue(validityTime.getEndTime().get().isMidnight24h());
        assertTrue(validityTime.getEndTime().get().getCompleteTime().isPresent());
        assertTrue(validityTime.getEndTime().get().getCompleteTime().get().equals(toMatch));
        assertTrue(validityTime.getEndTime().get().getCompleteTimeAsISOString().isPresent());
        assertTrue(validityTime.getEndTime().get().getCompleteTimeAsISOString().get().equals("2018-01-01T00:00:00Z"));

        //Validity time of the 2nd change forecast:
        fct = msg.getChangeForecasts().get().get(1);
        validityTime = fct.getValidityTime();
        toMatch = ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("Z"));
        assertTrue(validityTime.getStartTime().isPresent());
        assertFalse(validityTime.getStartTime().get().isMidnight24h());
        assertTrue(validityTime.getStartTime().get().getCompleteTime().isPresent());
        assertTrue(validityTime.getStartTime().get().getCompleteTime().get().equals(toMatch));
        assertTrue(validityTime.getStartTime().get().getCompleteTimeAsISOString().isPresent());
        assertTrue(validityTime.getStartTime().get().getCompleteTimeAsISOString().get().equals("2018-01-01T00:00:00Z"));
        toMatch = ZonedDateTime.of(2018, 1, 1, 6, 0, 0, 0, ZoneId.of("Z"));
        assertTrue(validityTime.getEndTime().isPresent());
        assertFalse(validityTime.getEndTime().get().isMidnight24h());
        assertTrue(validityTime.getEndTime().get().getCompleteTime().isPresent());
        assertTrue(validityTime.getEndTime().get().getCompleteTime().get().equals(toMatch));
        assertTrue(validityTime.getEndTime().get().getCompleteTimeAsISOString().isPresent());
        assertTrue(validityTime.getEndTime().get().getCompleteTimeAsISOString().get().equals("2018-01-01T06:00:00Z"));

        //Validity time of the 3rd change forecast:
        fct = msg.getChangeForecasts().get().get(2);
        validityTime = fct.getValidityTime();
        toMatch = ZonedDateTime.of(2018, 1, 1, 2, 0, 0, 0, ZoneId.of("Z"));
        assertTrue(validityTime.getStartTime().isPresent());
        assertFalse(validityTime.getStartTime().get().isMidnight24h());
        assertTrue(validityTime.getStartTime().get().getCompleteTime().isPresent());
        assertTrue(validityTime.getStartTime().get().getCompleteTime().get().equals(toMatch));
        assertTrue(validityTime.getStartTime().get().getCompleteTimeAsISOString().isPresent());
        assertTrue(validityTime.getStartTime().get().getCompleteTimeAsISOString().get().equals("2018-01-01T02:00:00Z"));
        toMatch = ZonedDateTime.of(2018, 1, 1, 12, 0, 0, 0, ZoneId.of("Z"));
        assertTrue(validityTime.getEndTime().isPresent());
        assertFalse(validityTime.getEndTime().get().isMidnight24h());
        assertTrue(validityTime.getEndTime().get().getCompleteTime().isPresent());
        assertTrue(validityTime.getEndTime().get().getCompleteTime().get().equals(toMatch));
        assertTrue(validityTime.getEndTime().get().getCompleteTimeAsISOString().isPresent());
        assertTrue(validityTime.getEndTime().get().getCompleteTimeAsISOString().get().equals("2018-01-01T12:00:00Z"));
    }

    @Test
    public void testCompleteTempFctTimes() {
        List<TAFAirTemperatureForecast> temperatures = new ArrayList<>();
        temperatures.add(new TAFAirTemperatureForecast.Builder().setMaxTemperatureTime(PartialOrCompleteTimeInstant.createDayHourInstant("3118"))
                .setMinTemperatureTime(PartialOrCompleteTimeInstant.createDayHourInstant("0104"))
                .buildPartial());

        TAF msg = new TAF.Builder().setBaseForecast(new TAFBaseForecast.Builder().setTemperatures(temperatures).buildPartial())
                .withCompleteForecastTimes(YearMonth.of(2017, Month.DECEMBER), 31, 18, ZoneId.of("Z"))
                .buildPartial();

        assertTrue(msg.getBaseForecast().isPresent());
        assertTrue(msg.getBaseForecast().get().getTemperatures().isPresent());
        List<TAFAirTemperatureForecast> fcts = msg.getBaseForecast().get().getTemperatures().get();
        assertTrue(fcts.size() == 1);
        TAFAirTemperatureForecast fct = fcts.get(0);

        PartialOrCompleteTimeInstant t = fct.getMaxTemperatureTime();
        ZonedDateTime toMatch = ZonedDateTime.of(2017, 12, 31, 18, 0, 0, 0, ZoneId.of("Z"));
        assertFalse(t.isMidnight24h());
        assertTrue(t.getCompleteTime().isPresent());
        assertTrue(t.getCompleteTime().get().equals(toMatch));
        assertTrue(t.getCompleteTimeAsISOString().isPresent());
        assertTrue(t.getCompleteTimeAsISOString().get().equals("2017-12-31T18:00:00Z"));

        t = fct.getMinTemperatureTime();
        toMatch = ZonedDateTime.of(2018, 1, 1, 4, 0, 0, 0, ZoneId.of("Z"));
        assertFalse(t.isMidnight24h());
        assertTrue(t.getCompleteTime().isPresent());
        assertTrue(t.getCompleteTime().get().equals(toMatch));
        assertTrue(t.getCompleteTimeAsISOString().isPresent());
        assertTrue(t.getCompleteTimeAsISOString().get().equals("2018-01-01T04:00:00Z"));

    }

}
