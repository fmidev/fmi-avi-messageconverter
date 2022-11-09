package fi.fmi.avi.util.geoutil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.fmi.avi.model.Geometry.Winding;

public class GeoUtils {
  private static final Logger log = LoggerFactory.getLogger(GeoUtils.class);
  private static final ObjectMapper om = new ObjectMapper();
  private static final GeoJsonReader reader = new GeoJsonReader(
      new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING)));
  private static final GeoJsonWriter writer = new GeoJsonWriter();

  public static Geometry jsonFeature2jtsGeometry(Feature F) {
    try {
      if (F.getGeometry() == null) {
        return null;
      }
      String json = om.writeValueAsString(F.getGeometry());
      return reader.read(json);
    } catch (ParseException | JsonProcessingException e) {
      log.error(e.getMessage());
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  public static Feature jtsGeometry2jsonFeature(Geometry g) {
    Feature f = null;
    try {
      String json = writer.write(g);
      org.geojson.Geometry<Double> geo = om.readValue(
          json,
          org.geojson.Geometry.class);
      f = new Feature();
      f.setGeometry(geo);
    } catch (IOException e) {
      log.error(e.getMessage());
    }
    return f;
  }

  public static List<Double> enforceWinding(List<Double> positions, Winding requestedWinding) {
    List<Coordinate> coords = new ArrayList<>();
    for (int i = 0; i < positions.size(); i = i + 2) {
      coords.add(new Coordinate(positions.get(i + 1), positions.get(i)));
    }
    if (Orientation.isCCW(coords.toArray(new Coordinate[0]))) {
      if (requestedWinding.equals(Winding.CW)) {
        Collections.reverse(coords);
      }
    } else {
      if (requestedWinding.equals(Winding.CCW)) {
        Collections.reverse(coords);
      }
    }
    List<Double> newPositions = new ArrayList<>();
    for (Coordinate c : coords) {
      newPositions.add(c.y);
      newPositions.add(c.x);
    }
    return newPositions;
  }

  public static Winding getWinding(List<Double> positions) {
    List<Coordinate> coords = new ArrayList<>();
    for (int i = 0; i < positions.size(); i = i + 2) {
      coords.add(new Coordinate(positions.get(i + 1), positions.get(i)));
    }
    if (Orientation.isCCW(coords.toArray(new Coordinate[0]))) {
      return Winding.CCW;
    }
    return Winding.CW;
  }

  public static Feature merge(Feature f1, Feature f2) {
    Geometry g1 = jsonFeature2jtsGeometry(f1);
    Geometry g2 = jsonFeature2jtsGeometry(f2);

    Geometry gNew = g1.union(g2);
    Coordinate[] coords = gNew.getCoordinates();
    if (!Orientation.isCCW(coords)) {
      gNew = gNew.reverse();
    }
    Feature f = jtsGeometry2jsonFeature(gNew);
    return f;
  }
}
