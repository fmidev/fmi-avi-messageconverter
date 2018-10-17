package fi.fmi.avi.model;

import java.util.List;

public interface MeteorologicalBulletin<T extends AviationWeatherMessage, S extends BulletinHeading> extends AviationWeatherMessageOrCollection {

    S getHeading();

    List<T> getMessages();

}
