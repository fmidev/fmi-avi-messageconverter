package fi.fmi.avi.model.immutable;

import fi.fmi.avi.model.MultiPolygonGeometry;
import fi.fmi.avi.model.PolygonGeometry;
import fi.fmi.avi.model.Winding;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GeometryWindingTest {

    @Test
    public void testPolygonWinding() {
        // Y,X as they come out of getExteriorPoints
        final List<Double> cwCoordsList = Arrays.asList(90.0, -180.0, 90.0, 180.0, 60.0, 180.0, 60.0, -180.0, 90.0, -180.0);
        final List<Double> ccwCoordsList = Arrays.asList(90.0, -180.0, 60.0, -180.0, 60.0, 180.0, 90.0, 180.0, 90.0, -180.0);

        final PolygonGeometryImpl.Builder builder = PolygonGeometryImpl.builder().addAllExteriorRingPositions(cwCoordsList);
        final PolygonGeometry ccmGeom = builder.build();
        assertEquals(cwCoordsList, ccmGeom.getExteriorRingPositions());
        assertEquals(Winding.CLOCKWISE, ccmGeom.getExteriorRingWinding()); // Unchanged
        assertEquals(cwCoordsList, ccmGeom.getExteriorRingPositions(Winding.CLOCKWISE));
        assertEquals(ccwCoordsList, ccmGeom.getExteriorRingPositions(Winding.COUNTERCLOCKWISE));
    }

    @Test
    public void testMultiPolygonWinding() {
        final List<List<Double>> ccwCoordsList = Arrays.asList(
                Arrays.asList(1.0, 1.0, 2.0, 2.0, 2.0, 1.0, 1.0, 1.0),
                Arrays.asList(10.0, 10.0, 12.0, 12.0, 15.0, 10.0, 10.0, 10.0));

        final List<List<Double>> cwCoordsList = Arrays.asList(
                Arrays.asList(1.0, 1.0, 2.0, 1.0, 2.0, 2.0, 1.0, 1.0),
                Arrays.asList(10.0, 10.0, 15.0, 10.0, 12.0, 12.0, 10.0, 10.0));

        final MultiPolygonGeometryImpl.Builder builder = MultiPolygonGeometryImpl.builder()
                .addAllExteriorRingPositions(cwCoordsList);
        final MultiPolygonGeometry ccmGeom = builder.build();
        assertEquals(cwCoordsList, ccmGeom.getExteriorRingPositions());
        assertEquals(cwCoordsList, ccmGeom.getExteriorRingPositions(Winding.CLOCKWISE));
        assertEquals(ccwCoordsList, ccmGeom.getExteriorRingPositions(Winding.COUNTERCLOCKWISE));
    }

    /**
     * Test for polygon crossing the antimeridian (day boundary).
     * <p>
     * Input polygon (CCW winding when viewed on a globe):
     * N05 W155 - N20 W155 - N20 E160 - N05 E160 - N05 W155
     * </p>
     * <p>
     * This test verifies that winding detection works correctly for polygons
     * that cross the antimeridian at 180°/-180° longitude.
     * </p>
     * <p>
     * KNOWN BUG: The current implementation using JTS Orientation.isCCW() fails for
     * such polygons.
     * </p>
     */
    @Test
    @Ignore("Known issue: Winding detection fails for polygons crossing the antimeridian")
    public void testPolygonWindingCrossingAntimeridianCCWInput() {
        final List<Double> ccwCoords = Arrays.asList(
                5.0, -155.0,   // N05 W155
                20.0, -155.0,  // N20 W155
                20.0, 160.0,   // N20 E160
                5.0, 160.0,    // N05 E160
                5.0, -155.0    // N05 W155 (close ring)
        );
        final List<Double> cwCoords = Arrays.asList(
                5.0, -155.0,   // N05 W155
                5.0, 160.0,    // N05 E160
                20.0, 160.0,   // N20 E160
                20.0, -155.0,  // N20 W155
                5.0, -155.0    // N05 W155 (close ring)
        );

        final PolygonGeometry polygon = PolygonGeometryImpl.builder()
                .addAllExteriorRingPositions(ccwCoords)
                .build();

        // The polygon should be detected as CCW
        assertEquals(Winding.COUNTERCLOCKWISE, polygon.getExteriorRingWinding());

        // Requesting CCW should return the same coordinates
        assertEquals(ccwCoords, polygon.getExteriorRingPositions(Winding.COUNTERCLOCKWISE));

        // Requesting CW should return reversed coordinates
        assertEquals(cwCoords, polygon.getExteriorRingPositions(Winding.CLOCKWISE));
    }

}
