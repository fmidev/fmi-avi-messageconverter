package fi.fmi.avi.parser;

import java.util.Collection;
import java.util.List;

import fi.fmi.avi.data.AviationWeatherMessage;

/**
 * Created by rinne on 21/04/17.
 */
public interface ParsingResult<T extends AviationWeatherMessage> {

    enum ParsingStatus {SUCCESS, WITH_ERRORS, FAIL};

    ParsingStatus getStatus();

    T getParsedMessage();

    List<ParsingIssue> getParsingIssues();

    void setParsedMessage(T message);

    void addIssue(ParsingIssue issue);

    void addIssue(Collection<ParsingIssue> issues);

}
