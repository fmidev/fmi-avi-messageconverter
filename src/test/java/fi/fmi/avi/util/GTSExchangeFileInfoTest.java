package fi.fmi.avi.util;

import fi.fmi.avi.model.PartialDateTime;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.bulletin.BulletinHeading;
import fi.fmi.avi.model.bulletin.DataTypeDesignatorT1;
import fi.fmi.avi.model.bulletin.DataTypeDesignatorT2;
import fi.fmi.avi.model.bulletin.immutable.BulletinHeadingImpl;
import fi.fmi.avi.model.taf.immutable.TAFBulletinImpl;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.EnumSet;

import static org.assertj.core.api.Assertions.assertThat;

public class GTSExchangeFileInfoTest {

    private static BulletinHeading createTestHeading() {
        return BulletinHeadingImpl.builder()
                .setLocationIndicator("EFKL")
                .setGeographicalDesignator("FI")
                .setDataTypeDesignatorT1ForTAC(DataTypeDesignatorT1.FORECASTS)
                .setDataTypeDesignatorT2(DataTypeDesignatorT2.ForecastsDataTypeDesignatorT2.FCT_AERODROME_VT_LONG)
                .setBulletinNumber(31)
                .setIssueTime(PartialOrCompleteTimeInstant.of(PartialDateTime.ofDayHourMinute(25, 14, 0)))
                .build();
    }

    private static BulletinHeading createCorrectedTestHeading() {
        return BulletinHeadingImpl.builder()
                .setLocationIndicator("ABCD")
                .setAugmentationIndicator(BulletinHeading.Type.CORRECTED, 1)
                .setGeographicalDesignator("FI")
                .setDataTypeDesignatorT1ForTAC(DataTypeDesignatorT1.FORECASTS)
                .setDataTypeDesignatorT2(DataTypeDesignatorT2.ForecastsDataTypeDesignatorT2.FCT_AERODROME_VT_LONG)
                .setBulletinNumber(12)
                .setIssueTime(PartialOrCompleteTimeInstant.of(PartialDateTime.ofDayHourMinute(9, 10, 0)))
                .build();
    }

    @Test
    public void testFileNameGenerator() {
        final GTSExchangeFileInfo info = new GTSExchangeFileInfo.Builder()
                .setPFlag(GTSExchangeFileInfo.GTSExchangePFlag.A)
                .setMetadataFile(true)
                .setFileType(GTSExchangeFileInfo.GTSExchangeFileType.METADATA)
                .setCompressionType(GTSExchangeFileInfo.GTSExchangeCompressionType.GZIP)
                .setHeading(createCorrectedTestHeading())
                .setFreeFormPart("foobar12345_-")
                .setTimeStamp(LocalDateTime.of(2019, Month.JANUARY, 9, 10, 5))
                .build();

        assertThat(info.toGTSExchangeFilename()).isEqualTo("AM_FTFI12ABCD091000CCA_C_ABCD_20190109100500_foobar12345_-.met.gz");
    }

    @Test
    public void testFileNameParser() {
        final String filename = "AM_FTFI12ABCD091000CCA_C_ABCD_201901091005--_foobar12345_-.met.gz";
        final GTSExchangeFileInfo info = GTSExchangeFileInfo.Builder.from(filename).build();

        assertThat(info.getPFlag()).isSameAs(GTSExchangeFileInfo.GTSExchangePFlag.A);
        assertThat(info.isMetadataFile()).isTrue();
        assertThat(info.getFileType()).isSameAs(GTSExchangeFileInfo.GTSExchangeFileType.METADATA);
        assertThat(info.getCompressionType()).hasValue(GTSExchangeFileInfo.GTSExchangeCompressionType.GZIP);
        assertThat(info.getHeading()).isEqualTo(createCorrectedTestHeading());
        assertThat(info.getFreeFormPart()).hasValue("foobar12345_-");
        assertThat(info.getTimeStampYear()).hasValue(2019);
        assertThat(info.getTimeStampMonth()).hasValue(Month.JANUARY);
        assertThat(info.getTimeStampDay()).hasValue(9);
        assertThat(info.getTimeStampHour()).hasValue(10);
        assertThat(info.getTimeStampMinute()).hasValue(5);
        assertThat(info.getTimeStampSecond()).isEmpty();
    }

    @Test
    public void testFromBulletin() {
        final BulletinHeading heading = createTestHeading();
        final TAFBulletinImpl bulletin = TAFBulletinImpl.builder()
                .setHeading(heading)
                .setTimeStamp(ZonedDateTime.of(2019, 11, 25, 14, 38, 0, 0, ZoneId.of("UTC")))
                .addAllTimeStampFields(EnumSet.of(
                        ChronoField.YEAR, ChronoField.MONTH_OF_YEAR, ChronoField.DAY_OF_MONTH,
                        ChronoField.HOUR_OF_DAY, ChronoField.MINUTE_OF_HOUR, ChronoField.SECOND_OF_MINUTE))
                .build();

        final GTSExchangeFileInfo info = GTSExchangeFileInfo.Builder.from(bulletin)
                .setFileType(GTSExchangeFileInfo.GTSExchangeFileType.XML)
                .build();

        assertThat(info.getHeading()).isEqualTo(heading);
        assertThat(info.getFileType()).isSameAs(GTSExchangeFileInfo.GTSExchangeFileType.XML);
        assertThat(info.isMetadataFile()).isFalse();
        assertThat(info.getTimeStampYear()).hasValue(2019);
        assertThat(info.getTimeStampMonth()).hasValue(Month.NOVEMBER);
        assertThat(info.getTimeStampDay()).hasValue(25);
        assertThat(info.getTimeStampHour()).hasValue(14);
        assertThat(info.getTimeStampMinute()).hasValue(38);
        assertThat(info.getTimeStampSecond()).hasValue(0);
        assertThat(info.toGTSExchangeFilename()).isEqualTo("A_LTFI31EFKL251400_C_EFKL_20191125143800.xml");
    }

    @Test
    public void testFromBulletinWithPartialTimestamp() {
        final BulletinHeading heading = createTestHeading();
        final TAFBulletinImpl bulletin = TAFBulletinImpl.builder()
                .setHeading(heading)
                .setTimeStamp(ZonedDateTime.of(2019, 11, 25, 14, 38, 45, 0, ZoneId.of("UTC")))
                .addAllTimeStampFields(EnumSet.of(ChronoField.YEAR, ChronoField.MONTH_OF_YEAR, ChronoField.DAY_OF_MONTH))
                .build();

        final GTSExchangeFileInfo info = GTSExchangeFileInfo.Builder.from(bulletin)
                .setFileType(GTSExchangeFileInfo.GTSExchangeFileType.TEXT)
                .build();

        assertThat(info.getTimeStampYear()).hasValue(2019);
        assertThat(info.getTimeStampMonth()).hasValue(Month.NOVEMBER);
        assertThat(info.getTimeStampDay()).hasValue(25);
        assertThat(info.getTimeStampHour()).isEmpty();
        assertThat(info.getTimeStampMinute()).isEmpty();
        assertThat(info.getTimeStampSecond()).isEmpty();
        assertThat(info.toGTSExchangeFilename()).isEqualTo("A_FTFI31EFKL251400_C_EFKL_20191125------.txt");
    }
}
