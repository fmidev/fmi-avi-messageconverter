package fi.fmi.avi.parser;

import fi.fmi.avi.data.AviationWeatherMessage;

/**
 * Created by rinne on 15/02/17.
 */
public interface AviMessageTACTokenizer {

    LexemeSequence tokenizeMessage(AviationWeatherMessage msg);

    LexemeSequence tokenizeMessage(AviationWeatherMessage msg, ParsingHints hints);
}
