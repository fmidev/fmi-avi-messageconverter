package fi.fmi.avi.converter;

import static junit.framework.TestCase.assertTrue;

import java.io.InputStream;
import java.util.Objects;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.unitils.thirdparty.org.apache.commons.io.IOUtils;

import fi.fmi.avi.converter.json.JSONConverterTest;
import fi.fmi.avi.converter.json.JSONTestConfiguration;
import fi.fmi.avi.converter.json.conf.JSONConverter;
import fi.fmi.avi.model.immutable.AerodromeImpl;
import fi.fmi.avi.model.immutable.GeoPositionImpl;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.TAFBulletin;
import fi.fmi.avi.model.taf.immutable.TAFImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JSONTestConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class ConversionChainTest {

    @Autowired
    private AviMessageConverter converter;

    @Test
    public void testSimpleChain() throws Exception {
        final InputStream is = JSONConverterTest.class.getResourceAsStream("taf1.json");
        Objects.requireNonNull(is);
        final String input = IOUtils.toString(is, "UTF-8");
        is.close();
        ConversionResult<String> result = new ConversionChainBuilder<>(this.converter, JSONConverter.JSON_STRING_TO_TAF_POJO)
                .withMutator(TAFImpl.Builder::from, TAF.class, TAFImpl.Builder.class)
                .withMutator(tafBuilder -> tafBuilder
                                .setAerodrome(AerodromeImpl.Builder.from(tafBuilder.getAerodrome())
                                        .setDesignator("EETN")
                                        .setName("Tallinn Airport")
                                        .setFieldElevationValue(40.0)
                                        .setLocationIndicatorICAO("EETN")
                                        .setReferencePoint(GeoPositionImpl.builder()
                                                .setCoordinateReferenceSystemId("http://www.opengis.net/def/crs/EPSG/0/4326")
                                                .addCoordinates(24.8325, 59.413333)
                                                .build())
                                        .build())
                                .build(), TAFImpl.Builder.class, TAF.class)
                .withConversionStep(JSONConverter.TAF_POJO_TO_JSON_STRING)//
                .withConversionStep(JSONConverter.JSON_STRING_TO_TAF_POJO)//
                .build(JSONConverter.TAF_POJO_TO_JSON_STRING)//
                .convertMessage(input, ConversionHints.EMPTY);
        assertTrue(ConversionResult.Status.SUCCESS == result.getStatus());
        assertTrue(result.getConvertedMessage().isPresent());
        assertTrue(result.getConvertedMessage().get().contains("\"name\" : \"Foo\""));
    }

    @Test
    public void testBulletinExtractionChain() throws Exception {
        final InputStream is = JSONConverterTest.class.getResourceAsStream("tafBulletin1.json");
        Objects.requireNonNull(is);
        final String input = IOUtils.toString(is,"UTF-8");
        is.close();
        ConversionResult<String> result = new ConversionChainBuilder<>(this.converter, JSONConverter.JSON_STRING_TO_TAF_BULLETIN_POJO)
                .withMutator(bulletin -> bulletin.getMessages().get(0),TAFBulletin.class, TAF.class)
                .build(JSONConverter.TAF_POJO_TO_JSON_STRING)//
                .convertMessage(input, ConversionHints.EMPTY);
        assertTrue(ConversionResult.Status.SUCCESS == result.getStatus());
        assertTrue(result.getConvertedMessage().isPresent());
    }

}
