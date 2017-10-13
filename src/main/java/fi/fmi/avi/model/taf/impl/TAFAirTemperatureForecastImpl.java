package fi.fmi.avi.model.taf.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.impl.NumericMeasureImpl;
import fi.fmi.avi.model.taf.TAFAirTemperatureForecast;

/**
 * Created by rinne on 30/01/15.
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class TAFAirTemperatureForecastImpl implements TAFAirTemperatureForecast {

	private static Pattern DAY_HOUR_PATTERN = Pattern.compile("([0-9]{2})?([0-9]{2})([A-Z]*)");
	
    private NumericMeasure maxTemperature;
    private int maxTemperatureDayOfMonth = -1;
    private int maxTemperatureHour = -1;
    private ZonedDateTime maxTemperatureTime = null;
    private NumericMeasure minTemperature;
    private int minTemperatureDayOfMonth = -1;
    private int minTemperatureHour = -1;
    private ZonedDateTime minTemperatureTime = null;

    public TAFAirTemperatureForecastImpl(){
    }

    public TAFAirTemperatureForecastImpl(final TAFAirTemperatureForecast input) {
    	if (input != null) {
    		if (input.getMaxTemperature() != null) {
				this.maxTemperature = new NumericMeasureImpl(input.getMaxTemperature());
			}
			if (input.getMaxTemperatureTime() != null) {
				this.setMaxTemperatureTime(input.getMaxTemperatureTime());
			} else {
				this.setPartialMaxTemperatureTime(input.getPartialMaxTemperatureTime());
			}
			if (input.getMinTemperatureTime() != null) {
				this.setMinTemperatureTime(input.getMinTemperatureTime());
			} else {
				this.setPartialMinTemperatureTime(input.getPartialMinTemperatureTime());
			}
			if (input.getMinTemperature() != null) {
				this.minTemperature = new NumericMeasureImpl(input.getMinTemperature());
			}
		}
    }

    @Override
    public NumericMeasure getMaxTemperature() {
        return maxTemperature;
    }

    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public void setMaxTemperature(final NumericMeasure maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    @Override
    public int getMaxTemperatureDayOfMonth() {
    	if (this.maxTemperatureTime != null) {
            return maxTemperatureTime.getDayOfMonth();
    	} else {
            return maxTemperatureDayOfMonth;
    	}
    }

    @Override
    public int getMaxTemperatureHour() {
        return maxTemperatureHour;
    }

    @Override
    public NumericMeasure getMinTemperature() {
        return minTemperature;
    }

    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public void setMinTemperature(final NumericMeasure minTemperature) {
        this.minTemperature = minTemperature;
    }

    @Override
    public int getMinTemperatureDayOfMonth() {
    	if (this.minTemperatureTime != null) {
    		return this.minTemperatureTime.getDayOfMonth();
    	} else {
    		return minTemperatureDayOfMonth;
    	}
    }

    @Override
    public int getMinTemperatureHour() {
        return minTemperatureHour;
    }

	@Override
	public String getPartialMaxTemperatureTime() {
		if (this.maxTemperatureHour > -1){
			if (this.maxTemperatureDayOfMonth> -1){ 
				return String.format("%02d%02dZ", this.maxTemperatureDayOfMonth, this.maxTemperatureHour);
			} else {
				return String.format("%02dZ", this.maxTemperatureHour);
			}
    	} else {
    		return null;
    	}
	}

	@Override
	public ZonedDateTime getMaxTemperatureTime() {
		return this.maxTemperatureTime;
	}

	@Override
	public String getPartialMinTemperatureTime() {
		if (this.minTemperatureHour > -1){
			if (this.minTemperatureDayOfMonth> -1){ 
				return String.format("%02d%02dZ", this.minTemperatureDayOfMonth, this.minTemperatureHour);
			} else {
				return String.format("%02dZ", this.minTemperatureHour);
			}
    	} else {
    		return null;
    	}
	}

	@Override
	public ZonedDateTime getMinTemperatureTime() {
		return this.minTemperatureTime;
	}

	@Override
	public void amendTimeReferences(final ZonedDateTime referenceTime) {
		if (referenceTime != null) {
			if (this.minTemperatureHour > -1) {
				if (this.minTemperatureDayOfMonth > -1) {
					if (this.minTemperatureDayOfMonth < referenceTime.getDayOfMonth()) {
						//Assume the next month
						ZonedDateTime oneMonthAfterIssue = referenceTime.plusMonths(1);
						this.setMinTemperatureTime(ZonedDateTime.of(LocalDateTime.of(oneMonthAfterIssue.getYear(), oneMonthAfterIssue.getMonth(), this.minTemperatureDayOfMonth, this.minTemperatureHour, 0), oneMonthAfterIssue.getZone()));
					} else {
						this.setMinTemperatureTime(ZonedDateTime.of(LocalDateTime.of(referenceTime.getYear(), referenceTime.getMonth(), this.minTemperatureDayOfMonth, this.minTemperatureHour, 0), referenceTime.getZone()));
					}
				} else {
					this.minTemperatureTime = ZonedDateTime.of(LocalDateTime.of(referenceTime.getYear(), referenceTime.getMonth(), referenceTime.getDayOfMonth(), this.minTemperatureHour, 0), referenceTime.getZone());
				}
			}
			
			if (this.maxTemperatureHour > -1) {
				if (this.maxTemperatureDayOfMonth > -1) {
					if (this.maxTemperatureDayOfMonth < referenceTime.getDayOfMonth()) {
						//Assume the next month
						ZonedDateTime oneMonthAfterIssue = referenceTime.plusMonths(1);
						this.setMaxTemperatureTime(ZonedDateTime.of(LocalDateTime.of(oneMonthAfterIssue.getYear(), oneMonthAfterIssue.getMonth(), this.maxTemperatureDayOfMonth, this.maxTemperatureHour, 0), oneMonthAfterIssue.getZone()));
					} else {
						this.setMaxTemperatureTime(ZonedDateTime.of(LocalDateTime.of(referenceTime.getYear(), referenceTime.getMonth(), this.maxTemperatureDayOfMonth, this.maxTemperatureHour, 0), referenceTime.getZone()));
					}
				} else {
					this.maxTemperatureTime = ZonedDateTime.of(LocalDateTime.of(referenceTime.getYear(), referenceTime.getMonth(), referenceTime.getDayOfMonth(), this.maxTemperatureHour, 0), referenceTime.getZone());
				}
			}
		}
	}

	@Override
	public boolean areTimeReferencesResolved() {
		if (this.minTemperatureHour > -1 && this.minTemperatureTime == null) {
			return false;
		}
		if (this.maxTemperatureHour > -1 && this.maxTemperatureTime == null) {
			return false;
		}
		return true;
	}

	@Override
	@JsonProperty("partialMinTemperatureTime")
	public void setPartialMinTemperatureTime(final String time) {
		if (time == null) {
    		this.minTemperatureDayOfMonth = -1;
    		this.minTemperatureHour = -1;
    		this.minTemperatureTime = null;
    	} else {
    		Matcher m = DAY_HOUR_PATTERN.matcher(time);
    		if (m.matches()) {
    			int day = -1;
    			if (m.group(1) != null) {
    				day = Integer.parseInt(m.group(1));
    			}
    			int hour = Integer.parseInt(m.group(2));
    			if (timeOk(day, hour)) {
    				this.minTemperatureDayOfMonth = day;
    				this.minTemperatureHour = hour;
    			} else {
    				throw new IllegalArgumentException("Invalid day and/or hour values in '" + time + "'");
    			}
    			this.minTemperatureTime = null;
    		} else {
    			throw new IllegalArgumentException("Time '" + time + "' is not in format 'HHZ' or 'ddHHZ'");
    		}
    	}
	}

	@Override
	public void setPartialMinTemperatureTime(final int hour) {
		this.setPartialMinTemperatureTime(-1, hour);
	}
	
	@Override
	public void setPartialMinTemperatureTime(final int day, final int hour) {
		if (timeOk(day, hour)) {
			this.minTemperatureDayOfMonth = day;
			this.minTemperatureHour = hour;
			this.minTemperatureTime = null;
		}
	}

	@Override
	public void setMinTemperatureTime(final int year, final int monthOfYear, final int dayOfMonth, final int hour,
			final ZoneId timeZone) {
		this.setMinTemperatureTime(ZonedDateTime.of(LocalDateTime.of(year, monthOfYear, dayOfMonth, hour, 0), timeZone));
	}

	@Override
	public void setMinTemperatureTime(final ZonedDateTime time) {
		this.minTemperatureTime = time;
		this.minTemperatureDayOfMonth = time.getDayOfMonth();
		this.minTemperatureHour = time.getHour();
	}

	@Override
	@JsonProperty("partialMaxTemperatureTime")
	public void setPartialMaxTemperatureTime(final String time) {
		if (time == null) {
    		this.maxTemperatureDayOfMonth = -1;
    		this.maxTemperatureHour = -1;
    		this.maxTemperatureTime = null;
    	} else {
    		Matcher m = DAY_HOUR_PATTERN.matcher(time);
    		if (m.matches()) {
    			int day = -1;
    			if (m.group(1) != null) {
    				day = Integer.parseInt(m.group(1));
    			}
    			int hour = Integer.parseInt(m.group(2));
    			if (timeOk(day, hour)) {
    				this.maxTemperatureDayOfMonth = day;
    				this.maxTemperatureHour = hour;
    			} else {
    				throw new IllegalArgumentException("Invalid day and/or hour values in '" + time + "'");
    			}
    			this.maxTemperatureTime = null;
    		} else {
    			throw new IllegalArgumentException("Time '" + time + "' is not in format 'HHZ' or 'ddHHZ'");
    		}
    	}
		
	}

	@Override
	public void setPartialMaxTemperatureTime(final int hour) {
		this.setPartialMaxTemperatureTime(-1, hour);
	}
	
	@Override
	public void setPartialMaxTemperatureTime(final int day, final int hour) {
		if (timeOk(day, hour)) {
			this.maxTemperatureDayOfMonth = day;
			this.maxTemperatureHour = hour;
			this.maxTemperatureTime = null;
		}
		
	}

	@Override
	public void setMaxTemperatureTime(int year, int monthOfYear, int dayOfMonth, int hour,
			ZoneId timeZone) {
		this.setMaxTemperatureTime(ZonedDateTime.of(LocalDateTime.of(year, monthOfYear, dayOfMonth, hour, 0), timeZone));
	}

	@Override
	public void setMaxTemperatureTime(ZonedDateTime time) {
		this.maxTemperatureTime = time;
		this.maxTemperatureDayOfMonth = time.getDayOfMonth();
		this.maxTemperatureHour = time.getHour();
		
	}
	
	 private static boolean timeOk(final int day, final int hour) {
		if (day > 31) {
			return false;
		}
    	if (hour > 24) {
			return false;
		}
		return true;
    }
}
