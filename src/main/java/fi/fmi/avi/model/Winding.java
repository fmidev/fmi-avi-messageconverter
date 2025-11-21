package fi.fmi.avi.model;

import org.locationtech.jts.algorithm.Orientation;
import org.locationtech.jts.geom.Coordinate;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public enum Winding {
    CLOCKWISE, COUNTERCLOCKWISE;

    /**
     * Enforces the specified winding order on a list of coordinate positions.
     * <p>
     * The positions list should contain coordinate pairs as alternating latitude/longitude values.
     * If the ring is not closed (first and last coordinate pairs differ) or contains fewer than
     * 4 coordinate pairs, the original list is returned unchanged.
     * </p>
     *
     * @param positions        the list of coordinate positions (lat, lon pairs)
     * @param requestedWinding the desired winding order
     * @return the positions list with enforced winding order, or the original list if not a closed ring
     */
    public static List<Double> enforceWinding(final List<Double> positions, final Winding requestedWinding) {
        if (!isClosedRing(positions)) {
            return positions;
        }

        final Coordinate[] coordinates = getCoordinates(positions).toArray(Coordinate[]::new);
        final boolean isCCW = Orientation.isCCW(coordinates);

        if (isCCW == (requestedWinding == COUNTERCLOCKWISE)) {
            return positions;
        }

        return IntStream.range(0, coordinates.length)
                .map(i -> coordinates.length - 1 - i)
                .mapToObj(i -> coordinates[i])
                .flatMap(coordinate -> Stream.of(coordinate.y, coordinate.x))
                .collect(Collectors.toList());
    }

    public static Winding getWinding(final List<Double> positions) {
        final Coordinate[] coords = getCoordinates(positions).toArray(Coordinate[]::new);
        return Orientation.isCCW(coords) ? COUNTERCLOCKWISE : CLOCKWISE;
    }

    public static boolean isClosedRing(final List<Double> positions) {
        return positions.size() >= 8
                && positions.get(0).equals(positions.get(positions.size() - 2))
                && positions.get(1).equals(positions.get(positions.size() - 1));
    }

    private static Stream<Coordinate> getCoordinates(final List<Double> positions) {
        return IntStream.range(0, positions.size() / 2)
                .mapToObj(i -> new Coordinate(positions.get(i * 2 + 1), positions.get(i * 2)));
    }

}