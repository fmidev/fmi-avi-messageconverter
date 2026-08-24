package fi.fmi.avi.model.taf.immutable;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.Aerodrome;
import fi.fmi.avi.model.AerodromeWeatherMessageBuilderHelper;
import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.model.AviationWeatherMessageBuilderHelper;
import fi.fmi.avi.model.BuilderHelper;
import fi.fmi.avi.model.PartialDateTime;
import fi.fmi.avi.model.PartialOrCompleteTime;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.PartialOrCompleteTimes;
import fi.fmi.avi.model.immutable.AerodromeImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.TAFAirTemperatureForecast;
import fi.fmi.avi.model.taf.TAFBaseForecast;
import fi.fmi.avi.model.taf.TAFChangeForecast;
import fi.fmi.avi.model.taf.TAFReference;

/**
 * {@code Builder} does not extend an Immutables-generated builder directly - it reads back its own
 * in-progress state extensively (e.g. {@code onValueOrPartialBuild()},
 * {@code withCompleteForecastTimes(...)}), which Immutables' generated builders do not support.
 */
@Value.Immutable
@Value.Style(init = "set*", get = { "is*", "get*" },
        passAnnotations = { com.fasterxml.jackson.databind.annotation.JsonDeserialize.class, com.fasterxml.jackson.annotation.JsonProperty.class },
        typeInnerBuilder = "InternalImmutableBuilder", builder = "internalBuilder")
@JsonDeserialize(builder = TAFImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "reportStatus", "cancelMessage", "missingMessage", "aerodrome", "issueTime", "validityTime", "baseForecast", "changeForecasts",
        "referredReportValidPeriod", "remarks", "permissibleUsage", "permissibleUsageReason", "permissibleUsageSupplementary", "translated",
        "translatedBulletinID", "translatedBulletinReceptionTime", "translationCentreDesignator", "translationCentreName", "translationTime", "translatedTAC" })
public abstract class TAFImpl implements TAF, Serializable {

    private static final long serialVersionUID = 4002686554552796585L;

    public static Builder builder() {
        return new Builder();
    }

    public static TAFImpl immutableCopyOf(final TAF taf) {
        requireNonNull(taf);
        if (taf instanceof TAFImpl) {
            return (TAFImpl) taf;
        } else {
            return Builder.copyOf(taf).build();
        }
    }

    public static Optional<TAFImpl> immutableCopyOf(final Optional<TAF> taf) {
        requireNonNull(taf);
        return taf.map(TAFImpl::immutableCopyOf);
    }

    /**
     * Provides the value of the status property.
     * <p>
     * Note, this method is provided for backward compatibility with previous versions of the API. The <code>status</code> is no longer
     * explicitly stored. This implementation uses {@link TAFStatus#fromReportStatus(ReportStatus, boolean, boolean)} instead to determine the returned value
     * on-the-fly.
     *
     * @return the message status
     *
     * @deprecated please migrate to using a combination of {@link #getReportStatus()} and {@link #isCancelMessage()} instead
     */
    @Override
    @JsonIgnore
    @Deprecated
    public TAFStatus getStatus() {
        return TAF.super.getStatus();
    }

    /**
     * Provides the value of the referredReport property.
     *
     * <p>
     * Note, this method is provided for backward compatibility with previous versions of the API. The <code>referredReport</code> is no longer
     * explicitly stored. This implementation uses {@link #getAerodrome()} and {@link #getReferredReportValidPeriod()} instead to determine the returned value
     * on-the-fly for cancel, amendment and correction messages. Returns {@link Optional#empty()} if {@link #getReferredReportValidPeriod()} is
     * not present.
     * </p>
     *
     * @return the amended message information messages
     *
     * @deprecated please migrate to using {@link #getAerodrome()} and {@link #getReferredReportValidPeriod()} instead
     */
    @Override
    @JsonIgnore
    @Deprecated
    public Optional<TAFReference> getReferredReport() {
        return TAF.super.getReferredReport();
    }

    public Builder toBuilder() {
        return new Builder().mergeFrom(this);
    }

    /**
     * Returns true if issue time, valid time and all other time references contained in this
     * message are full ZonedDateTime instances.
     *
     * @return true if all time references are complete, false otherwise
     */
    @Override
    @JsonIgnore
    public boolean areAllTimeReferencesComplete() {
        if (getIssueTime().isPresent() && !getIssueTime().get().getCompleteTime().isPresent() //
                || getValidityTime().isPresent() && !getValidityTime().get().isComplete() //
                || getReferredReportValidPeriod().isPresent() && !getReferredReportValidPeriod().get().isComplete() //
                || getBaseForecast().isPresent() && !getBaseForecast().get().areAllTimeReferencesComplete()) {
            return false;
        }
        if (this.getChangeForecasts().isPresent()) {
            for (final TAFChangeForecast changeForecast : this.getChangeForecasts().get()) {
                if (!changeForecast.getPeriodOfChange().isComplete()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    @JsonIgnore
    public boolean allAerodromeReferencesContainPosition() {
        return this.getAerodrome().getReferencePoint().isPresent();
    }

    public static class Builder {
        @Nullable
        private Aerodrome referredReportAerodrome;
        private boolean missingMessage;

    @JsonIgnore
    protected Aerodrome aerodrome;
    protected AviationWeatherMessage.ReportStatus reportStatus;
    protected boolean cancelMessage;
    protected boolean translated;
    protected Optional<PartialOrCompleteTimeInstant> issueTime = Optional.empty();
    protected Optional<List<String>> remarks = Optional.empty();
    protected Optional<AviationCodeListUser.PermissibleUsage> permissibleUsage = Optional.empty();
    protected Optional<AviationCodeListUser.PermissibleUsageReason> permissibleUsageReason = Optional.empty();
    protected Optional<String> permissibleUsageSupplementary = Optional.empty();
    protected Optional<String> translatedBulletinID = Optional.empty();
    protected Optional<ZonedDateTime> translatedBulletinReceptionTime = Optional.empty();
    protected Optional<String> translationCentreDesignator = Optional.empty();
    protected Optional<String> translationCentreName = Optional.empty();
    protected Optional<ZonedDateTime> translationTime = Optional.empty();
    protected Optional<String> translatedTAC = Optional.empty();
    protected Optional<PartialOrCompleteTimePeriod> validityTime = Optional.empty();
    @JsonIgnore
    protected Optional<TAFBaseForecast> baseForecast = Optional.empty();
    @JsonIgnore
    protected Optional<List<TAFChangeForecast>> changeForecasts = Optional.empty();
    protected Optional<PartialOrCompleteTimePeriod> referredReportValidPeriod = Optional.empty();

    public Aerodrome getAerodrome() {
        return requireAerodrome();
    }

    private Aerodrome requireAerodrome() {
        if (aerodrome == null) {
            throw new IllegalStateException("aerodrome not set");
        }
        return aerodrome;
    }

    public AviationWeatherMessage.ReportStatus getReportStatus() {
        if (reportStatus == null) {
            throw new IllegalStateException("reportStatus not set");
        }
        return reportStatus;
    }

    public Builder setReportStatus(final AviationWeatherMessage.ReportStatus reportStatus) {
        this.reportStatus = requireNonNull(reportStatus, "reportStatus");
        missingMessage = false;
        return this;
    }

    public boolean isCancelMessage() {
        return cancelMessage;
    }

    public Builder setCancelMessage(final boolean cancelMessage) {
        this.cancelMessage = cancelMessage;
        missingMessage = false;
        return this;
    }

    public boolean isTranslated() {
        return translated;
    }

    public Builder setTranslated(final boolean translated) {
        this.translated = translated;
        return this;
    }

    public Optional<PartialOrCompleteTimeInstant> getIssueTime() {
        return issueTime;
    }

    public Builder setIssueTime(final PartialOrCompleteTimeInstant issueTime) {
        this.issueTime = Optional.of(requireNonNull(issueTime, "issueTime"));
        return this;
    }

    public Builder setIssueTime(final Optional<? extends PartialOrCompleteTimeInstant> issueTime) {
        requireNonNull(issueTime, "issueTime");
        this.issueTime = issueTime.isPresent() ? Optional.of(issueTime.get()) : Optional.empty();
        return this;
    }

    public Builder clearIssueTime() {
        this.issueTime = Optional.empty();
        return this;
    }

    public Builder mapIssueTime(final UnaryOperator<PartialOrCompleteTimeInstant> mapper) {
        requireNonNull(mapper, "mapper");
        this.issueTime = this.issueTime.map(mapper);
        return this;
    }

    public Optional<List<String>> getRemarks() {
        return remarks;
    }

    public Builder setRemarks(final List<String> remarks) {
        this.remarks = Optional.of(requireNonNull(remarks, "remarks"));
        return this;
    }

    public Builder setRemarks(final Optional<? extends List<String>> remarks) {
        requireNonNull(remarks, "remarks");
        this.remarks = remarks.isPresent() ? Optional.of(remarks.get()) : Optional.empty();
        return this;
    }

    public Builder clearRemarks() {
        this.remarks = Optional.empty();
        return this;
    }

    public Builder mapRemarks(final UnaryOperator<List<String>> mapper) {
        requireNonNull(mapper, "mapper");
        this.remarks = this.remarks.map(mapper);
        return this;
    }

    public Optional<AviationCodeListUser.PermissibleUsage> getPermissibleUsage() {
        return permissibleUsage;
    }

    public Builder setPermissibleUsage(final AviationCodeListUser.PermissibleUsage permissibleUsage) {
        this.permissibleUsage = Optional.of(requireNonNull(permissibleUsage, "permissibleUsage"));
        return this;
    }

    public Builder setPermissibleUsage(final Optional<? extends AviationCodeListUser.PermissibleUsage> permissibleUsage) {
        requireNonNull(permissibleUsage, "permissibleUsage");
        this.permissibleUsage = permissibleUsage.isPresent() ? Optional.of(permissibleUsage.get()) : Optional.empty();
        return this;
    }

    public Builder clearPermissibleUsage() {
        this.permissibleUsage = Optional.empty();
        return this;
    }

    public Builder mapPermissibleUsage(final UnaryOperator<AviationCodeListUser.PermissibleUsage> mapper) {
        requireNonNull(mapper, "mapper");
        this.permissibleUsage = this.permissibleUsage.map(mapper);
        return this;
    }

    public Optional<AviationCodeListUser.PermissibleUsageReason> getPermissibleUsageReason() {
        return permissibleUsageReason;
    }

    public Builder setPermissibleUsageReason(final AviationCodeListUser.PermissibleUsageReason permissibleUsageReason) {
        this.permissibleUsageReason = Optional.of(requireNonNull(permissibleUsageReason, "permissibleUsageReason"));
        return this;
    }

    public Builder setPermissibleUsageReason(final Optional<? extends AviationCodeListUser.PermissibleUsageReason> permissibleUsageReason) {
        requireNonNull(permissibleUsageReason, "permissibleUsageReason");
        this.permissibleUsageReason = permissibleUsageReason.isPresent() ? Optional.of(permissibleUsageReason.get()) : Optional.empty();
        return this;
    }

    public Builder clearPermissibleUsageReason() {
        this.permissibleUsageReason = Optional.empty();
        return this;
    }

    public Builder mapPermissibleUsageReason(final UnaryOperator<AviationCodeListUser.PermissibleUsageReason> mapper) {
        requireNonNull(mapper, "mapper");
        this.permissibleUsageReason = this.permissibleUsageReason.map(mapper);
        return this;
    }

    public Optional<String> getPermissibleUsageSupplementary() {
        return permissibleUsageSupplementary;
    }

    public Builder setPermissibleUsageSupplementary(final String permissibleUsageSupplementary) {
        this.permissibleUsageSupplementary = Optional.of(requireNonNull(permissibleUsageSupplementary, "permissibleUsageSupplementary"));
        return this;
    }

    public Builder setPermissibleUsageSupplementary(final Optional<? extends String> permissibleUsageSupplementary) {
        requireNonNull(permissibleUsageSupplementary, "permissibleUsageSupplementary");
        this.permissibleUsageSupplementary = permissibleUsageSupplementary.isPresent() ? Optional.of(permissibleUsageSupplementary.get()) : Optional.empty();
        return this;
    }

    public Builder clearPermissibleUsageSupplementary() {
        this.permissibleUsageSupplementary = Optional.empty();
        return this;
    }

    public Builder mapPermissibleUsageSupplementary(final UnaryOperator<String> mapper) {
        requireNonNull(mapper, "mapper");
        this.permissibleUsageSupplementary = this.permissibleUsageSupplementary.map(mapper);
        return this;
    }

    public Optional<String> getTranslatedBulletinID() {
        return translatedBulletinID;
    }

    public Builder setTranslatedBulletinID(final String translatedBulletinID) {
        this.translatedBulletinID = Optional.of(requireNonNull(translatedBulletinID, "translatedBulletinID"));
        return this;
    }

    public Builder setTranslatedBulletinID(final Optional<? extends String> translatedBulletinID) {
        requireNonNull(translatedBulletinID, "translatedBulletinID");
        this.translatedBulletinID = translatedBulletinID.isPresent() ? Optional.of(translatedBulletinID.get()) : Optional.empty();
        return this;
    }

    public Builder clearTranslatedBulletinID() {
        this.translatedBulletinID = Optional.empty();
        return this;
    }

    public Builder mapTranslatedBulletinID(final UnaryOperator<String> mapper) {
        requireNonNull(mapper, "mapper");
        this.translatedBulletinID = this.translatedBulletinID.map(mapper);
        return this;
    }

    public Optional<ZonedDateTime> getTranslatedBulletinReceptionTime() {
        return translatedBulletinReceptionTime;
    }

    public Builder setTranslatedBulletinReceptionTime(final ZonedDateTime translatedBulletinReceptionTime) {
        this.translatedBulletinReceptionTime = Optional.of(requireNonNull(translatedBulletinReceptionTime, "translatedBulletinReceptionTime"));
        return this;
    }

    public Builder setTranslatedBulletinReceptionTime(final Optional<? extends ZonedDateTime> translatedBulletinReceptionTime) {
        requireNonNull(translatedBulletinReceptionTime, "translatedBulletinReceptionTime");
        this.translatedBulletinReceptionTime = translatedBulletinReceptionTime.isPresent() ? Optional.of(translatedBulletinReceptionTime.get()) : Optional.empty();
        return this;
    }

    public Builder clearTranslatedBulletinReceptionTime() {
        this.translatedBulletinReceptionTime = Optional.empty();
        return this;
    }

    public Builder mapTranslatedBulletinReceptionTime(final UnaryOperator<ZonedDateTime> mapper) {
        requireNonNull(mapper, "mapper");
        this.translatedBulletinReceptionTime = this.translatedBulletinReceptionTime.map(mapper);
        return this;
    }

    public Optional<String> getTranslationCentreDesignator() {
        return translationCentreDesignator;
    }

    public Builder setTranslationCentreDesignator(final String translationCentreDesignator) {
        this.translationCentreDesignator = Optional.of(requireNonNull(translationCentreDesignator, "translationCentreDesignator"));
        return this;
    }

    public Builder setTranslationCentreDesignator(final Optional<? extends String> translationCentreDesignator) {
        requireNonNull(translationCentreDesignator, "translationCentreDesignator");
        this.translationCentreDesignator = translationCentreDesignator.isPresent() ? Optional.of(translationCentreDesignator.get()) : Optional.empty();
        return this;
    }

    public Builder clearTranslationCentreDesignator() {
        this.translationCentreDesignator = Optional.empty();
        return this;
    }

    public Builder mapTranslationCentreDesignator(final UnaryOperator<String> mapper) {
        requireNonNull(mapper, "mapper");
        this.translationCentreDesignator = this.translationCentreDesignator.map(mapper);
        return this;
    }

    public Optional<String> getTranslationCentreName() {
        return translationCentreName;
    }

    public Builder setTranslationCentreName(final String translationCentreName) {
        this.translationCentreName = Optional.of(requireNonNull(translationCentreName, "translationCentreName"));
        return this;
    }

    public Builder setTranslationCentreName(final Optional<? extends String> translationCentreName) {
        requireNonNull(translationCentreName, "translationCentreName");
        this.translationCentreName = translationCentreName.isPresent() ? Optional.of(translationCentreName.get()) : Optional.empty();
        return this;
    }

    public Builder clearTranslationCentreName() {
        this.translationCentreName = Optional.empty();
        return this;
    }

    public Builder mapTranslationCentreName(final UnaryOperator<String> mapper) {
        requireNonNull(mapper, "mapper");
        this.translationCentreName = this.translationCentreName.map(mapper);
        return this;
    }

    public Optional<ZonedDateTime> getTranslationTime() {
        return translationTime;
    }

    public Builder setTranslationTime(final ZonedDateTime translationTime) {
        this.translationTime = Optional.of(requireNonNull(translationTime, "translationTime"));
        return this;
    }

    public Builder setTranslationTime(final Optional<? extends ZonedDateTime> translationTime) {
        requireNonNull(translationTime, "translationTime");
        this.translationTime = translationTime.isPresent() ? Optional.of(translationTime.get()) : Optional.empty();
        return this;
    }

    public Builder clearTranslationTime() {
        this.translationTime = Optional.empty();
        return this;
    }

    public Builder mapTranslationTime(final UnaryOperator<ZonedDateTime> mapper) {
        requireNonNull(mapper, "mapper");
        this.translationTime = this.translationTime.map(mapper);
        return this;
    }

    public Optional<String> getTranslatedTAC() {
        return translatedTAC;
    }

    public Builder setTranslatedTAC(final String translatedTAC) {
        this.translatedTAC = Optional.of(requireNonNull(translatedTAC, "translatedTAC"));
        return this;
    }

    public Builder setTranslatedTAC(final Optional<? extends String> translatedTAC) {
        requireNonNull(translatedTAC, "translatedTAC");
        this.translatedTAC = translatedTAC.isPresent() ? Optional.of(translatedTAC.get()) : Optional.empty();
        return this;
    }

    public Builder clearTranslatedTAC() {
        this.translatedTAC = Optional.empty();
        return this;
    }

    public Builder mapTranslatedTAC(final UnaryOperator<String> mapper) {
        requireNonNull(mapper, "mapper");
        this.translatedTAC = this.translatedTAC.map(mapper);
        return this;
    }

    public Optional<PartialOrCompleteTimePeriod> getValidityTime() {
        return validityTime;
    }

    public Builder setValidityTime(final PartialOrCompleteTimePeriod validityTime) {
        this.validityTime = Optional.of(requireNonNull(validityTime, "validityTime"));
        return this;
    }

    public Builder setValidityTime(final Optional<? extends PartialOrCompleteTimePeriod> validityTime) {
        requireNonNull(validityTime, "validityTime");
        this.validityTime = validityTime.isPresent() ? Optional.of(validityTime.get()) : Optional.empty();
        return this;
    }

    public Builder clearValidityTime() {
        this.validityTime = Optional.empty();
        return this;
    }

    public Builder mapValidityTime(final UnaryOperator<PartialOrCompleteTimePeriod> mapper) {
        requireNonNull(mapper, "mapper");
        this.validityTime = this.validityTime.map(mapper);
        return this;
    }

    public Optional<TAFBaseForecast> getBaseForecast() {
        return baseForecast;
    }

    @JsonProperty("baseForecast")
    @JsonDeserialize(as = TAFBaseForecastImpl.class)
    public Builder setBaseForecast(final TAFBaseForecast baseForecast) {
        this.baseForecast = Optional.of(requireNonNull(baseForecast, "baseForecast"));
        return this;
    }

    public Builder setBaseForecast(final Optional<? extends TAFBaseForecast> baseForecast) {
        requireNonNull(baseForecast, "baseForecast");
        this.baseForecast = baseForecast.isPresent() ? Optional.of(baseForecast.get()) : Optional.empty();
        return this;
    }

    public Builder clearBaseForecast() {
        this.baseForecast = Optional.empty();
        return this;
    }

    public Builder mapBaseForecast(final UnaryOperator<TAFBaseForecast> mapper) {
        requireNonNull(mapper, "mapper");
        this.baseForecast = this.baseForecast.map(mapper);
        return this;
    }

    public Optional<List<TAFChangeForecast>> getChangeForecasts() {
        return changeForecasts;
    }

    @JsonProperty("changeForecasts")
    @JsonDeserialize(contentAs = TAFChangeForecastImpl.class)
    public Builder setChangeForecasts(final List<TAFChangeForecast> changeForecasts) {
        this.changeForecasts = Optional.of(requireNonNull(changeForecasts, "changeForecasts"));
        return this;
    }

    public Builder setChangeForecasts(final Optional<? extends List<TAFChangeForecast>> changeForecasts) {
        requireNonNull(changeForecasts, "changeForecasts");
        this.changeForecasts = changeForecasts.isPresent() ? Optional.of(changeForecasts.get()) : Optional.empty();
        return this;
    }

    public Builder clearChangeForecasts() {
        this.changeForecasts = Optional.empty();
        return this;
    }

    public Builder mapChangeForecasts(final UnaryOperator<List<TAFChangeForecast>> mapper) {
        requireNonNull(mapper, "mapper");
        this.changeForecasts = this.changeForecasts.map(mapper);
        return this;
    }

    public Optional<PartialOrCompleteTimePeriod> getReferredReportValidPeriod() {
        return referredReportValidPeriod;
    }

    public Builder setReferredReportValidPeriod(final PartialOrCompleteTimePeriod referredReportValidPeriod) {
        this.referredReportValidPeriod = Optional.of(requireNonNull(referredReportValidPeriod, "referredReportValidPeriod"));
        return this;
    }

    public Builder setReferredReportValidPeriod(final Optional<? extends PartialOrCompleteTimePeriod> referredReportValidPeriod) {
        requireNonNull(referredReportValidPeriod, "referredReportValidPeriod");
        this.referredReportValidPeriod = referredReportValidPeriod.isPresent() ? Optional.of(referredReportValidPeriod.get()) : Optional.empty();
        return this;
    }

    public Builder clearReferredReportValidPeriod() {
        this.referredReportValidPeriod = Optional.empty();
        return this;
    }

    public Builder mapReferredReportValidPeriod(final UnaryOperator<PartialOrCompleteTimePeriod> mapper) {
        requireNonNull(mapper, "mapper");
        this.referredReportValidPeriod = this.referredReportValidPeriod.map(mapper);
        return this;
    }



        Builder() {
            setCancelMessage(false);
            setReportStatus(ReportStatus.NORMAL);
            setTranslated(false);
        }

        @JsonProperty("aerodrome")
        @JsonDeserialize(as = AerodromeImpl.class)
        public Builder setAerodrome(final Aerodrome aerodrome) {
            this.aerodrome = requireNonNull(aerodrome, "aerodrome");
            return this;
        }

        public static Builder copyOf(final TAF value) {
            if (value instanceof TAFImpl) {
                return ((TAFImpl) value).toBuilder();
            } else {
                final Builder builder = builder();
                AviationWeatherMessageBuilderHelper.copyFrom(builder, value, //
                        Builder::setReportStatus, //
                        Builder::setIssueTime, //
                        Builder::setRemarks, //
                        Builder::setPermissibleUsage, //
                        Builder::setPermissibleUsageReason, //
                        Builder::setPermissibleUsageSupplementary, //
                        Builder::setTranslated, //
                        Builder::setTranslatedBulletinID, //
                        Builder::setTranslatedBulletinReceptionTime, //
                        Builder::setTranslationCentreDesignator, //
                        Builder::setTranslationCentreName, //
                        Builder::setTranslationTime, //
                        Builder::setTranslatedTAC);
                AerodromeWeatherMessageBuilderHelper.copyFrom(builder, value,  //
                        Builder::setAerodrome);
                return builder//
                        .setValidityTime(value.getValidityTime())//
                        .setBaseForecast(TAFBaseForecastImpl.immutableCopyOf(value.getBaseForecast()))//
                        .setChangeForecasts(value.getChangeForecasts()//
                                .map(changeForecasts -> BuilderHelper.toImmutableList(changeForecasts, TAFChangeForecastImpl::immutableCopyOf)))//
                        .setCancelMessage(value.isCancelMessage())//
                        .setReferredReportValidPeriod(value.getReferredReportValidPeriod());
            }
        }

        public Builder mergeFrom(final TAFImpl template) {
            requireNonNull(template, "template");
            this.aerodrome = template.getAerodrome();
            this.reportStatus = template.getReportStatus();
            this.cancelMessage = template.isCancelMessage();
            this.translated = template.isTranslated();
            this.issueTime = template.getIssueTime();
            this.remarks = template.getRemarks();
            this.permissibleUsage = template.getPermissibleUsage();
            this.permissibleUsageReason = template.getPermissibleUsageReason();
            this.permissibleUsageSupplementary = template.getPermissibleUsageSupplementary();
            this.translatedBulletinID = template.getTranslatedBulletinID();
            this.translatedBulletinReceptionTime = template.getTranslatedBulletinReceptionTime();
            this.translationCentreDesignator = template.getTranslationCentreDesignator();
            this.translationCentreName = template.getTranslationCentreName();
            this.translationTime = template.getTranslationTime();
            this.translatedTAC = template.getTranslatedTAC();
            this.validityTime = template.getValidityTime();
            this.baseForecast = template.getBaseForecast();
            this.changeForecasts = template.getChangeForecasts();
            this.referredReportValidPeriod = template.getReferredReportValidPeriod();
            return this;
        }

        public ImmutableTAFImpl build() {
            onValueOrPartialBuild();
            final ImmutableTAFImpl.Builder delegate = ImmutableTAFImpl.internalBuilder()//
                    .setAerodrome(getAerodrome())//
                    .setReportStatus(getReportStatus())//
                    .setCancelMessage(isCancelMessage())//
                    .setTranslated(isTranslated());
            getIssueTime().ifPresent(delegate::setIssueTime);
            getRemarks().ifPresent(delegate::setRemarks);
            getPermissibleUsage().ifPresent(delegate::setPermissibleUsage);
            getPermissibleUsageReason().ifPresent(delegate::setPermissibleUsageReason);
            getPermissibleUsageSupplementary().ifPresent(delegate::setPermissibleUsageSupplementary);
            getTranslatedBulletinID().ifPresent(delegate::setTranslatedBulletinID);
            getTranslatedBulletinReceptionTime().ifPresent(delegate::setTranslatedBulletinReceptionTime);
            getTranslationCentreDesignator().ifPresent(delegate::setTranslationCentreDesignator);
            getTranslationCentreName().ifPresent(delegate::setTranslationCentreName);
            getTranslationTime().ifPresent(delegate::setTranslationTime);
            getTranslatedTAC().ifPresent(delegate::setTranslatedTAC);
            getValidityTime().ifPresent(delegate::setValidityTime);
            getBaseForecast().ifPresent(delegate::setBaseForecast);
            getChangeForecasts().ifPresent(delegate::setChangeForecasts);
            getReferredReportValidPeriod().ifPresent(delegate::setReferredReportValidPeriod);
            return delegate.build();
        }

        private void onValueOrPartialBuild() {
            if (referredReportAerodrome != null) {
                final Aerodrome aerodrome = getNullableAerodrome();
                if (aerodrome == null) {
                    this.setAerodrome(referredReportAerodrome);
                } else if (!aerodrome.equals(referredReportAerodrome)) {
                    throw new IllegalStateException(
                            "TAF aerodrome and referred report aerodrome differ; TAF.aerodrome: " + aerodrome + "; referredReport.aerodrome: "
                                    + referredReportAerodrome);
                }
            }
            if (missingMessage) {
                clearBaseForecast();
            }
        }

        public Builder withCompleteIssueTime(final YearMonth yearMonth) {
            requireNonNull(yearMonth, "yearMonth");
            return mapIssueTime((input) -> input.toBuilder().completePartialAt(yearMonth).build());
        }

        public Builder withCompleteIssueTimeNear(final ZonedDateTime reference) {
            requireNonNull(reference, "reference");
            return mapIssueTime((input) -> input.toBuilder().completePartialNear(reference).build());
        }

        public Builder withCompleteForecastTimes(final YearMonth issueYearMonth, final int issueDay, final int issueHour, final ZoneId tz) {
            requireNonNull(issueYearMonth, "issueYearMonth");
            requireNonNull(tz, "tz");
            return withCompleteForecastTimes(
                    ZonedDateTime.of(LocalDateTime.of(issueYearMonth.getYear(), issueYearMonth.getMonth(), issueDay, issueHour, 0), tz));
        }

        public Builder withCompleteForecastTimes(final ZonedDateTime reference) {
            requireNonNull(reference, "reference");
            completeValidityTime(reference);
            completeReferredReportValidPeriod(reference);
            final ZonedDateTime validityStart = getValidityTime()//
                    .flatMap(PartialOrCompleteTimePeriod::getStartTime)//
                    .flatMap(PartialOrCompleteTimeInstant::getCompleteTime)//
                    .orElse(LocalDateTime.MIN.atZone(reference.getZone()));
            final ZonedDateTime validityEnd = getValidityTime()//
                    .flatMap(PartialOrCompleteTimePeriod::getEndTime)//
                    .flatMap(PartialOrCompleteTimeInstant::getCompleteTime)//
                    .orElse(LocalDateTime.MAX.atZone(reference.getZone()));
            completeAirTemperatureForecast(reference, validityStart, validityEnd);
            completeChangeForecastPeriods(reference, validityStart, validityEnd);
            return this;
        }

        public Builder withAllTimesComplete(final ZonedDateTime reference) {
            requireNonNull(reference, "reference");
            withCompleteIssueTimeNear(reference);
            return withCompleteForecastTimes(getIssueTime()//
                    .flatMap(PartialOrCompleteTimeInstant::getCompleteTime)//
                    .orElse(reference));
        }

        private Aerodrome getNullableAerodrome() {
            return aerodrome;
        }

        @Deprecated
        public Builder mapStatus(final UnaryOperator<TAFStatus> mapper) {
            requireNonNull(mapper, "mapper");
            return setStatus(mapper.apply(getStatus()));
        }

        /**
         * Provides the current builder value of the status property.
         *
         * Note, this method is provided for backward compatibility with previous versions of the API. The <code>status</code> is no longer
         * explicitly stored. This implementation uses {@link TAFStatus#fromReportStatus(ReportStatus, boolean, boolean)} instead to determine the returned
         * value
         * on-the-fly.
         *
         * @return the message status
         *
         * @deprecated migrate to using a combination of {@link #getReportStatus()} and {@link #isCancelMessage()} instead
         */
        @Deprecated
        public TAFStatus getStatus() {
            return TAFStatus.fromReportStatus(getReportStatus(), isCancelMessage(), isMissingMessage());
        }

        /**
         * Sets the TAF-specific message status.
         *
         * Note, this method is provided for backward compatibility with previous versions of the API. The <code>status</code> is no longer
         * explicitly stored. Instead, this method sets other property values with the following logic:
         * <dl>
         *     <dt>{@link fi.fmi.avi.model.AviationCodeListUser.TAFStatus#CANCELLATION CANCELLATION}</dt>
         *     <dd>
         *         <code>reportStatus = {@link fi.fmi.avi.model.AviationWeatherMessage.ReportStatus#AMENDMENT AMENDMENT}</code><br>
         *         <code>cancelMessage = true</code><br>
         *     </dd>
         *
         *     <dt>{@link fi.fmi.avi.model.AviationCodeListUser.TAFStatus#MISSING MISSING}</dt>
         *     <dd>
         *         <code>reportStatus = {@link fi.fmi.avi.model.AviationWeatherMessage.ReportStatus#NORMAL NORMAL}</code><br>
         *         <code>cancelMessage = false</code><br>
         *         <code>baseForecast = Optional.empty()</code> ; postponed until {@link #build()} by setting internal
         *         {@link #isMissingMessage()} missingMessage} flag.<br>
         *     </dd>
         *
         *     <dt>{@link fi.fmi.avi.model.AviationCodeListUser.TAFStatus#NORMAL NORMAL}</dt>
         *     <dd>
         *         <code>reportStatus = {@link fi.fmi.avi.model.AviationWeatherMessage.ReportStatus#NORMAL NORMAL}</code><br>
         *         <code>cancelMessage = false</code><br>
         *     </dd>
         *
         *     <dt>{@link fi.fmi.avi.model.AviationCodeListUser.TAFStatus#AMENDMENT AMENDMENT}</dt>
         *     <dd>
         *         <code>reportStatus = {@link fi.fmi.avi.model.AviationWeatherMessage.ReportStatus#AMENDMENT AMENDMENT}</code><br>
         *         <code>cancelMessage = false</code><br>
         *     </dd>
         *
         *     <dt>{@link fi.fmi.avi.model.AviationCodeListUser.TAFStatus#CORRECTION CORRECTION}</dt>
         *     <dd>
         *         <code>reportStatus = {@link fi.fmi.avi.model.AviationWeatherMessage.ReportStatus#CORRECTION CORRECTION}</code><br>
         *         <code>cancelMessage = false</code><br>
         *     </dd>
         * </dl>
         *
         * @param status
         *         the status to set
         *
         * @return builder
         *
         * @deprecated migrate to using a combination of {@link #setReportStatus(ReportStatus)} and {@link #setCancelMessage(boolean)}, and controlling
         * presence of {@link #setBaseForecast(Optional) baseForecast} instead
         */
        @Deprecated
        public Builder setStatus(final TAFStatus status) {
            requireNonNull(status);
            missingMessage = status.isMissingMessage();
            this.reportStatus = status.getReportStatus();
            this.cancelMessage = status.isCancelMessage();
            return this;
        }

        /**
         * Determines if the current builder status indicates a "missing" message. Message is considered missing if <em>one</em> of following conditions is
         * true:
         * <ul>
         *     <li>this builder <em>internal</em> {@code missingMessage} flag is set</li>
         *     <li>this builder does not represent a {@link #isCancelMessage() cancel message} and {@link #getBaseForecast() base forecast} is empty</li>
         * </ul>
         *
         * <p>
         *     The <em>internal</em> {@code missingMessage} flag is temporarily used for backwards compatibility with old api, and is set upon
         *     {@link #setStatus(TAFStatus) setStatus(MISSING)}. It is cleared on these invocations:
         * </p>
         * <ul>
         *     <li>{@link #setStatus(TAFStatus)} with status other than {@code MISSING}</li>
         *     <li>{@link #setCancelMessage(boolean)}</li>
         *     <li>{@link #setReportStatus(ReportStatus)}</li>
         * </ul>
         * <p>
         *     but <em>not</em> on {@link #setBaseForecast(TAFBaseForecast)} or {@link #clearBaseForecast()}. The base idea is that old deprecated and new
         *     API should not be mixed, but only one of them (preferably the new) should be used. The {@code baseForecast} property exists in both, therefore
         *     it is ambiguous whether e.g. clearing {@code baseForecast} denotes missing message or not.
         * </p>
         *
         * @return {@code true} if this builder represents a missing message; {@code false} otherwise
         */
        public boolean isMissingMessage() {
            return missingMessage || (!isCancelMessage() && !getBaseForecast().isPresent());
        }

        /**
         * Clears {@link #clearReferredReportValidPeriod() referredReportValidPeriod} and <em>internally</em> stored referredReport aerodrome.
         *
         * @return builder
         *
         * @deprecated please migrate to using {@link #clearReferredReportValidPeriod()} instead
         */
        @Deprecated
        public Builder clearReferredReport() {
            referredReportAerodrome = null;
            return clearReferredReportValidPeriod();
        }

        @Deprecated
        public Builder mapReferredReport(final UnaryOperator<TAFReference> mapper) {
            requireNonNull(mapper, "mapper");
            final Optional<TAFReference> ref = getReferredReport();
            if (ref.isPresent()) {
                return setReferredReport(mapper.apply(ref.get()));
            } else {
                return this;
            }
        }

        /**
         * Provides the current builder value of the referredReport property.
         *
         * <p>
         * Note, this method is provided for backward compatibility with previous versions of the API. The <code>referredReport</code> is no longer
         * explicitly stored. This implementation uses {@link #getAerodrome()} and {@link #getReferredReportValidPeriod()} instead to determine the returned
         * value on-the-fly for cancel, amendment and correction messages. Returns {@link Optional#empty()} if {@link #getReferredReportValidPeriod()} is
         * not present or if aerodrome cannot be determined because either {@link #setAerodrome(Aerodrome)} nor {@link #setReferredReport(TAFReference)}
         * is not invoked.
         * </p>
         *
         * @return the amended message information messages
         *
         * @deprecated please migrate to using {@link #getAerodrome()} and {@link #getReferredReportValidPeriod()} instead
         */
        @Deprecated
        public Optional<TAFReference> getReferredReport() {
            final Aerodrome aerodrome = referredReportAerodrome == null ? getNullableAerodrome() : referredReportAerodrome;
            if (aerodrome != null && getReferredReportValidPeriod().isPresent()) {
                return Optional.of(TAFReferenceImpl.builder()
                        .setAerodrome(AerodromeImpl.immutableCopyOf(aerodrome))
                        .setValidityTime(getReferredReportValidPeriod())
                        .build());
            } else {
                return Optional.empty();
            }
        }

        /**
         * Sets the link to another (referred) report used for cancellation and amendment messages.
         * <p>
         * Note, this method is provided for backward compatibility with previous versions of the API. The <code>referredReport</code> is no longer
         * explicitly stored. Instead, this method sets {@link #setReferredReportValidPeriod(PartialOrCompleteTimePeriod)} and stores <em>internally</em> the
         * {@link TAFReference#getAerodrome() aerodrome} of referred report to be checked against {@link #getAerodrome()} upon {@link #build()}.
         * <p>
         *
         * @param referredReport
         *         the reference to the amended message
         *
         * @return the builder
         *
         * @throws IllegalArgumentException
         *         if the {@link TAFReference#getAerodrome()} does not equal {@link #getAerodrome()} aerodrome
         * @deprecated please migrate into using {@link #setReferredReportValidPeriod(PartialOrCompleteTimePeriod)} instead
         */
        @Deprecated
        public Builder setReferredReport(final TAFReference referredReport) {
            referredReportAerodrome = referredReport.getAerodrome();
            setReferredReportValidPeriod(referredReport.getValidityTime());
            return this;
        }

        /**
         * Sets or clears the link to another (referred) report used for cancellation and amendment messages.
         * <p>
         * Note, this method is provided for backward compatibility with previous versions of the API. The <code>referredReport</code> is no longer
         * explicitly stored. See {@link #setReferredReport(TAFReference)} and {@link #clearReferredReport()} for description of transition phase behavior.
         * <p>
         *
         * @param referredReport
         *         the reference to the amended message
         *
         * @return the builder
         *
         * @throws IllegalArgumentException
         *         if the {@link TAFReference#getAerodrome()} does not equal {@link #getAerodrome()} aerodrome
         * @deprecated please migrate into using {@link #setReferredReportValidPeriod(Optional)} instead
         */
        @Deprecated
        public Builder setReferredReport(final Optional<TAFReference> referredReport) {
            if (referredReport.isPresent()) {
                return setReferredReport(referredReport.get());
            } else {
                return clearReferredReport();
            }
        }

        private void completeValidityTime(final ZonedDateTime reference) {
            mapValidityTime(validityTime -> validityTime.toBuilder().completePartialStartingNear(reference).build());
        }

        private void completeReferredReportValidPeriod(final ZonedDateTime reference) {
            mapReferredReportValidPeriod(validityTime -> validityTime.toBuilder().completePartialStartingNear(reference).build());
        }

        private void completeAirTemperatureForecast(final ZonedDateTime reference, final ZonedDateTime validityStart, final ZonedDateTime validityEnd) {
            if (getBaseForecast().isPresent() && getBaseForecast().get().getTemperatures().isPresent()) {
                final List<TAFAirTemperatureForecast> temperatureForecasts = new ArrayList<>();
                final Function<PartialDateTime, ZonedDateTime> completion = partial -> toZonedDateTimeSatisfyingConditionOrNear(
                        PartialDateTime.ReferenceCondition.NEAR, false, validityStart, validityEnd).apply(partial, reference);
                for (final TAFAirTemperatureForecast airTemp : getBaseForecast().get().getTemperatures().get()) {
                    temperatureForecasts.add(TAFAirTemperatureForecastImpl.builder()//
                            .setMaxTemperature(NumericMeasureImpl.immutableCopyOf(airTemp.getMaxTemperature()))//
                            .setMinTemperature(NumericMeasureImpl.immutableCopyOf(airTemp.getMinTemperature()))//
                            .setMaxTemperatureTime(airTemp.getMaxTemperatureTime().toBuilder().completePartial(completion).build())//
                            .setMinTemperatureTime(airTemp.getMinTemperatureTime().toBuilder().completePartial(completion).build())//
                            .build());
                }
                mapBaseForecast(fct -> TAFBaseForecastImpl.Builder.copyOf(fct).setTemperatures(Collections.unmodifiableList(temperatureForecasts)).build());
            }
        }

        private void completeChangeForecastPeriods(final ZonedDateTime reference, final ZonedDateTime validityStart, final ZonedDateTime validityEnd) {
            if (getChangeForecasts().isPresent() && !getChangeForecasts().get().isEmpty()) {
                final List<TAFChangeForecast> changeForecasts = getChangeForecasts().get();
                final Iterable<PartialOrCompleteTimePeriod> partialTimes = changeForecasts.stream().map(TAFChangeForecast::getPeriodOfChange)::iterator;
                final List<PartialOrCompleteTime> times = PartialOrCompleteTimes.completeAscendingPartialTimes(partialTimes, reference,
                        toZonedDateTimeSatisfyingConditionOrNear(PartialDateTime.ReferenceCondition.NOT_BEFORE, false, validityStart, validityEnd));
                final List<TAFChangeForecast> completedForecasts = new ArrayList<>();
                for (int i = 0; i < times.size(); i++) {
                    final PartialOrCompleteTime time = times.get(i);
                    completedForecasts.add(
                            TAFChangeForecastImpl.Builder.copyOf(changeForecasts.get(i)).setPeriodOfChange((PartialOrCompleteTimePeriod) time).build());
                }
                setChangeForecasts(Collections.unmodifiableList(completedForecasts));
            }
        }

        private BiFunction<PartialDateTime, ZonedDateTime, ZonedDateTime> toZonedDateTimeSatisfyingConditionOrNear(
                final PartialDateTime.ReferenceCondition condition, final boolean strictCondition, final ZonedDateTime validityStart,
                final ZonedDateTime validityEnd) {
            return (partial, reference) -> {
                try {
                    return partial.toZonedDateTime(reference, condition, strictCondition, validityStart, validityEnd);
                } catch (final DateTimeException exception) {
                    try {
                        return partial.toZonedDateTimeNear(reference);
                    } catch (final DateTimeException ignored) {
                        throw exception;
                    }
                }
            };
        }
    }
}
