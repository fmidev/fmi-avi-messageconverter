package fi.fmi.avi.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.locationtech.jts.algorithm.Orientation;
import org.locationtech.jts.geom.Coordinate;

public enum Winding {
    CLOCKWISE, COUNTERCLOCKWISE;
    public static List<Double> enforceWinding(List<Double> positions, Winding requestedWinding) {
        List<Coordinate> coords = getCoords(positions);
        if (Orientation.isCCW(coords.toArray(new Coordinate[0]))) {
          if (requestedWinding.equals(Winding.CLOCKWISE)) {
            Collections.reverse(coords);
          } else {
            return positions; // Nothing to do
          }
        } else {
          if (requestedWinding.equals(Winding.COUNTERCLOCKWISE)) {
            Collections.reverse(coords);
        } else {
            return positions; // Nothing to do
          }
        }
        List<Double> newPositions = new ArrayList<>(coords.size()*2);
        for (Coordinate c : coords) {
          newPositions.add(c.y);
          newPositions.add(c.x);
        }
        return newPositions;
      }

      public static Winding getWinding(List<Double> positions) {
        List<Coordinate> coords = getCoords(positions);
        if (Orientation.isCCW(coords.toArray(new Coordinate[0]))) {
          return Winding.COUNTERCLOCKWISE;
        }
        return Winding.CLOCKWISE;
      }

      private static List<Coordinate> getCoords(List<Double>positions) {
        List<Coordinate> coords = new ArrayList<>(positions.size()/2);
        for (int i = 0; i < positions.size(); i = i + 2) {
          coords.add(new Coordinate(positions.get(i + 1), positions.get(i)));
        }
        return coords;
      }

}
