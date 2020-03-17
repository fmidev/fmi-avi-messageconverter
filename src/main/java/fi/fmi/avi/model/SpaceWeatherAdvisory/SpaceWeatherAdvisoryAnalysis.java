package fi.fmi.avi.model.SpaceWeatherAdvisory;

import java.util.List;
import java.util.Optional;

import javax.swing.text.html.Option;

import fi.fmi.avi.model.PhenomenonGeometryWithHeight;

public interface SpaceWeatherAdvisoryAnalysis {
    Optional<Type> getAnalysisType();

    //Optional<PhenomenonGeometryWithHeight> getAffectedArea();

    //Optional<String> getLocatioIndicator();

    Optional<List<SpaceWeatherRegion>> getRegion();

    boolean isNoPhenomenaExpected();

    boolean isNoInformationAvailable();

    enum Type {
        FORECAST, OBSERVATION
    }
}
