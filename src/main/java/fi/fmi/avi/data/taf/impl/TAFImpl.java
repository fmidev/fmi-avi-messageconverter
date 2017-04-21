package fi.fmi.avi.data.taf.impl;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.data.taf.TAF;
import fi.fmi.avi.data.taf.TAFBaseForecast;
import fi.fmi.avi.data.taf.TAFChangeForecast;

/**
 * Created by rinne on 30/01/15.
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class TAFImpl implements TAF {

    private TAFStatus status;
    private int issueDayOfMonth = -1;
    private int issueHour = -1;
    private int issueMinute = -1;
    private String timeZone;
    private String aerodromeDesignator;
    private int validityStartDayOfMonth = -1;
    private int validityStartHour = -1;
    private int validityEndDayOfMonth = -1;
    private int validityEndHour = -1;
    private TAFBaseForecast baseForecast;
    private List<TAFChangeForecast> changeForecasts;
    private String previousReportAerodromeDesignator;
    private int previousReportValidityStartDayOfMonth = -1;
    private int previousReportValidityStartHour = -1;
    private int previousReportValidityEndDayOfMonth = -1;
    private int previousReportValidityEndHour = -1;

    public TAFImpl() {
    }

    public TAFImpl(final TAF input) {
        this.status = input.getStatus();
        this.issueDayOfMonth = input.getIssueDayOfMonth();
        this.issueHour = input.getIssueHour();
        this.issueMinute = input.getIssueMinute();
        this.timeZone = input.getIssueTimeZone();
        this.aerodromeDesignator = input.getAerodromeDesignator();
        this.validityStartDayOfMonth = input.getValidityStartDayOfMonth();
        this.validityStartHour = input.getValidityStartHour();
        this.validityEndDayOfMonth = input.getValidityEndDayOfMonth();
        this.validityEndHour = input.getValidityEndHour();
        this.baseForecast = new TAFBaseForecastImpl(input.getBaseForecast());
        this.changeForecasts = new ArrayList<TAFChangeForecast>();
        for (TAFChangeForecast fct : input.getChangeForecasts()) {
            this.changeForecasts.add(new TAFChangeForecastImpl(fct));
        }
        this.previousReportAerodromeDesignator = input.getPreviousReportAerodromeDesignator();
        this.previousReportValidityStartDayOfMonth = input.getPreviousReportValidityStartDayOfMonth();
        this.previousReportValidityStartHour = input.getPreviousReportValidityStartHour();
        this.previousReportValidityEndDayOfMonth = input.getPreviousReportValidityEndDayOfMonth();
        this.previousReportValidityEndHour = input.getPreviousReportValidityEndHour();
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
    public int getIssueDayOfMonth() {
        return issueDayOfMonth;
    }

    @Override
    public void setIssueDayOfMonth(final int issueDayOfMonth) {
        this.issueDayOfMonth = issueDayOfMonth;
    }

    @Override
    public int getIssueHour() {
        return issueHour;
    }

    @Override
    public void setIssueHour(final int issueHour) {
        this.issueHour = issueHour;
    }

    @Override
    public int getIssueMinute() {
        return issueMinute;
    }

    @Override
    public void setIssueMinute(final int issueMinute) {
        this.issueMinute = issueMinute;
    }

    @Override
    public String getIssueTimeZone() {
        return timeZone;
    }

    @Override
    public void setIssueTimeZone(final String timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public String getAerodromeDesignator() {
        return aerodromeDesignator;
    }

    @Override
    public void setAerodromeDesignator(final String aerodromeDesignator) {
        this.aerodromeDesignator = aerodromeDesignator;
    }

    @Override
    public int getValidityStartDayOfMonth() {
        return validityStartDayOfMonth;
    }

    @Override
    public void setValidityStartDayOfMonth(final int validityStartDayOfMonth) {
        this.validityStartDayOfMonth = validityStartDayOfMonth;
    }

    @Override
    public int getValidityStartHour() {
        return validityStartHour;
    }

    @Override
    public void setValidityStartHour(final int validityStartHour) {
        this.validityStartHour = validityStartHour;
    }

    @Override
    public int getValidityEndDayOfMonth() {
        return validityEndDayOfMonth;
    }

    @Override
    public void setValidityEndDayOfMonth(final int validityEndDayOfMonth) {
        this.validityEndDayOfMonth = validityEndDayOfMonth;
    }

    @Override
    public int getValidityEndHour() {
        return validityEndHour;
    }

    @Override
    public void setValidityEndHour(final int validityEndHour) {
        this.validityEndHour = validityEndHour;
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
    public String getPreviousReportAerodromeDesignator() {
        return previousReportAerodromeDesignator;
    }

    @Override
    public void setPreviousReportAerodromeDesignator(final String previousReportAerodromeDesignator) {
        this.previousReportAerodromeDesignator = previousReportAerodromeDesignator;
    }

    @Override
    public int getPreviousReportValidityStartDayOfMonth() {
        return previousReportValidityStartDayOfMonth;
    }

    @Override
    public void setPreviousReportValidityStartDayOfMonth(final int previousReportValidityStartDayOfMonth) {
        this.previousReportValidityStartDayOfMonth = previousReportValidityStartDayOfMonth;
    }

    @Override
    public int getPreviousReportValidityStartHour() {
        return previousReportValidityStartHour;
    }

    @Override
    public void setPreviousReportValidityStartHour(final int previousReportValidityStartHour) {
        this.previousReportValidityStartHour = previousReportValidityStartHour;
    }

    @Override
    public int getPreviousReportValidityEndDayOfMonth() {
        return previousReportValidityEndDayOfMonth;
    }

    @Override
    public void setPreviousReportValidityEndDayOfMonth(final int previousReportValidityEndDayOfMonth) {
        this.previousReportValidityEndDayOfMonth = previousReportValidityEndDayOfMonth;
    }

    @Override
    public int getPreviousReportValidityEndHour() {
        return previousReportValidityEndHour;
    }

    @Override
    public void setPreviousReportValidityEndHour(final int previousReportValidityEndHour) {
        this.previousReportValidityEndHour = previousReportValidityEndHour;
    }
}
