package fi.fmi.avi.model.sigmet;

import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.SIGMETAIRMET;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.sigmet.immutable.AIRMETImpl;

/**
 * The class-level {@link JsonDeserialize} hint is needed because this interface can appear as the element type of a
 * {@code List} property (e.g. {@code AIRMETBulletin.getMessages(): List<AIRMET>}); a per-property {@code contentAs}
 * hint placed on an overridden {@code addAllX(...)}/{@code setX(...)} method is not reliably picked up by Jackson
 * for builder-style deserialization of such properties (see docs/07-modernization-plan.md).
 */
@JsonDeserialize(as = AIRMETImpl.class)
public interface AIRMET extends SIGMETAIRMET {
    Optional<AeronauticalAirmetWeatherPhenomenon> getPhenomenon();

    Optional<AirmetCloudLevels> getCloudLevels();

    Optional<AirmetWind> getWind();

    Optional<List<WeatherCausingVisibilityReduction>> getObscuration();

    Optional<NumericMeasure> getVisibility();

    Optional<Reference> getCancelledReference();

}
