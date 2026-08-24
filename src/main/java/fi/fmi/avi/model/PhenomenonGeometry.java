package fi.fmi.avi.model;

import java.util.Optional;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.immutable.PhenomenonGeometryImpl;

/**
 * The class-level {@link JsonDeserialize} hint is needed because this interface can appear as the element type of a
 * {@code List} nested inside an {@code Optional} (e.g. {@code Optional<List<PhenomenonGeometry>>}); Jackson's
 * {@code contentAs}/{@code as} attributes only narrow one container level, so a property-level hint on such a
 * doubly-nested generic type cannot reach all the way down to this element type (see docs/07-modernization-plan.md).
 */
@JsonDeserialize(as = PhenomenonGeometryImpl.class)
public interface PhenomenonGeometry {
    Optional<TacOrGeoGeometry> getGeometry();
    Optional<PartialOrCompleteTimeInstant> getTime();
    Optional<Boolean> getApproximateLocation();
    Optional<Boolean> getNoVolcanicAshExpected();
}
