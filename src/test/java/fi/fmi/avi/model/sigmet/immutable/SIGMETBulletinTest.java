package fi.fmi.avi.model.sigmet.immutable;

import org.junit.Test;

import java.time.ZonedDateTime;

import fi.fmi.avi.model.Airspace;
import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.PartialDateTime;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.bulletin.DataTypeDesignatorT2;
import fi.fmi.avi.model.bulletin.immutable.BulletinHeadingImpl;
import fi.fmi.avi.model.immutable.AirspaceImpl;
import fi.fmi.avi.model.immutable.UnitPropertyGroupImpl;

public class SIGMETBulletinTest {
    @Test
    public void testSIGMETBulletin() {
        final SIGMETBulletinImpl.Builder builder = SIGMETBulletinImpl.builder()//
                .setHeading(BulletinHeadingImpl.builder()//
                        .setGeographicalDesignator("FI")//
                        .setLocationIndicator("EFKL")//
                        .setBulletinNumber(31)//
                        .setDataTypeDesignatorT2(DataTypeDesignatorT2.WarningsDataTypeDesignatorT2.WRN_SIGMET)
                        .setIssueTime(PartialOrCompleteTimeInstant.of(PartialDateTime.ofDayHourMinute(17, 7, 0)))
                        .build());

        builder.addMessages(SIGMETImpl.builder()//
                .setIssuingAirTrafficServicesUnit(
                        new UnitPropertyGroupImpl.Builder().setPropertyGroup("HELSINKI FIR", "EFIN", "FIR").build())
                .setMeteorologicalWatchOffice(
                        new UnitPropertyGroupImpl.Builder().setPropertyGroup("Helsinki", "EFKL", "MWO").build())
                .setAirspace(new AirspaceImpl.Builder()
                        .setDesignator("EFIN")
                        .setType(Airspace.AirspaceType.FIR)
                        .setName("HELSINKI FIR")
                        .build())
                .setValidityPeriod(PartialOrCompleteTimePeriod.builder()
                        .setStartTime(PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2017-08-27T07:50:00Z")))
                        .setEndTime(PartialOrCompleteTimeInstant.of(ZonedDateTime.parse("2017-08-27T09:50:00Z")))
                        .build())
                .setPhenomenonType(AviationCodeListUser.SigmetPhenomenonType.SIGMET)
                .setSequenceNumber("1")
                .setTranslatedTAC("EFIN SIGMET 1 VALID 170750/170950 EFKL-\n"//
                        + "EFIN FINLAND FIR SEV TURB FCST AT 0740Z\n"//
                        + "S OF LINE N5953 E01931 -\n"//
                        + "N6001 E02312 - N6008 E02606 - N6008\n"//
                        + "E02628 FL220-340 MOV N 15KT\n"//
                        + "WKN=").setTranslated(false).build());
        builder.build();
    }
}
