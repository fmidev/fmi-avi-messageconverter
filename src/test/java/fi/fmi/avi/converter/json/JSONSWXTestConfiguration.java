package fi.fmi.avi.converter.json;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import fi.fmi.avi.converter.AviMessageConverter;
import fi.fmi.avi.converter.AviMessageSpecificConverter;
import fi.fmi.avi.converter.json.conf.JSONConverter;
import fi.fmi.avi.model.SWX.SWX;

@Configuration
@Import(JSONConverter.class)
public class JSONSWXTestConfiguration {

    @Bean
    private static ObjectMapper getObjectMapper() {
        System.err.println("ObjectMapper created");
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new Jdk8Module());
        om.registerModule(new JavaTimeModule());
        return om;
    }

    @Autowired
    private AviMessageSpecificConverter<SWX, String> swxJSONSerializer;

    @Autowired
    private AviMessageSpecificConverter<String, SWX> swxJSONParser;

    @Bean
    public AviMessageConverter aviMessageConverter() {
        AviMessageConverter p = new AviMessageConverter();
        p.setMessageSpecificConverter(JSONConverter.JSON_STRING_TO_SWX_POJO, swxJSONParser);
        p.setMessageSpecificConverter(JSONConverter.SWX_POJO_TO_JSON_STRING, swxJSONSerializer);
        return p;
    }
}
