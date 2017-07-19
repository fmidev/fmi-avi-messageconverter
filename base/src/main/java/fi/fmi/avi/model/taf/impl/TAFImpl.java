package fi.fmi.avi.model.taf.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.Aerodrome;
import fi.fmi.avi.model.impl.AerodromeWeatherMessageImpl;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.TAFBaseForecast;
import fi.fmi.avi.model.taf.TAFChangeForecast;


/**
 * Created by rinne on 30/01/15.
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class TAFImpl extends AerodromeWeatherMessageImpl implements TAF {
	
    private static final Pattern VALIDITY_PERIOD_PATTERN = Pattern.compile("^(([0-9]{2})([0-9]{2})([0-9]{2}))|(([0-9]{2})([0-9]{2})/([0-9]{2})([0-9]{2}))$");
    
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
        if (input.getValidityStartTime() != null && input.getValidityEndTime() != null) {
        	this.setValidityStartTime(input.getValidityStartTime());
        	this.setValidityEndTime(input.getValidityEndTime());
        } else {
        	this.setPartialValidityTimePeriod(input.getPartialValidityTimePeriod());
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
    public int getValidityEndDayOfMonth() {
        return validityEndDayOfMonth;
    }

    @Override
    @JsonIgnore
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
    public String getPartialValidityTimePeriod() {
    	if (this.validityStartDayOfMonth > -1 && this.validityStartHour > -1 && this.validityEndHour > -1) {
    		StringBuilder sb = new StringBuilder();
    		sb.append(String.format("%02d%02d", this.validityStartDayOfMonth, this.validityStartHour));
    		if (this.validityEndDayOfMonth > -1) {
    			sb.append('/');
    			sb.append(String.format("%02d%02d", this.validityEndDayOfMonth, this.validityEndHour));
    		} else {
    			sb.append(String.format("%02d", this.validityEndHour));
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
	                //old 24h TAF, just one day field
	                int day = Integer.parseInt(m.group(2));
	                int fromHour = Integer.parseInt(m.group(3));
	                int toHour = Integer.parseInt(m.group(4));
	                this.setPartialValidityTimePeriod(day, fromHour, toHour);
	            } else {
	                //30h TAF
	                int fromDay = Integer.parseInt(m.group(6));
	                int fromHour = Integer.parseInt(m.group(7));
	                int toDay = Integer.parseInt(m.group(8));
	                int toHour = Integer.parseInt(m.group(9));
	                this.setPartialValidityTimePeriod(fromDay, toDay, fromHour, toHour);
	            }
    		} else {
    			throw new IllegalArgumentException("Time period is not either 'ddHHHH' or 'ddHH/ddHH'");
    		}
    	}
	}
	
	@Override
	public void setPartialValidityTimePeriod(int day, int startHour, int endHour) {
		this.setPartialValidityTimePeriod(day, -1, startHour, endHour);
	}

	@Override
	public void setPartialValidityTimePeriod(int startDay, int endDay, int startHour, int endHour) {
		if (timeOk(startDay, startHour) && timeOk(endDay, endHour)) {
			this.validityStartDayOfMonth = startDay;
			this.validityStartHour = startHour;
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

	@Override
	public void setValidityStartTime(int year, int monthOfYear, int dayOfMonth, int hour, int minute, ZoneId timeZone) {
		this.setValidityStartTime(ZonedDateTime.of(LocalDateTime.of(year, monthOfYear, dayOfMonth, hour,  minute), timeZone));
		
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
	public void setValidityStartTime(ZonedDateTime time) {
		this.validityStartTime = time;
		this.validityStartDayOfMonth = time.getDayOfMonth();
		this.validityStartHour = time.getHour();
	}

	@Override
	public void setValidityEndTime(int year, int monthOfYear, int dayOfMonth, int hour, int minute, ZoneId timeZone) {
		this.setValidityEndTime(ZonedDateTime.of(LocalDateTime.of(year, monthOfYear, dayOfMonth, hour,  minute), timeZone));
		
	}
	
	@JsonProperty("validityEndTime")
    public String getValidityEndTimeISO() {
    	if (this.validityEndTime != null) {
    		return this.validityEndTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    	} else {
    		return null;
    	}
    }
	
	@Override
	@JsonIgnore
	public ZonedDateTime getValidityEndTime() {
		return this.validityEndTime;
	}

	@JsonProperty("validityEndTime")
    public void setValidityEndTimeISO(final String time) {
    	this.setValidityEndTime(ZonedDateTime.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(time)));
    }
	
	@Override
	public void setValidityEndTime(ZonedDateTime time) {
		this.validityEndTime = time;
		this.validityEndDayOfMonth = time.getDayOfMonth();
		this.validityEndHour = time.getHour();
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
	
	private boolean timeOk(final int day, final int hour) {
		if (day > 31) {
			return false;
		}
		if (hour > 24) {
			return false;
		}
		return true;
    }

	@Override
	protected void syncAerodromeInfo(Aerodrome fullInfo) {
		//No need to disseminate Aerodrome info the properties for TAFImpl
	}

	
}
