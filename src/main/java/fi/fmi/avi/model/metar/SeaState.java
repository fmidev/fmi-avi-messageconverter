package fi.fmi.avi.model.metar;

import java.util.Optional;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.NumericMeasure;

public interface SeaState extends AviationCodeListUser {

    boolean isSeaSurfaceTemperatureUnobservableByAutoSystem();

    Optional<NumericMeasure> getSeaSurfaceTemperature();

    Optional<NumericMeasure> getSignificantWaveHeight();

    Optional<SeaSurfaceState> getSeaSurfaceState();

}
