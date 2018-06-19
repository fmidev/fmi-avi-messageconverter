package fi.fmi.avi.converter.json;

import fi.fmi.avi.converter.AviMessageSpecificConverter;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.immutable.TAFImpl;

public class TAFJSONParser extends AbstractJSONParser implements AviMessageSpecificConverter<String, TAF> {

    /**
     * Converts a single message.
     *
     * @param input input message
     * @param hints parsing hints
     * @return the {@link ConversionResult} with the converter message and the possible conversion issues
     */
    @Override
    public ConversionResult<TAF> convertMessage(String input, ConversionHints hints) {
        return doConvertMessage(input, TAF.class, TAFImpl.class, hints);
    }
}
