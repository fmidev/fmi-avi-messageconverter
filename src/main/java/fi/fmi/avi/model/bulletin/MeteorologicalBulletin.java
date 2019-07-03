package fi.fmi.avi.model.bulletin;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.model.AviationWeatherMessageOrCollection;

public interface MeteorologicalBulletin<T extends AviationWeatherMessage> extends AviationWeatherMessageOrCollection {

    BulletinHeading getHeading();

    List<T> getMessages();

    Optional<ZonedDateTime> getTimeStamp();

    Set<ChronoField> getTimeStampFields();

}
