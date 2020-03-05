package fi.fmi.avi.converter.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.unitils.thirdparty.org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import fi.fmi.avi.converter.AviMessageConverter;
import fi.fmi.avi.converter.AviMessageSpecificConverter;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.converter.json.conf.JSONConverter;
import fi.fmi.avi.model.PartialDateTime;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.PhenomenonGeometryWithHeight;
import fi.fmi.avi.model.SWX.AdvisoryNumberImpl;
import fi.fmi.avi.model.SWX.NextAdvisory;
import fi.fmi.avi.model.SWX.NextAdvisoryImpl;
import fi.fmi.avi.model.SWX.SWX;
import fi.fmi.avi.model.SWX.SWXGeometry;
import fi.fmi.avi.model.SWX.immutable.SWXGeometryImpl;
import fi.fmi.avi.model.SWX.immutable.SWXImpl;
import fi.fmi.avi.model.immutable.PhenomenonGeometryWithHeightImpl;
import fi.fmi.avi.model.immutable.TacOrGeoGeometryImpl;
import fi.fmi.avi.model.sigmet.SIGMET;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JSONSWXTestConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class JSONSWXConverterTest {

    @Autowired
    private AviMessageConverter converter;

    private final String NO_ADVISORIES = "NO FURTHER ADVISORIES";

    //@Before
    public void setup() {
        AviMessageSpecificConverter<SWX, String> swxJSONSerializer = new SWXJSONSerializer();
        AviMessageSpecificConverter<String, SWX> swxJSONParser = new SWXJSONParser();

        AviMessageConverter p = new AviMessageConverter();
        p.setMessageSpecificConverter(JSONConverter.JSON_STRING_TO_SWX_POJO, swxJSONParser);
        p.setMessageSpecificConverter(JSONConverter.SWX_POJO_TO_JSON_STRING, swxJSONSerializer);
        converter =  p;
    }

    private SWXGeometry getSWXGeometry() {
        List<String> regions = new ArrayList<>(Arrays.asList("HNH", "HSH"));

        SWXGeometryImpl.Builder spaceGeometry = SWXGeometryImpl.builder()
                .setEasternLatitudeBand(18000)
                .setWesternLatitudeBand(18000)
                .addAllLatitudeRegions(regions);

        return spaceGeometry.build();
    }

    private AdvisoryNumberImpl getAdvisoryNumber() {
        AdvisoryNumberImpl.Builder advisory = AdvisoryNumberImpl.builder().setYear(2020).setSerialNumber(1);

        return advisory.build();
    }

    private NextAdvisory getNextAdvisory(boolean hasNext) {
        NextAdvisoryImpl.Builder next = NextAdvisoryImpl.builder();

        if (hasNext) {
            PartialOrCompleteTimeInstant nextAdvisoryTime = PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2020-02-27T01:00Z[UTC]"));
            next.nextAdvisory(nextAdvisoryTime);
        } else {
            next.noFurtherAdvisory(NO_ADVISORIES);
        }

        return next.build();
    }

    private List<String> getRemarks() {
        List<String> remarks = new ArrayList<>();
        remarks.add("RADIATION LVL EXCEEDED 100 PCT OF BACKGROUND LVL AT FL350 AND ABV. THE CURRENT EVENT HAS PEAKED AND LVL SLW RTN TO BACKGROUND LVL."
                + " SEE WWW.SPACEWEATHERPROVIDER.WEB");

        return remarks;
    }

    private List<PhenomenonGeometryWithHeight> getForecasts(boolean hasObservation) {
        int numberOfForecasts = (hasObservation) ? 4 : 5;
        List<PhenomenonGeometryWithHeight> forecasts = new ArrayList<>();

        int day = 27;
        int hour = 1;

        for (int i = 0; i < numberOfForecasts; i++) {

            String partialTime = "--" + day + "T" + hour + ":00Z";
            forecasts.add(getForecast(partialTime));
            hour += 6;
            if (hour >= 24) {
                day += 1;
                hour = hour % 24;
            }
        }

        return forecasts;
    }

    private PhenomenonGeometryWithHeight getForecast(String partialTime) {
        PhenomenonGeometryWithHeightImpl.Builder phenomenon = new PhenomenonGeometryWithHeightImpl.Builder().setTime(
                PartialOrCompleteTimeInstant.of(PartialDateTime.parse(partialTime)))
                .setGeometry(TacOrGeoGeometryImpl.of(getSWXGeometry()))
                .setApproximateLocation(false);

        return phenomenon.build();
    }

    @Test
    public void testSWXSerialization() throws Exception {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new Jdk8Module());
        om.registerModule(new JavaTimeModule());


        InputStream is = JSONSigmetConverterTest.class.getResourceAsStream("swx1.json");
        Objects.requireNonNull(is);

        String reference = IOUtils.toString(is,"UTF-8");

        SWXImpl SWXObject = SWXImpl.builder()
                .setTranslated(false)
                .setIssueTime(PartialOrCompleteTimeInstant.builder().setCompleteTime(ZonedDateTime.parse("2020-02-27T01:00Z[UTC]")).build())
                .setStatus(SWX.STATUS.TEST)
                .setTranslationCentreName("DONLON")
                .addAllWeatherEffects(Arrays.asList("HF COM MOD", "GNSS MOD"))
                .setAdvisoryNumber(getAdvisoryNumber())
                .setReplacementAdvisoryNumber(Optional.empty())
                .setObservation(getForecast("--27T00:00Z"))
                .addAllForecasts(getForecasts(true))
                .setRemarks(getRemarks())
                .setNextAdvisory(getNextAdvisory(true))
                .build();


        ConversionResult<String> result = converter.convertMessage(SWXObject, JSONConverter.SWX_POJO_TO_JSON_STRING, ConversionHints.EMPTY);
        assertTrue(ConversionResult.Status.SUCCESS == result.getStatus());
        assertTrue(result.getConvertedMessage().isPresent());

        JsonNode refRoot=om.readTree(reference);
        JsonNode convertedRoot=om.readTree(result.getConvertedMessage().get());
        System.err.println("EQUALS: "+refRoot.equals(convertedRoot));
        assertEquals("constructed and parsed tree not equal", refRoot, convertedRoot);
    }
}
