package fi.fmi.avi.parser;

import fi.fmi.avi.data.AviationWeatherMessage;

public interface AviMessageParser {

    <T extends AviationWeatherMessage> T parseMessage(LexemeSequence lexed, Class<T> type) throws ParsingException;

    <T extends AviationWeatherMessage> T parseMessage(LexemeSequence lexed, Class<T> type, ParsingHints hints) throws ParsingException;
}
