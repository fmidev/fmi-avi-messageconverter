package fi.fmi.avi.converter.json.conf;

import fi.fmi.avi.converter.AviMessageConverter;
import fi.fmi.avi.converter.AviMessageSpecificConverter;
import fi.fmi.avi.converter.ConversionSpecification;
import fi.fmi.avi.converter.json.*;
import fi.fmi.avi.model.bulletin.GenericMeteorologicalBulletin;
import fi.fmi.avi.model.metar.METAR;
import fi.fmi.avi.model.sigmet.AIRMET;
import fi.fmi.avi.model.sigmet.SIGMET;
import fi.fmi.avi.model.sigmet.SIGMETBulletin;
import fi.fmi.avi.model.swx.amd79.SpaceWeatherAdvisoryAmd79;
import fi.fmi.avi.model.swx.amd82.SpaceWeatherAdvisoryAmd82;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.TAFBulletin;

/**
 * Java POJO and JSON conversions.
 *
 * <p><b>Modernization note (2026-08, see docs/07-modernization-plan.md):</b> this class used to be
 * a Spring {@code @Configuration} class with one {@code @Bean} method per converter. It is now a
 * plain factory with no dependency on any DI framework, so this module builds and runs the same way
 * whether or not the consuming application uses Spring, Guice, Dagger, or manual wiring.
 *
 * <p>A consuming Spring application wires these into its own context with a handful of
 * {@code @Bean} methods, e.g.:
 * <pre>{@code
 * @Configuration
 * public class MyAppConversionConfig {
 *     @Bean
 *     public AviMessageConverter aviMessageConverter() {
 *         final AviMessageConverter converter = new AviMessageConverter();
 *         JSONConverter.addTo(converter);
 *         // ... also add TAC / IWXXM converters here, once those modules follow the same pattern
 *         return converter;
 *     }
 * }
 * }</pre>
 * or, if individual {@link AviMessageSpecificConverter} beans are wanted (e.g. to compose them
 * with other formats' beans the way the pre-modernization {@code @Bean}-based configuration did),
 * declare them individually by delegating to the static factory methods below, e.g.
 * {@code @Bean public AviMessageSpecificConverter<TAF, String> tafJSONSerializer() { return JSONConverter.tafJSONSerializer(); }}
 */
public final class JSONConverter {

    private JSONConverter() {
        throw new AssertionError("JSONConverter is a static factory and is not meant to be instantiated");
    }

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
    public static final ConversionSpecification<SIGMET, String> SIGMET_POJO_TO_JSON_STRING = new ConversionSpecification<>(SIGMET.class, String.class,
            null, "SIGMET, fmi-avi-messageconverter JSON");

    /**
     * Pre-configured spec for {@link SpaceWeatherAdvisoryAmd79} to fmi-avi-messageconverter JSON SWX document String.
     */
    public static final ConversionSpecification<SpaceWeatherAdvisoryAmd79, String> SWX_AMD79_POJO_TO_JSON_STRING = new ConversionSpecification<>(SpaceWeatherAdvisoryAmd79.class, String.class,
            null, "SWX, fmi-avi-messageconverter JSON");

    /**
     * Pre-configured spec for {@link SpaceWeatherAdvisoryAmd82} to fmi-avi-messageconverter JSON SWX document String.
     */
    public static final ConversionSpecification<SpaceWeatherAdvisoryAmd82, String> SWX_AMD82_POJO_TO_JSON_STRING = new ConversionSpecification<>(SpaceWeatherAdvisoryAmd82.class, String.class,
            null, "SWX, fmi-avi-messageconverter JSON");

    /**
     * Pre-configured spec for {@link AIRMET} to IWXXM 2.1 XML format AIRMET document String.
     */
    public static final ConversionSpecification<AIRMET, String> AIRMET_POJO_TO_JSON_STRING = new ConversionSpecification<>(AIRMET.class, String.class,
            null, "SIGMET, fmi-avi-messageconverter JSON");

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
     * Pre-configured spec for fmi-avi-messageconverter JSON SWX document String to {@link SpaceWeatherAdvisoryAmd79}.
     */
    public static final ConversionSpecification<String, SpaceWeatherAdvisoryAmd79> JSON_STRING_TO_SWX_AMD79_POJO = new ConversionSpecification<>(String.class, SpaceWeatherAdvisoryAmd79.class,
            "SWX, fmi-avi-messageconverter JSON", null);

    /**
     * Pre-configured spec for fmi-avi-messageconverter JSON SWX document String to {@link SpaceWeatherAdvisoryAmd82}.
     */
    public static final ConversionSpecification<String, SpaceWeatherAdvisoryAmd82> JSON_STRING_TO_SWX_AMD82_POJO = new ConversionSpecification<>(String.class, SpaceWeatherAdvisoryAmd82.class,
            "SWX, fmi-avi-messageconverter JSON", null);

    /**
     * Pre-configured spec for fmi-avi-messageconverter JSON METAR document String to {@link METAR}.
     */
    public static final ConversionSpecification<String, METAR> JSON_STRING_TO_METAR_POJO = new ConversionSpecification<>(String.class, METAR.class,
            "METAR, fmi-avi-messageconverter JSON", null);

    /**
     * Pre-configured spec for fmi-avi-messageconverter JSON SIGMET document String to {@link METAR}.
     */
    public static final ConversionSpecification<String, SIGMET> JSON_STRING_TO_SIGMET_POJO = new ConversionSpecification<>(String.class, SIGMET.class,
            "SIGMET, fmi-avi-messageconverter JSON", null);

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
     * Pre-configured spec for IWXXM 2.1 XML format AIRMET document DOM Node to {@link AIRMET}
     */
    public static final ConversionSpecification<String, AIRMET> JSON_STRING_TO_AIRMET_POJO = new ConversionSpecification<>(String.class, AIRMET.class,
            "AIRMET, fmi-avi-messageconverter JSON", null);


    /**
     * Pre-configured spec for fmi-avi-messageconverter JSON GenericMeteorologicalBulletin document String to
     * {@link GenericMeteorologicalBulletin}.
     */
    public static final ConversionSpecification<String, GenericMeteorologicalBulletin> JSON_STRING_TO_GENERIC_BULLETIN_POJO = new ConversionSpecification<>(String.class,
            GenericMeteorologicalBulletin.class, "GenericMeteorologicalBulletin, fmi-avi-messageconverter JSON", null);

    public static AviMessageSpecificConverter<METAR, String> metarJSONSerializer() {
        return new METARJSONSerializer();
    }

    public static AviMessageSpecificConverter<TAF, String> tafJSONSerializer() {
        return new TAFJSONSerializer();
    }

    public static AviMessageSpecificConverter<SIGMET, String> sigmetJSONSerializer() {
        return new SIGMETJSONSerializer();
    }

    public static AviMessageSpecificConverter<SpaceWeatherAdvisoryAmd79, String> swxAmd79JSONSerializer() {
        return new SpaceWeatherAdvisoryAmd79JSONSerializer();
    }

    public static AviMessageSpecificConverter<SpaceWeatherAdvisoryAmd82, String> swxAmd82JSONSerializer() {
        return new SpaceWeatherAdvisoryAmd82JSONSerializer();
    }

    public static AviMessageSpecificConverter<SIGMETBulletin, String> sigmetBulletinJSONSerializer() {
        return new SIGMETBulletinJSONSerializer();
    }

    public static AviMessageSpecificConverter<GenericMeteorologicalBulletin, String> genericBulletinJSONSerializer() {
        return new GenericMeteorologicalBulletinJSONSerializer();
    }

    public static AviMessageSpecificConverter<TAFBulletin, String> tafBulletinJSONSerializer() {
        return new TAFBulletinJSONSerializer();
    }

    public static AviMessageSpecificConverter<String, TAF> tafJSONParser() {
        return new TAFJSONParser();
    }

    public static AviMessageSpecificConverter<String, METAR> metarJSONParser() {
        return new METARJSONParser();
    }

    public static AviMessageSpecificConverter<String, SIGMET> sigmetJSONParser() {
        return new SIGMETJSONParser();
    }

    public static AviMessageSpecificConverter<String, SpaceWeatherAdvisoryAmd79> swxAmd79JSONParser() {
        return new SpaceWeatherAdvisoryAmd79JSONParser();
    }

    public static AviMessageSpecificConverter<String, SpaceWeatherAdvisoryAmd82> swxAmd82JSONParser() {
        return new SpaceWeatherAdvisoryAmd82JSONParser();
    }

    public static AviMessageSpecificConverter<String, AIRMET> airmetJSONParser() {
        return new AIRMETJSONParser();
    }

    public static AviMessageSpecificConverter<String, TAFBulletin> tafBulletinJSONParser() {
        return new TAFBulletinJSONParser();
    }

    public static AviMessageSpecificConverter<String, SIGMETBulletin> sigmetBulletinJSONParser() {
        return new SIGMETBulletinJSONParser();
    }

    public static AviMessageSpecificConverter<String, GenericMeteorologicalBulletin> genericBulletinJSONParser() {
        return new GenericMeteorologicalBulletinJSONParser();
    }

    public static AviMessageSpecificConverter<AIRMET, String> airmetJSONSerializer() {
        return new AIRMETJSONSerializer();
    }

    /**
     * Registers every JSON conversion spec declared above onto {@code target}, using
     * {@link AviMessageConverter#setMessageSpecificConverter}. This is the plain-Java equivalent of
     * what the pre-modernization {@code JSONTestConfiguration} Spring {@code @Bean} method did, and
     * is the method a non-Spring consumer (or a Spring {@code @Bean} method, per the class javadoc)
     * calls to get a fully wired {@link AviMessageConverter} for every message type this module
     * supports in JSON.
     *
     * @param target the converter to register the JSON conversions on; returned for chaining
     * @return {@code target}, for convenience
     */
    public static AviMessageConverter addTo(final AviMessageConverter target) {
        target.setMessageSpecificConverter(JSON_STRING_TO_TAF_POJO, tafJSONParser());
        target.setMessageSpecificConverter(JSON_STRING_TO_METAR_POJO, metarJSONParser());
        target.setMessageSpecificConverter(JSON_STRING_TO_SIGMET_POJO, sigmetJSONParser());
        target.setMessageSpecificConverter(JSON_STRING_TO_AIRMET_POJO, airmetJSONParser());
        target.setMessageSpecificConverter(JSON_STRING_TO_SWX_AMD79_POJO, swxAmd79JSONParser());
        target.setMessageSpecificConverter(JSON_STRING_TO_SWX_AMD82_POJO, swxAmd82JSONParser());
        target.setMessageSpecificConverter(JSON_STRING_TO_TAF_BULLETIN_POJO, tafBulletinJSONParser());
        target.setMessageSpecificConverter(JSON_STRING_TO_SIGMET_BULLETIN_POJO, sigmetBulletinJSONParser());
        target.setMessageSpecificConverter(JSON_STRING_TO_GENERIC_BULLETIN_POJO, genericBulletinJSONParser());

        target.setMessageSpecificConverter(TAF_POJO_TO_JSON_STRING, tafJSONSerializer());
        target.setMessageSpecificConverter(METAR_POJO_TO_JSON_STRING, metarJSONSerializer());
        target.setMessageSpecificConverter(SIGMET_POJO_TO_JSON_STRING, sigmetJSONSerializer());
        target.setMessageSpecificConverter(AIRMET_POJO_TO_JSON_STRING, airmetJSONSerializer());
        target.setMessageSpecificConverter(SWX_AMD79_POJO_TO_JSON_STRING, swxAmd79JSONSerializer());
        target.setMessageSpecificConverter(SWX_AMD82_POJO_TO_JSON_STRING, swxAmd82JSONSerializer());
        target.setMessageSpecificConverter(TAF_BULLETIN_POJO_TO_JSON_STRING, tafBulletinJSONSerializer());
        target.setMessageSpecificConverter(SIGMET_BULLETIN_POJO_TO_JSON_STRING, sigmetBulletinJSONSerializer());
        target.setMessageSpecificConverter(GENERIC_METEOROLOGICAL_BULLETIN_POJO_TO_JSON_STRING, genericBulletinJSONSerializer());
        return target;
    }

    /**
     * Convenience method: builds a new {@link AviMessageConverter} wired with every JSON conversion
     * this module supports. Equivalent to {@code JSONConverter.addTo(new AviMessageConverter())}.
     */
    public static AviMessageConverter createAviMessageConverter() {
        return addTo(new AviMessageConverter());
    }
}
