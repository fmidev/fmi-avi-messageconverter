package fi.fmi.avi.model.metar.immutable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.model.immutable.AerodromeImpl;
import fi.fmi.avi.model.immutable.GeoPositionImpl;
import fi.fmi.avi.model.metar.METAR;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.YearMonth;
import java.time.ZoneId;

public class JSONSerializationTest {

    @Test
    public void testMETAR() throws IOException {
        METAR m = readFromJSON("metar11.json", METARImpl.class);
        METARImpl.Builder mib = METARImpl.immutableCopyOf(m).toBuilder();
        AerodromeImpl.Builder airportBuilder = new AerodromeImpl.Builder()
                .setDesignator("EETN")
                .setName("Tallinn Airport")
                .setFieldElevationValue(40.0)
                .setLocationIndicatorICAO("EETN")
                .setReferencePoint(new GeoPositionImpl.Builder()
                        .setCoordinateReferenceSystemId("http://www.opengis.net/def/crs/EPSG/0/4326")
                        .setCoordinates(new Double[]{24.8325, 59.413333})
                        .build()
                );
        m = mib
                .setAerodrome(airportBuilder.build())
                .withCompleteIssueTime(YearMonth.of(2017,7))
                .withCompleteForecastTimes(YearMonth.of(2017,7),11,11,ZoneId.of("UTC"))
                .build();

        //printAsJson(m);
    }

    protected <T extends AviationWeatherMessage> T readFromJSON(String fileName, Class<T> clz) throws IOException {
        T retval = null;
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new Jdk8Module());
        om.registerModule(new JavaTimeModule());
        InputStream is = JSONSerializationTest.class.getResourceAsStream(fileName);
        if (is != null) {
            retval = om.readValue(is, clz);
        } else {
            throw new FileNotFoundException("Resource '" + fileName + "' could not be loaded");
        }
        return retval;
    }

    protected static void printAsJson(AviationWeatherMessage msg) throws IOException {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new Jdk8Module());
        om.registerModule(new JavaTimeModule());
        ObjectWriter writer = om.writerWithDefaultPrettyPrinter();
        writer.writeValue(System.out, msg);
    }
}
