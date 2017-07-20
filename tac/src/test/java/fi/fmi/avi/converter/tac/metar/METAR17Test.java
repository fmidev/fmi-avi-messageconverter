package fi.fmi.avi.converter.tac.metar;

import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.AERODROME_DESIGNATOR;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.AIR_DEWPOINT_TEMPERATURE;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.AIR_PRESSURE_QNH;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.CLOUD;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.END_TOKEN;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.HORIZONTAL_VISIBILITY;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.ISSUE_TIME;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.METAR_START;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.SURFACE_WIND;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.WEATHER;
import static org.junit.Assert.assertEquals;

import java.util.List;

import fi.fmi.avi.converter.ConversionIssue;
import fi.fmi.avi.converter.ConversionResult.Status;
import fi.fmi.avi.converter.tac.conf.TACConverter;
import fi.fmi.avi.model.metar.METAR;
import fi.fmi.avi.model.metar.impl.METARImpl;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionSpecification;
import fi.fmi.avi.converter.tac.AbstractAviMessageTest;
import fi.fmi.avi.converter.tac.lexer.Lexeme.Identity;

public class METAR17Test extends AbstractAviMessageTest<String, METAR> {

	@Override
	public String getJsonFilename() {
		return "metar/metar17.json";
	}
	
	@Override
	public String getMessage() {
		return "METAR KORD 201004Z 05008KT 1 1/4SM -DZ BR OVC006 03/03 04/54 A2964=";
	}
	
	@Override
	public String getCanonicalMessage() {
		return
				"METAR KORD 201004Z 05008KT 1 1/4SM -DZ BR OVC006 03/03 A2964=";
	}
	
	@Override
	public String getTokenizedMessagePrefix() {
		return "";
	}

	@Override
	public ConversionHints getLexerParsingHints() {
		return ConversionHints.METAR;
	}

	@Override
	public Status getExpectedParsingStatus() {
		return Status.WITH_ERRORS;
	}

	@Override
	public void assertParsingIssues(List<ConversionIssue> conversionIssues) {
		assertEquals(1, conversionIssues.size());
		ConversionIssue issue = conversionIssues.get(0);

		assertEquals(ConversionIssue.Type.SYNTAX_ERROR, issue.getType());
		assertEquals("More than one of AIR_DEWPOINT_TEMPERATURE in " + getMessage(), issue.getMessage());
	}

	@Override
	public Identity[] getLexerTokenSequenceIdentity() {
		return new Identity[] {
				METAR_START, AERODROME_DESIGNATOR, ISSUE_TIME, SURFACE_WIND, HORIZONTAL_VISIBILITY, WEATHER, WEATHER, CLOUD,
                AIR_DEWPOINT_TEMPERATURE, AIR_DEWPOINT_TEMPERATURE, AIR_PRESSURE_QNH, END_TOKEN
		};
	}

	@Override
    public ConversionSpecification<String, METAR> getParsingSpecification() {
        return TACConverter.TAC_TO_METAR_POJO;
    }
	
	@Override
    public ConversionSpecification<METAR, String> getSerializationSpecification() {
        return TACConverter.METAR_POJO_TO_TAC;
    }

	@Override
    public Class<? extends METAR> getTokenizerImplmentationClass() {
        return METARImpl.class;
    }

}
