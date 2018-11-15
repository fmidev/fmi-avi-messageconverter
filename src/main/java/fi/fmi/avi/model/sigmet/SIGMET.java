package fi.fmi.avi.model.sigmet;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.AviationWeatherMessage;

/**
 * This is a placeholder SIGMET model class created to support handling SIGMET data only as TAC encoded
 * strings. In this revision only the {@link #getTranslatedTAC()} value is used for representing the SIGMET content.
 *
 * @apiNote will change dramatically,  to be replaced with a full SIGMET model as soon as available.
 */
public interface SIGMET extends AviationWeatherMessage, AviationCodeListUser {
}
