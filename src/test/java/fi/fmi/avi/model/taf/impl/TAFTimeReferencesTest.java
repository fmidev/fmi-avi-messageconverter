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

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.immutable.AerodromeImpl;
import fi.fmi.avi.model.immutable.CloudForecastImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.TAFAirTemperatureForecast;
import fi.fmi.avi.model.taf.TAFChangeForecast;
import fi.fmi.avi.model.taf.immutable.TAFAirTemperatureForecastImpl;
import fi.fmi.avi.model.taf.immutable.TAFBaseForecastImpl;
import fi.fmi.avi.model.taf.immutable.TAFChangeForecastImpl;
import fi.fmi.avi.model.taf.immutable.TAFImpl;
import fi.fmi.avi.model.taf.immutable.TAFSurfaceWindImpl;

public class TAFTimeReferencesTest {

    @Test
    public void testIssueTimeCompletion() {
        TAF msg = new TAFImpl.Builder()//
                .setIssueTime(PartialOrCompleteTimeInstant.createIssueTime("201004Z"))//
                .withCompleteIssueTime(YearMonth.of(2017, Month.DECEMBER))//
                .buildPartial();

        assertTrue(msg.getIssueTime().getPartialTime().isPresent());
        assertTrue(msg.getIssueTime().getPartialTime().get().equals("201004Z"));
        PartialOrCompleteTimeInstant it = msg.getIssueTime();

        ZonedDateTime toMatch = ZonedDateTime.of(2017, 12, 20, 10, 4, 0, 0, ZoneId.of("Z"));
        assertFalse(it.isMidnight24h());
        assertTrue(it.getCompleteTime().isPresent());
        assertTrue(it.getCompleteTime().get().equals(toMatch));

    }

    @Test
    public void testCompleteMessageValidTime() {
        TAF msg = new TAFImpl.Builder()//
                .setValidityTime(PartialOrCompleteTimePeriod.createValidityTimeDHDH("3118/0118"))//
                .withCompleteForecastTimes(YearMonth.of(2017, Month.DECEMBER), 31, 18, ZoneId.of("Z"))//
                .buildPartial();

        ZonedDateTime toMatch = ZonedDateTime.of(2017, 12, 31, 18, 0, 0, 0, ZoneId.of("Z"));

        //Validity time of the entire TAF:
        assertTrue(msg.getValidityTime().isPresent());
        PartialOrCompleteTimePeriod validityTime = msg.getValidityTime().get();

        assertTrue(validityTime.getStartTime().isPresent());
        assertFalse(validityTime.getStartTime().get().isMidnight24h());
        assertTrue(validityTime.getStartTime().get().getCompleteTime().isPresent());
        assertTrue(validityTime.getStartTime().get().getCompleteTime().get().equals(toMatch));

        toMatch = ZonedDateTime.of(2018, 1, 1, 18, 0, 0, 0, ZoneId.of("Z"));
        assertTrue(validityTime.getEndTime().isPresent());
        assertFalse(validityTime.getEndTime().get().isMidnight24h());
        assertTrue(validityTime.getEndTime().get().getCompleteTime().isPresent());
        assertTrue(validityTime.getEndTime().get().getCompleteTime().get().equals(toMatch));
    }

    @Test
    public void testCompleteChangeFctValidTimes() {
        List<TAFChangeForecast> changeForecasts = new ArrayList<>();
        changeForecasts.add(new TAFChangeForecastImpl.Builder().setPeriodOfChange(PartialOrCompleteTimePeriod.createValidityTimeDHDH("3119/3124"))//
                .setChangeIndicator(AviationCodeListUser.TAFChangeIndicator.FROM)//
                .setCeilingAndVisibilityOk(true)//
                .setNoSignificantWeather(true)//
                .build());
        changeForecasts.add(new TAFChangeForecastImpl.Builder().setPeriodOfChange(PartialOrCompleteTimePeriod.createValidityTimeDHDH("0100/0106"))//
                .setChangeIndicator(AviationCodeListUser.TAFChangeIndicator.FROM)//
                .setCeilingAndVisibilityOk(true)//
                .setNoSignificantWeather(true)//
                .build());
        changeForecasts.add(new TAFChangeForecastImpl.Builder().setPeriodOfChange(PartialOrCompleteTimePeriod.createValidityTimeDHDH("0102/0112"))//
                .setChangeIndicator(AviationCodeListUser.TAFChangeIndicator.FROM)//
                .setCeilingAndVisibilityOk(true)//
                .setNoSignificantWeather(true)//
                .build());

        TAF msg = new TAFImpl.Builder()//
                .setChangeForecasts(changeForecasts)//
                .withCompleteForecastTimes(YearMonth.of(2017, Month.DECEMBER), 31, 18, ZoneId.of("Z"))//
                .buildPartial();

        assertTrue(msg.getChangeForecasts().isPresent());
        assertTrue(msg.getChangeForecasts().get().size() == 3);

        //Validity time of the 1st change forecast:
        TAFChangeForecast fct = msg.getChangeForecasts().get().get(0);
        PartialOrCompleteTimePeriod validityTime = fct.getPeriodOfChange();
        ZonedDateTime toMatch = ZonedDateTime.of(2017, 12, 31, 19, 0, 0, 0, ZoneId.of("Z"));
        assertTrue(validityTime.getStartTime().isPresent());
        assertFalse(validityTime.getStartTime().get().isMidnight24h());
        assertTrue(validityTime.getStartTime().get().getCompleteTime().isPresent());
        assertTrue(validityTime.getStartTime().get().getCompleteTime().get().equals(toMatch));

        toMatch = ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("Z"));
        assertTrue(validityTime.getEndTime().isPresent());
        assertTrue(validityTime.getEndTime().get().isMidnight24h());
        assertTrue(validityTime.getEndTime().get().getCompleteTime().isPresent());
        assertTrue(validityTime.getEndTime().get().getCompleteTime().get().equals(toMatch));

        //Validity time of the 2nd change forecast:
        fct = msg.getChangeForecasts().get().get(1);
        validityTime = fct.getPeriodOfChange();
        toMatch = ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("Z"));
        assertTrue(validityTime.getStartTime().isPresent());
        assertFalse(validityTime.getStartTime().get().isMidnight24h());
        assertTrue(validityTime.getStartTime().get().getCompleteTime().isPresent());
        assertTrue(validityTime.getStartTime().get().getCompleteTime().get().equals(toMatch));

        toMatch = ZonedDateTime.of(2018, 1, 1, 6, 0, 0, 0, ZoneId.of("Z"));
        assertTrue(validityTime.getEndTime().isPresent());
        assertFalse(validityTime.getEndTime().get().isMidnight24h());
        assertTrue(validityTime.getEndTime().get().getCompleteTime().isPresent());
        assertTrue(validityTime.getEndTime().get().getCompleteTime().get().equals(toMatch));

        //Validity time of the 3rd change forecast:
        fct = msg.getChangeForecasts().get().get(2);
        validityTime = fct.getPeriodOfChange();
        toMatch = ZonedDateTime.of(2018, 1, 1, 2, 0, 0, 0, ZoneId.of("Z"));
        assertTrue(validityTime.getStartTime().isPresent());
        assertFalse(validityTime.getStartTime().get().isMidnight24h());
        assertTrue(validityTime.getStartTime().get().getCompleteTime().isPresent());
        assertTrue(validityTime.getStartTime().get().getCompleteTime().get().equals(toMatch));

        toMatch = ZonedDateTime.of(2018, 1, 1, 12, 0, 0, 0, ZoneId.of("Z"));
        assertTrue(validityTime.getEndTime().isPresent());
        assertFalse(validityTime.getEndTime().get().isMidnight24h());
        assertTrue(validityTime.getEndTime().get().getCompleteTime().isPresent());
        assertTrue(validityTime.getEndTime().get().getCompleteTime().get().equals(toMatch));
    }

    @Test
    public void testCompleteTempFctTimes() {
        List<TAFAirTemperatureForecast> temperatures = new ArrayList<>();
        temperatures.add(new TAFAirTemperatureForecastImpl.Builder()//
                .setMaxTemperatureTime(PartialOrCompleteTimeInstant.createDayHourInstant("3118"))//
                .setMaxTemperature(new NumericMeasureImpl.Builder().setUom("degC").setValue(2.0).build())//
                .setMinTemperatureTime(PartialOrCompleteTimeInstant.createDayHourInstant("0104"))//
                .setMinTemperature(new NumericMeasureImpl.Builder().setUom("degC").setValue(-1.0).build())//
                .build());

        TAF msg = new TAFImpl.Builder()//
                .setAerodrome(new AerodromeImpl.Builder()//
                        .setDesignator("EKHF")//
                        .build())//
                .setTranslated(false)//
                .setStatus(AviationCodeListUser.TAFStatus.NORMAL)//
                .setBaseForecast(new TAFBaseForecastImpl.Builder()//
                        .setTemperatures(temperatures)//
                        .setCeilingAndVisibilityOk(true)//
                        .setPrevailingVisibility(new NumericMeasureImpl.Builder().setUom("m").setValue(8000.0).build())//
                        .setSurfaceWind(new TAFSurfaceWindImpl.Builder()//
                                .setMeanWindDirection(new NumericMeasureImpl.Builder().setUom("deg").setValue(180.0).build())//
                                .setMeanWindSpeed(new NumericMeasureImpl.Builder().setUom("[kn_i]").setValue(15.0).build())//
                                .setVariableDirection(false)//
                                .build())//
                        .setNoSignificantWeather(true)//
                        .setCloud(new CloudForecastImpl.Builder()//
                                .setNoSignificantCloud(true)//
                                .build())//
                        .build())//
                .withCompleteForecastTimes(YearMonth.of(2017, Month.DECEMBER), 31, 18, ZoneId.of("Z"))//
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

        t = fct.getMinTemperatureTime();
        toMatch = ZonedDateTime.of(2018, 1, 1, 4, 0, 0, 0, ZoneId.of("Z"));
        assertFalse(t.isMidnight24h());
        assertTrue(t.getCompleteTime().isPresent());
        assertTrue(t.getCompleteTime().get().equals(toMatch));

    }

}
