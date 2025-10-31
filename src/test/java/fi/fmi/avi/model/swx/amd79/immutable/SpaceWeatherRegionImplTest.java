package fi.fmi.avi.model.swx.amd79.immutable;

import fi.fmi.avi.model.CircleByCenterPoint;
import fi.fmi.avi.model.PolygonGeometry;
import fi.fmi.avi.model.immutable.CoordinateReferenceSystemImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.PolygonGeometryImpl;
import fi.fmi.avi.model.swx.VerticalLimits;
import fi.fmi.avi.model.swx.VerticalLimitsImpl;
import fi.fmi.avi.model.swx.amd79.AirspaceVolume;
import fi.fmi.avi.model.swx.amd79.SpaceWeatherRegion;
import fi.fmi.avi.util.SubSolarPointUtils;
import org.junit.Test;

import java.time.Instant;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class SpaceWeatherRegionImplTest {

    @Test
    public void testComputedAirspaceVolumeDaylightSide() {
        final Instant analysisTime = Instant.parse("2023-10-31T12:00:00Z");
        final SpaceWeatherRegionImpl region = SpaceWeatherRegionImpl.builder()
                .setLocationIndicator(SpaceWeatherRegion.SpaceWeatherLocation.DAYLIGHT_SIDE)
                .withComputedAirspaceVolume(
                        SpaceWeatherRegion.SpaceWeatherLocation.DAYLIGHT_SIDE,
                        analysisTime,
                        null,
                        null,
                        null
                )
                .build();

        assertThat(region.getAirSpaceVolume()).isPresent();
        final AirspaceVolume volume = region.getAirSpaceVolume().get();
        assertThat(volume.getHorizontalProjection())
                .isPresent()
                .get()
                .isInstanceOf(CircleByCenterPoint.class);

        final CircleByCenterPoint circle = (CircleByCenterPoint) volume.getHorizontalProjection().get();
        assertThat(circle.getRadius().getValue()).isEqualTo(SubSolarPointUtils.DAYSIDE_RADIUS_KM);
        assertThat(circle.getRadius().getUom()).isEqualTo("km");
        assertThat(circle.getCenterPointCoordinates()).containsExactly(-14.11, -4.09);
    }

    @Test
    public void testComputedAirspaceVolumeLatitudeBand() {
        final VerticalLimits verticalLimits = VerticalLimitsImpl.builder()
                .setLowerLimit(NumericMeasureImpl.builder().setValue(100.0).setUom("km").build())
                .setUpperLimit(NumericMeasureImpl.builder().setValue(200.0).setUom("km").build())
                .build();

        final SpaceWeatherRegionImpl region = SpaceWeatherRegionImpl.builder()
                .setLocationIndicator(SpaceWeatherRegion.SpaceWeatherLocation.HIGH_NORTHERN_HEMISPHERE)
                .withComputedAirspaceVolume(
                        SpaceWeatherRegion.SpaceWeatherLocation.HIGH_NORTHERN_HEMISPHERE,
                        null,
                        -90.0,
                        90.0,
                        verticalLimits
                )
                .build();

        assertThat(region.getAirSpaceVolume()).isPresent();
        final AirspaceVolume volume = region.getAirSpaceVolume().get();
        assertThat(volume.getHorizontalProjection())
                .isPresent()
                .get()
                .isInstanceOf(PolygonGeometry.class);

        final PolygonGeometry polygon = (PolygonGeometry) volume.getHorizontalProjection().get();
        assertThat(polygon.getExteriorRingPositions()).hasSize(10);
    }

    @Test
    public void testComputedAirspaceVolumeDefaultLongitudes() {
        final SpaceWeatherRegionImpl region = SpaceWeatherRegionImpl.builder()
                .setLocationIndicator(SpaceWeatherRegion.SpaceWeatherLocation.MIDDLE_NORTHERN_HEMISPHERE)
                .withComputedAirspaceVolume(
                        SpaceWeatherRegion.SpaceWeatherLocation.MIDDLE_NORTHERN_HEMISPHERE,
                        null,
                        null,
                        null,
                        VerticalLimitsImpl.none()
                )
                .build();

        assertThat(region.getAirSpaceVolume()).isPresent();
        final PolygonGeometry polygon = (PolygonGeometry) region.getAirSpaceVolume().get().getHorizontalProjection().get();
        assertThat(polygon.getExteriorRingPositions()).containsExactly(60.0, -180.0, 30.0, -180.0, 30.0, 180.0, 60.0, 180.0, 60.0, -180.0);
    }

    @Test
    public void testAirspaceVolumeFromPolygon() {
        final PolygonGeometry polygon = PolygonGeometryImpl.builder()
                .addAllExteriorRingPositions(Arrays.asList(60.0, -180.0, 90.0, -180.0, 90.0, 180.0, 60.0, 180.0, 60.0, -180.0))
                .setCrs(CoordinateReferenceSystemImpl.wgs84())
                .build();

        final VerticalLimits verticalLimits = VerticalLimitsImpl.builder()
                .setLowerLimit(NumericMeasureImpl.builder().setValue(100.0).setUom("km").build())
                .build();

        final SpaceWeatherRegionImpl region = SpaceWeatherRegionImpl.builder()
                .setAirSpaceVolume(AirspaceVolumeImpl.Builder.fromPolygon(polygon, verticalLimits))
                .build();

        assertThat(region.getAirSpaceVolume()).isPresent();
        final AirspaceVolume volume = region.getAirSpaceVolume().get();
        assertThat(volume.getHorizontalProjection()).hasValue(polygon);
    }

    @Test
    public void testAirspaceVolumeFromBounds() {
        final VerticalLimits verticalLimits = VerticalLimitsImpl.builder()
                .setUpperLimit(NumericMeasureImpl.builder().setValue(200.0).setUom("km").build())
                .build();

        final SpaceWeatherRegionImpl region = SpaceWeatherRegionImpl.builder()
                .setAirSpaceVolume(AirspaceVolumeImpl.Builder.fromBounds(60.0, -90.0, 90.0, 90.0, verticalLimits))
                .build();

        assertThat(region.getAirSpaceVolume()).isPresent();
        final PolygonGeometry polygon = (PolygonGeometry) region.getAirSpaceVolume().get().getHorizontalProjection().get();
        assertThat(polygon.getExteriorRingPositions())
                .containsExactly(60.0, -90.0, 90.0, -90.0, 90.0, 90.0, 60.0, 90.0, 60.0, -90.0);
    }

    @Test
    public void testPolygonGeometryLongitudeBoundariesBoth180() {
        final SpaceWeatherRegionImpl region = SpaceWeatherRegionImpl.builder()
                .setAirSpaceVolume(AirspaceVolumeImpl.Builder.fromBounds(60.0, -180.0, 90.0, 180.0, VerticalLimitsImpl.none()))
                .build();

        final PolygonGeometry polygon = (PolygonGeometry) region.getAirSpaceVolume().get().getHorizontalProjection().get();
        assertThat(polygon.getExteriorRingPositions()).containsExactly(60.0, -180.0, 90.0, -180.0, 90.0, 180.0, 60.0, 180.0, 60.0, -180.0);
    }

}