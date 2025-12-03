package fi.fmi.avi.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class SubSolarPointUtilsTest {

    private static final double TOLERANCE = 0.01;

    private final String timestamp;
    private final double expLat;
    private final double expLon;
    private final double expAntiLat;
    private final double expAntiLon;

    public SubSolarPointUtilsTest(final String timestamp,
                                  final double expLat,
                                  final double expLon,
                                  final double expAntiLat,
                                  final double expAntiLon) {
        this.timestamp = timestamp;
        this.expLat = expLat;
        this.expLon = expLon;
        this.expAntiLat = expAntiLat;
        this.expAntiLon = expAntiLon;
    }

    @Parameterized.Parameters(name = "{index}: {0} → lat={1} lon={2} | anti={3},{4}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"2016-11-08T00:06:00Z", -16.63, 174.44, 16.63, -5.56},
                {"2016-11-08T01:00:00Z", -16.64, 160.94, 16.64, -19.06},
                {"2016-11-08T07:00:00Z", -16.71, 70.94, 16.71, -109.06},
                {"2016-11-08T13:00:00Z", -16.78, -19.05, 16.78, 160.95},
                {"2016-11-08T19:00:00Z", -16.86, -109.05, 16.86, 70.95},
                {"2016-11-09T01:00:00Z", -16.93, 160.96, 16.93, -19.04},
                {"2020-11-08T01:00:00Z", -16.65, 160.94, 16.65, -19.06},
                {"2025-10-27T06:00:00Z", -12.88, 85.97, 12.88, -94.03},
                {"2025-10-27T12:00:00Z", -12.96, -4.04, 12.96, 175.96},
        });
    }

    @Test
    public void matches_expected_lat_lon() {
        final ZonedDateTime time = ZonedDateTime.parse(timestamp);

        final List<Double> latLon = SubSolarPointUtils.computeSubSolarPoint(time.toInstant());
        assertEquals("latitude", expLat, latLon.get(0), TOLERANCE);
        assertEquals("longitude", expLon, latLon.get(1), TOLERANCE);

        final List<Double> antiLatLon = SubSolarPointUtils.computeSubSolarPointAntipode(time.toInstant());
        assertEquals("antipode latitude", expAntiLat, antiLatLon.get(0), TOLERANCE);
        assertEquals("antipode longitude", expAntiLon, antiLatLon.get(1), TOLERANCE);
    }
}