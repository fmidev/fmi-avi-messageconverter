package fi.fmi.avi.model.swx.amd82;

import fi.fmi.avi.model.PartialOrCompleteTimeInstant;

import java.util.List;
import java.util.Optional;

public interface SpaceWeatherAdvisoryAnalysis {
    PartialOrCompleteTimeInstant getTime();

    Type getAnalysisType();

    List<SpaceWeatherIntensityAndRegion> getIntensityAndRegions();

    Optional<NilReason> getNilReason();

    enum NilReason {
        NO_SWX_EXPECTED, NO_INFORMATION_AVAILABLE
    }

    enum Type {
        FORECAST, OBSERVATION
    }
}
