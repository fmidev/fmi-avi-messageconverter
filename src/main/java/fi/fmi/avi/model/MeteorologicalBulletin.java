package fi.fmi.avi.model;

import java.time.YearMonth;
import java.util.List;

public interface MeteorologicalBulletin<T extends AviationWeatherMessage, S extends BulletinHeading> extends AviationWeatherMessageOrCollection {

    /**
     * Returns the issue time of the bulletin.
     * The returned {@link PartialOrCompleteTimeInstant} may or may not contain
     * a completely resolved date time depending on which information it was
     * created with.
     *
     * @return the fully resolved issue time
     *
     * @see PartialOrCompleteTimeInstant.Builder#completePartialAt(YearMonth)
     */
    PartialOrCompleteTimeInstant getIssueTime();

    S getHeading();

    List<T> getMessages();

}
