package fi.fmi.avi.model;

import java.util.List;

public interface CloudForecast extends AviationCodeListUser {

	boolean isNoSignificantCloud();
	  
    NumericMeasure getVerticalVisibility();
    
    void setNoSignificantCloud(boolean nsc);

    void setVerticalVisibility(NumericMeasure verticalVisibility);

    List<CloudLayer> getLayers();

    void setLayers(List<CloudLayer> layers);

}
