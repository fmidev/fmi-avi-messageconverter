package fi.fmi.avi.model.taf;

import fi.fmi.avi.model.BulletinHeading;

public interface TAFBulletinHeading extends BulletinHeading {

    boolean isContainingAmendedMessages();

    boolean isContainingCorrectedMessages();

    boolean isValidLessThan12Hours();

}
