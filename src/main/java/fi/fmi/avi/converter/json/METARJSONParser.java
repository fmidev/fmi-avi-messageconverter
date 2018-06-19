package fi.fmi.avi.converter.json;

import fi.fmi.avi.converter.AviMessageSpecificConverter;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.model.metar.METAR;
import fi.fmi.avi.model.metar.immutable.METARImpl;

public class METARJSONParser extends AbstractJSONParser implements AviMessageSpecificConverter<String,METAR> {

    @Override
    public ConversionResult<METAR> convertMessage(String input, ConversionHints hints) {
        return doConvertMessage(input, METAR.class, METARImpl.class, hints);
    }
}
