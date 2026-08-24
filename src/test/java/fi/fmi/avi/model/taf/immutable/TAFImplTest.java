package fi.fmi.avi.model.taf.immutable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.ZonedDateTime;
import java.util.Collections;

import org.junit.Test;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.immutable.AerodromeImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;

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
    // NOTE: Immutables has no buildPartial() equivalent (FreeBuilder-only; see docs/07-modernization-plan.md),
    // so tests below now populate every required property (aerodrome, temperatures, changeIndicator) with
    // these arbitrary placeholder values before calling build().
    private static final AerodromeImpl DUMMY_AERODROME = AerodromeImpl.builder().setDesignator("XXXX").build();
    private static final NumericMeasure DUMMY_TEMPERATURE = NumericMeasureImpl.of(0, "degC");

    @Test
    public void testAreAllTimeReferencesCompleteOnEmpty() {
        final TAFImpl taf = TAFImpl.builder()//
                .setAerodrome(DUMMY_AERODROME)//
                .build();
        assertTrue(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnPartialIssueTime() {
        final TAFImpl taf = TAFImpl.builder()//
                .setAerodrome(DUMMY_AERODROME)//
                .setIssueTime(PARTIAL_ISSUE_TIME)//
                .build();
        assertFalse(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnCompletedPartialIssueTime() {
        final TAFImpl taf = TAFImpl.builder()//
                .setAerodrome(DUMMY_AERODROME)//
                .setIssueTime(PARTIAL_ISSUE_TIME)//
                .withCompleteIssueTimeNear(ZONED_DATE_TIME)//
                .build();
        assertTrue(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnAllCompletedPartialIssueTime() {
        final TAFImpl taf = TAFImpl.builder()//
                .setAerodrome(DUMMY_AERODROME)//
                .setIssueTime(PARTIAL_ISSUE_TIME)//
                .withAllTimesComplete(ZONED_DATE_TIME)//
                .build();
        assertTrue(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnCompleteIssueTime() {
        final TAFImpl taf = TAFImpl.builder()//
                .setAerodrome(DUMMY_AERODROME)//
                .setIssueTime(COMPLETE_TIME_INSTANT)//
                .build();
        assertTrue(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnPartialValidityTime() {
        final TAFImpl taf = TAFImpl.builder()//
                .setAerodrome(DUMMY_AERODROME)//
                .setValidityTime(PARTIAL_TIME_PERIOD)//
                .build();
        assertFalse(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnCompletedPartialValidityTime() {
        final TAFImpl taf = TAFImpl.builder()//
                .setAerodrome(DUMMY_AERODROME)//
                .setValidityTime(PARTIAL_TIME_PERIOD)//
                .withCompleteForecastTimes(ZONED_DATE_TIME)//
                .build();
        assertTrue(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnAllCompletedPartialValidityTime() {
        final TAFImpl taf = TAFImpl.builder()//
                .setAerodrome(DUMMY_AERODROME)//
                .setValidityTime(PARTIAL_TIME_PERIOD)//
                .withAllTimesComplete(ZONED_DATE_TIME)//
                .build();
        assertTrue(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnCompleteValidityTime() {
        final TAFImpl taf = TAFImpl.builder()//
                .setAerodrome(DUMMY_AERODROME)//
                .setValidityTime(COMPLETE_TIME_PERIOD)//
                .build();
        assertTrue(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnPartialReferredReportValidPeriod() {
        final TAFImpl taf = TAFImpl.builder()//
                .setAerodrome(DUMMY_AERODROME)//
                .setReferredReportValidPeriod(PARTIAL_TIME_PERIOD)//
                .build();
        assertFalse(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnCompletedPartialReferredReportValidPeriod() {
        final TAFImpl taf = TAFImpl.builder()//
                .setAerodrome(DUMMY_AERODROME)//
                .setReferredReportValidPeriod(PARTIAL_TIME_PERIOD)//
                .withCompleteForecastTimes(ZONED_DATE_TIME)//
                .build();
        assertTrue(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnAllCompletedPartialReferredReportValidPeriod() {
        final TAFImpl taf = TAFImpl.builder()//
                .setAerodrome(DUMMY_AERODROME)//
                .setReferredReportValidPeriod(PARTIAL_TIME_PERIOD)//
                .withAllTimesComplete(ZONED_DATE_TIME)//
                .build();
        assertTrue(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnCompleteReferredReportValidPeriod() {
        final TAFImpl taf = TAFImpl.builder()//
                .setAerodrome(DUMMY_AERODROME)//
                .setReferredReportValidPeriod(COMPLETE_TIME_PERIOD)//
                .build();
        assertTrue(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnPartialBaseForecastTimes() {
        final TAFImpl taf = TAFImpl.builder()//
                .setAerodrome(DUMMY_AERODROME)//
                .setBaseForecast(TAFBaseForecastImpl.builder()//
                        .setTemperatures(Collections.singletonList(TAFAirTemperatureForecastImpl.builder()//
                                .setMaxTemperature(DUMMY_TEMPERATURE)//
                                .setMinTemperature(DUMMY_TEMPERATURE)//
                                .setMinTemperatureTime(PARTIAL_ISSUE_TIME)//
                                .setMaxTemperatureTime(PARTIAL_ISSUE_TIME)//
                                .build()))//
                        .build())//
                .build();
        assertFalse(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnCompletedPartialBaseForecastTimes() {
        final TAFImpl taf = TAFImpl.builder()//
                .setAerodrome(DUMMY_AERODROME)//
                .setBaseForecast(TAFBaseForecastImpl.builder()//
                        .setTemperatures(Collections.singletonList(TAFAirTemperatureForecastImpl.builder()//
                                .setMaxTemperature(DUMMY_TEMPERATURE)//
                                .setMinTemperature(DUMMY_TEMPERATURE)//
                                .setMinTemperatureTime(PARTIAL_ISSUE_TIME)//
                                .setMaxTemperatureTime(PARTIAL_ISSUE_TIME)//
                                .build()))//
                        .build())//
                .withCompleteForecastTimes(ZONED_DATE_TIME)//
                .build();
        assertTrue(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnAllCompletedPartialBaseForecastTimes() {
        final TAFImpl taf = TAFImpl.builder()//
                .setAerodrome(DUMMY_AERODROME)//
                .setBaseForecast(TAFBaseForecastImpl.builder()//
                        .setTemperatures(Collections.singletonList(TAFAirTemperatureForecastImpl.builder()//
                                .setMaxTemperature(DUMMY_TEMPERATURE)//
                                .setMinTemperature(DUMMY_TEMPERATURE)//
                                .setMinTemperatureTime(PARTIAL_ISSUE_TIME)//
                                .setMaxTemperatureTime(PARTIAL_ISSUE_TIME)//
                                .build()))//
                        .build())//
                .withAllTimesComplete(ZONED_DATE_TIME)//
                .build();
        assertTrue(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnCompleteBaseForecastTimes() {
        final TAFImpl taf = TAFImpl.builder()//
                .setAerodrome(DUMMY_AERODROME)//
                .setBaseForecast(TAFBaseForecastImpl.builder()//
                        .setTemperatures(Collections.singletonList(TAFAirTemperatureForecastImpl.builder()//
                                .setMaxTemperature(DUMMY_TEMPERATURE)//
                                .setMinTemperature(DUMMY_TEMPERATURE)//
                                .setMinTemperatureTime(COMPLETE_TIME_INSTANT)//
                                .setMaxTemperatureTime(COMPLETE_TIME_INSTANT)//
                                .build()))//
                        .build())//
                .build();
        assertTrue(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnPartialChangeForecastTimes() {
        final TAFImpl taf = TAFImpl.builder()//
                .setAerodrome(DUMMY_AERODROME)//
                .setChangeForecasts(Collections.singletonList(TAFChangeForecastImpl.builder()//
                        .setChangeIndicator(AviationCodeListUser.TAFChangeIndicator.FROM)//
                        .setPeriodOfChange(PARTIAL_TIME_PERIOD)//
                        .build()))//
                .build();
        assertFalse(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnCompletedPartialChangeForecastTimes() {
        final TAFImpl taf = TAFImpl.builder()//
                .setAerodrome(DUMMY_AERODROME)//
                .setChangeForecasts(Collections.singletonList(TAFChangeForecastImpl.builder()//
                        .setChangeIndicator(AviationCodeListUser.TAFChangeIndicator.FROM)//
                        .setPeriodOfChange(PARTIAL_TIME_PERIOD)//
                        .build()))//
                .withCompleteForecastTimes(ZONED_DATE_TIME)//
                .build();
        assertTrue(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnAllCompletedPartialChangeForecastTimes() {
        final TAFImpl taf = TAFImpl.builder()//
                .setAerodrome(DUMMY_AERODROME)//
                .setChangeForecasts(Collections.singletonList(TAFChangeForecastImpl.builder()//
                        .setChangeIndicator(AviationCodeListUser.TAFChangeIndicator.FROM)//
                        .setPeriodOfChange(PARTIAL_TIME_PERIOD)//
                        .build()))//
                .withAllTimesComplete(ZONED_DATE_TIME)//
                .build();
        assertTrue(taf.areAllTimeReferencesComplete());
    }

    @Test
    public void testAreAllTimeReferencesCompleteOnCompleteChangeForecastTimes() {
        final TAFImpl taf = TAFImpl.builder()//
                .setAerodrome(DUMMY_AERODROME)//
                .setChangeForecasts(Collections.singletonList(TAFChangeForecastImpl.builder()//
                        .setChangeIndicator(AviationCodeListUser.TAFChangeIndicator.FROM)//
                        .setPeriodOfChange(COMPLETE_TIME_PERIOD)//
                        .build()))//
                .build();
        assertTrue(taf.areAllTimeReferencesComplete());
    }
}
