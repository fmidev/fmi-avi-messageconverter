# fmi-avi-messageconverter
The main module for aviation weather message conversions between different formats.

This project only includes conversions between the internal JSON serialisation and the 
Java POJO files. For other formats like the TAC code and IWXXM it needs one or more conversion modules providing parsing 
and serializing implementations between the aviation weather message model objects 
(in the sub packages of fi.fmi.avi.model) and the specific message formats.

## Get started
If you only need to convert between the JSON format and Java POJOs, you are ready to go
without additional dependencies.

For parsing/serialization functionality for addditional formats, use the specific Maven project, such as
[fmi-avi-messageconverter-tac](https://github.com/fmidev/fmi-avi-messageconverter-tac) and 
[fmi-avi-messageconverter-iwxxm](https://github.com/fmidev/fmi-avi-messageconverter-iwxxm). For 
conversions between two formats, such as TAC to IWXXM, include both, and configure the 
AviMessageConverter class to handle all the necessary formats. See Configuring converter 
below for more details.

These projects is available as maven dependencies in the FMI OS maven repository. To access them, 
add this repository to your project pom, or in your settings:

```xml
<repositories>
  <repository>
    <id>fmi-os-mvn-release-repo</id>
    <url>https://raw.githubusercontent.com/fmidev/fmi-os-mvn-repo/master</url>
    <snapshots>
      <enabled>false</enabled>
    </snapshots>
    <releases>
      <enabled>true</enabled>
      <updatePolicy>daily</updatePolicy>
    </releases>
  </repository>
</repositories>
``` 

For the copy-paste maven dependency blocks, see the Readme of each conversion module.

Once the converter has been configured to support the wanted conversion specifications, running 
the conversion is straight-forward:



```java
String tac = "TAF EFAB 190815Z 1909/1915 14008G15MPS 9999 BKN010 BKN015=";
ConversionResult<TAF> result = converter.convertMessage(tac,TACConverter.TAC_TO_TAF_POJO);
 if (ConversionResult.Status.SUCCESS = result.getStatus()) {
   Optional<TAF> pojo = result.getConvertedMessage();}
```

To create a custom conversion chain from one format to another, just combine two or more conversions:

```java
String tac = "TAF EETN 190815Z 1909/1915 14008G15MPS 9999 BKN010 BKN015=";
ConversionResult<TAFImpl> result1 = converter.convertMessage(tac, TACConverter.TAC_TO_IMMUTABLE_TAF_POJO);
if (ConversionResult.Status.SUCCESS == result1.getStatus()) {
    Optional<TAFImpl> msg = result1.getConvertedMessage();
    if (msg.isPresent()) {
        //Add aerodrome and full time info missing from TAC:
        TAFImpl tafPojo = msg.get().toBuilder()
                .setAerodrome(new AerodromeImpl.Builder()
                        .setDesignator("EETN")
                        .setName("Tallinn Airport")
                        .setFieldElevationValue(40.0)
                        .setLocationIndicatorICAO("EETN")
                        .setReferencePoint(new GeoPositionImpl.Builder()
                                .setCoordinateReferenceSystemId("http://www.opengis.net/def/crs/EPSG/0/4326")
                                .setCoordinates(new Double[]{24.8325, 59.413333})
                                .build())
                        .build())
                .withCompleteIssueTime(YearMonth.of(2017, 7))
                .withCompleteForecastTimes(YearMonth.of(2017, 7), 19, 08, ZoneId.of("UTC"))
                .build();
        ConversionResult<String> result2 = converter.convertMessage(tafPojo, IWXXMConverter.TAF_POJO_TO_IWXXM21_STRING);
        if (ConversionResult.Status.SUCCESS == result2.getStatus()) {
            Optional<String> iwxxmTAF = result2.getConvertedMessage();
            // Do something with the IWXXM TAF

        }
    }
}
```

## Supported message types
Currently, this project contains Java model classes for aviation weather message types METAR and TAF. It's anticipated 
that more message types such as SIGMET, AIRMET, SPECI will be added once the project gets more mature.

## Immutables and Builders

All the POJO messages have been implemented as immutable Java classes using [FreeBuilder](https://github.com/inferred/FreeBuilder) 
for generating most of the builder code. As the classes are immutable, there is no need to prepare for the
runtime changes of the instances of these objects (with change listeners etc.). The builder classes and methods
have been provided to help with creating modified versions of message instances in cases where it is
necessary to modify them.

The public read-only APIs of the message classes are described by the Java interfaces in packages
```fi.fmi.avi.model```, ```fi.fmi.avi.model.metar``` and ```fi.fmi.avi.model.taf```. The 
FreeBuilder-powered immutable implementations are located in "immutable" sub-packages of these packages as 
abstract classes with postfix "Impl". FreeBuilder annotation processor generates the actual 
implementation classes in the same packages (with "_Builder" postfix).

## Configuring the AviMessageConverter
The AviMessageConverter class in taught to handle new conversions by calling the method 

`setMessageSpecificConverter(ConversionSpecification<S, T>, AviMessageSpecificConverter<S, T>)`

A ConversionSpecification class instance defines the conversion from one input format to particular output format.
By convention, each of the conversion modules should include all the provided conversions as public static final
fields, such as `fi.fmi.avi.converter.tac.TACConverter.TAC_TO_TAF_POJO` or 
`fi.fmi.avi.converter.iwxxm.IWXXMConverter.TAF_POJO_TO_IWXXM21_DOM` used in the examples above.

The recommended way of configuring the converter is using Spring Java configuration:

```java
package my.stuff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.w3c.dom.Document;

import fi.fmi.avi.converter.AviMessageConverter;
import fi.fmi.avi.converter.AviMessageSpecificConverter;

import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.metar.METAR;

@Configuration
@Import(fi.fmi.avi.converter.json.conf.JSONConverter.class)
@Import(fi.fmi.avi.converter.tac.conf.TACConverter.class)
@Import(fi.fmi.avi.converter.iwxxm.conf.IWXXMConverter.class)
public class MyMessageConverterConfig {
    
    @Autowired
    @Qualifier("metarJSONSerializer")
    private AviMessageSpecificConverter<METAR, String> metarJSONSerializer;
    
    @Autowired
    @Qualifier("tafJSONSerializer")
    private AviMessageSpecificConverter<TAF, String> tafJSONSerializer();

    @Autowired
    @Qualifier("tafJSONParser")
    private AviMessageSpecificConverter<String, TAF> tafJSONParser;
    
    @Autowired
    @Qualifier("metarJSONParser")
    private AviMessageSpecificConverter<String, METAR> metarJSONParser;

    @Autowired
    @Qualifier("metarTACParser")
    private AviMessageSpecificConverter<String, METAR> metarTACParser;
    
    @Autowired
    @Qualifier("tafTACParser")
    private AviMessageSpecificConverter<String, TAF> tafTACParser;
    
    @Autowired
    @Qualifier("metarTACSerializer")
    private AviMessageSpecificConverter<METAR, String> metarTACSerializer;
    
    @Autowired
    @Qualifier("tafTACSerializer")
    private AviMessageSpecificConverter<TAF, String> tafTACSerializer;
    
    @Autowired
    @Qualifier("tafIWXXMDOMSerializer")
    private AviMessageSpecificConverter<TAF, Document> tafIWXXMDOMSerializer;
    
    @Autowired
    @Qualifier("tafIWXXMStringSerializer")
    private AviMessageSpecificConverter<TAF, String> tafIWXXMStringSerializer;
    
    @Autowired
    @Qualifier("tafIWXXMStringParser")
    private AviMessageSpecificConverter<String, TAF> tafIWXXMStringParser;
    
    @Autowired
    @Qualifier("tafIWXXMDOMParser")
    private AviMessageSpecificConverter<Document, TAF> tafIWXXMDOMParser;
    
    @Bean
    public AviMessageConverter aviMessageConverter() {
        AviMessageConverter p = new AviMessageConverter();
        p.setMessageSpecificConverter(JSONConverter.JSON_STRING_TO_METAR_POJO,metarJSONParser);
        p.setMessageSpecificConverter(JSONConverter.METAR_POJO_TO_JSON_STRING, metarJSONSerializer);
        p.setMessageSpecificConverter(JSONConverter.JSON_STRING_TO_TAF_POJO, tafJSONParser);
        p.setMessageSpecificConverter(JSONConverter.TAF_POJO_TO_JSON_STRING, tafJSONSerializer);
        
        p.setMessageSpecificConverter(TACConverter.TAC_TO_METAR_POJO, metarTACParser);
        p.setMessageSpecificConverter(TACConverter.TAC_TO_TAF_POJO, tafTACParser);
        p.setMessageSpecificConverter(TACConverter.METAR_POJO_TO_TAC, metarTACSerializer);
        p.setMessageSpecificConverter(TACConverter.TAF_POJO_TO_TAC, tafTACSerializer);
        
        p.setMessageSpecificConverter(IWXXMConverter.TAF_POJO_TO_IWXXM21_DOM, tafIWXXMDOMSerializer);
        p.setMessageSpecificConverter(IWXXMConverter.TAF_POJO_TO_IWXXM21_STRING, tafIWXXMStringSerializer);
        p.setMessageSpecificConverter(IWXXMConverter.IWXXM21_STRING_TO_TAF_POJO, tafIWXXMStringParser);
        p.setMessageSpecificConverter(IWXXMConverter.IWXXM21_DOM_TO_TAF_POJO, tafIWXXMDOMParser);
        return p;
    }

}
```

The AviMessageConverter uses the AviMessageSpecificConverter instance given with an exactly matching 
ConversionSpecification to run the conversion for that kind of conversion.
  
## Adding new conversion modules
TODO 
