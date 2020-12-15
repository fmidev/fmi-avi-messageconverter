package fi.fmi.avi.model.taf.immutable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import fi.fmi.avi.model.Aerodrome;
import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.immutable.AerodromeImpl;
import fi.fmi.avi.model.taf.TAF;

public class TAFIWXXM21_30_ResilienceTest {

    @Test
    public void testTAFStatus() {
        TAF t = TAFImpl.builder()//
                .setStatus(TAF.TAFStatus.NORMAL)//
                .setBaseForecast(TAFBaseForecastImpl.builder().buildPartial())//
                .buildPartial();
        assertEquals(AviationCodeListUser.TAFStatus.NORMAL, t.getStatus());
        assertTrue(t.getReportStatus().isPresent());
        assertEquals(AviationWeatherMessage.ReportStatus.NORMAL, t.getReportStatus().get());
        assertFalse(t.isCancelMessage());
        assertFalse(t.isMissingMessage());
        assertFalse(t.getReferredReportValidPeriod().isPresent());
        assertFalse(t.getReferredReport().isPresent());

        t = TAFImpl.builder()//
                .setStatus(AviationCodeListUser.TAFStatus.CANCELLATION)//
                .setBaseForecast(TAFBaseForecastImpl.builder().buildPartial())//
                .buildPartial();
        assertEquals(TAF.TAFStatus.CANCELLATION, t.getStatus());
        assertTrue(t.getReportStatus().isPresent());
        assertEquals(AviationWeatherMessage.ReportStatus.AMENDMENT, t.getReportStatus().get());
        assertTrue(t.isCancelMessage());
        assertFalse(t.isMissingMessage());
        assertFalse(t.getReferredReportValidPeriod().isPresent());
        assertFalse(t.getReferredReport().isPresent());

        t = TAFImpl.builder()//
                .setStatus(AviationCodeListUser.TAFStatus.AMENDMENT)//
                .setBaseForecast(TAFBaseForecastImpl.builder().buildPartial())//
                .buildPartial();
        assertEquals(TAF.TAFStatus.AMENDMENT, t.getStatus());
        assertTrue(t.getReportStatus().isPresent());
        assertEquals(AviationWeatherMessage.ReportStatus.AMENDMENT, t.getReportStatus().get());
        assertFalse(t.isCancelMessage());
        assertFalse(t.isMissingMessage());
        assertFalse(t.getReferredReportValidPeriod().isPresent());
        assertFalse(t.getReferredReport().isPresent());

        t = TAFImpl.builder().setStatus(AviationCodeListUser.TAFStatus.CORRECTION).setBaseForecast(TAFBaseForecastImpl.builder().buildPartial())//
                .buildPartial();
        assertEquals(TAF.TAFStatus.CORRECTION, t.getStatus());
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
        assertEquals(TAF.TAFStatus.NORMAL, t.getStatus());

        t = TAFImpl.builder().buildPartial();
        assertEquals(TAF.TAFStatus.MISSING, t.getStatus());

        t = TAFImpl.builder().setBaseForecast(TAFBaseForecastImpl.builder().buildPartial()).setStatus(AviationCodeListUser.TAFStatus.MISSING).buildPartial();
        assertEquals(TAF.TAFStatus.MISSING, t.getStatus());
        assertFalse(t.getBaseForecast().isPresent());

        t = TAFImpl.builder().setStatus(AviationCodeListUser.TAFStatus.MISSING).setBaseForecast(TAFBaseForecastImpl.builder().buildPartial()).buildPartial();
        assertEquals(TAF.TAFStatus.NORMAL, t.getStatus());
        assertTrue(t.getBaseForecast().isPresent());

        t = TAFImpl.builder().setStatus(AviationCodeListUser.TAFStatus.MISSING).buildPartial();
        assertEquals(TAF.TAFStatus.MISSING, t.getStatus());

    }

    @Test
    public void testReportStatus() {
        TAF t = TAFImpl.builder()//
                .setBaseForecast(TAFBaseForecastImpl.builder().buildPartial())//
                .setReportStatus(AviationWeatherMessage.ReportStatus.NORMAL)//
                .buildPartial();
        assertEquals(AviationCodeListUser.TAFStatus.NORMAL, t.getStatus());
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
        assertEquals(TAF.TAFStatus.AMENDMENT, t.getStatus());
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
        assertEquals(TAF.TAFStatus.CORRECTION, t.getStatus());
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
        assertEquals(AviationCodeListUser.TAFStatus.CANCELLATION, t.getStatus());
        assertTrue(t.isCancelMessage());
        assertTrue(t.getReportStatus().isPresent());
        assertEquals(AviationWeatherMessage.ReportStatus.AMENDMENT, t.getReportStatus().get());

        t = TAFImpl.builder().setReportStatus(AviationWeatherMessage.ReportStatus.AMENDMENT).setCancelMessage(true);
        assertEquals(AviationCodeListUser.TAFStatus.CANCELLATION, t.getStatus());
        assertTrue(t.isCancelMessage());
        assertTrue(t.getReportStatus().isPresent());
        assertEquals(AviationWeatherMessage.ReportStatus.AMENDMENT, t.getReportStatus().get());

        t = TAFImpl.builder().setReportStatus(AviationWeatherMessage.ReportStatus.NORMAL).setCancelMessage(true);
        assertEquals(AviationCodeListUser.TAFStatus.CANCELLATION, t.getStatus());
        assertTrue(t.isCancelMessage());
        assertTrue(t.getReportStatus().isPresent());
        assertEquals(AviationWeatherMessage.ReportStatus.NORMAL, t.getReportStatus().get());

        t = TAFImpl.builder().setCancelMessage(true).setReportStatus(AviationWeatherMessage.ReportStatus.NORMAL);
        assertEquals(AviationCodeListUser.TAFStatus.CANCELLATION, t.getStatus());
        assertTrue(t.isCancelMessage());
        assertTrue(t.getReportStatus().isPresent());
        assertEquals(AviationWeatherMessage.ReportStatus.NORMAL, t.getReportStatus().get());

        t = TAFImpl.builder().setReportStatus(AviationWeatherMessage.ReportStatus.CORRECTION).setCancelMessage(true);
        assertEquals(AviationCodeListUser.TAFStatus.CANCELLATION, t.getStatus());
        assertTrue(t.isCancelMessage());
        assertTrue(t.getReportStatus().isPresent());
        assertEquals(AviationWeatherMessage.ReportStatus.CORRECTION, t.getReportStatus().get());

        t = TAFImpl.builder().setCancelMessage(true).setReportStatus(AviationWeatherMessage.ReportStatus.CORRECTION);
        assertEquals(AviationCodeListUser.TAFStatus.CANCELLATION, t.getStatus());
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
        TAF t = TAFImpl.builder()//
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
        TAF t = TAFImpl.builder()//
                .setReferredReport(TAFReferenceImpl.builder()//
                        .setValidityTime(tp1)//
                        .buildPartial())//
                .buildPartial();
        assertFalse(t.getReferredReportValidPeriod().isPresent());

        t = TAFImpl.builder()//
                .setStatus(AviationCodeListUser.TAFStatus.CANCELLATION)//
                .setReferredReport(TAFReferenceImpl.builder()//
                        .setValidityTime(tp1)//
                        .buildPartial())//
                .buildPartial();
        assertTrue(t.getReferredReportValidPeriod().isPresent());
        assertEquals(tp1, t.getReferredReportValidPeriod().get());

        t = TAFImpl.builder()//
                .setReferredReport(TAFReferenceImpl.builder()//
                        .setValidityTime(tp1)//
                        .buildPartial())//
                .setStatus(AviationCodeListUser.TAFStatus.CANCELLATION)//
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
        assertFalse(t.getReferredReportValidPeriod().isPresent());

        t = TAFImpl.builder()
                .setReferredReport(TAFReferenceImpl.builder().setAerodrome(ad).setValidityTime(tp1).build())
                .setStatus(AviationCodeListUser.TAFStatus.CANCELLATION)
                .buildPartial();
        assertTrue(t.getReferredReportValidPeriod().isPresent());
        assertEquals(tp1, t.getReferredReportValidPeriod().get());

        t = TAFImpl.builder()
                .setReferredReport(TAFReferenceImpl.builder().setAerodrome(ad).setValidityTime(tp1).build())
                .setStatus(AviationCodeListUser.TAFStatus.AMENDMENT)
                .buildPartial();
        assertTrue(t.getReferredReportValidPeriod().isPresent());
        assertEquals(tp1, t.getReferredReportValidPeriod().get());

        t = TAFImpl.builder()
                .setReferredReport(TAFReferenceImpl.builder().setAerodrome(ad).setValidityTime(tp1).build())
                .setStatus(AviationCodeListUser.TAFStatus.CORRECTION)
                .buildPartial();
        assertTrue(t.getReferredReportValidPeriod().isPresent());
        assertEquals(tp1, t.getReferredReportValidPeriod().get());

        assertThrows("setReferredReport should throw an IllegalArgumentException when given a conflicting aerodrome", IllegalArgumentException.class,
                new ThrowingRunnable() {
                    @Override
                    public void run() throws Throwable {
                        final TAF t = TAFImpl.builder().setAerodrome(ad)//
                                .setReferredReport(TAFReferenceImpl.builder().setAerodrome(ad2).setValidityTime(tp1).build()).buildPartial();
                    }
                });
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

}
