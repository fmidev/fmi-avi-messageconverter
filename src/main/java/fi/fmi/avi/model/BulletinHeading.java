package fi.fmi.avi.model;

import java.util.Optional;

public interface BulletinHeading {

    /**
     * Type of the content (AAx, RRx, CCx) part of the abbreviated heading
     *
     * @return the type of the content
     */
    Type getType();

    enum Type {NORMAL, DELAYED, AMENDED, CORRECTED}


    /**
     * This corresponds to the 'CCCC' part of the abbreviated heading
     *
     * @return the indicator
     */
    String getLocationIndicator();

    /**
     * This corresponds to the 'A<sub>1</sub>A<sub>2</sub>' part of the abbreviated heading
     *
     * @return the designator
     */
    String getGeographicalDesignator();

    /**
     * This corresponds to the 'ii' number of the abbreviated heading
     *
     * @return the number
     */
    int getBulletinNumber();

    /**
     * Corresponds to the A-Z letter 'x' of the RRx, AAx or CCx part of the abbreviated heading
     *
     * @return the augmentation number
     */
    Optional<Integer> getBulletinAugmentationNumber();

}
