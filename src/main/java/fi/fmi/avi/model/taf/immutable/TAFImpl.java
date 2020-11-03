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
import java.util.stream.Collectors;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.Aerodrome;
import fi.fmi.avi.model.AviationWeatherMessage;
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
@JsonPropertyOrder({ "status", "aerodrome", "issueTime", "validityTime", "baseForecast", "changeForecasts", "referredReport", "isCancelledMessage",
        "isMissingMessage", "cancelledReportValidPeriod", "reportStatus", "remarks", "permissibleUsage", "permissibleUsageReason",
        "permissibleUsageSupplementary", "translated", "translatedBulletinID", "translatedBulletinReceptionTime", "translationCentreDesignator",
        "translationCentreName", "translationTime", "translatedTAC" })
public abstract class TAFImpl implements TAF, Serializable {

    private static final long serialVersionUID = -449932311496894566L;

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
                || getBaseForecast().isPresent() && !getBaseForecast().get().areAllTimeReferencesComplete() //
                || getReferredReport().isPresent() && !getReferredReport().get().areAllTimeReferencesComplete()) {
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

        @Deprecated
        public Builder() {
            setStatus(TAFStatus.NORMAL);
            setTranslated(false);
        }

        public static Builder from(final TAF value) {
            if (value instanceof TAFImpl) {
                return ((TAFImpl) value).toBuilder();
            } else {
                //From AviationWeatherMessage:
                final Builder retval = builder()//
                        .setReportStatus(value.getReportStatus())
                        .setPermissibleUsage(value.getPermissibleUsage())
                        .setPermissibleUsageReason(value.getPermissibleUsageReason())
                        .setPermissibleUsageSupplementary(value.getPermissibleUsageSupplementary())
                        .setTranslated(value.isTranslated())
                        .setTranslatedBulletinID(value.getTranslatedBulletinID())
                        .setTranslatedBulletinReceptionTime(value.getTranslatedBulletinReceptionTime())
                        .setTranslationCentreDesignator(value.getTranslationCentreDesignator())
                        .setTranslationCentreName(value.getTranslationCentreName())
                        .setTranslationTime(value.getTranslationTime())
                        .setTranslatedTAC(value.getTranslatedTAC());

                value.getRemarks().map(remarks -> retval.setRemarks(Collections.unmodifiableList(remarks)));

                //From AerodromeWeatherMessage:
                retval.setAerodrome(AerodromeImpl.immutableCopyOf(value.getAerodrome()))//
                        .setIssueTime(value.getIssueTime());

                //From TAF:
                retval.setStatus(value.getStatus())
                        .setValidityTime(value.getValidityTime())
                        .setBaseForecast(TAFBaseForecastImpl.immutableCopyOf(value.getBaseForecast()))
                        .setReferredReport(TAFReferenceImpl.immutableCopyOf(value.getReferredReport()));

                value.getChangeForecasts()
                        .map(forecasts -> retval.setChangeForecasts(
                                Collections.unmodifiableList(forecasts.stream().map(TAFChangeForecastImpl::immutableCopyOf).collect(Collectors.toList()))));
                return retval;
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
            completeReferredReport(reference, validityEnd);
            return this;
        }

        public Builder withAllTimesComplete(final ZonedDateTime reference) {
            requireNonNull(reference, "reference");
            withCompleteIssueTimeNear(reference);
            return withCompleteForecastTimes(getIssueTime()//
                    .flatMap(PartialOrCompleteTimeInstant::getCompleteTime)//
                    .orElse(reference));
        }

        /**
         * Sets the TAF-specific message status.
         * <p>
         * Note, this method is provided for backward compatibility with previous versions of the API. In addition to
         * setting the <code>status</code> value, it also sets other property values with the following logic:
         * <ul>
         *     <li>status is {@link fi.fmi.avi.model.AviationCodeListUser.TAFStatus#CANCELLATION}: <code>reportStatus =</code>
         *     {@link fi.fmi.avi.model.AviationWeatherMessage.ReportStatus#NORMAL}, <code>cancelMessage = true</code>, <code>missingMessage = false</code></li>
         *     <li>status is {@link fi.fmi.avi.model.AviationCodeListUser.TAFStatus#MISSING}: <code>reportStatus =</code>
         *     {@link fi.fmi.avi.model.AviationWeatherMessage.ReportStatus#NORMAL}, <code>cancelMessage = false</code>, <code>missingMessage = true</code></li>
         *     <li>status is {@link fi.fmi.avi.model.AviationCodeListUser.TAFStatus#NORMAL}: <code>reportStatus =</code>
         *     {@link fi.fmi.avi.model.AviationWeatherMessage.ReportStatus#NORMAL}, <code>cancelMessage = false</code>, <code>missingMessage = false</code></li>
         *     <li>status is {@link fi.fmi.avi.model.AviationCodeListUser.TAFStatus#AMENDMENT}: <code>reportStatus =</code>
         *     {@link fi.fmi.avi.model.AviationWeatherMessage.ReportStatus#AMENDMENT}, <code>cancelMessage = false</code>, <code>missingMessage = false</code></li>
         *     <li>status is {@link fi.fmi.avi.model.AviationCodeListUser.TAFStatus#CORRECTION}: <code>reportStatus =</code>
         *     {@link fi.fmi.avi.model.AviationWeatherMessage.ReportStatus#CORRECTION}, <code>cancelMessage = false</code>, <code>missingMessage = false</code></li>
         * </ul>
         *
         * @param status the status to set
         * @return builder
         * @deprecated migrate to using a combination of {@link #setReportStatus(ReportStatus)}, {@link #setCancelMessage(boolean)} and
         * {@link #setMissingMessage(boolean)} instead
         */
        @Override
        @Deprecated
        public Builder setStatus(final TAFStatus status) {
            super.setStatus(status);
            if (TAFStatus.CANCELLATION.equals(status)) {
                super.setReportStatus(ReportStatus.NORMAL).setCancelMessage(true).setMissingMessage(false);
            } else if (TAFStatus.MISSING.equals(status)) {
                super.setReportStatus(ReportStatus.NORMAL).setMissingMessage(true).setCancelMessage(false);
            } else {
                if (TAFStatus.NORMAL.equals(status)) {
                    super.setReportStatus(ReportStatus.NORMAL);
                } else if (TAFStatus.AMENDMENT.equals(status)) {
                    super.setReportStatus(ReportStatus.AMENDMENT);
                } else if (TAFStatus.CORRECTION.equals(status)) {
                    super.setReportStatus(ReportStatus.CORRECTION);
                } else {
                    throw new IllegalArgumentException("Cannot determine report status from TAFStatus " + status);
                }
                super.setMissingMessage(false).setCancelMessage(false);
            }
            return this;
        }

        /**
         * Provides the current builder value of the status property.
         *
         * @return the message status
         * @deprecated migrate to using a combination of {@link #getReportStatus()}, {@link #isCancelMessage()} and
         * * {@link #isMissingMessage()} instead
         */
        @Override
        @Deprecated
        public TAFStatus getStatus() {
            return super.getStatus();
        }

        /**
         * Sets the report message status.
         * <p>
         * Note that for backward compatibility this method also sets the <code>status</code> property with the following logic:
         * <ul>
         *     <li>status is {@link fi.fmi.avi.model.AviationWeatherMessage.ReportStatus#NORMAL}: <code>status =</code>
         *     {@link fi.fmi.avi.model.AviationCodeListUser.TAFStatus#NORMAL}</li>
         *     <li>status is {@link fi.fmi.avi.model.AviationWeatherMessage.ReportStatus#AMENDMENT}: <code>status =</code>
         *     {@link fi.fmi.avi.model.AviationCodeListUser.TAFStatus#AMENDMENT}</li>
         *     <li>status is {@link fi.fmi.avi.model.AviationWeatherMessage.ReportStatus#CORRECTION}: <code>status =</code>
         *     {@link fi.fmi.avi.model.AviationCodeListUser.TAFStatus#CORRECTION}</li>
         * </ul>
         *
         * @param status the message status to set
         * @return the builder
         * @see #setReportStatus(ReportStatus)
         */
        @Override
        public Builder setReportStatus(final ReportStatus status) {
            super.setReportStatus(status);
            if (ReportStatus.NORMAL.equals(status)) {
                super.setStatus(TAFStatus.NORMAL);
            } else if (ReportStatus.AMENDMENT.equals(status)) {
                super.setStatus(TAFStatus.AMENDMENT);
            } else if (ReportStatus.CORRECTION.equals(status)) {
                super.setStatus(TAFStatus.CORRECTION);
            } else {
                throw new IllegalArgumentException("Cannot determine TAFStatus from report status " + status);
            }
            return this;
        }

        /**
         * Sets the aerodrome which the TAF applies to.
         * <p>
         * Additionally if {@link #getCancelledReportValidPeriod()} is present but {@link #getReferredReport()} is not, sets the referred report with the
         * provided aerodrome and the value of {@link #getCancelledReportValidPeriod()}.
         *
         * @param aerodrome
         * @return
         */
        @Override
        @JsonDeserialize(as = AerodromeImpl.class)
        public Builder setAerodrome(final Aerodrome aerodrome) {
            super.setAerodrome(aerodrome);
            if (super.getCancelledReportValidPeriod().isPresent() && !super.getReferredReport().isPresent()) {
                super.setReferredReport(TAFReferenceImpl.builder()//
                        .setAerodrome(this.getAerodrome())//
                        .setValidityTime(super.getCancelledReportValidPeriod().get())//
                        .build());
            }
            return this;
        }

        @Override
        @JsonDeserialize(as = TAFBaseForecastImpl.class)
        public Builder setBaseForecast(final TAFBaseForecast baseForecast) {
            return super.setBaseForecast(baseForecast);
        }

        @Override
        @JsonDeserialize(contentAs = TAFChangeForecastImpl.class)
        public Builder setChangeForecasts(final List<TAFChangeForecast> changeForecasts) {
            return super.setChangeForecasts(changeForecasts);
        }

        /**
         * Sets the link to another (referred) report used for cancellation and amendment messages.
         * <p>
         * Also sets the {@link #setCancelledReportValidPeriod(PartialOrCompleteTimePeriod)} using the {@link TAFReference#getValidityTime()} value if
         * {@link #isCancelMessage()} equals true.
         *
         * @param referredReport the reference
         * @return the builder
         */
        @Override
        @JsonDeserialize(as = TAFReferenceImpl.class)
        public Builder setReferredReport(final TAFReference referredReport) {
            super.setReferredReport(referredReport);
            if (this.isCancelMessage() && referredReport.getValidityTime().isPresent()) {
                super.setCancelledReportValidPeriod(referredReport.getValidityTime().get());
            }
            return this;
        }

        /**
         * Sets the cancellation status of this message.
         * If <code>cancel == true</code> also calls {@link #setReportStatus(ReportStatus)} with
         * {@link fi.fmi.avi.model.AviationCodeListUser.TAFStatus#CANCELLATION}. Otherwise resets the <code>status</code> value based on the
         * {@link #getReportStatus()} value.
         *
         * @param cancel true to set as cancellation, false to unset
         * @return the builder
         */
        @Override
        public Builder setCancelMessage(final boolean cancel) {
            super.setCancelMessage(cancel);
            if (cancel) {
                super.setStatus(TAFStatus.CANCELLATION);
            } else {
                if (this.getReportStatus().isPresent()) {
                    this.setReportStatus(this.getReportStatus()); // takes care of setting the status based on reportStatus
                }
            }
            if (cancel && this.getReferredReport().isPresent()) {
                this.setCancelledReportValidPeriod(this.getReferredReport().get().getValidityTime());
            }
            return this;
        }

        /**
         * Sets the cancellation status of this message.
         * If <code>missing == true</code> also calls {@link #setReportStatus(AviationWeatherMessage.ReportStatus)} with
         * {@link fi.fmi.avi.model.AviationCodeListUser.TAFStatus#MISSING}. Otherwise resets the <code>status</code> value based on the
         * {@link #getReportStatus()} value.
         *
         * @param missing true to set as missing message, false to unset
         * @return the builder
         */
        @Override
        public Builder setMissingMessage(final boolean missing) {
            super.setMissingMessage(missing);
            if (missing) {
                super.setStatus(TAFStatus.MISSING);
            } else {
                if (this.getReportStatus().isPresent()) {
                    this.setReportStatus(this.getReportStatus()); // takes care of setting the status based on reportStatus
                }
            }
            return this;
        }

        /**
         * Sets the time period of the cancelled previously issued message.
         * Additionally: If {@link #getReferredReport()} is present, updates it's validity time period. If not present, created a new instance of
         * {@link TAFReference} with using the {@link #getAerodrome()} and the provided time period. If the aerodrome is not set, does not set the
         * referredReport property.
         *
         * @param period
         * @return
         */
        @Override
        public Builder setCancelledReportValidPeriod(final PartialOrCompleteTimePeriod period) {
            super.setCancelledReportValidPeriod(period);
            if (this.getReferredReport().isPresent()) {
                super.setReferredReport(TAFReferenceImpl.immutableCopyOf(this.getReferredReport().get()).toBuilder()//
                        .setValidityTime(period)//
                        .build());
            } else {
                try {
                    super.setReferredReport(TAFReferenceImpl.builder()//
                            .setAerodrome(this.getAerodrome())//
                            .setValidityTime(period)//
                            .build());
                } catch (final IllegalStateException ise) {
                    //Aerodrome is not set at this point, defer to setting the referredReport when setting the Aerodrome
                }
            }
            return this;
        }

        @Override
        public TAFImpl build() {
            // Referred report aerodrome consistency:
            if (this.getReferredReport().isPresent()) {
                final Aerodrome base = this.getAerodrome();
                final Aerodrome ref = this.getReferredReport().get().getAerodrome();
                if (base != null && ref != null && !base.equals(ref)) {
                    throw new IllegalStateException("Aerodrome " + base + " set for TAF is not the same as the aerodrome set for the referred report " + ref);
                }
            }
            //cancelReport - referredReport validity time consistency
            if (this.isCancelMessage()) {
                if (this.getReferredReport().isPresent() && this.getReferredReport().get().getValidityTime().isPresent()) {
                    if (this.getCancelledReportValidPeriod().isPresent()) {
                        if (!this.getReferredReport().get().getValidityTime().get().equals(this.getCancelledReportValidPeriod().get())) {
                            throw new IllegalStateException("ReferredReport.validityTime and cancelledReportValidPeriod are both given but not equal");
                        }
                    }
                }
            }
            return super.build();
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

        private void completeReferredReport(final ZonedDateTime reference, final ZonedDateTime validityEnd) {
            mapReferredReport(referredReport -> {
                final TAFReferenceImpl.Builder builder = TAFReferenceImpl.Builder.from(referredReport);
                if (validityEnd.toLocalDateTime().equals(LocalDateTime.MAX)) {
                    builder.withAllTimesComplete(reference);
                } else {
                    builder.withAllTimesCompleteFromValidityEnd(validityEnd);
                }
                return builder.build();
            });
        }
    }
}
