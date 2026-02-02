package fi.fmi.avi.util;

import fi.fmi.avi.model.bulletin.BulletinHeading;
import fi.fmi.avi.model.bulletin.MeteorologicalBulletin;
import fi.fmi.avi.model.bulletin.immutable.BulletinHeadingImpl;
import org.inferred.freebuilder.FreeBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@FreeBuilder
public abstract class GTSExchangeFileInfo implements Serializable {

    private static final Set<TimeStampField> DEFAULT_TIME_FIELDS = new HashSet<>(Arrays.asList(TimeStampField.YEAR, //
            TimeStampField.MONTH, //
            TimeStampField.DAY, //
            TimeStampField.HOUR, //
            TimeStampField.MINUTE, //
            TimeStampField.SECOND));
    private static final long serialVersionUID = -3155603027188089668L;
    //A_T1T2A1A2iiCCCCYYGGgg[BBB]_C_CCCC_yyyyMMddhhmmss[_freeformat].type[.compression]
    private static final Pattern P_FLAG_A_PATTERN = Pattern.compile("^A(?<meta>M)?_" + "(?<T1T2>[A-Z]{2})(?<A1A2>[A-Z]{2})(?<ii>[0-9]{2})(?<CCCC>[A-Z]{4})"
            + "(?<issueDay>[0-9]{2})(?<issueHour>[0-9]{2})(?<issueMinute>[0-9]{2})(?<BBB>(CC|RR|AA)[A-Z])?" + "_C_[A-Z]{4}_"
            + "(?<yyyy>[0-9]{4}|----)(?<MM>[0-9]{2}|--)(?<dd>[0-9]{2}|--)(?<hh>[0-9]{2}|--)(?<mm>[0-9]{2}|--)(?<ss>[0-9]{2}|--)"
            + "(_(?<freeForm>[a-zA-Z0-9_-]*))?.(?<type>[a-z]{2,3})(.(?<compression>[a-zA_Z0-9]{1,3}))?$");

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new Builder();
    }

    public abstract GTSExchangePFlag getPFlag();

    public abstract BulletinHeading getHeading();

    public abstract GTSExchangeFileType getFileType();

    public abstract Optional<GTSExchangeCompressionType> getCompressionType();

    public abstract Optional<String> getFreeFormPart();

    public abstract boolean isMetadataFile();

    //TODO: adopt PartialDateTime to work with year, month and second fields too
    //Until then, use separate int fields in this class:
    public abstract Optional<Integer> getTimeStampYear();

    public abstract Optional<Month> getTimeStampMonth();

    public abstract Optional<Integer> getTimeStampDay();

    public abstract Optional<Integer> getTimeStampHour();

    public abstract Optional<Integer> getTimeStampMinute();

    public abstract Optional<Integer> getTimeStampSecond();

    public String toGTSExchangeFilename() {
        return this.toGTSExchangeFilename(DEFAULT_TIME_FIELDS);
    }

    public String toGTSExchangeFilename(final Set<TimeStampField> fieldsToInclude) {

        //TODO: rest of the types
        switch (this.getPFlag()) {
            case A:
                return createATypeFilename(fieldsToInclude);
            default:
                throw new IllegalStateException("Names with pFlag value '" + this.getPFlag() + "' not yet supported");
        }

    }

    private String createATypeFilename(final Set<TimeStampField> fieldsToInclude) {
        final StringBuilder sb = new StringBuilder();
        sb.append('A');
        if (this.isMetadataFile()) {
            sb.append('M');
        }
        sb.append('_');

        //T1T2A1A2ii
        if (this.getFileType() == GTSExchangeFileType.XML) {
            sb.append(this.getHeading().getDataDesignatorsForXML());
        } else {
            sb.append(this.getHeading().getDataDesignatorsForTAC());
        }

        //CCCC:
        sb.append(this.getHeading().getLocationIndicator());

        //YYGGgg:
        final OptionalInt dayOfMonth = this.getHeading().getIssueTime().getDay();
        final OptionalInt hourOfDay = this.getHeading().getIssueTime().getHour();
        final OptionalInt minute = this.getHeading().getIssueTime().getMinute();
        if (!dayOfMonth.isPresent() || !hourOfDay.isPresent() || !minute.isPresent()) {
            throw new IllegalArgumentException("Issue time must be given with day, hour and minute information");
        }
        sb.append(String.format("%02d", dayOfMonth.getAsInt()));
        sb.append(String.format("%02d", hourOfDay.getAsInt()));
        sb.append(String.format("%02d", minute.getAsInt()));

        //BBB:
        final Optional<Integer> augNumber = this.getHeading().getAugmentationNumber();
        if (augNumber.isPresent()) {
            int seqNumber = augNumber.get();
            seqNumber = 'A' + seqNumber - 1;
            sb.append(this.getHeading().getType().getPrefix());
            sb.append(Character.toChars(seqNumber));
        }

        sb.append('_');
        sb.append('C');
        sb.append('_');
        sb.append(this.getHeading().getLocationIndicator());
        sb.append('_');

        final Optional<Integer> timeStampYear = this.getTimeStampYear();
        if (fieldsToInclude.contains(TimeStampField.YEAR) && timeStampYear.isPresent()) {
            sb.append(String.format("%04d", timeStampYear.get()));
        } else {
            sb.append("----");
        }
        final Optional<Month> timeStampMonth = this.getTimeStampMonth();
        if (fieldsToInclude.contains(TimeStampField.MONTH) && timeStampMonth.isPresent()) {
            sb.append(String.format("%02d", timeStampMonth.get().getValue()));
        } else {
            sb.append("--");
        }
        final Optional<Integer> timeStampDay = this.getTimeStampDay();
        if (fieldsToInclude.contains(TimeStampField.DAY) && timeStampDay.isPresent()) {
            sb.append(String.format("%02d", timeStampDay.get()));
        } else {
            sb.append("--");
        }
        final Optional<Integer> timeStampHour = this.getTimeStampHour();
        if (fieldsToInclude.contains(TimeStampField.HOUR) && timeStampHour.isPresent()) {
            sb.append(String.format("%02d", timeStampHour.get()));
        } else {
            sb.append("--");
        }
        final Optional<Integer> timeStampMinute = this.getTimeStampMinute();
        if (fieldsToInclude.contains(TimeStampField.MINUTE) && timeStampMinute.isPresent()) {
            sb.append(String.format("%02d", timeStampMinute.get()));
        } else {
            sb.append("--");
        }
        final Optional<Integer> timeStampSecond = this.getTimeStampSecond();
        if (fieldsToInclude.contains(TimeStampField.SECOND) && timeStampSecond.isPresent()) {
            sb.append(String.format("%02d", timeStampSecond.get()));
        } else {
            sb.append("--");
        }

        if (this.getFreeFormPart().isPresent()) {
            sb.append('_');
            sb.append(this.getFreeFormPart().get());
        }
        sb.append('.');
        sb.append(this.getFileType().getExtension());
        if (this.getCompressionType().isPresent()) {
            sb.append('.');
            sb.append(this.getCompressionType().get().getExtension());
        }
        return sb.toString();
    }

    private DateTimeFormatter getFormatter(final Set<TimeStampField> fieldsToInclude) {
        final StringBuilder sb = new StringBuilder();
        if (fieldsToInclude.contains(TimeStampField.YEAR)) {
            sb.append("yyyy");
        } else {
            sb.append("----");
        }

        if (fieldsToInclude.contains(TimeStampField.MONTH)) {
            sb.append("MM");
        } else {
            sb.append("--");
        }

        if (fieldsToInclude.contains(TimeStampField.DAY)) {
            sb.append("dd");
        } else {
            sb.append("--");
        }

        if (fieldsToInclude.contains(TimeStampField.HOUR)) {
            sb.append("HH");
        } else {
            sb.append("--");
        }

        if (fieldsToInclude.contains(TimeStampField.MINUTE)) {
            sb.append("mm");
        } else {
            sb.append("--");
        }

        if (fieldsToInclude.contains(TimeStampField.SECOND)) {
            sb.append("ss");
        } else {
            sb.append("--");
        }
        return DateTimeFormatter.ofPattern(sb.toString());
    }

    public enum TimeStampField {YEAR, MONTH, DAY, HOUR, MINUTE, SECOND}

    public enum GTSExchangePFlag {T, A, W, Z, X}

    public enum GTSExchangeFileType {
        METADATA("met"), //
        TIFF("tif"), //
        GIF("gif"), //
        PNG("png"), //
        POSTSCRIPT("ps"), //
        MPEG("mpg"), //
        JPEG("jpg"), //
        TEXT("txt"), //
        HTML("htm"), //
        WMO_BINARY("bin"), //
        MS_WORD("doc"), //
        COREL_WORD_PERFECT("wpd"), //
        HDF("hdf"), //
        NETCDF("nc"), //
        PDF("pdf"), //
        XML("xml");

        private final String extension;

        GTSExchangeFileType(final String extension) {
            this.extension = extension;
        }

        public static GTSExchangeFileType fromExtension(final String code) {
            for (final GTSExchangeFileType t : GTSExchangeFileType.values()) {
                if (t.getExtension().equals(code)) {
                    return t;
                }
            }
            throw new IllegalArgumentException("Unknown extension '" + code + "'");
        }

        public String getExtension() {
            return this.extension;
        }
    }

    public enum GTSExchangeCompressionType {
        UNIX_COMPRESS("Z"), //
        ZIP("zip"), //
        GZIP("gz"), //
        BZIP2("bz2"), //
        XZ("xz");

        private final String extension;

        GTSExchangeCompressionType(final String extension) {
            this.extension = extension;
        }

        public static GTSExchangeCompressionType fromExtension(final String code) {
            for (final GTSExchangeCompressionType t : GTSExchangeCompressionType.values()) {
                if (t.getExtension().equals(code)) {
                    return t;
                }
            }
            throw new IllegalArgumentException("Unknown extension '" + code + "'");
        }

        public String getExtension() {
            return this.extension;
        }
    }

    public static class Builder extends GTSExchangeFileInfo_Builder {

        Builder() {
            this.setMetadataFile(false);
        }

        public static Builder from(final MeteorologicalBulletin<?> bulletin) {
            final Builder builder = new Builder()
                    .setPFlag(GTSExchangePFlag.A) // TODO: support P flags other than A
                    .setHeading(bulletin.getHeading());
            if (bulletin.getTimeStamp().isPresent()) {
                final ZonedDateTime timeStamp = bulletin.getTimeStamp().get();
                final Set<ChronoField> fieldsToInclude = bulletin.getTimeStampFields();
                if (fieldsToInclude.contains(ChronoField.YEAR)) {
                    builder.setTimeStampYear(timeStamp.getYear());
                }
                if (fieldsToInclude.contains(ChronoField.MONTH_OF_YEAR)) {
                    builder.setTimeStampMonth(timeStamp.getMonth());
                }
                if (fieldsToInclude.contains(ChronoField.DAY_OF_MONTH)) {
                    builder.setTimeStampDay(timeStamp.getDayOfMonth());
                }
                if (fieldsToInclude.contains(ChronoField.HOUR_OF_DAY)) {
                    builder.setTimeStampHour(timeStamp.getHour());
                }
                if (fieldsToInclude.contains(ChronoField.MINUTE_OF_HOUR)) {
                    builder.setTimeStampMinute(timeStamp.getMinute());
                }
                if (fieldsToInclude.contains(ChronoField.SECOND_OF_MINUTE)) {
                    builder.setTimeStampSecond(timeStamp.getSecond());
                }
            } else {
                builder.setTimeStamp(LocalDateTime.now(ZoneId.of("UTC")));
            }
            return builder;
        }

        public static Builder from(final String gtsExchangeFilename) {
            //TODO: support other P flags than A
            if (!gtsExchangeFilename.startsWith("A")) {
                throw new IllegalArgumentException("Only file names for pflag value 'A' are currently supported");
            }
            final Matcher m = P_FLAG_A_PATTERN.matcher(gtsExchangeFilename);
            if (!m.matches()) {
                throw new IllegalArgumentException("File name '" + gtsExchangeFilename + "' does not match the General file naming conventions for FTP/SFTP "
                        + "data exchange as defined in the " + "WMO-No. 386 Manual on the Global Telecommunication System, 2015 edition (updated 2017)");
            }
            GTSExchangeCompressionType compressionType = null;
            if (m.group("compression") != null) {
                compressionType = GTSExchangeCompressionType.fromExtension(m.group("compression"));
            }
            String abbreviatedHeading =
                    m.group("T1T2") + m.group("A1A2") + m.group("ii") + m.group("CCCC") + m.group("issueDay") + m.group("issueHour") + m.group("issueMinute");
            if (m.group("BBB") != null) {
                abbreviatedHeading += m.group("BBB");
            }
            Optional<Integer> timeStampYear = Optional.empty();
            if (!"----".equals(m.group("yyyy"))) {
                timeStampYear = Optional.of(Integer.parseInt(m.group("yyyy")));
            }
            Optional<Month> timeStampMonth = Optional.empty();
            if (!"--".equals(m.group("MM"))) {
                timeStampMonth = Optional.of(Month.of(Integer.parseInt(m.group("MM"))));
            }
            Optional<Integer> timeStampDay = Optional.empty();
            if (!"--".equals(m.group("dd"))) {
                timeStampDay = Optional.of(Integer.parseInt(m.group("dd")));
            }
            Optional<Integer> timeStampHour = Optional.empty();
            if (!"--".equals(m.group("hh"))) {
                timeStampHour = Optional.of(Integer.parseInt(m.group("hh")));
            }
            Optional<Integer> timeStampMinute = Optional.empty();
            if (!"--".equals(m.group("mm"))) {
                timeStampMinute = Optional.of(Integer.parseInt(m.group("mm")));
            }
            Optional<Integer> timeStampSecond = Optional.empty();
            if (!"--".equals(m.group("ss"))) {
                timeStampSecond = Optional.of(Integer.parseInt(m.group("ss")));
            }

            return new Builder().setPFlag(GTSExchangePFlag.A)//
                    .setMetadataFile(m.group("meta") != null)//
                    .setFileType(GTSExchangeFileType.fromExtension(m.group("type")))//
                    .setFreeFormPart(Optional.ofNullable(m.group("freeForm")))//
                    .setCompressionType(Optional.ofNullable(compressionType))//
                    .setHeading(BulletinHeadingImpl.Builder.from(abbreviatedHeading).build())//
                    .setTimeStampYear(timeStampYear)//
                    .setTimeStampMonth(timeStampMonth)//
                    .setTimeStampDay(timeStampDay)//
                    .setTimeStampHour(timeStampHour)//
                    .setTimeStampMinute(timeStampMinute)//
                    .setTimeStampSecond(timeStampSecond);
        }

        public Builder setTimeStamp(final LocalDateTime timeStamp) {
            this.setTimeStampYear(timeStamp.getYear());
            this.setTimeStampMonth(timeStamp.getMonth());
            this.setTimeStampDay(timeStamp.getDayOfMonth());
            this.setTimeStampHour(timeStamp.getHour());
            this.setTimeStampMinute(timeStamp.getMinute());
            this.setTimeStampSecond(timeStamp.getSecond());
            return this;
        }
    }

}
