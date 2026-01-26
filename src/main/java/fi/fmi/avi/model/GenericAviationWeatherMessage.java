package fi.fmi.avi.model;

import java.util.Map;
import java.util.Optional;

public interface GenericAviationWeatherMessage extends AviationWeatherMessage {
    Optional<String> getXMLNamespace();

    String getOriginalMessage();

    Optional<MessageType> getMessageType();

    Format getMessageFormat();

    Optional<PartialOrCompleteTimePeriod> getValidityTime();

    /**
     * Returns the observation time for METAR and SPECI. For other message types, this will be empty.
     *
     * @return the observation time, if applicable
     */
    Optional<PartialOrCompleteTimeInstant> getObservationTime();

    Map<LocationIndicatorType, String> getLocationIndicators();

    boolean isNil();

    enum Format {
        TAC, IWXXM
    }

    enum LocationIndicatorType {
        AERODROME, //
        ISSUING_CENTRE, //
        ISSUING_AIR_TRAFFIC_SERVICES_UNIT, //
        ISSUING_AIR_TRAFFIC_SERVICES_REGION, //
        ORIGINATING_METEOROLOGICAL_WATCH_OFFICE
    }
}
