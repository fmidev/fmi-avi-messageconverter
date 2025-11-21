package fi.fmi.avi.model.swx.amd82;

import fi.fmi.avi.model.AviationWeatherMessage;

import java.util.List;

public interface SpaceWeatherAdvisoryAmd82 extends AviationWeatherMessage {
    IssuingCenter getIssuingCenter();

    Effect getEffect();

    AdvisoryNumber getAdvisoryNumber();

    List<AdvisoryNumber> getReplaceAdvisoryNumbers();

    List<SpaceWeatherAdvisoryAnalysis> getAnalyses();

    NextAdvisory getNextAdvisory();
}
