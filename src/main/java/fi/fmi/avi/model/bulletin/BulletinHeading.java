package fi.fmi.avi.model.bulletin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.fmi.avi.model.MessageType;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;

import java.time.YearMonth;
import java.util.Optional;

public interface BulletinHeading {

    /**
     * Type of the content (AAx, RRx, CCx) part of the abbreviated heading.
     *
     * @return the type of the content
     */
    Type getType();

    DataTypeDesignatorT1 getDataTypeDesignatorT1ForTAC();

    @JsonIgnore
    default DataTypeDesignatorT1 getDataTypeDesignatorT1ForXML() {
        return DataTypeDesignatorT1.AVIATION_INFORMATION_IN_XML;
    }

    DataTypeDesignatorT2 getDataTypeDesignatorT2();

    @JsonIgnore
    default String getDataTypeDesignatorsForTAC() {
        return "" + getDataTypeDesignatorT1ForTAC().code() + getDataTypeDesignatorT2().code();
    }

    @JsonIgnore
    default String getDataTypeDesignatorsForXML() {
        return "" + getDataTypeDesignatorT1ForXML().code() + getDataTypeDesignatorT2().code();
    }

    @JsonIgnore
    default String getDataDesignatorsForTAC() {
        return "" + getDataTypeDesignatorsForTAC() + getGeographicalDesignator() + String.format("%02d", getBulletinNumber());
    }

    @JsonIgnore
    default String getDataDesignatorsForXML() {
        return "" + getDataTypeDesignatorsForXML() + getGeographicalDesignator() + String.format("%02d", getBulletinNumber());
    }

    /**
     * This corresponds to the 'CCCC' part of the abbreviated heading
     *
     * @return the indicator
     */
    String getLocationIndicator();

    /**
     * This corresponds to the 'A<sub>1</sub>A<sub>2</sub>' part of the abbreviated heading
     *
     * @return the designator
     */
    String getGeographicalDesignator();

    /**
     * This corresponds to the 'ii' number of the abbreviated heading
     *
     * @return the number
     */
    int getBulletinNumber();

    /**
     * Corresponds to the A-Z letter 'x' of the RRx, AAx or CCx part of the abbreviated heading
     *
     * @return the augmentation number
     */
    Optional<Integer> getBulletinAugmentationNumber();

    /**
     * Corresponds to the 'BBB' part of the abbreviated heading.
     * <p>
     * An empty String indicates that the bulletin heading {@link BulletinHeading.Type} is
     * {@link BulletinHeading.Type#NORMAL}. The parsed interpretations of a non-empty string are available using
     * {@link #getType()} and {@link #getBulletinAugmentationNumber()}.
     *
     * @return the bulletin augmentation indicator
     */
    String getBulletinAugmentationIndicator();

    /**
     * Returns the issue time of the bulletin.
     * The returned {@link PartialOrCompleteTimeInstant} may or may not contain
     * a completely resolved date time depending on which information it was
     * created with.
     *
     * @return the issue time
     * @see PartialOrCompleteTimeInstant.Builder#completePartialAt(YearMonth)
     */
    PartialOrCompleteTimeInstant getIssueTime();

    @JsonIgnore
    Optional<MessageType> getExpectedContainedMessageType();

    enum Type {
        NORMAL(""), DELAYED("RR"), AMENDED("AA"), CORRECTED("CC");

        private final String prefix;

        Type(final String prefix) {
            this.prefix = prefix;
        }

        public static Type fromCode(final String code) {
            for (final Type t : Type.values()) {
                if (t.getPrefix().equals(code)) {
                    return t;
                }
            }
            throw new IllegalArgumentException("Unknown prefix '" + code + "'");
        }

        public String getPrefix() {
            return this.prefix;
        }
    }
}
