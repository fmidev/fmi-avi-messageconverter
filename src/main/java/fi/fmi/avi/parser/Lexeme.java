package fi.fmi.avi.parser;

import static fi.fmi.avi.parser.Lexeme.ParsedValueName.COUNTRY;
import static fi.fmi.avi.parser.Lexeme.ParsedValueName.COVER;
import static fi.fmi.avi.parser.Lexeme.ParsedValueName.DAY1;
import static fi.fmi.avi.parser.Lexeme.ParsedValueName.DAY2;
import static fi.fmi.avi.parser.Lexeme.ParsedValueName.DIRECTION;
import static fi.fmi.avi.parser.Lexeme.ParsedValueName.HOUR1;
import static fi.fmi.avi.parser.Lexeme.ParsedValueName.HOUR2;
import static fi.fmi.avi.parser.Lexeme.ParsedValueName.MAX_DIRECTION;
import static fi.fmi.avi.parser.Lexeme.ParsedValueName.MAX_VALUE;
import static fi.fmi.avi.parser.Lexeme.ParsedValueName.MEAN_VALUE;
import static fi.fmi.avi.parser.Lexeme.ParsedValueName.MINUTE1;
import static fi.fmi.avi.parser.Lexeme.ParsedValueName.MIN_DIRECTION;
import static fi.fmi.avi.parser.Lexeme.ParsedValueName.MIN_VALUE;
import static fi.fmi.avi.parser.Lexeme.ParsedValueName.RELATIONAL_OPERATOR;
import static fi.fmi.avi.parser.Lexeme.ParsedValueName.RELATIONAL_OPERATOR2;
import static fi.fmi.avi.parser.Lexeme.ParsedValueName.RUNWAY;
import static fi.fmi.avi.parser.Lexeme.ParsedValueName.TENDENCY_OPERATOR;
import static fi.fmi.avi.parser.Lexeme.ParsedValueName.TYPE;
import static fi.fmi.avi.parser.Lexeme.ParsedValueName.UNIT;
import static fi.fmi.avi.parser.Lexeme.ParsedValueName.UNIT2;
import static fi.fmi.avi.parser.Lexeme.ParsedValueName.VALUE;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Lexeme is a basic lexical unit of an aviation weather message. 
 * A Lexeme is an abstraction of a single semantic character string 
 * token of a message encoded using Traditional Alphanumeric Codes (TAC).
 * 
 * In AviMessageParser library a TAC message is first parsed into 
 * {@link LexemeSequence} containing a list of identified 
 * (and possibly unidentified) {@link Lexeme}s by
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

        private final Set<ParsedValueName> possibleParameters = new HashSet<>();
        
        Identity(final ParsedValueName...names) {
        	possibleParameters.addAll(Arrays.asList(names));
        }
        
        public Set<ParsedValueName> getPossibleNames() {
        	return this.possibleParameters;
        }

        public boolean canStore(final ParsedValueName name) {
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

    /**
     * Returns the identity of the Lexeme if the Lexeme has been identified.
     * 
     * @return Lexeme identity, or null if unrecognized.
     */
    Identity getIdentity();

    /**
     * Returns the identity of the Lexeme if the Lexeme has been identified, and 
     * the status is either {@link Status#OK} or {@link Status#WARNING}.
     * 
     * @return Lexeme identity, or null if status is {@link Status#UNRECOGNIZED} or {@link Status#SYNTAX_ERROR}.
     */
    Identity getIdentityIfAcceptable();

    /**
     * Returns the recognizing status of Lexeme.
     * @return the status
     */
    Status getStatus();

    /**
     * Returns the lexing-related message for this Lexeme, if provided by the {@link AviMessageLexer}. 
     * This message is typically provided when warning or error status is set.
     * 
     * @return the lexing message
     */
    String getLexerMessage();

    /**
     * The start index of the token used for creating this Lexeme in the original message input.
     * This is only available if populated by the lexer.
     * 
     * @return index of the first character of the Lexeme token
     */
    int getStartIndex();


    /**
     * The end index of the token used for creating this Lexeme in the original message input.
     * This is only available if populated by the lexer.
     * 
     * @return index of the last character of the Lexeme token
     */
    int getEndIndex();

    /**
     * Provides all additional information entries parsed from the original token by the 
     * {@link AviMessageLexer}.
     * 
     * @see #getParsedValue(ParsedValueName, Class)
     * @return map of entries
     */
    Map<ParsedValueName, Object> getParsedValues();

    /**
     * Returns a particular additional information entry parsed from the original token by the 
     * {@link AviMessageLexer} as given type (if possible). 
     * 
     * The implementations must throw a 
     * {@link ClassCastException} if the provided value exists, but cannot be returned as
     * the type given by {@code clz}. The implementations must throw an 
     * {@link IllegalArgumentException} if the requested entity is not allowed to be used
     * with the {@link Identity} of this Lexeme.
     * 
     * @param name name of the value
     * @param clz class of the expected type
     * @param <T> returned type
     * @return a previously stores value or null if not value is available
     */
    <T> T getParsedValue(ParsedValueName name, Class<T> clz) throws ClassCastException, IllegalArgumentException ;

    /**
     * Returns this Lexeme as TAC encoded token.
     * 
     * @return the TAC token
     */
    String getTACToken();

    /**
     * Returns the first Lexeme in the {@link LexemeSequence} containing this Lexeme.
     * This link is provided mainly as navigation shortcut.
     * 
     * @return the first Lexeme of the sequence, if available
     */
    Lexeme getFirst();

    /**
     * Returns the Lexeme immediately before this one in the {@link LexemeSequence} 
     * containing this Lexeme. For the first Lexeme in sequence this must return 
     * {@code null}.
     * 
     * @return the previous Lexeme of the sequence, if available
     */
    Lexeme getPrevious();

    /**
     * Returns the Lexeme immediately after this one in the {@link LexemeSequence} 
     * containing this Lexeme. For the last Lexeme in sequence this must return 
     * {@code null}.
     * 
     * @return the next Lexeme of the sequence, if available
     */
    Lexeme getNext();

    /**
     * For checking if the Lexeme knows the previous Lexeme in it's sequence.
     * 
     * @return the previous Lexeme
     */
    boolean hasPrevious();

    /**
     * For checking if the Lexeme knows the next Lexeme in it's sequence.
     * 
     * @return the next Lexeme
     */
    boolean hasNext();

    /**
     * A synthetic Lexeme has been created by the lexing process to fix some small syntax
     * issues of the input message, such a missing start token. 
     * 
     * @return true of the Lexemehas been marked as synthetic
     */
    boolean isSynthetic();

    /**
     * Identifies this Lexeme as {@code id} with {@link Status#OK} and no additional message.
     * 
     * @param id identity to assign
     */
    void identify(final Identity id);

    /**
     * Identifies this Lexeme as {@code id} with {@code status} and no additional message.
     * 
     * @param id identity to assign
     * @param status to set
     */
    void identify(final Identity id, final Status status);

    /**
     * Identifies this Lexeme as {@code id} with {@code status} and {@code message}.
     * 
     * @param id identity to assign
     * @param status to set
     * @param note additional message, such as lexing warning note
     */
    void identify(final Identity id, final Status status, final String note);

    /**
     * Return true when the Lexeme is not in {@link Status#UNRECOGNIZED}
     * 
     * @return true is recognized
     */
    boolean isRecognized();

    /**
     * Sets the Lexeme reconizing status.
     * 
     * @param status to set
     */
    void setStatus(final Status status);

    /**
     * Marks this Lexeme as synthetic.
     * 
     * @see #isSynthetic()
     * @param synthetic true to mark as synthetic
     */
    void setSynthetic(final boolean synthetic);

    /**
     * Stores an additional information entity to used later in the parsing process.
     * The main purpose is the simplify further parsing be storing the information
     * bits parsed from the token to make lexing possible. This way this information 
     * does not have to be parsed more than once.
     * 
     * One of the {@link #identify} methods must be called before this method to provide
     * allowed {@code name} checking.
     * 
     * @param name name of the stored value
     * @param value value to store
     * 
     * @throws IllegalArgumentException if the {@code name} is not allowed to be used with the current Lexeme identity
     * @throws IllegalStateException if the Lexeme has not yet been identified.
     */
    void setParsedValue(ParsedValueName name, Object value) throws IllegalArgumentException, IllegalStateException;

    /**
     * Sets a lexing note, such as an explanation for warning or error status.
     *  
     * @param msg message
     */
    void setLexerMessage(final String msg);

    /**
     * Provides access to a {@link LexemeVisitor} to refine this Lexeme.
     * Typically called by a {@link LexemeVisitor} to try to recognize
     * the Lexeme.
     *
     * For hierarchical Lexemes, the implementation is responsible for
     * delegating the {@link #accept(LexemeVisitor, ConversionHints)} to the child
     * nodes.
     *
     * @see LexemeVisitor#visit(Lexeme, ConversionHints)
     *
     * @param visitor to visit this Lexeme
     * @param hints hints to pass the lexing process
     */
    void accept(final LexemeVisitor visitor, final ConversionHints hints);

}
