package fi.fmi.avi.model.sigmet;

import fi.fmi.avi.model.BulletinHeading;

public interface SIGMETBulletinHeading extends BulletinHeading {
    @Deprecated
    default WarningsDataTypeDesignatorT2 getSIGMETType() {
        return getDataTypeDesignatorT2();
    }

    @Override
    WarningsDataTypeDesignatorT2 getDataTypeDesignatorT2();

    @Override
    default DataTypeDesignatorT1 getDataTypeDesignatorT1ForTAC() {
        return DataTypeDesignatorT1.WARNINGS;
    }

    @Deprecated
    enum SIGMETType {TROPICAL_CYCLONE, VOLCANIC_ASH, SEVERE_WEATHER}
}
