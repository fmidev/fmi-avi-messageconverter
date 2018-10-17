package fi.fmi.avi.model;

import java.util.Optional;

public interface BulletinHeading {

    /**
     * This corresponds to the 'CCCC' part of the abbreviated heading
     *
     * @return
     */
    String getLocationIndicator();

    /**
     * This corresponds to the 'A<sub>1</sub>A<sub>2</sub>' part of the abbreviated heading
     *
     * @return
     */
    String getGeographicalDesignator();

    /**
     * This corresponds to the 'ii' number of the abbreviated heading
     *
     * @return
     */
    int getBulletinNumber();

    /**
     * Corresponds to the 'RRx' part of the abbreviated heading
     *
     * @return
     */
    boolean isContainingDelayedMessages();

    /**
     * Corresponds to the A-Z letter 'x' of the RRx, AAx or CCx part of the abbreviated heading
     *
     * @return
     */
    Optional<Integer> getBulletinAugmentationNumber();

}
