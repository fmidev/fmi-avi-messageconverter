package fi.fmi.avi.model.SpaceWeatherAdvisory;

import java.util.Optional;

import fi.fmi.avi.model.PhenomenonGeometryWithHeight;

public interface SpaceWeatherAdvisoryAnalysis {
    Type getAnalysisType();

    Optional<PhenomenonGeometryWithHeight> getAffectedArea();

    boolean isNoPhenomenaExpected();

    boolean isNoInformationAvailable();

    enum Type {
        FORECAST, OBSERVATION
    }
}
