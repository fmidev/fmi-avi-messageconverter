package fi.fmi.avi.model.SpaceWeatherAdvisory;

import java.util.List;
import java.util.Optional;

import fi.fmi.avi.model.AviationWeatherMessage;

public interface SpaceWeatherAdvisory extends AviationWeatherMessage {
    IssuingCenter getIssuingCenter();

    AdvisoryNumber getAdvisoryNumber();

    Optional<AdvisoryNumber> getReplaceAdvisoryNumber();

    List<String> getPhenomena();

    List<SpaceWeatherAdvisoryAnalysis> getAnalyses();

    NextAdvisory getNextAdvisory();

}
