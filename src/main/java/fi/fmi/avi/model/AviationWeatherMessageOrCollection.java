package fi.fmi.avi.model;

import java.time.YearMonth;

public interface AviationWeatherMessageOrCollection {
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
    PartialOrCompleteTimeInstant getIssueTime();
}
