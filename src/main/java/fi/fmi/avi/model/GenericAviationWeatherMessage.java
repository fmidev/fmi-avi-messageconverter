package fi.fmi.avi.model;

import java.util.Optional;

public interface GenericAviationWeatherMessage extends AviationWeatherMessage {
    enum Format { TAC, IWXXM }

    String getOriginalMessage();

    Optional<MessageType> getMessageType();

    Format getMessageFormat();

    Optional<PartialOrCompleteTimePeriod> getValidityTime();

    Optional<Aerodrome> getTargetAerodrome();



}
