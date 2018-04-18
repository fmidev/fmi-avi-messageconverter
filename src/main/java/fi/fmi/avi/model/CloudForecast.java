package fi.fmi.avi.model;

import java.util.List;
import java.util.Optional;


public interface CloudForecast extends AviationCodeListUser {

    boolean isNoSignificantCloud();

    Optional<NumericMeasure> getVerticalVisibility();

    Optional<List<CloudLayer>> getLayers();


}
