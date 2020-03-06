package fi.fmi.avi.model.SWX;

import java.util.List;
import java.util.Optional;

import fi.fmi.avi.model.AviationWeatherMessage;

public interface SWX extends AviationWeatherMessage {
    String getIssuingCenterName();

    AdvisoryNumber getAdvisoryNumber();

    Optional<AdvisoryNumber> getReplacementAdvisoryNumber();

    Optional<STATUS> getStatus();

    List<String> getPhenomena();

    List<SWXAnalysis> getAnalyses();

    NextAdvisory getNextAdvisory();

    enum STATUS {
        TEST, EXER
    }
}
