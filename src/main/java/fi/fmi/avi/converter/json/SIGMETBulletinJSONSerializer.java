package fi.fmi.avi.converter.json;

import fi.fmi.avi.converter.AviMessageSpecificConverter;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.model.sigmet.SIGMETBulletin;

/**
 * A simple wrapper to specialize {@link AbstractJSONSerializer} for SIGMETBulletin.
 */
public class SIGMETBulletinJSONSerializer extends AbstractJSONSerializer implements AviMessageSpecificConverter<SIGMETBulletin, String> {

    /**
     * Converts a SIGMETBulletin object into JSON.
     *
     * @param input
     *         input message
     * @param hints
     *         parsing hints
     *
     * @return the {@link ConversionResult} with the converter message and the possible conversion issues
     */
    @Override
    public ConversionResult<String> convertMessage(final SIGMETBulletin input, final ConversionHints hints) {
        return doConvertMessage(input, hints);
    }
}
