package fi.fmi.avi.converter.json.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fi.fmi.avi.converter.AviMessageSpecificConverter;
import fi.fmi.avi.converter.ConversionSpecification;
import fi.fmi.avi.converter.json.METARJSONParser;
import fi.fmi.avi.converter.json.METARJSONSerializer;
import fi.fmi.avi.converter.json.TAFJSONParser;
import fi.fmi.avi.converter.json.TAFJSONSerializer;
import fi.fmi.avi.model.metar.METAR;
import fi.fmi.avi.model.taf.TAF;

/**
 * Spring configuration for Java POJO and JSON conversion.
 */
@Configuration
public class JSONConverter {

    /**
     * Pre-configured spec for {@link TAF} to IWXXM 2.1 XML format TAF document String.
     */
    public static final ConversionSpecification<TAF, String> TAF_POJO_TO_JSON_STRING = new ConversionSpecification<>(TAF.class, String.class,
            null, "TAF, fmi-avi-messageconverter JSON");


    /**
     * Pre-configured spec for {@link TAF} to IWXXM 2.1 XML format TAF document DOM Node.
     */
    public static final ConversionSpecification<METAR, String> METAR_POJO_TO_JSON_STRING = new ConversionSpecification<>(METAR.class, String.class,
            null, "METAR, fmi-avi-messageconverter JSON");

    /**
     * Pre-configured spec for IWXXM 2.1 XML format TAF document String to {@link TAF}.
     */
    public static final ConversionSpecification<String, TAF> JSON_STRING_TO_TAF_POJO = new ConversionSpecification<>(String.class,TAF.class,
            "TAF, fmi-avi-messageconverter JSON", null);


    /**
     * Pre-configured spec for IWXXM 2.1 XML format TAF document DOM Node to {@link TAF}.
     */
    public static final ConversionSpecification<String, METAR> JSON_STRING_TO_METAR_POJO = new ConversionSpecification<>(String.class, METAR.class,
            "METAR, fmi-avi-messageconverter JSON", null);


    @Bean
    public AviMessageSpecificConverter<METAR, String> metarJSONSerializer() {
        return new METARJSONSerializer();
    }

    @Bean
    public AviMessageSpecificConverter<TAF, String> tafJSONSerializer() {
        return new TAFJSONSerializer();
    }

    @Bean
    public AviMessageSpecificConverter<String, TAF> tafJSONParser() {
        return new TAFJSONParser();
    }

    @Bean
    public AviMessageSpecificConverter<String, METAR> metarJSONParser() {
        return new METARJSONParser();
    }

}
