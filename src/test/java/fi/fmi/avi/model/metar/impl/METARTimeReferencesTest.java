package fi.fmi.avi.model.metar.impl;

import org.junit.Test;

public class METARTimeReferencesTest {

	@Test
	public void testTrendTimeCompletion() {
        /*
        METAR msg = new METARImpl();
		TrendForecast trend = new TrendForecastImpl();
		TrendTimeGroups timeGroups = new TrendTimeGroupsImpl();
		trend.setTimeGroups(timeGroups);
		List<TrendForecast> trends = new ArrayList<>();
		trends.add(trend);
		msg.setTrends(trends);

		assertNull(msg.getTrends().get(0).getTimeGroups().getPartialStartTime());
		assertNull(msg.getTrends().get(0).getTimeGroups().getPartialEndTime());
		
		msg.getTrends().get(0).getTimeGroups().setPartialStartTime("1830");
		msg.getTrends().get(0).getTimeGroups().setPartialEndTime("2400");
		assertNull(msg.getTrends().get(0).getTimeGroups().getCompleteStartTime());
		assertNull(msg.getTrends().get(0).getTimeGroups().getCompleteEndTime());
		
		assertFalse(msg.areTrendTimeReferencesComplete());
		
		msg.completeTrendTimeReferences(2017,12,31, 23, ZoneId.of("Z"));
	
		assertNull(msg.getIssueTime());

		assertEquals(ZonedDateTime.of(2018, 1, 1, 18, 30, 0, 0, ZoneId.of("Z")), msg.getTrends().get(0).getTimeGroups().getCompleteStartTime());
		assertEquals(ZonedDateTime.of(2018, 1, 2, 0, 0, 0, 0, ZoneId.of("Z")), msg.getTrends().get(0).getTimeGroups().getCompleteEndTime());
		assertTrue(msg.areTrendTimeReferencesComplete());
		*/
		
	}

	@Test
	public void testSetTrendPartialStartEndTime() {
        /*
        METAR msg = new METARImpl();
		TrendForecast trend = new TrendForecastImpl();
		TrendTimeGroups timeGroups = new TrendTimeGroupsImpl();
		trend.setTimeGroups(timeGroups);
		List<TrendForecast> trends = new ArrayList<>();
		trends.add(trend);
		msg.setTrends(trends);

		assertNull(timeGroups.getCompleteStartTime());
		assertNull(timeGroups.getCompleteEndTime());
		assertNull(timeGroups.getPartialStartTime());
		assertNull(timeGroups.getPartialEndTime());
		assertFalse(timeGroups.hasStartTime());
		assertFalse(timeGroups.hasEndTime());

		timeGroups.setPartialStartTime("1200");
		assertTrue(timeGroups.hasStartTime());
		assertEquals("1200",timeGroups.getPartialStartTime());
		assertNull(timeGroups.getCompleteStartTime());

		timeGroups.setPartialEndTime("1530");
		assertTrue(timeGroups.hasEndTime());
		assertEquals("1530",timeGroups.getPartialEndTime());
		assertNull(timeGroups.getCompleteEndTime());

		timeGroups.setPartialEndTime("2400");
		assertTrue(timeGroups.hasEndTime());
		assertEquals("2400",timeGroups.getPartialEndTime());
		assertNull(timeGroups.getCompleteEndTime());

		msg.completeTrendTimeReferences(2017, 1, 31, 23, ZoneId.of("Z"));
		assertTrue(timeGroups.hasEndTime());
		assertTrue(timeGroups.hasStartTime());

		assertEquals(ZonedDateTime.of(2017, 2, 1, 12, 0, 0, 0, ZoneId.of("Z")), timeGroups.getCompleteStartTime());
		assertEquals(ZonedDateTime.of(2017, 2, 2, 0, 0, 0, 0, ZoneId.of("Z")), timeGroups.getCompleteEndTime());

		msg.uncompleteTrendTimeReferences();
		assertNull(timeGroups.getCompleteStartTime());
		assertNull(timeGroups.getCompleteEndTime());
		assertFalse(timeGroups.isStartTimeComplete());
		assertFalse(timeGroups.isEndTimeComplete());

		msg.completeTrendTimeReferences(2018, 5, 31, 12, ZoneId.of("Z"));

		assertEquals(ZonedDateTime.of(2018, 5, 31, 12, 0, 0, 0, ZoneId.of("Z")), timeGroups.getCompleteStartTime());
		assertEquals(ZonedDateTime.of(2018, 6, 1, 0, 0, 0, 0, ZoneId.of("Z")), timeGroups.getCompleteEndTime());
*/
	}

}
