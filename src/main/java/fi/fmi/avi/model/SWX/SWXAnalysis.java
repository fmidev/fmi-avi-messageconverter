package fi.fmi.avi.model.SWX;

import fi.fmi.avi.model.PhenomenonGeometryWithHeight;

public interface SWXAnalysis {
    Type getAnalysisType();

    PhenomenonGeometryWithHeight getAnalysis();

    enum Type {
        FORECAST, OBSERVATION
    }
}
