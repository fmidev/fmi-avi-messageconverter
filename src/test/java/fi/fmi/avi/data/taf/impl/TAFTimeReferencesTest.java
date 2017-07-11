package fi.fmi.avi.data.taf.impl;

import static org.junit.Assert.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import fi.fmi.avi.data.taf.TAF;
import fi.fmi.avi.data.taf.TAFAirTemperatureForecast;
import fi.fmi.avi.data.taf.TAFBaseForecast;
import fi.fmi.avi.data.taf.TAFChangeForecast;

public class TAFTimeReferencesTest {

	@Test
	public void testAmendTimeReferences() {
		TAF msg = new TAFImpl();
		TAFBaseForecast baseForecast = new TAFBaseForecastImpl();
		TAFAirTemperatureForecast tempFct = new TAFAirTemperatureForecastImpl();
		List<TAFAirTemperatureForecast> temperatures = new ArrayList<>();
		temperatures.add(tempFct);
		baseForecast.setTemperatures(temperatures);
		msg.setBaseForecast(baseForecast);
		List<TAFChangeForecast> changeForecasts = new ArrayList<>();
		TAFChangeForecast changeFct1 = new TAFChangeForecastImpl();
		TAFChangeForecast changeFct2 = new TAFChangeForecastImpl();
		changeForecasts.add(changeFct1);
		changeForecasts.add(changeFct2);
		msg.setChangeForecasts(changeForecasts);
		
		msg.setPartialIssueTime("311200Z");
		msg.setPartialValidityStartTime("3112");
		msg.setPartialValidityEndTime("0106");
		tempFct.setPartialMaxTemperatureTime("3118");
		tempFct.setPartialMinTemperatureTime("0104");
		changeFct1.setPartialValidityStartTime("3119");
		changeFct1.setPartialValidityEndTime("24");
		changeFct2.setPartialValidityStartTime("0100");
		changeFct2.setPartialValidityEndTime("0106");
		
		assertNull(msg.getIssueTime());
		assertNull(msg.getValidityStartTime());
		assertNull(msg.getValidityEndTime());
		assertNull(msg.getBaseForecast().getTemperatures().get(0).getMaxTemperatureTime());
		assertNull(msg.getBaseForecast().getTemperatures().get(0).getMinTemperatureTime());
		assertNull(msg.getChangeForecasts().get(0).getValidityStartTime());
		assertNull(msg.getChangeForecasts().get(0).getValidityEndTime());
		assertNull(msg.getChangeForecasts().get(1).getValidityStartTime());
		assertNull(msg.getChangeForecasts().get(1).getValidityEndTime());
		assertFalse(msg.areTimeReferencesResolved());
		
		msg.amendTimeReferences(ZonedDateTime.of(2017, 12, 1, 0, 0, 0, 0, ZoneId.of("Z")));
		assertEquals(ZonedDateTime.of(2017, 12, 31, 12, 0, 0, 0, ZoneId.of("Z")), msg.getIssueTime());
		assertEquals(ZonedDateTime.of(2017, 12, 31, 12, 0, 0, 0, ZoneId.of("Z")), msg.getValidityStartTime());
		assertEquals(ZonedDateTime.of(2018, 1, 1, 6, 0, 0, 0, ZoneId.of("Z")), msg.getValidityEndTime());
		assertEquals(ZonedDateTime.of(2017, 12, 31, 18, 0, 0, 0, ZoneId.of("Z")), msg.getBaseForecast().getTemperatures().get(0).getMaxTemperatureTime());
		assertEquals(ZonedDateTime.of(2018, 1, 1, 4, 0, 0, 0, ZoneId.of("Z")), msg.getBaseForecast().getTemperatures().get(0).getMinTemperatureTime());
		assertEquals(ZonedDateTime.of(2017, 12, 31, 19, 0, 0, 0, ZoneId.of("Z")), msg.getChangeForecasts().get(0).getValidityStartTime());
		assertEquals(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("Z")), msg.getChangeForecasts().get(0).getValidityEndTime());
		assertEquals(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("Z")), msg.getChangeForecasts().get(1).getValidityStartTime());
		assertEquals(ZonedDateTime.of(2018, 1, 1, 6, 0, 0, 0, ZoneId.of("Z")), msg.getChangeForecasts().get(1).getValidityEndTime());
		assertTrue(msg.areTimeReferencesResolved());
		
		
	}

}
