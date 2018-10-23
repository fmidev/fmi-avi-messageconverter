package fi.fmi.avi.converter.json;

import fi.fmi.avi.converter.AviMessageSpecificConverter;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.model.sigmet.SIGMET;

/**
 * A simple wrapper to specialize {@link AbstractJSONSerializer} for SIGMET.
 */
public class SIGMETJSONSerializer extends AbstractJSONSerializer implements AviMessageSpecificConverter<SIGMET, String> {

    /**
     * Converts a SIGMET object into JSON.
     *
     * @param input input message
     * @param hints parsing hints
     * @return the {@link ConversionResult} with the converter message and the possible conversion issues
     */
    @Override
    public ConversionResult<String> convertMessage(SIGMET input, ConversionHints hints) {
        return doConvertMessage(input,  hints);
    }
}
