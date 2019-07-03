package fi.fmi.avi.converter.json;

import fi.fmi.avi.converter.AviMessageSpecificConverter;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.model.bulletin.GenericMeteorologicalBulletin;
import fi.fmi.avi.model.bulletin.immutable.GenericMeteorologicalBulletinImpl;

/**
 * A simple wrapper to specialize the {@link AbstractJSONParser} for TAFBulletin.
 */
public class GenericMeteorologicalBulletinJSONParser extends AbstractJSONParser implements AviMessageSpecificConverter<String, GenericMeteorologicalBulletin> {

    /**
     * Converts JSON to TAFBulletin Object.
     *
     * @param input
     *         input message
     * @param hints
     *         parsing hints
     *
     * @return the conversion result.
     */
    @Override
    public ConversionResult<GenericMeteorologicalBulletin> convertMessage(final String input, final ConversionHints hints) {
        return doConvertMessage(input, GenericMeteorologicalBulletin.class, GenericMeteorologicalBulletinImpl.class, hints);
    }
}
