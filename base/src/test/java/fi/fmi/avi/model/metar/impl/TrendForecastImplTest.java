package fi.fmi.avi.model.metar.impl;

import static org.junit.Assert.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Test;

public class TrendForecastImplTest {

	@Test
	public void testSetPartialStartEndTime() {
		TrendForecastImpl fct = new TrendForecastImpl();
		TrendTimeGroupsImpl timeGroups = new TrendTimeGroupsImpl();
		fct.setTimeGroups(timeGroups);
		
		assertNull(timeGroups.getStartTime());
		assertNull(timeGroups.getEndTime());
		assertNull(timeGroups.getPartialStartTime());
		assertNull(timeGroups.getPartialEndTime());
		assertFalse(timeGroups.hasStartTime());
		assertFalse(timeGroups.hasEndTime());
		
		timeGroups.setPartialStartTime("1200");
		assertTrue(timeGroups.hasStartTime());
		assertEquals("1200",timeGroups.getPartialStartTime());
		assertNull(timeGroups.getStartTime());
		
		timeGroups.setPartialEndTime("1530");
		assertTrue(timeGroups.hasEndTime());
		assertEquals("1530",timeGroups.getPartialEndTime());
		assertNull(timeGroups.getEndTime());
		
		timeGroups.setPartialEndTime("2400");
		assertTrue(timeGroups.hasEndTime());
		assertEquals("2400",timeGroups.getPartialEndTime());
		assertNull(timeGroups.getEndTime());
		
		timeGroups.amendTimeReferences(ZonedDateTime.of(2017, 1, 31, 12, 0, 0, 0, ZoneId.of("Z")));
		assertTrue(timeGroups.hasEndTime());
		assertTrue(timeGroups.hasStartTime());
		assertEquals(ZonedDateTime.of(2017, 2, 1, 0, 0, 0, 0, ZoneId.of("Z")), timeGroups.getEndTime());
		assertEquals(ZonedDateTime.of(2017, 1, 31, 12, 0, 0, 0, ZoneId.of("Z")), timeGroups.getStartTime());
		
		timeGroups.amendTimeReferences(ZonedDateTime.of(2018, 5, 31, 12, 0, 0, 0, ZoneId.of("Z")));
		assertEquals(ZonedDateTime.of(2018, 6, 1, 0, 0, 0, 0, ZoneId.of("Z")), timeGroups.getEndTime());
		assertEquals(ZonedDateTime.of(2018, 5, 31, 12, 0, 0, 0, ZoneId.of("Z")), timeGroups.getStartTime());
		
	}
	
	

}
