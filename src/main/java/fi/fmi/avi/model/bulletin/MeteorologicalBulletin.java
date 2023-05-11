package fi.fmi.avi.model.bulletin;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Optional;
import java.util.Set;

import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.model.AviationWeatherMessageCollection;

public interface MeteorologicalBulletin<T extends AviationWeatherMessage> extends AviationWeatherMessageCollection<T> {

    BulletinHeading getHeading();

    Optional<ZonedDateTime> getTimeStamp();

    Set<ChronoField> getTimeStampFields();

}
