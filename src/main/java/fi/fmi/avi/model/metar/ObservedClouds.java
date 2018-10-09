package fi.fmi.avi.model.metar;

import java.util.List;
import java.util.Optional;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.NumericMeasure;

public interface ObservedClouds extends AviationCodeListUser {

    /**
     * True, when the result of a successful could observation is
     * that there are is no cloud of operational significance,
     * no restriction on vertical visibility and 'CAVOK' is not appropriate
     * ('NSC' in TAC).
     *
     * @return true if no significant cloud is present
     */
    boolean isNoSignificantCloud();

    /**
     * True, when no clouds are detected by an automatic observing system ('NCD' in TAC).
     *
     * @return true if no clouds could be automatically detected
     */
    boolean isNoCloudsDetectedByAutoSystem();

    /**
     * True, when the sky is obscured and the value of the vertical visibility cannot be determined by the
     * automatic observing system due to a temporary failure of the system/sensor.
     *
     * @return true if vertical visibility could not be detected
     */
    boolean isVerticalVisibilityUnobservableByAutoSystem();

    /**
     * @return
     */
    Optional<NumericMeasure> getVerticalVisibility();

    Optional<List<ObservedCloudLayer>> getLayers();

}
