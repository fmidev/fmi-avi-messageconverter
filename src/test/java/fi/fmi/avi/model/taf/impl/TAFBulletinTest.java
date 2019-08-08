package fi.fmi.avi.model.taf.impl;

import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Ignore;
import org.junit.Test;

import fi.fmi.avi.JSONTestUtil;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.bulletin.BulletinHeading;
import fi.fmi.avi.model.bulletin.DataTypeDesignatorT1;
import fi.fmi.avi.model.bulletin.DataTypeDesignatorT2;
import fi.fmi.avi.model.bulletin.immutable.BulletinHeadingImpl;
import fi.fmi.avi.model.immutable.AerodromeImpl;
import fi.fmi.avi.model.immutable.GeoPositionImpl;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.immutable.TAFBulletinImpl;
import fi.fmi.avi.model.taf.immutable.TAFImpl;

public class TAFBulletinTest {

    @Test
    public void testTAFBulletinLengthConsistentLong() throws Exception {
        final TAF t = JSONTestUtil.readFromJSON(this.getClass().getResourceAsStream("taf1.json"), TAFImpl.class);
        final AerodromeImpl.Builder airportBuilder = AerodromeImpl.builder()//
                .setDesignator("EETN")//
                .setName("Vaasa Airport")//
                .setFieldElevationValue(19.0)//
                .setLocationIndicatorICAO("EFVA")//
                .setReferencePoint(GeoPositionImpl.builder()//
                        .setCoordinateReferenceSystemId("http://www.opengis.net/def/crs/EPSG/0/4326")//
                        .addCoordinates(21.762199401855, 63.050701141357)//
                        .setElevationValue(19.0)//
                        .setElevationUom("m")//
                        .build());
        final TAFImpl.Builder tafBuilder = TAFImpl.immutableCopyOf(t).toBuilder();
        tafBuilder.setAerodrome(airportBuilder.build())//
                .withCompleteIssueTime(YearMonth.of(2017, 7))//
                .withCompleteForecastTimes(YearMonth.of(2017, 7), 27, 11, ZoneId.of("Z"))//
                .setTranslationTime(ZonedDateTime.now());

        final TAFBulletinImpl.Builder bulletinBuilder = TAFBulletinImpl.builder()//
                .setHeading(BulletinHeadingImpl.builder()//
                        .setType(BulletinHeading.Type.NORMAL)//
                        .setGeographicalDesignator("FI")//
                        .setLocationIndicator("EFKL")//
                        .setBulletinNumber(31)//
                        .setDataTypeDesignatorT1ForTAC(DataTypeDesignatorT1.FORECASTS)
                        .setDataTypeDesignatorT2(DataTypeDesignatorT2.ForecastsDataTypeDesignatorT2.FCT_AERODROME_VT_LONG)//
                        .setIssueTime(PartialOrCompleteTimeInstant.of(ZonedDateTime.now()))//
                        .build());
        bulletinBuilder.addMessages(tafBuilder.build());
        bulletinBuilder.build();
    }

    //Check for TAF length removed, so ignoring the test
    @Ignore
    @Test(expected = IllegalStateException.class)
    public void testTAFBulletinLengthInconsistentLong() throws Exception {
        final TAF t = JSONTestUtil.readFromJSON(this.getClass().getResourceAsStream("taf1.json"), TAFImpl.class);
        final AerodromeImpl.Builder airportBuilder = AerodromeImpl.builder()//
                .setDesignator("EETN")//
                .setName("Vaasa Airport")//
                .setFieldElevationValue(19.0)//
                .setLocationIndicatorICAO("EFVA")//
                .setReferencePoint(GeoPositionImpl.builder()//
                        .setCoordinateReferenceSystemId("http://www.opengis.net/def/crs/EPSG/0/4326")//
                        .addCoordinates(21.762199401855, 63.050701141357)//
                        .setElevationValue(19.0)//
                        .setElevationUom("m")//
                        .build());
        final TAFImpl.Builder tafBuilder = TAFImpl.immutableCopyOf(t).toBuilder();
        tafBuilder.setAerodrome(airportBuilder.build())//
                .withCompleteIssueTime(YearMonth.of(2017, 7))//
                .withCompleteForecastTimes(YearMonth.of(2017, 7), 27, 11, ZoneId.of("Z"))//
                .setTranslationTime(ZonedDateTime.now());

        final TAFBulletinImpl.Builder bulletinBuilder = TAFBulletinImpl.builder()//
                .setHeading(BulletinHeadingImpl.builder()//
                        .setType(BulletinHeading.Type.NORMAL)//
                        .setGeographicalDesignator("FI")//
                        .setLocationIndicator("EFKL")//
                        .setBulletinNumber(31)//
                        .setDataTypeDesignatorT1ForTAC(DataTypeDesignatorT1.FORECASTS)
                        .setDataTypeDesignatorT2(DataTypeDesignatorT2.ForecastsDataTypeDesignatorT2.FCT_AERODROME_VT_SHORT)//
                        .setIssueTime(PartialOrCompleteTimeInstant.of(ZonedDateTime.now()))//
                        .build());
        bulletinBuilder.addMessages(tafBuilder.build());
        bulletinBuilder.build();
    }

}
