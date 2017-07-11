package fi.fmi.avi.data.taf.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.data.Aerodrome;
import fi.fmi.avi.data.impl.AviationWeatherMessageImpl;
import fi.fmi.avi.data.taf.TAF;
import fi.fmi.avi.data.taf.TAFBaseForecast;
import fi.fmi.avi.data.taf.TAFChangeForecast;

/**
 * Created by rinne on 30/01/15.
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class TAFImpl extends AviationWeatherMessageImpl implements TAF {

	private static final Pattern VALIDITY_START_PATTERN = Pattern.compile("([0-9]{2})([0-9]{2})");
    private static final Pattern VALIDITY_END_PATTERN = Pattern.compile("([0-9]{2})?([0-9]{2})");
    
    private TAFStatus status;
    private Aerodrome aerodrome;
    private ZonedDateTime validityStartTime = null;
    private int validityStartDayOfMonth = -1;
    private int validityStartHour = -1;
    private ZonedDateTime validityEndTime = null; 
    private int validityEndDayOfMonth = -1;
    private int validityEndHour = -1;
    private TAFBaseForecast baseForecast;
    private List<TAFChangeForecast> changeForecasts;
    private TAF referredReport;
    private List<String> remarks;

    public TAFImpl() {
    }

    public TAFImpl(final TAF input) {
        super(input);
        this.status = input.getStatus();
        this.aerodrome = input.getAerodrome();
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
        this.baseForecast = new TAFBaseForecastImpl(input.getBaseForecast());
        this.changeForecasts = new ArrayList<TAFChangeForecast>();
        for (TAFChangeForecast fct : input.getChangeForecasts()) {
            this.changeForecasts.add(new TAFChangeForecastImpl(fct));
        }
        this.referredReport = input.getReferredReport();
        this.remarks = new ArrayList<>();
        this.remarks.addAll(input.getRemarks());
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
    public Aerodrome getAerodrome() {
        return aerodrome;
    }

    @Override
    public void setAerodrome(final Aerodrome aerodrome) {
        this.aerodrome = aerodrome;
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
    public int getValidityEndDayOfMonth() {
        return validityEndDayOfMonth;
    }

    @Override
    public int getValidityEndHour() {
        return validityEndHour;
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
    public TAF getReferredReport() {
        return this.referredReport;
    }

    @Override
    @JsonDeserialize(as = TAFImpl.class)
    public void setReferredReport(final TAF referredReport) {
        this.referredReport = referredReport;
    }

	@Override
	public boolean isAerodromeInfoResolved() {
		return this.aerodrome != null && this.aerodrome.isResolved();
	}

	@Override
	public void amendAerodromeInfo(Aerodrome fullInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getPartialValidityStartTime() {
		if (this.validityStartHour > -1 && this.validityStartDayOfMonth> -1){
			return String.format("%02d%02d", this.validityStartDayOfMonth, this.validityStartHour);
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
		super.amendTimeReferences(referenceTime);
		ZonedDateTime issueTime = this.getIssueTime();
		if (issueTime != null) {
			if (this.validityStartDayOfMonth > -1 && this.validityStartHour > -1) {
				if (this.validityStartDayOfMonth < issueTime.getDayOfMonth()) {
					// roll over the next month
					ZonedDateTime t = issueTime.plusMonths(1);
					this.setValidityStartTime(ZonedDateTime.of(LocalDateTime.of(t.getYear(), t.getMonth(), this.validityStartDayOfMonth, this.validityStartHour, 0), issueTime.getZone()));
				} else if (this.validityStartDayOfMonth == issueTime.getDayOfMonth()) {
					ZonedDateTime t = ZonedDateTime.from(issueTime);
					if (this.validityStartHour < issueTime.getHour()) {
						// roll over to the next day
						t = t.plusDays(1);
					}
					this.setValidityStartTime(ZonedDateTime.of(LocalDateTime.of(t.getYear(), t.getMonth(), t.getDayOfMonth(), this.validityStartHour, 0), t.getZone()));
				} else {
					// the same month but a later day
					this.setValidityStartTime(ZonedDateTime.of(LocalDateTime.of(issueTime.getYear(), issueTime.getMonth(), this.validityStartDayOfMonth, this.validityStartHour, 0), issueTime.getZone()));
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
			
			if (this.baseForecast != null && !this.baseForecast.areTimeReferencesResolved()) {
				this.baseForecast.amendTimeReferences(issueTime);
			}
			if (this.changeForecasts != null) {
				for (TAFChangeForecast fct:this.changeForecasts){
					if (!fct.areTimeReferencesResolved()) {
						fct.amendTimeReferences(issueTime);
					}
				}
			}
		}
	}
	
	@Override
	public boolean areTimeReferencesResolved() {
		if (!super.areTimeReferencesResolved()) {
			return false;
		}
		if (this.validityStartDayOfMonth > -1 && this.validityStartHour > -1 && this.validityStartTime == null) {
			return false;
		}
		if (this.validityEndHour > -1 && this.validityEndTime == null) {
			return false;
		}
		if (this.baseForecast != null && !this.baseForecast.areTimeReferencesResolved()) {
			return false;
		}
		if (this.changeForecasts != null) {
			for (TAFChangeForecast fct:this.changeForecasts){
				if (!fct.areTimeReferencesResolved()) {
					return false;
				}
			}
		}
		return true;
	}
	

	@Override
	public void setPartialValidityStartTime(String time) {
		if (time == null) {
    		this.validityStartDayOfMonth = -1;
    		this.validityStartHour = -1;
    		this.validityStartTime = null;
    	} else {
    		Matcher m = VALIDITY_START_PATTERN.matcher(time);
    		if (m.matches()) {
    			int day = Integer.parseInt(m.group(1));
    			int hour = Integer.parseInt(m.group(2));
    			if (timeOk(day, hour)) {
    				this.validityStartDayOfMonth = day;
    				this.validityStartHour = hour;
    			} else {
    				throw new IllegalArgumentException("Invalid day and/or hour values in '" + time + "'");
    			}
    			this.validityStartTime = null;
    		} else {
    			throw new IllegalArgumentException("Time '" + time + "' is not in format 'ddHH'");
    		}
    	}
		
	}

	@Override
	public void setPartialValidityStartTime(int day, int hour) {
		if (timeOk(day, hour)) {
			this.validityStartDayOfMonth = day;
			this.validityStartHour = hour;
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
    			if (timeOk(day, hour)) {
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
		if (timeOk(day, hour)) {
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
	
	 private boolean timeOk(final int day, final int hour) {
    	if (day > 31) {
			return false;
    	}
		if (hour > 23) {
			return false;
		}
		return true;
    }


}
