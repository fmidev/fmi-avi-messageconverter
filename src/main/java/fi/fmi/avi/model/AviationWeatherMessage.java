package fi.fmi.avi.model;

import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import fi.fmi.avi.model.AviationCodeListUser.PermissibleUsage;
import fi.fmi.avi.model.AviationCodeListUser.PermissibleUsageReason;

/**
 * A generic interface for all aviation related weather reports and forecasts.
 *
 */

public interface AviationWeatherMessage {

    /**
     * Returns the issue time of the message.
     * It the returned {@link PartialOrCompleteTimeInstance} may or may not contain
     * a completely resolved date time depending on which information it was
     * created with.
     *
     * @return the fully resolved issue time
     *
     * @see PartialOrCompleteTimeInstance.Builder#completedWithYearMonth(YearMonth)
     * @see PartialOrCompleteTimeInstance.Builder#completedWithYearMonthDay(YearMonth, int)
     *
     */
    PartialOrCompleteTimeInstance issueTime();

    /**
     * Returns the remarks, if included in the message.
     * in TAC remarks are provided at the end of the message
     * after the 'RMK' token.
     *
     * @return the remark tokens as-is
     */
    Optional<List<String>> remarks();

    /**
     * See https://schemas.wmo.int/iwxxm/2.1/common.xsd
     *
     * @return permissible usage,
     */
    Optional<PermissibleUsage> permissibleUsage();
    
    /**
     * See https://schemas.wmo.int/iwxxm/2.1/common.xsd
     *
     * @return permissible usage reason,
     */
    Optional<PermissibleUsageReason> permissibleUsageReason();
    
    /**
     * See https://schemas.wmo.int/iwxxm/2.1/common.xsd
     *
     * @return permissible usage supplementary
     */
    Optional<String> permissibleUsageSupplementary();
    
    /**
     * Indication of the message has been created by automatic translation from another format.
     *
     * @return true if the message has been translated
     */
    boolean translated();
    
    /**
     * See https://schemas.wmo.int/iwxxm/2.1/common.xsd
     *
     * @return bulleting id of the original message (if available),
     */
    Optional<String> translatedBulletinID();

    /**
     * See https://schemas.wmo.int/iwxxm/2.1/common.xsd
     *
     * @return reception time of the original bulletin (if available)
     */
    Optional<ZonedDateTime> translatedBulletinReceptionTime();
    
    /**
     * See https://schemas.wmo.int/iwxxm/2.1/common.xsd
     *
     * @return translation centre designator (if available)
     */
    Optional<String> translationCentreDesignator();
    
    /**
     * See https://schemas.wmo.int/iwxxm/2.1/common.xsd
     *
     * @return translation centre name (if available)
     */
    Optional<String> translationCentreName();
    
    /**
     * See https://schemas.wmo.int/iwxxm/2.1/common.xsd
     *
     * @return time the translation occurred
     */
    Optional<ZonedDateTime> translationTime();
    
    /**
     * Returns the original TAC format message, if available.
     *
     * @return the original TAC message before translation
     */
    Optional<String> translatedTAC();

}
