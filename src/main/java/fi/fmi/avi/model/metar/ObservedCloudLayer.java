package fi.fmi.avi.model.metar;

import fi.fmi.avi.model.CloudLayer;

public interface ObservedCloudLayer extends CloudLayer {

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
    boolean isAmountNotDetectedByAutoSystem();

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
    boolean isHeightNotDetectedByAutoSystem();

    /**
     * True, when the cloud type cannot be observed by an auto system due to sensor limitations
     * ('BKN015///' etc. in TAC).
     *
     * @return true if type could not be detected
     */
    boolean isCloudTypeUnobservableByAutoSystem();

}
