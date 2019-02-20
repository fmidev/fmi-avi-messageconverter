package fi.fmi.avi.model;

import java.util.Optional;

public interface GenericAviationWeatherMessage extends AviationWeatherMessage {
    enum MessageType {TAF, METAR, SPECI, SIGMET, GAFOR, AIRMET, TROPICAL_CYCLONE_ADVISORY, VOLCANIC_ASH_ADVISORY}

    ;
    enum Format { TAC, IWXXM };

    String getOriginalMessage();

    MessageType getMessageType();

    Format getMessageFormat();

    PartialOrCompleteTimeInstant getIssueTime();

    Optional<PartialOrCompleteTimePeriod> getValidityTime();

    Optional<Aerodrome> getTargetAerodrome();



}
