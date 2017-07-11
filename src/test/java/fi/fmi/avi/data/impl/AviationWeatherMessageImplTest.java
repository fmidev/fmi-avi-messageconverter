package fi.fmi.avi.data.impl;

import static org.junit.Assert.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Test;

public class AviationWeatherMessageImplTest {

	@Test
	public void testSetPartialIssueTimeWithAmend() {
		SimpleMessage msg = new SimpleMessage();
		assertNull(msg.getIssueTime());
		assertNull(msg.getPartialIssueTime());
		
		msg.setPartialIssueTime("200116Z");
		assertNull(msg.getIssueTime());
		assertEquals("200116Z", msg.getPartialIssueTime());
		
		msg.amendTimeReferences(ZonedDateTime.of(2017, 1, 1, 0, 0, 0, 0, ZoneId.of("Z")));
		assertEquals(ZonedDateTime.of(2017, 1, 20, 1, 16, 0, 0, ZoneId.of("Z")), msg.getIssueTime());
		assertEquals("200116Z", msg.getPartialIssueTime());
		
		msg.amendTimeReferences(ZonedDateTime.of(2016, 3, 1, 0, 0, 0, 0, ZoneId.of("Z")));
		assertEquals(ZonedDateTime.of(2016, 3, 20, 1, 16, 0, 0, ZoneId.of("Z")), msg.getIssueTime());
		assertEquals("200116Z", msg.getPartialIssueTime());
		
	}

	@Test
	public void testSetIssueTimeZonedDateTime() {
		SimpleMessage msg = new SimpleMessage();
		assertNull(msg.getIssueTime());
		assertNull(msg.getPartialIssueTime());
		
		msg.setIssueTime(ZonedDateTime.of(2017, 1, 20, 1, 16, 0, 0, ZoneId.of("Z")));
		assertEquals(ZonedDateTime.of(2017, 1, 20, 1, 16, 0, 0, ZoneId.of("Z")), msg.getIssueTime());
		assertEquals("200116Z", msg.getPartialIssueTime());
		
	}

	
	@Test(expected=IllegalArgumentException.class)
	public void testInvalidAmend() {
		SimpleMessage msg = new SimpleMessage();
		msg.setPartialIssueTime("291200Z");
		msg.amendTimeReferences(ZonedDateTime.of(2017, 2, 1, 0, 0, 0, 0, ZoneId.of("Z")));
	}

	static class SimpleMessage extends AviationWeatherMessageImpl {
		SimpleMessage(){
			
		}
	}
}
