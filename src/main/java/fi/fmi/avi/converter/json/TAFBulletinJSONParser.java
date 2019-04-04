package fi.fmi.avi.converter.json;

import fi.fmi.avi.converter.AviMessageSpecificConverter;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.model.taf.TAFBulletin;
import fi.fmi.avi.model.taf.immutable.TAFBulletinImpl;

/**
 * A simple wrapper to specialize the {@link AbstractJSONParser} for TAFBulletin.
 */
public class TAFBulletinJSONParser extends AbstractJSONParser implements AviMessageSpecificConverter<String, TAFBulletin> {

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
    public ConversionResult<TAFBulletin> convertMessage(final String input, final ConversionHints hints) {
        return doConvertMessage(input, TAFBulletin.class, TAFBulletinImpl.class, hints);
    }
}
