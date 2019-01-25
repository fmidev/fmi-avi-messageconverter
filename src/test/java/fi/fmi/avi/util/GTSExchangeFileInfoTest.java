package fi.fmi.avi.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.time.Month;

import org.junit.Test;

import fi.fmi.avi.model.BulletinHeading;
import fi.fmi.avi.model.PartialDateTime;
import fi.fmi.avi.model.immutable.GenericBulletinHeadingImpl;
import fi.fmi.avi.model.taf.TAFBulletinHeading;
import fi.fmi.avi.model.taf.immutable.TAFBulletinHeadingImpl;

public class GTSExchangeFileInfoTest {
    @Test
    public void testFileNameGenerator() {
        GTSExchangeFileInfo info = new GTSExchangeFileInfo.Builder().setPFlag(GTSExchangeFileInfo.GTSExchangePFlag.A)
                .setMetadataFile(true)
                .setFileType(GTSExchangeFileInfo.GTSExchangeFileType.METADATA)
                .setCompressionType(GTSExchangeFileInfo.GTSExchangeCompressionType.GZIP)
                .setHeading(new TAFBulletinHeadingImpl.Builder().setLocationIndicator("ABCD")
                        .setBulletinAugmentationNumber('A')
                        .setGeographicalDesignator("FI")
                        .setDataTypeDesignatorT2(BulletinHeading.ForecastsDataTypeDesignatorT2.AERODROME_VT_LONG)
                        .setType(TAFBulletinHeading.Type.CORRECTED)
                        .setBulletinNumber(12)
                        .build())
                .setIssueTime(PartialDateTime.ofDayHourMinute(9, 10, 0))
                .setFreeFormPart("foobar12345_-")
                .setTimeStamp(LocalDateTime.of(2019, Month.JANUARY, 9, 10, 5))
                .build();

        assertEquals("AM_FTFI12ABCD091000CCA_C_ABCD_201901091005--_foobar12345_-.met.gz", info.toGTSExchangeFileName());
    }

    @Test
    public void testFileNameParser() {
        GTSExchangeFileInfo info = GTSExchangeFileInfo.Builder.from("AM_FTFI12ABCD091000CCA_C_ABCD_201901091005--_foobar12345_-.met.gz").build();
        BulletinHeading expectedHeading = new GenericBulletinHeadingImpl.Builder().setLocationIndicator("ABCD")
                .setBulletinAugmentationNumber('A')
                .setGeographicalDesignator("FI")
                .setDataTypeDesignatorT1ForTAC(BulletinHeading.DataTypeDesignatorT1.FORECASTS)
                .setDataTypeDesignatorT2(BulletinHeading.ForecastsDataTypeDesignatorT2.AERODROME_VT_LONG)
                .setType(TAFBulletinHeading.Type.CORRECTED)
                .setBulletinNumber(12)
                .build();

        assertTrue(GTSExchangeFileInfo.GTSExchangePFlag.A == info.getPFlag());
        assertTrue(info.isMetadataFile());
        assertTrue(GTSExchangeFileInfo.GTSExchangeFileType.METADATA == info.getFileType());
        assertTrue(info.getCompressionType().isPresent());
        assertTrue(GTSExchangeFileInfo.GTSExchangeCompressionType.GZIP == info.getCompressionType().get());
        assertEquals(expectedHeading, info.getHeading());
        assertEquals(PartialDateTime.ofDayHourMinute(9, 10, 0), info.getIssueTime());
        assertTrue(info.getFreeFormPart().isPresent());
        assertEquals("foobar12345_-", info.getFreeFormPart().get());

        assertTrue(info.getTimeStampYear().isPresent());
        assertEquals(2019, info.getTimeStampYear().get().intValue());

        assertTrue(info.getTimeStampMonth().isPresent());
        assertEquals(Month.JANUARY, info.getTimeStampMonth().get());

        assertTrue(info.getTimeStampDay().isPresent());
        assertEquals(9, info.getTimeStampDay().get().intValue());

        assertTrue(info.getTimeStampHour().isPresent());
        assertEquals(10, info.getTimeStampHour().get().intValue());

        assertTrue(info.getTimeStampMinute().isPresent());
        assertEquals(5, info.getTimeStampMinute().get().intValue());

        assertFalse(info.getTimeStampSecond().isPresent());

    }
}
