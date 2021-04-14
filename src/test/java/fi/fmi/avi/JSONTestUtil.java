package fi.fmi.avi;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import fi.fmi.avi.model.AviationWeatherMessage;

public class JSONTestUtil {

    public static <T extends AviationWeatherMessage> T readFromJSON(final InputStream inputStream, final Class<T> clz) throws IOException {
        requireNonNull(inputStream, "inputStream");
        final ObjectMapper om = new ObjectMapper();
        om.registerModule(new Jdk8Module());
        om.registerModule(new JavaTimeModule());
        return om.readValue(inputStream, clz);
    }

    public static void printAsJson(final AviationWeatherMessage message, final OutputStream outputStream) throws IOException {
        requireNonNull(message, "message");
        requireNonNull(outputStream, "outputStream");
        final ObjectMapper om = new ObjectMapper();
        om.registerModule(new Jdk8Module());
        om.registerModule(new JavaTimeModule());
        final ObjectWriter writer = om.writerWithDefaultPrettyPrinter();
        writer.writeValue(outputStream, message);
    }
}
