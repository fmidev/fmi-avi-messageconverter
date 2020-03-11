package fi.fmi.avi.converter.json;

import fi.fmi.avi.converter.AviMessageSpecificConverter;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.model.SpaceWeatherAdvisory.SpaceWeatherAdvisory;
import fi.fmi.avi.model.SpaceWeatherAdvisory.immutable.SpaceWeatherAdvisoryImpl;

public class SpaceWeatherAdvisoryJSONParser extends AbstractJSONParser implements AviMessageSpecificConverter<String, SpaceWeatherAdvisory>  {
    @Override
    public ConversionResult<SpaceWeatherAdvisory> convertMessage(String input, ConversionHints hints) {
        return doConvertMessage(input, SpaceWeatherAdvisory.class, SpaceWeatherAdvisoryImpl.class, hints);
    }
}
