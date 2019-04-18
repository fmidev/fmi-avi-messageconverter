package fi.fmi.avi.model.taf.impl;

import org.junit.Ignore;
import org.junit.Test;

import fi.fmi.avi.model.BulletinHeading;
import fi.fmi.avi.model.PartialDateTime;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.immutable.BulletinHeadingImpl;
import fi.fmi.avi.model.sigmet.immutable.SIGMETBulletinImpl;
import fi.fmi.avi.model.sigmet.immutable.SIGMETImpl;

public class SIGMETBulletinTest {

    @Test
    @Ignore
    public void testSIGMETBulletin() throws Exception {
        final SIGMETBulletinImpl.Builder builder = SIGMETBulletinImpl.builder()//
                .setHeading(BulletinHeadingImpl.builder()//
                        .setGeographicalDesignator("FI")//
                        .setLocationIndicator("EFKL")//
                        .setBulletinNumber(31)//
                        .setDataTypeDesignatorT2(BulletinHeading.WarningsDataTypeDesignatorT2.WRN_SIGMET)
                        .setIssueTime(PartialOrCompleteTimeInstant.of(PartialDateTime.ofDayHourMinute(17, 7, 0)))
                        .build());

        builder.addMessages(SIGMETImpl.builder()//
                .setTranslatedTAC("EFIN SIGMET 1 VALID 170750/170950 EFKL-\n"//
                        + "EFIN FINLAND FIR SEV TURB FCST AT 0740Z\n"//
                        + "S OF LINE N5953 E01931 -\n"//
                        + "N6001 E02312 - N6008 E02606 - N6008\n"//
                        + "E02628 FL220-340 MOV N 15KT\n"//
                        + "WKN=").setTranslated(false).buildPartial());
        builder.build();
    }
}
