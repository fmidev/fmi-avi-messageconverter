package fi.fmi.avi.data.taf.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonInclude;

import fi.fmi.avi.data.taf.TAFChangeForecast;

/**
 * Created by rinne on 30/01/15.
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class TAFChangeForecastImpl extends TAFForecastImpl implements TAFChangeForecast {

    private static final Pattern VALIDITY_START_PATTERN = Pattern.compile("([0-9]{2})([0-9]{2})([0-9]{2})?");
    private static final Pattern VALIDITY_END_PATTERN = Pattern.compile("([0-9]{2})?([0-9]{2})");
    
	private TAFChangeIndicator changeIndicator;
    private ZonedDateTime validityStartTime = null;
    private int validityStartDayOfMonth = -1;
    private int validityStartHour = -1;
    private int validityStartMinute = -1;
    private ZonedDateTime validityEndTime = null; 
    private int validityEndDayOfMonth = -1;
    private int validityEndHour = -1;

    public TAFChangeForecastImpl(){
    }

    public TAFChangeForecastImpl(final TAFChangeForecast input) {
        super(input);
        this.setChangeIndicator(input.getChangeIndicator());
        if (input.getValidityStartTime() != null) {
        	this.setValidityStartTime(input.getValidityStartTime());
        } else {
        	this.setPartialValidityStartTime(input.getPartialValidityStartTime());
        }
        if (input.getValidityEndTime() != null) {
        	this.setValidityEndTime(input.getValidityEndTime());
        } else {
        	this.setPartialValidityEndTime(input.getPartialValidityEndTime());
        }
    }

    @Override
    public TAFChangeIndicator getChangeIndicator() {
        return changeIndicator;
    }

    @Override
    public void setChangeIndicator(final TAFChangeIndicator changeIndicator) {
        this.changeIndicator = changeIndicator;
    }


    @Override
    public int getValidityStartDayOfMonth() {
        return validityStartDayOfMonth;
    }

    @Override
    public int getValidityStartHour() {
        return validityStartHour;
    }

    @Override
    public int getValidityStartMinute() {
        return validityStartMinute;
    }


    @Override
    public int getValidityEndDayOfMonth() {
        return validityEndDayOfMonth;
    }

    @Override
    public int getValidityEndHour() {
        return validityEndHour;
    }
    
	@Override
	public String getPartialValidityStartTime() {
		if (this.validityStartHour > -1 && this.validityStartDayOfMonth> -1){
			if (this.validityStartMinute > -1) {
				return String.format("%02d%02d%02d", this.validityStartDayOfMonth, this.validityStartHour, this.validityStartMinute);
			} else {
				return String.format("%02d%02d", this.validityStartDayOfMonth, this.validityStartHour);
			}
    	} else {
    		return null;
    	}
	}

	@Override
	public ZonedDateTime getValidityStartTime() {
		return this.validityStartTime;
	}

	@Override
	public String getPartialValidityEndTime() {
		if (this.validityEndHour > -1){
			if (this.validityEndDayOfMonth > -1) {
				return String.format("%02d%02d", this.validityEndDayOfMonth, this.validityEndHour);
			} else {
				return String.format("%02d", this.validityStartHour);
			}
    	} else {
    		return null;
    	}
	}

	@Override
	public ZonedDateTime getValidityEndTime() {
		return this.validityEndTime;
	}

	@Override
	public void amendTimeReferences(ZonedDateTime referenceTime) {
		if (referenceTime != null) {
			if (this.validityStartDayOfMonth > -1 && this.validityStartHour > -1) {
				if (this.validityStartDayOfMonth < referenceTime.getDayOfMonth()) {
					// roll over the next month
					ZonedDateTime t = referenceTime.plusMonths(1);
					this.setValidityStartTime(ZonedDateTime.of(LocalDateTime.of(t.getYear(), t.getMonth(), this.validityStartDayOfMonth, this.validityStartHour, this.validityStartMinute > -1?this.validityStartMinute:0), referenceTime.getZone()));
				} else if (this.validityStartDayOfMonth == referenceTime.getDayOfMonth()) {
					ZonedDateTime t = referenceTime;
					if (this.validityStartHour < referenceTime.getHour()) {
						// roll over to the next day
						t = t.plusDays(1);
					}
					this.setValidityStartTime(ZonedDateTime.of(LocalDateTime.of(t.getYear(), t.getMonth(), t.getDayOfMonth(), this.validityStartHour, this.validityStartMinute > -1?this.validityStartMinute:0), t.getZone()));
				} else {
					// the same month but a later day
					this.setValidityStartTime(ZonedDateTime.of(LocalDateTime.of(referenceTime.getYear(), referenceTime.getMonth(), this.validityStartDayOfMonth, this.validityStartHour, this.validityStartMinute > -1?this.validityStartMinute:0), referenceTime.getZone()));
				}
				
				if (this.validityEndHour > -1) {
					ZonedDateTime t = this.validityStartTime;
					if (this.validityEndDayOfMonth > -1) {
						if (this.validityEndDayOfMonth < this.validityStartDayOfMonth) {
							//roll over to next month
							t = t.plusMonths(1);
							this.setValidityEndTime(ZonedDateTime.of(LocalDateTime.of(t.getYear(), t.getMonth(), this.validityEndDayOfMonth, this.validityEndHour, 0), t.getZone()));
						} else if (this.validityEndDayOfMonth == this.validityStartDayOfMonth) {
							if (this.validityEndHour < this.validityStartHour) {
								//roll over to the next day
								t = t.plusDays(1);
								this.setValidityEndTime(ZonedDateTime.of(LocalDateTime.of(t.getYear(), t.getMonth(), t.getDayOfMonth(), this.validityEndHour, 0), t.getZone()));
							} else if (this.validityEndHour == 24) {
								this.setValidityEndTime(ZonedDateTime.of(LocalDateTime.of(t.getYear(), t.getMonth(), this.validityEndDayOfMonth, 0, 0), t.getZone()).plusDays(1));
							} else {
								this.setValidityEndTime(ZonedDateTime.of(LocalDateTime.of(t.getYear(), t.getMonth(), this.validityEndDayOfMonth, this.validityEndHour, 0), t.getZone()));
							}
						} else {
							// the same month but a later day
							this.setValidityEndTime(ZonedDateTime.of(LocalDateTime.of(t.getYear(), t.getMonth(), this.validityEndDayOfMonth, this.validityEndHour, 0), t.getZone()));
						}
					} else {
						if (this.validityEndHour < this.validityStartHour) {
							//roll over to the next day
							t = t.plusDays(1);
							this.setValidityEndTime(ZonedDateTime.of(LocalDateTime.of(t.getYear(), t.getMonth(), t.getDayOfMonth(), this.validityEndHour, 0), t.getZone()));
						} else if (this.validityEndHour == 24) {
							this.setValidityEndTime(ZonedDateTime.of(LocalDateTime.of(t.getYear(), t.getMonth(), t.getDayOfMonth(), 0, 0), t.getZone()).plusDays(1));
						} else {
							this.setValidityEndTime(ZonedDateTime.of(LocalDateTime.of(t.getYear(), t.getMonth(), t.getDayOfMonth(), this.validityEndHour, 0), t.getZone()));
						}
					}
				}
			}
		}
	}

	@Override
	public boolean areTimeReferencesResolved() {
		if (this.validityStartDayOfMonth > -1 && this.validityStartHour > -1 && this.validityStartTime == null) {
			return false;
		}
		if (this.validityEndHour > -1 && this.validityEndTime == null) {
			return false;
		}
		return true;
	}
	
	@Override
	public void setPartialValidityStartTime(String time) {
		if (time == null) {
    		this.validityStartDayOfMonth = -1;
    		this.validityStartHour = -1;
    		this.validityStartMinute = -1;
    		this.validityStartTime = null;
    	} else {
    		Matcher m = VALIDITY_START_PATTERN.matcher(time);
    		if (m.matches()) {
    			int day = Integer.parseInt(m.group(1));
    			int minute = -1;
    			if (m.group(3) != null) {
    				minute = Integer.parseInt(m.group(3));
    			}
    			int hour = Integer.parseInt(m.group(2));
    			if (timeOk(day, hour, minute)) {
    				this.validityStartDayOfMonth = day;
    				this.validityStartHour = hour;
    				this.validityStartMinute = minute;
    			} else {
    				throw new IllegalArgumentException("Invalid day and/or hour values in '" + time + "'");
    			}
    			this.validityStartTime = null;
    		} else {
    			throw new IllegalArgumentException("Time '" + time + "' is not in format 'ddHH' or 'ddHHmm'");
    		}
    	}	
	}

	
	@Override
	public void setPartialValidityStartTime(int day, int hour) {
		this.setPartialValidityStartTime(day, hour, -1);
		
	}
	
	@Override
	public void setPartialValidityStartTime(int day, int hour, int minute) {
		if (timeOk(day, hour, minute)) {
			this.validityStartDayOfMonth = day;
			this.validityStartHour = hour;
			this.validityStartMinute = minute;
			this.validityStartTime = null;
		}
	}

	@Override
	public void setValidityStartTime(int year, int monthOfYear, int dayOfMonth, int hour, int minute, ZoneId timeZone) {
		this.setValidityStartTime(ZonedDateTime.of(LocalDateTime.of(year, monthOfYear, dayOfMonth, hour,  minute), timeZone));	
	}

	@Override
	public void setValidityStartTime(ZonedDateTime time) {
		this.validityStartTime = time;
		this.validityStartDayOfMonth = time.getDayOfMonth();
		this.validityStartHour = time.getHour();
		this.validityStartMinute = time.getMinute();
	}

	@Override
	public void setPartialValidityEndTime(String time) {
		if (time == null) {
    		this.validityEndDayOfMonth = -1;
    		this.validityEndHour = -1;
    		this.validityEndTime = null;
    	} else {
    		Matcher m = VALIDITY_END_PATTERN.matcher(time);
    		if (m.matches()) {
    			int day = -1;
    			if (m.group(1) != null) {
    				day = Integer.parseInt(m.group(1));
    			}
    			int hour = Integer.parseInt(m.group(2));
    			if (timeOk(day, hour, -1)) {
    				this.validityEndDayOfMonth = day;
    				this.validityEndHour = hour;
    			} else {
    				throw new IllegalArgumentException("Invalid day and/or hour values in '" + time + "'");
    			}
    			this.validityEndTime = null;
    		} else {
    			throw new IllegalArgumentException("Time '" + time + "' is not in format 'HH' or 'ddHH'");
    		}
    	}	
		
	}

	@Override
	public void setPartialValidityEndTime(int day, int hour) {
		if (timeOk(day, hour, -1)) {
			this.validityEndDayOfMonth = day;
			this.validityEndHour = hour;
			this.validityEndTime = null;
		}
	}

	@Override
	public void setValidityEndTime(int year, int monthOfYear, int dayOfMonth, int hour, int minute, ZoneId timeZone) {
		this.setValidityEndTime(ZonedDateTime.of(LocalDateTime.of(year, monthOfYear, dayOfMonth, hour,  minute), timeZone));
	}

	@Override
	public void setValidityEndTime(ZonedDateTime time) {
		this.validityEndTime = time;
		this.validityEndDayOfMonth = time.getDayOfMonth();
		this.validityEndHour = time.getHour();		
	}
	
	 private boolean timeOk(final int day, final int hour, final int minute) {
    	if (day > 31) {
			return false;
    	}
		if (hour > 24) {
			return false;
		}
		if (minute > 59) {
			return false;
		}
		if ((hour == 24) && (minute > 0)) {
			return false;
		}
		return true;
	 }

}
