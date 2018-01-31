package fi.fmi.avi.model.taf;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import fi.fmi.avi.model.Aerodrome;

/**
 * Created by rinne on 22/11/17.
 */
public class TAFReference implements Serializable {

    private static final long serialVersionUID = 3614074296244140508L;

    private Aerodrome aerodrome;
    private ZonedDateTime issueTime;
    private ZonedDateTime validTimeStart;
    private ZonedDateTime validTimeEnd;
    private TAF.TAFStatus status;

    public TAFReference(final TAFReference source) {
        this.aerodrome = new Aerodrome(source.getAerodrome());
        this.issueTime = source.getIssueTime();
        this.validTimeStart = source.getValidityStartTime();
        this.validTimeEnd = source.getValidityEndTime();
        this.status = source.getStatus();
    }

    public Aerodrome getAerodrome() {
        return aerodrome;
    }

    public void setAerodrome(final Aerodrome aerodrome) {
        this.aerodrome = aerodrome;
    }

    @JsonIgnore
    public ZonedDateTime getIssueTime() {
        return issueTime;
    }

    @JsonIgnore
    public void setIssueTime(final ZonedDateTime issueTime) {
        this.issueTime = issueTime;
    }

    @JsonProperty("issueTime")
    public String getIssueTimeISO() {
        if (this.issueTime != null) {
            return this.issueTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } else {
            return null;
        }
    }

    @JsonProperty("issueTime")
    public void setIssueTimeISO(final String time) {
        this.issueTime = ZonedDateTime.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(time));
    }

    @JsonIgnore
    public ZonedDateTime getValidityStartTime() {
        return validTimeStart;
    }

    @JsonIgnore
    public void setValidityStartTime(final ZonedDateTime validTimeStart) {
        this.validTimeStart = validTimeStart;
    }

    @JsonProperty("validityStartTime")
    public String getValidityStartTimeISO() {
        if (this.validTimeStart != null) {
            return this.validTimeStart.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } else {
            return null;
        }
    }

    @JsonProperty("validityStartTime")
    public void setValidityStartTimeISO(final String time) {
        this.validTimeStart = ZonedDateTime.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(time));
    }

    @JsonIgnore
    public ZonedDateTime getValidityEndTime() {
        return validTimeEnd;
    }

    @JsonIgnore
    public void setValidityEndTime(final ZonedDateTime validTimeEnd) {
        this.validTimeEnd = validTimeEnd;
    }

    @JsonProperty("validityEndTime")
    public String getValidityEndTimeISO() {
        if (this.validTimeStart != null) {
            return this.validTimeStart.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } else {
            return null;
        }
    }

    @JsonProperty("validityEndTime")
    public void setValidityEndTimeISO(final String time) {
        this.validTimeEnd = ZonedDateTime.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(time));
    }

    public TAF.TAFStatus getStatus() {
        return status;
    }

    public void setStatus(final TAF.TAFStatus status) {
        this.status = status;
    }
}
