package fi.fmi.avi.model.taf.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.AerodromeUpdateEvent;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.impl.AerodromeWeatherMessageImpl;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.TAFAirTemperatureForecast;
import fi.fmi.avi.model.taf.TAFBaseForecast;
import fi.fmi.avi.model.taf.TAFChangeForecast;
import fi.fmi.avi.model.taf.TAFReference;

/**
 * Created by rinne on 30/01/15.
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class TAFImpl extends AerodromeWeatherMessageImpl implements TAF {

    public static final Pattern VALIDITY_PERIOD_PATTERN = Pattern.compile("^(([0-9]{2})([0-9]{2})([0-9]{2}))|(([0-9]{2})([0-9]{2})/([0-9]{2})([0-9]{2}))$");

    private final TAFValidityTime validityTime;
    private TAFStatus status;
    private TAFBaseForecast baseForecast;
    private List<TAFChangeForecast> changeForecasts;
    private TAFReference referredReport;

    public TAFImpl() {
        this.validityTime = new TAFValidityTime();
    }

    public TAFImpl(final TAF input) {
        super(input);
        this.validityTime = new TAFValidityTime();
        if (input != null) {
            this.status = input.getStatus();
            if (input.getValidityStartTime() != null && input.getValidityEndTime() != null) {
                this.setValidityStartTime(input.getValidityStartTime());
                this.setValidityEndTime(input.getValidityEndTime());
            } else {
                this.setPartialValidityTimePeriod(input.getPartialValidityTimePeriod());
            }
            if (input.getBaseForecast() != null) {
                this.baseForecast = new TAFBaseForecastImpl(input.getBaseForecast());
            }
            if (input.getChangeForecasts() != null) {
                this.changeForecasts = new ArrayList<>();
                for (final TAFChangeForecast fct : input.getChangeForecasts()) {
                    this.changeForecasts.add(new TAFChangeForecastImpl(fct));
                }
            }
            if (input.getReferredReport() != null) {
                this.referredReport = new TAFReference(input.getReferredReport());
            }
        }
    }

    @Override
    public TAFStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(final TAFStatus status) {
        this.status = status;
    }

    @Override
    @JsonIgnore
    public int getValidityStartDayOfMonth() {
        return this.validityTime.getStartTimeDay();
    }

    @Override
    @JsonIgnore
    public int getValidityStartHour() {
        return this.validityTime.getStartTimeHour();
    }

    @Override
    @JsonIgnore
    public int getValidityEndDayOfMonth() {
        return this.validityTime.getEndTimeDay();
    }

    @Override
    @JsonIgnore
    public int getValidityEndHour() {
        return this.validityTime.getEndTimeHour();
    }

    @Override
    public TAFBaseForecast getBaseForecast() {
        return baseForecast;
    }

    @Override
    @JsonDeserialize(as = TAFBaseForecastImpl.class)
    public void setBaseForecast(final TAFBaseForecast baseForecast) {
        this.baseForecast = baseForecast;
    }

    @Override
    public List<TAFChangeForecast> getChangeForecasts() {
        return changeForecasts;
    }

    @Override
    @JsonDeserialize(contentAs = TAFChangeForecastImpl.class)
    public void setChangeForecasts(final List<TAFChangeForecast> changeForecasts) {
        this.changeForecasts = changeForecasts;
    }

    @Override
    public TAFReference getReferredReport() {
        return this.referredReport;
    }

    @Override
    public void setReferredReport(final TAFReference referredReport) {
        this.referredReport = referredReport;
    }

    @Override
    @JsonProperty("partialValidityTimePeriod")
    public String getPartialValidityTimePeriod() {
        return this.validityTime.getPartialValidityTimePeriod();
    }

    @Override
    @JsonProperty("partialValidityTimePeriod")
    public void setPartialValidityTimePeriod(final String time) {
        this.validityTime.setPartialValidityTimePeriod(time);
    }

    @Override
    public void setPartialValidityTimePeriod(final int day, final int startHour, final int endHour) {
        this.setPartialValidityTimePeriod(day, -1, startHour, endHour);
    }

    @Override
    public void setPartialValidityTimePeriod(final int startDay, final int endDay, final int startHour, final int endHour) {
        this.validityTime.setPartialValidityTimePeriod(startDay, endDay, startHour, endHour);
    }

    @Override
    public void setValidityStartTime(final int year, final int monthOfYear, final int dayOfMonth, final int hour, final int minute, final ZoneId timeZone) {
        this.setValidityStartTime(ZonedDateTime.of(LocalDateTime.of(year, monthOfYear, dayOfMonth, hour, minute), timeZone));

    }

    @JsonProperty("validityStartTime")
    public String getValidityStartTimeISO() {
        return this.validityTime.getValidityStartTimeISO();
    }

    @JsonProperty("validityStartTime")
    public void setValidityStartTimeISO(final String time) {
        this.validityTime.setCompleteStartTimeAsISOString(time);
    }

    @Override
    @JsonIgnore
    public ZonedDateTime getValidityStartTime() {
        return this.validityTime.getCompleteStartTime();
    }

    @Override
    public void setValidityStartTime(final ZonedDateTime time) {
        this.validityTime.setCompleteStartTime(time);
    }

    @Override
    public void setValidityEndTime(final int year, final int monthOfYear, final int dayOfMonth, final int hour, final int minute, final ZoneId timeZone) {
        this.setValidityEndTime(ZonedDateTime.of(LocalDateTime.of(year, monthOfYear, dayOfMonth, hour, minute), timeZone));

    }

    @JsonProperty("validityEndTime")
    public String getValidityEndTimeISO() {
        return this.validityTime.getValidityEndTimeISO();
    }

    @JsonProperty("validityEndTime")
    public void setValidityEndTimeISO(final String time) {
        this.validityTime.setCompleteEndTimeAsISOString(time);
    }

    @Override
    @JsonIgnore
    public ZonedDateTime getValidityEndTime() {
        return this.validityTime.getCompleteEndTime();
    }

    @Override
    public void setValidityEndTime(final ZonedDateTime time) {
        this.validityTime.setCompleteEndTime(time);
    }

    @Override
    public void completeForecastTimeReferences(final int issueYear, final int issueMonth, final int issueDay, final int issueHour, final ZoneId tz) {
        final ZonedDateTime approximateIssueTime = ZonedDateTime.of(LocalDateTime.of(issueYear, issueMonth, issueDay, issueHour, 0), tz);
        List<PartialOrCompleteTimePeriod> list = new ArrayList<>();
        list.add(this.validityTime);
        completePartialTimeReferenceList(list, approximateIssueTime);

        if (this.baseForecast != null && this.baseForecast.getTemperatures() != null) {
            for (final TAFAirTemperatureForecast airTemp : this.baseForecast.getTemperatures()) {
                airTemp.completeForecastTimeReferences(approximateIssueTime);
            }
        }
        if (this.changeForecasts != null) {
            list = this.changeForecasts.stream().map((fct) -> ((TAFChangeForecastImpl) fct).getValidityTimeInternal()).collect(Collectors.toList());
            completePartialTimeReferenceList(list, approximateIssueTime);
        }
    }

    @Override
    public void uncompleteForecastTimeReferences() {
        this.validityTime.setCompleteStartTime(null);
        this.validityTime.setCompleteEndTime(null);
        if (this.baseForecast != null && this.baseForecast.getTemperatures() != null) {
            for (final TAFAirTemperatureForecast airTemp : this.baseForecast.getTemperatures()) {
                airTemp.resetForecastTimeReferences();
            }
        }
        if (this.changeForecasts != null) {
            for (final TAFChangeForecast fct : this.changeForecasts) {
                fct.setValidityStartTime(null);
                fct.setValidityEndTime(null);
            }
        }
    }

    @Override
    public boolean areForecastTimeReferencesComplete() {
        if (!this.validityTime.isStartTimeComplete()) {
            return false;
        }
        if (!this.validityTime.isEndTimeComplete()) {
            return false;
        }
        if (this.baseForecast != null && this.baseForecast.getTemperatures() != null) {
            for (final TAFAirTemperatureForecast airTemp : this.baseForecast.getTemperatures()) {
                if (!airTemp.areForecastTimeReferencesComplete()) {
                    return false;
                }
            }
        }
        if (this.changeForecasts != null) {
            for (final TAFChangeForecast fct : this.changeForecasts) {
                if (fct.getValidityStartTime() == null) {
                    return false;
                }
                if (fct.getValidityEndTime() == null) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void aerodromeInfoAdded(final AerodromeUpdateEvent e) {
        //NOOP
    }

    @Override
    public void aerodromeInfoRemoved(final AerodromeUpdateEvent e) {
        //NOOP
    }

    @Override
    public void aerodromeInfoChanged(final AerodromeUpdateEvent e) {
        //NOOP
    }

}
