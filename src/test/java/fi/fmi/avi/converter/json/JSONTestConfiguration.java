package fi.fmi.avi.converter.json;

import fi.fmi.avi.converter.AviMessageConverter;
import fi.fmi.avi.converter.AviMessageSpecificConverter;
import fi.fmi.avi.converter.json.conf.JSONConverter;
import fi.fmi.avi.model.metar.METAR;
import fi.fmi.avi.model.taf.TAF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(JSONConverter.class)
public class JSONTestConfiguration {
    
    @Autowired
    private AviMessageSpecificConverter<TAF, String> tafJSONSerializer;
    
    @Autowired
    private AviMessageSpecificConverter<METAR, String> metarJSONSerializer;

    @Autowired
    private AviMessageSpecificConverter<String, TAF> tafJSONParser;

    @Autowired
    private AviMessageSpecificConverter<String, METAR> metarJSONParser;

    @Bean
    public AviMessageConverter aviMessageConverter() {
        AviMessageConverter p = new AviMessageConverter();
        p.setMessageSpecificConverter(JSONConverter.JSON_STRING_TO_METAR_POJO,metarJSONParser);
        p.setMessageSpecificConverter(JSONConverter.METAR_POJO_TO_JSON_STRING, metarJSONSerializer);
        p.setMessageSpecificConverter(JSONConverter.JSON_STRING_TO_TAF_POJO, tafJSONParser);
        p.setMessageSpecificConverter(JSONConverter.TAF_POJO_TO_JSON_STRING, tafJSONSerializer);
        return p;
    }

}
