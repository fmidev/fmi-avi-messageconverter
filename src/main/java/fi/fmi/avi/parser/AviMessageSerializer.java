package fi.fmi.avi.parser;

import fi.fmi.avi.data.AviationWeatherMessage;

/**
 * Writes out the provided {@link AviationWeatherMessage} POJOs in different serialized formats.
 *
 * @author Ilkka Rinne / Spatineo Oy 2017
 */
public interface AviMessageSerializer {

    /**
     * Tries to provide a serialized version of the given <code>input</code> message POJO
     * according to the given {@link ConversionSpecification} <code>spec</code>.
     *
     * @param input
     *         the input POJO
     * @param spec
     *         the specification for conversion
     * @param <S>
     *         input message kind
     * @param <T>
     *         output message kind
     *
     * @return the serialized message if successful
     *
     * @throws SerializingException
     *         if serialization could not be completed successfully
     */
    <S extends AviationWeatherMessage, T> T serializeMessage(S input, ConversionSpecification<T, S> spec) throws SerializingException;

    /**
     * Tries to provide a serialized version of the given <code>input</code> message POJO
     * according to the given {@link ConversionSpecification} <code>spec</code>.
     *
     * @param input the input POJO
     * @param spec the specification for conversion
     * @param hints additional guidance for the serializer
     * @param <S> input message kind
     * @param <T> output message kind
     * @return the serialized message if successful
     *
     * @throws SerializingException if serialization could not be completed successfully
     */
    <S extends AviationWeatherMessage, T> T serializeMessage(S input, ConversionSpecification<T, S> spec, ConversionHints hints) throws SerializingException;
}
