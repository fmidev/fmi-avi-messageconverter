package fi.fmi.avi.model;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.immutable.GenericAviationWeatherMessageImpl;

/**
 * The class-level {@link JsonDeserialize} hint is needed because this interface can appear as the element type of a
 * {@code List} property (e.g. {@code GenericMeteorologicalBulletin.getMessages()}); a per-property
 * {@code contentAs} hint placed on an overridden {@code addAllX(...)}/{@code setX(...)} method is not reliably
 * picked up by Jackson for builder-style deserialization of such properties (see docs/07-modernization-plan.md).
 */
@JsonDeserialize(as = GenericAviationWeatherMessageImpl.class)
public interface GenericAviationWeatherMessage extends AviationWeatherMessage {
    /**
     * Returns the GML identifier (gml:id) of the IWXXM message root element.
     *
     * @return the gml:id attribute value, if available
     */
    Optional<String> getGmlId();

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
