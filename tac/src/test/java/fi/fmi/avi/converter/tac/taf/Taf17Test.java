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
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.NO_SIGNIFICANT_WEATHER;


import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.impl.TAFImpl;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionSpecification;
import fi.fmi.avi.converter.tac.AbstractAviMessageTest;
import fi.fmi.avi.converter.tac.lexer.Lexeme.Identity;

public class Taf17Test extends AbstractAviMessageTest<String, TAF> {

	@Override
	public String getJsonFilename() {
		return "fi/fmi/avi/converter/tac/taf/taf17.json";
	}
	
	@Override
	public String getMessage() {
				return
						"TAF KOKC 051130Z 051212 14008KT 5SM BR BKN030 "
						+ "TEMPO 1316 1 1/2SM BR "
						+ "FM1600 16010KT P6SM SKC "
						+ "BECMG 2224 20013G20KT 4SM SHRA OVC020 "
						+ "PROB40 0006 2SM TSRA OVC008CB "
						+ "BECMG 0608 21015KT P6SM NSW SCT040=";
	}
	
	@Override
	public String getTokenizedMessagePrefix() {
		return "";
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
		return new Identity[] { TAF_START, AERODROME_DESIGNATOR, ISSUE_TIME, VALID_TIME, SURFACE_WIND, HORIZONTAL_VISIBILITY, WEATHER, CLOUD,
                FORECAST_CHANGE_INDICATOR, CHANGE_FORECAST_TIME_GROUP, HORIZONTAL_VISIBILITY, WEATHER,
                FORECAST_CHANGE_INDICATOR, SURFACE_WIND, HORIZONTAL_VISIBILITY, CLOUD,
                FORECAST_CHANGE_INDICATOR, CHANGE_FORECAST_TIME_GROUP, SURFACE_WIND,  HORIZONTAL_VISIBILITY, WEATHER, CLOUD,
                FORECAST_CHANGE_INDICATOR, CHANGE_FORECAST_TIME_GROUP, HORIZONTAL_VISIBILITY, WEATHER, CLOUD,
                FORECAST_CHANGE_INDICATOR, CHANGE_FORECAST_TIME_GROUP, SURFACE_WIND, HORIZONTAL_VISIBILITY, NO_SIGNIFICANT_WEATHER, CLOUD, END_TOKEN };
	}

	@Override
    public ConversionSpecification<String, TAF> getParsingSpecification() {
        return ConversionSpecification.TAC_TO_TAF_POJO;
    }
    
    @Override
    public ConversionSpecification<TAF, String> getSerializationSpecification() {
        return ConversionSpecification.TAF_POJO_TO_TAC;
    }


	@Override
	public Class<? extends TAF> getTokenizerImplmentationClass() {
		return TAFImpl.class;
	}

}
