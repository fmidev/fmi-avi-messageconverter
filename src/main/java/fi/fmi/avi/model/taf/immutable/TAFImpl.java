package fi.fmi.avi.model.taf.immutable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.Aerodrome;
import fi.fmi.avi.model.PartialOrCompleteTime;
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
@JsonPropertyOrder({"status", "aerodrome", "issueTime", "validityTime", "baseForecast", "changeForecasts",
        "referredReport", "remarks", "permissibleUsage", "permissibleUsageReason", "permissibleUsageSupplementary",
        "translated", "translatedBulletinID", "translatedBulletinReceptionTime", "translationCentreDesignator",
        "translationCentreName", "translationTime", "translatedTAC"})
public abstract class TAFImpl implements TAF, Serializable {

    public static TAFImpl immutableCopyOf(final TAF taf) {
        checkNotNull(taf);
        if (taf instanceof TAFImpl) {
            return (TAFImpl) taf;
        } else {
            return Builder.from(taf).build();
        }
    }

    public static Optional<TAFImpl> immutableCopyOf(final Optional<TAF> taf) {
        checkNotNull(taf);
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
                for (TAFAirTemperatureForecast airTemp:airTemps) {
                    PartialOrCompleteTimeInstant minTime = airTemp.getMinTemperatureTime();
                    PartialOrCompleteTimeInstant maxTime = airTemp.getMaxTemperatureTime();
                    if (!minTime.getCompleteTime().isPresent() || !maxTime.getCompleteTime().isPresent()) {
                        return false;
                    }
                }
            }
        }

        if (this.getChangeForecasts().isPresent()) {
            for (TAFChangeForecast changeForecast:this.getChangeForecasts().get()) {
                if (!changeForecast.getPeriodOfChange().isComplete()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    @JsonIgnore
    public boolean allAerodromeReferencesContainPositionAndElevation() {
        return this.getAerodrome().getFieldElevationValue().isPresent()
                && this.getAerodrome().getReferencePoint().isPresent();
    }

    public static class Builder extends TAFImpl_Builder {

        public static Builder from(final TAF value) {
            //From AviationWeatherMessage:
            Builder retval = new Builder().setIssueTime(value.getIssueTime())
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

        public Builder() {
            setStatus(TAFStatus.NORMAL);
            setTranslated(false);
        }
        public Builder withCompleteForecastTimes(final YearMonth issueYearMonth, int issueDay, int issueHour, final ZoneId tz) throws IllegalArgumentException {
            final ZonedDateTime approximateIssueTime = ZonedDateTime.of(
                    LocalDateTime.of(issueYearMonth.getYear(), issueYearMonth.getMonth(), issueDay, issueHour, 0), tz);
            Builder retval = this;

            if (getValidityTime().isPresent()) {
                retval = retval.mapValidityTime(vTime -> PartialOrCompleteTimePeriod.completePartialTimeReference(vTime, approximateIssueTime));
            }

            if (getBaseForecast().isPresent() && getBaseForecast().get().getTemperatures().isPresent()) {
                List<TAFAirTemperatureForecast> newTemps = new ArrayList<>();
                for (final TAFAirTemperatureForecast airTemp : getBaseForecast().get().getTemperatures().get()) {
                    newTemps.add(TAFAirTemperatureForecastImpl.Builder.from(airTemp)
                            .mutateMinTemperatureTime(time -> time.completedWithIssueYearMonthDay(issueYearMonth, issueDay).build())
                            .mutateMaxTemperatureTime(time -> time.completedWithIssueYearMonthDay(issueYearMonth, issueDay).build())
                            .build());
                }
                retval = retval.mapBaseForecast(fct -> TAFBaseForecastImpl.Builder.from(fct).setTemperatures(newTemps).build());

            }
            if (getChangeForecasts().isPresent() && !getChangeForecasts().get().isEmpty()) {
                List<TAFChangeForecast> oldFcts = getChangeForecasts().get();
                List<PartialOrCompleteTime> list = oldFcts.stream().map(TAFChangeForecast::getPeriodOfChange).collect(Collectors.toList());
                list = PartialOrCompleteTimePeriod.completePartialTimeReferenceList(list, approximateIssueTime);
                List<TAFChangeForecast> newFcts = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    PartialOrCompleteTime time = list.get(i);
                    newFcts.add(TAFChangeForecastImpl.Builder.from(oldFcts.get(i)).setPeriodOfChange((PartialOrCompleteTimePeriod) time).build());
                }
                retval = retval.setChangeForecasts(newFcts);
            }

            if (getReferredReport().isPresent()) {
                TAFReferenceImpl.Builder builder = TAFReferenceImpl.immutableCopyOf(getReferredReport().get()).toBuilder();
                PartialOrCompleteTimePeriod referredValidity = getReferredReport().get().getValidityTime();
                builder.setValidityTime(PartialOrCompleteTimePeriod.completePartialTimeReferenceBackwards(referredValidity,approximateIssueTime));
                setReferredReport(builder.build());
            }
            return retval;
        }

        public Builder withCompleteIssueTime(final YearMonth yearMonth) throws IllegalArgumentException {
            return mutateIssueTime((input) -> input.completedWithIssueYearMonth(yearMonth));
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
            return super.setReferredReport(referredReport);
        }
    }
}
