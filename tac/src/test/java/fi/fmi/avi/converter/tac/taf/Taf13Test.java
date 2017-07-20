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
import fi.fmi.avi.converter.ConversionSpecification;
import fi.fmi.avi.converter.tac.AbstractAviMessageTest;
import fi.fmi.avi.converter.tac.lexer.Lexeme.Identity;

public class Taf13Test extends AbstractAviMessageTest<String, TAF> {

	@Override
	public String getJsonFilename() {
		return "taf/taf13.json";
	}
	
	@Override
	public String getMessage() {
		return
				"TAF EFHK 011733Z 0118/0218 VRB02KT 4000 -SN BKN003 " +
	    		"TEMPO 0118/0120 1500 SN " + 
				"BECMG 0120/0122 1500 BR " +
	    		"PROB40 TEMPO 0122/0203 0700 FG " + 
				"BECMG 0204/0206 21010KT 5000 BKN005 " +
	    		"BECMG 0210/0212 9999 BKN010=";
	}
	
	@Override
	public String getTokenizedMessagePrefix() {
		return "";
	}
	
	@Override
	public Identity[] getLexerTokenSequenceIdentity() {
		return new Identity[] {
				TAF_START, AERODROME_DESIGNATOR, ISSUE_TIME, VALID_TIME, SURFACE_WIND, HORIZONTAL_VISIBILITY, WEATHER, CLOUD, 
        		FORECAST_CHANGE_INDICATOR, CHANGE_FORECAST_TIME_GROUP, HORIZONTAL_VISIBILITY, WEATHER,
        		FORECAST_CHANGE_INDICATOR, CHANGE_FORECAST_TIME_GROUP, HORIZONTAL_VISIBILITY, WEATHER,
        		FORECAST_CHANGE_INDICATOR, CHANGE_FORECAST_TIME_GROUP, HORIZONTAL_VISIBILITY, WEATHER,
        		FORECAST_CHANGE_INDICATOR, CHANGE_FORECAST_TIME_GROUP, SURFACE_WIND, HORIZONTAL_VISIBILITY, CLOUD,
        		FORECAST_CHANGE_INDICATOR, CHANGE_FORECAST_TIME_GROUP, HORIZONTAL_VISIBILITY, CLOUD,
        		END_TOKEN
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
