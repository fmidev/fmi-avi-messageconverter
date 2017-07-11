package fi.fmi.avi.data.taf.impl;

import static org.junit.Assert.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Test;

public class TAFAirTemperatureForecastImplTest {

	@Test
	public void testSetPartialMaxTimeWithAmend() {
		TAFAirTemperatureForecastImpl msg = new TAFAirTemperatureForecastImpl();
		assertNull(msg.getMaxTemperatureTime());
		assertNull(msg.getPartialMaxTemperatureTime());
		
		msg.setPartialMaxTemperatureTime("1013Z");
		assertNull(msg.getMaxTemperatureTime());
		assertEquals("1013Z", msg.getPartialMaxTemperatureTime());
		assertTrue(msg.getMaxTemperatureDayOfMonth() == 10);
		assertTrue(msg.getMaxTemperatureHour() == 13);
		
		msg.amendTimeReferences(ZonedDateTime.of(2017, 1, 20, 1, 16, 0, 0, ZoneId.of("Z")));
		assertEquals(ZonedDateTime.of(2017, 2, 10, 13, 0, 0, 0, ZoneId.of("Z")), msg.getMaxTemperatureTime());
		assertEquals("1013Z", msg.getPartialMaxTemperatureTime());
		
		msg.amendTimeReferences(ZonedDateTime.of(2017, 1, 10, 1, 16, 0, 0, ZoneId.of("Z")));
		assertEquals(ZonedDateTime.of(2017, 1, 10, 13, 0, 0, 0, ZoneId.of("Z")), msg.getMaxTemperatureTime());
		assertEquals("1013Z", msg.getPartialMaxTemperatureTime());
		
		msg.amendTimeReferences(ZonedDateTime.of(2017, 1, 9, 1, 16, 0, 0, ZoneId.of("Z")));
		assertEquals(ZonedDateTime.of(2017, 1, 10, 13, 0, 0, 0, ZoneId.of("Z")), msg.getMaxTemperatureTime());
		assertEquals("1013Z", msg.getPartialMaxTemperatureTime());
		
		
		msg.setPartialMaxTemperatureTime("13Z");
		assertNull(msg.getMaxTemperatureTime());
		assertEquals("13Z", msg.getPartialMaxTemperatureTime());
		assertTrue(msg.getMaxTemperatureDayOfMonth() == -1);
		assertTrue(msg.getMaxTemperatureHour() == 13);
		
		msg.amendTimeReferences(ZonedDateTime.of(2017, 1, 20, 1, 16, 0, 0, ZoneId.of("Z")));
		assertEquals(ZonedDateTime.of(2017, 1, 20, 13, 0, 0, 0, ZoneId.of("Z")), msg.getMaxTemperatureTime());
		assertEquals("13Z", msg.getPartialMaxTemperatureTime());
		
		msg.amendTimeReferences(ZonedDateTime.of(2017, 1, 10, 1, 16, 0, 0, ZoneId.of("Z")));
		assertEquals(ZonedDateTime.of(2017, 1, 10, 13, 0, 0, 0, ZoneId.of("Z")), msg.getMaxTemperatureTime());
		assertEquals("13Z", msg.getPartialMaxTemperatureTime());
		
		msg.amendTimeReferences(ZonedDateTime.of(2017, 1, 9, 1, 16, 0, 0, ZoneId.of("Z")));
		assertEquals(ZonedDateTime.of(2017, 1, 9, 13, 0, 0, 0, ZoneId.of("Z")), msg.getMaxTemperatureTime());
		assertEquals("13Z", msg.getPartialMaxTemperatureTime());
	}

}
