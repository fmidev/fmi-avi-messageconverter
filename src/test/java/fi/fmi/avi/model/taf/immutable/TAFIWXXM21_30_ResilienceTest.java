package fi.fmi.avi.model.taf.immutable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

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
        assertFalse(t.getCancelledReportValidPeriod().isPresent());
        assertFalse(t.getReferredReport().isPresent());

        t = TAFImpl.builder()//
                .setStatus(AviationCodeListUser.TAFStatus.CANCELLATION)//
                .setBaseForecast(TAFBaseForecastImpl.builder().buildPartial())//
                .buildPartial();
        assertEquals(t.getStatus(), TAF.TAFStatus.CANCELLATION);
        assertTrue(t.getReportStatus().isPresent());
        assertEquals(AviationWeatherMessage.ReportStatus.NORMAL, t.getReportStatus().get());
        assertTrue(t.isCancelMessage());
        assertFalse(t.isMissingMessage());
        assertFalse(t.getCancelledReportValidPeriod().isPresent());
        assertFalse(t.getReferredReport().isPresent());

        t = TAFImpl.builder()//
                .setStatus(AviationCodeListUser.TAFStatus.AMENDMENT)//
                .setBaseForecast(TAFBaseForecastImpl.builder().buildPartial())//
                .buildPartial();
        assertEquals(t.getStatus(), TAF.TAFStatus.AMENDMENT);
        assertTrue(t.getReportStatus().isPresent());
        assertEquals(AviationWeatherMessage.ReportStatus.AMENDMENT, t.getReportStatus().get());
        assertFalse(t.isCancelMessage());
        assertFalse(t.isMissingMessage());
        assertFalse(t.getCancelledReportValidPeriod().isPresent());
        assertFalse(t.getReferredReport().isPresent());

        t = TAFImpl.builder().setStatus(AviationCodeListUser.TAFStatus.CORRECTION).setBaseForecast(TAFBaseForecastImpl.builder().buildPartial())//
                .buildPartial();
        assertEquals(t.getStatus(), TAF.TAFStatus.CORRECTION);
        assertTrue(t.getReportStatus().isPresent());
        assertEquals(AviationWeatherMessage.ReportStatus.CORRECTION, t.getReportStatus().get());
        assertFalse(t.isCancelMessage());
        assertFalse(t.isMissingMessage());
        assertFalse(t.getCancelledReportValidPeriod().isPresent());
        assertFalse(t.getReferredReport().isPresent());
    }

    @Test
    public void testMissingMessage() {
        TAFImpl t = TAFImpl.builder().setBaseForecast(TAFBaseForecastImpl.builder().buildPartial()).buildPartial();
        assertEquals(t.getStatus(), TAF.TAFStatus.NORMAL);

        t = TAFImpl.builder().buildPartial();
        assertEquals(t.getStatus(), TAF.TAFStatus.MISSING);

        t = TAFImpl.builder().setBaseForecast(TAFBaseForecastImpl.builder().buildPartial()).setStatus(AviationCodeListUser.TAFStatus.MISSING).buildPartial();
        assertEquals(t.getStatus(), TAF.TAFStatus.NORMAL);

        t = TAFImpl.builder().setStatus(AviationCodeListUser.TAFStatus.MISSING).buildPartial();
        assertEquals(t.getStatus(), TAF.TAFStatus.MISSING);

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
        assertFalse(t.getCancelledReportValidPeriod().isPresent());
        assertFalse(t.getReferredReport().isPresent());

        t = TAFImpl.builder()//
                .setBaseForecast(TAFBaseForecastImpl.builder().buildPartial())//
                .setReportStatus(AviationWeatherMessage.ReportStatus.AMENDMENT)//
                .buildPartial();
        assertEquals(t.getStatus(), TAF.TAFStatus.AMENDMENT);
        assertTrue(t.getReportStatus().isPresent());
        assertEquals(AviationWeatherMessage.ReportStatus.AMENDMENT, t.getReportStatus().get());
        assertFalse(t.isCancelMessage());
        assertFalse(t.isMissingMessage());
        assertFalse(t.getCancelledReportValidPeriod().isPresent());
        assertFalse(t.getReferredReport().isPresent());

        t = TAFImpl.builder()//
                .setBaseForecast(TAFBaseForecastImpl.builder().buildPartial())//
                .setReportStatus(AviationWeatherMessage.ReportStatus.CORRECTION)//
                .buildPartial();
        assertEquals(t.getStatus(), TAF.TAFStatus.CORRECTION);
        assertTrue(t.getReportStatus().isPresent());
        assertEquals(AviationWeatherMessage.ReportStatus.CORRECTION, t.getReportStatus().get());
        assertFalse(t.isCancelMessage());
        assertFalse(t.isMissingMessage());
        assertFalse(t.getCancelledReportValidPeriod().isPresent());
        assertFalse(t.getReferredReport().isPresent());

    }

    @Test
    public void testStatusAndReportStatusConsistency() {
        TAFImpl.Builder t = TAFImpl.builder().setCancelMessage(true).setReportStatus(AviationWeatherMessage.ReportStatus.AMENDMENT);
        assertEquals(t.getStatus(), AviationCodeListUser.TAFStatus.CANCELLATION);
        assertTrue(t.getReportStatus().isPresent());
        assertEquals(t.getReportStatus().get(), AviationWeatherMessage.ReportStatus.AMENDMENT);

        t = TAFImpl.builder().setReportStatus(AviationWeatherMessage.ReportStatus.AMENDMENT).setCancelMessage(true);
        assertEquals(t.getStatus(), AviationCodeListUser.TAFStatus.CANCELLATION);
        assertTrue(t.getReportStatus().isPresent());
        assertEquals(t.getReportStatus().get(), AviationWeatherMessage.ReportStatus.AMENDMENT);
    }

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

    @Test
    public void testCancelledReportValidTimeAffectsReferredReportValidity() {
        final PartialOrCompleteTimePeriod tp1 = PartialOrCompleteTimePeriod.createValidityTime("0112/0214");
        final PartialOrCompleteTimePeriod tp2 = PartialOrCompleteTimePeriod.createValidityTime("0112/0214");
        assertEquals(tp2, tp1);
        TAF t = TAFImpl.builder()//
                .setCancelledReportValidPeriod(tp1)//
                .setAerodrome(AerodromeImpl.builder().setDesignator("FOO").build()).buildPartial();
        assertTrue(t.getReferredReport().isPresent());
        assertTrue(t.getReferredReport().get().getValidityTime().isPresent());
        assertEquals(tp2, t.getReferredReport().get().getValidityTime().get());
    }

    @Test
    public void testSetCancelMessageAffectsCancelledReportValidTime() {
        final PartialOrCompleteTimePeriod tp1 = PartialOrCompleteTimePeriod.createValidityTime("0112/0214");
        final PartialOrCompleteTimePeriod tp2 = PartialOrCompleteTimePeriod.createValidityTime("0112/0214");
        assertEquals(tp2, tp1);
        TAF t = TAFImpl.builder()//
                .setReferredReport(TAFReferenceImpl.builder()//
                        .setValidityTime(tp1)//
                        .buildPartial())//
                .buildPartial();
        assertFalse(t.getCancelledReportValidPeriod().isPresent());

        t = TAFImpl.builder()//
                .setReferredReport(TAFReferenceImpl.builder()//
                        .setValidityTime(tp1)//
                        .buildPartial())//
                .setCancelMessage(true).buildPartial();
        assertTrue(t.getCancelledReportValidPeriod().isPresent());
        assertEquals(tp2, t.getCancelledReportValidPeriod().get());

    }

    @Test
    public void testReferredReportAerodromeConsistencyOnBuild() {
        TAFImpl.builder()//
                .setAerodrome(AerodromeImpl.builder().setDesignator("FOO").build())//
                .setReferredReport(TAFReferenceImpl.builder()//
                        .setAerodrome(AerodromeImpl.builder().setDesignator("FOO").build())//
                        .buildPartial())//
                .build();

        assertThrows(IllegalStateException.class, () -> {
            TAFImpl.builder()//
                    .setAerodrome(AerodromeImpl.builder().setDesignator("FOO").build())//
                    .setReferredReport(TAFReferenceImpl.builder()//
                            .setAerodrome(AerodromeImpl.builder().setDesignator("BAR").build())//
                            .buildPartial())//
                    .build();
        });
    }

    @Test
    public void testCancelledReportReferredReportValidTimeConsistencyOnBuild() {
        final PartialOrCompleteTimePeriod tp1 = PartialOrCompleteTimePeriod.createValidityTime("0112/0214");
        final PartialOrCompleteTimePeriod tp2 = PartialOrCompleteTimePeriod.createValidityTime("0106/0212");
        TAFImpl.builder()//
                .setAerodrome(AerodromeImpl.builder().setDesignator("FOO").build())//
                .setCancelMessage(true).setReferredReport(TAFReferenceImpl.builder()//
                .setAerodrome(AerodromeImpl.builder().setDesignator("FOO").build()).setValidityTime(tp1)//
                .buildPartial())//
                .setCancelledReportValidPeriod(tp1).build();

        TAFImpl.builder()//
                .setAerodrome(AerodromeImpl.builder().setDesignator("FOO").build())//
                .setCancelledReportValidPeriod(tp2)//
                .setReferredReport(TAFReferenceImpl.builder()//
                        .setAerodrome(AerodromeImpl.builder().setDesignator("FOO").build())//
                        .setValidityTime(tp1)//
                        .buildPartial())//
                .setCancelMessage(true)//
                .build();

        TAFImpl.builder()//
                .setAerodrome(AerodromeImpl.builder().setDesignator("FOO").build())//
                .setCancelledReportValidPeriod(tp2)//
                .setCancelMessage(true)//
                .build();

    }

    @Test
    public void testCancelBuildOrderConsistency() {
        final PartialOrCompleteTimePeriod validityTime = PartialOrCompleteTimePeriod.createValidityTime("0112/0214");
        final PartialOrCompleteTimePeriod cancelledReportValidPeriod = PartialOrCompleteTimePeriod.createValidityTime("0106/0212");
        TAFImpl.Builder t = TAFImpl.builder().setValidityTime(validityTime).setCancelMessage(true).setCancelledReportValidPeriod(cancelledReportValidPeriod);
        assertTrue(t.getCancelledReportValidPeriod().isPresent());
        assertEquals(t.getCancelledReportValidPeriod().get(), cancelledReportValidPeriod);

        t = TAFImpl.builder().setValidityTime(validityTime).setCancelledReportValidPeriod(cancelledReportValidPeriod).setCancelMessage(true);
        assertTrue(t.getCancelledReportValidPeriod().isPresent());
        assertEquals(t.getCancelledReportValidPeriod().get(), cancelledReportValidPeriod);
    }

}
