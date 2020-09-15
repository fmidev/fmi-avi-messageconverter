package fi.fmi.avi.converter.json;

import fi.fmi.avi.converter.AviMessageSpecificConverter;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.model.swx.SpaceWeatherAdvisory;
import fi.fmi.avi.model.swx.immutable.SpaceWeatherAdvisoryImpl;

public class SpaceWeatherAdvisoryJSONParser extends AbstractJSONParser implements AviMessageSpecificConverter<String, SpaceWeatherAdvisory>  {
    @Override
    public ConversionResult<SpaceWeatherAdvisory> convertMessage(final String input, final ConversionHints hints) {
        return doConvertMessage(input, SpaceWeatherAdvisory.class, SpaceWeatherAdvisoryImpl.class, hints);
    }
}
