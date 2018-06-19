package fi.fmi.avi.converter.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionIssue;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.model.AviationWeatherMessage;

import java.io.IOException;

public abstract class AbstractJSONParser {

    protected  <T,S extends AviationWeatherMessage> ConversionResult<T> doConvertMessage(String input, Class<T> clz, Class<S> implClz, ConversionHints hints) {
        ConversionResult<T> result = new ConversionResult<>();
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new Jdk8Module());
        om.registerModule(new JavaTimeModule());
        try {
            S o = om.readValue(input, implClz);
            if (clz.isAssignableFrom(implClz)) {
                result.setConvertedMessage((T) o);
            }
            result.setStatus(ConversionResult.Status.SUCCESS);
        } catch (IOException e) {
            result.addIssue(new ConversionIssue(ConversionIssue.Severity.ERROR, ConversionIssue.Type.OTHER, "Error parsing JSON", e));
            result.setStatus(ConversionResult.Status.FAIL);
        }
        return result;
    }
}
