package fi.fmi.avi.model.taf;

import fi.fmi.avi.model.BulletinHeading;

public interface TAFBulletinHeading extends BulletinHeading {

    boolean isValidLessThan12Hours();

}
