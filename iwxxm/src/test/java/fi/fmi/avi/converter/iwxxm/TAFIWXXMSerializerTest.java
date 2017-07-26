package fi.fmi.avi.converter.iwxxm;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.fasterxml.jackson.databind.ObjectMapper;

import fi.fmi.avi.converter.AviMessageConverter;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.converter.iwxxm.conf.IWXXMConverterConfig;
import fi.fmi.avi.model.Aerodrome;
import fi.fmi.avi.model.GeoPosition;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.impl.TAFImpl;

/**
 * Created by rinne on 19/07/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = IWXXMConverterConfig.class, loader = AnnotationConfigContextLoader.class)
public class TAFIWXXMSerializerTest {

    @Autowired
    private AviMessageConverter converter;

    @Test
    public void testStringSerialization() throws Exception {
        assertTrue(converter.isSpecificationSupported(IWXXMConverterConfig.TAF_POJO_TO_IWXXM21_STRING));
        TAF t = readFromJSON("taf12.json");
        Aerodrome airport = new Aerodrome();

        airport.setDesignator("EFVA");
        airport.setName("Vaasa Airport");
        airport.setFieldElevation(6.0);
        airport.setLocationIndicatorICAO("EFVA");
        GeoPosition refPoint = new GeoPosition("http://www.opengis.net/def/crs/EPSG/0/4326", 21.764167,63.045278);
        refPoint.setElevationValue(6.0);
        refPoint.setElevationUom("m");
        airport.setReferencePoint(refPoint);
        
        t.amendAerodromeInfo(airport);

        //Partial: 301130Z
        ZonedDateTime issueTime = ZonedDateTime.of(2017,7,29,21,0,0,0, ZoneId.of("Z"));
        t.amendTimeReferences(issueTime);

        ConversionResult<String> result = converter.convertMessage(t, IWXXMConverterConfig.TAF_POJO_TO_IWXXM21_STRING);
        System.out.println(result.getConversionIssues().toString());
        assertTrue(ConversionResult.Status.SUCCESS == result.getStatus());

        //TODO: XPAth query based content asserts
        System.out.println(result.getConvertedMessage());
    }

    protected TAF readFromJSON(String fileName) throws IOException {
        ObjectMapper om = new ObjectMapper();
        InputStream is = TAFIWXXMSerializerTest.class.getResourceAsStream(fileName);
        if (is != null) {
            return om.readValue(is, TAFImpl.class);
        } else {
            throw new FileNotFoundException("Resource '" + fileName + "' could not be loaded");
        }
    }
}
