package fi.fmi.avi.converter.json;

import fi.fmi.avi.converter.AviMessageSpecificConverter;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.model.SWX.SWX;
import fi.fmi.avi.model.SWX.immutable.SWXImpl;

public class SWXJSONParser extends AbstractJSONParser implements AviMessageSpecificConverter<String, SWX>  {
    @Override
    public ConversionResult<SWX> convertMessage(String input, ConversionHints hints) {
        return doConvertMessage(input, SWX.class, SWXImpl.class, hints);
    }
}
