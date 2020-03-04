package fi.fmi.avi.model.SWX.immutable;

import static org.junit.Assert.assertEquals;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import fi.fmi.avi.model.PartialDateTime;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.PhenomenonGeometryWithHeight;
import fi.fmi.avi.model.SWX.AdvisoryNumberImpl;
import fi.fmi.avi.model.SWX.NextAdvisory;
import fi.fmi.avi.model.SWX.NextAdvisoryImpl;
import fi.fmi.avi.model.SWX.SWX;
import fi.fmi.avi.model.SWX.SWXGeometry;
import fi.fmi.avi.model.immutable.PhenomenonGeometryWithHeightImpl;
import fi.fmi.avi.model.immutable.TacOrGeoGeometryImpl;

public class SWXTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final String NO_ADVISORIES = "NO FURTHER ADVISORIES";

    @BeforeClass
    public static void setup() {
        OBJECT_MAPPER.registerModule(new Jdk8Module()).registerModule(new JavaTimeModule());
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
    public void buildSWXWithNextAdvisory() throws Exception {
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

        Assert.assertEquals(1, SWXObject.getAdvisoryNumber().getSerialNumber());
        Assert.assertEquals(2020, SWXObject.getAdvisoryNumber().getYear());
        Assert.assertTrue(SWXObject.getObservation().isPresent());
        Assert.assertEquals(4, SWXObject.getForecasts().size());
        Assert.assertTrue(SWXObject.getNextAdvisory().nextAdvisory().isPresent());
        Assert.assertFalse(SWXObject.getNextAdvisory().noFurtherAdvisory().isPresent());

        final String serialized = OBJECT_MAPPER.writeValueAsString(SWXObject);
        final SWXImpl deserialized = OBJECT_MAPPER.readValue(serialized, SWXImpl.class);

        assertEquals(SWXObject, deserialized);
    }

    @Test
    public void buildSWXWithoutNextAdvisory() throws Exception {
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
                .setNextAdvisory(getNextAdvisory(false))
                .build();

        Assert.assertEquals(1, SWXObject.getAdvisoryNumber().getSerialNumber());
        Assert.assertEquals(2020, SWXObject.getAdvisoryNumber().getYear());
        Assert.assertTrue(SWXObject.getObservation().isPresent());
        Assert.assertEquals(4, SWXObject.getForecasts().size());
        Assert.assertFalse(SWXObject.getNextAdvisory().nextAdvisory().isPresent());
        Assert.assertEquals(NO_ADVISORIES, SWXObject.getNextAdvisory().noFurtherAdvisory().get());

        final String serialized = OBJECT_MAPPER.writeValueAsString(SWXObject);
        final SWXImpl deserialized = OBJECT_MAPPER.readValue(serialized, SWXImpl.class);

        assertEquals(SWXObject, deserialized);
    }

    @Test
    public void buildSWXWithoutObservation() throws Exception {
        SWXImpl SWXObject = SWXImpl.builder()
                .setTranslated(false)
                .setIssueTime(PartialOrCompleteTimeInstant.builder().setCompleteTime(ZonedDateTime.parse("2020-02-27T01:00Z[UTC]")).build())
                .setStatus(SWX.STATUS.TEST)
                .addAllWeatherEffects(Arrays.asList("HF COM MOD", "GNSS MOD"))
                .setTranslationCentreName("DONLON")
                .setAdvisoryNumber(getAdvisoryNumber())
                .setReplacementAdvisoryNumber(Optional.empty())
                .addAllForecasts(getForecasts(false))
                .setRemarks(getRemarks())
                .setNextAdvisory(getNextAdvisory(false))
                .build();

        Assert.assertEquals(1, SWXObject.getAdvisoryNumber().getSerialNumber());
        Assert.assertEquals(2020, SWXObject.getAdvisoryNumber().getYear());
        Assert.assertFalse(SWXObject.getObservation().isPresent());
        Assert.assertEquals(5, SWXObject.getForecasts().size());
        Assert.assertFalse(SWXObject.getNextAdvisory().nextAdvisory().isPresent());
        Assert.assertEquals(NO_ADVISORIES, SWXObject.getNextAdvisory().noFurtherAdvisory().get());

        final String serialized = OBJECT_MAPPER.writeValueAsString(SWXObject);
        final SWXImpl deserialized = OBJECT_MAPPER.readValue(serialized, SWXImpl.class);

        assertEquals(SWXObject, deserialized);
    }

    @Test
    public void swxSerializationTest() throws Exception {
        SWXImpl SWXObject = SWXImpl.builder()
                .setTranslated(false)
                .setIssueTime(PartialOrCompleteTimeInstant.builder().setCompleteTime(ZonedDateTime.parse("2020-02-27T01:00Z[UTC]")).build())
                .setStatus(SWX.STATUS.TEST)
                .setReplacementAdvisoryNumber(getAdvisoryNumber())
                .setTranslationCentreName("DONLON")
                .addAllWeatherEffects(Arrays.asList("HF COM MOD", "GNSS MOD"))
                .setAdvisoryNumber(getAdvisoryNumber())
                .setReplacementAdvisoryNumber(Optional.empty())
                .setObservation(getForecast("--27T00:00Z"))
                .addAllForecasts(getForecasts(true))
                .setRemarks(getRemarks())
                .setNextAdvisory(getNextAdvisory(true))
                .build();

        final String serialized = OBJECT_MAPPER.writeValueAsString(SWXObject);
        final SWXImpl deserialized = OBJECT_MAPPER.readValue(serialized, SWXImpl.class);

        assertEquals(SWXObject, deserialized);
    }
}
