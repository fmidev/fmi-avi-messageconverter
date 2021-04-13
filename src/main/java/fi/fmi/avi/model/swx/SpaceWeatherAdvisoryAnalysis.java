package fi.fmi.avi.model.swx;

import java.util.List;
import java.util.Optional;

import fi.fmi.avi.model.PartialOrCompleteTimeInstant;

public interface SpaceWeatherAdvisoryAnalysis {
    PartialOrCompleteTimeInstant getTime();

    Type getAnalysisType();

    List<SpaceWeatherRegion> getRegions();

    Optional<NilPhenomenonReason> getNilPhenomenonReason();

    enum NilPhenomenonReason {
        NO_PHENOMENON_EXPECTED, NO_INFORMATION_AVAILABLE
    }

    enum Type {
        FORECAST, OBSERVATION
    }
}
