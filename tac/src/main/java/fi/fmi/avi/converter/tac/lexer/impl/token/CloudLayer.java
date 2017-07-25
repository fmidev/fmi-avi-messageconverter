package fi.fmi.avi.converter.tac.lexer.impl.token;

import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.CLOUD;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName.COVER;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName.TYPE;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName.UNIT;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName.VALUE;

import java.util.regex.Matcher;

import fi.fmi.avi.model.AviationCodeListUser.CloudAmount;
import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.model.CloudForecast;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.metar.METAR;
import fi.fmi.avi.model.metar.ObservedClouds;
import fi.fmi.avi.model.metar.TrendForecast;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.TAFBaseForecast;
import fi.fmi.avi.model.taf.TAFChangeForecast;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.tac.lexer.SerializingException;
import fi.fmi.avi.converter.tac.lexer.Lexeme;
import fi.fmi.avi.converter.tac.lexer.Lexeme.Identity;
import fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName;
import fi.fmi.avi.converter.tac.lexer.impl.FactoryBasedReconstructor;
import fi.fmi.avi.converter.tac.lexer.impl.RegexMatchingLexemeVisitor;

/**
 * Created by rinne on 10/02/17.
 */
public class CloudLayer extends RegexMatchingLexemeVisitor {

    public enum CloudCover {
        SKY_CLEAR("SKC"), NO_LOW_CLOUDS("CLR"), NO_SIG_CLOUDS("NSC"), FEW("FEW"), SCATTERED("SCT"), BROKEN("BKN"), OVERCAST("OVC"), SKY_OBSCURED("VV");

        private final String code;

        CloudCover(final String code) {
            this.code = code;
        }

        public static CloudCover forCode(final String code) {
            for (CloudCover w : values()) {
                if (w.code.equals(code)) {
                    return w;
                }
            }
            return null;
        }

    }

    public enum CloudType {
        TOWERING_CUMULUS("TCU"), CUMULONIMBUS("CB");

        private final String code;

        CloudType(final String code) {
            this.code = code;
        }

        public static CloudType forCode(final String code) {
            for (CloudType w : values()) {
                if (w.code.equals(code)) {
                    return w;
                }
            }
            return null;
        }

    }
    
    public enum SpecialValue {
        AMOUNT_AND_HEIGHT_UNOBSERVABLE_BY_AUTO_SYSTEM, CLOUD_BASE_BELOW_AERODROME;
    }

    public CloudLayer(final Priority prio) {
        super("^(([A-Z]{3}|VV)([0-9]{3}|/{3})(CB|TCU)?)|(/{6})|(SKC|NSC)$", prio);
    }

    @Override
    public void visitIfMatched(final Lexeme token, final Matcher match, final ConversionHints hints) {
        if (match.group(5) != null) {
        	token.identify(CLOUD);
        	//Amount And Height Unobservable By Auto System
        	token.setParsedValue(ParsedValueName.VALUE, SpecialValue.AMOUNT_AND_HEIGHT_UNOBSERVABLE_BY_AUTO_SYSTEM);
        
        } else { 
	    	CloudCover cloudCover;
	    	if (match.group(6) != null) {
	    		cloudCover = CloudCover.forCode(match.group(6));
	    	} else {
	    		cloudCover = CloudCover.forCode(match.group(2));
	    	}
	        if (cloudCover != null) {
	            token.identify(Lexeme.Identity.CLOUD);
	            token.setParsedValue(COVER, cloudCover);
	        } else {
	            token.identify(CLOUD, Lexeme.Status.SYNTAX_ERROR, "Unknown cloud cover " + match.group(2));
	        }
	        if (match.group(3) != null) {
	            if ("///".equals(match.group(3))) {
	                token.setParsedValue(VALUE, SpecialValue.CLOUD_BASE_BELOW_AERODROME);
	            } else {
	                token.setParsedValue(VALUE, Integer.parseInt(match.group(3)));
	                token.setParsedValue(UNIT, "hft");
	            }
	        }
            if (match.group(4) != null) {
	            token.setParsedValue(TYPE, CloudType.forCode(match.group(4)));
            }
        }
    }
    
    public static class Reconstructor extends FactoryBasedReconstructor {

        @Override
        public <T extends AviationWeatherMessage> Lexeme getAsLexeme(final T msg, Class<T> clz, final ConversionHints hints, final Object... specifier)
                throws SerializingException {
            Lexeme retval = null;
            fi.fmi.avi.model.CloudLayer layer = getAs(specifier, 0, fi.fmi.avi.model.CloudLayer.class);
            String specialValue = getAs(specifier, 0, String.class);

            NumericMeasure verVis = null;
            boolean nsc = false;
            
            if (TAF.class.isAssignableFrom(clz)) {
                TAFBaseForecast baseFct = getAs(specifier, 1, TAFBaseForecast.class);
                TAFChangeForecast changeFct = getAs(specifier, 1, TAFChangeForecast.class);
            	if (baseFct != null || changeFct != null) {
            		CloudForecast cFct;
            		if (baseFct != null) {
            			cFct = baseFct.getCloud();
            		} else {
            			cFct = changeFct.getCloud();
            		}
                    if ("VV".equals(specialValue)) {
                        verVis = cFct.getVerticalVisibility();
            		} else if (cFct.isNoSignificantCloud()) {
            			nsc = true;
            		}
				}
            } else if (METAR.class.isAssignableFrom(clz)) {
            	METAR metar = (METAR)msg;
            	ObservedClouds obsClouds = metar.getClouds();
            	if (obsClouds.isNoSignificantCloud()) {
            		nsc = true;
            	}
            	TrendForecast trend = getAs(specifier, TrendForecast.class);
            	if (trend != null) {
            		if ("VV".equals(specialValue)) {
            			verVis = trend.getCloud().getVerticalVisibility();
            		} else if (trend.getCloud() != null && trend.getCloud().isNoSignificantCloud()) {
            			nsc = true;
            		}
            	} else {
            		if ("VV".equals(specialValue)) {
            			verVis = metar.getClouds().getVerticalVisibility();
            		}
            	}
            }
            if (nsc) {
            	retval = this.createLexeme("NSC", Identity.CLOUD);
            } else {
            	String str = getCloudLayerOrVerticalVisibilityToken(layer, verVis);
            	if (str != null) {
            		retval = this.createLexeme(str, Identity.CLOUD);
            	}
        	}

            return retval;
        }

        private String getCloudLayerOrVerticalVisibilityToken(final fi.fmi.avi.model.CloudLayer layer, final NumericMeasure verVis) throws SerializingException {
            String ret = null;
    		if (layer != null) {
    			StringBuilder sb = new StringBuilder();
    			NumericMeasure base = layer.getBase();

                CloudAmount amount = layer.getAmount();
    			fi.fmi.avi.model.AviationCodeListUser.CloudType type = layer.getCloudType();
        		sb.append(amount.name());
        		if (CloudAmount.SKC != amount) {
	                if (base == null || base.getValue() == null) {
	                    sb.append("///");
	                } else {
	                    sb.append(String.format("%03d", getAsHectoFeet(base)));
	                }
	                if (type != null) {
	        			sb.append(type.name());
	        		}
        		}
        		ret = sb.toString();
        		
    		} else if (verVis != null) {
    			StringBuilder sb = new StringBuilder();
    			sb.append("VV");
    			sb.append(String.format("%03d", getAsHectoFeet(verVis)));
    			ret = sb.toString();
    		}
    		return ret;
        }

        private long getAsHectoFeet(final NumericMeasure value) throws SerializingException {
            long hftValue = -1L;
            if (value != null) {
				if ("hft".equalsIgnoreCase(value.getUom())) {
					hftValue = Math.round(value.getValue());
				} else if ("[ft_i]".equalsIgnoreCase(value.getUom())) {
					hftValue = Math.round(value.getValue() / 100.0);
				} else {
                    throw new SerializingException("Unable to reconstruct cloud layer / vertical visibility height with UoM '" + value.getUom() + "'");
                }
        	} else {
                throw new SerializingException("Unable to reconstruct cloud layer / vertical visibility height with null value");
            }
			return hftValue;
        }
    }
}
