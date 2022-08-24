package fi.fmi.avi.model;

import java.util.List;

public interface AviationWeatherMessageCollection<T extends AviationWeatherMessage> extends AviationWeatherMessageOrCollection {
    List<T> getMessages();
}
