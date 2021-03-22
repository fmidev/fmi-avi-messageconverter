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

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.Aerodrome;
import fi.fmi.avi.model.AerodromeWeatherMessageBuilderHelper;
import fi.fmi.avi.model.AviationWeatherMessageBuilderHelper;
import fi.fmi.avi.model.BuilderHelper;
import fi.fmi.avi.model.PartialDateTime;
import fi.fmi.avi.model.PartialOrCompleteTime;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.PartialOrCompleteTimes;
import fi.fmi.avi.model.immutable.AerodromeImpl;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.TAFAirTemperatureForecast;
import fi.fmi.avi.model.taf.TAFBaseForecast;
import fi.fmi.avi.model.taf.TAFChangeForecast;
import fi.fmi.avi.model.taf.TAFReference;

/**
 * Created by rinne on 18/04/2018.
 */
@FreeBuilder
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
            return Builder.from(taf).build();
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

    public abstract Builder toBuilder();

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

    public static class Builder extends TAFImpl_Builder {
        @Nullable
        private Aerodrome referredReportAerodrome;
        private boolean missingMessage;

        @Deprecated
        public Builder() {
            setCancelMessage(false);
            setReportStatus(ReportStatus.NORMAL);
            setTranslated(false);
        }

        public static Builder from(final TAF value) {
            if (value instanceof TAFImpl) {
                return ((TAFImpl) value).toBuilder();
            } else {
                final Builder builder = builder();
                AviationWeatherMessageBuilderHelper.copyFrom(builder, value,  //
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
                        Builder::setTranslatedTAC, //
                        Builder::setIssueTime, //
                        Builder::setReportStatus);
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

        @Override
        public TAFImpl build() {
            onValueOrPartialBuild();
            return super.build();
        }

        @Override
        public TAFImpl buildPartial() {
            onValueOrPartialBuild();
            return super.buildPartial();
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
            try {
                return getAerodrome();
            } catch (final IllegalStateException ignored) {
                return null;
            }
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
         *         <code>baseForecast = Optional.empty()</code> ; postponed until {@link #build()} / {@link #buildPartial()} by setting internal
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
            super.setReportStatus(status.getReportStatus());
            super.setCancelMessage(status.isCancelMessage());
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

        @Override
        @JsonDeserialize(as = AerodromeImpl.class)
        public Builder setAerodrome(final Aerodrome aerodrome) {
            super.setAerodrome(aerodrome);
            return this;
        }

        @Override
        @JsonDeserialize(as = TAFBaseForecastImpl.class)
        public Builder setBaseForecast(final TAFBaseForecast baseForecast) {
            super.setBaseForecast(baseForecast);
            return this;
        }

        @Override
        @JsonDeserialize(contentAs = TAFChangeForecastImpl.class)
        public Builder setChangeForecasts(final List<TAFChangeForecast> changeForecasts) {
            return super.setChangeForecasts(changeForecasts);
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

        /**
         * {@inheritDoc}
         *
         * <p>
         * Additionally this method clears the <em>internal</em> {@code missingMessage} flag (see {@link #isMissingMessage()} for details).
         * </p>
         *
         * @param cancelMessage
         *         {@inheritDoc}
         *
         * @return {@inheritDoc}
         */
        @Override
        public Builder setCancelMessage(final boolean cancelMessage) {
            missingMessage = false;
            return super.setCancelMessage(cancelMessage);
        }

        /**
         * {@inheritDoc}
         *
         * <p>
         * Additionally this method clears the <em>internal</em> {@code missingMessage} flag (see {@link #isMissingMessage()} for details).
         * </p>
         *
         * @param reportStatus
         *         {@inheritDoc}
         *
         * @return {@inheritDoc}
         */
        @Override
        public Builder setReportStatus(final ReportStatus reportStatus) {
            missingMessage = false;
            return super.setReportStatus(reportStatus);
        }

        private void completeValidityTime(final ZonedDateTime reference) {
            mapValidityTime(validityTime -> validityTime.toBuilder().completePartialStartingNear(reference).build());
        }

        private void completeAirTemperatureForecast(final ZonedDateTime reference, final ZonedDateTime validityStart, final ZonedDateTime validityEnd) {
            if (getBaseForecast().isPresent() && getBaseForecast().get().getTemperatures().isPresent()) {
                final List<TAFAirTemperatureForecast> temperatureForecasts = new ArrayList<>();
                final Function<PartialDateTime, ZonedDateTime> completion = partial -> toZonedDateTimeSatisfyingConditionOrNear(
                        PartialDateTime.ReferenceCondition.NEAR, false, validityStart, validityEnd).apply(partial, reference);
                for (final TAFAirTemperatureForecast airTemp : getBaseForecast().get().getTemperatures().get()) {
                    temperatureForecasts.add(TAFAirTemperatureForecastImpl.Builder.from(airTemp)
                            .mutateMinTemperatureTime(time -> time.completePartial(completion).build())
                            .mutateMaxTemperatureTime(time -> time.completePartial(completion).build())
                            .build());
                }
                mapBaseForecast(fct -> TAFBaseForecastImpl.Builder.from(fct).setTemperatures(Collections.unmodifiableList(temperatureForecasts)).build());
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
                            TAFChangeForecastImpl.Builder.from(changeForecasts.get(i)).setPeriodOfChange((PartialOrCompleteTimePeriod) time).build());
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
