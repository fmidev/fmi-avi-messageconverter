package fi.fmi.avi.model;

import java.util.Optional;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.immutable.RunwayDirectionImpl;

/**
 * The class-level {@link JsonDeserialize} hint is needed because this interface can appear as the element type of a
 * {@code List} nested inside an {@code Optional} (e.g. {@code Optional<List<RunwayDirection>>}); Jackson's
 * {@code contentAs}/{@code as} attributes only narrow one container level, so a property-level hint on such a
 * doubly-nested generic type cannot reach all the way down to this element type (see doc/immutables-migration.md).
 */
@JsonDeserialize(as = RunwayDirectionImpl.class)
public interface RunwayDirection {

    String getDesignator();

    Optional<Double> getTrueBearing();

    Optional<Double> getElevationTDZMeters();

    Optional<Aerodrome> getAssociatedAirportHeliport();

}