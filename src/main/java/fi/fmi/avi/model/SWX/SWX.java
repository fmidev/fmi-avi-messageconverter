package fi.fmi.avi.model.SWX;

import java.util.List;
import java.util.Optional;

import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.model.PhenomenonGeometryWithHeight;

public interface SWX extends AviationWeatherMessage {
    AdvisoryNumber getAdvisoryNumber();

    Optional<AdvisoryNumber> getReplacementAdvisoryNumber();

    Optional<STATUS> getStatus();

    List<String> getWeatherEffects();

    Optional<PhenomenonGeometryWithHeight> getObservation();

    List<PhenomenonGeometryWithHeight> getForecasts();

    NextAdvisory getNextAdvisory();

    enum STATUS {
        TEST, EXER
    }
}
