package fi.fmi.avi.converter.json;

import fi.fmi.avi.converter.AviMessageSpecificConverter;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.model.taf.TAF;

/**
 * A simple wrapper to specialize {@link AbstractJSONSerializer} for TAF.
 */
public class TAFJSONSerializer extends AbstractJSONSerializer implements AviMessageSpecificConverter<TAF, String> {

    /**
     * Converts a TAF object into JSON.
     *
     * @param input input message
     * @param hints parsing hints
     * @return the {@link ConversionResult} with the converter message and the possible conversion issues
     */
    @Override
    public ConversionResult<String> convertMessage(TAF input, ConversionHints hints) {
        return doConvertMessage(input,  hints);
    }
}
