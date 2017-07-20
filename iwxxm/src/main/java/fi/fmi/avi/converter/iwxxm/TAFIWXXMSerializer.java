package fi.fmi.avi.converter.iwxxm;


import java.io.StringWriter;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import net.opengis.gml32.ReferenceType;
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

import com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper;

import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionIssue;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.TAFAirTemperatureForecast;
import fi.fmi.avi.model.taf.TAFForecast;
import fi.fmi.avi.model.taf.TAFSurfaceWind;
import icao.iwxxm21.AerodromeAirTemperatureForecastType;
import icao.iwxxm21.MeteorologicalAerodromeForecastRecordType;
import icao.iwxxm21.PermissibleUsageType;
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

        AviationCodeListUser.TAFStatus status = input.getStatus();

        //Set status:
        taf.setStatus(TAFReportStatusType.valueOf(status.name()));

        //Set issue time:
        create(TimeInstantPropertyType.class, (prop) -> {
            TimeInstantType ti = create(TimeInstantType.class);
            TimePositionType tp = create(TimePositionType.class);
            tp.getValue().add(input.getIssueTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            ti.setTimePosition(tp);
            ti.setId(issueTimeId);
            prop.setTimeInstant(ti);
            taf.setIssueTime(prop);
        });

        if (AviationCodeListUser.TAFStatus.MISSING != status) {
            //Set valid time:
            create(TimePeriodPropertyType.class, (prop) -> {
                TimePeriodType tp = create(TimePeriodType.class);
                tp.setId(validTimeId);
                TimePositionType beginPos = create(TimePositionType.class);
                beginPos.getValue().add(input.getValidityStartTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                TimePositionType endPos = create(TimePositionType.class);
                endPos.getValue().add(input.getValidityEndTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                tp.setBeginPosition(beginPos);
                tp.setEndPosition(endPos);
                prop.setTimePeriod(tp);
                taf.setValidTime(prop);
            });
            this.updateChangeForecast(input, taf, issueTimeId, validTimeId, foiId, processId, retval);
        }
        this.updateBaseForecast(input, taf, issueTimeId, validTimeId, foiId, processId, retval);

        if (AviationCodeListUser.TAFStatus.CORRECTION == status || AviationCodeListUser.TAFStatus.CANCELLATION == status
                || AviationCodeListUser.TAFStatus.AMENDMENT == status) {
            this.updatePreviousReportReferences(input, taf, retval);
        } else {
            //TAF: previousReportValidPeriod must be null unless this cancels, corrects or amends a previous report
            if (input.getReferredReport() != null) {
                retval.addIssue(new ConversionIssue(ConversionIssue.Type.LOGICAL_ERROR,
                        "TAF contains reference to the previous report even if it's type is "
                        + "not amendment, cancellation or correction"));
            }
        }
        try {
            retval.setConvertedMessage(this.renderXMLString(taf));
            this.updateMessageMetadata(retval, taf);
        } catch (JAXBException e) {
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
            final ConversionResult<String> result) {

        TAFForecast baseForecastInput = source.getBaseForecast();
        if (baseForecastInput != null) {
            final OMObservationType baseFct = create(OMObservationType.class);
            baseFct.setId("bfct-" + UUID.randomUUID().toString());

            //Set message type
            create(ReferenceType.class, (ref) -> {
                ref.setHref("http://codes.wmo.int/49-2/observation-type/iwxxm/2.1/MeteorologicalAerodromeForecast");
                ref.setTitle("Aerodrome Base Forecast");
                baseFct.setType(ref);
            });

            //Set phenomenon time reference:
            create(TimeObjectPropertyType.class, (prop) -> {
                if (AviationCodeListUser.TAFStatus.MISSING == source.getStatus()) {
                    prop.setHref("#" + issueTimeId);
                    prop.setTitle("issueTime of the TAF missing");
                } else {
                    prop.setHref("#" + validTimeId);
                    prop.setTitle("Valid time period of the TAF");
                }
                baseFct.setPhenomenonTime(prop);
            });

            //Set result time reference:
            create(TimeInstantPropertyType.class, (prop) -> {
                prop.setHref("#" + issueTimeId);
                prop.setTitle("issueTime of the TAF");
                baseFct.setResultTime(prop);
            });

            //Set valid time reference:
            if (AviationCodeListUser.TAFStatus.MISSING != source.getStatus()) {
                create(TimePeriodPropertyType.class, (prop) -> {
                    prop.setHref("#" + validTimeId);
                    prop.setTitle("Valid time period of the TAF");
                    baseFct.setValidTime(prop);
                });
            }

            //Set process:
            create(OMProcessPropertyType.class, (prop) -> {
                create(ProcessType.class, (process)  -> {
                    process.setId(processId);
                    create(StringOrRefType.class, (descr) -> {
                       descr.setValue("WMO No. 49 Volume 2 Meteorological Service for International Air Navigation APPENDIX 5 TECHNICAL SPECIFICATIONS RELATED TO FORECASTS");
                       process.setDescription(descr);
                    });
                    prop.setAny(wrap(process, ProcessType.class));
                    baseFct.setProcedure(prop);
                });
            });

            //Set observed property:
            create(ReferenceType.class, (ref) -> {
                ref.setHref("http://codes.wmo.int/49-2/observable-property/MeteorologicalAerodromeForecast");
                ref.setTitle("TAF forecast properties");
                baseFct.setObservedProperty(ref);
            });



            //Finally, inject baseForecast to TAF:
            create(OMObservationPropertyType.class, (prop) -> {
               prop.setOMObservation(baseFct);
               target.setBaseForecast(prop);
            });
        } else {

        }
        /*

            Aerodrome ad = msg.getTargetAerodrome();
            if (ad == null) {
                throw new ProcessingException(ProcessingException.ErrorType.MISSING_DATA, msg,
                        "No metadata found for aerodrome '" + msg.getTargetAerodromeId() + "'");
            }
            String aerodromeId = "ad-" + UUID.randomUUID().toString();
            this.updateSamplingFeature(msg, baseFct, ad, foiId, aerodromeId);
            this.updateForecastResult(msg, baseFct, baseForecastInput);
        } else {
            //Base forecast must be missing for cancellations and only for them:
            if (TAFStatus.CANCELLATION != msg.getParsedMessage().getStatus()) {
                throw new ProcessingException(ProcessingException.ErrorType.MISSING_DATA, msg, "Base forecast missing for non-cancellation TAF");
            }
        }
         */
    }

    private void updateChangeForecast(final TAF source,
            final TAFType target,
            final String issueTimeId,
            final String validTimeId,
            final String foid,
            final String processId,
            final ConversionResult<String> result) {
    }

    private void updateForecastResult(final TAFForecast source,
            final OMObservationType target,
            final ConversionResult<String> result) {


    }

    private void updateForecastSurfaceWind(final TAFSurfaceWind source,
            final MeteorologicalAerodromeForecastRecordType target,
            final ConversionResult<String> result) {

    }

    private void setAirTemperatureForecast(final TAFAirTemperatureForecast source,
            final AerodromeAirTemperatureForecastType target,
            final ConversionResult<String> result) {

    }

    private void updatePreviousReportReferences(final TAF source, final TAFType target, final ConversionResult<String> result) {

    }

    private void updateMessageMetadata(final ConversionResult results, final TAFType target) {
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

    private String renderXMLString(final TAFType tafElem) throws JAXBException {
        StringWriter sw = new StringWriter();
        Marshaller marshaller = getJAXBContext().createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://icao.int/iwxxm/2.1 https://schemas.wmo.int/iwxxm/2.1/iwxxm.xsd");
        marshaller.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", new IWXXMNamespaceMapper());
        marshaller.marshal(wrap(tafElem, TAFType.class), sw);
        return sw.toString();
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
