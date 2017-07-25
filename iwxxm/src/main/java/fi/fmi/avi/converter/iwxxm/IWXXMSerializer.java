package fi.fmi.avi.converter.iwxxm;

import fi.fmi.avi.converter.AviMessageSpecificConverter;
import fi.fmi.avi.model.AviationWeatherMessage;

/**
 * Created by rinne on 19/07/17.
 */
public interface IWXXMSerializer <T extends AviationWeatherMessage, S> extends AviMessageSpecificConverter<T, S> {

}
