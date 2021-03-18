package fi.fmi.avi.model;

import static java.util.Objects.requireNonNull;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public final class AviationWeatherMessageBuilderHelper {
    private AviationWeatherMessageBuilderHelper() {
        throw new AssertionError();
    }

    /**
     * Copy properties declared in {@link AviationWeatherMessage} from provided {@code value} to {@code builder} using provided setters.
     *
     * <p>
     * This method exists for completeness safety. Whenever the {@link AviationWeatherMessage} interface changes, applying changes here will enforce to
     * conform to changes in all builders using this method. This ensures that changes will not get unnoticed in builder classes.
     * </p>
     *
     * @param <T>
     *         type of {@code value}
     * @param <B>
     *         type of {@code builder}
     * @param builder
     *         builder to copy properties to
     * @param value
     *         value object to copy properties from
     * @param setRemarks
     *         setter for remarks
     * @param setPermissibleUsage
     *         setter for permissibleUsage
     * @param setPermissibleUsageReason
     *         setter for permissibleUsageReason
     * @param setPermissibleUsageSupplementary
     *         setter for permissibleUsageSupplementary
     * @param setTranslated
     *         setter for translated
     * @param setTranslatedBulletinID
     *         stter for translatedBulletinID
     * @param setTranslatedBulletinReceptionTime
     *         setter for translatedBulletinReceptionTime
     * @param setTranslationCentreDesignator
     *         setter for translationCentreDesignator
     * @param setTranslationCentreName
     *         setter for translationCentreName
     * @param setTranslationTime
     *         setter for translationTime
     * @param setTranslatedTAC
     *         setter for translatedTAC
     * @param setIssueTime
     *         setter for issueTime
     * @param setReportStatus
     *         setter for reportStatus
     */
    public static <T extends AviationWeatherMessage, B> void copyFrom(final B builder, final T value,  //
            final BiConsumer<B, Optional<List<String>>> setRemarks, //
            final BiConsumer<B, Optional<AviationCodeListUser.PermissibleUsage>> setPermissibleUsage, //
            final BiConsumer<B, Optional<AviationCodeListUser.PermissibleUsageReason>> setPermissibleUsageReason, //
            final BiConsumer<B, Optional<String>> setPermissibleUsageSupplementary, //
            final BiConsumer<B, Boolean> setTranslated, //
            final BiConsumer<B, Optional<String>> setTranslatedBulletinID, //
            final BiConsumer<B, Optional<ZonedDateTime>> setTranslatedBulletinReceptionTime, //
            final BiConsumer<B, Optional<String>> setTranslationCentreDesignator, //
            final BiConsumer<B, Optional<String>> setTranslationCentreName, //
            final BiConsumer<B, Optional<ZonedDateTime>> setTranslationTime, //
            final BiConsumer<B, Optional<String>> setTranslatedTAC, //
            final BiConsumer<B, Optional<PartialOrCompleteTimeInstant>> setIssueTime, //
            final BiConsumer<B, Optional<AviationWeatherMessage.ReportStatus>> setReportStatus) {
        requireNonNull(value, "value");
        requireNonNull(builder, "builder");
        setRemarks.accept(builder, value.getRemarks().map(BuilderHelper::toImmutableList));
        setPermissibleUsage.accept(builder, value.getPermissibleUsage());
        setPermissibleUsageReason.accept(builder, value.getPermissibleUsageReason());
        setPermissibleUsageSupplementary.accept(builder, value.getPermissibleUsageSupplementary());
        setTranslated.accept(builder, value.isTranslated());
        setTranslatedBulletinID.accept(builder, value.getTranslatedBulletinID());
        setTranslatedBulletinReceptionTime.accept(builder, value.getTranslatedBulletinReceptionTime());
        setTranslationCentreDesignator.accept(builder, value.getTranslationCentreDesignator());
        setTranslationCentreName.accept(builder, value.getTranslationCentreName());
        setTranslationTime.accept(builder, value.getTranslationTime());
        setTranslatedTAC.accept(builder, value.getTranslatedTAC());
        setIssueTime.accept(builder, value.getIssueTime());
        setReportStatus.accept(builder, value.getReportStatus());
    }
}
