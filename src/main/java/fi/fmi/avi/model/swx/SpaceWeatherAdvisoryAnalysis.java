package fi.fmi.avi.model.swx;

import java.util.List;
import java.util.Optional;

import fi.fmi.avi.model.AviationWeatherMessageOrCollection;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;

public interface SpaceWeatherAdvisoryAnalysis extends AviationWeatherMessageOrCollection {
    PartialOrCompleteTimeInstant getTime();

    //TODO/FIXME: are there any cases where we would not have the type?
    Optional<Type> getAnalysisType();

    Optional<List<SpaceWeatherRegion>> getRegion();

    boolean isNoPhenomenaExpected();

    boolean isNoInformationAvailable();

    enum Type {
        FORECAST, OBSERVATION
    }
}
