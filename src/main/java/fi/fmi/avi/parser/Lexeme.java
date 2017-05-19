package fi.fmi.avi.parser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import static fi.fmi.avi.parser.Lexeme.ParsedValueName.*;

/**
 * Lexeme is a basic lexical unit of an aviation weather message. 
 * A Lexeme is an abstraction of a single semantic character string 
 * token of a message encoded using Traditional Alphanumeric Codes (TAC).
 * 
 * In AviMessageParser library a TAC message is first parsed into 
 * {@link LexemeSequence} containing a list of identified 
 * (and possibly unidentified) {@Link Lexeme}s by 
 * {@link AviMessageLexer}. This result is then typically fed into
 * {@link AviMessageParser} to construct a Java POJO for the entire 
 * message.
 * 
 * In addition to the identification of the token, a Lexeme may contain
 * the start and end index if the token in the original input String,
 * token the lexing status and possible error/warning message 
 * provided by the Lexer. Lexer also provides navigation links 
 * forward and backward in the LexemeSequence, and acts a the abstract
 * target for the {@link LexemeVisitor} according to the Visitor design 
 * pattern for iteratively identifying a set of Lexemes by a matching them
 * against different possible token patterns until they can be either 
 * positively identified or all possible options have been tried out.
 * 
 * To support further use of the results of the identifying work necessary for 
 * identifying a Lexeme, {@link AviMessageLexer} may store
 * values extracted from the parsed string token into the Lexeme
 * by using the {@link #setParsedValue(ParsedValueName, Object)} method.
 * These values may be queried by the {@link AviMessageParser} for 
 * constructing the {@link fi.fmi.avi.data.AviationWeatherMessage} POJOs.
 * 
 * {@link LexemeSequence} may also be directly used for providing validating 
 * user feedback for a TAC message under construction. Any non-empty 
 * String will always pass the {@link AviMessageLexer#lexMessage(String)} method
 * returning a {@link LexemeSequence} with both recognized and unrecognized 
 * Lexemes.
 * 
 * @author Ilkka Rinne / Spatineo 2017
 *
 */
public interface Lexeme {

	/**
	 * Lexeme status based on lexing process.
	 *
	 */
    enum Status {
        UNRECOGNIZED, OK, SYNTAX_ERROR, WARNING
    }
    /**
     * Lexeme identity corresponding to the different token 
     * types used in aviation weather messages.
     * 
     */
    enum Identity {
        METAR_START,
        TAF_START,
        CORRECTION, 
        AMENDMENT, 
        CANCELLATION, 
        NIL,
        ISSUE_TIME(DAY1, HOUR1, MINUTE1),
        AERODROME_DESIGNATOR(VALUE, COUNTRY),
        CAVOK,
        AIR_DEWPOINT_TEMPERATURE(VALUE, UNIT),
        AIR_PRESSURE_QNH(VALUE, UNIT),
        SURFACE_WIND(DIRECTION, MAX_VALUE, MEAN_VALUE, UNIT),
        VARIABLE_WIND_DIRECTION(MIN_DIRECTION, MAX_DIRECTION, UNIT),
        HORIZONTAL_VISIBILITY(RELATIONAL_OPERATOR, VALUE, UNIT, DIRECTION),
        CLOUD(VALUE, COVER, TYPE, UNIT),
        FORECAST_CHANGE_INDICATOR(DAY1, HOUR1, MINUTE1, TYPE), 
        NO_SIGNIFICANT_WEATHER, 
        NO_SIGNIFICANT_CLOUD, 
        NO_SIGNIFICANT_CHANGES, 
        CHANGE_FORECAST_TIME_GROUP(DAY1, DAY2, HOUR1, HOUR2),
        AUTOMATED,
        RUNWAY_VISUAL_RANGE(RUNWAY, MIN_VALUE, MAX_VALUE, RELATIONAL_OPERATOR, RELATIONAL_OPERATOR2, TENDENCY_OPERATOR, UNIT),
        WEATHER(VALUE),
        RECENT_WEATHER(VALUE),
        WIND_SHEAR(RUNWAY),
        SEA_STATE(UNIT, UNIT2, VALUE),
        RUNWAY_STATE(RUNWAY, VALUE),
        VALID_TIME(DAY1, DAY2, HOUR1, HOUR2),
        MIN_TEMPERATURE(DAY1, HOUR1, VALUE),
        MAX_TEMPERATURE(DAY1, HOUR1, VALUE),
        REMARKS_START,
        REMARK(VALUE),
        COLOR_CODE(VALUE),
        END_TOKEN;
        
        private Set<ParsedValueName> possibleParameters = new HashSet<>();
        
        Identity(final ParsedValueName...names) {
        	possibleParameters.addAll(Arrays.asList(names));
        }
        
        public Set<ParsedValueName> getPossibleNames() {
        	return this.possibleParameters;
        }
        
        public boolean canStore(ParsedValueName name) {
        	return this.possibleParameters.contains(name);
        }
        
    }

    /**
     * Possible names for querying values of the stored 
     * token parameters created during the lexing process. The names used
     * for each of the Lexemes
     *
     */
    enum ParsedValueName {
        COUNTRY, DAY1, DAY2, HOUR1, HOUR2, MINUTE1, MINUTE2, TYPE, COVER,
        VALUE,
        UNIT, UNIT2,
        MAX_VALUE,
        MIN_VALUE,
        MEAN_VALUE,
        DIRECTION,
        MIN_DIRECTION,
        MAX_DIRECTION,
        RELATIONAL_OPERATOR,
        RELATIONAL_OPERATOR2,
        TENDENCY_OPERATOR,
        RUNWAY
    }

    Identity getIdentity();

    Identity getIdentityIfAcceptable();

    Status getStatus();

    String getLexerMessage();

    int getStartIndex();

    int getEndIndex();

    Map<ParsedValueName, Object> getParsedValues();

    <T> T getParsedValue(ParsedValueName name, Class<T> clz);

    String getTACToken();

    Lexeme getFirst();

    Lexeme getPrevious();

    Lexeme getNext();

    boolean hasPrevious();

    boolean hasNext();

    boolean isSynthetic();

    void identify(final Identity id);

    void identify(final Identity id, final Status status);

    void identify(final Identity id, final Status status, final String note);

    boolean isRecognized();

    void setStatus(final Status status);

    void setSynthetic(final boolean synthetic);

    void setParsedValue(ParsedValueName name, Object value);

    void setLexerMessage(final String msg);

    void setTACToken(final String token);

    //Visitor pattern
    void accept(final LexemeVisitor visitor, final ParsingHints hints);

}
