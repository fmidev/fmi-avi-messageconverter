package fi.fmi.avi.model.taf.immutable;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Sets;

import fi.fmi.avi.model.Aerodrome;
import fi.fmi.avi.model.AviationCodeListUser.TAFStatus;
import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.immutable.AerodromeImpl;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.TAFReference;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class TAFIWXXM21_30_ResilienceTest {
    private static final AerodromeImpl AERODROME = AerodromeImpl.builder()//
            .setDesignator("YUDO")//
            .build();
    private static final PartialOrCompleteTimePeriod REFERRED_REPORT_VALID_PERIOD = PartialOrCompleteTimePeriod.createValidityTime("0100/0200");
    private static final TAFReferenceImpl REFERRED_REPORT = TAFReferenceImpl.builder()//
            .setAerodrome(AERODROME)//
            .setValidityTime(REFERRED_REPORT_VALID_PERIOD)//
            .build();
    private static final TAFBaseForecastImpl BASE_FORECAST = TAFBaseForecastImpl.builder().buildPartial();

    private static <E> Optional<E> last(final List<E> list) {
        final int size = list.size();
        return size > 0 ? Optional.of(list.get(size - 1)) : Optional.empty();
    }

    private static <E> Optional<E> last(final List<E> list1, final List<E> list2) {
        final Optional<E> last = last(list2);
        if (last.isPresent()) {
            return last;
        }
        return last(list1);
    }

    @Test
    public void testTAFStatus() {
        TAF t = TAFImpl.builder()//
                .setStatus(TAFStatus.NORMAL)//
                .setBaseForecast(TAFBaseForecastImpl.builder().buildPartial())//
                .buildPartial();
        assertEquals(TAFStatus.NORMAL, t.getStatus());
        assertTrue(t.getReportStatus().isPresent());
        assertEquals(AviationWeatherMessage.ReportStatus.NORMAL, t.getReportStatus().get());
        assertFalse(t.isCancelMessage());
        assertFalse(t.isMissingMessage());
        assertFalse(t.getReferredReportValidPeriod().isPresent());
        assertFalse(t.getReferredReport().isPresent());

        t = TAFImpl.builder()//
                .setStatus(TAFStatus.CANCELLATION)//
                .setBaseForecast(TAFBaseForecastImpl.builder().buildPartial())//
                .buildPartial();
        assertEquals(TAFStatus.CANCELLATION, t.getStatus());
        assertTrue(t.getReportStatus().isPresent());
        assertEquals(AviationWeatherMessage.ReportStatus.AMENDMENT, t.getReportStatus().get());
        assertTrue(t.isCancelMessage());
        assertFalse(t.isMissingMessage());
        assertFalse(t.getReferredReportValidPeriod().isPresent());
        assertFalse(t.getReferredReport().isPresent());

        t = TAFImpl.builder()//
                .setStatus(TAFStatus.AMENDMENT)//
                .setBaseForecast(TAFBaseForecastImpl.builder().buildPartial())//
                .buildPartial();
        assertEquals(TAFStatus.AMENDMENT, t.getStatus());
        assertTrue(t.getReportStatus().isPresent());
        assertEquals(AviationWeatherMessage.ReportStatus.AMENDMENT, t.getReportStatus().get());
        assertFalse(t.isCancelMessage());
        assertFalse(t.isMissingMessage());
        assertFalse(t.getReferredReportValidPeriod().isPresent());
        assertFalse(t.getReferredReport().isPresent());

        t = TAFImpl.builder().setStatus(TAFStatus.CORRECTION).setBaseForecast(TAFBaseForecastImpl.builder().buildPartial())//
                .buildPartial();
        assertEquals(TAFStatus.CORRECTION, t.getStatus());
        assertTrue(t.getReportStatus().isPresent());
        assertEquals(AviationWeatherMessage.ReportStatus.CORRECTION, t.getReportStatus().get());
        assertFalse(t.isCancelMessage());
        assertFalse(t.isMissingMessage());
        assertFalse(t.getReferredReportValidPeriod().isPresent());
        assertFalse(t.getReferredReport().isPresent());
    }

    @Test
    public void testMissingMessage() {
        TAFImpl t = TAFImpl.builder().setBaseForecast(TAFBaseForecastImpl.builder().buildPartial()).buildPartial();
        assertEquals(TAFStatus.NORMAL, t.getStatus());

        t = TAFImpl.builder().buildPartial();
        assertEquals(TAFStatus.MISSING, t.getStatus());

        t = TAFImpl.builder().setBaseForecast(TAFBaseForecastImpl.builder().buildPartial()).setStatus(TAFStatus.MISSING).buildPartial();
        assertEquals(TAFStatus.MISSING, t.getStatus());
        assertFalse(t.getBaseForecast().isPresent());

        // Expect not to mix old and new API; baseForecast gets omitted
        t = TAFImpl.builder().setStatus(TAFStatus.MISSING).setBaseForecast(TAFBaseForecastImpl.builder().buildPartial()).buildPartial();
        assertEquals(TAFStatus.MISSING, t.getStatus());
        assertFalse(t.getBaseForecast().isPresent());

        t = TAFImpl.builder().setStatus(TAFStatus.MISSING).buildPartial();
        assertEquals(TAFStatus.MISSING, t.getStatus());

    }

    @Test
    public void testReportStatus() {
        TAF t = TAFImpl.builder()//
                .setBaseForecast(TAFBaseForecastImpl.builder().buildPartial())//
                .setReportStatus(AviationWeatherMessage.ReportStatus.NORMAL)//
                .buildPartial();
        assertEquals(TAFStatus.NORMAL, t.getStatus());
        assertTrue(t.getReportStatus().isPresent());
        assertEquals(AviationWeatherMessage.ReportStatus.NORMAL, t.getReportStatus().get());
        assertFalse(t.isCancelMessage());
        assertFalse(t.isMissingMessage());
        assertFalse(t.getReferredReportValidPeriod().isPresent());
        assertFalse(t.getReferredReport().isPresent());

        t = TAFImpl.builder()//
                .setBaseForecast(TAFBaseForecastImpl.builder().buildPartial())//
                .setReportStatus(AviationWeatherMessage.ReportStatus.AMENDMENT)//
                .buildPartial();
        assertEquals(TAFStatus.AMENDMENT, t.getStatus());
        assertTrue(t.getReportStatus().isPresent());
        assertEquals(AviationWeatherMessage.ReportStatus.AMENDMENT, t.getReportStatus().get());
        assertFalse(t.isCancelMessage());
        assertFalse(t.isMissingMessage());
        assertFalse(t.getReferredReportValidPeriod().isPresent());
        assertFalse(t.getReferredReport().isPresent());

        t = TAFImpl.builder()//
                .setBaseForecast(TAFBaseForecastImpl.builder().buildPartial())//
                .setReportStatus(AviationWeatherMessage.ReportStatus.CORRECTION)//
                .buildPartial();
        assertEquals(TAFStatus.CORRECTION, t.getStatus());
        assertTrue(t.getReportStatus().isPresent());
        assertEquals(AviationWeatherMessage.ReportStatus.CORRECTION, t.getReportStatus().get());
        assertFalse(t.isCancelMessage());
        assertFalse(t.isMissingMessage());
        assertFalse(t.getReferredReportValidPeriod().isPresent());
        assertFalse(t.getReferredReport().isPresent());

    }

    @Test
    public void testStatusAndReportStatusConsistency() {
        TAFImpl.Builder t = TAFImpl.builder().setCancelMessage(true).setReportStatus(AviationWeatherMessage.ReportStatus.AMENDMENT);
        assertEquals(TAFStatus.CANCELLATION, t.getStatus());
        assertTrue(t.isCancelMessage());
        assertTrue(t.getReportStatus().isPresent());
        assertEquals(AviationWeatherMessage.ReportStatus.AMENDMENT, t.getReportStatus().get());

        t = TAFImpl.builder().setReportStatus(AviationWeatherMessage.ReportStatus.AMENDMENT).setCancelMessage(true);
        assertEquals(TAFStatus.CANCELLATION, t.getStatus());
        assertTrue(t.isCancelMessage());
        assertTrue(t.getReportStatus().isPresent());
        assertEquals(AviationWeatherMessage.ReportStatus.AMENDMENT, t.getReportStatus().get());

        t = TAFImpl.builder().setReportStatus(AviationWeatherMessage.ReportStatus.NORMAL).setCancelMessage(true);
        assertEquals(TAFStatus.CANCELLATION, t.getStatus());
        assertTrue(t.isCancelMessage());
        assertTrue(t.getReportStatus().isPresent());
        assertEquals(AviationWeatherMessage.ReportStatus.NORMAL, t.getReportStatus().get());

        t = TAFImpl.builder().setCancelMessage(true).setReportStatus(AviationWeatherMessage.ReportStatus.NORMAL);
        assertEquals(TAFStatus.CANCELLATION, t.getStatus());
        assertTrue(t.isCancelMessage());
        assertTrue(t.getReportStatus().isPresent());
        assertEquals(AviationWeatherMessage.ReportStatus.NORMAL, t.getReportStatus().get());

        t = TAFImpl.builder().setReportStatus(AviationWeatherMessage.ReportStatus.CORRECTION).setCancelMessage(true);
        assertEquals(TAFStatus.CANCELLATION, t.getStatus());
        assertTrue(t.isCancelMessage());
        assertTrue(t.getReportStatus().isPresent());
        assertEquals(AviationWeatherMessage.ReportStatus.CORRECTION, t.getReportStatus().get());

        t = TAFImpl.builder().setCancelMessage(true).setReportStatus(AviationWeatherMessage.ReportStatus.CORRECTION);
        assertEquals(TAFStatus.CANCELLATION, t.getStatus());
        assertTrue(t.isCancelMessage());
        assertTrue(t.getReportStatus().isPresent());
        assertEquals(AviationWeatherMessage.ReportStatus.CORRECTION, t.getReportStatus().get());
    }

    /*
    @Test
    public void testReferredReportValidityAffectsCancelledReportValidTime() {
        final PartialOrCompleteTimePeriod tp1 = PartialOrCompleteTimePeriod.createValidityTime("0112/0214");
        final PartialOrCompleteTimePeriod tp2 = PartialOrCompleteTimePeriod.createValidityTime("0112/0214");
        assertEquals(tp2, tp1);
        TAF t = TAFImpl.builder()//
                .setCancelMessage(true)//
                .setReferredReport(TAFReferenceImpl.builder()//
                        .setValidityTime(tp1)//
                        .buildPartial())//
                .buildPartial();
        assertTrue(t.getCancelledReportValidPeriod().isPresent());
        assertEquals(tp2, t.getCancelledReportValidPeriod().get());
    }

     */

    @Test
    public void testCancelledReportValidTimeAffectsReferredReportValidity() {
        final PartialOrCompleteTimePeriod tp1 = PartialOrCompleteTimePeriod.createValidityTime("0112/0214");
        final PartialOrCompleteTimePeriod tp2 = PartialOrCompleteTimePeriod.createValidityTime("0112/0214");
        assertEquals(tp2, tp1);
        final TAF t = TAFImpl.builder()//
                .setReferredReportValidPeriod(tp1)//
                .setCancelMessage(true)//
                .setAerodrome(AerodromeImpl.builder().setDesignator("FOO").build())//
                .buildPartial();
        assertTrue(t.getReferredReport().isPresent());
        assertTrue(t.getReferredReport().get().getValidityTime().isPresent());
        assertEquals(tp2, t.getReferredReport().get().getValidityTime().get());
    }

    @Test
    //@Ignore
    /*
    Test ignored, based on the latest spec setting cancelMessage should not affect the cancelled report valid time,
    see https://github.com/fmidev/fmi-avi-messageconverter/pull/68#discussion_r540170430
     */

    public void testSetCancelStatusCancelledReportValidTime() {
        final PartialOrCompleteTimePeriod tp1 = PartialOrCompleteTimePeriod.createValidityTime("0112/0214");
        final AerodromeImpl aerodrome = AerodromeImpl.builder().setDesignator("YUDO").build();
        TAF t = TAFImpl.builder()//
                .setReferredReport(TAFReferenceImpl.builder()//
                        .setAerodrome(aerodrome)//
                        .setValidityTime(tp1)//
                        .buildPartial())//
                .buildPartial();
        assertTrue(t.getReferredReportValidPeriod().isPresent());

        t = TAFImpl.builder()//
                .setStatus(TAFStatus.CANCELLATION)//
                .setReferredReport(TAFReferenceImpl.builder()//
                        .setAerodrome(aerodrome)//
                        .setValidityTime(tp1)//
                        .buildPartial())//
                .buildPartial();
        assertTrue(t.getReferredReportValidPeriod().isPresent());
        assertEquals(tp1, t.getReferredReportValidPeriod().get());

        t = TAFImpl.builder()//
                .setReferredReport(TAFReferenceImpl.builder()//
                        .setAerodrome(aerodrome)//
                        .setValidityTime(tp1)//
                        .buildPartial())//
                .setStatus(TAFStatus.CANCELLATION)//
                .buildPartial();
        assertTrue(t.getReferredReportValidPeriod().isPresent());
        assertEquals(tp1, t.getReferredReportValidPeriod().get());

    }

    @Test
    public void testReferredReportAdapter() {
        final PartialOrCompleteTimePeriod tp1 = PartialOrCompleteTimePeriod.createValidityTime("0112/0214");
        final Aerodrome ad = AerodromeImpl.builder().setDesignator("FOO").build();
        final Aerodrome ad2 = AerodromeImpl.builder().setDesignator("BAR").build();

        TAF t = TAFImpl.builder().setCancelMessage(true)//
                .setReferredReportValidPeriod(tp1)//
                .setAerodrome(ad)//
                .buildPartial();
        assertTrue(t.getReferredReport().isPresent());
        assertTrue(t.getReferredReport().get().getValidityTime().isPresent());
        assertEquals(tp1, t.getReferredReport().get().getValidityTime().get());
        assertEquals(ad, t.getReferredReport().get().getAerodrome());

        t = TAFImpl.builder().setReferredReport(TAFReferenceImpl.builder().setAerodrome(ad).setValidityTime(tp1).build()).buildPartial();
        assertTrue(t.getReferredReportValidPeriod().isPresent());

        t = TAFImpl.builder().setReferredReport(TAFReferenceImpl.builder().setAerodrome(ad).setValidityTime(tp1).build()).setStatus(TAFStatus.CANCELLATION)
                .buildPartial();
        assertTrue(t.getReferredReportValidPeriod().isPresent());
        assertEquals(tp1, t.getReferredReportValidPeriod().get());

        t = TAFImpl.builder().setReferredReport(TAFReferenceImpl.builder().setAerodrome(ad).setValidityTime(tp1).build()).setStatus(TAFStatus.AMENDMENT)
                .buildPartial();
        assertTrue(t.getReferredReportValidPeriod().isPresent());
        assertEquals(tp1, t.getReferredReportValidPeriod().get());

        t = TAFImpl.builder()
                .setReferredReport(TAFReferenceImpl.builder().setAerodrome(ad).setValidityTime(tp1).build())
                .setStatus(TAFStatus.CORRECTION)
                .buildPartial();
        assertTrue(t.getReferredReportValidPeriod().isPresent());
        assertEquals(tp1, t.getReferredReportValidPeriod().get());

        assertThrows("setReferredReport should throw an IllegalStateException when given a conflicting aerodrome", IllegalStateException.class,
                () -> TAFImpl.builder().setAerodrome(ad)//
                        .setReferredReport(TAFReferenceImpl.builder().setAerodrome(ad2).setValidityTime(tp1).build()).buildPartial());
    }

    @Test
    public void testCancelBuildOrderConsistency() {
        final PartialOrCompleteTimePeriod validityTime = PartialOrCompleteTimePeriod.createValidityTime("0112/0214");
        final PartialOrCompleteTimePeriod cancelledReportValidPeriod = PartialOrCompleteTimePeriod.createValidityTime("0106/0212");
        TAFImpl.Builder t = TAFImpl.builder()//
                .setValidityTime(validityTime)//
                .setCancelMessage(true)//
                .setReferredReportValidPeriod(cancelledReportValidPeriod);
        assertTrue(t.getReferredReportValidPeriod().isPresent());
        assertEquals(cancelledReportValidPeriod, t.getReferredReportValidPeriod().get());

        t = TAFImpl.builder().setValidityTime(validityTime).setReferredReportValidPeriod(cancelledReportValidPeriod).setCancelMessage(true);
        assertTrue(t.getReferredReportValidPeriod().isPresent());
        assertEquals(cancelledReportValidPeriod, t.getReferredReportValidPeriod().get());
    }

    @Test
    public void debugTestSetStatusMultipleInvocations() {
        //        AMENDMENT, AMENDMENT, MISSING
        testSetStatusMultipleInvocations(Arrays.asList(TAFStatus.AMENDMENT, TAFStatus.MISSING, TAFStatus.AMENDMENT), Collections.emptyList());
    }

    @Parameters(source = TAFIWXXM21_30_ResilienceTest.StatusesProvider.class)
    @Test
    public void testSetStatusMultipleInvocations(final List<TAFStatus> statusesFirst, final List<TAFStatus> statusesLast) {
        final TAFIWXXM21_30_ResilienceTest.StatusAssertion assertion = TAFIWXXM21_30_ResilienceTest.StatusAssertion.of(
                last(statusesFirst, statusesLast).orElse(TAFStatus.NORMAL));
        final TAFImpl.Builder builder = TAFImpl.builder();
        statusesFirst.forEach(builder::setStatus);
        builder//
                .setBaseForecast(BASE_FORECAST)//
                .setReferredReport(REFERRED_REPORT);
        statusesLast.forEach(builder::setStatus);
        final TAFImpl taf = builder.buildPartial();

        assertion.assertStatus(statusesFirst + ", " + statusesLast, taf, REFERRED_REPORT, BASE_FORECAST);
    }

    @Parameters(source = TAFIWXXM21_30_ResilienceTest.StatusesProvider.class)
    @Test
    public void testSetStatusMultipleInvocationsWithoutReferredReport(final List<TAFStatus> statusesFirst, final List<TAFStatus> statusesLast) {
        final TAFIWXXM21_30_ResilienceTest.StatusAssertion assertion = TAFIWXXM21_30_ResilienceTest.StatusAssertion.of(
                last(statusesFirst, statusesLast).orElse(TAFStatus.NORMAL));
        final TAFImpl.Builder builder = TAFImpl.builder();
        statusesFirst.forEach(builder::setStatus);
        builder.setBaseForecast(BASE_FORECAST);
        statusesLast.forEach(builder::setStatus);
        final TAFImpl taf = builder.buildPartial();

        assertion.assertStatus(statusesFirst + ", " + statusesLast, taf, null, BASE_FORECAST);
    }

    private enum StatusAssertion {
        NORMAL(TAFStatus.NORMAL) {
            @Override
            public void assertStatus(final String message, final TAF taf, final TAFReferenceImpl expectedReferredReport,
                    final TAFBaseForecastImpl baseForecast) {
                assertEquals("status: " + message, TAFStatus.NORMAL, taf.getStatus());
                assertEquals("referredReport: " + message, expectedReferredReport, taf.getReferredReport().orElse(null));

                assertEquals("reportStatus: " + message, AviationWeatherMessage.ReportStatus.NORMAL, taf.getReportStatus().orElse(null));
                assertFalse("cancelMessage: " + message, taf.isCancelMessage());
                final PartialOrCompleteTimePeriod expectedReferredReportValidPeriod = Optional.ofNullable(expectedReferredReport)
                        .flatMap(TAFReference::getValidityTime)
                        .orElse(null);
                assertEquals("referredReportValidPeriod: " + message, expectedReferredReportValidPeriod, taf.getReferredReportValidPeriod().orElse(null));
                assertEquals("baseForecast: " + message, baseForecast, taf.getBaseForecast().orElse(null));
            }
        },//
        AMENDMENT(TAFStatus.AMENDMENT) {
            @Override
            public void assertStatus(final String message, final TAF taf, final TAFReferenceImpl expectedReferredReport,
                    final TAFBaseForecastImpl baseForecast) {
                assertEquals("status: " + message, TAFStatus.AMENDMENT, taf.getStatus());
                assertEquals("referredReport: " + message, expectedReferredReport, taf.getReferredReport().orElse(null));

                assertEquals("reportStatus: " + message, AviationWeatherMessage.ReportStatus.AMENDMENT, taf.getReportStatus().orElse(null));
                assertFalse("cancelMessage: " + message, taf.isCancelMessage());
                final PartialOrCompleteTimePeriod expectedReferredReportValidPeriod = Optional.ofNullable(expectedReferredReport)
                        .flatMap(TAFReference::getValidityTime)
                        .orElse(null);
                assertEquals("referredReportValidPeriod: " + message, expectedReferredReportValidPeriod, taf.getReferredReportValidPeriod().orElse(null));
                assertEquals("baseForecast: " + message, baseForecast, taf.getBaseForecast().orElse(null));
            }
        },//
        CANCELLATION(TAFStatus.CANCELLATION) {
            @Override
            public void assertStatus(final String message, final TAF taf, final TAFReferenceImpl expectedReferredReport,
                    final TAFBaseForecastImpl baseForecast) {
                assertEquals("status: " + message, TAFStatus.CANCELLATION, taf.getStatus());
                assertEquals("referredReport: " + message, expectedReferredReport, taf.getReferredReport().orElse(null));

                assertEquals("reportStatus: " + message, AviationWeatherMessage.ReportStatus.AMENDMENT, taf.getReportStatus().orElse(null));
                assertTrue("cancelMessage: " + message, taf.isCancelMessage());
                final PartialOrCompleteTimePeriod expectedReferredReportValidPeriod = Optional.ofNullable(expectedReferredReport)
                        .flatMap(TAFReference::getValidityTime)
                        .orElse(null);
                assertEquals("referredReportValidPeriod: " + message, expectedReferredReportValidPeriod, taf.getReferredReportValidPeriod().orElse(null));
                assertEquals("baseForecast: " + message, baseForecast, taf.getBaseForecast().orElse(null));
            }
        },//
        CORRECTION(TAFStatus.CORRECTION) {
            @Override
            public void assertStatus(final String message, final TAF taf, final TAFReferenceImpl expectedReferredReport,
                    final TAFBaseForecastImpl baseForecast) {
                assertEquals("status: " + message, TAFStatus.CORRECTION, taf.getStatus());
                assertEquals("referredReport: " + message, expectedReferredReport, taf.getReferredReport().orElse(null));

                assertEquals("reportStatus: " + message, AviationWeatherMessage.ReportStatus.CORRECTION, taf.getReportStatus().orElse(null));
                assertFalse("cancelMessage: " + message, taf.isCancelMessage());
                final PartialOrCompleteTimePeriod expectedReferredReportValidPeriod = Optional.ofNullable(expectedReferredReport)
                        .flatMap(TAFReference::getValidityTime)
                        .orElse(null);
                assertEquals("referredReportValidPeriod: " + message, expectedReferredReportValidPeriod, taf.getReferredReportValidPeriod().orElse(null));
                assertEquals("baseForecast: " + message, baseForecast, taf.getBaseForecast().orElse(null));
            }
        },//
        MISSING(TAFStatus.MISSING) {
            @Override
            public void assertStatus(final String message, final TAF taf, final TAFReferenceImpl expectedReferredReport,
                    final TAFBaseForecastImpl baseForecast) {
                assertEquals("status: " + message, TAFStatus.MISSING, taf.getStatus());
                assertEquals("referredReport: " + message, expectedReferredReport, taf.getReferredReport().orElse(null));

                assertEquals("reportStatus: " + message, AviationWeatherMessage.ReportStatus.NORMAL, taf.getReportStatus().orElse(null));
                assertFalse("cancelMessage: " + message, taf.isCancelMessage());
                final PartialOrCompleteTimePeriod expectedReferredReportValidPeriod = Optional.ofNullable(expectedReferredReport)
                        .flatMap(TAFReference::getValidityTime)
                        .orElse(null);
                assertEquals("referredReportValidPeriod: " + message, expectedReferredReportValidPeriod, taf.getReferredReportValidPeriod().orElse(null));
                assertNull("baseForecast: " + message, taf.getBaseForecast().orElse(null));
            }
        };//

        private static final Map<TAFStatus, TAFIWXXM21_30_ResilienceTest.StatusAssertion> ASSERTIONS_BY_STATUS = Collections.unmodifiableMap(
                Arrays.stream(TAFIWXXM21_30_ResilienceTest.StatusAssertion.values())//
                        .collect(Collectors.toMap(TAFIWXXM21_30_ResilienceTest.StatusAssertion::getStatus, Function.identity(), (a, b) -> {
                            throw new IllegalArgumentException("Duplicate " + a);
                        }, () -> new EnumMap<>(TAFStatus.class))));

        private final TAFStatus status;

        StatusAssertion(final TAFStatus status) {
            this.status = status;
        }

        public static TAFIWXXM21_30_ResilienceTest.StatusAssertion of(final TAFStatus status) {
            requireNonNull(status, "status");
            final TAFIWXXM21_30_ResilienceTest.StatusAssertion assertion = ASSERTIONS_BY_STATUS.get(status);
            if (assertion == null) {
                throw new IllegalArgumentException("status");
            }
            return assertion;
        }

        public TAFStatus getStatus() {
            return status;
        }

        public abstract void assertStatus(String message, TAF taf, final TAFReferenceImpl expectedReferredReport, final TAFBaseForecastImpl baseForecast);
    }

    public static class StatusesProvider {
        public static Object[][] provideStatuses() {
            return cartesianProducts(EnumSet.allOf(TAFStatus.class), 0, 3)//
                    .flatMap(TAFIWXXM21_30_ResilienceTest.StatusesProvider::splits)//
                    .toArray(Object[][]::new);
        }

        private static <E> Stream<List<E>> cartesianProducts(final Set<E> values, final int repeatMin, final int repeatMax) {
            return IntStream.rangeClosed(repeatMin, repeatMax)//
                    .mapToObj(count -> Sets.cartesianProduct(repeat(values, count)))//
                    .flatMap(Collection::stream);
        }

        private static <T> List<T> repeat(final T value, final int times) {
            return Stream.generate(() -> value)//
                    .limit(times)//
                    .collect(Collectors.toList());
        }

        private static <E> Stream<Object[]> splits(final List<E> values) {
            return IntStream.rangeClosed(0, values.size())//
                    .mapToObj(i -> new Object[] { values.subList(0, i), values.subList(i, values.size()) });
        }
    }
}
