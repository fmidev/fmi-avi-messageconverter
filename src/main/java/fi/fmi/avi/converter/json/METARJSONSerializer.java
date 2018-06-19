package fi.fmi.avi.converter.json;

import fi.fmi.avi.converter.AviMessageSpecificConverter;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.model.metar.METAR;

public class METARJSONSerializer extends AbstractJSONSerializer implements AviMessageSpecificConverter<METAR, String> {

    /**
     * Converts a single message.
     *
     * @param input input message
     * @param hints parsing hints
     * @return the {@link ConversionResult} with the converter message and the possible conversion issues
     */
    @Override
    public ConversionResult<String> convertMessage(METAR input, ConversionHints hints) {
        return doConvertMessage(input, hints);
    }
}
