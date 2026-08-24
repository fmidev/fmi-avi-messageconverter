package fi.fmi.avi.model.swx.amd82;

import fi.fmi.avi.model.AviationWeatherMessage;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.swx.amd82.immutable.SpaceWeatherAdvisoryAmd82Impl;

/**
 * The class-level {@link JsonDeserialize} hint is needed because this interface can appear as the element type of a
 * {@code List} property (e.g. {@code SpaceWeatherAmd82Bulletin.getMessages()}); a per-property {@code contentAs}
 * hint placed on an overridden {@code addAllX(...)}/{@code setX(...)} method is not reliably picked up by Jackson
 * for builder-style deserialization of such properties (see docs/07-modernization-plan.md).
 */
@JsonDeserialize(as = SpaceWeatherAdvisoryAmd82Impl.class)
public interface SpaceWeatherAdvisoryAmd82 extends AviationWeatherMessage {
    IssuingCenter getIssuingCenter();

    Effect getEffect();

    AdvisoryNumber getAdvisoryNumber();

    List<AdvisoryNumber> getReplaceAdvisoryNumbers();

    List<SpaceWeatherAdvisoryAnalysis> getAnalyses();

    NextAdvisory getNextAdvisory();
}
