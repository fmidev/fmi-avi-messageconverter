package fi.fmi.avi.data.taf.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import fi.fmi.avi.data.taf.TAFChangeForecast;

/**
 * Created by rinne on 30/01/15.
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class TAFChangeForecastImpl extends TAFForecastImpl implements TAFChangeForecast {

    private static final Pattern VALIDITY_PERIOD_PATTERN = Pattern.compile("^(([0-9]{2})([0-9]{2}))|(([0-9]{2})([0-9]{2})/([0-9]{2})([0-9]{2}))$");
    private static final Pattern VALIDITY_START_PATTERN = Pattern.compile("^(FM)?([0-9]{2})?([0-9]{2})([0-9]{2})$");
    
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
        if (input.getValidityStartTime() != null && input.getValidityEndTime() != null) {
        	this.setValidityStartTime(input.getValidityStartTime());
        	this.setValidityEndTime(input.getValidityEndTime());
        } else if (input.getPartialValidityTimePeriod() != null) {
        	this.setPartialValidityTimePeriod(input.getPartialValidityTimePeriod());
        } else {
        	this.setPartialValidityStartTime(input.getPartialValidityStartTime());
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
    @JsonIgnore
    public int getValidityStartDayOfMonth() {
        return validityStartDayOfMonth;
    }

    @Override
    @JsonIgnore
    public int getValidityStartHour() {
        return validityStartHour;
    }

    @Override
    @JsonIgnore
    public int getValidityStartMinute() {
        return validityStartMinute;
    }


    @Override
    @JsonIgnore
    public int getValidityEndDayOfMonth() {
        return validityEndDayOfMonth;
    }

    @Override
    @JsonIgnore
    public int getValidityEndHour() {
        return validityEndHour;
    }
   
    @Override
    @JsonProperty("partialValidityStartTime")
   	public String getPartialValidityStartTime() {
       	if (this.validityEndHour == -1 && this.validityStartHour > -1 && this.validityStartMinute > -1) {
       		StringBuilder sb = new StringBuilder();
       		if (this.validityStartDayOfMonth > -1) {
       			sb.append(String.format("%02d", this.validityStartDayOfMonth));
       		}
       		sb.append(String.format("%02d%02d", this.validityStartHour, this.validityStartMinute));
       		
       		return sb.toString();
       	} else {
       		return null;
       	}
   	}
    
    @Override
    @JsonProperty("partialValidityStartTime")
	public void setPartialValidityStartTime(String time) {
		if (time == null) {
			this.setPartialValidityStartTime(-1, -1, -1);
    	} else {
    		Matcher m = VALIDITY_START_PATTERN.matcher(time);
    		if (m.matches()) {
    			int day = -1;
    			if (m.group(2) != null) {
    				day = Integer.parseInt(m.group(2));
    			}
    			int hour = Integer.parseInt(m.group(3));
    			int minute = Integer.parseInt(m.group(4));
    			this.setPartialValidityStartTime(day, hour, minute);
    		} else {
    			throw new IllegalArgumentException("Time '" + time + "' is not in format '(FM)HHmm' or '(FM)ddHHmm'");
    		}
    	}	
	}
	
	@Override
	public void setPartialValidityStartTime(int hour, int minute) {
		this.setPartialValidityStartTime(-1, hour, minute);	
	}
	
	@Override
	public void setPartialValidityStartTime(int day, int hour, int minute) {
		if (timeOk(day, hour, minute)) {
			this.validityStartDayOfMonth = day;
			this.validityStartHour = hour;
			this.validityStartMinute = minute;
			if (this.validityStartTime != null) {
				if ( (this.validityStartDayOfMonth != day)
						|| (this.validityStartHour != hour)
						|| (this.validityStartMinute != minute)) {
					this.validityStartTime = null;
				}
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
	public String getPartialValidityTimePeriod() {
    	if (this.validityStartHour > -1 && this.validityEndHour > -1) {
    		StringBuilder sb = new StringBuilder();
    		if (this.validityStartDayOfMonth > -1 && this.validityEndDayOfMonth > -1) {
    			sb.append(String.format("%02d%02d", this.validityStartDayOfMonth, this.validityStartHour));
    			sb.append('/');
    			sb.append(String.format("%02d%02d", this.validityEndDayOfMonth, this.validityEndHour));
    		} else {
    			sb.append(String.format("%02d%02d", this.validityStartHour, this.validityEndHour));
    		}
    		return sb.toString();
    	} else {
    		return null;
    	}
	}

	@Override
	@JsonProperty("partialValidityTimePeriod")
	public void setPartialValidityTimePeriod(String time) {
		if (time == null) {
			this.setPartialValidityTimePeriod(-1, -1, -1, -1);
    	} else {
    		Matcher m = VALIDITY_PERIOD_PATTERN.matcher(time);
    		if (m.matches()) {
	    		if (m.group(1) != null) {
	                //old 24h TAF: HHHH
	                int fromHour = Integer.parseInt(m.group(2));
	                int toHour = Integer.parseInt(m.group(3));
	                this.setPartialValidityTimePeriod(fromHour, toHour);
	
	            } else {
	                //30h TAF: ddHH/ddHH
	                int fromDay = Integer.parseInt(m.group(5));
	                int fromHour = Integer.parseInt(m.group(6));
	                int toDay = Integer.parseInt(m.group(7));
	                int toHour = Integer.parseInt(m.group(8));
	                this.setPartialValidityTimePeriod(fromDay, toDay, fromHour, toHour);
	            }
	    		this.validityStartMinute = -1;
    		} else {
    			throw new IllegalArgumentException("Time period is not either 'ddHHHH' or 'ddHH/ddHH'");
    		}
    		
    	}	
	}
	
	@Override
	public void setPartialValidityTimePeriod(int startHour, int endHour) {
		this.setPartialValidityTimePeriod(-1, -1, startHour, endHour);
	}

	@Override
	public void setPartialValidityTimePeriod(int startDay, int endDay, int startHour, int endHour) {
		if (timeOk(startDay, startHour, -1) && timeOk(endDay, endHour, -1)) {
			this.validityStartDayOfMonth = startDay;
			this.validityStartHour = startHour;
			this.validityStartMinute = -1;
			this.validityEndDayOfMonth = endDay;
			this.validityEndHour = endHour;
			if (this.validityStartTime != null) {
				if ( (this.validityStartTime.getDayOfMonth() != startDay) || (this.validityStartTime.getHour() != startHour) ) {
					this.validityStartTime = null;
				}
			}
			if (this.validityEndTime != null) {
				if ( (this.validityEndTime.getDayOfMonth() != endDay) || (this.validityEndTime.getHour() != endHour) ) {
					this.validityEndTime = null;
				}
			}
		}
	}
	
	@JsonProperty("validityStartTime")
    public String getValidityStartTimeISO() {
    	if (this.validityStartTime != null) {
    		return this.validityStartTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    	} else {
    		return null;
    	}
    }
	
	@Override
	@JsonIgnore
	public ZonedDateTime getValidityStartTime() {
		return this.validityStartTime;
	}
	
	@JsonProperty("validityStartTime")
	public void setValidityStartTimeISO(final String time) {
		this.setValidityStartTime(ZonedDateTime.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(time)));
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

	@JsonProperty("validityEndTime")
    public String getValidityEndTimeISO() {
    	if (this.validityEndTime != null) {
    		return this.validityEndTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    	} else {
    		return null;
    	}
    }
	
	@JsonProperty("validityEndTime")
	public void setValidityEndTimeISO(final String time) {
		this.setValidityEndTime(ZonedDateTime.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(time)));
	}
	 
	@Override
	@JsonIgnore
	public ZonedDateTime getValidityEndTime() {
		return this.validityEndTime;
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
