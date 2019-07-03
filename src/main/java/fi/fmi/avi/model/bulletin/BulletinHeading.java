package fi.fmi.avi.model.bulletin;

import java.time.YearMonth;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fi.fmi.avi.model.MessageType;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;

public interface BulletinHeading {

    /**
     * Type of the content (AAx, RRx, CCx) part of the abbreviated heading.
     *
     * @return the type of the content
     */
    Type getType();

    /**
     * @return
     */
    DataTypeDesignatorT1 getDataTypeDesignatorT1ForTAC();

    /**
     * @return
     */
    @JsonIgnore
    default DataTypeDesignatorT1 getDataTypeDesignatorT1ForXML() {
        return DataTypeDesignatorT1.AVIATION_INFORMATION_IN_XML;
    }

    DataTypeDesignatorT2 getDataTypeDesignatorT2();

    /**
     * @return
     */
    @JsonIgnore
    default String getDataTypeDesignatorsForTAC() {
        return "" + getDataTypeDesignatorT1ForTAC().code() + getDataTypeDesignatorT2().code();
    }

    /**
     * @return
     */
    @JsonIgnore
    default String getDataTypeDesignatorsForXML() {
        return "" + getDataTypeDesignatorT1ForXML().code() + getDataTypeDesignatorT2().code();
    }

    /**
     * @return
     */
    @JsonIgnore
    default String getDataDesignatorsForTAC() {
        return "" + getDataTypeDesignatorsForTAC() + getGeographicalDesignator() + String.format("%02d", getBulletinNumber());
    }

    /**
     * @return
     */
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
     * Returns the issue time of the bulletin.
     * The returned {@link PartialOrCompleteTimeInstant} may or may not contain
     * a completely resolved date time depending on which information it was
     * created with.
     *
     * @return the issue time
     *
     * @see PartialOrCompleteTimeInstant.Builder#completePartialAt(YearMonth)
     */
    PartialOrCompleteTimeInstant getIssueTime();

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
