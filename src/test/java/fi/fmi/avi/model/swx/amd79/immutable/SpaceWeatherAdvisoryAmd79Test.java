package fi.fmi.avi.model.swx.amd79.immutable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fi.fmi.avi.model.*;
import fi.fmi.avi.model.immutable.CircleByCenterPointImpl;
import fi.fmi.avi.model.immutable.CoordinateReferenceSystemImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.PolygonGeometryImpl;
import fi.fmi.avi.model.swx.amd79.*;
import fi.fmi.avi.util.SubSolarPointUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class SpaceWeatherAdvisoryAmd79Test {
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

    private AdvisoryNumberImpl getAdvisoryNumber() {
        final AdvisoryNumberImpl.Builder advisory = AdvisoryNumberImpl.builder().setYear(2020).setSerialNumber(1);

        return advisory.build();
    }

    private NextAdvisory getNextAdvisory(final boolean hasNext) {
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

    private List<String> getRemarks() {
        final List<String> remarks = new ArrayList<>();
        remarks.add("RADIATION LVL EXCEEDED 100 PCT OF BACKGROUND LVL AT FL350 AND ABV. THE CURRENT EVENT HAS PEAKED AND LVL SLW RTN TO BACKGROUND LVL."
                + " SEE WWW.SPACEWEATHERPROVIDER.WEB");

        return remarks;
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

        final SpaceWeatherAdvisoryAmd79Impl SWXObject = SpaceWeatherAdvisoryAmd79Impl.builder()
                .setIssuingCenter(getIssuingCenter())
                .setIssueTime(PartialOrCompleteTimeInstant.builder().setCompleteTime(ZonedDateTime.parse("2020-02-27T01:00Z[UTC]")).build())
                .setPermissibleUsageReason(AviationCodeListUser.PermissibleUsageReason.TEST)
                .addAllPhenomena(Arrays.asList(SpaceWeatherPhenomenon.fromWMOCodeListValue("http://codes.wmo.int/49-2/SpaceWxPhenomena/HF_COM_MOD"),
                        SpaceWeatherPhenomenon.fromWMOCodeListValue("http://codes.wmo.int/49-2/SpaceWxPhenomena/GNSS_MOD")))
                .setAdvisoryNumber(getAdvisoryNumber())
                .setReplaceAdvisoryNumber(Optional.empty())
                .addAllAnalyses(analyses)
                .setRemarks(getRemarks())
                .setNextAdvisory(nextAdvisory.build())
                .build();

        assertThat(SWXObject.getAdvisoryNumber().getSerialNumber()).as("serial number").isEqualTo(1);
        assertThat(SWXObject.getAdvisoryNumber().getYear()).as("year").isEqualTo(2020);
        assertThat(SWXObject.getAnalyses().get(0).getAnalysisType()).as("analysis type").isEqualTo(SpaceWeatherAdvisoryAnalysis.Type.FORECAST);
        assertThat(SWXObject.getNextAdvisory().getTimeSpecifier()).as("next advisory time specifier").isEqualTo(NextAdvisory.Type.NEXT_ADVISORY_BY);
        assertThat(SWXObject.getNextAdvisory().getTime()).as("next advisory time").isPresent();

        final String serialized = OBJECT_MAPPER.writeValueAsString(SWXObject);
        final SpaceWeatherAdvisoryAmd79Impl deserialized = OBJECT_MAPPER.readValue(serialized, SpaceWeatherAdvisoryAmd79Impl.class);
        assertThat(deserialized).isEqualTo(SWXObject);
    }

    @Test
    public void buildSWXWithDaylightSideRegion() throws Exception {
        final Instant analysisTime = Instant.parse("2025-10-31T11:00:00Z");
        final ZonedDateTime issueTime = ZonedDateTime.parse("2025-10-31T09:23:11Z[UTC]");

        final List<SpaceWeatherRegion> regions = new ArrayList<>();
        regions.add(SpaceWeatherRegionImpl.builder()
                .setLocationIndicator(SpaceWeatherRegion.SpaceWeatherLocation.DAYLIGHT_SIDE)
                .setAirSpaceVolume(AirspaceVolumeImpl.Builder.forDaylightSide(analysisTime))
                .build());

        final SpaceWeatherAdvisoryAnalysisImpl.Builder analysis = SpaceWeatherAdvisoryAnalysisImpl.builder();
        analysis.setAnalysisType(SpaceWeatherAdvisoryAnalysis.Type.OBSERVATION)
                .setTime(PartialOrCompleteTimeInstant.of(issueTime))
                .addAllRegions(regions)
                .setNilPhenomenonReason(SpaceWeatherAdvisoryAnalysis.NilPhenomenonReason.NO_INFORMATION_AVAILABLE);

        final SpaceWeatherAdvisoryAmd79Impl SWXObject = SpaceWeatherAdvisoryAmd79Impl.builder()
                .setIssuingCenter(getIssuingCenter())
                .setIssueTime(PartialOrCompleteTimeInstant.of(issueTime))
                .setPermissibleUsageReason(AviationCodeListUser.PermissibleUsageReason.TEST)
                .addAllPhenomena(Collections.singletonList(SpaceWeatherPhenomenon.fromWMOCodeListValue("http://codes.wmo.int/49-2/SpaceWxPhenomena/HF_COM_MOD")))
                .setAdvisoryNumber(getAdvisoryNumber())
                .setReplaceAdvisoryNumber(Optional.empty())
                .addAllAnalyses(Collections.singletonList(analysis.build()))
                .setRemarks(getRemarks())
                .setNextAdvisory(getNextAdvisory(true))
                .build();

        assertThat(SWXObject.getAnalyses()).hasSize(1);

        final SpaceWeatherRegion dayside = SWXObject.getAnalyses().get(0).getRegions().get(0);
        assertThat(dayside.getLocationIndicator()).hasValue(SpaceWeatherRegion.SpaceWeatherLocation.DAYLIGHT_SIDE);
        assertThat(dayside.getAirSpaceVolume()).isPresent();
        assertThat(dayside.getAirSpaceVolume().get().getHorizontalProjection())
                .isPresent()
                .get()
                .isInstanceOf(CircleByCenterPoint.class);

        final CircleByCenterPoint circle = (CircleByCenterPoint) dayside.getAirSpaceVolume().get().getHorizontalProjection().get();
        assertThat(circle.getRadius().getValue()).isEqualTo(SubSolarPointUtils.DAYSIDE_RADIUS_KM);
        assertThat(circle.getRadius().getUom()).isEqualTo("km");
        assertThat(circle.getCenterPointCoordinates()).containsExactly(-14.26d, 10.9d);

        final String serialized = OBJECT_MAPPER.writeValueAsString(SWXObject);
        final SpaceWeatherAdvisoryAmd79Impl deserialized = OBJECT_MAPPER.readValue(serialized, SpaceWeatherAdvisoryAmd79Impl.class);
        assertThat(deserialized).isEqualTo(SWXObject);
    }

    @Test
    public void buildSWXWithoutNextAdvisory() throws Exception {
        final SpaceWeatherAdvisoryAmd79Impl SWXObject = SpaceWeatherAdvisoryAmd79Impl.builder()
                .setIssuingCenter(getIssuingCenter())
                .setIssueTime(PartialOrCompleteTimeInstant.builder().setCompleteTime(ZonedDateTime.parse("2020-02-27T01:00Z[UTC]")).build())
                .setPermissibleUsageReason(AviationCodeListUser.PermissibleUsageReason.TEST)
                .addAllPhenomena(Arrays.asList(SpaceWeatherPhenomenon.fromWMOCodeListValue("http://codes.wmo.int/49-2/SpaceWxPhenomena/HF_COM_MOD"),
                        SpaceWeatherPhenomenon.fromWMOCodeListValue("http://codes.wmo.int/49-2/SpaceWxPhenomena/GNSS_MOD")))
                .setAdvisoryNumber(getAdvisoryNumber())
                .setReplaceAdvisoryNumber(Optional.empty())
                .addAllAnalyses(getAnalyses(true))
                .setRemarks(getRemarks())
                .setNextAdvisory(getNextAdvisory(false))
                .build();

        assertThat(SWXObject.getAdvisoryNumber().getSerialNumber()).as("serial number").isEqualTo(1);
        assertThat(SWXObject.getAdvisoryNumber().getYear()).as("year").isEqualTo(2020);
        assertThat(SWXObject.getAnalyses().get(0).getAnalysisType()).as("analysis type").isEqualTo(SpaceWeatherAdvisoryAnalysis.Type.OBSERVATION);
        assertThat(SWXObject.getAnalyses()).as("analyses").hasSize(5);
        assertThat(SWXObject.getNextAdvisory().getTime()).as("next advisory time").isNotPresent();
        assertThat(SWXObject.getNextAdvisory().getTimeSpecifier()).as("next advisory time specifier").isEqualTo(NextAdvisory.Type.NO_FURTHER_ADVISORIES);

        final String serialized = OBJECT_MAPPER.writeValueAsString(SWXObject);
        final SpaceWeatherAdvisoryAmd79Impl deserialized = OBJECT_MAPPER.readValue(serialized, SpaceWeatherAdvisoryAmd79Impl.class);

        assertThat(deserialized).isEqualTo(SWXObject);
    }

    @Test
    public void buildSWXWithoutObservation() throws Exception {
        final SpaceWeatherAdvisoryAmd79Impl SWXObject = SpaceWeatherAdvisoryAmd79Impl.builder()
                .setIssuingCenter(getIssuingCenter())
                .setIssueTime(PartialOrCompleteTimeInstant.builder().setCompleteTime(ZonedDateTime.parse("2020-02-27T01:00Z[UTC]")).build())
                .setPermissibleUsageReason(AviationCodeListUser.PermissibleUsageReason.TEST)
                .addAllAnalyses(getAnalyses(false))
                .addAllPhenomena(Arrays.asList(SpaceWeatherPhenomenon.fromWMOCodeListValue("http://codes.wmo.int/49-2/SpaceWxPhenomena/HF_COM_MOD"),
                        SpaceWeatherPhenomenon.fromWMOCodeListValue("http://codes.wmo.int/49-2/SpaceWxPhenomena/GNSS_MOD")))
                .setAdvisoryNumber(getAdvisoryNumber())
                .setReplaceAdvisoryNumber(Optional.empty())
                .setRemarks(getRemarks())
                .setNextAdvisory(getNextAdvisory(false))
                .build();

        assertThat(SWXObject.getAdvisoryNumber().getSerialNumber()).as("serial number").isEqualTo(1);
        assertThat(SWXObject.getAdvisoryNumber().getYear()).as("year").isEqualTo(2020);
        assertThat(SWXObject.getAnalyses()).as("analyses").hasSize(5);

        assertThat(SWXObject.getNextAdvisory().getTime()).as("next advisory time").isNotPresent();
        assertThat(SWXObject.getNextAdvisory().getTimeSpecifier()).as("next advisory time specifier").isEqualTo(NextAdvisory.Type.NO_FURTHER_ADVISORIES);

        final String serialized = OBJECT_MAPPER.writeValueAsString(SWXObject);
        final SpaceWeatherAdvisoryAmd79Impl deserialized = OBJECT_MAPPER.readValue(serialized, SpaceWeatherAdvisoryAmd79Impl.class);

        assertThat(deserialized).isEqualTo(SWXObject);
    }

    @Test
    public void swxSerializationTest() throws Exception {
        final SpaceWeatherAdvisoryAmd79Impl SWXObject = SpaceWeatherAdvisoryAmd79Impl.builder()
                .setIssuingCenter(getIssuingCenter())
                .setIssueTime(PartialOrCompleteTimeInstant.builder().setCompleteTime(ZonedDateTime.parse("2020-02-27T01:00Z[UTC]")).build())
                .setPermissibleUsageReason(AviationCodeListUser.PermissibleUsageReason.TEST)
                .setReplaceAdvisoryNumber(getAdvisoryNumber())
                .addAllPhenomena(Arrays.asList(SpaceWeatherPhenomenon.fromWMOCodeListValue("http://codes.wmo.int/49-2/SpaceWxPhenomena/HF_COM_MOD"),
                        SpaceWeatherPhenomenon.fromWMOCodeListValue("http://codes.wmo.int/49-2/SpaceWxPhenomena/GNSS_MOD")))
                .setAdvisoryNumber(getAdvisoryNumber())
                .setReplaceAdvisoryNumber(Optional.empty())
                .addAllAnalyses(getAnalyses(true))
                .setRemarks(getRemarks())
                .setNextAdvisory(getNextAdvisory(true))
                .setReportStatus(AviationWeatherMessage.ReportStatus.NORMAL)
                .build();

        final String serialized = OBJECT_MAPPER.writeValueAsString(SWXObject);
        final SpaceWeatherAdvisoryAmd79Impl deserialized = OBJECT_MAPPER.readValue(serialized, SpaceWeatherAdvisoryAmd79Impl.class);

        assertThat(deserialized).isEqualTo(SWXObject);
    }

    @Test
    public void swxPartialTimeCompletionTest() {
        final NextAdvisory partialNextAdvisory = NextAdvisoryImpl.builder()//
                .setTimeSpecifier(NextAdvisory.Type.NEXT_ADVISORY_AT)//
                .setTime(PartialOrCompleteTimeInstant.builder().setPartialTime(PartialDateTime.ofHour(1)).build())//
                .build();

        final SpaceWeatherAdvisoryAmd79Impl advisory = SpaceWeatherAdvisoryAmd79Impl.builder()
                .setIssuingCenter(getIssuingCenter())
                .setIssueTime(PartialOrCompleteTimeInstant.builder().setPartialTime(PartialDateTime.ofDayHourMinute(27, 1, 31)).build())
                .setPermissibleUsageReason(AviationCodeListUser.PermissibleUsageReason.TEST)
                .setReplaceAdvisoryNumber(getAdvisoryNumber())
                .addAllPhenomena(Arrays.asList(SpaceWeatherPhenomenon.fromWMOCodeListValue("http://codes.wmo.int/49-2/SpaceWxPhenomena/HF_COM_MOD"),
                        SpaceWeatherPhenomenon.fromWMOCodeListValue("http://codes.wmo.int/49-2/SpaceWxPhenomena/GNSS_MOD")))
                .setAdvisoryNumber(getAdvisoryNumber())
                .setReplaceAdvisoryNumber(Optional.empty())
                .addAllAnalyses(getAnalyses(true))
                .setRemarks(getRemarks())
                .setNextAdvisory(partialNextAdvisory)
                .setReportStatus(AviationWeatherMessage.ReportStatus.NORMAL)
                .build();

        final ZonedDateTime referenceTime = ZonedDateTime.parse("2020-02-27T00:00Z");
        final SpaceWeatherAdvisoryAmd79 completedAdvisory = advisory.toBuilder().withAllTimesComplete(referenceTime).build();
        assertThat(nullableCompleteTime(completedAdvisory.getIssueTime())).as("issueTime").isEqualTo(ZonedDateTime.parse("2020-02-27T01:31Z"));
        assertThat(nullableCompleteTime(completedAdvisory.getNextAdvisory().getTime())).as("nextAdvisory").isEqualTo(ZonedDateTime.parse("2020-02-28T01:00Z"));
        final Iterator<SpaceWeatherAdvisoryAnalysis> completedAnalyses = completedAdvisory.getAnalyses().iterator();
        assertThat(nullableCompleteTime(completedAnalyses.next().getTime())).as("observation").isEqualTo(ZonedDateTime.parse("2020-02-27T01:00Z"));
        assertThat(nullableCompleteTime(completedAnalyses.next().getTime())).as("forecast +6").isEqualTo(ZonedDateTime.parse("2020-02-27T02:00Z"));
        assertThat(nullableCompleteTime(completedAnalyses.next().getTime())).as("forecast +12").isEqualTo(ZonedDateTime.parse("2020-02-27T03:00Z"));
        assertThat(nullableCompleteTime(completedAnalyses.next().getTime())).as("forecast +18").isEqualTo(ZonedDateTime.parse("2020-02-27T04:00Z"));
        assertThat(nullableCompleteTime(completedAnalyses.next().getTime())).as("forecast +24").isEqualTo(ZonedDateTime.parse("2020-02-27T05:00Z"));
        assertThat(completedAnalyses.hasNext()).as("no more analyses").isFalse();
        assertThat(completedAdvisory.areAllTimeReferencesComplete()).as("all times are complete").isTrue();
    }
}
