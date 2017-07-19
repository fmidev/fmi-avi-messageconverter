package fi.fmi.avi.converter.tac.lexer.impl.token;

import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.AERODROME_DESIGNATOR;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.ISSUE_TIME;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName.DAY1;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName.HOUR1;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName.MINUTE1;

import java.util.regex.Matcher;

import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.tac.lexer.Lexeme;
import fi.fmi.avi.converter.tac.lexer.impl.FactoryBasedReconstructor;

/**
 * Created by rinne on 10/02/17.
 */
public class IssueTime extends TimeHandlingRegex {

    public IssueTime(final Priority prio) {
        super("^([0-9]{2})([0-9]{2})([0-9]{2})Z$", prio);
    }

    @Override
    public void visitIfMatched(final Lexeme token, final Matcher match, final ConversionHints hints) {
        if (token.hasPrevious() && token.getPrevious().getIdentity() == AERODROME_DESIGNATOR) {
            int date = Integer.parseInt(match.group(1));
            int hour = Integer.parseInt(match.group(2));
            int minute = Integer.parseInt(match.group(3));
            if (timeOkDayHourMinute(date, hour, minute)) {
            	token.identify(ISSUE_TIME);
            	token.setParsedValue(DAY1, Integer.valueOf(date));
                token.setParsedValue(HOUR1, Integer.valueOf(hour));
                token.setParsedValue(MINUTE1, Integer.valueOf(minute));
            } else {
                token.identify(ISSUE_TIME, Lexeme.Status.SYNTAX_ERROR, "Invalid date & time values");
            }
        }
    }

    public static class Reconstructor extends FactoryBasedReconstructor {

        @Override
        public <T extends AviationWeatherMessage> Lexeme getAsLexeme(T msg, Class<T> clz, final ConversionHints hints, final Object... specifier) {
            return this.createLexeme(msg.getPartialIssueTime(), Lexeme.Identity.ISSUE_TIME);
        }
    }

}
