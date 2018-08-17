package fi.fmi.avi.model.metar;

import java.util.List;
import java.util.Optional;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.CloudLayer;
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
     * True, when cumulonimbus clouds or towering cumulus clouds are detected by the automatic observing system but
     * the cloud amount cannot be observed due to a sensor or system failure ('///015' etc. in TAC).
     *
     * @return true the amount could not be detected
     */
    boolean isAmountUnobservableByAutoSystem();

    /**
     * True, when cumulonimbus clouds or towering cumulus clouds are detected by the automatic observing system,
     * and the automated system is working properly, but did not observe the cloud amount ('///015' etc. in TAC).
     *
     * @return true the amount could not be detected
     */
    boolean isAmountNotDetectedCloudsDetectedByAutoSystem();

    /**
     * True, when cumulonimbus clouds or towering cumulus clouds are detected by the automatic observing system
     * and the height of cloud base cannot be observed due to a sensor or system failure ('BKN///' etc. in TAC).
     *
     * @return true the height could not be detected
     */
    boolean isHeightUnobservableByAutoSystem();

    /**
     * True, when cumulonimbus clouds or towering cumulus clouds are detected by the automatic observing system and
     * if the automated system is working properly but did not observe the cloud base height ('BKN///' etc. in TAC).
     *
     * @return true the height could not be detected
     */
    boolean isHeightNotDetectedCloudsDetectedByAutoSystem();

    /**
     * True, when the cloud type cannot be observed by an auto system due to sensor limitations
     * ('BKN015///' etc. in TAC).
     *
     * @return true if type could not be detected
     */
    boolean isCloudTypeUnobservableByAutoSystem();

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

    Optional<List<CloudLayer>> getLayers();

}
