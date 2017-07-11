package fi.fmi.avi.data.impl;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fi.fmi.avi.data.AviationWeatherMessage;
import fi.fmi.avi.data.Weather;

/**
 * Created by rinne on 05/06/17.
 */
public abstract class AviationWeatherMessageImpl implements AviationWeatherMessage {

    public static List<String> getAsWeatherCodes(final List<Weather> weatherList) {
        return getAsWeatherCodes(weatherList, null);
    }

    public static List<String> getAsWeatherCodes(final List<Weather> weatherList, final String prefix) {
        List<String> retval = null;
        if (weatherList != null) {
            retval = new ArrayList<>(weatherList.size());
            for (final Weather w : weatherList) {
                if (prefix != null) {
                    retval.add(prefix + w.getCode());
                } else {
                    retval.add(w.getCode());
                }
            }
        }
        return retval;
    }
    
    private static final Pattern DAY_HOUR_MINUTE_PATTERN = Pattern.compile("([0-9]{2})([0-9]{2})([0-9]{2})([A-Z]*)");
    
    private int issueDayOfMonth = -1;
    private int issueHour = -1;
    private int issueMinute = -1;
    
    private ZoneId timeZone;
    private ZonedDateTime fullyResolvedIssueTime;
    
    private List<String> remarks;

    public AviationWeatherMessageImpl() {
    }

    public AviationWeatherMessageImpl(AviationWeatherMessage input) {
    	if (input.getIssueTime() != null) {
    		this.setIssueTime(input.getIssueTime());
    	} else {
    		this.fullyResolvedIssueTime = null;
    		this.setPartialIssueTime(input.getPartialIssueTime());
    	}
        this.remarks = input.getRemarks();
    }
    
    @Override
    public void setPartialIssueTime(final String time) {
    	if (time == null) {
    		this.issueDayOfMonth = -1;
    		this.issueHour = -1;
    		this.issueMinute = -1;
    		this.timeZone = null;
    	} else {
	    	Matcher m = DAY_HOUR_MINUTE_PATTERN.matcher(time);
	    	if (m.matches()) {
	    		int day = Integer.parseInt(m.group(1));
	    		int hour = Integer.parseInt(m.group(2));
	    		int minute = Integer.parseInt(m.group(3));
	    		if (timeOk(day, hour, minute)) {
	    			this.issueDayOfMonth = day;
	    			this.issueHour = hour;
	    			this.issueMinute = minute;
	    		} else {
	    			throw new IllegalArgumentException("Invalid day, hour and/or minute values in '" + time + "'");
	    		}
	    		try {
	    			this.timeZone = ZoneId.of(m.group(4));
	    		} catch (DateTimeException dte) {
	    			throw new IllegalArgumentException("Time zone id '" + m.group(4) + "' is unknown");
	    		}
	    	} else {
	    		throw new IllegalArgumentException("Time '" + time + "' is not in format 'ddHHmmz'");
	    	}
    	}
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.METAR#setIssueTime()
     */
    @Override
    public void setPartialIssueTime(final int dayOfMonth, final int hour, final int minute) {
        this.setPartialIssueTime(dayOfMonth, hour, minute, ZoneId.of("UTC"));
    }
    
    /* (non-Javadoc)
     * @see fi.fmi.avi.data.METAR#setIssueTime(int, int, int, String)
     */
    @Override
    public void setPartialIssueTime(final int dayOfMonth, final int hour, final int minute, final ZoneId timeZoneID) {
    	if (timeOk(dayOfMonth, hour, minute)) {
    		this.issueDayOfMonth = dayOfMonth;
    		this.issueHour = hour;
    		this.issueMinute = minute;
    		this.timeZone = timeZoneID;
            this.fullyResolvedIssueTime = null;
    	} else {
    		throw new IllegalArgumentException("Invalid day, hour and/or minute values");
    	}
    }
    
    @Override
	public void setIssueTime(int year, int monthOfYear, int dayOfMonth, int hour, int minute, ZoneId timeZoneID) {
		this.setIssueTime(ZonedDateTime.of(LocalDateTime.of(year, monthOfYear, dayOfMonth, hour, minute), timeZoneID));
	}
    
    @Override
    public void setIssueTime(final ZonedDateTime issueTime) {
    	this.fullyResolvedIssueTime = issueTime;
		this.issueDayOfMonth = this.fullyResolvedIssueTime.getDayOfMonth();
		this.issueHour = this.fullyResolvedIssueTime.getHour();
		this.issueMinute = this.fullyResolvedIssueTime.getMinute();
		this.timeZone = issueTime.getZone();
    }

    @Override
    public String getPartialIssueTime() {
    	if (this.issueDayOfMonth > -1 && this.issueHour > -1 && this.issueMinute > -1 && this.timeZone != null) {
			return String.format("%02d%02d%02d%s", this.issueDayOfMonth, this.issueHour, this.issueMinute, this.timeZone);
		} else {
			return null;
		}
    }
    
    @Override
    public ZonedDateTime getIssueTime() {
        return this.fullyResolvedIssueTime;
    }

    @Override
    public List<String> getRemarks() {
        return this.remarks;
    }

    @Override
    public void setRemarks(final List<String> remarks) {
        this.remarks = remarks;
    }
    
    @Override
    public void amendTimeReferences(final ZonedDateTime referenceTime) {
    	try {
    		this.setIssueTime(ZonedDateTime.of(LocalDateTime.of(referenceTime.getYear(), referenceTime.getMonth(), this.issueDayOfMonth, this.issueHour, this.issueMinute), this.timeZone));
    	} catch (DateTimeException dte) {
    		throw new IllegalArgumentException("Issue time with day of month '" + this.issueDayOfMonth + "' cannot be amended with month '" + referenceTime.getMonth() + "'", dte);
    	}
    }
    
    @Override
    public boolean areTimeReferencesResolved() {
    	return this.fullyResolvedIssueTime != null;
    }
    
    private boolean timeOk(final int day, final int hour, final int minute) {
    	if (day > 31) {
			return false;
    	}
		if (hour > 23) {
			return false;
		}
		if (minute > 59) {
			return false;
		} else if (hour == 24 && minute != 0) {
			return false;
		}
		return true;
    }

}
