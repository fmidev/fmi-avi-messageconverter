package fi.fmi.avi.converter.json;

import fi.fmi.avi.converter.AviMessageSpecificConverter;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.model.swx.amd79.SpaceWeatherAdvisoryAmd79;

public class SpaceWeatherAdvisoryAmd79JSONSerializer extends AbstractJSONSerializer implements AviMessageSpecificConverter<SpaceWeatherAdvisoryAmd79, String> {
    @Override
    public ConversionResult<String> convertMessage(final SpaceWeatherAdvisoryAmd79 input, final ConversionHints hints) {
        return doConvertMessage(input, hints);
    }
}
