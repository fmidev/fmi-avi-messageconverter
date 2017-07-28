# fmi-avi-messageconverter
The main module for aviation weather message conversions between different formats.

This project does nothing by itself, it needs one or more conversion modules providing parsing 
and serializing implementations between the aviation weather message model objects 
(in the sub packages of fi.fmi.avi.model) and the specific message formats, such as TAC or IWXXM.

## Get started
For parsing/serialization functionality for a one format, use the specific Maven project, such as
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
   TAF pojo = result.getConvertedMessage();
 }
```

To create a custom conversion chain from one format to another, just combine two or more conversions:

```java
String tac = "TAF EFAB 190815Z 1909/1915 14008G15MPS 9999 BKN010 BKN015=";
ConversionResult<TAF> result1 = converter.convertMessage(tac,TACConverter.TAC_TO_TAF_POJO);
 if (ConversionResult.Status.SUCCESS = result1.getStatus()) {
   TAF tafPojo = result1.getConvertedMessage();
   tafPojo.setTranslated(true);
   //provide additional info, such as the target aerodrome & complete reference time:
   tafPojo.amendAerodromeInfo(aerodrome);
   tafPojo.amendTimeReferences(referenceTime);
   ConversionResult<Document> result2 = converter.convertMessage(tafPojo,IWXXMConverter.TAF_POJO_TO_IWXXM21_DOM);
   if (ConversionResult.Status.SUCCESS = result2.getStatus()) {
     Document iwxxmTAF = result2.getConvertedMessage();
     [do your stuff with the IWXXM TAF DOM Document]
   }
 }
```

## Supported message types
Currently, this project contains Java model classes for aviation weather message types METAR and TAF. It's anticipated 
that more message types such as SIGMET, AIRMET, SPECI will be added once the project gets more mature.

## Configuring the AviMessageConverter
The AviMessageConverter class in taught to handle new conversions by calling the method 

`setMessageSpecificConverter(ConversionSpecification<S, T>, AviMessageSpecificConverter<S, T>)`

A ConversionSpecification class instance defines the conversion from one input format to particular output format.
By convention, each of the conversion modules should include all the provided conversions as public static final
fields, such as `fi.fmi.avi.converter.tac.TACConverter.TAC_TO_TAF_POJO` or 
`fi.fmi.avi.converter.iwxxm.IWXXMConverter.TAF_POJO_TO_IWXXM21_DOM` used in the examples above.

The recommended way of configuring the converter is using Spring Java configuration:

```java
 @Bean
 public AviMessageConverter aviMessageConverter() throws JAXBException {
    AviMessageConverter p = new AviMessageConverter();
    p.setMessageSpecificConverter(TACConverter.TAC_TO_METAR_POJO, metarTACParser());
    p.setMessageSpecificConverter(TACConverter.TAC_TO_TAF_POJO, tafTACParser());
    p.setMessageSpecificConverter(TACConverter.METAR_POJO_TO_TAC, metarTACSerializer());
    p.setMessageSpecificConverter(TACConverter.TAF_POJO_TO_TAC, tafTACSerializer());
    p.setMessageSpecificConverter(IWXXMConverter.TAF_POJO_TO_IWXXM21_DOM, tafIWXXMDOMSerializer());
    p.setMessageSpecificConverter(IWXXMConverter.TAF_POJO_TO_IWXXM21_STRING, tafIWXXMStringSerializer());
    return p;
 }
```

The AviMessageConverter uses the AviMessageSpecificConverter instance given with an exactly matching 
ConversionSpecification to run the conversion for that kind of conversion.
  
## Adding new conversion modules
TODO 
