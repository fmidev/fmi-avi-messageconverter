package fi.fmi.avi.data.taf.impl;

import java.util.ArrayList;
import java.util.List;

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

    private TAFStatus status;
    private Aerodrome aerodrome;
    private int validityStartDayOfMonth = -1;
    private int validityStartHour = -1;
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
        this.validityStartDayOfMonth = input.getValidityStartDayOfMonth();
        this.validityStartHour = input.getValidityStartHour();
        this.validityEndDayOfMonth = input.getValidityEndDayOfMonth();
        this.validityEndHour = input.getValidityEndHour();
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


}
