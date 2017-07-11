package fi.fmi.avi.data.metar.impl;

import static org.junit.Assert.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import fi.fmi.avi.data.metar.METAR;
import fi.fmi.avi.data.metar.TrendForecast;
import fi.fmi.avi.data.metar.TrendTimeGroups;

public class METARTimeReferencesTest {

	@Test
	public void testAmendTimeReferences() {
		METAR msg = new METARImpl();
		TrendForecast trend = new TrendForecastImpl();
		TrendTimeGroups timeGroups = new TrendTimeGroupsImpl();
		trend.setTimeGroups(timeGroups);
		List<TrendForecast> trends = new ArrayList<>();
		trends.add(trend);
		msg.setTrends(trends);
		
		assertNull(msg.getIssueTime());
		assertNull(msg.getTrends().get(0).getTimeGroups().getStartTime());
		assertNull(msg.getTrends().get(0).getTimeGroups().getEndTime());
		
		msg.setPartialIssueTime("201200Z");
		assertNull(msg.getIssueTime());
		
		msg.getTrends().get(0).getTimeGroups().setPartialStartTime("1830");
		msg.getTrends().get(0).getTimeGroups().setPartialEndTime("2400");
		assertNull(msg.getTrends().get(0).getTimeGroups().getStartTime());
		assertNull(msg.getTrends().get(0).getTimeGroups().getEndTime());
		
		assertFalse(msg.areTimeReferencesResolved());
		
		msg.amendTimeReferences(ZonedDateTime.of(2017, 12, 31, 23, 59, 59, 999, ZoneId.of("Z")));
	
		assertEquals(ZonedDateTime.of(2017, 12, 20, 12, 0, 0, 0, ZoneId.of("Z")), msg.getIssueTime());
		assertEquals(ZonedDateTime.of(2017, 12, 20, 18, 30, 0, 0, ZoneId.of("Z")), msg.getTrends().get(0).getTimeGroups().getStartTime());
		assertEquals(ZonedDateTime.of(2017, 12, 21, 0, 0, 0, 0, ZoneId.of("Z")), msg.getTrends().get(0).getTimeGroups().getEndTime());
		assertTrue(msg.areTimeReferencesResolved());
	}

}
