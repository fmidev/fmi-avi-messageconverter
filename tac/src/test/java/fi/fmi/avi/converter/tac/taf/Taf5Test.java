package fi.fmi.avi.converter.tac.taf;

import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.AERODROME_DESIGNATOR;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.CHANGE_FORECAST_TIME_GROUP;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.CLOUD;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.END_TOKEN;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.FORECAST_CHANGE_INDICATOR;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.HORIZONTAL_VISIBILITY;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.ISSUE_TIME;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.SURFACE_WIND;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.TAF_START;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.VALID_TIME;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.WEATHER;

import fi.fmi.avi.converter.tac.conf.TACConverter;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.impl.TAFImpl;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionSpecification;
import fi.fmi.avi.converter.tac.AbstractAviMessageTest;
import fi.fmi.avi.converter.tac.lexer.Lexeme.Identity;

public class Taf5Test extends AbstractAviMessageTest<String, TAF> {

	@Override
	public String getJsonFilename() {
		return "taf/taf5.json";
	}
	
	@Override
	public String getMessage() {
		return
				"ENOA 301100Z 3012/3112 29028KT 9999 -SHRA FEW015TCU SCT025 " + 
				"TEMPO 3012/3024 4000 SHRAGS BKN012CB " + 
				"BECMG 3017/3020 25018KT " +
                "BECMG 3100/3103 17008KT " + 
				"BECMG 3107/3110 23015KT=";
	}
	
	@Override
	public String getTokenizedMessagePrefix() {
		return "TAF ";
	}
	
	@Override
	public ConversionHints getLexerParsingHints() {
		return ConversionHints.TAF;
	}

	@Override
	public ConversionHints getParserConversionHints() {
		return ConversionHints.TAF;
	}

	@Override
	public Identity[] getLexerTokenSequenceIdentity() {
		return new Identity[] {
				TAF_START, AERODROME_DESIGNATOR, ISSUE_TIME, VALID_TIME, SURFACE_WIND, HORIZONTAL_VISIBILITY, WEATHER, CLOUD,
                CLOUD, FORECAST_CHANGE_INDICATOR, CHANGE_FORECAST_TIME_GROUP, HORIZONTAL_VISIBILITY, WEATHER, CLOUD, FORECAST_CHANGE_INDICATOR,
                CHANGE_FORECAST_TIME_GROUP, SURFACE_WIND, FORECAST_CHANGE_INDICATOR, CHANGE_FORECAST_TIME_GROUP, SURFACE_WIND, FORECAST_CHANGE_INDICATOR,
                CHANGE_FORECAST_TIME_GROUP, SURFACE_WIND, END_TOKEN
		};
	}

	@Override
    public ConversionSpecification<String, TAF> getParsingSpecification() {
        return TACConverter.TAC_TO_TAF_POJO;
    }
    
    @Override
    public ConversionSpecification<TAF, String> getSerializationSpecification() {
        return TACConverter.TAF_POJO_TO_TAC;
    }


	@Override
	public Class<? extends TAF> getTokenizerImplmentationClass() {
		return TAFImpl.class;
	}

}
