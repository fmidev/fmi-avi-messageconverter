package fi.fmi.avi.model.taf;

import fi.fmi.avi.model.BulletinHeading;

public interface TAFBulletinHeading extends BulletinHeading {

    @Deprecated
    default boolean isValidLessThan12Hours() {
        return getDataTypeDesignatorT2() == ForecastsDataTypeDesignatorT2.AERODROME_VT_SHORT;
    }

    @Override
    default DataTypeDesignatorT1 getDataTypeDesignatorT1ForTAC() {
        return DataTypeDesignatorT1.FORECASTS;
    }

    @Override
    ForecastsDataTypeDesignatorT2 getDataTypeDesignatorT2();

}
