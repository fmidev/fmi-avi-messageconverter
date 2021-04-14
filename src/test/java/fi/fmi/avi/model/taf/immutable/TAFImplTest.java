package fi.fmi.avi.model.taf.immutable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.ZonedDateTime;
import java.util.Collections;

import org.junit.Test;

import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;

public class TAFImplTest {

    private static final ZonedDateTime ZONED_DATE_TIME = ZonedDateTime.parse("2020-01-02T03:00:00Z");
    private static final PartialOrCompleteTimeInstant COMPLETE_TIME_INSTANT = PartialOrCompleteTimeInstant.of(ZONED_DATE_TIME);
    private static final PartialOrCompleteTimePeriod COMPLETE_TIME_PERIOD = PartialOrCompleteTimePeriod.builder()//
            .setStartTime(COMPLETE_TIME_INSTANT)//
            .setEndTime(COMPLETE_TIME_INSTANT.toBuilder()//
                    .mapCompleteTime(completeTime -> completeTime.plusHours(24))//
                    .build()).build();
    private static final PartialOrCompleteTimePeriod PARTIAL_TIME_PERIOD = PartialOrCompleteTimePeriod.createValidityTime("0103/0203");
    private static final PartialOrCompleteTimeInstant PARTIAL_ISSUE_TIME = PartialOrCompleteTimeInstant.createIssueTime("010300Z");

    @Test
    public void testAreAllTimeReferencesCompleteOnEmpty() {
        final TAFImpl taf = TAFImpl.builder()//
                .buildPartial();
        assertTrue(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnPartialIssueTime() {
        final TAFImpl taf = TAFImpl.builder()//
                .setIssueTime(PARTIAL_ISSUE_TIME)//
                .buildPartial();
        assertFalse(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnCompletedPartialIssueTime() {
        final TAFImpl taf = TAFImpl.builder()//
                .setIssueTime(PARTIAL_ISSUE_TIME)//
                .withCompleteIssueTimeNear(ZONED_DATE_TIME)//
                .buildPartial();
        assertTrue(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnAllCompletedPartialIssueTime() {
        final TAFImpl taf = TAFImpl.builder()//
                .setIssueTime(PARTIAL_ISSUE_TIME)//
                .withAllTimesComplete(ZONED_DATE_TIME)//
                .buildPartial();
        assertTrue(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnCompleteIssueTime() {
        final TAFImpl taf = TAFImpl.builder()//
                .setIssueTime(COMPLETE_TIME_INSTANT)//
                .buildPartial();
        assertTrue(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnPartialValidityTime() {
        final TAFImpl taf = TAFImpl.builder()//
                .setValidityTime(PARTIAL_TIME_PERIOD)//
                .buildPartial();
        assertFalse(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnCompletedPartialValidityTime() {
        final TAFImpl taf = TAFImpl.builder()//
                .setValidityTime(PARTIAL_TIME_PERIOD)//
                .withCompleteForecastTimes(ZONED_DATE_TIME)//
                .buildPartial();
        assertTrue(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnAllCompletedPartialValidityTime() {
        final TAFImpl taf = TAFImpl.builder()//
                .setValidityTime(PARTIAL_TIME_PERIOD)//
                .withAllTimesComplete(ZONED_DATE_TIME)//
                .buildPartial();
        assertTrue(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnCompleteValidityTime() {
        final TAFImpl taf = TAFImpl.builder()//
                .setValidityTime(COMPLETE_TIME_PERIOD)//
                .buildPartial();
        assertTrue(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnPartialReferredReportValidPeriod() {
        final TAFImpl taf = TAFImpl.builder()//
                .setReferredReportValidPeriod(PARTIAL_TIME_PERIOD)//
                .buildPartial();
        assertFalse(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnCompletedPartialReferredReportValidPeriod() {
        final TAFImpl taf = TAFImpl.builder()//
                .setReferredReportValidPeriod(PARTIAL_TIME_PERIOD)//
                .withCompleteForecastTimes(ZONED_DATE_TIME)//
                .buildPartial();
        assertTrue(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnAllCompletedPartialReferredReportValidPeriod() {
        final TAFImpl taf = TAFImpl.builder()//
                .setReferredReportValidPeriod(PARTIAL_TIME_PERIOD)//
                .withAllTimesComplete(ZONED_DATE_TIME)//
                .buildPartial();
        assertTrue(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnCompleteReferredReportValidPeriod() {
        final TAFImpl taf = TAFImpl.builder()//
                .setReferredReportValidPeriod(COMPLETE_TIME_PERIOD)//
                .buildPartial();
        assertTrue(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnPartialBaseForecastTimes() {
        final TAFImpl taf = TAFImpl.builder()//
                .setBaseForecast(TAFBaseForecastImpl.builder()//
                        .setTemperatures(Collections.singletonList(TAFAirTemperatureForecastImpl.builder()//
                                .setMinTemperatureTime(PARTIAL_ISSUE_TIME)//
                                .setMaxTemperatureTime(PARTIAL_ISSUE_TIME)//
                                .buildPartial()))//
                        .buildPartial())//
                .buildPartial();
        assertFalse(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnCompletedPartialBaseForecastTimes() {
        final TAFImpl taf = TAFImpl.builder()//
                .setBaseForecast(TAFBaseForecastImpl.builder()//
                        .setTemperatures(Collections.singletonList(TAFAirTemperatureForecastImpl.builder()//
                                .setMinTemperatureTime(PARTIAL_ISSUE_TIME)//
                                .setMaxTemperatureTime(PARTIAL_ISSUE_TIME)//
                                .buildPartial()))//
                        .buildPartial())//
                .withCompleteForecastTimes(ZONED_DATE_TIME)//
                .buildPartial();
        assertTrue(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnAllCompletedPartialBaseForecastTimes() {
        final TAFImpl taf = TAFImpl.builder()//
                .setBaseForecast(TAFBaseForecastImpl.builder()//
                        .setTemperatures(Collections.singletonList(TAFAirTemperatureForecastImpl.builder()//
                                .setMinTemperatureTime(PARTIAL_ISSUE_TIME)//
                                .setMaxTemperatureTime(PARTIAL_ISSUE_TIME)//
                                .buildPartial()))//
                        .buildPartial())//
                .withAllTimesComplete(ZONED_DATE_TIME)//
                .buildPartial();
        assertTrue(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnCompleteBaseForecastTimes() {
        final TAFImpl taf = TAFImpl.builder()//
                .setBaseForecast(TAFBaseForecastImpl.builder()//
                        .setTemperatures(Collections.singletonList(TAFAirTemperatureForecastImpl.builder()//
                                .setMinTemperatureTime(COMPLETE_TIME_INSTANT)//
                                .setMaxTemperatureTime(COMPLETE_TIME_INSTANT)//
                                .buildPartial()))//
                        .buildPartial())//
                .buildPartial();
        assertTrue(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnPartialChangeForecastTimes() {
        final TAFImpl taf = TAFImpl.builder()//
                .setChangeForecasts(Collections.singletonList(TAFChangeForecastImpl.builder()//
                        .setPeriodOfChange(PARTIAL_TIME_PERIOD)//
                        .buildPartial()))//
                .buildPartial();
        assertFalse(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnCompletedPartialChangeForecastTimes() {
        final TAFImpl taf = TAFImpl.builder()//
                .setChangeForecasts(Collections.singletonList(TAFChangeForecastImpl.builder()//
                        .setPeriodOfChange(PARTIAL_TIME_PERIOD)//
                        .buildPartial()))//
                .withCompleteForecastTimes(ZONED_DATE_TIME)//
                .buildPartial();
        assertTrue(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnAllCompletedPartialChangeForecastTimes() {
        final TAFImpl taf = TAFImpl.builder()//
                .setChangeForecasts(Collections.singletonList(TAFChangeForecastImpl.builder()//
                        .setPeriodOfChange(PARTIAL_TIME_PERIOD)//
                        .buildPartial()))//
                .withAllTimesComplete(ZONED_DATE_TIME)//
                .buildPartial();
        assertTrue(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnCompleteChangeForecastTimes() {
        final TAFImpl taf = TAFImpl.builder()//
                .setChangeForecasts(Collections.singletonList(TAFChangeForecastImpl.builder()//
                        .setPeriodOfChange(COMPLETE_TIME_PERIOD)//
                        .buildPartial()))//
                .buildPartial();
        assertTrue(taf.areAllTimeReferencesComplete());
    }
}
