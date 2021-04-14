package fi.fmi.avi.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.time.Month;

import org.junit.Test;

import fi.fmi.avi.model.PartialDateTime;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.bulletin.BulletinHeading;
import fi.fmi.avi.model.bulletin.DataTypeDesignatorT1;
import fi.fmi.avi.model.bulletin.DataTypeDesignatorT2;
import fi.fmi.avi.model.bulletin.immutable.BulletinHeadingImpl;

public class GTSExchangeFileInfoTest {
    @Test
    public void testFileNameGenerator() {
        final GTSExchangeFileInfo info = new GTSExchangeFileInfo.Builder().setPFlag(GTSExchangeFileInfo.GTSExchangePFlag.A)
                .setMetadataFile(true)
                .setFileType(GTSExchangeFileInfo.GTSExchangeFileType.METADATA)
                .setCompressionType(GTSExchangeFileInfo.GTSExchangeCompressionType.GZIP)
                .setHeading(BulletinHeadingImpl.builder()//
                        .setLocationIndicator("ABCD")//
                        .setBulletinAugmentationNumber('A')//
                        .setGeographicalDesignator("FI")//
                        .setDataTypeDesignatorT1ForTAC(DataTypeDesignatorT1.FORECASTS)
                        .setDataTypeDesignatorT2(DataTypeDesignatorT2.ForecastsDataTypeDesignatorT2.FCT_AERODROME_VT_LONG)//
                        .setType(BulletinHeading.Type.CORRECTED)//
                        .setBulletinNumber(12)//
                        .setIssueTime(PartialOrCompleteTimeInstant.of(PartialDateTime.ofDayHourMinute(9, 10, 0)))
                        .build())

                .setFreeFormPart("foobar12345_-")
                .setTimeStamp(LocalDateTime.of(2019, Month.JANUARY, 9, 10, 5))
                .build();

        assertEquals("AM_FTFI12ABCD091000CCA_C_ABCD_20190109100500_foobar12345_-.met.gz", info.toGTSExchangeFileName());
    }

    @Test
    public void testFileNameParser() {
        final GTSExchangeFileInfo info = GTSExchangeFileInfo.Builder.from("AM_FTFI12ABCD091000CCA_C_ABCD_201901091005--_foobar12345_-.met.gz").build();
        final BulletinHeading expectedHeading = BulletinHeadingImpl.builder()
                .setLocationIndicator("ABCD")
                .setBulletinAugmentationNumber('A')
                .setGeographicalDesignator("FI")
                .setDataTypeDesignatorT1ForTAC(DataTypeDesignatorT1.FORECASTS)
                .setDataTypeDesignatorT2(DataTypeDesignatorT2.ForecastsDataTypeDesignatorT2.FCT_AERODROME_VT_LONG)
                .setType(BulletinHeading.Type.CORRECTED)
                .setBulletinNumber(12)
                .setIssueTime(PartialOrCompleteTimeInstant.of(PartialDateTime.ofDayHourMinute(9, 10, 0)))
                .build();

        assertSame(GTSExchangeFileInfo.GTSExchangePFlag.A, info.getPFlag());
        assertTrue(info.isMetadataFile());
        assertSame(GTSExchangeFileInfo.GTSExchangeFileType.METADATA, info.getFileType());
        assertTrue(info.getCompressionType().isPresent());
        assertSame(GTSExchangeFileInfo.GTSExchangeCompressionType.GZIP, info.getCompressionType().get());
        assertEquals(expectedHeading, info.getHeading());

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
