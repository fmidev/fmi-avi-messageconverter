package fi.fmi.avi.model.swx.amd79;

import fi.fmi.avi.model.AviationWeatherMessage;

import java.util.List;
import java.util.Optional;

public interface SpaceWeatherAdvisoryAmd79 extends AviationWeatherMessage {
    IssuingCenter getIssuingCenter();

    AdvisoryNumber getAdvisoryNumber();

    Optional<AdvisoryNumber> getReplaceAdvisoryNumber();

    List<SpaceWeatherPhenomenon> getPhenomena();

    List<SpaceWeatherAdvisoryAnalysis> getAnalyses();

    NextAdvisory getNextAdvisory();
}
