package fi.fmi.avi.model.swx.amd82.immutable;

import fi.fmi.avi.model.CircleByCenterPoint;
import fi.fmi.avi.model.PolygonGeometry;
import fi.fmi.avi.model.immutable.CircleByCenterPointImpl;
import fi.fmi.avi.model.immutable.CoordinateReferenceSystemImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.PolygonGeometryImpl;
import fi.fmi.avi.model.swx.amd82.AirspaceVolume;
import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class AirspaceVolumeImplTest {

    @Test
    public void testFromAmd79RoundsPolygonCoordinates() {
        final fi.fmi.avi.model.swx.amd79.immutable.AirspaceVolumeImpl amd79Volume =
                fi.fmi.avi.model.swx.amd79.immutable.AirspaceVolumeImpl.builder()
                        .setHorizontalProjection(PolygonGeometryImpl.builder()
                                .addAllExteriorRingPositions(Arrays.asList(
                                        60.5, -180.7,
                                        30.2, -180.3,
                                        30.8, 180.6,
                                        60.9, 180.1,
                                        60.5, -180.7))
                                .setCrs(CoordinateReferenceSystemImpl.wgs84())
                                .build())
                        .build();

        final AirspaceVolume amd82Volume = AirspaceVolumeImpl.Builder.fromAmd79(amd79Volume).build();

        assertThat(amd82Volume.getHorizontalProjection()).isPresent();
        final PolygonGeometry polygon = (PolygonGeometry) amd82Volume.getHorizontalProjection().get();
        assertThat(polygon.getExteriorRingPositions()).containsExactly(
                61.0, -181.0,
                30.0, -180.0,
                31.0, 181.0,
                61.0, 180.0,
                61.0, -181.0);
    }

    @Test
    public void testFromAmd79KeepsCircleByCenterPointCoordinates() {
        final fi.fmi.avi.model.swx.amd79.immutable.AirspaceVolumeImpl amd79Volume =
                fi.fmi.avi.model.swx.amd79.immutable.AirspaceVolumeImpl.builder()
                        .setHorizontalProjection(CircleByCenterPointImpl.builder()
                                .addAllCenterPointCoordinates(Arrays.asList(-16.64, 160.94))
                                .setRadius(NumericMeasureImpl.builder()
                                        .setValue(10100d)
                                        .setUom("km")
                                        .build())
                                .setCrs(CoordinateReferenceSystemImpl.wgs84())
                                .build())
                        .build();

        final AirspaceVolume amd82Volume = AirspaceVolumeImpl.Builder.fromAmd79(amd79Volume).build();

        assertThat(amd82Volume.getHorizontalProjection()).isPresent();
        final CircleByCenterPoint circle = (CircleByCenterPoint) amd82Volume.getHorizontalProjection().get();
        assertThat(circle.getCenterPointCoordinates()).containsExactly(-16.64, 160.94);
    }

    @Test
    public void testFromAmd79PreservesIntegerCoordinates() {
        final fi.fmi.avi.model.swx.amd79.immutable.AirspaceVolumeImpl amd79Volume =
                fi.fmi.avi.model.swx.amd79.immutable.AirspaceVolumeImpl.builder()
                        .setHorizontalProjection(PolygonGeometryImpl.builder()
                                .addAllExteriorRingPositions(Arrays.asList(
                                        60.0, -180.0,
                                        30.0, -180.0,
                                        30.0, 180.0,
                                        60.0, 180.0,
                                        60.0, -180.0))
                                .setCrs(CoordinateReferenceSystemImpl.wgs84())
                                .build())
                        .build();

        final AirspaceVolume amd82Volume = AirspaceVolumeImpl.Builder.fromAmd79(amd79Volume).build();

        assertThat(amd82Volume.getHorizontalProjection()).isPresent();
        final PolygonGeometry polygon = (PolygonGeometry) amd82Volume.getHorizontalProjection().get();
        assertThat(polygon.getExteriorRingPositions()).containsExactly(
                60.0, -180.0,
                30.0, -180.0,
                30.0, 180.0,
                60.0, 180.0,
                60.0, -180.0);
    }

    @Test
    public void testFromAmd79HandlesNegativeCoordinatesCorrectly() {
        final fi.fmi.avi.model.swx.amd79.immutable.AirspaceVolumeImpl amd79Volume =
                fi.fmi.avi.model.swx.amd79.immutable.AirspaceVolumeImpl.builder()
                        .setHorizontalProjection(PolygonGeometryImpl.builder()
                                .addAllExteriorRingPositions(Arrays.asList(
                                        -45.6, -122.4,
                                        -45.4, -122.4))
                                .setCrs(CoordinateReferenceSystemImpl.wgs84())
                                .build())
                        .build();

        final AirspaceVolume amd82Volume = AirspaceVolumeImpl.Builder.fromAmd79(amd79Volume).build();

        assertThat(amd82Volume.getHorizontalProjection()).isPresent();
        final PolygonGeometry polygon = (PolygonGeometry) amd82Volume.getHorizontalProjection().get();
        assertThat(polygon.getExteriorRingPositions()).containsExactly(-46.0, -122.0, -45.0, -122.0);
    }

    @Test
    public void testFromAmd79HandlesEmptyOptionals() {
        final fi.fmi.avi.model.swx.amd79.immutable.AirspaceVolumeImpl amd79Volume =
                fi.fmi.avi.model.swx.amd79.immutable.AirspaceVolumeImpl.builder()
                        .build();

        final AirspaceVolume amd82Volume = AirspaceVolumeImpl.Builder.fromAmd79(amd79Volume).build();

        assertThat(amd82Volume.getHorizontalProjection()).isEmpty();
        assertThat(amd82Volume.getUpperLimit()).isEmpty();
        assertThat(amd82Volume.getLowerLimit()).isEmpty();
        assertThat(amd82Volume.getMaximumLimit()).isEmpty();
        assertThat(amd82Volume.getMinimumLimit()).isEmpty();
        assertThat(amd82Volume.getWidth()).isEmpty();
    }
}
