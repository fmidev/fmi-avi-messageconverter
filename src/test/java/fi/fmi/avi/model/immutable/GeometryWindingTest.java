package fi.fmi.avi.model.immutable;

import fi.fmi.avi.model.MultiPolygonGeometry;
import fi.fmi.avi.model.PolygonGeometry;
import fi.fmi.avi.model.Winding;
import junitparams.JUnitParamsRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(JUnitParamsRunner.class)
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
}
