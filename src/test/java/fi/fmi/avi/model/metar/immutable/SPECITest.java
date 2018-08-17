package fi.fmi.avi.model.metar.immutable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.time.Month;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import org.junit.Test;

import fi.fmi.avi.model.PartialDateTime;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.metar.SPECI;

public class SPECITest {

    @Test
    public void testBuildAsSPECI() {
        final SPECI msg = new METARImpl.Builder()//
                .setIssueTime(PartialOrCompleteTimeInstant.createIssueTime("311004Z"))//
                .withCompleteIssueTime(YearMonth.of(2017, Month.DECEMBER))//
                .buildPartialAsSPECI();

        assertEquals(Optional.of(PartialDateTime.parse("--31T10:04Z")), msg.getIssueTime().getPartialTime());
        final PartialOrCompleteTimeInstant it = msg.getIssueTime();

        final ZonedDateTime toMatch = ZonedDateTime.of(2017, 12, 31, 10, 4, 0, 0, ZoneId.of("Z"));
        assertFalse(it.isMidnight24h());
        assertEquals(Optional.of(toMatch), it.getCompleteTime());
    }
}
