package fi.fmi.avi.model.SpaceWeatherAdvisory;

import java.util.List;
import java.util.Optional;

import fi.fmi.avi.model.AviationWeatherMessage;

public interface SpaceWeatherAdvisory extends AviationWeatherMessage {
    IssuingCenter getIssuingCenter();

    AdvisoryNumber getAdvisoryNumber();

    Optional<AdvisoryNumber> getReplaceAdvisoryNumber();

    Optional<Status> getStatus();

    List<String> getPhenomena();

    List<SpaceWeatherAdvisoryAnalysis> getAnalyses();

    NextAdvisory getNextAdvisory();

    enum Status {
        TEST, EXERCISE
    }
}
