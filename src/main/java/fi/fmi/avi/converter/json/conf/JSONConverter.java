package fi.fmi.avi.converter.json.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fi.fmi.avi.converter.AviMessageSpecificConverter;
import fi.fmi.avi.converter.ConversionSpecification;
import fi.fmi.avi.converter.json.GenericMeteorologicalBulletinJSONParser;
import fi.fmi.avi.converter.json.GenericMeteorologicalBulletinJSONSerializer;
import fi.fmi.avi.converter.json.METARJSONParser;
import fi.fmi.avi.converter.json.METARJSONSerializer;
import fi.fmi.avi.converter.json.SIGMETBulletinJSONParser;
import fi.fmi.avi.converter.json.SIGMETBulletinJSONSerializer;
import fi.fmi.avi.converter.json.SIGMETJSONParser;
import fi.fmi.avi.converter.json.SIGMETJSONSerializer;
import fi.fmi.avi.converter.json.TAFBulletinJSONParser;
import fi.fmi.avi.converter.json.TAFBulletinJSONSerializer;
import fi.fmi.avi.converter.json.TAFJSONParser;
import fi.fmi.avi.converter.json.TAFJSONSerializer;
import fi.fmi.avi.model.bulletin.GenericMeteorologicalBulletin;
import fi.fmi.avi.model.metar.METAR;
import fi.fmi.avi.model.sigmet.SIGMET;
import fi.fmi.avi.model.sigmet.SIGMETBulletin;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.TAFBulletin;

/**
 * Spring configuration for Java POJO and JSON conversion.
 */
@SuppressWarnings("SpringFacetCodeInspection")
@Configuration
public class JSONConverter {

    /**
     * Pre-configured spec for {@link TAF} to fmi-avi-messageconverter JSON TAF document String.
     */
    public static final ConversionSpecification<TAF, String> TAF_POJO_TO_JSON_STRING = new ConversionSpecification<>(TAF.class, String.class,
            null, "TAF, fmi-avi-messageconverter JSON");

    /**
     * Pre-configured spec for {@link METAR} to fmi-avi-messageconverter JSON METAR document String.
     */
    public static final ConversionSpecification<METAR, String> METAR_POJO_TO_JSON_STRING = new ConversionSpecification<>(METAR.class, String.class,
            null, "METAR, fmi-avi-messageconverter JSON");

    /**
     * Pre-configured spec for {@link SIGMET} to fmi-avi-messageconverter JSON SIGMET document String.
     */
    public static final ConversionSpecification<SIGMET, String> SIGMET_POJO_TO_JSON_STRING = new ConversionSpecification<>(SIGMET.class, String.class, null,
            "SIGMET (TAC-passthrough), fmi-avi-messageconverter JSON");

    /**
     * Pre-configured spec for {@link TAFBulletin} to fmi-avi-messageconverter JSON TAFBulletin document String.
     */
    public static final ConversionSpecification<TAFBulletin, String> TAF_BULLETIN_POJO_TO_JSON_STRING = new ConversionSpecification<>(TAFBulletin.class,
            String.class, null, "TAFBulletin, fmi-avi-messageconverter JSON");

    /**
     * Pre-configured spec for {@link SIGMETBulletin} to fmi-avi-messageconverter JSON SIGMETBulletin document String.
     */
    public static final ConversionSpecification<SIGMETBulletin, String> SIGMET_BULLETIN_POJO_TO_JSON_STRING = new ConversionSpecification<>(
            SIGMETBulletin.class, String.class, null, "SIGMETBulletin, fmi-avi-messageconverter JSON");


    /**
     * Pre-configured spec for {@link GenericMeteorologicalBulletin} to fmi-avi-messageconverter JSON GenericMeteorologicalBulletin document String.
     */
    public static final ConversionSpecification<GenericMeteorologicalBulletin, String> GENERIC_METEOROLOGICAL_BULLETIN_POJO_TO_JSON_STRING = new
            ConversionSpecification<>(
            GenericMeteorologicalBulletin.class, String.class, null, "GenericMeteorologicalBulletin, fmi-avi-messageconverter JSON");

    /**
     * Pre-configured spec for fmi-avi-messageconverter JSON TAF document String to {@link TAF}.
     */
    public static final ConversionSpecification<String, TAF> JSON_STRING_TO_TAF_POJO = new ConversionSpecification<>(String.class,TAF.class,
            "TAF, fmi-avi-messageconverter JSON", null);


    /**
     * Pre-configured spec for fmi-avi-messageconverter JSON METAR document String to {@link METAR}.
     */
    public static final ConversionSpecification<String, METAR> JSON_STRING_TO_METAR_POJO = new ConversionSpecification<>(String.class, METAR.class,
            "METAR, fmi-avi-messageconverter JSON", null);

    /**
     * Pre-configured spec for fmi-avi-messageconverter JSON SIGMET document String to {@link METAR}.
     */
    public static final ConversionSpecification<String, SIGMET> JSON_STRING_TO_SIGMET_POJO = new ConversionSpecification<>(String.class, SIGMET.class,
            "SIGMET (TAC-passthrough), fmi-avi-messageconverter JSON", null);

    /**
     * Pre-configured spec for fmi-avi-messageconverter JSON TAFBulletin document String to {@link TAFBulletin}.
     */
    public static final ConversionSpecification<String, TAFBulletin> JSON_STRING_TO_TAF_BULLETIN_POJO = new ConversionSpecification<>(String.class,
            TAFBulletin.class, "TAFBulletin, fmi-avi-messageconverter JSON", null);

    /**
     * Pre-configured spec for fmi-avi-messageconverter JSON SIGMETBulletin document String to {@link SIGMETBulletin}.
     */
    public static final ConversionSpecification<String, SIGMETBulletin> JSON_STRING_TO_SIGMET_BULLETIN_POJO = new ConversionSpecification<>(String.class,
            SIGMETBulletin.class, "SIGMETBulletin, fmi-avi-messageconverter JSON", null);


    /**
     * Pre-configured spec for fmi-avi-messageconverter JSON GenericMeteorologicalBulletin document String to
     * {@link GenericMeteorologicalBulletin}.
     */
    public static final ConversionSpecification<String, GenericMeteorologicalBulletin> JSON_STRING_TO_GENERIC_BULLETIN_POJO = new ConversionSpecification<>(String.class,
            GenericMeteorologicalBulletin.class, "GenericMeteorologicalBulletin, fmi-avi-messageconverter JSON", null);

    @Bean
    public AviMessageSpecificConverter<METAR, String> metarJSONSerializer() {
        return new METARJSONSerializer();
    }

    @Bean
    public AviMessageSpecificConverter<TAF, String> tafJSONSerializer() {
        return new TAFJSONSerializer();
    }

    @Bean
    public AviMessageSpecificConverter<SIGMET, String> sigmetJSONSerializer() {
        return new SIGMETJSONSerializer();
    }

    @Bean
    public AviMessageSpecificConverter<SIGMETBulletin, String> sigmetBulletinJSONSerializer() {
        return new SIGMETBulletinJSONSerializer();
    }

    @Bean
    public AviMessageSpecificConverter<GenericMeteorologicalBulletin, String> genericBulletinJSONSerializer() {
        return new GenericMeteorologicalBulletinJSONSerializer();
    }

    @Bean
    public AviMessageSpecificConverter<TAFBulletin, String> tafBulletinJSONSerializer() {
        return new TAFBulletinJSONSerializer();
    }

    @Bean
    public AviMessageSpecificConverter<String, TAF> tafJSONParser() {
        return new TAFJSONParser();
    }

    @Bean
    public AviMessageSpecificConverter<String, METAR> metarJSONParser() {
        return new METARJSONParser();
    }

    @Bean
    public AviMessageSpecificConverter<String, SIGMET> sigmetJSONParser() {
        return new SIGMETJSONParser();
    }

    @Bean
    public AviMessageSpecificConverter<String, TAFBulletin> tafBulletinJSONParser() {
        return new TAFBulletinJSONParser();
    }

    @Bean
    public AviMessageSpecificConverter<String, SIGMETBulletin> sigmetBulletinJSONParser() {
        return new SIGMETBulletinJSONParser();
    }

    @Bean
    public AviMessageSpecificConverter<String, GenericMeteorologicalBulletin> genericBulletinJSONParser() {
        return new GenericMeteorologicalBulletinJSONParser();
    }


}
