package fi.fmi.avi.converter.json;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import fi.fmi.avi.converter.AviMessageConverter;
import fi.fmi.avi.converter.AviMessageSpecificConverter;
import fi.fmi.avi.converter.json.conf.JSONConverter;
import fi.fmi.avi.model.metar.METAR;
import fi.fmi.avi.model.sigmet.SIGMET;
import fi.fmi.avi.model.sigmet.SIGMETBulletin;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.TAFBulletin;

@Configuration
@Import(JSONConverter.class)
public class JSONTestConfiguration {
    
    @Autowired
    private AviMessageSpecificConverter<TAF, String> tafJSONSerializer;
    
    @Autowired
    private AviMessageSpecificConverter<METAR, String> metarJSONSerializer;

    @Autowired
    private AviMessageSpecificConverter<SIGMET, String> sigmetJSONSerializer;

    @Autowired
    private AviMessageSpecificConverter<TAFBulletin, String> tafBulletinJSONSerializer;

    @Autowired
    private AviMessageSpecificConverter<SIGMETBulletin, String> sigmetBulletinJSONSerializer;

    @Autowired
    private AviMessageSpecificConverter<String, TAF> tafJSONParser;

    @Autowired
    private AviMessageSpecificConverter<String, METAR> metarJSONParser;

    @Autowired
    private AviMessageSpecificConverter<String, SIGMET> sigmetJSONParser;

    @Autowired
    private AviMessageSpecificConverter<String, TAFBulletin> tafBulletinJSONParser;

    @Autowired
    private AviMessageSpecificConverter<String, SIGMETBulletin> sigmetBulletinJSONParser;

    @Bean
    public AviMessageConverter aviMessageConverter() {
        final AviMessageConverter p = new AviMessageConverter();
        p.setMessageSpecificConverter(JSONConverter.JSON_STRING_TO_TAF_POJO, tafJSONParser);
        p.setMessageSpecificConverter(JSONConverter.JSON_STRING_TO_METAR_POJO, metarJSONParser);
        p.setMessageSpecificConverter(JSONConverter.JSON_STRING_TO_SIGMET_POJO, sigmetJSONParser);
        p.setMessageSpecificConverter(JSONConverter.JSON_STRING_TO_TAF_BULLETIN_POJO, tafBulletinJSONParser);
        p.setMessageSpecificConverter(JSONConverter.JSON_STRING_TO_SIGMET_BULLETIN_POJO, sigmetBulletinJSONParser);

        p.setMessageSpecificConverter(JSONConverter.TAF_POJO_TO_JSON_STRING, tafJSONSerializer);
        p.setMessageSpecificConverter(JSONConverter.METAR_POJO_TO_JSON_STRING, metarJSONSerializer);
        p.setMessageSpecificConverter(JSONConverter.SIGMET_POJO_TO_JSON_STRING, sigmetJSONSerializer);
        p.setMessageSpecificConverter(JSONConverter.TAF_BULLETIN_POJO_TO_JSON_STRING, tafBulletinJSONSerializer);
        p.setMessageSpecificConverter(JSONConverter.SIGMET_BULLETIN_POJO_TO_JSON_STRING, sigmetBulletinJSONSerializer);
        return p;
    }

}
