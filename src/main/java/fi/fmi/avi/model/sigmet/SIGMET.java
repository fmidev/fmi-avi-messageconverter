package fi.fmi.avi.model.sigmet;

import fi.fmi.avi.model.PhenomenonGeometry;
import fi.fmi.avi.model.SIGMETAIRMET;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.sigmet.immutable.SIGMETImpl;

/**
 * The class-level {@link JsonDeserialize} hint is needed because this interface can appear as the element type of a
 * {@code List} property (e.g. {@code SIGMETBulletin.getMessages(): List<SIGMET>}); a per-property {@code contentAs}
 * hint placed on an overridden {@code addAllX(...)}/{@code setX(...)} method is not reliably picked up by Jackson
 * for builder-style deserialization of such properties (see docs/07-modernization-plan.md).
 */
@JsonDeserialize(as = SIGMETImpl.class)
public interface SIGMET extends SIGMETAIRMET {

    SigmetPhenomenonType getPhenomenonType();

    Optional<AeronauticalSignificantWeatherPhenomenon> getPhenomenon();

    Optional<Reference> getCancelledReference();

    Optional<List<PhenomenonGeometry>> getForecastGeometries();

    Optional<VAInfo> getVAInfo(); // If this is present this is a VASigmet
}
