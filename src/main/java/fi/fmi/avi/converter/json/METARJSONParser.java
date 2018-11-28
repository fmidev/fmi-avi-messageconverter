package fi.fmi.avi.converter.json;

import fi.fmi.avi.converter.AviMessageSpecificConverter;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.model.metar.METAR;
import fi.fmi.avi.model.metar.immutable.METARImpl;

/**
 * A simple wrapper to specialize the {@link AbstractJSONParser} for METAR.
 */
public class METARJSONParser extends AbstractJSONParser implements AviMessageSpecificConverter<String,METAR> {

    /**
     * Converts JSON to METAR Object.
     *
     * @param input
     *         input message
     * @param hints
     *         parsing hints
     *
     * @return the conversion result.
     */
    @Override
    public ConversionResult<METAR> convertMessage(final String input, final ConversionHints hints) {
        return doConvertMessage(input, METAR.class, METARImpl.class, hints);
    }
}
