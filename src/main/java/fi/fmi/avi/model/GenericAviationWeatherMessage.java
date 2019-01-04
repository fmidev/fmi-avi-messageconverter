package fi.fmi.avi.model;

import java.util.Optional;

public interface GenericAviationWeatherMessage extends AviationWeatherMessage {
    enum MessageType { TAF, METAR, SIGMET, GAFOR, AIRMET };
    enum Format { TAC, IWXXM };

    String getOriginalMessage();

    MessageType getMessageType();

    Format getMessageFormat();

    PartialOrCompleteTimeInstant getIssueTime();

    Optional<PartialOrCompleteTimePeriod> getValidityTime();

    Optional<Aerodrome> getTargetAerodrome();



}
