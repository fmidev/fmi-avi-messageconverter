package fi.fmi.avi.model.swx.amd82.immutable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.ImmutableList;
import fi.fmi.avi.model.*;
import fi.fmi.avi.model.immutable.CircleByCenterPointImpl;
import fi.fmi.avi.model.immutable.CoordinateReferenceSystemImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.PolygonGeometryImpl;
import fi.fmi.avi.model.swx.amd82.*;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.Assert.*;

public class SpaceWeatherAdvisoryAmd82Test {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @BeforeClass
    public static void setup() {
        OBJECT_MAPPER.registerModule(new Jdk8Module()).registerModule(new JavaTimeModule());
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static ZonedDateTime nullableCompleteTime(final Optional<PartialOrCompleteTimeInstant> partialOrCompleteTimeInstant) {
        return partialOrCompleteTimeInstant.flatMap(PartialOrCompleteTimeInstant::getCompleteTime).orElse(null);
    }

    private static ZonedDateTime nullableCompleteTime(final PartialOrCompleteTimeInstant partialOrCompleteTimeInstant) {
        return partialOrCompleteTimeInstant.getCompleteTime().orElse(null);
    }

    private static AdvisoryNumberImpl getAdvisoryNumber() {
        return AdvisoryNumberImpl.builder().setYear(2020).setSerialNumber(1).build();
    }

    private static NextAdvisory getNextAdvisory(final boolean hasNext) {
        final NextAdvisoryImpl.Builder next = NextAdvisoryImpl.builder();

        if (hasNext) {
            final PartialOrCompleteTimeInstant nextAdvisoryTime = PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2020-02-27T01:00Z[UTC]"));
            next.setTime(nextAdvisoryTime);
            next.setTimeSpecifier(NextAdvisory.Type.NEXT_ADVISORY_AT);
        } else {
            next.setTimeSpecifier(NextAdvisory.Type.NO_FURTHER_ADVISORIES);
        }

        return next.build();
    }

    private static List<String> getRemarks() {
        final List<String> remarks = new ArrayList<>();
        remarks.add("RADIATION LVL EXCEEDED 100 PCT OF BACKGROUND LVL AT FL350 AND ABV. THE CURRENT EVENT HAS PEAKED AND LVL SLW RTN TO BACKGROUND LVL."
                + " SEE WWW.SPACEWEATHERPROVIDER.WEB");

        return remarks;
    }

    private static List<AdvisoryNumber> getReplacementNumbers(final int... serials) {
        return Arrays.stream(serials)
                .mapToObj(serial -> AdvisoryNumberImpl.builder()
                        .setYear(2020)
                        .setSerialNumber(serial)
                        .build())
                .collect(ImmutableList.toImmutableList());
    }

    private List<SpaceWeatherAdvisoryAnalysis> getAnalyses(final boolean hasObservation) {
        final List<SpaceWeatherAdvisoryAnalysis> analyses = new ArrayList<>();

        final int day = 27;
        final int hour = 1;
        for (int i = 0; i < 5; i++) {
            final SpaceWeatherAdvisoryAnalysisImpl.Builder analysis = SpaceWeatherAdvisoryAnalysisImpl.builder();

            final SpaceWeatherRegionImpl.Builder region = SpaceWeatherRegionImpl.builder();

            analysis.setTime(PartialOrCompleteTimeInstant.builder().setPartialTime(PartialDateTime.ofDayHour(day, hour + i)).build());
            region.setAirSpaceVolume(getAirspaceVolume(true));
            region.setLocationIndicator(SpaceWeatherRegion.SpaceWeatherLocation.HIGH_NORTHERN_HEMISPHERE);

            analysis.addAllRegions(
                    Arrays.asList(region.build(), region.setLocationIndicator(SpaceWeatherRegion.SpaceWeatherLocation.MIDDLE_NORTHERN_HEMISPHERE).build()));

            if (i == 0 && hasObservation) {
                analysis.setAnalysisType(SpaceWeatherAdvisoryAnalysis.Type.OBSERVATION);
            } else {
                analysis.setAnalysisType(SpaceWeatherAdvisoryAnalysis.Type.FORECAST);
            }

            analysis.setNilPhenomenonReason(SpaceWeatherAdvisoryAnalysis.NilPhenomenonReason.NO_INFORMATION_AVAILABLE);
            analyses.add(analysis.build());
        }

        return analyses;
    }

    private AirspaceVolume getAirspaceVolume(final boolean isPointGeometry) {
        final AirspaceVolumeImpl.Builder airspaceVolume = AirspaceVolumeImpl.builder();
        airspaceVolume.setUpperLimitReference("Reference");

        if (isPointGeometry) {
            final PolygonGeometry geometry = PolygonGeometryImpl.builder()
                    .addAllExteriorRingPositions(Arrays.asList(-180.0, 90.0, -180.0, 60.0, 180.0, 60.0, 180.0, 90.0, -180.0, 90.0))
                    .setCrs(CoordinateReferenceSystemImpl.wgs84())
                    .build();
            airspaceVolume.setHorizontalProjection(geometry);
        } else {
            final NumericMeasureImpl.Builder measure = NumericMeasureImpl.builder().setValue(5409.75).setUom("[nmi_i]");

            final CircleByCenterPointImpl.Builder cbcp = CircleByCenterPointImpl.builder()
                    .addAllCenterPointCoordinates(Arrays.asList(-16.6392, 160.9368))
                    .setRadius(measure.build())
                    .setCrs(CoordinateReferenceSystemImpl.wgs84());

            airspaceVolume.setHorizontalProjection(cbcp.build());
        }

        final NumericMeasure nm = NumericMeasureImpl.builder().setUom("uom").setValue(350.0).build();
        airspaceVolume.setUpperLimit(nm);

        return airspaceVolume.build();
    }

    private IssuingCenter getIssuingCenter() {
        final IssuingCenterImpl.Builder issuingCenter = IssuingCenterImpl.builder();
        issuingCenter.setName("DONLON");
        issuingCenter.setType("OTHER:SWXC");
        return issuingCenter.build();
    }

    @Test
    public void buildSWXWithCircleByCenterPoint() throws Exception {
        final NextAdvisoryImpl.Builder nextAdvisory = NextAdvisoryImpl.builder()
                .setTimeSpecifier(NextAdvisory.Type.NEXT_ADVISORY_BY)
                .setTime(PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2020-02-27T01:00Z[UTC]")));

        final int day = 27;
        final int hour = 1;

        final List<SpaceWeatherRegion> regions = new ArrayList<>();
        regions.add(SpaceWeatherRegionImpl.builder()
                .setLocationIndicator(SpaceWeatherRegion.SpaceWeatherLocation.HIGH_NORTHERN_HEMISPHERE)
                .setAirSpaceVolume(getAirspaceVolume(false))
                .build());
        regions.add(SpaceWeatherRegionImpl.builder()
                .setLocationIndicator(SpaceWeatherRegion.SpaceWeatherLocation.MIDDLE_NORTHERN_HEMISPHERE)
                .setAirSpaceVolume(getAirspaceVolume(false))
                .build());
        final PartialOrCompleteTimeInstant time = PartialOrCompleteTimeInstant.builder().setPartialTime(PartialDateTime.ofDayHour(day, hour)).build();
        final SpaceWeatherAdvisoryAnalysisImpl.Builder analysis = SpaceWeatherAdvisoryAnalysisImpl.builder();
        analysis.setAnalysisType(SpaceWeatherAdvisoryAnalysis.Type.FORECAST)
                .setTime(time)
                .addAllRegions(regions)
                .setNilPhenomenonReason(SpaceWeatherAdvisoryAnalysis.NilPhenomenonReason.NO_INFORMATION_AVAILABLE);

        final List<SpaceWeatherAdvisoryAnalysis> analyses = new ArrayList<>();
        analyses.add(analysis.build());
        analyses.add(analysis.build());
        analyses.add(analysis.build());
        analyses.add(analysis.build());
        analyses.add(analysis.build());

        final SpaceWeatherAdvisoryAmd82Impl advisory = SpaceWeatherAdvisoryAmd82Impl.builder()
                .setIssuingCenter(getIssuingCenter())
                .setIssueTime(PartialOrCompleteTimeInstant.builder().setCompleteTime(ZonedDateTime.parse("2020-02-27T01:00Z[UTC]")).build())
                .setPermissibleUsageReason(AviationCodeListUser.PermissibleUsageReason.TEST)
                .addAllPhenomena(Arrays.asList(SpaceWeatherPhenomenon.fromWMOCodeListValue("http://codes.wmo.int/49-2/SpaceWxPhenomena/HF_COM_MOD"),
                        SpaceWeatherPhenomenon.fromWMOCodeListValue("http://codes.wmo.int/49-2/SpaceWxPhenomena/GNSS_MOD")))
                .setAdvisoryNumber(getAdvisoryNumber())
                .addAllAnalyses(analyses)
                .setRemarks(getRemarks())
                .setNextAdvisory(nextAdvisory.build())
                .build();

        Assert.assertEquals(1, advisory.getAdvisoryNumber().getSerialNumber());
        Assert.assertEquals(2020, advisory.getAdvisoryNumber().getYear());
        Assert.assertEquals(SpaceWeatherAdvisoryAnalysis.Type.FORECAST, advisory.getAnalyses().get(0).getAnalysisType());
        Assert.assertEquals(NextAdvisory.Type.NEXT_ADVISORY_BY, advisory.getNextAdvisory().getTimeSpecifier());
        Assert.assertTrue(advisory.getNextAdvisory().getTime().isPresent());

        final String serialized = OBJECT_MAPPER.writeValueAsString(advisory);
        final SpaceWeatherAdvisoryAmd82Impl deserialized = OBJECT_MAPPER.readValue(serialized, SpaceWeatherAdvisoryAmd82Impl.class);
        assertEquals(advisory, deserialized);
    }

    @Test
    public void buildSWXWithoutNextAdvisory() throws Exception {
        final SpaceWeatherAdvisoryAmd82Impl advisory = SpaceWeatherAdvisoryAmd82Impl.builder()
                .setIssuingCenter(getIssuingCenter())
                .setIssueTime(PartialOrCompleteTimeInstant.builder().setCompleteTime(ZonedDateTime.parse("2020-02-27T01:00Z[UTC]")).build())
                .setPermissibleUsageReason(AviationCodeListUser.PermissibleUsageReason.TEST)
                .addAllPhenomena(Arrays.asList(SpaceWeatherPhenomenon.fromWMOCodeListValue("http://codes.wmo.int/49-2/SpaceWxPhenomena/HF_COM_MOD"),
                        SpaceWeatherPhenomenon.fromWMOCodeListValue("http://codes.wmo.int/49-2/SpaceWxPhenomena/GNSS_MOD")))
                .setAdvisoryNumber(getAdvisoryNumber())
                .addAllAnalyses(getAnalyses(true))
                .setRemarks(getRemarks())
                .setNextAdvisory(getNextAdvisory(false))
                .build();

        Assert.assertEquals(1, advisory.getAdvisoryNumber().getSerialNumber());
        Assert.assertEquals(2020, advisory.getAdvisoryNumber().getYear());
        Assert.assertEquals(SpaceWeatherAdvisoryAnalysis.Type.OBSERVATION, advisory.getAnalyses().get(0).getAnalysisType());
        Assert.assertEquals(5, advisory.getAnalyses().size());
        Assert.assertFalse(advisory.getNextAdvisory().getTime().isPresent());
        Assert.assertEquals(NextAdvisory.Type.NO_FURTHER_ADVISORIES, advisory.getNextAdvisory().getTimeSpecifier());

        final String serialized = OBJECT_MAPPER.writeValueAsString(advisory);
        final SpaceWeatherAdvisoryAmd82Impl deserialized = OBJECT_MAPPER.readValue(serialized, SpaceWeatherAdvisoryAmd82Impl.class);

        assertEquals(advisory, deserialized);
    }

    @Test
    public void buildSWXWithoutObservation() throws Exception {
        final SpaceWeatherAdvisoryAmd82Impl advisory = SpaceWeatherAdvisoryAmd82Impl.builder()
                .setIssuingCenter(getIssuingCenter())
                .setIssueTime(PartialOrCompleteTimeInstant.builder().setCompleteTime(ZonedDateTime.parse("2020-02-27T01:00Z[UTC]")).build())
                .setPermissibleUsageReason(AviationCodeListUser.PermissibleUsageReason.TEST)
                .addAllAnalyses(getAnalyses(false))
                .addAllPhenomena(Arrays.asList(SpaceWeatherPhenomenon.fromWMOCodeListValue("http://codes.wmo.int/49-2/SpaceWxPhenomena/HF_COM_MOD"),
                        SpaceWeatherPhenomenon.fromWMOCodeListValue("http://codes.wmo.int/49-2/SpaceWxPhenomena/GNSS_MOD")))
                .setAdvisoryNumber(getAdvisoryNumber())
                .setRemarks(getRemarks())
                .setNextAdvisory(getNextAdvisory(false))
                .build();

        Assert.assertEquals(1, advisory.getAdvisoryNumber().getSerialNumber());
        Assert.assertEquals(2020, advisory.getAdvisoryNumber().getYear());
        Assert.assertEquals(5, advisory.getAnalyses().size());

        Assert.assertFalse(advisory.getNextAdvisory().getTime().isPresent());
        Assert.assertEquals(NextAdvisory.Type.NO_FURTHER_ADVISORIES, advisory.getNextAdvisory().getTimeSpecifier());

        final String serialized = OBJECT_MAPPER.writeValueAsString(advisory);
        final SpaceWeatherAdvisoryAmd82Impl deserialized = OBJECT_MAPPER.readValue(serialized, SpaceWeatherAdvisoryAmd82Impl.class);

        assertEquals(advisory, deserialized);
    }

    @Test
    public void swxSerializationTest() throws Exception {
        final SpaceWeatherAdvisoryAmd82Impl advisory = SpaceWeatherAdvisoryAmd82Impl.builder()
                .setIssuingCenter(getIssuingCenter())
                .setIssueTime(PartialOrCompleteTimeInstant.builder().setCompleteTime(ZonedDateTime.parse("2020-02-27T01:00Z[UTC]")).build())
                .setPermissibleUsageReason(AviationCodeListUser.PermissibleUsageReason.TEST)
                .setAdvisoryNumber(getAdvisoryNumber())
                .addAllPhenomena(Arrays.asList(SpaceWeatherPhenomenon.fromWMOCodeListValue("http://codes.wmo.int/49-2/SpaceWxPhenomena/HF_COM_MOD"),
                        SpaceWeatherPhenomenon.fromWMOCodeListValue("http://codes.wmo.int/49-2/SpaceWxPhenomena/GNSS_MOD")))

                .addAllAnalyses(getAnalyses(true))
                .setRemarks(getRemarks())
                .setNextAdvisory(getNextAdvisory(true))
                .setReportStatus(AviationWeatherMessage.ReportStatus.NORMAL)
                .build();

        final String serialized = OBJECT_MAPPER.writeValueAsString(advisory);
        final SpaceWeatherAdvisoryAmd82Impl deserialized = OBJECT_MAPPER.readValue(serialized, SpaceWeatherAdvisoryAmd82Impl.class);

        assertEquals(advisory, deserialized);
    }

    @Test
    public void swxPartialTimeCompletionTest() {
        final NextAdvisory partialNextAdvisory = NextAdvisoryImpl.builder()//
                .setTimeSpecifier(NextAdvisory.Type.NEXT_ADVISORY_AT)//
                .setTime(PartialOrCompleteTimeInstant.builder().setPartialTime(PartialDateTime.ofHour(1)).build())//
                .build();

        final SpaceWeatherAdvisoryAmd82Impl advisory = SpaceWeatherAdvisoryAmd82Impl.builder()
                .setIssuingCenter(getIssuingCenter())
                .setIssueTime(PartialOrCompleteTimeInstant.builder().setPartialTime(PartialDateTime.ofDayHourMinute(27, 1, 31)).build())
                .setPermissibleUsageReason(AviationCodeListUser.PermissibleUsageReason.TEST)
                .addAllPhenomena(Arrays.asList(SpaceWeatherPhenomenon.fromWMOCodeListValue("http://codes.wmo.int/49-2/SpaceWxPhenomena/HF_COM_MOD"),
                        SpaceWeatherPhenomenon.fromWMOCodeListValue("http://codes.wmo.int/49-2/SpaceWxPhenomena/GNSS_MOD")))
                .setAdvisoryNumber(getAdvisoryNumber())
                .addAllAnalyses(getAnalyses(true))
                .setRemarks(getRemarks())
                .setNextAdvisory(partialNextAdvisory)
                .setReportStatus(AviationWeatherMessage.ReportStatus.NORMAL)
                .build();

        final ZonedDateTime referenceTime = ZonedDateTime.parse("2020-02-27T00:00Z");
        final SpaceWeatherAdvisoryAmd82 completedAdvisory = advisory.toBuilder().withAllTimesComplete(referenceTime).build();
        assertEquals("issueTime", ZonedDateTime.parse("2020-02-27T01:31Z"), nullableCompleteTime(completedAdvisory.getIssueTime()));
        assertEquals("nextAdvisory", ZonedDateTime.parse("2020-02-28T01:00Z"), nullableCompleteTime(completedAdvisory.getNextAdvisory().getTime()));
        final Iterator<SpaceWeatherAdvisoryAnalysis> completedAnalyses = completedAdvisory.getAnalyses().iterator();
        assertEquals("observation", ZonedDateTime.parse("2020-02-27T01:00Z"), nullableCompleteTime(completedAnalyses.next().getTime()));
        assertEquals("forecast +6", ZonedDateTime.parse("2020-02-27T02:00Z"), nullableCompleteTime(completedAnalyses.next().getTime()));
        assertEquals("forecast +12", ZonedDateTime.parse("2020-02-27T03:00Z"), nullableCompleteTime(completedAnalyses.next().getTime()));
        assertEquals("forecast +18", ZonedDateTime.parse("2020-02-27T04:00Z"), nullableCompleteTime(completedAnalyses.next().getTime()));
        assertEquals("forecast +24", ZonedDateTime.parse("2020-02-27T05:00Z"), nullableCompleteTime(completedAnalyses.next().getTime()));
        assertFalse("no more analyses", completedAnalyses.hasNext());
        assertTrue("all times are complete", completedAdvisory.areAllTimeReferencesComplete());
    }

    @Test
    public void buildSWXWithMultipleReplaceAdvisoryNumbers() throws Exception {
        final SpaceWeatherAdvisoryAmd82Impl advisory = SpaceWeatherAdvisoryAmd82Impl.builder()
                .setIssuingCenter(getIssuingCenter())
                .setIssueTime(PartialOrCompleteTimeInstant.builder().setCompleteTime(ZonedDateTime.parse("2020-02-27T01:00Z[UTC]")).build())
                .setPermissibleUsageReason(AviationCodeListUser.PermissibleUsageReason.TEST)
                .addAllPhenomena(Arrays.asList(
                        SpaceWeatherPhenomenon.fromWMOCodeListValue("http://codes.wmo.int/49-2/SpaceWxPhenomena/HF_COM_MOD"),
                        SpaceWeatherPhenomenon.fromWMOCodeListValue("http://codes.wmo.int/49-2/SpaceWxPhenomena/GNSS_MOD")))
                .setAdvisoryNumber(getAdvisoryNumber())
                .addAllReplaceAdvisoryNumber(getReplacementNumbers(13, 14, 15))
                .addAllAnalyses(getAnalyses(true))
                .setRemarks(getRemarks())
                .setNextAdvisory(getNextAdvisory(true))
                .build();

        assertEquals(3, advisory.getReplaceAdvisoryNumber().size());
        assertEquals(2020, advisory.getReplaceAdvisoryNumber().get(0).getYear());
        assertEquals(13, advisory.getReplaceAdvisoryNumber().get(0).getSerialNumber());
        assertEquals(2020, advisory.getReplaceAdvisoryNumber().get(1).getYear());
        assertEquals(14, advisory.getReplaceAdvisoryNumber().get(1).getSerialNumber());
        assertEquals(2020, advisory.getReplaceAdvisoryNumber().get(1).getYear());
        assertEquals(15, advisory.getReplaceAdvisoryNumber().get(2).getSerialNumber());


        final String serialized = OBJECT_MAPPER.writeValueAsString(advisory);
        final SpaceWeatherAdvisoryAmd82Impl deserialized = OBJECT_MAPPER.readValue(serialized, SpaceWeatherAdvisoryAmd82Impl.class);

        assertEquals(advisory, deserialized);
    }

    @Test(expected = IllegalStateException.class)
    public void buildSWXWithTooManyReplaceAdvisoryNumbers() {
        SpaceWeatherAdvisoryAmd82Impl.builder()
                .setIssuingCenter(getIssuingCenter())
                .setIssueTime(PartialOrCompleteTimeInstant.builder().setCompleteTime(ZonedDateTime.parse("2020-02-27T01:00Z[UTC]")).build())
                .setPermissibleUsageReason(AviationCodeListUser.PermissibleUsageReason.TEST)
                .addAllPhenomena(Arrays.asList(
                        SpaceWeatherPhenomenon.fromWMOCodeListValue("http://codes.wmo.int/49-2/SpaceWxPhenomena/HF_COM_MOD"),
                        SpaceWeatherPhenomenon.fromWMOCodeListValue("http://codes.wmo.int/49-2/SpaceWxPhenomena/GNSS_MOD")))
                .setAdvisoryNumber(getAdvisoryNumber())
                .addAllReplaceAdvisoryNumber(getReplacementNumbers(13, 14, 15, 16, 17))
                .addAllAnalyses(getAnalyses(true))
                .setRemarks(getRemarks())
                .setNextAdvisory(getNextAdvisory(true))
                .build();
    }

}
