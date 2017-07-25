package fi.fmi.avi.converter.iwxxm;


import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import net.bytebuddy.implementation.bytecode.StackSize;
import net.opengis.gml32.AbstractGeometryType;
import net.opengis.gml32.AbstractTimeObjectType;
import net.opengis.gml32.AngleType;
import net.opengis.gml32.FeaturePropertyType;
import net.opengis.gml32.LengthType;
import net.opengis.gml32.ReferenceType;
import net.opengis.gml32.SpeedType;
import net.opengis.gml32.StringOrRefType;
import net.opengis.gml32.TimeInstantPropertyType;
import net.opengis.gml32.TimeInstantType;
import net.opengis.gml32.TimePeriodPropertyType;
import net.opengis.gml32.TimePeriodType;
import net.opengis.gml32.TimePositionType;
import net.opengis.om20.OMObservationPropertyType;
import net.opengis.om20.OMObservationType;
import net.opengis.om20.OMProcessPropertyType;
import net.opengis.om20.TimeObjectPropertyType;

/* 
 * Ugly, but the seemingly the recommended way to pre-define namespace prefixes,
 * see http://docs.oracle.com/cd/E17802_01/webservices/webservices/docs/1.5/jaxb/vendorProperties.html 
 */
import com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper;

import aero.aixm511.AirportHeliportType;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionIssue;
import fi.fmi.avi.converter.ConversionIssue.Type;

import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.converter.ConversionResult.Status;
import fi.fmi.avi.model.Aerodrome;
import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.AviationCodeListUser.TAFStatus;
import fi.fmi.avi.model.CloudForecast;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.Weather;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.TAFAirTemperatureForecast;
import fi.fmi.avi.model.taf.TAFBaseForecast;
import fi.fmi.avi.model.taf.TAFChangeForecast;
import fi.fmi.avi.model.taf.TAFForecast;
import fi.fmi.avi.model.taf.TAFSurfaceWind;
import icao.iwxxm21.AerodromeAirTemperatureForecastPropertyType;
import icao.iwxxm21.AerodromeAirTemperatureForecastType;
import icao.iwxxm21.AerodromeCloudForecastPropertyType;
import icao.iwxxm21.AerodromeCloudForecastType;
import icao.iwxxm21.AerodromeForecastChangeIndicatorType;
import icao.iwxxm21.AerodromeForecastWeatherType;
import icao.iwxxm21.AerodromeSurfaceWindForecastPropertyType;
import icao.iwxxm21.AerodromeSurfaceWindForecastType;
import icao.iwxxm21.AirportHeliportPropertyType;
import icao.iwxxm21.LengthWithNilReasonType;
import icao.iwxxm21.MeteorologicalAerodromeForecastRecordPropertyType;
import icao.iwxxm21.MeteorologicalAerodromeForecastRecordType;
import icao.iwxxm21.PermissibleUsageType;
import icao.iwxxm21.RelationalOperatorType;
import icao.iwxxm21.TAFReportStatusType;
import icao.iwxxm21.TAFType;
import wmo.metce2013.ProcessType;

/**
 * Created by rinne on 19/07/17.
 */
public class TAFIWXXMSerializer extends AerodromeMessageIWXXMSerializer implements IWXXMSerializer<TAF> {
  public static final int MAX_FCT_WEATHER_CODES = 3;
  public static final int MAX_FCT_TEMPERATURES = 2;
  public static final int MAX_CHANGE_FORECASTS = 7;
private static final int AbstractTimeObjectType = 0;


  public TAFIWXXMSerializer() {
  }

  @Override
  public ConversionResult<String> convertMessage(final TAF input, final ConversionHints hints) {
    ConversionResult<String> retval = new ConversionResult<>();
    if (!input.isAerodromeInfoResolved() || !input.areTimeReferencesResolved()) {
      retval.addIssue(new ConversionIssue(ConversionIssue.Type.MISSING_DATA, "Aerodrome info and time references must be resolved before converting " + "to IWXXM"));
      return retval;
    }
    TAFType taf = create(TAFType.class);
    taf.setId("taf-" + UUID.randomUUID().toString());

    //Referenced IDs:
    final String issueTimeId = "time-" + UUID.randomUUID().toString();
    final String validTimeId = "time-" + UUID.randomUUID().toString();
    final String foiId = "foi-" + UUID.randomUUID().toString();
    final String processId = "process-" + UUID.randomUUID().toString();
    final String aerodromeId = "ad-" + UUID.randomUUID().toString();

    AviationCodeListUser.TAFStatus status = input.getStatus();

    //Set status:
    taf.setStatus(TAFReportStatusType.valueOf(status.name()));

    //Set issue time:
    taf.setIssueTime(create(TimeInstantPropertyType.class, (prop) -> {
      TimeInstantType ti = create(TimeInstantType.class);
      TimePositionType tp = create(TimePositionType.class);
      tp.getValue().add(input.getIssueTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
      ti.setTimePosition(tp);
      ti.setId(issueTimeId);
      prop.setTimeInstant(ti);
    }));

    if (AviationCodeListUser.TAFStatus.MISSING != status) {
      //Set valid time:
      taf.setValidTime(create(TimePeriodPropertyType.class, (prop) -> {
        TimePeriodType tp = create(TimePeriodType.class);
        tp.setId(validTimeId);
        TimePositionType beginPos = create(TimePositionType.class);
        beginPos.getValue().add(input.getValidityStartTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        TimePositionType endPos = create(TimePositionType.class);
        endPos.getValue().add(input.getValidityEndTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        tp.setBeginPosition(beginPos);
        tp.setEndPosition(endPos);
        prop.setTimePeriod(tp);

      }));
      this.updateChangeForecast(input, taf, issueTimeId, validTimeId, foiId, processId, retval);
    }
    this.updateBaseForecast(input, taf, issueTimeId, validTimeId, foiId, processId, aerodromeId, retval);

    if (AviationCodeListUser.TAFStatus.CORRECTION == status || AviationCodeListUser.TAFStatus.CANCELLATION == status
        || AviationCodeListUser.TAFStatus.AMENDMENT == status) {
      this.updatePreviousReportReferences(input, taf, aerodromeId, retval);
    } else {
      //TAF: previousReportValidPeriod must be null unless this cancels, corrects or amends a previous report
      if (input.getReferredReport() != null) {
        retval.addIssue(new ConversionIssue(ConversionIssue.Type.LOGICAL_ERROR,
            "TAF contains reference to the previous report even if its type is "
                + "not amendment, cancellation or correction"));
      }
    }
    try {
      retval.setStatus(Status.SUCCESS);
      this.updateMessageMetadata(retval, taf);
      retval.setConvertedMessage(this.renderXMLString(taf, hints));
    } catch (JAXBException e) {
      retval.setStatus(Status.FAIL);
      retval.addIssue(new ConversionIssue(ConversionIssue.Type.OTHER, "Unable to render IWXXM message to String", e));
    }
    return retval;
  }

  private void updateBaseForecast(final TAF source,
      final TAFType target,
      final String issueTimeId,
      final String validTimeId,
      final String foiId,
      final String processId,
      final String aerodromeId,
      final ConversionResult<String> result) {

    TAFForecast baseForecastInput = source.getBaseForecast();
    if (baseForecastInput != null) {
      final OMObservationType baseFct = create(OMObservationType.class);
      baseFct.setId("bfct-" + UUID.randomUUID().toString());

      baseFct.setType(create(ReferenceType.class, (ref) -> {
        ref.setHref("http://codes.wmo.int/49-2/observation-type/iwxxm/2.1/MeteorologicalAerodromeForecast");
        ref.setTitle("Aerodrome Base Forecast");

      }));

      baseFct.setPhenomenonTime(create(TimeObjectPropertyType.class, (prop) -> {
        if (AviationCodeListUser.TAFStatus.MISSING == source.getStatus()) {
          prop.setHref("#" + issueTimeId);
          prop.setTitle("issueTime of the TAF missing");
        } else {
          prop.setHref("#" + validTimeId);
          prop.setTitle("Valid time period of the TAF");
        }
      }));

      baseFct.setResultTime(create(TimeInstantPropertyType.class, (prop) -> {
        prop.setHref("#" + issueTimeId);
        prop.setTitle("issueTime of the TAF");
      }));

      if (AviationCodeListUser.TAFStatus.MISSING != source.getStatus()) {
        baseFct.setValidTime(create(TimePeriodPropertyType.class, (prop) -> {
          prop.setHref("#" + validTimeId);
          prop.setTitle("Valid time period of the TAF");
        }));
      }

      baseFct.setProcedure(create(OMProcessPropertyType.class, (prop) -> {
        prop.setAny(createAndWrap(ProcessType.class, (process)  -> {
          process.setId(processId);
          process.setDescription(create(StringOrRefType.class, (descr) -> {
            descr.setValue("WMO No. 49 Volume 2 Meteorological Service for International Air Navigation APPENDIX 5 TECHNICAL SPECIFICATIONS RELATED TO FORECASTS");
          }));
        }));
      }));

      baseFct.setObservedProperty(create(ReferenceType.class, (ref) -> {
        ref.setHref("http://codes.wmo.int/49-2/observable-property/MeteorologicalAerodromeForecast");
        ref.setTitle("TAF forecast properties");
      }));

      Aerodrome ad = source.getAerodrome();
      this.updateSamplingFeature(ad, baseFct, foiId, aerodromeId, result);

      this.updateForecastResult(source, baseForecastInput, baseFct, result);

      target.setBaseForecast(create(OMObservationPropertyType.class, (prop) -> {
        prop.setOMObservation(baseFct);
      }));
    } else {
      if (TAFStatus.CANCELLATION != source.getStatus()) {
        result.addIssue(new ConversionIssue(Type.MISSING_DATA, "Base forecast missing for non-cancellation TAF"));
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void updateChangeForecast(final TAF source,
      final TAFType target,
      final String issueTimeId,
      final String validTimeId,
      final String foid,
      final String processId,
      final ConversionResult<String> result) {

    List<TAFChangeForecast> fcts = source.getChangeForecasts();
    if (fcts != null && fcts.size() > 0) {
      if (fcts.size() <= MAX_CHANGE_FORECASTS) {
        TAFChangeForecast fctInput;
        for (int i = 0; i < fcts.size(); i++) {
          fctInput = fcts.get(i);
          if (fctInput != null) {
            final OMObservationType changeFct = create(OMObservationType.class);
            changeFct.setId("chfct-" + UUID.randomUUID().toString());
            changeFct.setType(create(ReferenceType.class, (ref) -> {
              ref.setHref("http://codes.wmo.int/49-2/observation-type/IWXXM/1.0/MeteorologicalAerodromeForecast");
              ref.setTitle("Aerodrome Forecast");
            }));
            ZonedDateTime startTime = fctInput.getValidityStartTime();
            ZonedDateTime endTime = fctInput.getValidityEndTime();
            if (startTime != null && endTime != null) {
              if (startTime.isBefore(source.getValidityStartTime())) {
                result.addIssue(new ConversionIssue(Type.LOGICAL_ERROR,"Change group start time '" + startTime.toString() + "'" +
                    " is before TAF validity start time " + source.getValidityStartTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
              }
              if (endTime.isAfter(source.getValidityEndTime())) {
                result.addIssue(new ConversionIssue(Type.LOGICAL_ERROR,"Change group end time '" + endTime.toString() + "' is " +
                    " after TAF validity end time " +source.getValidityEndTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
              }

              target.getChangeForecast().add(create(OMObservationPropertyType.class, (prop) -> {
                prop.setOMObservation(changeFct);
              }));

            } else {
              result.addIssue(new ConversionIssue(Type.MISSING_DATA, "Missing full validity start and/or end times in change forecast at index " + i));
            }

            changeFct.setPhenomenonTime(create(TimeObjectPropertyType.class, (toProp) -> {
              JAXBElement<?> wrapped = createAndWrap(TimePeriodType.class, (period) -> {
                period.setId("time-" + UUID.randomUUID().toString());
                period.setBeginPosition(create(TimePositionType.class, (tPos) -> {
                  tPos.getValue().add(startTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                }));
                period.setEndPosition(create(TimePositionType.class, (tPos) -> {
                  tPos.getValue().add(endTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                }));
              });
              toProp.setAbstractTimeObject((JAXBElement<AbstractTimeObjectType>)wrapped);
            }));

            changeFct.setResultTime(create(TimeInstantPropertyType.class, (tiProp) -> {
              tiProp.setHref("#" + issueTimeId);
              tiProp.setTitle("Issue time of the TAF");
            }));

            changeFct.setValidTime(create(TimePeriodPropertyType.class, (tpProp) -> {
              tpProp.setHref("#" + validTimeId);
              tpProp.setTitle("Valid time period of the TAF");
            }));

            changeFct.setProcedure(create(OMProcessPropertyType.class, (procProp) -> {
              procProp.setHref("#" + processId);
              procProp.setTitle("WMO 49-2 TAF");
            }));

            changeFct.setType(create(ReferenceType.class, (ref) -> {
              ref.setHref("http://codes.wmo.int/49-2/observation-type/iwxxm/2.1/MeteorologicalAerodromeForecast");
              ref.setTitle("Aerodrome Change Forecast");
            }));

            changeFct.setObservedProperty(create(ReferenceType.class, (ref) -> {
              ref.setHref("http://codes.wmo.int/49-2/observable-property/MeteorologicalAerodromeForecast");
              ref.setTitle("TAF forecast properties");
            }));

            changeFct.setFeatureOfInterest(create(FeaturePropertyType.class, (foiProp) -> {
              foiProp.setHref("#" + foid);
              foiProp.setTitle("Same aerodrome as in baseForecast");
            }));

            this.updateForecastResult(source, fctInput, changeFct, result);
          }
        }
      } else {
        result.addIssue(new ConversionIssue(Type.SYNTAX_ERROR, "Found " + fcts.size() + " change forecasts, " +
            "maximum number in IWXXM is " + MAX_CHANGE_FORECASTS));
      }
    }
  }

  private void updateForecastResult(final TAF taf, final TAFForecast source,
      final OMObservationType target,
      final ConversionResult<String> result) {
    if (source == null) {
      return;
    }

    if (TAFStatus.MISSING == taf.getStatus()) {
      if (source instanceof TAFBaseForecast) {
        //TODO nilReason="missing"
        target.setResult(null);
      } else {
        throw new IllegalArgumentException("Can only add NIL result for type TAFBaseForecast. Tried to " +
            "add NIL result for type " + source.getClass().getCanonicalName());
      }
    } else {
      MeteorologicalAerodromeForecastRecordType fctRecord = create(MeteorologicalAerodromeForecastRecordType.class);
      fctRecord.setId("rec-" + UUID.randomUUID().toString());
      fctRecord.setCloudAndVisibilityOK(source.isCeilingAndVisibilityOk());
      if (!source.isCeilingAndVisibilityOk()) {
        NumericMeasure measure = source.getPrevailingVisibility();
        if (measure != null) {
          fctRecord.setPrevailingVisibility(asMeasure(measure, LengthType.class));
        }
        if (source.getPrevailingVisibilityOperator() != null) {
          fctRecord.setPrevailingVisibilityOperator(RelationalOperatorType.valueOf(source.getPrevailingVisibilityOperator().name()));
        }
        if (source.getForecastWeather() != null && source.getForecastWeather().size() > 0) {
          if (source.getForecastWeather().size() <= MAX_FCT_WEATHER_CODES) {
            for (Weather weather : source.getForecastWeather()) {
              fctRecord.getWeather().add(create(AerodromeForecastWeatherType.class, (w) -> {
                w.setHref(AviationCodeListUser.CODELIST_VALUE_PREFIX_SIG_WEATHER + weather.getCode());
                w.setTitle(weather.getDescription());
              }));
            }
          } else {
            result.addIssue(new ConversionIssue(Type.SYNTAX_ERROR, "Found " + source.getForecastWeather().size() + " forecast weather codes in TAF change forecast, the maximum number in IWXXM is " + MAX_FCT_WEATHER_CODES));
          }
        }
        CloudForecast cFct = source.getCloud();
        if (cFct != null) {
          final AerodromeCloudForecastType acFct = create(AerodromeCloudForecastType.class);
          this.updateForecastClouds(cFct, acFct, result);
          fctRecord.setCloud(create(AerodromeCloudForecastPropertyType.class, (prop) -> {
            prop.setAerodromeCloudForecast(acFct);
          }));
        }
      }
      if (source.getSurfaceWind() != null) {
        final AerodromeSurfaceWindForecastType wind = create(AerodromeSurfaceWindForecastType.class);
        this.updateForecastSurfaceWind(source.getSurfaceWind(), wind, result);
        fctRecord.setSurfaceWind(create(AerodromeSurfaceWindForecastPropertyType.class, (prop) -> {
          prop.setAerodromeSurfaceWindForecast(wind);
        }));
      }

      if (source instanceof TAFBaseForecast) {
        TAFBaseForecast baseFct = (TAFBaseForecast)source;
        if (baseFct.getTemperatures() != null) {
          int tempCount = baseFct.getTemperatures().size();
          if (tempCount > 0) {
            if (tempCount <= MAX_FCT_TEMPERATURES) {
              for (int i = 0; i < tempCount; i++) {
                TAFAirTemperatureForecast airTemp = baseFct.getTemperatures().get(i);
                AerodromeAirTemperatureForecastType tempFct = create(AerodromeAirTemperatureForecastType.class);
                this.setAirTemperatureForecast(airTemp, tempFct, result);
                fctRecord.getTemperature().add(create(AerodromeAirTemperatureForecastPropertyType.class, (prop) -> {
                  prop.setAerodromeAirTemperatureForecast(tempFct);
                }));
              }
            } else {
              result.addIssue(new ConversionIssue(Type.SYNTAX_ERROR,
                  "Found " + tempCount + " air temperature forecasts in TAF, " +
                      "only " + MAX_FCT_TEMPERATURES + " allowed in IWXXM"));
            }
          }
        }
      } else if (source instanceof TAFChangeForecast) {
        TAFChangeForecast changeFct = (TAFChangeForecast)source;
        fctRecord.setChangeIndicator(AerodromeForecastChangeIndicatorType.valueOf(changeFct.getChangeIndicator().name()));
      } else {
        throw new IllegalArgumentException("Unknown TAF forecast type " + source.getClass().getCanonicalName());
      }

      target.setResult(create(MeteorologicalAerodromeForecastRecordPropertyType.class, (prop) -> {
        prop.setMeteorologicalAerodromeForecastRecord(fctRecord);
      }));

    }
  }

  private void updateForecastSurfaceWind(final TAFSurfaceWind source,
      final AerodromeSurfaceWindForecastType target,
      final ConversionResult<String> result) {
        if (source != null) {
          NumericMeasure measure = source.getMeanWindSpeed();
            if (measure != null) {
                target.setMeanWindSpeed(asMeasure(measure, SpeedType.class));
            }
            measure = source.getMeanWindDirection();
            if (measure != null) {
                target.setMeanWindDirection(asMeasure(measure, AngleType.class));
            }
            measure = source.getWindGust();
            if (measure != null) {
                target.setWindGustSpeed(asMeasure(measure, SpeedType.class));
            }
            target.setVariableWindDirection(source.isVariableDirection());
        }
  }

  private void setAirTemperatureForecast(final TAFAirTemperatureForecast source,
      final AerodromeAirTemperatureForecastType target,
      final ConversionResult<String> result) {
    if (source != null) {
      NumericMeasure measure = source.getMinTemperature();
      if (measure != null) {
        target.setMinimumAirTemperature(asMeasure(measure));
        target.setMinimumAirTemperatureTime(create(TimeInstantPropertyType.class, (prop) -> {
          prop.setTimeInstant(create(TimeInstantType.class, (time) -> {
            time.setId("time-" + UUID.randomUUID().toString());
            time.setTimePosition(create(TimePositionType.class, (tPos) -> {
              tPos.getValue().add(source.getMinTemperatureTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            }));
          }));
        }));
      }

      measure = source.getMaxTemperature();
      if (measure != null) {
        target.setMaximumAirTemperature(asMeasure(measure));
        target.setMaximumAirTemperatureTime(create(TimeInstantPropertyType.class, (prop) -> {
          prop.setTimeInstant(create(TimeInstantType.class, (time) -> {
            time.setId("time-" + UUID.randomUUID().toString());
            time.setTimePosition(create(TimePositionType.class, (tPos) -> {
              tPos.getValue().add(source.getMaxTemperatureTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            }));
          }));
        }));
      }

    }
  }

  private void updatePreviousReportReferences(final TAF source, final TAFType target, String aerodromeId, final ConversionResult<String> result) {
    if (TAFReportStatusType.CANCELLATION == target.getStatus() || TAFReportStatusType.CORRECTION == target.getStatus()
          || TAFReportStatusType.AMENDMENT == target.getStatus()) {
      TAF prevReport = source.getReferredReport();
      if (prevReport != null) {
        target.setPreviousReportAerodrome(create(AirportHeliportPropertyType.class, (prop) -> {
          if (source.getAerodrome().equals(prevReport.getAerodrome())) {
            prop.setHref("#" + aerodromeId);
            prop.setTitle("Same aerodrome as the in the base forecast");
          } else {
            prop.setAirportHeliport(create(AirportHeliportType.class, (aerodrome) -> {
              String aId = "ad-" + UUID.randomUUID().toString();
              this.setAerodromeData(aerodrome, prevReport.getAerodrome(), aId);
            }));
          }
        }));
        
        ZonedDateTime from = prevReport.getValidityStartTime();
        ZonedDateTime to = prevReport.getValidityEndTime();
        if (from != null && to != null) {
          target.setPreviousReportValidPeriod(create(TimePeriodPropertyType.class, (prop) -> {
            prop.setTimePeriod(create(TimePeriodType.class, (period) -> {
              period.setId("time-" + UUID.randomUUID().toString());
              period.setBeginPosition(create(TimePositionType.class, (tPos) -> {
                tPos.getValue().add(from.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
              }));
              period.setEndPosition(create(TimePositionType.class, (tPos) -> {
                tPos.getValue().add(to.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
              }));
            }));
          }));
        } else {
          result.addIssue(new ConversionIssue(Type.MISSING_DATA, "Missing full validity time start and/or end of the referred (previous) report"));
        }
      } else {
        result.addIssue(new ConversionIssue(Type.MISSING_DATA, "Missing the referred (previous) report for report of type " + target.getStatus()));
      }
    }
  }

  private void updateMessageMetadata(final ConversionResult<?> results, final TAFType target) {
    if (ConversionResult.Status.SUCCESS != results.getStatus()) {
      target.setPermissibleUsage(PermissibleUsageType.NON_OPERATIONAL);
      List<ConversionIssue> issues = results.getConversionIssues();
      if (!issues.isEmpty()) {
        target.setPermissibleUsageSupplementary("Issues:" + results.getConversionIssues().toString());
      }
    } else {
      target.setPermissibleUsage(PermissibleUsageType.OPERATIONAL);
    }
  }

  private String renderXMLString(final TAFType tafElem, final ConversionHints hints) throws JAXBException {
    StringWriter sw = new StringWriter();
    Marshaller marshaller = getJAXBContext().createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
    marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://icao.int/iwxxm/2.1 https://schemas.wmo.int/iwxxm/2.1/iwxxm.xsd http://def.wmo.int/metce/2013 http://schemas.wmo.int/metce/1.2/metce.xsd http://www.opengis.net/samplingSpatial/2.0 http://schemas.opengis.net/samplingSpatial/2.0/spatialSamplingFeature.xsd");
    marshaller.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", new IWXXMNamespaceMapper());
    marshaller.marshal(wrap(tafElem, TAFType.class), sw);
    return asCleanedUpXML(sw, hints);
  }
  
  private String asCleanedUpXML(final StringWriter input, final ConversionHints hints) {
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setNamespaceAware(true);
      dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
      DocumentBuilder db = dbf.newDocumentBuilder();
      InputSource is = new InputSource(new StringReader(input.toString()));
      Document dom3Doc = db.parse(is);
      StringWriter sw = new StringWriter();
      Result cleanedResult = new StreamResult(sw);
      TransformerFactory tFactory = TransformerFactory.newInstance();
      
      //TODO: add a cleaning XSL transformation loading from resources:
      Transformer transformer = tFactory.newTransformer(this.getCleanupTransformationStylesheet(hints));
      
      //transformer.setOutputProperty( "omit-xml-declaration", "yes" );
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      DOMSource dsource = new DOMSource(dom3Doc);
      transformer.transform(dsource, cleanedResult);
      return sw.toString();
    } catch (ParserConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (SAXException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (TransformerConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (TransformerException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }
  
  private Source getCleanupTransformationStylesheet(final ConversionHints hints) {
    return new StreamSource(this.getClass().getResourceAsStream("2.1/TAFCleanup.xsl"));
  }

  static class IWXXMNamespaceMapper extends NamespacePrefixMapper {
    private static Map<String,String> mapping = new HashMap<>();

    IWXXMNamespaceMapper() {
      mapping.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");
      mapping.put("http://www.w3.org/1999/xlink", "xlink");
      mapping.put("http://www.opengis.net/gml/3.2", "gml");
      mapping.put("http://www.isotc211.org/2005/gmd", "gmd");
      mapping.put("http://www.isotc211.org/2005/gco", "gco");
      mapping.put("http://www.isotc211.org/2005/gts", "gts");
      mapping.put("http://www.aixm.aero/schema/5.1.1", "aixm");
      mapping.put("http://icao.int/iwxxm/2.1", "iwxxm");
      mapping.put("http://def.wmo.int/opm/2013", "opm");
      mapping.put("http://def.wmo.int/metce/2013", "metce");
      mapping.put("http://www.opengis.net/om/2.0", "om");
      mapping.put("http://www.opengis.net/sampling/2.0", "sam");
      mapping.put("http://www.opengis.net/samplingSpatial/2.0", "sams");
    }

    @Override
    public String getPreferredPrefix(final String namespace, final String prefix, final boolean required) {
      return mapping.getOrDefault(namespace, prefix);
    }
  }

}
