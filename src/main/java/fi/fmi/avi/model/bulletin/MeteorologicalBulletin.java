package fi.fmi.avi.model.bulletin;

import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.model.AviationWeatherMessageCollection;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Optional;
import java.util.Set;

public interface MeteorologicalBulletin<T extends AviationWeatherMessage> extends AviationWeatherMessageCollection<T> {

    BulletinHeading getHeading();

    Optional<ZonedDateTime> getTimeStamp();

    Set<ChronoField> getTimeStampFields();

    /**
     * Returns the collect identifier string for the IWXXM collect schema.
     * <p>
     * This contains the value for the {@code <collect:bulletinIdentifier>} element.
     * <p>
     * When serializing to IWXXM, if this property is present, it will be used as-is.
     * If absent, a new identifier will be generated from the current model state.
     * <p>
     * When the identifier conforms to the general file naming convention described in
     * Attachment II-15 to the WMO Manual on the Global Telecommunication System (WMO No. 386),
     * it can be parsed using {@link fi.fmi.avi.util.GTSExchangeFileInfo}.
     *
     * @return the collect identifier string, or empty if not set
     */
    Optional<String> getCollectIdentifier();

}
