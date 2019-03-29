package fi.fmi.avi.model.sigmet;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import org.locationtech.jts.geom.Geometry;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface SigmetAnalysis {
    public SigmetAnalysisType getAnalysisType();
    public Geometry getAnalysisGeometry();
    public Optional<PartialOrCompleteTimeInstant> getAnalysisTime();
    public Optional<Boolean> getAnalysisApproximateLocation();

    public Optional<NumericMeasure> getLowerLimit();
    public Optional<AviationCodeListUser.RelationalOperator> getLowerLimitOperator();

    public Optional<NumericMeasure> getUpperLimit();
    public Optional<AviationCodeListUser.RelationalOperator> getUpperLimitOperator();

    public Optional<NumericMeasure> getMovingSpeed();
    public Optional<NumericMeasure> getMovingDirection();

    public Optional<SigmetIntensityChange> getIntensityChange();

    public Optional<PartialOrCompleteTimeInstant> getForecastTime();
    public Optional<Geometry> getForecastGeometry();
    public Optional<Boolean> getForecastApproximateLocation();

    public Optional<Boolean> getNoVaExpected(); //Only applicable to ForecastPositionAnalysis
}
