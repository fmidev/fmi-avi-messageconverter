package fi.fmi.avi.parser;

import java.util.Map;

/**
 * Created by rinne on 16/12/16.
 */
public interface Lexeme {

    enum Status {
        UNRECOGNIZED, OK, SYNTAX_ERROR, WARNING
    }

    enum Identity {
        METAR_START,
        TAF_START,
        CORRECTION, AMENDMENT, CANCELLATION, NIL,
        ISSUE_TIME,
        AERODROME_DESIGNATOR,
        CAVOK,
        AIR_DEWPOINT_TEMPERATURE,
        AIR_PRESSURE_QNH,
        SURFACE_WIND,
        VARIABLE_WIND_DIRECTION,
        HORIZONTAL_VISIBILITY,
        CLOUD,
        FORECAST_CHANGE_INDICATOR, NO_SIGNIFICANT_WEATHER, CHANGE_FORECAST_TIME_GROUP,
        AUTOMATED,
        RUNWAY_VISUAL_RANGE, WEATHER,
        RECENT_WEATHER,
        WIND_SHEAR,
        SEA_STATE,
        RUNWAY_STATE,
        VALID_TIME,
        MIN_TEMPERATURE,
        MAX_TEMPERATURE,
        REMARKS_START,
        REMARK,
        TEXT,
        COLOR_CODE,
        END_TOKEN

    }

    enum ParsedValueName {
        COUNTRY, DAY1, DAY2, HOUR1, HOUR2, MINUTE1, MINUTE2, TYPE, COVER,
        VALUE,
        UNIT,
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

    Map<ParsedValueName, Object> getParsedValues();

    Object getParsedValue(ParsedValueName name);
    
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

    void setParsedValues(Map<ParsedValueName, Object> values);

    void setLexerMessage(final String msg);

    void setTACToken(final String token);

    //Visitor pattern
    void accept(final LexemeVisitor visitor, final ParsingHints hints);

}
