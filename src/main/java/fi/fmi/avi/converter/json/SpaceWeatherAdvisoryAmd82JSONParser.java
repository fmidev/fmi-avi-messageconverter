package fi.fmi.avi.converter.json;

import fi.fmi.avi.converter.AviMessageSpecificConverter;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.model.swx.amd82.SpaceWeatherAdvisoryAmd82;
import fi.fmi.avi.model.swx.amd82.immutable.SpaceWeatherAdvisoryAmd82Impl;

public class SpaceWeatherAdvisoryAmd82JSONParser extends AbstractJSONParser implements AviMessageSpecificConverter<String, SpaceWeatherAdvisoryAmd82> {
    @Override
    public ConversionResult<SpaceWeatherAdvisoryAmd82> convertMessage(final String input, final ConversionHints hints) {
        return doConvertMessage(input, SpaceWeatherAdvisoryAmd82.class, SpaceWeatherAdvisoryAmd82Impl.class, hints);
    }
}
