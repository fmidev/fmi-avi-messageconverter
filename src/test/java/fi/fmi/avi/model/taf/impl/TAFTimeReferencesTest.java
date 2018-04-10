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

import fi.fmi.avi.model.PartialOrCompleteTimeInstance;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.TAFAirTemperatureForecast;
import fi.fmi.avi.model.taf.TAFBaseForecast;
import fi.fmi.avi.model.taf.TAFChangeForecast;

public class TAFTimeReferencesTest {

    @Test
    public void testCompleteMessageValidTime() {
        TAF msg = new TAF.Builder().setValidityTime(new PartialOrCompleteTimePeriod.Builder().setStartTime(
                new PartialOrCompleteTimeInstance.Builder().setPartialTime("3118")
                        .setPartialTimePattern(PartialOrCompleteTimeInstance.DAY_HOUR_PATTERN)
                        .build())
                .setEndTime(new PartialOrCompleteTimeInstance.Builder().setPartialTime("0118")
                        .setPartialTimePattern(PartialOrCompleteTimeInstance.DAY_HOUR_PATTERN)
                        .build())
                .build()).withCompleteForecastTimes(YearMonth.of(2017, Month.DECEMBER), 31, 18, ZoneId.of("Z")).buildPartial();

        ZonedDateTime toMatch = ZonedDateTime.of(2017, 12, 31, 18, 0, 0, 0, ZoneId.of("Z"));

        //Validity time of the entire TAF:
        assertTrue(msg.getValidityTime().isPresent());
        PartialOrCompleteTimePeriod validityTime = msg.getValidityTime().get();

        assertFalse(validityTime.getStartTime().isMidnight24h());
        assertTrue(validityTime.getStartTime().getCompleteTime().isPresent());
        assertTrue(validityTime.getStartTime().getCompleteTime().get().equals(toMatch));
        assertTrue(validityTime.getStartTime().getCompleteTimeAsISOString().isPresent());
        assertTrue(validityTime.getStartTime().getCompleteTimeAsISOString().get().equals("2017-12-31T18:00:00Z"));

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
        changeForecasts.add(new TAFChangeForecast.Builder().setValidityTime(new PartialOrCompleteTimePeriod.Builder().setStartTime(
                new PartialOrCompleteTimeInstance.Builder().setPartialTime("3119").setPartialTimePattern(PartialOrCompleteTimeInstance.DAY_HOUR_PATTERN).build())
                .setEndTime(new PartialOrCompleteTimeInstance.Builder().setPartialTime("3124")
                        .setPartialTimePattern(PartialOrCompleteTimeInstance.DAY_HOUR_PATTERN)
                        .build())
                .build()).buildPartial());
        changeForecasts.add(new TAFChangeForecast.Builder().setValidityTime(new PartialOrCompleteTimePeriod.Builder().setStartTime(
                new PartialOrCompleteTimeInstance.Builder().setPartialTime("0100").setPartialTimePattern(PartialOrCompleteTimeInstance.DAY_HOUR_PATTERN).build())
                .setEndTime(new PartialOrCompleteTimeInstance.Builder().setPartialTime("0106")
                        .setPartialTimePattern(PartialOrCompleteTimeInstance.DAY_HOUR_PATTERN)
                        .build())
                .build()).buildPartial());
        changeForecasts.add(new TAFChangeForecast.Builder().setValidityTime(new PartialOrCompleteTimePeriod.Builder().setStartTime(
                new PartialOrCompleteTimeInstance.Builder().setPartialTime("0102").setPartialTimePattern(PartialOrCompleteTimeInstance.DAY_HOUR_PATTERN).build())
                .setEndTime(new PartialOrCompleteTimeInstance.Builder().setPartialTime("0112")
                        .setPartialTimePattern(PartialOrCompleteTimeInstance.DAY_HOUR_PATTERN)
                        .build())
                .build()).buildPartial());

        TAF msg = new TAF.Builder().setChangeForecasts(changeForecasts)
                .withCompleteForecastTimes(YearMonth.of(2017, Month.DECEMBER), 31, 18, ZoneId.of("Z"))
                .buildPartial();

        assertTrue(msg.getChangeForecasts().isPresent());
        assertTrue(msg.getChangeForecasts().get().size() == 3);

        //Validity time of the 1st change forecast:
        TAFChangeForecast fct = msg.getChangeForecasts().get().get(0);
        PartialOrCompleteTimePeriod validityTime = fct.getValidityTime();
        ZonedDateTime toMatch = ZonedDateTime.of(2017, 12, 31, 19, 0, 0, 0, ZoneId.of("Z"));
        assertFalse(validityTime.getStartTime().isMidnight24h());
        assertTrue(validityTime.getStartTime().getCompleteTime().isPresent());
        assertTrue(validityTime.getStartTime().getCompleteTime().get().equals(toMatch));
        assertTrue(validityTime.getStartTime().getCompleteTimeAsISOString().isPresent());
        assertTrue(validityTime.getStartTime().getCompleteTimeAsISOString().get().equals("2017-12-31T19:00:00Z"));
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
        assertFalse(validityTime.getStartTime().isMidnight24h());
        assertTrue(validityTime.getStartTime().getCompleteTime().isPresent());
        assertTrue(validityTime.getStartTime().getCompleteTime().get().equals(toMatch));
        assertTrue(validityTime.getStartTime().getCompleteTimeAsISOString().isPresent());
        assertTrue(validityTime.getStartTime().getCompleteTimeAsISOString().get().equals("2018-01-01T00:00:00Z"));
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
        assertFalse(validityTime.getStartTime().isMidnight24h());
        assertTrue(validityTime.getStartTime().getCompleteTime().isPresent());
        assertTrue(validityTime.getStartTime().getCompleteTime().get().equals(toMatch));
        assertTrue(validityTime.getStartTime().getCompleteTimeAsISOString().isPresent());
        assertTrue(validityTime.getStartTime().getCompleteTimeAsISOString().get().equals("2018-01-01T02:00:00Z"));
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
        temperatures.add(new TAFAirTemperatureForecast.Builder().setMaxTemperatureTime(new PartialOrCompleteTimeInstance.Builder().setPartialTime("3118")
                .setPartialTimePattern(PartialOrCompleteTimeInstance.DAY_HOUR_PATTERN)
                .build())
                .setMinTemperatureTime(new PartialOrCompleteTimeInstance.Builder().setPartialTime("0104")
                        .setPartialTimePattern(PartialOrCompleteTimeInstance.DAY_HOUR_PATTERN)
                        .build())
                .buildPartial());

        TAF msg = new TAF.Builder().setBaseForecast(new TAFBaseForecast.Builder().setTemperatures(temperatures).buildPartial())
                .withCompleteForecastTimes(YearMonth.of(2017, Month.DECEMBER), 31, 18, ZoneId.of("Z"))
                .buildPartial();

        assertTrue(msg.getBaseForecast().isPresent());
        assertTrue(msg.getBaseForecast().get().getTemperatures().isPresent());
        List<TAFAirTemperatureForecast> fcts = msg.getBaseForecast().get().getTemperatures().get();
        assertTrue(fcts.size() == 1);
        TAFAirTemperatureForecast fct = fcts.get(0);

        PartialOrCompleteTimeInstance t = fct.getMaxTemperatureTime();
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
