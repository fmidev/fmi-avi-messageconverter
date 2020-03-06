package fi.fmi.avi.model.SWX;

import java.util.Optional;

import fi.fmi.avi.model.PhenomenonGeometryWithHeight;

public interface SWXAnalysis {
    Type getAnalysisType();

    Optional<PhenomenonGeometryWithHeight> getAffectedArea();

    boolean isNoPhenomenaExpected();

    boolean isNoInformationAvailable();

    enum Type {
        FORECAST, OBSERVATION
    }
}
