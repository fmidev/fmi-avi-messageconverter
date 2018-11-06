package fi.fmi.avi.model.taf.impl;

import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Test;

import fi.fmi.avi.JSONTestUtil;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.immutable.AerodromeImpl;
import fi.fmi.avi.model.immutable.GeoPositionImpl;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.TAFBulletinHeading;
import fi.fmi.avi.model.taf.immutable.TAFBulletinHeadingImpl;
import fi.fmi.avi.model.taf.immutable.TAFBulletinImpl;
import fi.fmi.avi.model.taf.immutable.TAFImpl;

public class TAFBulletinTest {

    @Test
    public void testTAFBulletinLengthConsistentLong() throws Exception {
        TAF t = JSONTestUtil.readFromJSON(this.getClass().getResourceAsStream("taf1.json"), TAFImpl.class);
        AerodromeImpl.Builder airportBuilder = new AerodromeImpl.Builder().setDesignator("EETN")
                .setName("Vaasa Airport")
                .setFieldElevationValue(19.0)
                .setLocationIndicatorICAO("EFVA")
                .setReferencePoint(new GeoPositionImpl.Builder().setCoordinateReferenceSystemId("http://www.opengis.net/def/crs/EPSG/0/4326")
                        .setCoordinates(new Double[] { 21.762199401855, 63.050701141357 })
                        .setElevationValue(19.0)
                        .setElevationUom("m")
                        .build());
        TAFImpl.Builder tafBuilder = TAFImpl.immutableCopyOf(t).toBuilder();
        tafBuilder.setAerodrome(airportBuilder.build())
                .withCompleteIssueTime(YearMonth.of(2017, 7))
                .withCompleteForecastTimes(YearMonth.of(2017, 7), 27, 11, ZoneId.of("Z"))
                .setTranslationTime(ZonedDateTime.now());

        TAFBulletinImpl.Builder bulletinBuilder = new TAFBulletinImpl.Builder().setIssueTime(PartialOrCompleteTimeInstant.of(ZonedDateTime.now()))
                .setHeading(new TAFBulletinHeadingImpl.Builder().setType(TAFBulletinHeading.Type.NORMAL)
                        .setGeographicalDesignator("FI")
                        .setLocationIndicator("EFKL")
                        .setBulletinNumber(31)
                        .setValidLessThan12Hours(false)
                        .build());
        bulletinBuilder.addMessages(tafBuilder.build());
        bulletinBuilder.build();
    }

    @Test(expected = IllegalStateException.class)
    public void testTAFBulletinLengthInconsistentLong() throws Exception {
        TAF t = JSONTestUtil.readFromJSON(this.getClass().getResourceAsStream("taf1.json"), TAFImpl.class);
        AerodromeImpl.Builder airportBuilder = new AerodromeImpl.Builder().setDesignator("EETN")
                .setName("Vaasa Airport")
                .setFieldElevationValue(19.0)
                .setLocationIndicatorICAO("EFVA")
                .setReferencePoint(new GeoPositionImpl.Builder().setCoordinateReferenceSystemId("http://www.opengis.net/def/crs/EPSG/0/4326")
                        .setCoordinates(new Double[] { 21.762199401855, 63.050701141357 })
                        .setElevationValue(19.0)
                        .setElevationUom("m")
                        .build());
        TAFImpl.Builder tafBuilder = TAFImpl.immutableCopyOf(t).toBuilder();
        tafBuilder.setAerodrome(airportBuilder.build())
                .withCompleteIssueTime(YearMonth.of(2017, 7))
                .withCompleteForecastTimes(YearMonth.of(2017, 7), 27, 11, ZoneId.of("Z"))
                .setTranslationTime(ZonedDateTime.now());

        TAFBulletinImpl.Builder bulletinBuilder = new TAFBulletinImpl.Builder().setIssueTime(PartialOrCompleteTimeInstant.of(ZonedDateTime.now()))
                .setHeading(new TAFBulletinHeadingImpl.Builder().setType(TAFBulletinHeading.Type.NORMAL)
                        .setGeographicalDesignator("FI")
                        .setLocationIndicator("EFKL")
                        .setBulletinNumber(31)
                        .setValidLessThan12Hours(true)
                        .build());
        bulletinBuilder.addMessages(tafBuilder.build());
        bulletinBuilder.build();
    }

}
