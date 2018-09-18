package fi.fmi.avi.model.taf.immutable;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.Aerodrome;
import fi.fmi.avi.model.PartialOrCompleteTime;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
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
@JsonPropertyOrder({ "status", "aerodrome", "issueTime", "validityTime", "baseForecast", "changeForecasts", "referredReport", "remarks", "permissibleUsage",
        "permissibleUsageReason", "permissibleUsageSupplementary", "translated", "translatedBulletinID", "translatedBulletinReceptionTime",
        "translationCentreDesignator", "translationCentreName", "translationTime", "translatedTAC" })
public abstract class TAFImpl implements TAF, Serializable {

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
        if (!this.getIssueTime().getCompleteTime().isPresent()) {
            return false;
        }
        if (this.getValidityTime().isPresent()) {
            if (!this.getValidityTime().get().isComplete()) {
                return false;
            }
        }
        if (this.getBaseForecast().isPresent()) {
            if (this.getBaseForecast().get().getTemperatures().isPresent()) {
                List<TAFAirTemperatureForecast> airTemps = this.getBaseForecast().get().getTemperatures().get();
                for (TAFAirTemperatureForecast airTemp : airTemps) {
                    PartialOrCompleteTimeInstant minTime = airTemp.getMinTemperatureTime();
                    PartialOrCompleteTimeInstant maxTime = airTemp.getMaxTemperatureTime();
                    if (!minTime.getCompleteTime().isPresent() || !maxTime.getCompleteTime().isPresent()) {
                        return false;
                    }
                }
            }
        }

        if (this.getChangeForecasts().isPresent()) {
            for (TAFChangeForecast changeForecast : this.getChangeForecasts().get()) {
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

        public Builder() {
            setStatus(TAFStatus.NORMAL);
            setTranslated(false);
        }

        public static Builder from(final TAF value) {
            if (value instanceof TAFImpl) {
                return ((TAFImpl) value).toBuilder();
            } else {
                //From AviationWeatherMessage:
                Builder retval = new Builder()//
                        .setIssueTime(value.getIssueTime())
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
                retval.setAerodrome(AerodromeImpl.immutableCopyOf(value.getAerodrome()));

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
            return mutateIssueTime((input) -> input.completePartialAt(yearMonth));
        }

        public Builder withCompleteIssueTimeNear(final ZonedDateTime reference) {
            return mutateIssueTime((input) -> input.completePartialNear(reference));
        }

        public Builder withCompleteForecastTimes(final YearMonth issueYearMonth, final int issueDay, final int issueHour, final ZoneId tz) {
            return withCompleteForecastTimes(
                    ZonedDateTime.of(LocalDateTime.of(issueYearMonth.getYear(), issueYearMonth.getMonth(), issueDay, issueHour, 0), tz));
        }

        public Builder withCompleteForecastTimes(final ZonedDateTime reference) {
            requireNonNull(reference, "reference");
            return completeValidityTime(reference)//
                    .completeAirTemperatureForecast(reference)//
                    .completeChangeForecastPeriods(reference)//
                    .completeReferredReport(reference);
        }

        private Builder completeValidityTime(final ZonedDateTime reference) {
            return mapValidityTime(validityTime -> validityTime.toBuilder().completePartialStartingNear(reference).build());
        }

        private Builder completeAirTemperatureForecast(final ZonedDateTime reference) {
            if (getBaseForecast().isPresent() && getBaseForecast().get().getTemperatures().isPresent()) {
                final List<TAFAirTemperatureForecast> temperatureForecasts = new ArrayList<>();
                for (final TAFAirTemperatureForecast airTemp : getBaseForecast().get().getTemperatures().get()) {
                    temperatureForecasts.add(TAFAirTemperatureForecastImpl.Builder.from(airTemp)
                            .mutateMinTemperatureTime(time -> time.completePartialNear(reference).build())
                            .mutateMaxTemperatureTime(time -> time.completePartialNear(reference).build())
                            .build());
                }
                mapBaseForecast(fct -> TAFBaseForecastImpl.Builder.from(fct).setTemperatures(Collections.unmodifiableList(temperatureForecasts)).build());
            }
            return this;
        }

        private Builder completeChangeForecastPeriods(final ZonedDateTime reference) {
            if (getChangeForecasts().isPresent() && !getChangeForecasts().get().isEmpty()) {
                final List<TAFChangeForecast> changeForecasts = getChangeForecasts().get();
                final List<PartialOrCompleteTime> times = PartialOrCompleteTimePeriod.completeAscendingPartialTimes(
                        (Iterable<PartialOrCompleteTimePeriod>) changeForecasts.stream().map(TAFChangeForecast::getPeriodOfChange)::iterator, reference);
                final List<TAFChangeForecast> completedForecasts = new ArrayList<>();
                for (int i = 0; i < times.size(); i++) {
                    final PartialOrCompleteTime time = times.get(i);
                    completedForecasts.add(
                            TAFChangeForecastImpl.Builder.from(changeForecasts.get(i)).setPeriodOfChange((PartialOrCompleteTimePeriod) time).build());
                }
                setChangeForecasts(Collections.unmodifiableList(completedForecasts));
            }
            return this;
        }

        private Builder completeReferredReport(final ZonedDateTime reference) {
            return mapReferredReport(referredReport -> TAFReferenceImpl.Builder.from(referredReport)//
                    .mutateValidityTime(builder -> builder.completePartialEndingNear(reference))//
                    .build());
        }

        public Builder withAllTimesComplete(final ZonedDateTime reference) {
            requireNonNull(reference, "reference");
            return withCompleteIssueTimeNear(reference)//
                    .withCompleteForecastTimes(reference);
        }

        @Override
        @JsonDeserialize(as = AerodromeImpl.class)
        public Builder setAerodrome(final Aerodrome aerodrome) {
            return super.setAerodrome(aerodrome);
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

        @Override
        @JsonDeserialize(as = TAFReferenceImpl.class)
        public Builder setReferredReport(final TAFReference referredReport) {
            Builder retval = super.setReferredReport(referredReport);
            Aerodrome ref = referredReport.getAerodrome();
            try {
                Aerodrome base = this.getAerodrome();
                if (ref != null) {
                    if (base != null) {
                        if (!base.equals(ref)) {
                            throw new IllegalStateException("Aerodrome " + base + " already set for TAF, cannot set referred report with aerodrome " + ref);
                        }
                    }
                    this.setAerodrome(AerodromeImpl.immutableCopyOf(ref));
                }
            } catch (IllegalStateException ise) {
                //No aerodrome set, do not ask
                this.setAerodrome(AerodromeImpl.immutableCopyOf(ref));
            }
            return retval;
        }

    }
}
