package fi.fmi.avi.converter.tac.lexer.impl.token;

import static org.junit.Assert.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Test;

import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.impl.TAFImpl;

public class CalculateNumberOfHoursTest {

	@Test
	public void testSingleDay() {
		TAF msg = new TAFImpl();
		msg.setPartialValidityTimePeriod("0100/0106");
		int hours = ValidTime.calculateNumberOfHours(msg);
		assertEquals(6, hours);
	}

	@Test
	public void testNoHours() {
		TAF msg = new TAFImpl();
		msg.setPartialValidityTimePeriod("0100/0100");
		int hours = ValidTime.calculateNumberOfHours(msg);
		assertEquals(0, hours);
	}

	@Test
	public void testSpanDaySingleHour() {
		TAF msg = new TAFImpl();
		msg.setPartialValidityTimePeriod("0823/0900");
		int hours = ValidTime.calculateNumberOfHours(msg);
		assertEquals(1, hours);
	}
	

	@Test
	public void test24HourIllegalButUsedFormat() {
		TAF msg = new TAFImpl();
		msg.setPartialValidityTimePeriod("0800/0824");
		int hours = ValidTime.calculateNumberOfHours(msg);
		assertEquals(24, hours);
	}


	@Test
	public void testSpanDayMoreThan24Hours() {
		TAF msg = new TAFImpl();
		msg.setPartialValidityTimePeriod("0806/0912");
		int hours = ValidTime.calculateNumberOfHours(msg);
		assertEquals(30, hours);
	}
	
	@Test
	public void testSpanToNextMonthStartHoursMoreThanEndHours() {
		TAF msg = new TAFImpl();
		msg.setPartialValidityTimePeriod("3122/0108");
		int hours = ValidTime.calculateNumberOfHours(msg);
		assertEquals(10, hours);
	}

	
	@Test
	public void testSpanToNextMonthStartHoursLessThanEndHours_starts31st() {
		TAF msg = new TAFImpl();
		msg.setPartialValidityTimePeriod("3108/0122");
		int hours = ValidTime.calculateNumberOfHours(msg);
		assertEquals(38, hours);
	}
	
	@Test
	public void testSpanToNextMonthStartHoursLessThanEndHours_starts30th() {
		TAF msg = new TAFImpl();
		msg.setPartialValidityTimePeriod("3008/0122");
		int hours = ValidTime.calculateNumberOfHours(msg);
		assertEquals(38, hours);
	}
	
	@Test
	public void testSpanToNextMonthStartHoursLessThanEndHours_starts29th() {
		TAF msg = new TAFImpl();
		msg.setPartialValidityTimePeriod("2908/0122");
		int hours = ValidTime.calculateNumberOfHours(msg);
		assertEquals(38, hours);
	}
	
	@Test
	public void testSpanToNextMonthStartHoursLessThanEndHours_starts28th() {
		TAF msg = new TAFImpl();
		msg.setPartialValidityTimePeriod("2808/0122");
		int hours = ValidTime.calculateNumberOfHours(msg);
		assertEquals(38, hours);
	}
	

	@Test
	public void testSpanToNextMonthStartHoursLessThanEndHours_starts27th_illegal() {
		try {
			TAF msg = new TAFImpl();
			msg.setPartialValidityTimePeriod("2708/0122");
			int hours = ValidTime.calculateNumberOfHours(msg);
			fail("hours should not have been calculated "+hours);
		} catch(Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}
	
	@Test
	public void testFullTimeReferencesMakesIllegalOk() {
		TAF msg = new TAFImpl();
		msg.setPartialIssueTime("270030Z");
		msg.setPartialValidityTimePeriod("2708/0122");
		
		msg.amendTimeReferences(ZonedDateTime.of(2017, 2, 27, 0, 0, 0, 0, ZoneId.of("Z")));
		int hours = ValidTime.calculateNumberOfHours(msg);
		assertEquals(62, hours);
	}
	
	@Test
	public void testIllegalSpanTooLong() {
		try {
			TAF msg = new TAFImpl();
			msg.setPartialValidityTimePeriod("1522/0108");
			int hours = ValidTime.calculateNumberOfHours(msg);
			fail("hours should not have been calculated "+hours);
		} catch(Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}
	
}
