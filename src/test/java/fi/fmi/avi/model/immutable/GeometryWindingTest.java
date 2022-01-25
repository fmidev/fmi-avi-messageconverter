package fi.fmi.avi.model.immutable;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import fi.fmi.avi.model.MultiPolygonGeometry;
import fi.fmi.avi.model.PolygonGeometry;
import fi.fmi.avi.model.Geometry.Winding;

import com.fasterxml.jackson.core.JsonProcessingException;

import junitparams.JUnitParamsRunner;

@RunWith(JUnitParamsRunner.class)
public class GeometryWindingTest {

    @Test
    public void testPolygonWinding() throws JsonProcessingException {
        Double cwCoords[]={1.,1., 2.,2., 2.,1., 1.,1.};
        List<Double> cwCoordsList = Arrays.asList(cwCoords);
        Double ccwCoords[]={1.,1., 2.,1., 2.,2., 1.,1.};
        List<Double> ccwCoordsList = Arrays.asList(ccwCoords);

        PolygonGeometryImpl.Builder builder =  PolygonGeometryImpl.builder().addAllExteriorRingPositions(cwCoordsList);
        PolygonGeometry ccmGeom = builder.build();
        assertEquals(cwCoordsList, ccmGeom.getExteriorRingPositions());
        assertEquals(Winding.CW, ccmGeom.getExteriorRingWinding()); //Unchanged
        assertEquals(cwCoordsList, ccmGeom.getExteriorRingPositions(Winding.CW));
        assertEquals(ccwCoordsList, ccmGeom.getExteriorRingPositions(Winding.CCW));
    }

    @Test
    public void testMultiPolygonWinding() throws JsonProcessingException {
        Double cwCoords[][]={{1.,1., 2.,2., 2.,1., 1.,1.}, {10.,10., 12.,12., 15.,10., 10.,10.}};
        List<List<Double>> cwCoordsList = new ArrayList<>();
        for (Double []partCoords: cwCoords ) {
            cwCoordsList.add(Arrays.asList(partCoords));
        }

        Double ccwCoords[][]={{1.,1., 2.,1., 2.,2., 1.,1.}, {10.,10., 15.,10., 12.,12., 10.,10.}};
        List<List<Double>> ccwCoordsList = new ArrayList<>();
        for (Double []partCoords: ccwCoords ) {
            ccwCoordsList.add(Arrays.asList(partCoords));
        }

        MultiPolygonGeometryImpl.Builder builder =  MultiPolygonGeometryImpl.builder().addAllExteriorRingPositions(cwCoordsList);
        MultiPolygonGeometry ccmGeom = builder.build();
        assertEquals(cwCoordsList, ccmGeom.getExteriorRingPositions());
        assertEquals(cwCoordsList, ccmGeom.getExteriorRingPositions(Winding.CW));
        assertEquals(ccwCoordsList, ccmGeom.getExteriorRingPositions(Winding.CCW));
    }
}
