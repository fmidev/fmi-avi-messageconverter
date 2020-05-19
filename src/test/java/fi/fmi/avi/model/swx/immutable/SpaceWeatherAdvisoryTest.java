package fi.fmi.avi.model.swx.immutable;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
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

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.PartialDateTime;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.PointGeometry;
import fi.fmi.avi.model.immutable.CircleByCenterPointImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.PointGeometryImpl;
import fi.fmi.avi.model.swx.AirspaceVolume;
import fi.fmi.avi.model.swx.IssuingCenter;
import fi.fmi.avi.model.swx.NextAdvisory;
import fi.fmi.avi.model.swx.SpaceWeatherAdvisoryAnalysis;
import fi.fmi.avi.model.swx.SpaceWeatherRegion;

public class SpaceWeatherAdvisoryTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @BeforeClass
    public static void setup() {
        OBJECT_MAPPER.registerModule(new Jdk8Module()).registerModule(new JavaTimeModule());
    }

    private AdvisoryNumberImpl getAdvisoryNumber() {
        AdvisoryNumberImpl.Builder advisory = AdvisoryNumberImpl.builder().setYear(2020).setSerialNumber(1);

        return advisory.build();
    }

    private NextAdvisory getNextAdvisory(boolean hasNext) {
        NextAdvisoryImpl.Builder next = NextAdvisoryImpl.builder();

        if (hasNext) {
            PartialOrCompleteTimeInstant nextAdvisoryTime = PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2020-02-27T01:00Z[UTC]"));
            next.setTime(nextAdvisoryTime);
            next.setTimeSpecifier(NextAdvisory.Type.NEXT_ADVISORY_AT);
        } else {
            next.setTimeSpecifier(NextAdvisory.Type.NO_FURTHER_ADVISORIES);
        }

        return next.build();
    }

    private List<String> getRemarks() {
        List<String> remarks = new ArrayList<>();
        remarks.add("RADIATION LVL EXCEEDED 100 PCT OF BACKGROUND LVL AT FL350 AND ABV. THE CURRENT EVENT HAS PEAKED AND LVL SLW RTN TO BACKGROUND LVL."
                + " SEE WWW.SPACEWEATHERPROVIDER.WEB");

        return remarks;
    }

    private List<SpaceWeatherAdvisoryAnalysis> getAnalyses(boolean hasObservation) {
        List<SpaceWeatherAdvisoryAnalysis> analyses = new ArrayList<>();

        int day = 27;
        int hour = 1;
        for (int i = 0; i < 5; i++) {
            SpaceWeatherAdvisoryAnalysisImpl.Builder analysis = SpaceWeatherAdvisoryAnalysisImpl.builder();

            SpaceWeatherRegionImpl.Builder region = SpaceWeatherRegionImpl.builder();

            String partialTime = "--" + day + "T" + hour + ":00Z";
            analysis.setTime(PartialOrCompleteTimeInstant.builder().setPartialTime(PartialDateTime.parse(partialTime)).build());
            region.setAirSpaceVolume(getAirspaceVolume(true));
            region.setLocationIndicator("HNH");

            analysis.setRegion(Arrays.asList(region.build(), region.setLocationIndicator("MNH").build()));

            if (i == 0 && hasObservation) {
                analysis.setAnalysisType(SpaceWeatherAdvisoryAnalysis.Type.OBSERVATION);
            } else {
                analysis.setAnalysisType(SpaceWeatherAdvisoryAnalysis.Type.FORECAST);
            }

            analysis.setNoInformationAvailable(true);
            analysis.setNoPhenomenaExpected(true);
            analysis.setTime(PartialOrCompleteTimeInstant.builder().setCompleteTime(ZonedDateTime.parse("2020-02-27T01:00Z[UTC]")));

            analyses.add(analysis.build());
        }

        return analyses;
    }

    private AirspaceVolume getAirspaceVolume(boolean isPointGeometry) {
        AirspaceVolumeImpl.Builder airspaceVolume = AirspaceVolumeImpl.builder();
        airspaceVolume.setUpperLimitReference("Reference");
        airspaceVolume.setSrsName("Dimension");
        airspaceVolume.setSrsDimension(BigInteger.valueOf(2));
        airspaceVolume.setAxisLabels(Arrays.asList("lat", "lon"));

        if (isPointGeometry) {
            PointGeometry geometry = PointGeometryImpl.builder()
                    .setPoint(Arrays.asList(-180.0, 90.0, -180.0, 60.0, 180.0, 60.0, 180.0, 90.0, -180.0, 90.0))
                    .build();
            airspaceVolume.setGeometry(geometry);
        } else {
            NumericMeasureImpl.Builder measure = NumericMeasureImpl.builder().setValue(5409.75).setUom("[nmi_i]");

            CircleByCenterPointImpl.Builder cbcp = CircleByCenterPointImpl.builder()
                    .addAllCoordinates(Arrays.asList(-16.6392, 160.9368))
                    .setRadius(measure.build())
                    .setNumarc(BigInteger.valueOf(1));

            airspaceVolume.setGeometry(cbcp.build());
        }

        NumericMeasure nm = NumericMeasureImpl.builder().setUom("uom").setValue(Double.valueOf(350)).build();
        airspaceVolume.setUpperLimit(nm);

        return airspaceVolume.build();
    }

    private IssuingCenter getIssuingCenter() {
        IssuingCenterImpl.Builder issuingCenter = IssuingCenterImpl.builder();
        issuingCenter.setName("DONLON");
        issuingCenter.setType("OTHER:SWXC");
        issuingCenter.setInterpretation("SNAPSHOT");
        return issuingCenter.build();
    }

    @Test
    public void buildSWXWithCircleByCenterPoint() throws Exception {
        NextAdvisoryImpl.Builder nextAdvisory = NextAdvisoryImpl.builder()
                .setTimeSpecifier(NextAdvisory.Type.NEXT_ADVISORY_BY)
                .setTime(PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2020-02-27T01:00Z[UTC]")));

        int day = 27;
        int hour = 1;
        String partialTime = "--" + day + "T" + hour + ":00Z";

        List<SpaceWeatherRegion> regions = new ArrayList<>();
        regions.add(SpaceWeatherRegionImpl.builder().setLocationIndicator("HNH").setAirSpaceVolume(getAirspaceVolume(false)).build());
        regions.add(SpaceWeatherRegionImpl.builder().setLocationIndicator("MNH").setAirSpaceVolume(getAirspaceVolume(false)).build());
        PartialOrCompleteTimeInstant time = PartialOrCompleteTimeInstant.builder().setPartialTime(PartialDateTime.parse(partialTime)).build();
        SpaceWeatherAdvisoryAnalysisImpl.Builder analysis = SpaceWeatherAdvisoryAnalysisImpl.builder();
        analysis.setAnalysisType(SpaceWeatherAdvisoryAnalysis.Type.FORECAST)
                .setTime(time)
                .setRegion(regions)
                .setNoPhenomenaExpected(true)
                .setNoInformationAvailable(true);

        List<SpaceWeatherAdvisoryAnalysis> analyses = new ArrayList<>();
        analyses.add(analysis.build());
        analyses.add(analysis.build());
        analyses.add(analysis.build());
        analyses.add(analysis.build());
        analyses.add(analysis.build());

        SpaceWeatherAdvisoryImpl SWXObject = SpaceWeatherAdvisoryImpl.builder()
                .setIssuingCenter(getIssuingCenter())
                .setIssueTime(PartialOrCompleteTimeInstant.builder().setCompleteTime(ZonedDateTime.parse("2020-02-27T01:00Z[UTC]")).build())
                .setPermissibleUsageReason(AviationCodeListUser.PermissibleUsageReason.TEST)
                .addAllPhenomena(Arrays.asList("HF COM MOD", "GNSS MOD"))
                .setAdvisoryNumber(getAdvisoryNumber())
                .setReplaceAdvisoryNumber(Optional.empty())
                .addAllAnalyses(analyses)
                .setRemarks(getRemarks())
                .setNextAdvisory(nextAdvisory.build())
                .build();

        Assert.assertEquals(1, SWXObject.getAdvisoryNumber().getSerialNumber());
        Assert.assertEquals(2020, SWXObject.getAdvisoryNumber().getYear());
        Assert.assertTrue(SWXObject.getAnalyses().get(0).getAnalysisType().isPresent());
        Assert.assertEquals(SpaceWeatherAdvisoryAnalysis.Type.FORECAST, SWXObject.getAnalyses().get(0).getAnalysisType().get());
        Assert.assertEquals(NextAdvisory.Type.NEXT_ADVISORY_BY, SWXObject.getNextAdvisory().getTimeSpecifier());
        Assert.assertTrue(SWXObject.getNextAdvisory().getTime().isPresent());

        final String serialized = OBJECT_MAPPER.writeValueAsString(SWXObject);
        final SpaceWeatherAdvisoryImpl deserialized = OBJECT_MAPPER.readValue(serialized, SpaceWeatherAdvisoryImpl.class);
        System.out.println(serialized);
        assertEquals(SWXObject, deserialized);
    }

    @Test
    public void buildSWXWithoutNextAdvisory() throws Exception {
        SpaceWeatherAdvisoryImpl SWXObject = SpaceWeatherAdvisoryImpl.builder()
                .setIssuingCenter(getIssuingCenter())
                .setIssueTime(PartialOrCompleteTimeInstant.builder().setCompleteTime(ZonedDateTime.parse("2020-02-27T01:00Z[UTC]")).build())
                .setPermissibleUsageReason(AviationCodeListUser.PermissibleUsageReason.TEST)
                .addAllPhenomena(Arrays.asList("HF COM MOD", "GNSS MOD"))
                .setAdvisoryNumber(getAdvisoryNumber())
                .setReplaceAdvisoryNumber(Optional.empty())
                .addAllAnalyses(getAnalyses(true))
                .setRemarks(getRemarks())
                .setNextAdvisory(getNextAdvisory(false))
                .build();

        Assert.assertEquals(1, SWXObject.getAdvisoryNumber().getSerialNumber());
        Assert.assertEquals(2020, SWXObject.getAdvisoryNumber().getYear());
        Assert.assertTrue(SWXObject.getAnalyses().get(0).getAnalysisType().isPresent());
        Assert.assertEquals(SpaceWeatherAdvisoryAnalysis.Type.OBSERVATION, SWXObject.getAnalyses().get(0).getAnalysisType().get());
        Assert.assertEquals(5, SWXObject.getAnalyses().size());
        Assert.assertFalse(SWXObject.getNextAdvisory().getTime().isPresent());
        Assert.assertEquals(NextAdvisory.Type.NO_FURTHER_ADVISORIES, SWXObject.getNextAdvisory().getTimeSpecifier());

        final String serialized = OBJECT_MAPPER.writeValueAsString(SWXObject);
        final SpaceWeatherAdvisoryImpl deserialized = OBJECT_MAPPER.readValue(serialized, SpaceWeatherAdvisoryImpl.class);

        assertEquals(SWXObject, deserialized);
    }

    @Test
    public void buildSWXWithoutObservation() throws Exception {
        SpaceWeatherAdvisoryImpl SWXObject = SpaceWeatherAdvisoryImpl.builder()
                .setIssuingCenter(getIssuingCenter())
                .setIssueTime(PartialOrCompleteTimeInstant.builder().setCompleteTime(ZonedDateTime.parse("2020-02-27T01:00Z[UTC]")).build())
                .setPermissibleUsageReason(AviationCodeListUser.PermissibleUsageReason.TEST)
                .addAllAnalyses(getAnalyses(false))
                .addAllPhenomena(Arrays.asList("HF COM MOD", "GNSS MOD"))
                .setAdvisoryNumber(getAdvisoryNumber())
                .setReplaceAdvisoryNumber(Optional.empty())
                .setRemarks(getRemarks())
                .setNextAdvisory(getNextAdvisory(false))
                .build();

        Assert.assertEquals(1, SWXObject.getAdvisoryNumber().getSerialNumber());
        Assert.assertEquals(2020, SWXObject.getAdvisoryNumber().getYear());
        Assert.assertEquals(5, SWXObject.getAnalyses().size());

        Assert.assertFalse(SWXObject.getNextAdvisory().getTime().isPresent());
        Assert.assertEquals(NextAdvisory.Type.NO_FURTHER_ADVISORIES, SWXObject.getNextAdvisory().getTimeSpecifier());

        final String serialized = OBJECT_MAPPER.writeValueAsString(SWXObject);
        final SpaceWeatherAdvisoryImpl deserialized = OBJECT_MAPPER.readValue(serialized, SpaceWeatherAdvisoryImpl.class);

        assertEquals(SWXObject, deserialized);
    }

    @Test
    public void swxSerializationTest() throws Exception {
        SpaceWeatherAdvisoryImpl SWXObject = SpaceWeatherAdvisoryImpl.builder()
                .setIssuingCenter(getIssuingCenter())
                .setIssueTime(PartialOrCompleteTimeInstant.builder().setCompleteTime(ZonedDateTime.parse("2020-02-27T01:00Z[UTC]")).build())
                .setPermissibleUsageReason(AviationCodeListUser.PermissibleUsageReason.TEST)
                .setReplaceAdvisoryNumber(getAdvisoryNumber())
                .addAllPhenomena(Arrays.asList("HF COM MOD", "GNSS MOD"))
                .setAdvisoryNumber(getAdvisoryNumber())
                .setReplaceAdvisoryNumber(Optional.empty())
                .addAllAnalyses(getAnalyses(true))
                .setRemarks(getRemarks())
                .setNextAdvisory(getNextAdvisory(true))
                .setReportStatus(AviationWeatherMessage.ReportStatus.NORMAL)
                .build();

        final String serialized = OBJECT_MAPPER.writeValueAsString(SWXObject);
        System.out.println(serialized);
        final SpaceWeatherAdvisoryImpl deserialized = OBJECT_MAPPER.readValue(serialized, SpaceWeatherAdvisoryImpl.class);

        assertEquals(SWXObject, deserialized);
    }
}
