package fi.fmi.avi.model.impl;

import fi.fmi.avi.model.Aerodrome;
import fi.fmi.avi.model.AerodromeUpdateEvent;
import fi.fmi.avi.model.AerodromeWeatherMessage;

public abstract class AerodromeWeatherMessageImpl extends AviationWeatherMessageImpl implements AerodromeWeatherMessage {

    private static final long serialVersionUID = 6279277951363832370L;

    private Aerodrome aerodrome;

    protected AerodromeWeatherMessageImpl() {
        super();
    }

    protected AerodromeWeatherMessageImpl(final AerodromeWeatherMessage input) {
        super(input);
        if (input != null) {
            if (input.getAerodrome() != null) {
                this.aerodrome = new Aerodrome(input.getAerodrome());
            }
        }
    }

    @Override
    public Aerodrome getAerodrome() {
        return aerodrome;
    }

    @Override
    public void setAerodrome(final Aerodrome aerodrome) {
        if (aerodrome == null) {
            final AerodromeUpdateEvent evt = new AerodromeUpdateEvent(this.aerodrome);
            this.aerodrome = null;
            this.aerodromeInfoRemoved(evt);
        } else {
            this.aerodrome = aerodrome;
            this.aerodromeInfoAdded(new AerodromeUpdateEvent(this.aerodrome));
        }

    }

    @Override
    public boolean isAerodromeInfoResolved() {
        return this.aerodrome != null && this.aerodrome.isResolved();
    }

    @Override
    public void amendAerodromeInfo(final Aerodrome fullInfo) throws IllegalArgumentException {
        if (this.aerodrome != null) {
            if (this.aerodrome.getDesignator().equals(fullInfo.getDesignator())) {
                this.aerodrome = fullInfo;
                this.aerodromeInfoChanged(new AerodromeUpdateEvent(this.aerodrome));
            } else {
                throw new IllegalArgumentException("Cannot amend aerodrome info, designator of the amening aerodrome '" + fullInfo.getDesignator() + "' does "
                        + "not match the designator of the current aerodrome '" + this.aerodrome.getDesignator() + "'");
            }
        } else {
            this.aerodrome = fullInfo;
            this.aerodromeInfoAdded(new AerodromeUpdateEvent(this.aerodrome));
        }
    }

}
