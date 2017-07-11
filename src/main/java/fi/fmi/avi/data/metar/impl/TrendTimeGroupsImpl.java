package fi.fmi.avi.data.metar.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.fasterxml.jackson.annotation.JsonInclude;

import fi.fmi.avi.data.metar.TrendTimeGroups;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class TrendTimeGroupsImpl implements TrendTimeGroups {
	private Pattern HOUR_MINUTE_PATTERN = Pattern.compile("([0-9]{2})([0-9]{2})?");
	
    private int startHour = -1;
    private int startMinute = -1;
    private int endHour = -1;
    private int endMinute = -1;
    private boolean endHourIs24 = false;
    private ZonedDateTime from;
    private ZonedDateTime to;
    
    private boolean isSingular = false;

    public TrendTimeGroupsImpl() {
    }

    public TrendTimeGroupsImpl(final TrendTimeGroups input) {
    	if (input.getStartTime() != null) {
    		this.setStartTime(input.getStartTime());
    	} else {
    		this.setPartialStartTime(input.getPartialStartTime());
    	}
    	if (input.getEndTime() != null) {
    		this.setEndTime(input.getEndTime());
    	} else {
    		this.setPartialEndTime(input.getPartialEndTime());
    	}
        this.isSingular = input.isSingleInstance();
    }
    
    public String getPartialStartTime() {
    	if (this.startHour > -1 && this.startMinute > -1){
    		return String.format("%02d%02d", this.startHour, this.startMinute);
    	} else {
    		return null;
    	}
    }
    
    public int getStartHour() {
    	return this.startHour;
    }
    
    public int getStartMinute() {
    	return this.startMinute;
    }
    
    @Override
	public ZonedDateTime getStartTime() {
		return this.from;
	}
    
    public String getPartialEndTime() {
    	if (this.endHour > -1 && this.endMinute > -1){
    		return String.format("%02d%02d", this.endHour, this.endMinute);
    	} else {
    		return null;
    	}
    }
    
    public int getEndHour() {
    	return this.endHour;
    }
    
    public int getEndMinute() {
    	return this.endMinute;
    }
    
    @Override
	public ZonedDateTime getEndTime() {
		return this.to;
	}
    
    @Override
    public boolean isSingleInstance() {
        return isSingular;
    }
    
    
    @Override
	public void amendTimeReferences(final ZonedDateTime issueTime) {
		 if (issueTime != null) {
			if (this.hasStartTime()) {
				if (this.getStartHour() < issueTime.getHour()) {
					//assume the next day from issue
					ZonedDateTime oneDayAfterIssue = issueTime.plusDays(1);
					this.setStartTime(
							ZonedDateTime.of(
									LocalDateTime.of(oneDayAfterIssue.getYear(), oneDayAfterIssue.getMonth(), oneDayAfterIssue.getDayOfMonth(), this.getStartHour(), this.getStartMinute()),
									oneDayAfterIssue.getZone()));
				} else {
					//assume same day as issue
					this.setStartTime(
							ZonedDateTime.of(
									LocalDateTime.of(issueTime.getYear(), issueTime.getMonth(), issueTime.getDayOfMonth(), this.getStartHour(), this.getStartMinute()),
									issueTime.getZone()));
				}
				
				if (this.hasEndTime()) {
					//both start and end given, the end may be on the same day as start or on the next day:
					if (this.getEndHour() < this.getStartHour()) {
						//assume the next day from start
						ZonedDateTime oneDayAfterStart = this.getStartTime().plusDays(1);
						this.setEndTime(ZonedDateTime.of(
								LocalDateTime.of(oneDayAfterStart.getYear(), oneDayAfterStart.getMonth(), oneDayAfterStart.getDayOfMonth(), this.getEndHour(), this.getEndMinute()),
								oneDayAfterStart.getZone()));
					} else {
						if (this.endHourIs24) {
							//this is actually the 00 of the next day
							this.setEndTime(
									ZonedDateTime.of(
											LocalDateTime.of(from.getYear(), from.getMonth(), from.getDayOfMonth(), 0, 0), 
											from.getZone()).plusDays(1));
							
						} else {
							//assume same day as start
							this.setEndTime(
									ZonedDateTime.of(
											LocalDateTime.of(from.getYear(), from.getMonth(), from.getDayOfMonth(), this.getEndHour(), this.getEndMinute()),
											from.getZone()));
						}
						
					}
				}
			} else {
				//no start, just check "to" based on the issue time:
				if (this.hasEndTime()) {
					if (this.getEndHour() < issueTime.getHour()) {
						//assume the next day from issue
						ZonedDateTime oneDayAfterIssue = issueTime.plusDays(1);
						this.setEndTime(
								ZonedDateTime.of(
										LocalDateTime.of(oneDayAfterIssue.getYear(), oneDayAfterIssue.getMonth(), oneDayAfterIssue.getDayOfMonth(), this.getEndHour(), this.getEndMinute()),
										oneDayAfterIssue.getZone()));
					} else {
						if (this.endHourIs24) {
							//this is actually the 00 of the next day
							this.setEndTime(
									ZonedDateTime.of(
											LocalDateTime.of(issueTime.getYear(), issueTime.getMonth(), issueTime.getDayOfMonth(), this.getEndHour() - 1, 59),
											issueTime.getZone()).plusMinutes(1));
						} else {
							//assume same day as issue
							this.setEndTime(
									ZonedDateTime.of(
											LocalDateTime.of(issueTime.getYear(), issueTime.getMonth(), issueTime.getDayOfMonth(), this.getEndHour(), this.getEndMinute()),
											issueTime.getZone()));
						}
					}
				}
			}
		}	
	}

	@Override
	public boolean areTimeReferencesResolved() {
		boolean retval = true;
		if (this.hasStartTime() && this.getStartTime() == null) {
			retval = false;
		}
		if (this.hasEndTime() && this.getEndTime() == null) {
			retval = false;
		}
		return retval;
	}
	
    public void setPartialStartTime(final String time) {
    	if (time == null) {
    		this.startHour = -1;
    		this.startMinute = -1;
    		this.from = null;
    	} else {
    		Matcher m = HOUR_MINUTE_PATTERN.matcher(time);
    		if (m.matches()) {
    			int hour = Integer.parseInt(m.group(1));
    			int minute = Integer.parseInt(m.group(2));
    			if (timeOk(hour, minute)) {
    				this.startHour = hour;
    				this.startMinute = minute;
    			} else {
    				throw new IllegalArgumentException("Invalid hour and/or minute values in '" + time + "'");
    			}
    			this.from = null;
    		} else {
    			throw new IllegalArgumentException("Time '" + time + "' is not in format 'HHmm'");
    		}
    	}
    }
    
    @Override
    public void setPartialStartTime(final int hour, final int minute) {
    	if (timeOk(hour, minute)) {
    		 this.startHour = hour;
    	     this.startMinute = minute;
    	     this.from = null;
    	} else {
    		throw new IllegalArgumentException("Invalid hour and/or minute values");
    	}
    }
    
    @Override
	public void setStartTime(int year, int monthOfYear, int dayOfMonth, int hour, int minute, ZoneId timeZone) {
		this.setStartTime(ZonedDateTime.of(LocalDateTime.of(year, monthOfYear, dayOfMonth, hour, minute), timeZone));
	}

	@Override
	public void setStartTime(ZonedDateTime time) {
		this.from = time;
		this.startHour = this.from.getHour();
		this.startMinute = this.from.getMinute();
	}
	
	
	public void setPartialEndTime(final String time) {
    	if (time == null) {
    		this.endHour = -1;
    		this.endMinute = -1;
    		this.to = null;
    	} else {
    		Matcher m = HOUR_MINUTE_PATTERN.matcher(time);
    		if (m.matches()) {
    			this.endHour = Integer.parseInt(m.group(1));
    			this.endMinute = Integer.parseInt(m.group(2));
    			if (this.endHour == 24 && this.endMinute == 0) {
	        		this.endHourIs24 = true;
	    		} else {
	    			this.endHourIs24 = false;
	    		}
    			this.to = null;
    		} else {
    			throw new IllegalArgumentException("Time '" + time + "' is not in format 'HHmm'");
    		}
    	}
    }
	
	@Override
    public void setPartialEndTime(final int hour, final int minute) {
        if (timeOk(hour,minute)) {
        	this.endHour = hour;
        	this.endMinute = minute;
        	if (this.endHour == 24 && this.endMinute == 0) {
        		this.endHourIs24 = true;
    		} else {
    			this.endHourIs24 = false;
    		}
        	this.to = null;
        } else {
        	throw new IllegalArgumentException("Invalid hour and/or minute values");
        }
    }
	
	@Override
	public void setEndTime(int year, int monthOfYear, int dayOfMonth, int hour, int minute, ZoneId timeZone) {
		this.setEndTime(ZonedDateTime.of(LocalDateTime.of(year, monthOfYear, dayOfMonth, hour, minute), timeZone));
	}
	
	@Override
	public void setEndTime(ZonedDateTime time) {
		this.to = time;
		int hour = time.getHour();
		int minute = time.getMinute();
		if (hour == 0 && minute == 0) {
			this.endHour = 24;
			this.endHourIs24 = true;
		} else {
			this.endHour = this.to.getHour();
			this.endHourIs24 = false;
		}
		this.endMinute = minute;
	}
	
	@Override
    public void setSingleInstance(final boolean singular) {
        this.isSingular = singular;
    }
	
	@Override
    public boolean hasStartTime() {
        return this.startHour >= 0 && this.startMinute >= 0;
    }
	
	@Override
    public boolean hasEndTime() {
        return this.endHour >= 0 && this.endMinute >= 0;
    }
	
	 private boolean timeOk(final int hour, final int minute) {
		if (hour > 24) {
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
