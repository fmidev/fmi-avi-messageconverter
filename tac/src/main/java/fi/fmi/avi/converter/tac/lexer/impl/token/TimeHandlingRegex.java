package fi.fmi.avi.converter.tac.lexer.impl.token;

import fi.fmi.avi.converter.tac.lexer.impl.RegexMatchingLexemeVisitor;

/**
 * Created by rinne on 22/02/17.
 */
public abstract class TimeHandlingRegex extends RegexMatchingLexemeVisitor {

    public TimeHandlingRegex(final String pattern, final Priority priority) {
        super(pattern, priority);
    }

    static boolean timeOkDayHour(int date, int hour) {
        return timeOkDayHourMinute(date, hour, -1);
    }

    static boolean timeOkHour(int hour) {
        return timeOkDayHourMinute(-1, hour, -1);
    }
    
    static boolean timeOkHourMinute(int hour, int minute) {
        return timeOkDayHourMinute(-1, hour, minute);
    }

    static boolean timeOkDayHourMinute(int date, int hour, int minute) {
        if (date < 32 && hour < 25 && minute < 60) {
            if (hour == 24 && minute > 0) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
}
