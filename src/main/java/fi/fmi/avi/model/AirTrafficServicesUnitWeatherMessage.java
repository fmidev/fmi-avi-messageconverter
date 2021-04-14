package fi.fmi.avi.model;

import java.time.YearMonth;

public interface AirTrafficServicesUnitWeatherMessage extends AviationWeatherMessage {
    /**
     * Returns the issue time of the message.
     * The returned {@link PartialOrCompleteTimeInstant} may or may not contain
     * a completely resolved date time depending on which information it was
     * created with.
     *
     * @return the fully resolved issue time
     *
     * @see PartialOrCompleteTimeInstant.Builder#completePartialAt(YearMonth)
     */

    UnitPropertyGroup getIssuingAirTrafficServicesUnit();

    UnitPropertyGroup getMeteorologicalWatchOffice();
}
