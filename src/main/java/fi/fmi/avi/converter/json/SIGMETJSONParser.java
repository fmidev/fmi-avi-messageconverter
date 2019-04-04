package fi.fmi.avi.converter.json;

import fi.fmi.avi.converter.AviMessageSpecificConverter;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.model.sigmet.SIGMET;
import fi.fmi.avi.model.sigmet.WSVASIGMET;
import fi.fmi.avi.model.sigmet.immutable.WSVASIGMETImpl;

/**
 * A simple wrapper to specialize {@link AbstractJSONParser} for SIGMET.
 */
public class SIGMETJSONParser extends AbstractJSONParser implements AviMessageSpecificConverter<String, WSVASIGMET> {

    /**
     * Converts a JSON TAF message into TAF Object.
     *
     * @param input input message
     * @param hints parsing hints
     * @return the {@link ConversionResult} with the converter message and the possible conversion issues
     */
    @Override
    public ConversionResult<WSVASIGMET> convertMessage(String input, ConversionHints hints) {
        return doConvertMessage(input, WSVASIGMET.class, WSVASIGMETImpl.class, hints);
    }
}
