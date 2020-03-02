package fi.fmi.avi.model.SWX.immutable;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import fi.fmi.avi.model.PartialDateTime;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.SWX.AdvisoryNumber;
import fi.fmi.avi.model.SWX.AdvisoryNumberImpl;
import fi.fmi.avi.model.SWX.NextAdvisory;
import fi.fmi.avi.model.SWX.NextAdvisoryImpl;
import fi.fmi.avi.model.SWX.SWX;
import fi.fmi.avi.model.SWX.SWXGeometry;
import fi.fmi.avi.model.immutable.TacOrGeoGeometryImpl;
import fi.fmi.avi.model.PhenomenonGeometryWithHeight;
import fi.fmi.avi.model.immutable.PhenomenonGeometryWithHeightImpl;

public class SWXTest {
    private final String NO_ADVISORIES = "NO FURTHER ADVISORIES";

    private SWXGeometry getSWXGeometry() {
        List<String> regions = new ArrayList<>(Arrays.asList("HNH", "HSH"));

        SWXGeometryImpl.Builder spaceGeometry = SWXGeometryImpl.builder()
                .setEasternLatitudeBand(18000)
                .setWesternLatitudeBand(18000)
                .addAllLatitudeRegions(regions);

        return spaceGeometry.build();
    }

    private AdvisoryNumber getAdvisoryNumber() {
        AdvisoryNumberImpl.Builder advisory = AdvisoryNumberImpl.builder().setYear(2020).setSerialNumber(1);

        return advisory.build();
    }

    private NextAdvisory getNextAdvisory(boolean hasNext) {
        NextAdvisoryImpl.Builder next = NextAdvisoryImpl.builder();

        if (hasNext) {
            PartialOrCompleteTimeInstant nextAdvisoryTime = PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2020-02-27T01:00Z"));
            next.nextAdvisory(nextAdvisoryTime);
        } else {
            next.noFurtherAdvisory(NO_ADVISORIES);
        }

        return next.build();
    }

    private List<String> getRemarks() {
        List<String> remarks = new ArrayList<>();
        remarks.add(
                "RADIATION LVL EXCEEDED 100 PCT OF BACKGROUND LVL AT FL350 AND ABV. THE CURRENT EVENT HAS PEAKED AND LVL SLW RTN TO BACKGROUND LVL. SEE WWW.SPACEWEATHERPROVIDER.WEB\n"
                        + "NO FURTHER ADVISORIES");

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
    public void buildSWXWithNextAdvisory() {
        SWXImpl SWXObject = SWXImpl.builder()
                .setTranslated(false)
                .setIssueTime(PartialOrCompleteTimeInstant.builder().setCompleteTime(ZonedDateTime.parse("2020-02-27T01:00Z")).build())
                .setStatus(SWX.STATUS.TEST)
                .setTranslationCentreName("DONLON")
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
    }

    @Test
    public void buildSWXWithoutNextAdvisory() {
        SWXImpl SWXObject = SWXImpl.builder()
                .setTranslated(false)
                .setIssueTime(PartialOrCompleteTimeInstant.builder().setCompleteTime(ZonedDateTime.parse("2020-02-27T01:00Z")).build())
                .setStatus(SWX.STATUS.TEST)
                .setTranslationCentreName("DONLON")
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
    }

    @Test
    public void buildSWXWithoutObservation() {
        SWXImpl SWXObject = SWXImpl.builder()
                .setTranslated(false)
                .setIssueTime(PartialOrCompleteTimeInstant.builder().setCompleteTime(ZonedDateTime.parse("2020-02-27T01:00Z")).build())
                .setStatus(SWX.STATUS.TEST)
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
    }
}
