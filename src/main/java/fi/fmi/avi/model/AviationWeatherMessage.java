package fi.fmi.avi.model;

import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import fi.fmi.avi.model.AviationCodeListUser.PermissibleUsage;
import fi.fmi.avi.model.AviationCodeListUser.PermissibleUsageReason;

/**
 * A generic interface for all aviation related weather reports and forecasts.
 */

public interface AviationWeatherMessage {

    /**
     * Returns the issue time of the message.
     * It the returned {@link PartialOrCompleteTimeInstant} may or may not contain
     * a completely resolved date time depending on which information it was
     * created with.
     *
     * @return the fully resolved issue time
     *
     * @see PartialOrCompleteTimeInstant.Builder#completePartialAt(YearMonth)
     */
    PartialOrCompleteTimeInstant getIssueTime();

    /**
     * Returns the remarks, if included in the message.
     * in TAC remarks are provided at the end of the message
     * after the 'RMK' token.
     *
     * @return the remark tokens as-is
     */
    Optional<List<String>> getRemarks();

    /**
     * See https://schemas.wmo.int/iwxxm/2.1/common.xsd
     *
     * @return permissible usage,
     */
    Optional<PermissibleUsage> getPermissibleUsage();

    /**
     * See https://schemas.wmo.int/iwxxm/2.1/common.xsd
     *
     * @return permissible usage reason,
     */
    Optional<PermissibleUsageReason> getPermissibleUsageReason();

    /**
     * See https://schemas.wmo.int/iwxxm/2.1/common.xsd
     *
     * @return permissible usage supplementary
     */
    Optional<String> getPermissibleUsageSupplementary();

    /**
     * Indication of the message has been created by automatic translation from another format.
     *
     * @return true if the message has been translated
     */
    boolean isTranslated();

    /**
     * See https://schemas.wmo.int/iwxxm/2.1/common.xsd
     *
     * @return bulleting id of the original message (if available),
     */
    Optional<String> getTranslatedBulletinID();

    /**
     * See https://schemas.wmo.int/iwxxm/2.1/common.xsd
     *
     * @return reception time of the original bulletin (if available)
     */
    Optional<ZonedDateTime> getTranslatedBulletinReceptionTime();

    /**
     * See https://schemas.wmo.int/iwxxm/2.1/common.xsd
     *
     * @return translation centre designator (if available)
     */
    Optional<String> getTranslationCentreDesignator();

    /**
     * See https://schemas.wmo.int/iwxxm/2.1/common.xsd
     *
     * @return translation centre name (if available)
     */
    Optional<String> getTranslationCentreName();

    /**
     * See https://schemas.wmo.int/iwxxm/2.1/common.xsd
     *
     * @return time the translation occurred
     */
    Optional<ZonedDateTime> getTranslationTime();

    /**
     * Returns the original TAC format message, if available.
     *
     * @return the original TAC message before translation
     */
    Optional<String> getTranslatedTAC();

    /**
     * Returns true if issue time, valid time and all other time references contained in this
     * message are full ZonedDateTime instances.
     *
     * @return true if all time references are complete, false otherwise
     */
    boolean areAllTimeReferencesComplete();

}
