package fi.fmi.avi.converter.json;

import fi.fmi.avi.converter.AviMessageSpecificConverter;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.model.SWX.SWX;

public class SWXJSONSerializer extends AbstractJSONSerializer implements AviMessageSpecificConverter<SWX, String> {
    @Override
    public ConversionResult<String> convertMessage(final SWX input, final ConversionHints hints) {
        return doConvertMessage(input, hints);
    }
}
