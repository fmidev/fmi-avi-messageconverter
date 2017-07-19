package fi.fmi.avi.converter.tac.lexer.impl.token;

import static org.junit.Assert.*;

import java.util.regex.Matcher;

import org.junit.Before;
import org.junit.Test;

import fi.fmi.avi.converter.tac.lexer.impl.PrioritizedLexemeVisitor.Priority;

public class RunwayStateMatchingTest {
	RunwayState runwayState;
	
	@Before
	public void setUp() throws Exception {
		runwayState = new RunwayState(Priority.NORMAL);
	}

	@Test
	public void test16th_ed_99421594() {
		Matcher m = runwayState.getPattern().matcher("99421594");
		
		assertTrue(m.matches());
		assertEquals("99", runwayState.getRunwayDesignationMatch(m));
		assertEquals("4", m.group(RunwayState.MATCH_DEPOSIT_CODE));
		assertEquals("2", m.group(RunwayState.MATCH_CONTAMINATION_CODE));
		assertEquals("15", m.group(RunwayState.MATCH_DEPTH_CODE));
		assertNull(m.group(RunwayState.MATCH_CLEARED));
		assertEquals("94", m.group(RunwayState.MATCH_FRICTION_OR_BREAKING_CODE));
	}
	
	// This case is a mix between 16th and 19th, where the runway designator is
	// not surrounded with R../, but the designator has a R in the end
	@Test
	public void test16th_ed_15Rslashes() {
		Matcher m = runwayState.getPattern().matcher("15R//////");
		
		assertTrue(m.matches());
		assertEquals("15R", runwayState.getRunwayDesignationMatch(m));
		assertEquals("/", m.group(RunwayState.MATCH_DEPOSIT_CODE));
		assertEquals("/", m.group(RunwayState.MATCH_CONTAMINATION_CODE));
		assertEquals("//", m.group(RunwayState.MATCH_DEPTH_CODE));
		assertNull(m.group(RunwayState.MATCH_CLEARED));
		assertEquals("//", m.group(RunwayState.MATCH_FRICTION_OR_BREAKING_CODE));
	}
	
	
	@Test
	public void test16th_ed_14CLRD__() {
		Matcher m = runwayState.getPattern().matcher("14CLRD//");
		
		assertTrue(m.matches());
		assertEquals("14", runwayState.getRunwayDesignationMatch(m));
		assertNull( m.group(RunwayState.MATCH_DEPOSIT_CODE));
		assertNull( m.group(RunwayState.MATCH_CONTAMINATION_CODE));
		assertNull(m.group(RunwayState.MATCH_DEPTH_CODE));
		assertEquals("CLRD", m.group(RunwayState.MATCH_CLEARED));
		assertEquals("//", m.group(RunwayState.MATCH_FRICTION_OR_BREAKING_CODE));
	}

	@Test
	public void test19th_ed_R99_421594() {
		Matcher m = runwayState.getPattern().matcher("R99/421594");
		
		assertTrue(m.matches());
		assertEquals("99", runwayState.getRunwayDesignationMatch(m));
		assertEquals("4", m.group(RunwayState.MATCH_DEPOSIT_CODE));
		assertEquals("2", m.group(RunwayState.MATCH_CONTAMINATION_CODE));
		assertEquals("15", m.group(RunwayState.MATCH_DEPTH_CODE));
		assertNull(m.group(RunwayState.MATCH_CLEARED));
		assertEquals("94", m.group(RunwayState.MATCH_FRICTION_OR_BREAKING_CODE));
	}



	@Test
	public void test19th_ed_R14L_CLRD__() {
		Matcher m = runwayState.getPattern().matcher("R14L/CLRD//");
		
		assertTrue(m.matches());
		assertEquals("14L", runwayState.getRunwayDesignationMatch(m));
		assertNull(m.group(RunwayState.MATCH_DEPOSIT_CODE));
		assertNull(m.group(RunwayState.MATCH_CONTAMINATION_CODE));
		assertNull(m.group(RunwayState.MATCH_DEPTH_CODE));
		assertEquals("CLRD", m.group(RunwayState.MATCH_CLEARED));
		assertEquals("//", m.group(RunwayState.MATCH_FRICTION_OR_BREAKING_CODE));
	}

	
}
