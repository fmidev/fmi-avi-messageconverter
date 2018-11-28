package fi.fmi.avi.converter.json;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import fi.fmi.avi.converter.AviMessageConverter;
import fi.fmi.avi.converter.AviMessageSpecificConverter;
import fi.fmi.avi.converter.json.conf.JSONConverter;
import fi.fmi.avi.model.sigmet.SIGMET;

@Configuration
@Import(JSONConverter.class)
public class JSONVASigmetTestConfiguration {

    @Bean
    private static ObjectMapper getObjectMapper() {
        System.err.println("ObjectMapper created");
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new Jdk8Module());
        om.registerModule(new JavaTimeModule());
        om.registerModule(new JtsModule());
        return om;
    }

    @Autowired
    private AviMessageSpecificConverter<SIGMET, String> sigmetJSONSerializer;

    @Autowired
    private AviMessageSpecificConverter<String, SIGMET> sigmetJSONParser;

    @Bean
    public AviMessageConverter aviMessageConverter() {
        AviMessageConverter p = new AviMessageConverter();
        p.setMessageSpecificConverter(JSONConverter.JSON_STRING_TO_SIGMET_POJO, sigmetJSONParser);
        p.setMessageSpecificConverter(JSONConverter.SIGMET_POJO_TO_JSON_STRING, sigmetJSONSerializer);
        return p;
    }
}
