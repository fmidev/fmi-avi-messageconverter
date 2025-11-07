package fi.fmi.avi.model.swx.amd79.immutable;

import fi.fmi.avi.model.CircleByCenterPoint;
import fi.fmi.avi.model.PolygonGeometry;
import fi.fmi.avi.model.immutable.CircleByCenterPointImpl;
import fi.fmi.avi.model.immutable.CoordinateReferenceSystemImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.PolygonGeometryImpl;
import fi.fmi.avi.model.swx.amd79.AirspaceVolume;
import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class AirspaceVolumeImplTest {

    @Test
    public void testFromAmd82RoundsPolygonCoordinates() {
        final fi.fmi.avi.model.swx.amd82.immutable.AirspaceVolumeImpl amd82Volume =
                fi.fmi.avi.model.swx.amd82.immutable.AirspaceVolumeImpl.builder()
                        .setHorizontalProjection(PolygonGeometryImpl.builder()
                                .addAllExteriorRingPositions(Arrays.asList(
                                        63.5, -121.3,
                                        32.8, -177.9,
                                        35.2, 150.0,
                                        58.7, 173.1,
                                        63.5, -172.3))
                                .setCrs(CoordinateReferenceSystemImpl.wgs84())
                                .build())
                        .build();

        final AirspaceVolume amd79Volume = AirspaceVolumeImpl.Builder.fromAmd82(amd82Volume).build();

        assertThat(amd79Volume.getHorizontalProjection()).isPresent();
        final PolygonGeometry polygon = (PolygonGeometry) amd79Volume.getHorizontalProjection().get();
        assertThat(polygon.getExteriorRingPositions()).containsExactly(
                60.0, -120.0,
                30.0, -180.0,
                40.0, 150.0,
                60.0, 180.0,
                60.0, -165.0);
    }

    @Test
    public void testFromAmd82RemovesConsecutiveDuplicates() {
        final fi.fmi.avi.model.swx.amd82.immutable.AirspaceVolumeImpl amd82Volume =
                fi.fmi.avi.model.swx.amd82.immutable.AirspaceVolumeImpl.builder()
                        .setHorizontalProjection(PolygonGeometryImpl.builder()
                                .addAllExteriorRingPositions(Arrays.asList(
                                        63.5, -172.3,
                                        32.8, -177.9,
                                        35.2, 178.4,
                                        44.1, 173.1,
                                        63.5, -172.3))
                                .setCrs(CoordinateReferenceSystemImpl.wgs84())
                                .build())
                        .build();

        final AirspaceVolume amd79Volume = AirspaceVolumeImpl.Builder.fromAmd82(amd82Volume).build();

        assertThat(amd79Volume.getHorizontalProjection()).isPresent();
        final PolygonGeometry polygon = (PolygonGeometry) amd79Volume.getHorizontalProjection().get();
        assertThat(polygon.getExteriorRingPositions()).containsExactly(
                60.0, -165.0,
                30.0, -180.0,
                40.0, 180.0,
                60.0, -165.0);
    }

    @Test
    public void testFromAmd82PreservesCircleByCenterPointCoordinates() {
        final fi.fmi.avi.model.swx.amd82.immutable.AirspaceVolumeImpl amd82Volume =
                fi.fmi.avi.model.swx.amd82.immutable.AirspaceVolumeImpl.builder()
                        .setHorizontalProjection(CircleByCenterPointImpl.builder()
                                .addAllCenterPointCoordinates(Arrays.asList(-16.64, 160.94))
                                .setRadius(NumericMeasureImpl.builder()
                                        .setValue(10100d)
                                        .setUom("km")
                                        .build())
                                .setCrs(CoordinateReferenceSystemImpl.wgs84())
                                .build())
                        .build();

        final AirspaceVolume amd79Volume = AirspaceVolumeImpl.Builder.fromAmd82(amd82Volume).build();

        assertThat(amd79Volume.getHorizontalProjection()).isPresent();
        final CircleByCenterPoint circle = (CircleByCenterPoint) amd79Volume.getHorizontalProjection().get();
        assertThat(circle.getCenterPointCoordinates()).containsExactly(-16.64, 160.94);
    }

    @Test
    public void testFromAmd82HandlesEmptyOptionals() {
        final fi.fmi.avi.model.swx.amd82.immutable.AirspaceVolumeImpl amd82Volume =
                fi.fmi.avi.model.swx.amd82.immutable.AirspaceVolumeImpl.builder()
                        .build();

        final AirspaceVolume amd79Volume = AirspaceVolumeImpl.Builder.fromAmd82(amd82Volume).build();

        assertThat(amd79Volume.getHorizontalProjection()).isEmpty();
        assertThat(amd79Volume.getUpperLimit()).isEmpty();
        assertThat(amd79Volume.getLowerLimit()).isEmpty();
        assertThat(amd79Volume.getMaximumLimit()).isEmpty();
        assertThat(amd79Volume.getMinimumLimit()).isEmpty();
        assertThat(amd79Volume.getWidth()).isEmpty();
    }
}