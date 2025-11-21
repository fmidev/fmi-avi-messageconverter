package fi.fmi.avi.converter.json;

import fi.fmi.avi.converter.AviMessageSpecificConverter;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.model.swx.amd82.SpaceWeatherAdvisoryAmd82;

public class SpaceWeatherAdvisoryAmd82JSONSerializer extends AbstractJSONSerializer implements AviMessageSpecificConverter<SpaceWeatherAdvisoryAmd82, String> {
    @Override
    public ConversionResult<String> convertMessage(final SpaceWeatherAdvisoryAmd82 input, final ConversionHints hints) {
        return doConvertMessage(input, hints);
    }
}
