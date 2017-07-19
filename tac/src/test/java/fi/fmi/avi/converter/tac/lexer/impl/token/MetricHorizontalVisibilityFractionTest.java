package fi.fmi.avi.converter.tac.lexer.impl.token;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fi.fmi.avi.converter.tac.lexer.impl.token.MetricHorizontalVisibility;

public class MetricHorizontalVisibilityFractionTest {

    @Test
    public void testOneHalf() {
        String value = MetricHorizontalVisibility.Reconstructor.findClosestFraction(0.5, 16);
        assertEquals("1/2", value);
    }

    @Test
    public void testOneSeventh() {
        String value = MetricHorizontalVisibility.Reconstructor.findClosestFraction(1.0 / 7.0, 16);
        assertEquals("1/7", value);
    }

    @Test
    public void testSevenSixteenths() {
        String value = MetricHorizontalVisibility.Reconstructor.findClosestFraction(7.0 / 16.0, 16);
        assertEquals("7/16", value);
    }

    @Test
    public void testAllUpToMax() {
        final int maxDenominator = MetricHorizontalVisibility.MAX_STATUE_MILE_DENOMINATOR;
        for (int denominator = 2; denominator <= maxDenominator; denominator++) {
            for (int numerator = 1; numerator < denominator; numerator++) {
                double fraction = (double) numerator / (double) denominator;

                int actualNumerator = numerator;
                int actualDenominator = denominator;
                for (int div = denominator; div > 1; div--) {
                    if ((actualNumerator % div) == 0 && (actualDenominator % div) == 0) {
                        actualDenominator /= div;
                        actualNumerator /= div;
                        break;
                    }
                }
                String expected = String.format("%d/%d", actualNumerator, actualDenominator);
                String actual = MetricHorizontalVisibility.Reconstructor.findClosestFraction(fraction, maxDenominator);

                assertEquals(expected, actual);
            }
        }
    }

}
