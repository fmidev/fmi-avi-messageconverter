package fi.fmi.avi.converter.iwxxm;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.UUID;

import javax.xml.namespace.QName;

import net.opengis.gml32.AbstractGeometryType;
import net.opengis.gml32.AngleType;
import net.opengis.gml32.DirectPositionType;
import net.opengis.gml32.FeaturePropertyType;
import net.opengis.gml32.LengthType;
import net.opengis.gml32.MeasureType;
import net.opengis.gml32.PointType;
import net.opengis.gml32.ReferenceType;
import net.opengis.gml32.SpeedType;
import net.opengis.gml32.StringOrRefType;
import net.opengis.om20.OMObservationType;

import aero.aixm511.AirportHeliportTimeSlicePropertyType;
import aero.aixm511.AirportHeliportTimeSliceType;
import aero.aixm511.AirportHeliportType;
import aero.aixm511.CodeAirportHeliportDesignatorType;
import aero.aixm511.CodeIATAType;
import aero.aixm511.CodeICAOType;
import aero.aixm511.TextNameType;
import aero.aixm511.ValDistanceVerticalType;
import fi.fmi.avi.converter.ConversionIssue;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.model.Aerodrome;
import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.model.CloudForecast;
import fi.fmi.avi.model.CloudLayer;
import fi.fmi.avi.model.NumericMeasure;
import icao.iwxxm21.AerodromeCloudForecastType;
import icao.iwxxm21.AngleWithNilReasonType;
import icao.iwxxm21.CloudAmountReportedAtAerodromeType;
import icao.iwxxm21.CloudLayerType;
import icao.iwxxm21.DistanceWithNilReasonType;
import icao.iwxxm21.LengthWithNilReasonType;
import icao.iwxxm21.SigConvectiveCloudTypeType;

/**
 * Created by rinne on 20/07/17.
 */
public abstract class AerodromeMessageIWXXMSerializer extends IWXXMConverter {

    public static final int MAX_CLOUD_LAYERS = 4;

    protected void updateSamplingFeature(final Aerodrome input, final OMObservationType target, final String foiId, final String aerodromeId,
            final ConversionResult<String> result) {
        if (input == null) {
            throw new IllegalArgumentException("Aerodrome info is null");
        }

        /*
        FeaturePropertyType foiProp = obs.addNewFeatureOfInterest();
        QName concreteName = new QName("http://www.opengis.net/samplingSpatial/2.0", "SF_SpatialSamplingFeature");
        SFSpatialSamplingFeatureType sFeature = (SFSpatialSamplingFeatureType) foiProp.addNewAbstractFeature()
                .substitute(concreteName, SFSpatialSamplingFeatureType.type);
        sFeature.setId(foiId);

        ReferenceType sfType = sFeature.addNewType();
        sfType.setHref("http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingPoint");
        sfType.setTitle("SF_SamplingPoint");

        FeaturePropertyType adProp = sFeature.addNewSampledFeature();
        concreteName = new QName("http://icao.int/saf/1.0", "Aerodrome");
        AerodromeType aerodrome = (AerodromeType) adProp.addNewAbstractFeature().substitute(concreteName, AerodromeType.type);
        this.setAerodromeData(aerodrome, input, aerodromeId);

        ShapeType shapeProp = sFeature.addNewShape();
        AbstractGeometryType aGeom = shapeProp.addNewAbstractGeometry();
        concreteName = new QName("http://www.opengis.net/gml/3.2", "Point");
        PointType samplingPoint = (PointType) aGeom.substitute(concreteName, PointType.type);
        DirectPositionType pInput = input.getReferencePoint();

        //NOTE: CRS is fixed to EPSG:4326
        samplingPoint.setAxisLabels(Arrays.asList("Lat", "Long"));
        samplingPoint.setSrsDimension(BigInteger.valueOf(2L));
        samplingPoint.setSrsName("http://www.opengis.net/def/crs/EPSG/0/4326");
        samplingPoint.setId("point-" + UUID.randomUUID().toString());
        samplingPoint.addNewPos().setListValue(pInput.getListValue());
        */
    }

    protected void setAerodromeData(final AirportHeliportType aerodrome, final Aerodrome input, final String aerodromeId) {
        if (input == null) {
            return;
        }

        aerodrome.setId(aerodromeId);
        create(AirportHeliportTimeSliceType.class, (timeSlice) -> {
            create(AirportHeliportTimeSlicePropertyType.class, (prop) -> {

                create(CodeAirportHeliportDesignatorType.class, (designator) -> {
                   designator.setValue(input.getDesignator());
                   timeSlice.setDesignator(wrap(designator, CodeAirportHeliportDesignatorType.class));
                });

                create(TextNameType.class, (name) -> {
                    name.setValue(input.getName().toUpperCase());
                    timeSlice.setPortName(wrap(name, TextNameType.class));
                });

                if (input.getLocationIndicatorICAO() != null) {
                    create(CodeICAOType.class, (locator) -> {
                        locator.setValue(input.getLocationIndicatorICAO());
                        timeSlice.setLocationIndicatorICAO(wrap(locator, CodeICAOType.class));
                    });
                }

                if (input.getDesignatorIATA() != null) {
                    create(CodeIATAType.class, (designator) -> {
                        designator.setValue(input.getDesignatorIATA());
                        timeSlice.setDesignatorIATA(wrap(designator, CodeIATAType.class));
                    });
                }

                if (input.getFieldElevationValue() != null) {
                    create(ValDistanceVerticalType.class, (elevation) -> {
                       elevation.setValue(String.format("%.00f",input.getFieldElevationValue()));
                       elevation.setUom("m");
                       timeSlice.setFieldElevation(wrap(elevation, ValDistanceVerticalType.class));
                    });
                }


                prop.setAirportHeliportTimeSlice(timeSlice);
                aerodrome.getTimeSlice().add(prop);
            });

        });
    /*
        if (input.getFieldElevation() != null) {
            LengthType fe = aerodrome.addNewFieldElevation();

            //Fix for strange synchronization issue in XMLBeans: https://issues.apache.org/jira/browse/XMLBEANS-328
            synchronized (input.getFieldElevation().monitor()) {
                fe.setDoubleValue(input.getFieldElevation().getDoubleValue());
                fe.setUom(input.getFieldElevation().getUom());
            }
        }
        if (input.getReferencePoint() != null) {
            PointType arp = aerodrome.addNewARP().addNewPoint();
            // Note: CRS is fixed to EPSG:4326, nominal aerodrome
            // elevation is given as property fieldElevation:
            arp.setAxisLabels(Arrays.asList("Lat", "Long"));
            arp.setSrsDimension(BigInteger.valueOf(2L));
            arp.setSrsName("http://www.opengis.net/def/crs/EPSG/0/4326");
            arp.setId("point-" + UUID.randomUUID().toString());

            //Fix for strange synchronization issue in XMLBeans: https://issues.apache.org/jira/browse/XMLBEANS-328
            synchronized (input.getReferencePoint().monitor()) {
                arp.addNewPos().setListValue(input.getReferencePoint().getListValue());
            }
        }
        */
    }

    protected MeasureType asMeasure(final NumericMeasure source) {
        return asMeasure(source, MeasureType.class);
    }

    @SuppressWarnings("unchecked")
    protected <T extends MeasureType> T asMeasure(final NumericMeasure source, final Class<T> clz) {
        T retval = null;
        if (source != null) {
            if (SpeedType.class.isAssignableFrom(clz)) {
                retval = (T) create(SpeedType.class);
            } else if (AngleType.class.isAssignableFrom(clz)) {
                retval = (T) create(AngleType.class);
            } else if (DistanceWithNilReasonType.class.isAssignableFrom(clz)) {
                retval = (T) create(AngleWithNilReasonType.class);
            } else if (LengthWithNilReasonType.class.isAssignableFrom(clz)) {
                retval = (T) create(LengthWithNilReasonType.class);
            } else {
                retval = (T) create(MeasureType.class);
            }
            retval.setValue(source.getValue());
            retval.setUom(source.getUom());
        } else {
            throw new IllegalArgumentException("NumericMeasure is null or missing");
        }
        return retval;
    }

    protected void updateForecastClouds(final AerodromeCloudForecastType cFct, final CloudForecast cloudForecast, final ConversionResult<String> result) {
        if (cloudForecast != null) {
            cFct.setId("cfct-" + UUID.randomUUID().toString());
            NumericMeasure measure = cloudForecast.getVerticalVisibility();
            if (measure != null) {
                cFct.setVerticalVisibility(wrap(asMeasure(measure, LengthWithNilReasonType.class), LengthWithNilReasonType.class));
            }
            if (cloudForecast.getLayers().size() > 0) {
                if (cloudForecast.getLayers().size() <= MAX_CLOUD_LAYERS) {
                    for (CloudLayer layer : cloudForecast.getLayers()) {
                        create(AerodromeCloudForecastType.Layer.class, (l) -> {
                            create(CloudLayerType.class, (cl) -> {
                               this.setCloudLayerData(cl, layer);
                               l.setCloudLayer(cl);
                               cFct.getLayer().add(l);
                            });
                        });
                    }
                } else {
                    result.addIssue(new ConversionIssue(ConversionIssue.Type.SYNTAX_ERROR, "Found " + cloudForecast.getLayers().size() + " cloud forecast "
                            + "layers, the maximum number in IWXXM is " + MAX_CLOUD_LAYERS));
                }
            }
        }

    }

    protected void setCloudLayerData(final CloudLayerType target, final CloudLayer source) {
        if (source != null) {
            create(CloudAmountReportedAtAerodromeType.class, (amount) -> {
                amount.setHref(AviationCodeListUser.CODELIST_VALUE_PREFIX_CLOUD_AMOUNT_REPORTED_AT_AERODROME + source.getAmount().getCode());
                amount.setTitle("From codelist " + AviationCodeListUser.CODELIST_CLOUD_AMOUNT_REPORTED_AT_AERODROME);
                target.setBase(asMeasure(source.getBase(), DistanceWithNilReasonType.class));

            });
            AviationCodeListUser.CloudType type = source.getCloudType();
            if (type != null) {
                create(SigConvectiveCloudTypeType.class, (convCloud) -> {
                    convCloud.setHref(AviationCodeListUser.CODELIST_VALUE_PREFIX_SIG_CONVECTIVE_CLOUD_TYPE + type.getCode());
                    convCloud.setTitle("From codelist " + AviationCodeListUser.CODELIST_SIGNIFICANT_CONVECTIVE_CLOUD_TYPE);
                    target.setCloudType(wrap(convCloud, SigConvectiveCloudTypeType.class));
                });

            }
        }
    }
}
