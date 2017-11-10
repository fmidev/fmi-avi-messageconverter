package fi.fmi.avi.model.metar.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonInclude;

import fi.fmi.avi.model.impl.PartialOrCompleteTimePeriodImpl;
import fi.fmi.avi.model.metar.TrendTimeGroups;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class TrendTimeGroupsImpl extends PartialOrCompleteTimePeriodImpl implements TrendTimeGroups {
	private static final Pattern HOUR_MINUTE_PATTERN = Pattern.compile("([0-9]{2})([0-9]{2})?");
    
    private boolean isSingular = false;

    public TrendTimeGroupsImpl() {
    }

    public TrendTimeGroupsImpl(final TrendTimeGroups input) {
    	if (input != null) {
			if (input.getCompleteStartTime() != null) {
				this.setCompleteStartTime(input.getCompleteStartTime());
			} else {
				this.setPartialStartTime(input.getPartialStartTime());
			}
			if (input.getCompleteEndTime()!= null) {
				this.setCompleteEndTime(input.getCompleteEndTime());
			} else {
				this.setPartialEndTime(input.getPartialEndTime());
			}
			this.isSingular = input.isSingleInstance();
		}
    }

    
    @Override
    public boolean isSingleInstance() {
        return isSingular;
    }
	
	@Override
    public void setSingleInstance(final boolean singular) {
        this.isSingular = singular;
    }

    @Override
    public String getPartialStartTime() {
        if (this.hasStartTime()) {
            return String.format("%02d%02d", this.getStartTimeHour(), this.getStartTimeMinute());
        } else {
            return null;
        }
    }

    @Override
    public String getPartialEndTime() {
        if (this.hasEndTime()) {
            return String.format("%02d%02d", this.getEndTimeHour(), this.getEndTimeMinute());
        } else {
            return null;
        }
    }

    @Override
    public boolean hasStartTime() {
        return this.getStartTimeHour() > -1 && this.getStartTimeMinute() > -1;
    }

    @Override
    public boolean hasEndTime() {
        return this.getEndTimeHour() > -1 && this.getEndTimeMinute() > -1;
    }

    @Override
    protected boolean matchesPartialTimePattern(final String partialString) {
        return HOUR_MINUTE_PATTERN.matcher(partialString).matches();
    }

    @Override
    protected Pattern getPartialTimePattern() {
        return HOUR_MINUTE_PATTERN;
    }

    @Override
    protected int extractDayFromPartial(final String partialString) {
        return -1;
    }

    @Override
    protected int extractHourFromPartial(final String partialString) {
        Matcher m = HOUR_MINUTE_PATTERN.matcher(partialString);
        if (m.matches()) {
            return Integer.parseInt(m.group(1));
        } else {
            return -1;
        }
    }

    @Override
    protected int extractMinuteFromPartial(final String partialString) {
        Matcher m = HOUR_MINUTE_PATTERN.matcher(partialString);
        if (m.matches()) {
            return Integer.parseInt(m.group(2));
        } else {
            return -1;
        }
    }
}
