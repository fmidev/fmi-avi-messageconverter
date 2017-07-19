package fi.fmi.avi.converter.tac.lexer.impl.token;

import static org.junit.Assert.*;

import java.util.regex.Matcher;

import org.junit.Before;
import org.junit.Test;

import fi.fmi.avi.converter.tac.lexer.impl.PrioritizedLexemeVisitor.Priority;

public class WindShearMatchingTest {
	WindShear windShear;
	
	@Before
	public void setUp() throws Exception {
		windShear = new WindShear(Priority.NORMAL);
	}

	@Test
	public void testWS_ALL_RWY() {
		Matcher m = windShear.getPattern().matcher("WS ALL RWY");
		
		assertTrue(m.matches());
	}

	@Test
	public void testWS_RWY03() {
		Matcher m = windShear.getPattern().matcher("WS RWY03");
		
		assertTrue(m.matches());
		assertEquals("03", m.group(2));
	}

	
	@Test
	public void testWS_RWY04R() {
		Matcher m = windShear.getPattern().matcher("WS RWY04R");
		
		assertTrue(m.matches());
		assertEquals("04R", m.group(2));
	}

	@Test
	public void testWS_R04R() {
		Matcher m = windShear.getPattern().matcher("WS R04R");
		
		assertTrue(m.matches());
		assertEquals("04R", m.group(2));
	}
	

	@Test
	public void testWS_R18C() {
		Matcher m = windShear.getPattern().matcher("WS R18C");
		
		assertTrue(m.matches());
		assertEquals("18C", m.group(2));
	}
	
}
