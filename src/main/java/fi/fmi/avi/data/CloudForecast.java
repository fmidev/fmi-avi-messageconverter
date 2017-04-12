package fi.fmi.avi.data;

import java.util.List;

public interface CloudForecast extends AviationCodeListUser {

    NumericMeasure getVerticalVisibility();

    void setVerticalVisibility(NumericMeasure verticalVisibility);

    List<CloudLayer> getLayers();

    void setLayers(List<CloudLayer> layers);

}
