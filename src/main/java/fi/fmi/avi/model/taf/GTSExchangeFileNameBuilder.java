package fi.fmi.avi.model.taf;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.OptionalInt;

/**
 * See WMO No.386 Manual on the global telecommunications system, Attachment II-15, GTS Data Exchange Methods, SFTP/FTP procedures and file naming
 * convention.
 */
public class GTSExchangeFileNameBuilder {

    private GTSExchangePFlag pFlag;
    private TAFBulletinHeading heading;
    private GTSExchangeFileType fileType;
    private GTSExchangeCompressionType compressionType;
    private String freeFormPart;
    private boolean isMetadataFile;
    private LocalDateTime timeStamp;

    public GTSExchangeFileNameBuilder() {
        this.isMetadataFile = false;
    }

    public GTSExchangePFlag getPFlag() {
        return pFlag;
    }

    public GTSExchangeFileNameBuilder setPFlag(final GTSExchangePFlag pFlag) {
        this.pFlag = pFlag;
        return this;
    }

    public TAFBulletinHeading getHeading() {
        return heading;
    }

    public GTSExchangeFileNameBuilder setHeading(final TAFBulletinHeading heading) {
        if (heading.getGeographicalDesignator() == null || heading.getGeographicalDesignator().length() != 2) {
            throw new IllegalArgumentException("Invalid geographical location code '" + heading.getGeographicalDesignator() + "' in TAF bulletin");
        }
        if (heading.getBulletinNumber() < 0 || heading.getBulletinNumber() > 99) {
            throw new IllegalArgumentException("Invalid bulletin number ('ii' part) '" + heading.getBulletinNumber() + "' in TAF bulletin");
        }
        if (heading.getLocationIndicator() == null || heading.getLocationIndicator().length() != 4) {
            throw new IllegalArgumentException("Invalid location indicator '" + heading.getLocationIndicator() + "' in TAF bulletin");
        }
        OptionalInt augNumber = heading.getBulletinAugmentationNumber();
        if (augNumber.isPresent()) {
            if (augNumber.getAsInt() < 1 || augNumber.getAsInt() > ('Z' - 'A' + 1)) {
                throw new IllegalArgumentException(
                        "Illegal bulletin augmentation number '" + augNumber.getAsInt() + "', the value must be between 1 and  " + ('Z' - 'A' + 1));
            }
            if (TAFBulletinHeading.Type.NORMAL == heading.getType()) {
                throw new IllegalArgumentException("Bulletin contains augmentation number, but the type is NORMAL");
            }
        }
        this.heading = heading;
        return this;
    }

    public GTSExchangeFileType getFileType() {
        return fileType;
    }

    public GTSExchangeFileNameBuilder setFileType(final GTSExchangeFileType fileType) {
        this.fileType = fileType;
        return this;
    }

    public GTSExchangeCompressionType getCompressionType() {
        return compressionType;
    }

    public GTSExchangeFileNameBuilder setCompressionType(final GTSExchangeCompressionType compressionType) {
        this.compressionType = compressionType;
        return this;
    }

    public String getFreeFormPart() {
        return freeFormPart;
    }

    public GTSExchangeFileNameBuilder setFreeFormPart(final String freeFormPart) {
        this.freeFormPart = freeFormPart;
        return this;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public GTSExchangeFileNameBuilder setTimeStamp(final LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    public boolean isMetadataFile() {
        return isMetadataFile;
    }

    public GTSExchangeFileNameBuilder setMetadataFile(final boolean metadataFile) {
        isMetadataFile = metadataFile;
        return this;
    }

    public String build() {
        if (this.heading == null) {
            throw new IllegalStateException("No TAFBulletin heading set");
        }
        if (this.fileType == null) {
            throw new IllegalStateException("File type not set");
        }
        if (this.timeStamp == null) {
            this.timeStamp = LocalDateTime.now();
        }

        //TODO: rest of the types
        switch (this.pFlag) {
            case A:
                return createATypeFileName();
            default:
                throw new IllegalStateException("Names with pFlag value '" + this.pFlag + "' not yet supported");
        }
    }

    private String createATypeFileName() {
        StringBuilder sb = new StringBuilder();
        sb.append('A');
        if (this.isMetadataFile) {
            sb.append('M');
        }
        sb.append('_');
        sb.append('F');
        if (heading.isValidLessThan12Hours()) {
            sb.append('C');
        } else {
            sb.append('T');
        }
        sb.append(heading.getGeographicalDesignator());
        sb.append(String.format("%02d", heading.getBulletinNumber()));
        sb.append(heading.getLocationIndicator());
        OptionalInt augNumber = heading.getBulletinAugmentationNumber();
        if (augNumber.isPresent()) {
            int seqNumber = augNumber.getAsInt();
            seqNumber = 'A' + seqNumber - 1;
            switch (heading.getType()) {
                case AMENDED:
                    sb.append("AA");
                    break;
                case CORRECTED:
                    sb.append("CC");
                    break;
                case DELAYED:
                    sb.append("RR");
                    break;
            }
            sb.append(Character.toChars(seqNumber));
        }
        sb.append('_');
        sb.append('C');
        sb.append('_');
        sb.append(heading.getLocationIndicator());
        sb.append(this.timeStamp.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        if (this.freeFormPart != null) {
            sb.append('_');
            sb.append(freeFormPart);
        }
        sb.append('.');
        sb.append(this.fileType.getExtension());
        if (this.compressionType != null) {
            sb.append('.');
            sb.append(this.compressionType.getExtension());
        }
        return sb.toString();
    }

    public enum GTSExchangePFlag {T, A, W, Z, X}

    public enum GTSExchangeFileType {
        METADATA("met"),//
        TIFF("tif"),//
        GIF("gif"),//
        PNG("png"),//
        POSTSCRIPT("ps"),//
        MPEG("mpg"),//
        JPEG("jpg"),//
        TEXT("txt"),//
        HTML("htm"),//
        WMO_BINARY("bin"),//
        MS_WORD("doc"),//
        COREL_WORD_PERFECT("wpd"),//
        HDF("hdf"),//
        NetCDF("nc"),//
        PDF("pdf"),//
        XML("xml");

        private String extension;

        GTSExchangeFileType(final String extension) {
            this.extension = extension;
        }

        public String getExtension() {
            return this.extension;
        }
    }

    public enum GTSExchangeCompressionType {
        UNIX_COMPRESS("Z"),//
        ZIP("zip"),//
        GZIP("gz"), //
        BZIP2("bz2"), //
        XZ("xy");

        private String extension;

        GTSExchangeCompressionType(final String extension) {
            this.extension = extension;
        }

        public String getExtension() {
            return this.extension;
        }
    }
}
