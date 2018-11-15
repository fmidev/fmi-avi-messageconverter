package fi.fmi.avi.model.sigmet;

import fi.fmi.avi.model.BulletinHeading;

public interface SIGMETBulletinHeading extends BulletinHeading {
    SIGMETType getSIGMETType();

    enum SIGMETType {TROPICAL_CYCLONE, VOLCANIC_ASH, SEVERE_WEATHER}

}
