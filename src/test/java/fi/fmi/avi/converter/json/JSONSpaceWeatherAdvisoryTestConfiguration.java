package fi.fmi.avi.converter.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fi.fmi.avi.converter.AviMessageConverter;
import fi.fmi.avi.converter.AviMessageSpecificConverter;
import fi.fmi.avi.converter.json.conf.JSONConverter;
import fi.fmi.avi.model.swx.amd79.SpaceWeatherAdvisoryAmd79;
import fi.fmi.avi.model.swx.amd82.SpaceWeatherAdvisoryAmd82;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(JSONConverter.class)
public class JSONSpaceWeatherAdvisoryTestConfiguration {

    @Autowired
    private AviMessageSpecificConverter<SpaceWeatherAdvisoryAmd79, String> swxAmd79JSONSerializer;
    @Autowired
    private AviMessageSpecificConverter<SpaceWeatherAdvisoryAmd82, String> swxAmd82JSONSerializer;
    @Autowired
    private AviMessageSpecificConverter<String, SpaceWeatherAdvisoryAmd79> swxAmd79JSONParser;
    @Autowired
    private AviMessageSpecificConverter<String, SpaceWeatherAdvisoryAmd82> swxAmd82JSONParser;

    @Bean
    static ObjectMapper getObjectMapper() {
        final ObjectMapper om = new ObjectMapper();
        om.registerModule(new Jdk8Module());
        om.registerModule(new JavaTimeModule());
        return om;
    }

    @Bean
    public AviMessageConverter aviMessageConverter() {
        final AviMessageConverter p = new AviMessageConverter();
        p.setMessageSpecificConverter(JSONConverter.JSON_STRING_TO_SWX_AMD79_POJO, swxAmd79JSONParser);
        p.setMessageSpecificConverter(JSONConverter.JSON_STRING_TO_SWX_AMD82_POJO, swxAmd82JSONParser);
        p.setMessageSpecificConverter(JSONConverter.SWX_AMD79_POJO_TO_JSON_STRING, swxAmd79JSONSerializer);
        p.setMessageSpecificConverter(JSONConverter.SWX_AMD82_POJO_TO_JSON_STRING, swxAmd82JSONSerializer);
        return p;
    }
}
