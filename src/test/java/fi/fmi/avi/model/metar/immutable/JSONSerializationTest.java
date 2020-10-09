package fi.fmi.avi.model.metar.immutable;

import java.io.IOException;
import java.time.YearMonth;
import java.time.ZoneId;

import org.junit.Test;

import fi.fmi.avi.JSONTestUtil;
import fi.fmi.avi.model.immutable.AerodromeImpl;
import fi.fmi.avi.model.immutable.CoordinateReferenceSystemImpl;
import fi.fmi.avi.model.immutable.ElevatedPointImpl;
import fi.fmi.avi.model.metar.METAR;

public class JSONSerializationTest {

    @Test
    public void testMETAR() throws IOException {
        METAR m = JSONTestUtil.readFromJSON(this.getClass().getResourceAsStream("metar11.json"), METARImpl.class);
        final METARImpl.Builder mib = METARImpl.immutableCopyOf(m).toBuilder();
        final AerodromeImpl.Builder airportBuilder = AerodromeImpl.builder()
                .setDesignator("EETN")
                .setName("Tallinn Airport")
                .setFieldElevationValue(40.0)
                .setLocationIndicatorICAO("EETN")
                .setReferencePoint(ElevatedPointImpl.builder()//
                        .setCrs(CoordinateReferenceSystemImpl.wgs84())//
                        .addCoordinates(24.8325, 59.413333)//
                        .build());
        m = mib.setAerodrome(airportBuilder.build())
                .withCompleteIssueTime(YearMonth.of(2017, 7))
                .withCompleteForecastTimes(YearMonth.of(2017, 7), 11, 11, ZoneId.of("UTC"))
                .build();

        //printAsJson(m);
    }

}
