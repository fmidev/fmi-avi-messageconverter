package fi.fmi.avi.converter.json;

import fi.fmi.avi.converter.AviMessageSpecificConverter;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.model.bulletin.GenericMeteorologicalBulletin;

/**
 * A simple wrapper to specialize {@link AbstractJSONSerializer} for TAFBulletin.
 */
public class GenericMeteorologicalBulletinJSONSerializer extends AbstractJSONSerializer implements AviMessageSpecificConverter<GenericMeteorologicalBulletin, String> {

    /**
     * Converts a TAFBulletin object into JSON.
     *
     * @param input
     *         input message
     * @param hints
     *         parsing hints
     *
     * @return the {@link ConversionResult} with the converter message and the possible conversion issues
     */
    @Override
    public ConversionResult<String> convertMessage(final GenericMeteorologicalBulletin input, final ConversionHints hints) {
        return doConvertMessage(input, hints);
    }
}
