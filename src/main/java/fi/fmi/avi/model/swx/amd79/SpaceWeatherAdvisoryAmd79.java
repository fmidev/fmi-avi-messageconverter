package fi.fmi.avi.model.swx.amd79;

import fi.fmi.avi.model.AviationWeatherMessage;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.swx.amd79.immutable.SpaceWeatherAdvisoryAmd79Impl;

/**
 * The class-level {@link JsonDeserialize} hint is needed because this interface can appear as the element type of a
 * {@code List} property (e.g. {@code SpaceWeatherAmd79Bulletin.getMessages()}); a per-property {@code contentAs}
 * hint placed on an overridden {@code addAllX(...)}/{@code setX(...)} method is not reliably picked up by Jackson
 * for builder-style deserialization of such properties (see docs/07-modernization-plan.md).
 */
@JsonDeserialize(as = SpaceWeatherAdvisoryAmd79Impl.class)
public interface SpaceWeatherAdvisoryAmd79 extends AviationWeatherMessage {
    IssuingCenter getIssuingCenter();

    AdvisoryNumber getAdvisoryNumber();

    Optional<AdvisoryNumber> getReplaceAdvisoryNumber();

    List<SpaceWeatherPhenomenon> getPhenomena();

    List<SpaceWeatherAdvisoryAnalysis> getAnalyses();

    NextAdvisory getNextAdvisory();
}
