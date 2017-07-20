package fi.fmi.avi.converter.iwxxm;

import java.io.Reader;

import fi.fmi.avi.converter.AviMessageSpecificConverter;
import fi.fmi.avi.model.AviationWeatherMessage;

/**
 * Created by rinne on 19/07/17.
 */
public interface IWXXMParser <T extends AviationWeatherMessage> extends AviMessageSpecificConverter<String, T> {

}
