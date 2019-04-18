package fi.fmi.avi.model.taf.impl;

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
import fi.fmi.avi.model.immutable.AerodromeImpl;
import fi.fmi.avi.model.immutable.CloudForecastImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.SurfaceWindImpl;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.TAFAirTemperatureForecast;
import fi.fmi.avi.model.taf.TAFChangeForecast;
import fi.fmi.avi.model.taf.immutable.TAFAirTemperatureForecastImpl;
import fi.fmi.avi.model.taf.immutable.TAFBaseForecastImpl;
import fi.fmi.avi.model.taf.immutable.TAFChangeForecastImpl;
import fi.fmi.avi.model.taf.immutable.TAFImpl;

public class TAFTimeReferencesTest {

    @Test
    public void testIssueTimeCompletion() {
        final TAF msg = TAFImpl.builder()//
                .setIssueTime(PartialOrCompleteTimeInstant.createIssueTime("201004Z"))//
                .withCompleteIssueTime(YearMonth.of(2017, Month.DECEMBER))//
                .buildPartial();

        assertTrue(msg.getIssueTime().isPresent());
        assertEquals(Optional.of(PartialDateTime.parse("--20T10:04Z")), msg.getIssueTime().get().getPartialTime());
        final Optional<PartialOrCompleteTimeInstant> it = msg.getIssueTime();

        final ZonedDateTime toMatch = ZonedDateTime.of(2017, 12, 20, 10, 4, 0, 0, ZoneId.of("Z"));
        assertTrue(it.isPresent());
        assertFalse(it.get().isMidnight24h());
        assertEquals(Optional.of(toMatch), it.get().getCompleteTime());
    }

    @Test
    public void testCompleteMessageValidTime() {
        final TAF msg = TAFImpl.builder()//
                .setValidityTime(PartialOrCompleteTimePeriod.createValidityTimeDHDH("3118/0118"))//
                .withCompleteForecastTimes(YearMonth.of(2017, Month.DECEMBER), 31, 18, ZoneId.of("Z"))//
                .buildPartial();

        ZonedDateTime toMatch = ZonedDateTime.of(2017, 12, 31, 18, 0, 0, 0, ZoneId.of("Z"));

        //Validity time of the entire TAF:
        assertTrue(msg.getValidityTime().isPresent());
        final PartialOrCompleteTimePeriod validityTime = msg.getValidityTime().get();

        assertTrue(validityTime.getStartTime().isPresent());
        assertFalse(validityTime.getStartTime().get().isMidnight24h());
        assertTrue(validityTime.getStartTime().get().getCompleteTime().isPresent());
        assertEquals(validityTime.getStartTime().get().getCompleteTime().get(), toMatch);

        toMatch = ZonedDateTime.of(2018, 1, 1, 18, 0, 0, 0, ZoneId.of("Z"));
        assertTrue(validityTime.getEndTime().isPresent());
        assertFalse(validityTime.getEndTime().get().isMidnight24h());
        assertTrue(validityTime.getEndTime().get().getCompleteTime().isPresent());
        assertEquals(validityTime.getEndTime().get().getCompleteTime().get(), toMatch);
    }

    @Test
    public void testCompleteChangeFctValidTimes() {
        final List<TAFChangeForecast> changeForecasts = new ArrayList<>();
        changeForecasts.add(TAFChangeForecastImpl.builder().setPeriodOfChange(PartialOrCompleteTimePeriod.createValidityTimeDHDH("3119/3124"))//
                .setChangeIndicator(AviationCodeListUser.TAFChangeIndicator.FROM)//
                .setCeilingAndVisibilityOk(true)//
                .setNoSignificantWeather(true)//
                .build());
        changeForecasts.add(TAFChangeForecastImpl.builder().setPeriodOfChange(PartialOrCompleteTimePeriod.createValidityTimeDHDH("0100/0106"))//
                .setChangeIndicator(AviationCodeListUser.TAFChangeIndicator.FROM)//
                .setCeilingAndVisibilityOk(true)//
                .setNoSignificantWeather(true)//
                .build());
        changeForecasts.add(TAFChangeForecastImpl.builder().setPeriodOfChange(PartialOrCompleteTimePeriod.createValidityTimeDHDH("0102/0112"))//
                .setChangeIndicator(AviationCodeListUser.TAFChangeIndicator.FROM)//
                .setCeilingAndVisibilityOk(true)//
                .setNoSignificantWeather(true)//
                .build());

        final TAF msg = TAFImpl.builder()//
                .setChangeForecasts(changeForecasts)//
                .withCompleteForecastTimes(YearMonth.of(2017, Month.DECEMBER), 31, 18, ZoneId.of("Z"))//
                .buildPartial();

        assertTrue(msg.getChangeForecasts().isPresent());
        assertEquals(3, msg.getChangeForecasts().get().size());

        //Validity time of the 1st change forecast:
        TAFChangeForecast fct = msg.getChangeForecasts().get().get(0);
        PartialOrCompleteTimePeriod validityTime = fct.getPeriodOfChange();
        ZonedDateTime toMatch = ZonedDateTime.of(2017, 12, 31, 19, 0, 0, 0, ZoneId.of("Z"));
        assertTrue(validityTime.getStartTime().isPresent());
        assertFalse(validityTime.getStartTime().get().isMidnight24h());
        assertTrue(validityTime.getStartTime().get().getCompleteTime().isPresent());
        assertEquals(validityTime.getStartTime().get().getCompleteTime().get(), toMatch);

        toMatch = ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("Z"));
        assertTrue(validityTime.getEndTime().isPresent());
        assertTrue(validityTime.getEndTime().get().isMidnight24h());
        assertTrue(validityTime.getEndTime().get().getCompleteTime().isPresent());
        assertEquals(validityTime.getEndTime().get().getCompleteTime().get(), toMatch);

        //Validity time of the 2nd change forecast:
        fct = msg.getChangeForecasts().get().get(1);
        validityTime = fct.getPeriodOfChange();
        toMatch = ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("Z"));
        assertTrue(validityTime.getStartTime().isPresent());
        assertFalse(validityTime.getStartTime().get().isMidnight24h());
        assertTrue(validityTime.getStartTime().get().getCompleteTime().isPresent());
        assertEquals(validityTime.getStartTime().get().getCompleteTime().get(), toMatch);

        toMatch = ZonedDateTime.of(2018, 1, 1, 6, 0, 0, 0, ZoneId.of("Z"));
        assertTrue(validityTime.getEndTime().isPresent());
        assertFalse(validityTime.getEndTime().get().isMidnight24h());
        assertTrue(validityTime.getEndTime().get().getCompleteTime().isPresent());
        assertEquals(validityTime.getEndTime().get().getCompleteTime().get(), toMatch);

        //Validity time of the 3rd change forecast:
        fct = msg.getChangeForecasts().get().get(2);
        validityTime = fct.getPeriodOfChange();
        toMatch = ZonedDateTime.of(2018, 1, 1, 2, 0, 0, 0, ZoneId.of("Z"));
        assertTrue(validityTime.getStartTime().isPresent());
        assertFalse(validityTime.getStartTime().get().isMidnight24h());
        assertTrue(validityTime.getStartTime().get().getCompleteTime().isPresent());
        assertEquals(validityTime.getStartTime().get().getCompleteTime().get(), toMatch);

        toMatch = ZonedDateTime.of(2018, 1, 1, 12, 0, 0, 0, ZoneId.of("Z"));
        assertTrue(validityTime.getEndTime().isPresent());
        assertFalse(validityTime.getEndTime().get().isMidnight24h());
        assertTrue(validityTime.getEndTime().get().getCompleteTime().isPresent());
        assertEquals(validityTime.getEndTime().get().getCompleteTime().get(), toMatch);
    }

    @Test
    public void testCompleteTempFctTimes() {
        final List<TAFAirTemperatureForecast> temperatures = new ArrayList<>();
        temperatures.add(TAFAirTemperatureForecastImpl.builder()//
                .setMaxTemperatureTime(PartialOrCompleteTimeInstant.createDayHourInstant("3118"))//
                .setMaxTemperature(NumericMeasureImpl.builder().setUom("degC").setValue(2.0).build())//
                .setMinTemperatureTime(PartialOrCompleteTimeInstant.createDayHourInstant("0104"))//
                .setMinTemperature(NumericMeasureImpl.builder().setUom("degC").setValue(-1.0).build())//
                .build());

        final TAF msg = TAFImpl.builder()//
                .setAerodrome(AerodromeImpl.builder()//
                        .setDesignator("EKHF")//
                        .build())//
                .setTranslated(false)//
                .setStatus(AviationCodeListUser.TAFStatus.NORMAL)//
                .setBaseForecast(TAFBaseForecastImpl.builder()//
                        .setTemperatures(temperatures)//
                        .setCeilingAndVisibilityOk(true)//
                        .setPrevailingVisibility(NumericMeasureImpl.builder().setUom("m").setValue(8000.0).build())//
                        .setSurfaceWind(SurfaceWindImpl.builder()//
                                .setMeanWindDirection(NumericMeasureImpl.builder().setUom("deg").setValue(180.0).build())//
                                .setMeanWindSpeed(NumericMeasureImpl.builder().setUom("[kn_i]").setValue(15.0).build())//
                                .setVariableDirection(false)//
                                .build())//
                        .setNoSignificantWeather(true)//
                        .setCloud(CloudForecastImpl.builder()//
                                .setNoSignificantCloud(true)//
                                .build())//
                        .build())//
                .withCompleteForecastTimes(YearMonth.of(2017, Month.DECEMBER), 31, 18, ZoneId.of("Z"))//
                .buildPartial();

        assertTrue(msg.getBaseForecast().isPresent());
        assertTrue(msg.getBaseForecast().get().getTemperatures().isPresent());
        final List<TAFAirTemperatureForecast> fcts = msg.getBaseForecast().get().getTemperatures().get();
        assertEquals(1, fcts.size());
        final TAFAirTemperatureForecast fct = fcts.get(0);

        PartialOrCompleteTimeInstant t = fct.getMaxTemperatureTime();
        ZonedDateTime toMatch = ZonedDateTime.of(2017, 12, 31, 18, 0, 0, 0, ZoneId.of("Z"));
        assertFalse(t.isMidnight24h());
        assertTrue(t.getCompleteTime().isPresent());
        assertEquals(t.getCompleteTime().get(), toMatch);

        t = fct.getMinTemperatureTime();
        toMatch = ZonedDateTime.of(2018, 1, 1, 4, 0, 0, 0, ZoneId.of("Z"));
        assertFalse(t.isMidnight24h());
        assertTrue(t.getCompleteTime().isPresent());
        assertEquals(t.getCompleteTime().get(), toMatch);

    }

}
