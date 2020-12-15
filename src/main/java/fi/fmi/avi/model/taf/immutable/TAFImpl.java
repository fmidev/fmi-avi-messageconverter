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
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.Aerodrome;
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
@JsonPropertyOrder({ "aerodrome", "issueTime", "validityTime", "baseForecast", "changeForecasts", "isCancelledMessage", "isMissingMessage",
        "referredReportValidPeriod", "reportStatus", "remarks", "permissibleUsage", "permissibleUsageReason", "permissibleUsageSupplementary", "translated",
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
     * @deprecated please migrate to using a combination of {@link #getReportStatus()} and {@link #isCancelMessage()} instead
     */
    @Override
    @JsonIgnore
    @Deprecated
    public TAFStatus getStatus() {
        return TAFStatus.fromReportStatus(getReportStatus().orElse(ReportStatus.NORMAL), isCancelMessage(), isMissingMessage());
    }

    /**
     * Provides the value of the referredReport property.
     * <p>
     * Note, this method is provided for backward compatibility with previous versions of the API. The <code>referredReport</code> is no longer
     * explicitly stored. This implementation uses {@link #getAerodrome()} and {@link #getReferredReportValidPeriod()} instead to determine the returned value
     * on-the-fly for cancel, amendment and correction messages. Returns {@link Optional#empty()} if {@link #getReferredReportValidPeriod()} is
     * not present or {@link #isCancelMessage()} is false or {@link #getReportStatus()} returns
     * {@link fi.fmi.avi.model.AviationWeatherMessage.ReportStatus#NORMAL}.
     *
     * @return the amended message information messages
     * @deprecated please migrate to using {@link #getAerodrome()} and {@link #getReferredReportValidPeriod()} instead
     */
    @Override
    @JsonIgnore
    @Deprecated
    public Optional<TAFReference> getReferredReport() {
        final Optional<ReportStatus> status = getReportStatus();
        if (getReferredReportValidPeriod().isPresent() && (isCancelMessage() || status.isPresent()//
                && (ReportStatus.AMENDMENT.equals(status.get()) || ReportStatus.CORRECTION.equals(status.get())))) {
            return Optional.of(TAFReferenceImpl.builder()
                    .setAerodrome(AerodromeImpl.immutableCopyOf(this.getAerodrome()))
                    .setValidityTime(getReferredReportValidPeriod())
                    .build());
        } else {
            return Optional.empty();
        }

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
        private TAFBaseForecast deletedBaseForecast;

        @Nullable
        private PartialOrCompleteTimePeriod possibleReferredReportValidPeriod;

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
                retval.setCancelMessage(value.isCancelMessage()).setValidityTime(value.getValidityTime())//
                        .setBaseForecast(TAFBaseForecastImpl.immutableCopyOf(value.getBaseForecast()))//
                        .setReferredReportValidPeriod(value.getReferredReportValidPeriod());

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
            //completeReferredReport(reference, validityEnd);
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
         *
         * Note, this method is provided for backward compatibility with previous versions of the API. The <code>status</code> is no longer
         * explicitly stored. Instead, this method sets other property values with the following logic:
         * <ul>
         *     <li>status is {@link fi.fmi.avi.model.AviationCodeListUser.TAFStatus#CANCELLATION}:<code>reportStatus =</code>
         *     {@link fi.fmi.avi.model.AviationWeatherMessage.ReportStatus#AMENDMENT}, <code>cancelMessage = true</code>. Also sets
         *     {@link #setReferredReportValidPeriod(PartialOrCompleteTimePeriod)} if {@link #setReferredReport(TAFReference)} was previously called with a
         *     present {@link TAFReference#getValidityTime()} when {@link #isCancelMessage()} was false</li>
         *     <li>status is {@link fi.fmi.avi.model.AviationCodeListUser.TAFStatus#MISSING}: clears the baseForecast if present, but keeps a temporary
         *     copy internally in the builder in case the status is later set back to other value without setting a new baseForecast value explicitly</li>
         *     <li>status is {@link fi.fmi.avi.model.AviationCodeListUser.TAFStatus#NORMAL}: <code>reportStatus =</code>
         *     {@link fi.fmi.avi.model.AviationWeatherMessage.ReportStatus#NORMAL}, <code>cancelMessage = false</code></li>
         *     <li>status is {@link fi.fmi.avi.model.AviationCodeListUser.TAFStatus#AMENDMENT}: <code>reportStatus =</code>
         *     {@link fi.fmi.avi.model.AviationWeatherMessage.ReportStatus#AMENDMENT}, <code>cancelMessage = false</code></li>
         *     <li>status is {@link fi.fmi.avi.model.AviationCodeListUser.TAFStatus#CORRECTION}: <code>reportStatus =</code>
         *     {@link fi.fmi.avi.model.AviationWeatherMessage.ReportStatus#CORRECTION}, <code>cancelMessage = false</code></li>
         * </ul>
         *
         * @param status the status to set
         * @return builder
         * @deprecated migrate to using a combination of {@link #setReportStatus(ReportStatus)} and {@link #setCancelMessage(boolean)} instead
         */
        @Deprecated
        public Builder setStatus(final TAFStatus status) {
            requireNonNull(status, "tafStatus");
            final Optional<TAFBaseForecast> base = getBaseForecast();
            if (TAFStatus.MISSING.equals(status)) {
                if (base.isPresent()) {
                    this.deletedBaseForecast = base.get();
                    clearBaseForecast();
                }
            } else if (deletedBaseForecast != null) {
                if (base.isPresent()) {
                    deletedBaseForecast = null;
                } else {
                    setBaseForecast(deletedBaseForecast); //  nullifies deletedBaseForecast internally
                }
            }
            setReportStatus(status.getReportStatus());
            if (TAFStatus.CANCELLATION.equals(status)) {
                this.setCancelMessage(true);
            }
            if (TAFStatus.AMENDMENT.equals(status) || TAFStatus.CANCELLATION.equals(status) || TAFStatus.CORRECTION.equals(status)) {
                if (this.possibleReferredReportValidPeriod != null) {
                    if (getReferredReportValidPeriod().isPresent()) {
                        possibleReferredReportValidPeriod = null;
                    } else {
                        setReferredReportValidPeriod(this.possibleReferredReportValidPeriod);
                    }
                }
            }
            return this;
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
         * @deprecated migrate to using a combination of {@link #getReportStatus()} and {@link #isCancelMessage()} instead
         */
        @Deprecated
        public TAFStatus getStatus() {
            return TAFStatus.fromReportStatus(getReportStatus().orElse(ReportStatus.NORMAL), isCancelMessage(), isMissingMessage());
        }

        /**
         * Determines if the current builder status indicates a "missing" message.
         *
         * @return false if the {@link #getBaseForecast()} is present, true otherwise
         */
        public boolean isMissingMessage() {
            return !getBaseForecast().isPresent();
        }

        /**
         * Sets the aerodrome which the TAF applies to.
         * <p>
         * Additionally if {@link #getReferredReportValidPeriod()} is present but {@link #getReferredReport()} is not, sets the referred report with the
         * provided aerodrome and the value of {@link #getReferredReportValidPeriod()}.
         *
         * @param aerodrome
         * @return
         */
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
            deletedBaseForecast = null;
            return this;
        }

        @Override
        @JsonDeserialize(contentAs = TAFChangeForecastImpl.class)
        public Builder setChangeForecasts(final List<TAFChangeForecast> changeForecasts) {
            return super.setChangeForecasts(changeForecasts);
        }

        /**
         * Sets the link to another (referred) report used for cancellation and amendment messages.
         * <p>
         * Note, this method is provided for backward compatibility with previous versions of the API. The <code>referredReport</code> is no longer
         * explicitly stored. Instead, this method conditionally sets {@link #setReferredReportValidPeriod(PartialOrCompleteTimePeriod)} if
         * {@link #isCancelMessage()} is true. If {@link #isCancelMessage()} is false, stores the potential cancel report valid time temporarily in the Builder
         * to be set if {@link #setReportStatus(ReportStatus)} is later called with parameter
         * {@link fi.fmi.avi.model.AviationCodeListUser.TAFStatus#CANCELLATION}.
         * <p>
         * Also calls {@link #setAerodrome(Aerodrome)} with the provided aerodrome info if no aerodrome is set.
         *
         * @param referredReport the reference to the amended message
         * @return the builder
         * @throws IllegalArgumentException if the {@link TAFReference#getAerodrome()} does not equal {@link #getAerodrome()} aerodrome
         * @deprecated please migrate into using {@link #setReferredReportValidPeriod(PartialOrCompleteTimePeriod)} instead
         */
        @Deprecated
        public Builder setReferredReport(final TAFReference referredReport) throws IllegalArgumentException {
            try {
                if (!getAerodrome().equals(referredReport.getAerodrome())) {
                    throw new IllegalArgumentException(
                            "Aerodrome " + getAerodrome() + " set for TAF is not the same as the aerodrome set for the referred " + "report "
                                    + referredReport.getAerodrome());
                }
            } catch (final IllegalStateException ise) {
                //Aerodrome not set in builder, set it here:
                this.setAerodrome(referredReport.getAerodrome());
            }
            if (isCancelMessage()) {
                this.setReferredReportValidPeriod(referredReport.getValidityTime());
            } else if (referredReport.getValidityTime().isPresent()) {
                this.possibleReferredReportValidPeriod = referredReport.getValidityTime().get();
            }
            return this;
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
         * <p>
         * Note, this method is provided for backward compatibility with previous versions of the API. The <code>referredReport</code> is no longer
         * explicitly stored. This implementation uses {@link #getAerodrome()} and {@link #getReferredReportValidPeriod()} instead to determine the returned
         * value
         * on-the-fly for cancel messages. Returns {@link Optional#empty()} if {@link #isCancelMessage()} is false or {@link #getReferredReportValidPeriod()} is
         * not present.
         *
         * @return the amended message information for cancellation messages
         * @deprecated please migrate to using {@link #getAerodrome()} and {@link #getReferredReportValidPeriod()} instead
         */
        @Deprecated
        public Optional<TAFReference> getReferredReport() {
            final Optional<ReportStatus> status = getReportStatus();
            // getAerodrome() throws IllegalStateException if aerodrome is not yet set:
            try {
                if (getReferredReportValidPeriod().isPresent() && (isCancelMessage() || status.isPresent()//
                        && (ReportStatus.AMENDMENT.equals(status.get()) || ReportStatus.CORRECTION.equals(status.get())))) {
                    return Optional.of(TAFReferenceImpl.builder()
                            .setAerodrome(AerodromeImpl.immutableCopyOf(this.getAerodrome()))
                            .setValidityTime(getReferredReportValidPeriod())
                            .build());
                } else {
                    return Optional.empty();
                }
            } catch (final IllegalStateException ise) {
                return Optional.empty();
            }
        }

        /**
         * Sets the time period of the cancelled previously issued message.
         *
         * @param period valid time period of the cancelled report
         * @return the builder
         */
        @Override
        public Builder setReferredReportValidPeriod(final PartialOrCompleteTimePeriod period) {
            super.setReferredReportValidPeriod(period);
            this.possibleReferredReportValidPeriod = null;
            return this;
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
