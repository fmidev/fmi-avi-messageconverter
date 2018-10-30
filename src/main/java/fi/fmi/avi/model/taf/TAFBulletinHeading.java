package fi.fmi.avi.model.taf;

import fi.fmi.avi.model.BulletinHeading;

public interface TAFBulletinHeading extends BulletinHeading {

    Type getType();

    enum Type {NORMAL, DELAYED, AMENDED, CORRECTED}

    boolean isValidLessThan12Hours();

}
