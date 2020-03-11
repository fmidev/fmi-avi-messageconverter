package fi.fmi.avi.model.SpaceWeatherAdvisory;

import java.util.List;
import java.util.Optional;

import fi.fmi.avi.model.AviationWeatherMessage;

public interface SpaceWeatherAdvisory extends AviationWeatherMessage {
    String getIssuingCenterName();

    AdvisoryNumber getAdvisoryNumber();

    Optional<AdvisoryNumber> getReplaceAdvisoryNumber();

    Optional<STATUS> getStatus();

    List<String> getPhenomena();

    List<SpaceWeatherAdvisoryAnalysis> getAnalyses();

    NextAdvisory getNextAdvisory();

    enum STATUS {
        TEST, EXER
    }
}
