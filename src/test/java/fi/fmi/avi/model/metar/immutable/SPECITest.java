package fi.fmi.avi.model.metar.immutable;

import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.metar.SPECI;
import org.junit.Test;

import java.time.Month;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SPECITest {

    @Test
    public void testBuildAsSPECI() {
        SPECI msg = new METARImpl.Builder()//
                .setIssueTime(PartialOrCompleteTimeInstant.createIssueTime("311004Z"))//
                .withCompleteIssueTime(YearMonth.of(2017, Month.DECEMBER))//
                .buildPartialAsSPECI();

        assertTrue(msg.getIssueTime().getPartialTime().isPresent());
        assertTrue(msg.getIssueTime().getPartialTime().get().equals("311004Z"));
        PartialOrCompleteTimeInstant it = msg.getIssueTime();

        ZonedDateTime toMatch = ZonedDateTime.of(2017, 12, 31, 10, 4, 0, 0, ZoneId.of("Z"));
        assertFalse(it.isMidnight24h());
        assertTrue(it.getCompleteTime().isPresent());
        assertTrue(it.getCompleteTime().get().equals(toMatch));


    }
}
