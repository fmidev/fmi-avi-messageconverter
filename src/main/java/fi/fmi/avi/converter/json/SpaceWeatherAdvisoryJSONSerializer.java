package fi.fmi.avi.converter.json;

import fi.fmi.avi.converter.AviMessageSpecificConverter;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.model.SpaceWeatherAdvisory.SpaceWeatherAdvisory;

public class SpaceWeatherAdvisoryJSONSerializer extends AbstractJSONSerializer implements AviMessageSpecificConverter<SpaceWeatherAdvisory, String> {
    @Override
    public ConversionResult<String> convertMessage(final SpaceWeatherAdvisory input, final ConversionHints hints) {
        return doConvertMessage(input, hints);
    }
}
