package fi.fmi.avi.model.immutable;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import fi.fmi.avi.model.MultiPolygonGeometry;
import fi.fmi.avi.model.PolygonGeometry;
import fi.fmi.avi.model.Winding;


import junitparams.JUnitParamsRunner;

@RunWith(JUnitParamsRunner.class)
public class GeometryWindingTest {

    @Test
    public void testPolygonWinding() {
        // Y,X as they come out of getExteriorPoints
        List<Double> cwCoordsList = Arrays.asList(90.0, -180.0, 90.0, 180.0, 60.0, 180.0, 60.0, -180.0, 90.0, -180.0);
        List<Double> ccwCoordsList = Arrays.asList(90.0, -180.0, 60.0, -180.0, 60.0, 180.0, 90.0, 180.0, 90.0, -180.0);

        PolygonGeometryImpl.Builder builder = PolygonGeometryImpl.builder().addAllExteriorRingPositions(cwCoordsList);
        PolygonGeometry ccmGeom = builder.build();
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

        MultiPolygonGeometryImpl.Builder builder = MultiPolygonGeometryImpl.builder()
                .addAllExteriorRingPositions(cwCoordsList);
        MultiPolygonGeometry ccmGeom = builder.build();
        assertEquals(cwCoordsList, ccmGeom.getExteriorRingPositions());
        assertEquals(cwCoordsList, ccmGeom.getExteriorRingPositions(Winding.CLOCKWISE));
        assertEquals(ccwCoordsList, ccmGeom.getExteriorRingPositions(Winding.COUNTERCLOCKWISE));
    }
}
