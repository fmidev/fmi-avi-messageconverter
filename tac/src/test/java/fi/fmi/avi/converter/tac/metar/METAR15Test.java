package fi.fmi.avi.converter.tac.metar;

import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.AERODROME_DESIGNATOR;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.AIR_DEWPOINT_TEMPERATURE;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.AIR_PRESSURE_QNH;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.AUTOMATED;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.CLOUD;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.END_TOKEN;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.HORIZONTAL_VISIBILITY;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.ISSUE_TIME;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.METAR_START;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.SURFACE_WIND;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.VARIABLE_WIND_DIRECTION;
import static org.junit.Assert.assertEquals;

import java.util.List;

import fi.fmi.avi.converter.ConversionIssue;
import fi.fmi.avi.converter.ConversionResult.Status;
import fi.fmi.avi.model.metar.METAR;
import fi.fmi.avi.model.metar.impl.METARImpl;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionSpecification;
import fi.fmi.avi.converter.tac.AbstractAviMessageTest;
import fi.fmi.avi.converter.tac.lexer.Lexeme.Identity;

public class METAR15Test extends AbstractAviMessageTest<String, METAR> {

	@Override
	public String getJsonFilename() {
		return "fi/fmi/avi/converter/tac/metar/metar15.json";
	}
	
	@Override
	public String getMessage() {
		return
				"EFKK 091050Z AUTO 01009KT 340V040 9999 FEW012 BKN046 ///// Q////=";
	}
	
	@Override
	public String getCanonicalMessage() {
		return
				"EFKK 091050Z AUTO 01009KT 340V040 9999 FEW012 BKN046=";
	}
	
	@Override
	public String getTokenizedMessagePrefix() {
		return "METAR ";
	}
	
	@Override
    public ConversionHints getLexerParsingHints() {
        return ConversionHints.METAR;
    }

    @Override
    public ConversionHints getParserConversionHints() {
        return ConversionHints.METAR;
    }

	@Override
    public Status getExpectedParsingStatus() {
        return Status.WITH_ERRORS;
    }

    @Override
    public void assertParsingIssues(List<ConversionIssue> conversionIssues) {
        assertEquals(2, conversionIssues.size());

        ConversionIssue issue = conversionIssues.get(0);
        assertEquals(ConversionIssue.Type.MISSING_DATA, issue.getType());
        assertEquals("Missing air temperature and dewpoint temperature values in /////", issue.getMessage());

        issue = conversionIssues.get(1);
        assertEquals(ConversionIssue.Type.MISSING_DATA, issue.getType());
        assertEquals("Missing air pressure value: Q////", issue.getMessage());

    }

	@Override
	public Identity[] getLexerTokenSequenceIdentity() {
		return new Identity[] {
				METAR_START, AERODROME_DESIGNATOR, ISSUE_TIME, AUTOMATED, SURFACE_WIND, VARIABLE_WIND_DIRECTION,
                HORIZONTAL_VISIBILITY, CLOUD, CLOUD, AIR_DEWPOINT_TEMPERATURE, AIR_PRESSURE_QNH, END_TOKEN
		};
	}

	@Override
    public ConversionSpecification<String, METAR> getParsingSpecification() {
        return ConversionSpecification.TAC_TO_METAR_POJO;
    }
	
	@Override
    public ConversionSpecification<METAR, String> getSerializationSpecification() {
        return ConversionSpecification.METAR_POJO_TO_TAC;
    }

	@Override
    public Class<? extends METAR> getTokenizerImplmentationClass() {
        return METARImpl.class;
    }

}
