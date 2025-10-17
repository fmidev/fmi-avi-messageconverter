package fi.fmi.avi.converter.json;

import fi.fmi.avi.converter.AviMessageSpecificConverter;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.model.swx.amd79.SpaceWeatherAdvisoryAmd79;
import fi.fmi.avi.model.swx.amd79.immutable.SpaceWeatherAdvisoryAmd79Impl;

public class SpaceWeatherAdvisoryAmd79JSONParser extends AbstractJSONParser implements AviMessageSpecificConverter<String, SpaceWeatherAdvisoryAmd79> {
    @Override
    public ConversionResult<SpaceWeatherAdvisoryAmd79> convertMessage(final String input, final ConversionHints hints) {
        return doConvertMessage(input, SpaceWeatherAdvisoryAmd79.class, SpaceWeatherAdvisoryAmd79Impl.class, hints);
    }
}
