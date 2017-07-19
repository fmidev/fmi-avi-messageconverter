package fi.fmi.avi.data;

import java.util.List;

public interface CloudForecast extends AviationCodeListUser {

	boolean isNoSignificantCloud();
	  
    NumericMeasure getVerticalVisibility();
    
    void setNoSignificantCloud(boolean nsc);

    void setVerticalVisibility(NumericMeasure verticalVisibility);

    List<CloudLayer> getLayers();

    void setLayers(List<CloudLayer> layers);

}
