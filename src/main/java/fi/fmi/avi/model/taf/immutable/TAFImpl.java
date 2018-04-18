package fi.fmi.avi.model.taf.immutable;

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

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.immutable.AerodromeImpl;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.TAFAirTemperatureForecast;
import fi.fmi.avi.model.taf.TAFChangeForecast;

/**
 * Created by rinne on 18/04/2018.
 */
@FreeBuilder
@JsonDeserialize(builder = TAFImpl.Builder.class)
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

    abstract Builder toBuilder();

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
                List<PartialOrCompleteTimePeriod> list = oldFcts.stream().map(TAFChangeForecast::getValidityTime).collect(Collectors.toList());
                list = PartialOrCompleteTimePeriod.completePartialTimeReferenceList(list, approximateIssueTime);
                List<TAFChangeForecast> newFcts = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    newFcts.add(TAFChangeForecastImpl.Builder.from(oldFcts.get(i)).setValidityTime(list.get(i)).build());
                }
                retval = retval.setChangeForecasts(newFcts);
            }
            return retval;
        }

        public Builder withCompleteIssueTime(final YearMonth yearMonth) throws IllegalArgumentException {
            return mutateIssueTime((input) -> input.completedWithIssueYearMonth(yearMonth));
        }
    }
}
