package fi.fmi.avi.model;

import java.util.Optional;

public interface GenericAviationWeatherMessage extends AviationWeatherMessage {
    enum Format { TAC, IWXXM };

    String getOriginalMessage();

    Optional<AviationCodeListUser.MessageType> getMessageType();

    Format getMessageFormat();

    Optional<PartialOrCompleteTimeInstant> getIssueTime();

    Optional<PartialOrCompleteTimePeriod> getValidityTime();

    Optional<Aerodrome> getTargetAerodrome();



}
