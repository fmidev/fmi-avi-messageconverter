package fi.fmi.avi.model.immutable;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.fasterxml.jackson.databind.ObjectMapper;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class NumericMeasureImplTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Parameters
    @Test
    public void testZeroSerialization(final NumericMeasureImpl numericMeasure, final String expectedSerialization) throws IOException {
        final String serialized = OBJECT_MAPPER.writeValueAsString(numericMeasure);
        final NumericMeasureImpl deserialized = OBJECT_MAPPER.readValue(serialized, NumericMeasureImpl.class);

        assertEquals(expectedSerialization, serialized);
        assertEquals(numericMeasure, deserialized);
    }

    public Object parametersForTestZeroSerialization() {
        return new Object[] {//
                new Object[] { NumericMeasureImpl.of(0.0, "m"), "{\"value\":0.0,\"uom\":\"m\"}" },//
                new Object[] { NumericMeasureImpl.of(-0.0, "kt"), "{\"value\":-0.0,\"uom\":\"kt\"}" } };
    }
}
