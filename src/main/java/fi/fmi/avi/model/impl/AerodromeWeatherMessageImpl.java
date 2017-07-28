package fi.fmi.avi.model.impl;

import fi.fmi.avi.model.Aerodrome;
import fi.fmi.avi.model.AerodromeWeatherMessage;

public abstract class AerodromeWeatherMessageImpl extends AviationWeatherMessageImpl implements AerodromeWeatherMessage {

	private Aerodrome aerodrome;

	public AerodromeWeatherMessageImpl() {
		super();
	}
	
	public AerodromeWeatherMessageImpl(AerodromeWeatherMessage input) {
		super(input);
		this.aerodrome = input.getAerodrome();
	}

	 @Override
    public Aerodrome getAerodrome() {
        return aerodrome;
    }

    @Override
    public void setAerodrome(final Aerodrome aerodrome) {
        this.aerodrome = aerodrome;
        this.syncAerodromeInfo(this.aerodrome);
    }


    @Override
	public boolean isAerodromeInfoResolved() {
		return this.aerodrome != null && this.aerodrome.isResolved();
	}

	@Override
	public void amendAerodromeInfo(Aerodrome fullInfo) {
		if (this.aerodrome != null) {
			if (this.aerodrome.getDesignator().equals(fullInfo.getDesignator())) {
				this.aerodrome = fullInfo;
			}
		} else {
			this.aerodrome = fullInfo;
		}
		this.syncAerodromeInfo(this.aerodrome);
	}
	
	
	protected abstract void syncAerodromeInfo(Aerodrome fullInfo);
}
