package fi.fmi.avi.parser;

import fi.fmi.avi.data.AviationWeatherMessage;

public interface AviMessageParser {

    <T extends AviationWeatherMessage> ParsingResult<T> parseMessage(LexemeSequence lexed, Class<T> type);

    <T extends AviationWeatherMessage> ParsingResult<T> parseMessage(LexemeSequence lexed, Class<T> type, ParsingHints hints);
}
