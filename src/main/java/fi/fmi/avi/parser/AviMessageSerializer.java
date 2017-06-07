package fi.fmi.avi.parser;

import fi.fmi.avi.data.AviationWeatherMessage;

/**
 * Created by rinne on 07/06/17.
 */
public interface AviMessageSerializer {

    <S extends AviationWeatherMessage, T> T serializeMessage(S input, ConversionSpecification<T, S> spec) throws SerializingException;

    <S extends AviationWeatherMessage, T> T serializeMessage(S input, ConversionSpecification<T, S> spec, ConversionHints hints) throws SerializingException;
}
