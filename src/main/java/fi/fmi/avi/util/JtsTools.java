package fi.fmi.avi.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.geojson.Feature;
import org.locationtech.jts.algorithm.Orientation;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.locationtech.jts.io.geojson.GeoJsonWriter;

public class JtsTools {
  private static final ObjectMapper om = new ObjectMapper();
  private static final GeoJsonReader reader = new GeoJsonReader(
      new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING)));
  private static final GeoJsonWriter writer = new GeoJsonWriter();

  public static Geometry jsonFeature2jtsGeometry(final Feature feature) throws JtsToolsException {
    try {
      if (feature.getGeometry() == null) {
        return null;
      }
      final String json = om.writeValueAsString(feature.getGeometry());
      return reader.read(json);
    } catch (ParseException e) {
      throw(new JtsToolsException("Error parsing JSON feature", e));
    } catch (JsonProcessingException e) {
      throw(new JtsToolsException("Error writing JTS geometry", e));
    }
  }

  public static Feature jtsGeometry2jsonFeature(final Geometry g) throws JtsToolsException {
    Feature f = null;
    try {
      String json = writer.write(g);
      org.geojson.Geometry<Double> geo = om.readValue(
          json,
          new TypeReference<org.geojson.Geometry<Double>>(){});
      f = new Feature();
      f.setGeometry(geo);
    } catch (IOException e) {
      throw (new JtsToolsException("Error in O for writing JTS to JSON", null));
    }
    return f;
  }


  public static Feature merge(final Feature f1, final Feature f2) throws JtsToolsException {
    final Geometry g1 = jsonFeature2jtsGeometry(f1);
    if (g1==null) throw( new JtsToolsException("First polygon for merge is null"));
    final Geometry g2 = jsonFeature2jtsGeometry(f2);
    if (g2==null) throw( new JtsToolsException("Second polygon for merge is null"));

    Geometry gNew = g1.union(g2);
    Coordinate[] coords = gNew.getCoordinates();
    if (!Orientation.isCCW(coords)) {
      gNew = gNew.reverse();
    }
    final Feature f = jtsGeometry2jsonFeature(gNew);
    return f;
  }
}
