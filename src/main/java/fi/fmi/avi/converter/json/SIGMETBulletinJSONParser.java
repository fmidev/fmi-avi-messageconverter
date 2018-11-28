package fi.fmi.avi.converter.json;

import fi.fmi.avi.converter.AviMessageSpecificConverter;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.model.sigmet.SIGMETBulletin;
import fi.fmi.avi.model.sigmet.immutable.SIGMETBulletinImpl;

/**
 * A simple wrapper to specialize the {@link AbstractJSONParser} for TAFBulletin.
 */
public class SIGMETBulletinJSONParser extends AbstractJSONParser implements AviMessageSpecificConverter<String, SIGMETBulletin> {

    /**
     * Converts JSON to SIGMETBulletin Object.
     *
     * @param input
     *         input message
     * @param hints
     *         parsing hints
     *
     * @return the conversion result.
     */
    @Override
    public ConversionResult<SIGMETBulletin> convertMessage(final String input, final ConversionHints hints) {
        return doConvertMessage(input, SIGMETBulletin.class, SIGMETBulletinImpl.class, hints);
    }
}
